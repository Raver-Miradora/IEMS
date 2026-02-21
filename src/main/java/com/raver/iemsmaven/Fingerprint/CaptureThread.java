package com.raver.iemsmaven.Fingerprint;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.digitalpersona.uareu.Engine;
import javafx.scene.image.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaptureThread extends Thread {
    private ImageView imageview;
    private CaptureEvent lastCapture;
    private String threadName;
    private int delayTimeInMs;
    public boolean isCaptureCanceled;
    IdentificationThread identificationThread;
    public boolean runCapture = true;
    private Engine engine;
    private final Object LAST_CAPTURE_LOCK = new Object();

    
    public CaptureThread(ImageView imageview) {
        this.imageview = imageview;
        initializeEngine();
    }

    public CaptureThread(String threadName) {
        this.threadName = threadName;
        initializeEngine();
    }

    public CaptureThread(String threadName, int delayTimeInMs) {
        this.threadName = threadName;
        this.delayTimeInMs = delayTimeInMs;
        initializeEngine();
    }

    public CaptureThread(String threadName, ImageView imageview, IdentificationThread identificationThread) {
        this.threadName = threadName;
        this.imageview = imageview;
        this.identificationThread = identificationThread;
        initializeEngine();
    }

    private void initializeEngine() {
        try {
            engine = UareUGlobal.GetEngine();
            System.out.println("Fingerprint engine initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize fingerprint engine: " + e.getMessage());
            engine = null;
        }
    }

    public void startCapture() {
    System.out.println(threadName + ": Capture Thread Started");

    if (!Selection.readerIsConnected_noLogging()) {
        System.err.println(threadName + ": Reader not connected, aborting capture.");
        return;
    }

    if (engine == null) {
        System.err.println(threadName + ": Fingerprint engine not initialized, aborting capture.");
        return;
    }

    try {
        Selection.executeWithReader(reader -> {
            int[] resolutions = reader.GetCapabilities().resolutions;
            if (resolutions == null || resolutions.length == 0) {
                System.err.println(threadName + ": No supported resolutions found!");
                runCapture = false;
                return null;
            }
            int resolution = resolutions[0];
            System.out.println(threadName + " Using resolution: " + resolution);
            while (runCapture) {
                try {
                    Reader.Status status = reader.GetStatus();
                    System.out.println(threadName + " Reader Status: " + status);
                    if (status.status == Reader.ReaderStatus.BUSY) {
                        Thread.sleep(100);
                        continue;
                    }
                    if (status.status != Reader.ReaderStatus.READY &&
                        status.status != Reader.ReaderStatus.NEED_CALIBRATION) {
                        System.out.println(threadName + ": Reader not ready for capture");
                        Thread.sleep(500);
                        continue;
                    }
                    cancelCaptureBasedOnDelayTime(delayTimeInMs, reader);
                    Reader.CaptureResult captureResult = reader.Capture(
                            Fid.Format.ISO_19794_4_2005,
                            Reader.ImageProcessing.IMG_PROC_DEFAULT,
                            resolution,
                            -1
                    );
                    lastCapture = new CaptureEvent(captureResult, reader.GetStatus());
                    System.out.println(threadName + " Capture quality: " + captureResult.quality);
                    if (isValidCaptureResult(captureResult)) {
                        Fid.Fiv view = captureResult.image.getViews()[0];
                        Display.displayFingerprint(view, imageview);
                        lastCapture = new CaptureEvent(captureResult, reader.GetStatus());
                        System.out.println(threadName + " Capture quality: " + captureResult.quality);
                        runCapture = false;
                        break;
                    } else {
                        System.err.println(threadName + ": Invalid capture result, skipping identification");
                    }
                    runCapture = false;
                } catch (InterruptedException ex) {
                    System.err.println(threadName + ": Capture interrupted: " + ex.getMessage());
                    Thread.currentThread().interrupt();
                    runCapture = false;
                } catch (UareUException ue) {
                    System.err.println(threadName + ": Capture UareUException: " + ue.getMessage());
                    ue.printStackTrace();
                    try {
                        Selection.invalidateReader();
                    } catch (Exception invEx) {
                        System.err.println(threadName + ": error invalidating reader: " + invEx.getMessage());
                    }
                    runCapture = false;
                    break;
                } catch (Exception ex) {
                    System.err.println(threadName + ": Capture error: " + ex.getMessage());
                    ex.printStackTrace();
                    String msg = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
                    if (msg.contains("reader handle is not valid") || msg.contains("get_status") || msg.contains("invalid handle")) {
                        System.err.println(threadName + ": Detected invalid reader handle in generic exception, invalidating reader.");
                        try {
                            Selection.invalidateReader();
                        } catch (Exception invEx) {
                            System.err.println(threadName + ": error invalidating reader: " + invEx.getMessage());
                        }
                        runCapture = false;
                        break;
                    }
                    runCapture = false;
                }
            }
            return null;
        });
    } catch (UareUException e) {
        System.err.println(threadName + ": Reader operation failed: " + e.getMessage());
    }

    System.out.println(threadName + ": Capture Thread Stopped");
}

    private void setLastCapture(CaptureEvent evt) {
        synchronized (LAST_CAPTURE_LOCK) {
            this.lastCapture = evt;
        }
    }
    
    private boolean isValidCaptureResult(Reader.CaptureResult captureResult) {
        if (captureResult == null) {
            System.err.println("Capture result is null");
            return false;
        }
        
        if (captureResult.image == null) {
            System.err.println("Capture image is null");
            return false;
        }
        
        if (captureResult.image.getViews() == null || captureResult.image.getViews().length == 0) {
            System.err.println("No image views available");
            return false;
        }
        
        Fid.Fiv view = captureResult.image.getViews()[0];
        if (view == null) {
            System.err.println("First image view is null");
            return false;
        }
        
        return true;
    }
    
    public void startStream(ImageView imageview) {
    try {
        Selection.executeWithReader(reader -> {
            if (!Selection.readerIsConnected_noLogging()) {
                System.err.println(threadName + ": Reader not connected, aborting capture.");
                return null;
            }
            if (engine == null) {
                System.err.println(threadName + ": Fingerprint engine not initialized, aborting streaming.");
                return null;
            }
            int[] resolutions = reader.GetCapabilities().resolutions;
            if (resolutions == null || resolutions.length == 0) {
                System.err.println("No supported resolutions found for streaming!");
                return null;
            }
            int resolution = resolutions[0];
            reader.StartStreaming();
            while (runCapture) {
                try {
                    Reader.Status status = reader.GetStatus();
                    System.out.println("Reader Status: " + status);
                    if (status.status != Reader.ReaderStatus.READY &&
                        status.status != Reader.ReaderStatus.NEED_CALIBRATION) {
                        Thread.sleep(100);
                        continue;
                    }
                    Reader.CaptureResult captureResult = reader.GetStreamImage(
                            Fid.Format.ISO_19794_4_2005,
                            Reader.ImageProcessing.IMG_PROC_DEFAULT,
                            resolution
                    );
                    System.out.println("Capture quality: " + captureResult.quality);
                    if (isValidCaptureResult(captureResult)) {
                        Fid.Fiv view = captureResult.image.getViews()[0];
                        Display.displayFingerprint(view, imageview);
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.err.println("Streaming interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (UareUException ue) {
                    System.err.println("Streaming UareUException: " + ue.getMessage());
                    ue.printStackTrace();
                    try {
                        Selection.invalidateReader();
                    } catch (Exception invEx) {
                        System.err.println("Streaming error invalidating reader: " + invEx.getMessage());
                    }
                    break;
                } catch (Exception e) {
                    System.err.println("Streaming error: " + e.getMessage());
                    e.printStackTrace();
                    String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
                    if (msg.contains("reader handle is not valid") || msg.contains("get_status") || msg.contains("invalid handle")) {
                        System.err.println("Detected invalid reader handle in streaming, invalidating reader.");
                        try {
                            Selection.invalidateReader();
                        } catch (Exception invEx) {
                            System.err.println("Streaming error invalidating reader: " + invEx.getMessage());
                        }
                        break;
                    }
                }
            }
            reader.StopStreaming();
            return null;
        });
    } catch (UareUException e) {
        System.err.println(threadName + ": Streaming operation failed: " + e.getMessage());
    }
}


    public class CaptureEvent {
        private static final long serialVersionUID = 101;

        public Reader.CaptureResult captureResult;
        public Reader.Status readerStatus;
        public UareUException exception;

        public CaptureEvent(Reader.CaptureResult captureResult, Reader.Status readerStatus) {
            this.captureResult = captureResult;
            this.readerStatus = readerStatus;
        }
    }
    
    public CaptureEvent getLastCapture() {
        synchronized (LAST_CAPTURE_LOCK) {
            return lastCapture;
        }
    }

    public void cancelCaptureBasedOnDelayTime(int delayTimeInMs, Reader reader) {
        if (delayTimeInMs != 0) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    System.out.println("Delaying for " + delayTimeInMs + "ms");
                    Thread.sleep(delayTimeInMs);
                    reader.CancelCapture();
                    isCaptureCanceled = true;
                } catch (InterruptedException ex) {
                    System.err.println("Cancel capture interrupted: " + ex.getMessage());
                    Thread.currentThread().interrupt();
                } catch (UareUException ex) {
                    System.err.println("Cancel capture error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
            executor.shutdown();
        }
    }

    public void stopThread() {
     runCapture = false;                 // signal the loop to exit
     try {
         Reader r = Selection.getReader();
         if (r != null) {
             r.CancelCapture();          // this will unblock Capture/GetStreamImage
         }
     } catch (UareUException e) {
         System.err.println("Stop thread error: " + e.getMessage());
         e.printStackTrace();
     }
     isCaptureCanceled = true;
 }


    @Override
    public void run() {
        try{
             if (Selection.readerIsConnected_noLogging() && engine != null) {
                startCapture();
            } else {
                System.out.println(threadName + ": Capture Thread needs reader to be connected and engine initialized, closing thread...");
                runCapture = false;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.err.println("Thread sleep interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
       
    }

    public Engine getEngine() {
        return engine;
    }
}