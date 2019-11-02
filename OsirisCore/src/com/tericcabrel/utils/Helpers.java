package com.tericcabrel.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author ZEGEEK
 */
public class Helpers {
    public static String byteArrayToString(byte[] byteArray) {
        short n = (short) (((byteArray[byteArray.length - 2] << 8)) | ((byteArray[byteArray.length - 1] & 0xff)));

        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length - 2; i++) {
            hexStringBuffer.append((char) byteArray[i]);
        }

        return hexStringBuffer.toString() + String.valueOf(n);
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

    public static byte[] prepareNumberForApdu(int number) {
        int length = number > 1000 ? 4 : 3;
        byte[] result = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        int temp = 0;

        if(length == 4) {
            int milli = (number - number%1000) / 1000;
            temp = number - (milli * 1000);
            int centaine = (temp - temp%100) / 100;
            temp = temp - (centaine * 100);
            int dizaine = (temp - temp%10) / 10;
            temp = temp - (dizaine * 10);

            result = new byte[]{ (byte)milli, (byte)centaine, (byte)dizaine, (byte)temp};
        } else if (length == 3) {
            int centaine = (number - number % 100) / 100;
            temp = number - (centaine * 100);
            int dizaine = (temp - temp % 10) / 10;
            temp = temp - (dizaine * 10);

            result = new byte[]{ (byte)centaine, (byte)dizaine, (byte)temp };
        }

        return result;
    }

    public static String uploadFingerprint(String uid, String fingerprintPath, String picturePath) {
        File fingerFile = new File(fingerprintPath);
        File pictureFile = new File(picturePath);

        FileBody fingerBody = new FileBody(fingerFile, ContentType.DEFAULT_BINARY);
        FileBody pictureBody = new FileBody(pictureFile, ContentType.DEFAULT_BINARY);

        HttpPost post = new HttpPost("http://localhost:7000/api/fingerprints");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        System.out.println(fingerprintPath);
        System.out.println(picturePath);
        builder.addPart("fingerprint", fingerBody);
        builder.addPart("picture", pictureBody);
        builder.addTextBody("uid", uid, ContentType.DEFAULT_BINARY);
//
        HttpEntity entity = builder.build();
        post.setEntity(entity);

        HttpClient client = HttpClientBuilder.create().build();
        try {
            client.execute(post);

            return "RES200";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "RES400";
    }
}
