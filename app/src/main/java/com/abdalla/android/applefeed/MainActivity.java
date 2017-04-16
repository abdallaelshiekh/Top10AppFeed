package com.abdalla.android.applefeed;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: pre create asyncTask" );
        DownloadData downloadData = new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
        Log.d(TAG, "onCreate: after asyncTask executed");
    }
    private class DownloadData extends AsyncTask<String,Void,String>{
        private static final String TAG = "DownloadData";
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d(TAG, "onPostExecute: " + s);
            ParseApplications parseApp = new ParseApplications();
            boolean b = parseApp.parse(s);
            Log.d(TAG, "onPostExecute: "+b);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "doInBackground: " + params[0]);
            String rssFeed = downloadXML(params[0]);
            return rssFeed;
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();
            try{
                URL url = new URL(urlPath);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                Log.d(TAG, "downloadXML: " + httpURLConnection.getResponseCode());
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                int charsRead;
                char[] inputBuffer = new char[500];
                while (true){
                    charsRead = bufferedReader.read(inputBuffer);
                    if (charsRead<0)
                        break;
                    if (charsRead>0)
                        xmlResult.append(String.copyValueOf(inputBuffer,0,charsRead));
                }
                bufferedReader.close();
                return xmlResult.toString();

            }catch (MalformedURLException ex){
                ex.printStackTrace();
            }catch (IOException ex){
                ex.printStackTrace();
            }catch (SecurityException ex){
                ex.printStackTrace();
            }
            return null;
        }
    }
}
