package com.raver.iemsmaven.Fingerprint;

import com.raver.iemsmaven.Model.Fingerprint;
import com.raver.iemsmaven.Model.User;
import com.raver.iemsmaven.Utilities.PaneUtil;
import com.raver.iemsmaven.Utilities.SoundUtil;
import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Engine.Candidate;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.raver.iemsmaven.Utilities.APIClient;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
/**
 *
 * @author admin
 */
public class IdentificationThread extends Thread{
    private Consumer<Boolean> statusCallback;
    private List<Integer> fmdIndexMap = new ArrayList<>();
    private Runnable onFingerprintScan;
    IdentificationModal identificationModal = new IdentificationModal();
    private ImageView imageview;
    private Runnable onIdentificationFailure;
    private Engine engine; 
//    public CaptureThread captureThread;
    ObservableList<Fingerprint> fingerprintList;
    ObservableList<User> userList;
    int falsePositiveRate = Engine.PROBABILITY_ONE / 100000;
    int candidateCount = 1;
    int delayTimeInMs = 4000;
    private ScheduledExecutorService syncScheduler;
    
    private boolean headlessMode = false;
    public boolean runThisThread = true;
    // in Selection.java (one-time)
//    public static final Object READER_LOCK = new Object();
    private Consumer<User> onIdentificationSuccess;
    // Synchronization object for thread safety
    private static final Object ENGINE_LOCK = new Object();
    
    // Constructor with fingerprint display
    public IdentificationThread(ImageView imageview){
        this.imageview = imageview;
    }
    public IdentificationThread(ImageView imageview, Runnable onFingerprintScan) {
        this(imageview, onFingerprintScan, null, null);
    }
     public IdentificationThread(ImageView imageview, Runnable onFingerprintScan, Consumer<User> onIdentificationSuccess) {
        this(imageview, onFingerprintScan, onIdentificationSuccess, null);
    }
    // Constructor with fingerprint display and callback function
    public IdentificationThread(ImageView imageview, Runnable onFingerprintScan, Consumer<User> onIdentificationSuccess, Runnable onIdentificationFailure) {
        this.imageview = imageview;
        this.onFingerprintScan = onFingerprintScan;
        this.onIdentificationSuccess = onIdentificationSuccess;
        this.onIdentificationFailure = onIdentificationFailure;
    }
    // Constructor with status callback (5 arguments)
    public IdentificationThread(ImageView imageview, Runnable onFingerprintScan, Consumer<User> onIdentificationSuccess, Runnable onIdentificationFailure, Consumer<Boolean> statusCallback) {
        this.imageview = imageview;
        this.onFingerprintScan = onFingerprintScan;
        this.onIdentificationSuccess = onIdentificationSuccess;
        this.onIdentificationFailure = onIdentificationFailure;
        this.statusCallback = statusCallback; // <--- Store the callback
    }

    // Default constructor
    public IdentificationThread(){
        headlessMode = true;
    }
    
    // Set engine from CaptureThread
    public void setEngine(Engine engine) {
        synchronized (ENGINE_LOCK) {
            this.engine = engine;
        }
    }
    
    // Get engine with validation
    private Engine getValidatedEngine() throws UareUException {
        synchronized (ENGINE_LOCK) {
            if (engine == null) {
                try {
                    
                    engine = UareUGlobal.GetEngine();
                    System.out.println("Engine initialized in IdentificationThread");
                } catch (Exception e) {
                    throw new RuntimeException("Failed to initialize fingerprint engine: " + e.getMessage(), e);
                }
            }
            return engine;
        }
    }

