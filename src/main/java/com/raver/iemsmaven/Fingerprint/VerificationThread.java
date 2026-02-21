package com.raver.iemsmaven.Fingerprint;

import com.raver.iemsmaven.Model.Fingerprint;
import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;

/**
 * Verification Thread
 * This thread captures one fingerprint and securely compares it against the database.
 */
public class VerificationThread extends Thread {
    private Engine engine = UareUGlobal.GetEngine();
    private CaptureThread captureThread;
    
    private int userIdToMatch;
    private int delayTimeInMs;

    public boolean userIsVerified = false;
    private boolean runThisThread = true;
    public boolean isCaptureCanceled = false;

    //constructor
    public VerificationThread(int userIdToMatch, int delayTimeInMs){
        this.userIdToMatch = userIdToMatch;
        this.delayTimeInMs = delayTimeInMs;
    }
    
    public void startVerification() throws InterruptedException, UareUException {
        Selection.closeAndOpenReader();
        ThreadFlags.runVerificationThread = true;
        System.out.println("Verification Thread Started");

        if (runThisThread) {
            // 1. Capture a new fingerprint
            Fmd fmdToVerify = getFmdFromCaptureThread();
            
            if (fmdToVerify != null) {
                // 2. Securely verify it using the new method
                // This does the AES_ENCRYPT and comparison *inside* the database
                int matchedUserId = Fingerprint.verifyFingerprint(fmdToVerify.getData());

                // 3. Check if the matched ID is the one we expected
                if (matchedUserId == userIdToMatch) {
                    System.out.println("VERIFICATION: Success! Fingerprint matched User ID: " + matchedUserId);
                    userIsVerified = true;
                } else if (matchedUserId != -1) {
                    System.out.println("VERIFICATION: Failed. Fingerprint matched a *different* user (ID: " + matchedUserId + ")");
                    userIsVerified = false;
                } else {
                    System.out.println("VERIFICATION: Failed. Fingerprint did not match any user.");
                    userIsVerified = false;
                }
            } else {
                System.out.println("VERIFICATION: Failed. No FMD captured.");
                userIsVerified = false;
            }
        }
        
        runThisThread = false; // Stop the thread
        ThreadFlags.runVerificationThread = false;
        System.out.println("Verification Thread Stopped");
    }

    private Fmd getFmdFromCaptureThread() throws UareUException, InterruptedException {
        captureThread = new CaptureThread("Verification Thread", delayTimeInMs);
        captureThread.start();
        captureThread.join(0); // wait till done

        isCaptureCanceled = captureThread.isCaptureCanceled;

        CaptureThread.CaptureEvent evt = captureThread.getLastCapture();
        if (evt == null || evt.captureResult.image == null) {
            System.out.println("evt or evt.captureResult.image is null");
            return null;
        }
        
        // Create the FMD from the captured image
        return engine.CreateFmd(evt.captureResult.image, Fmd.Format.ISO_19794_2_2005);
    }

    @Override
    public void run(){
        try {
            startVerification();
        } catch (InterruptedException | UareUException ex) {
            ex.printStackTrace();
        }
    }
}