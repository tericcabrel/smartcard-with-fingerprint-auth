package com.tericcabrel;

import com.suprema.BioMiniSDK;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        FingerPrint fp = new FingerPrint("D:\\Card", "tecoright-three");

        if (fp.isSdkInitialized()) {
            List<FingerprintScanner> scanners = fp.getScanners();
            for (FingerprintScanner sc: scanners ) {
                System.out.println(sc.toString());
            }

            if (scanners.size() > 0) {
                fp.setParameters(0);

                boolean b = fp.verify("D:\\Card\\tecoright-one.dat", "D:\\Card\\tecoright.dat");
                System.out.println("Match result: " + b);
                b = fp.verify("D:\\Card\\tecoright.dat", "D:\\Card\\tecoright-two.dat");
                System.out.println("Match result: " + b);

                fp.captureSingle();
                if (fp.isFingerCaptured()) {
                    boolean en = fp.enroll(false);
                    System.out.println("Enroll: " + en);

                    System.out.println("Match : " + fp.verify("D:\\Card\\tecoright.dat", fp.getCurrentTemplate()));

                    fp.saveImage();
                }
            }
        }
    }
}
