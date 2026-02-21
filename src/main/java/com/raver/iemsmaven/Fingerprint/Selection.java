package com.raver.iemsmaven.Fingerprint;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import javafx.scene.image.ImageView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Selection{
//    public static final Object READER_LOCK = new Object();
    public static Reader reader;
    static final AtomicBoolean readerInUse = new AtomicBoolean(false);

    public static ReaderCollection getReaderCollection() throws UareUException {
        ReaderCollection readerCollection = UareUGlobal.GetReaderCollection();
        readerCollection.GetReaders();
        return readerCollection;
    }

    public static boolean readerIsConnected() {
        try {
            ReaderCollection readerCollection = getReaderCollection();
            if (!readerCollection.isEmpty()) {
                Reader tempReader = readerCollection.get(0);
                System.out.println("readerIsConnected method: Connected fingerprint reader: " + tempReader.GetDescription().name);
                return true;
            } else {
                System.out.println("No fingerprint reader found.");
            }
        } catch (UareUException e) {
            System.err.println("Error checking reader connection: " + e.getMessage());
        }
        return false;
    }

       public static boolean readerIsConnected_noLogging() {
        try {
            if (reader == null) {
                // 1. Get the list of available readers
                ReaderCollection readers = UareUGlobal.GetReaderCollection();
                readers.GetReaders();
                if (readers.size() == 0) {
                    return false;
                }

                // 2. Pick the first reader, open it exclusively
                reader = readers.get(0);
                reader.Open(Reader.Priority.EXCLUSIVE);
            }
            return true;
        } catch (UareUException e) {
            // Failed to open or enumerate - ensure we don't keep a broken reader handle
            System.err.println("Selection.readerIsConnected_noLogging: error opening/enumerating reader: " + e.getMessage());
            try {
                if (reader != null) {
                    reader = null;
                }
            } catch (Exception ignore) { }
            return false;
        }
    }

    public static void invalidateReader() {
        System.err.println("Selection: invalidating reader (closing and setting to null)");
        if (reader != null) {
            try {
                reader.Close();
                System.out.println("Selection: reader closed successfully during invalidation");
            } catch (Exception ex) {
                System.err.println("Selection: error closing reader during invalidation: " + ex.getMessage());
            } finally {
                reader = null;
            }
        } else {
            reader = null;
        }
    }

    public static Reader getReader() {
        // Wait if reader is currently in use
        while (readerInUse.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        
        readerInUse.set(true);
        try {
            if (!readerIsConnected_noLogging()) {
                return null;
            }
            return reader;
        } finally {
            readerInUse.set(false);
        }
    }

    public static void closeAndOpenReader() throws UareUException {
        if (reader != null) {
            try {
                reader.Close();
                System.out.println("Reader closed successfully");
            } catch (UareUException ex) {
                System.err.println("Error closing reader: " + ex.getMessage());
            }
            reader = null;
        }
        // Reinitialize on next access
        readerIsConnected_noLogging();
    }

    public static void closeReader() throws UareUException {
        if (reader != null) {
            try {
                reader.Close();
                System.out.println("Reader closed successfully");
                reader = null;
            } catch (UareUException ex) {
                System.err.println("Error closing reader: " + ex.getMessage());
            }
        }
    }

//    public static void waitAndGetReader() {
//        ExecutorService executor = Executors.newFixedThreadPool(1);
//        executor.execute(() -> {
//            while (ThreadFlags.programIsRunning && !Thread.currentThread().isInterrupted()) {
//                try {
//                    if (!readerIsConnected_noLogging()) {
//                        System.out.println("No fingerprint reader found. Waiting for a reader to be connected...");
//                    } else {
//                        System.out.println("Fingerprint reader is connected: " + 
//                            (reader != null ? reader.GetDescription().name : "Unknown"));
//                    }
//
//                    TimeUnit.SECONDS.sleep(5);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    System.out.println("Reader wait thread interrupted");
//                    break;
//                } catch (Exception e) {
//                    System.err.println("Error in reader wait thread: " + e.getMessage());
//                }
//            }
//        });
//    }
    public static Reader.CaptureResult performCapture(ImageView imageview, int delayTimeInMs) throws UareUException {
        // Use an array to hold the result from within the lambda
        final Reader.CaptureResult[] captureResultHolder = new Reader.CaptureResult[1];
        
        // Use the existing synchronized method
        executeWithReader(reader -> {
            int[] resolutions = reader.GetCapabilities().resolutions;
            if (resolutions == null || resolutions.length == 0) {
                System.err.println("performCapture: No supported resolutions found!");
                return null;
            }
            int resolution = resolutions[0];
            System.out.println("performCapture: Using resolution: " + resolution);

            // This loop retries if the reader is busy, it doesn't retry the capture itself.
            boolean captureAttempted = false;
            while (!captureAttempted && !Thread.currentThread().isInterrupted()) {
                try {
                    Reader.Status status = reader.GetStatus();
                    System.out.println("performCapture: Reader Status: " + status);

                    if (status.status == Reader.ReaderStatus.BUSY) {
                        Thread.sleep(100);
                        continue;
                    }
                    if (status.status != Reader.ReaderStatus.READY &&
                        status.status != Reader.ReaderStatus.NEED_CALIBRATION) {
                        System.out.println("performCapture: Reader not ready for capture");
                        Thread.sleep(500);
                        continue;
                    }

                    // Start timeout if applicable
                    cancelCaptureBasedOnDelayTime(delayTimeInMs, reader);

                    Reader.CaptureResult captureResult = reader.Capture(
                            Fid.Format.ISO_19794_4_2005,
                            Reader.ImageProcessing.IMG_PROC_DEFAULT,
                            resolution,
                            -1 // Block until capture or cancel
                    );

                    System.out.println("performCapture: Capture quality: " + captureResult.quality);
                    
                    if (isValidCaptureResult(captureResult)) {
                        Fid.Fiv view = captureResult.image.getViews()[0];
                        Display.displayFingerprint(view, imageview); // Assumes Display is a static helper
                    } else {
                        System.err.println("performCapture: Invalid capture result (null image or CANCELED).");
                    }
                    
                    captureResultHolder[0] = captureResult; // Store good or bad result
                    captureAttempted = true; // Exit loop

                } catch (InterruptedException ex) {
                    System.err.println("performCapture: Capture interrupted: " + ex.getMessage());
                    Thread.currentThread().interrupt();
                } catch (UareUException ue) {
                    System.err.println("performCapture: Capture UareUException: " + ue.getMessage());
                    throw ue; // Re-throw for executeWithReader to handle
                } catch (Exception ex) {
                    System.err.println("performCapture: Capture error: " + ex.getMessage());
                    throw new RuntimeException(ex); // Re-throw for executeWithReader to handle
                }
            }
            return null; // Lambda return value isn't used
        });

        return captureResultHolder[0];
    }
    private static boolean isValidCaptureResult(Reader.CaptureResult captureResult) {
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

    /**
     * Helper method copied from CaptureThread.
     */
    private static void cancelCaptureBasedOnDelayTime(int delayTimeInMs, Reader reader) {
        if (delayTimeInMs != 0) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    System.out.println("Delaying for " + delayTimeInMs + "ms");
                    Thread.sleep(delayTimeInMs);
                    reader.CancelCapture();
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

//    @Override
//    public void run() {
//        waitAndGetReader();
//    }

    // Simple method to execute reader operations with basic synchronization
    public static <T> T executeWithReader(ReaderOperation<T> operation) throws UareUException {
        Reader currentReader = getReader();
        if (currentReader == null) {
            return null;
        }

        // Mark that we're using the reader while executing - keep single point of control
        readerInUse.set(true);
        try {
            return operation.execute(currentReader);
        } catch (UareUException e) {
            // Native reader error — invalidate so subsequent attempts reopen reader
            System.err.println("Selection.executeWithReader: UareUException detected, invalidating reader: " + e.getMessage());
            try {
                invalidateReader();
            } catch (Exception ex) {
                System.err.println("Selection.executeWithReader: error invalidating reader: " + ex.getMessage());
            }
            // Re-throw so caller can handle / backoff
            throw e;
        } catch (RuntimeException e) {
            // Any other runtime problem — also invalidate as a safety measure
            System.err.println("Selection.executeWithReader: runtime exception, invalidating reader: " + e.getMessage());
            try {
                invalidateReader();
            } catch (Exception ex) {
                System.err.println("Selection.executeWithReader: error invalidating reader (runtime): " + ex.getMessage());
            }
            throw e;
        } finally {
            readerInUse.set(false);
        }
    }

    @FunctionalInterface
    public interface ReaderOperation<T> {
        T execute(Reader reader) throws UareUException;
    }
}