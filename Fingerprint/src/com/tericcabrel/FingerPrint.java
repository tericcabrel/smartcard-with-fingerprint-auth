package com.tericcabrel;

import com.suprema.BioMiniSDK;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FingerPrint {
    private static final int MAX_TEMPLATE_SIZE = 1024;

    private BioMiniSDK sdk;
    private String outputPath;
    private String filename;

    private boolean sdkInitialized = false;
    private boolean fingerCaptured = false;
    private long[] matcher = new long[1];
    private byte[] currentTemplate;

    public FingerPrint(String outputPath, String filename) {
        super();
        this.outputPath = outputPath;
        this.filename = filename;

        sdk = new BioMiniSDK();
        initialize();
    }

    private void initialize() {
        int result = sdk.UFS_Init();

        if (result == 0) {
            result = sdk.UFM_Create(matcher);
            sdkInitialized = true;
        }
    }

    public void setParameters(int scannerIndex) {
        int result = 0;
        long[] scanner = new long[1];

        sdk.UFS_GetScannerHandle(scannerIndex, scanner);

        int[] pValue = new int[1];
        pValue[0] = 5000; // 5 seconds
        result = sdk.UFS_SetParameter(scanner[0], sdk.UFS_PARAM_TIMEOUT, pValue); // 201 : timeout parameter

        pValue[0] = 100;
        result = sdk.UFS_SetParameter(scanner[0], sdk.UFS_PARAM_BRIGHTNESS, pValue);

        pValue[0] = 2;
        result = sdk.UFS_SetParameter(scanner[0], sdk.UFS_PARAM_DETECT_FAKE, pValue);

        pValue[0] = 4;
        result = sdk.UFS_SetParameter(scanner[0], sdk.UFS_PARAM_SENSITIVITY, pValue);

        result = sdk.UFS_SetTemplateType(scanner[0], sdk.UFS_TEMPLATE_TYPE_SUPREMA); //2001 Suprema type

        int[] refValue = new int[1];
        result = sdk.UFM_GetParameter(matcher[0],302, refValue); //302 : security level :UFM_

        //fast mode
        int[] refFastMode = new int[1];
        result = sdk.UFM_SetParameter(matcher[0], sdk.UFM_PARAM_FAST_MODE, refFastMode);

        result = sdk.UFM_SetTemplateType(matcher[0], sdk.UFM_TEMPLATE_TYPE_SUPREMA);
    }

    public List<FingerprintScanner> getScanners () {
        List<FingerprintScanner> list;
        long[] matcher = new long[1];
        long[] scanner = new long[1];
        long[] tempScanner = new long[1];
        int[] number = new int[1];
        int[] scannerType = new int[1];
        byte[] scannerId = new byte[512];

        int result = sdk.UFS_GetScannerNumber(number);

        if (result != 0) {
            return null;
        }

        int scannerNumber = number[0];
        result = sdk.UFM_Create(matcher);

        if (result != 0) {
            return  null;
        }

        list = new ArrayList<>();

        for(int j = 0; j < scannerNumber; j++) {
            result = sdk.UFS_GetScannerHandle(j, tempScanner);
            scanner = tempScanner;

            if(result == 0){
                result = sdk.UFS_GetScannerID(scanner[0], scannerId);
                if(result == 0) {
                    result = sdk.UFS_GetScannerType(scanner[0], scannerType);
                }

                if(result == 0) {
                    String szId = new String(scannerId);
                    list.add(new FingerprintScanner(szId, scannerType[0]));
                }
            }
        }

        return list;
    }

    public boolean captureSingle()
    {
        int result;
        long[] hScanner = new long[1];

        hScanner = getCurrentScannerHandle();

        if(hScanner == null) {
            System.err.println("GetScannerHandle fail!!");
            return false;
        }

        // pValue[0] can be value between [0 - 5] with default value to 2
        // int result = p.UFS_SetParameter(hScanner[0], p.UFS_PARAM_DETECT_FAKE, pValue); //312 : detect_fake parameter

        // Detect fake parameter advanced: pValue[0] = 1;
        // Do not detect fake parameter advanced: pValue[0] = 0;
        // int nRes =  p.UFS_SetParameter(hScanner[0],p.UFS_PARAM_LFD_TYPE, pValue);

        result = sdk.UFS_CaptureSingleImage(hScanner[0]);

        if (result == 0) {
            fingerCaptured = true;
            return true;
        }

        System.err.println("SingleImage fail! code:" + result);

        byte[] refErr = new byte[512];
        result = sdk.UFS_GetErrorString(result, refErr);
        String strErr = new String(refErr);

        if (result == 0) {
            System.err.println("UFS_GetErrorString err is " + strErr);
        }

        return false;
    }

    public boolean enroll(boolean saveFile) {
        if (!fingerCaptured) {
            return false;
        }

        byte[] bTemplate = new byte[MAX_TEMPLATE_SIZE];
        int[] refTemplateSize = new int[1];
        int[] refTemplateQuality = new int[1];

        byte[] byteTemplateArray = null;
        int[] intTemplateSizeArray = null;

        byteTemplateArray = new byte[MAX_TEMPLATE_SIZE];
        intTemplateSizeArray = new int[1];

        int result = 0;
        long[] hScanner = getCurrentScannerHandle();

        if (hScanner != null) {

            try {
                result = sdk.UFS_ExtractEx(hScanner[0], MAX_TEMPLATE_SIZE, bTemplate, refTemplateSize, refTemplateQuality);
                if (result != 0) {
                    System.out.println("failed extract");
                    return false;
                }
                System.arraycopy(bTemplate, 0, byteTemplateArray, 0, refTemplateSize[0]);//byte[][]

                intTemplateSizeArray[0] = refTemplateSize[0];

                currentTemplate = new byte[intTemplateSizeArray[0]];

                //array copy: (src,offset,target,offset,copy size)
                System.arraycopy(byteTemplateArray, 0, currentTemplate, 0, intTemplateSizeArray[0]);//byte[][]

                if (saveFile) {
                    RandomAccessFile rf = new RandomAccessFile(getOutput("dat"), "rw");

                    rf.write(currentTemplate);
                    rf.close();
                }

                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("No scanner");
        }

        return false;
    }

    public boolean saveImage()
    {
        long[] hScanner = null;

        hScanner = getCurrentScannerHandle();

        if (hScanner == null) {
            System.err.println("No scanner available!");
            return false;
        }

        int result = sdk.UFS_SaveCaptureImageBufferToBMP(hScanner[0], getOutput("png"));
        //int nRes = p.UFS_SaveCaptureImageBufferTo19794_4(hScanner[0], getOutput("png"));

        return result == 0;
    }

    public boolean verify(byte[] firstTemplate, byte[] secondTemplate) {
        int[] refVerify = new int[1];

        int result = sdk.UFM_Verify(matcher[0], firstTemplate, firstTemplate.length, secondTemplate, secondTemplate.length, refVerify);//byte[][]

        if (result == 0) {
            if (refVerify[0] == 1) {
                return true;
            }
        }

        return false;
    }

    public boolean verify(String filePath, byte[] template) {
        byte[] firstTemplate = fileToByteArray(filePath);

        return this.verify(firstTemplate, template);
    }

    public boolean verify(String firstFilePath, String secondFilePath) {
        byte[] firstTemplate = fileToByteArray(firstFilePath);
        byte[] secondTemplate = fileToByteArray(secondFilePath);

        return this.verify(firstTemplate, secondTemplate);
    }

    private byte[] fileToByteArray(String filePath) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[MAX_TEMPLATE_SIZE];
            int readCount = 0;

            while ((readCount = fis.read(buffer)) != -1){
                baos.write(buffer, 0, readCount);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[] { };
    }

    private long[] getCurrentScannerHandle()
    {
        long[] scanner = new long[1];
        int result = 0;
        int[] number = new int[1];

        result = sdk.UFS_GetScannerNumber(number);

        if (result != 0 || number[0] <= 0){
            return null;
        }

        int index = 0;
        result = sdk.UFS_GetScannerHandle(index, scanner);

        if (result == 0) {
            return scanner;
        }

        return null;
    }

    private String getOutput(String extension) {
        return outputPath + "\\" + filename + "." + extension;
    }

    public BioMiniSDK getSdk() {
        return sdk;
    }

    public FingerPrint setSdk(BioMiniSDK sdk) {
        this.sdk = sdk;
        return this;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public FingerPrint setOutputPath(String outputPath) {
        this.outputPath = outputPath;
        return this;
    }

    public boolean isSdkInitialized() {
        return sdkInitialized;
    }

    public FingerPrint setSdkInitialized(boolean sdkInitialized) {
        this.sdkInitialized = sdkInitialized;
        return this;
    }

    public boolean isFingerCaptured() {
        return fingerCaptured;
    }

    public FingerPrint setFingerCaptured(boolean fingerCaptured) {
        this.fingerCaptured = fingerCaptured;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public FingerPrint setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public long[] getMatcher() {
        return matcher;
    }

    public FingerPrint setMatcher(long[] matcher) {
        this.matcher = matcher;
        return this;
    }

    public byte[] getCurrentTemplate() {
        return currentTemplate;
    }

    public FingerPrint setCurrentTemplate(byte[] currentTemplate) {
        this.currentTemplate = currentTemplate;
        return this;
    }

    @Override
    public String toString() {
        return "FingerPrint{" +
                "sdk=" + sdk +
                ", outputPath='" + outputPath + '\'' +
                ", sdkInitialized=" + sdkInitialized +
                ", fingerCaptured=" + fingerCaptured +
                '}';
    }
}
