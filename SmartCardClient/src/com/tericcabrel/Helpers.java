package com.tericcabrel;

/**
 *
 * @author ZEGEEK
 */
public class Helpers {
    public static String byteArrayToString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append((char) byteArray[i]);
        }
        return hexStringBuffer.toString();
    }
    
    public static byte[] numberStringToByteArray(String str) {
        if (str == null) {
            return new byte[]{ };
        }
        
        int strLength = str.length();
        byte[] bytes = new byte[strLength];
        
        for(int i = 0; i < strLength; i++) {
            bytes[i] = Integer.valueOf(str.charAt(i) + "", 10).byteValue();
        }
        
        return bytes;
    }
}
