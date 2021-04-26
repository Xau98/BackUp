package com.android.backup.code;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.backup.activity.MainActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Code {




    public static void encrypt(Context context, String pathInput , String pathOutput ) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        FileInputStream fileInputStream = new FileInputStream(pathInput);
        FileOutputStream fileOutputStream = new FileOutputStream(pathOutput);
        // Length is 16 byte
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.SHAREPREFENCE, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token","//");
        String key = "@nguyenvantienba";
        SecretKeySpec sks = new SecretKeySpec( key.getBytes() ,
                "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fileOutputStream, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while ((b = fileInputStream.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fileInputStream.close();
    }

    //"MyDifficultPassw"
    public static void decrypt(Context context,String pathInput , String pathOutput) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        FileInputStream fileInputStream = new FileInputStream(pathInput);
        FileOutputStream fileOutputStream = new FileOutputStream(pathOutput);
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.SHAREPREFENCE, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token","//");
        Log.d("Tiennvh", "encrypt: "+ token);
        String key = "@nguyenvantienba";
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(),
                "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fileInputStream, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = cis.read(d)) != -1) {
            fileOutputStream.write(d, 0, b);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
        cis.close();
    }

}
