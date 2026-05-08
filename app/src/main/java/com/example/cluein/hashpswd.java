package com.example.cluein;

import android.os.Build;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class hashpswd {

    String password;
    public hashpswd(String password){
        this.password = password;
    }


    private  String hash(String password){

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            byte[] hashBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();

            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public String getHashed(){
        return hash(password);
    }

}
