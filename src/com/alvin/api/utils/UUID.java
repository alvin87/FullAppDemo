/**
 +------------------------------------------------------------------------------
 |   UUID 生成
 +------------------------------------------------------------------------------
 |   @Authors:         Hacder <i@hacder.com>
 |   @Created on       2012-3-05
 |   @Version          $Id
 +------------------------------------------------------------------------------
 **/

package com.alvin.api.utils;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class UUID {
    private String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.";
    private String SecretKey = "-x6g6ZWm2G9g_vr0Bo.pOq3kRIxsZ6rm";

    private static String DESKey = "12345678";
    private static byte[] DESiv = { (byte) 0x12, (byte) 0x34, (byte) 0x56,
            (byte) 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };

    public UUID() {
    }

    public String getUUID(String devID, String phoneNo) throws Exception {
        return makeUUID(devID + phoneNo);
    }

    public String makeUUID(String text) throws Exception {
        if (text == null || text.length() == 0)
            throw new Exception("Empty string");

        text = enDES(text);
        // byte[] iText = text.getBytes();
        // text = Base64.encodeToString(iText,Base64.DEFAULT);
        // text = text.replace("+", ".");
        // text = text.replace("/", "-");
        // text = text.replace("=", "_");
        text = encode(text);

        return text;
    }

    public static String insertString(String original, int offset,
            String destination) {
        return new StringBuilder(original).insert(offset, destination)
                .toString();
    }

    public String enDES(String input) {
        String result = "app43";
        try {
            result = bytesToHex(desEncrypt(input.getBytes()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public String encode(String text) throws Exception {
        if (text == null || text.length() == 0)
            throw new Exception("Empty string");

        int r1 = new Random().nextInt(chars.length());
        int r2 = new Random().nextInt(chars.length());
        int r3 = new Random().nextInt(chars.length());

        String ch1 = chars.substring(r1, r1 + 1);
        String ch2 = chars.substring(r2, r2 + 1);
        String ch3 = chars.substring(r3, r3 + 1);

        int knum = 0;
        byte[] chs = SecretKey.getBytes();
        for (byte ch : chs) {
            knum += ch;
        }

        String tmpStr = text;
        int tmpLen = tmpStr.length();
        tmpStr = insertString(tmpStr, r2 % ++tmpLen, ch3);
        tmpStr = insertString(tmpStr, r1 % ++tmpLen, ch2);
        tmpStr = insertString(tmpStr, knum % ++tmpLen, ch1);

        return tmpStr;
    }

    public static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        }

        int len = data.length;
        String str = "";
        for (int i = 0; i < len; i++) {
            if ((data[i] & 0xFF) < 16)
                str = str + "0" + java.lang.Integer.toHexString(data[i] & 0xFF);
            else
                str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
        }
        return str;
    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(
                        str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }

    public byte[] desEncrypt(byte[] plainText) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(DESiv);
        DESKeySpec dks = new DESKeySpec(DESKey.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte data[] = plainText;
        byte encryptedData[] = cipher.doFinal(data);
        return encryptedData;
    }

}