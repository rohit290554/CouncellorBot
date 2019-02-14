package com.example.councellorbot;

import android.util.Log;

import java.security.MessageDigest;


class Config {

    public static final String ROOT_URL = "http://precitex.in/project/";
    public static final String IMAGE_URL = "http://precitex.in/project/uploads/";

    public static final String URL_REGISTER = ROOT_URL + "Api.php?apicall=signup";
    public static final String URL_LOGIN= ROOT_URL + "Api.php?apicall=login";
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private static final String DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    public static final String API_KEY = "AIzaSyBPD5wHl8TzRnjtRodcijcF8nyZaKSTifo";
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;

    public static String hashPassword(String password) {
        StringBuffer sb = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Exception e) {
            Log.i("Exception: ", e.toString());
        }
        return sb.toString();
    }

    public static String getUrl(String originLat, String originLon, String destinationLat, String destinationLon){
        return DIRECTION_API + originLat+","+originLon+"&destination="+destinationLat+","+destinationLon+"&key="+API_KEY;
    }
}
