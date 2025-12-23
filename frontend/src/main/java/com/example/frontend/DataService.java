package com.example.frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TransferQueue;

public class DataService {

    private static DataService instance;

    public static DataService getInstance() {
        if(instance == null){
            instance = new DataService();
        }
        return instance;
    }

    public  String getHelloWorld(String URL) throws IOException {
        URL url = new URL(URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        System.out.println(status);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine = "";
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        System.out.println(content);

        in.close();
        String data = content.toString();
        return data;
    }
    public Boolean deleteUser(String URL) throws IOException{
        URL url = new URL(URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        int status = con.getResponseCode();
        if (status == 201){
            return Boolean.TRUE;
        }
        return  Boolean.FALSE;
    }
}
