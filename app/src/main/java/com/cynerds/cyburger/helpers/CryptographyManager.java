package com.cynerds.cyburger.helpers;

import android.content.Context;
import android.provider.Settings;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by fabri on 15/07/2017.
 */

public class CryptographyManager {

    byte[] encType;
    byte[] mInstance;
    byte[] cInstance;
    private String uId;
    private Context context;
    private byte[] k1;
    private byte[] k2;
    private byte[] k3;

     CryptographyManager(Context context) {
        this.context = context;
        encType = new byte[]{85, 84, 70, 45, 56};
        mInstance = new byte[]{83, 72, 65, 45, 50, 53, 54};
        cInstance = new byte[]{65, 69, 83};
        bUID();


    }

    private String b64(String text) throws UnsupportedEncodingException {
        // Sending side
        byte[] data = text.getBytes(new String(encType, "UTF-8"));
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return base64;
    }

    private String b64Dec(String base64) throws UnsupportedEncodingException {
        // Receiving side
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        String text = new String(data, new String(encType, "UTF-8"));

        return text;

    }

    private void bUID() {
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            uId = b64(b64(b64(b64(b64("TW9uZXl" + b64(android_id) + "NZUFwcA==")))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private SecretKeySpec generateKey() throws NoSuchAlgorithmException {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance(new String(mInstance, "UTF-8"));

        byte[] pwdBytes = uId.getBytes();
        messageDigest.update(pwdBytes);

        byte[] key = messageDigest.digest();

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, new String(cInstance, "UTF-8"));

        return secretKeySpec;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String encrypt(String data) throws Exception {

        Cipher cipher = Cipher.getInstance(new String(cInstance, "UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, generateKey());
        byte[] encrypted = cipher.doFinal(data.getBytes());

        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public String decrypt(String encrypted) throws Exception {

        Cipher cipher = Cipher.getInstance(new String(cInstance, "UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, generateKey());
        byte[] decodedValue = Base64.decode(encrypted, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(decodedValue);

        return new String(decrypted);
    }
}
