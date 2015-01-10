package com.example.zoltaare.angunangapp;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by zoltaare on 1/7/15.
 */
public class webserver extends NanoHTTPD {

    public webserver(){
        super(1234);
    }

    @Override public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Map<String, String> parameters = session.getHeaders();
        Map<String, String> files = session.getParms();

            String answer = "";
            try {
                try
                {
                    File asset = new File(Environment.getExternalStorageDirectory(), "www");
                    if (!asset.exists()) {
                        asset.mkdirs();
                    }
                    File index = new File(asset, "index.html");
                    FileWriter writer = new FileWriter(index);
                    writer.append("<h1>Test.</h1>");
                    writer.flush();
                    writer.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                //create asset directory
                File asset = android.os.Environment.getExternalStorageDirectory();
                FileReader index = new FileReader(asset.getAbsolutePath() +
                        "/www/index.html");
                BufferedReader reader = new BufferedReader(index);
                String line = "";
                while ((line = reader.readLine()) != null) {
                    answer += line;
                }
                reader.close();

            } catch(IOException ioe) {
                Log.w("Httpd", ioe.toString());
            }

        return new NanoHTTPD.Response(answer);
    }


}