    public void startIdentification(ImageView imageview) throws InterruptedException, UareUException {
            // Initial cleanup
            if (!Selection.readerIsConnected_noLogging()) {
                Selection.closeAndOpenReader();
            }

            System.out.println("Identification Thread Started");
            
            // --- CLOUD SYNC START ---
            System.out.println("Syncing fingerprints from Cloud...");
            try {
                List<Map<String, Object>> cloudPrints = APIClient.getAllFingerprints();
                if (cloudPrints != null) {
                    for (Map<String, Object> record : cloudPrints) {
                        int uId = Double.valueOf(record.get("user_id").toString()).intValue();
                        String fmdBase64 = (String) record.get("fmd");

                        // Convert Base64 back to byte[]
                        byte[] fmdBytes = java.util.Base64.getMimeDecoder().decode(fmdBase64);

                        // Save to Local Cache (Assuming Fingerprint model has this method)
                        // If you don't have a local cache method yet, you can skip saving 
                        // and just load them into the 'fingerprintList' variable directly here.
                        System.out.println("Synced User ID: " + uId);
                    }
                }
            } catch (Exception e) {
                System.err.println("Sync failed (Offline Mode?): " + e.getMessage());
            }
            // --- CLOUD SYNC END ---

            while (runThisThread && !Thread.currentThread().isInterrupted()) {
                try {
                    // 1. Check connection status
                    boolean isConnected = Selection.readerIsConnected_noLogging();

                    // 2. REPORT STATUS TO UI (Moved INSIDE the loop)
                    if (statusCallback != null) {
                        Platform.runLater(() -> statusCallback.accept(isConnected));
                    }

                    // 3. Handle Disconnection
                    if (!isConnected) {
                        System.out.println("Reader disconnected, waiting...");
                        Thread.sleep(2000); // Wait before retrying
                        try {
                            Selection.closeAndOpenReader(); // Try to reconnect
                        } catch (Exception e) {
                            System.err.println("Reconnection failed: " + e.getMessage());
                        }
                        continue; // Skip the rest of the loop and check again
                    }

                    // 4. Proceed with Capture (Only reachable if connected)
                    Fmd fmdToIdentify = getFmdFromCaptureThread(imageview);

                    if (fmdToIdentify == null) {
                        Thread.sleep(1000);
                        continue;
                    }

                    Fmd[] databaseFmds = getFmdsFromDatabase();
                    if (databaseFmds == null || databaseFmds.length == 0) {
                        System.out.println("No database FMDs found");
                        if (runThisThread && !Thread.currentThread().isInterrupted()) {
                            Thread.sleep(5000);
                        }
                        continue;
                    }

                    compareFmdToDatabaseFmds(fmdToIdentify, databaseFmds);

                } catch (InterruptedException e) {
                    System.out.println("Identification thread interrupted normally");
                    throw e;
                } catch (UareUException e) {
                    System.err.println("Identification error: " + e.getMessage());
                    if (runThisThread && !Thread.currentThread().isInterrupted()) {
                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                    System.err.println("Unexpected error: " + e.getMessage());
                    if (runThisThread && !Thread.currentThread().isInterrupted()) {
                        Thread.sleep(2000);
                    }
                }
            }
            System.out.println("Identification Thread Stopped");
        }

    private Fmd getFmdFromCaptureThread(ImageView imageview) throws UareUException, InterruptedException {
        if (onFingerprintScan != null) {
            onFingerprintScan.run();
        }

        // All the old logic for managing captureThread and READER_LOCK is GONE.
        
        try {
            // Simple, blocking call.
            Reader.CaptureResult captureResult = Selection.performCapture(imageview, 0);

            if (captureResult != null && captureResult.image != null) {
                Engine localEngine = getValidatedEngine();
                return localEngine.CreateFmd(captureResult.image, Fmd.Format.ISO_19794_2_2005);
            } else {
                System.err.println("Invalid capture event or image");
                return null;
            }
        } catch (UareUException e) {
            System.err.println("IdentificationThread: Capture/FMD creation failed: " + e.getMessage());
            throw e; // Re-throw to be handled by startIdentification loop
        } catch (Exception e) {
             System.err.println("IdentificationThread: Unexpected error during capture: " + e.getMessage());
             return null;
        }
    }


    private Fmd[] getFmdsFromDatabase() throws UareUException {
    fingerprintList = Fingerprint.getFingerprints();
    if (fingerprintList == null || fingerprintList.isEmpty()) {
        System.out.println("No fingerprints in database");
        return new Fmd[0];
    }

    List<Fmd> validFmdsList = new ArrayList<>();
    fmdIndexMap.clear();

    for (int i = 0; i < fingerprintList.size(); i++) {
        Fingerprint fp = fingerprintList.get(i);
        if (fp != null && fp.getFmd() != null) {
            try {
                Fmd converted = UareUGlobal.GetImporter().ImportFmd(
                        fp.getFmd(),
                        Fmd.Format.ISO_19794_2_2005,
                        Fmd.Format.ISO_19794_2_2005
                );
                validFmdsList.add(converted);
                fmdIndexMap.add(i); // remember original fingerprintList index
            } catch (Exception e) {
                System.err.println("Failed to import FMD for fingerprint " + i + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Fingerprint or its FMD is null at DB index: " + i);
        }
    }

    return validFmdsList.toArray(new Fmd[0]);
}

    
    private boolean compareFmdToDatabaseFmds(Fmd fmdToIdentify, Fmd[] databaseFmds) throws UareUException {
    if (fmdToIdentify == null) {
        System.out.println("fmdToIdentify is null");
        return false;
    }

    if (databaseFmds == null || databaseFmds.length == 0) {
        System.out.println("databaseFmds is null or empty");
        return false;
    }

    for (int i = 0; i < databaseFmds.length; i++) {
        if (databaseFmds[i] == null) {
            System.err.println("Null FMD found in database at index " + i);
            return false;
        }
    }

    try {
        Engine localEngine = getValidatedEngine();
        Candidate[] candidateFmds = localEngine.Identify(
            fmdToIdentify,
            0,
            databaseFmds,
            falsePositiveRate,
            candidateCount
        );

        if (candidateFmds != null && candidateFmds.length > 0) {
            System.out.println("Candidate found");
            int topCandidateFmdIndex = candidateFmds[0].fmd_index;

            // map returned index back to fingerprintList index using fmdIndexMap
            if (topCandidateFmdIndex >= 0 && topCandidateFmdIndex < fmdIndexMap.size()) {
                int fingerprintListIndex = fmdIndexMap.get(topCandidateFmdIndex);
                if (fingerprintListIndex >= 0 && fingerprintListIndex < fingerprintList.size()) {
                    int matchingUserId = fingerprintList.get(fingerprintListIndex).getUserId();
                    if (!headlessMode) {
                        try {
                            userIdentificationSuccess(matchingUserId);
                        } catch (Exception ex) {
                            // Catch resource-loading exceptions from UI code so identification loop keeps running
                            System.err.println("userIdentificationSuccess threw: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                    return true;
                } else {
                    System.err.println("Mapped fingerprintList index out of bounds: " + fingerprintListIndex);
                }
            } else {
                System.err.println("Invalid candidate index returned by engine: " + topCandidateFmdIndex + " (map size=" + fmdIndexMap.size() + ")");
            }
        } else {
            if (!headlessMode) {
                userIdentificationFailed();
            }
            System.out.println("No candidate/s found");
        }
    } catch (UnsatisfiedLinkError e) {
        System.err.println("Native library error: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Native library issue: " + e.getMessage(), e);
    } catch (Exception e) {
        System.err.println("Identification failed: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Identification error: " + e.getMessage(), e);
    }
    return false;
}


    private boolean compareFmdToDatabaseFmds(Fmd fmdToIdentify, Fmd[] databaseFmds, ArrayList<Fmd> fmdList) throws UareUException {
        if (fmdList == null || fmdList.isEmpty()) {
            return compareFmdToDatabaseFmds(fmdToIdentify, databaseFmds);
        }
        
        // Validate all FMDs in the list
        for (Fmd fmd : fmdList) {
            if (fmd == null) {
                System.err.println("Null FMD found in additional list");
                return false;
            }
        }
        
        Fmd[] combinedFmds = new Fmd[databaseFmds.length + fmdList.size()];
        System.arraycopy(databaseFmds, 0, combinedFmds, 0, databaseFmds.length);
        
        for (int i = 0; i < fmdList.size(); i++) {
            combinedFmds[databaseFmds.length + i] = fmdList.get(i);
        }

        Engine localEngine = getValidatedEngine();
        Candidate[] candidateFmds = localEngine.Identify(
            fmdToIdentify, 
            0, 
            combinedFmds, 
            falsePositiveRate, 
            candidateCount
        );

        return candidateFmds != null && candidateFmds.length > 0;
    }

    private void userIdentificationSuccess(int userId) {
        User user = User.getUserByUserId(userId);
        if (user == null) {
            System.err.println("User not found for ID: " + userId);
            return;
        }

        // --- API CALL START ---
        // Run in a separate thread so UI doesn't freeze
        new Thread(() -> {
            System.out.println("Sending attendance to Cloud for User: " + userId);
            // We send "AUTO" because our Smart PHP Script decides IN/OUT now
            boolean success = APIClient.logAttendance(userId, "AUTO");

            if (success) {
                System.out.println("CLOUD LOG SUCCESS!");
            } else {
                System.err.println("CLOUD LOG FAILED - Saving to local SQLite...");
                // TODO: Add code here to save to local SQLite if offline
            }
        }).start();
        // --- API CALL END ---

        // Continue with existing UI logic
        if (onIdentificationSuccess != null) {
            onIdentificationSuccess.accept(user);
        }
    }

    private void userIdentificationFailed() {
//        SoundUtil.playFailSound();
           if (onIdentificationFailure != null) {
            Platform.runLater(() -> {
                onIdentificationFailure.run();
            });
        } else {
            // Fallback to old behavior if no callback provided
            Platform.runLater(() -> {
                identificationModal.displayIdentificationFail(1500);
            });
        }

//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
    }
    
    public boolean fmdIsAlreadyEnrolled(Fmd fmdToIdentify) throws UareUException {
        Fmd[] databaseFmds = getFmdsFromDatabase();
        return compareFmdToDatabaseFmds(fmdToIdentify, databaseFmds);
    }
    
    public boolean fmdIsAlreadyEnrolled(Fmd fmdToIdentify, ArrayList<Fmd> fmdList) throws UareUException {
        Fmd[] databaseFmds = getFmdsFromDatabase();
        return compareFmdToDatabaseFmds(fmdToIdentify, databaseFmds, fmdList);
    }
    public void stopThread() {
        runThisThread = false;
        if (syncScheduler != null && !syncScheduler.isShutdown()) {
            syncScheduler.shutdownNow();
            System.out.println("Auto-Sync stopped.");
        }
        this.interrupt();

        System.out.println("IdentificationThread stopped successfully.");
    }
   
   @Override
    public void run() { 
        try {
            startBackgroundSync();
            startIdentification(imageview);
        } catch (InterruptedException ex) {
            System.out.println("Identification thread interrupted normally");
        } catch (UareUException ex) {
            Logger.getLogger(IdentificationThread.class.getName()).log(Level.SEVERE, "Fingerprint engine error", ex);
        } catch (Exception ex) {
            Logger.getLogger(IdentificationThread.class.getName()).log(Level.SEVERE, "Unexpected error", ex);
        } finally {
            // Cleanup resources
            System.out.println("Identification thread cleanup");
        }
    }
    private void startBackgroundSync() {
        syncScheduler = Executors.newSingleThreadScheduledExecutor();

        syncScheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Auto-Sync: Starting...");

                // --- PART 1: SYNC USERS (Metadata) ---
                List<Map<String, Object>> cloudUsers = APIClient.getAllUsers();
                if (cloudUsers != null) {
                    for (Map<String, Object> uData : cloudUsers) {
                        int uId = Double.valueOf(uData.get("user_id").toString()).intValue();

                        // If we don't have this user locally, Save them!
                        if (!User.hasUser(uId)) {
                            System.out.println("Auto-Sync: Downloading info for User " + uId);
                            User.saveSyncedUser(uData);
                        }
                    }
                }

                // --- PART 2: SYNC FINGERPRINTS (Biometrics) ---
                List<Map<String, Object>> cloudPrints = APIClient.getAllFingerprints();
                if (cloudPrints != null) {
                    for (Map<String, Object> record : cloudPrints) {
                        int uId = Double.valueOf(record.get("user_id").toString()).intValue();
                        String fmdBase64 = (String) record.get("fmd");

                        // Decode safely
                        byte[] fmdBytes = java.util.Base64.getMimeDecoder().decode(fmdBase64);

                        // If we don't have this fingerprint locally, Save it!
                        if (!Fingerprint.hasFingerprint(uId)) {
                            System.out.println("Auto-Sync: Downloading fingerprint for User " + uId);
                            Fingerprint newFp = new Fingerprint(uId, fmdBytes); 
                            newFp.save(); 
                        }
                    }
                }
                System.out.println("Auto-Sync: Complete.");

            } catch (Exception e) {
                System.err.println("Auto-Sync Failed (Offline?): " + e.getMessage());
            }
        }, 0, 60, TimeUnit.SECONDS); 
    }
}