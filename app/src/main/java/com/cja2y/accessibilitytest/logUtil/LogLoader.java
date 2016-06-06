package com.cja2y.accessibilitytest.logUtil;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Kale
 * @date 2016/3/25
 */
public class LogLoader {

    public static void load(final Process process, final LoadHandler handler) {
        AsyncTask<Void, Void, BufferedReader> task = new AsyncTask<Void, Void, BufferedReader>() {

            @Override
            protected BufferedReader doInBackground(Void... params) {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    //  Runtime.runFinalizersOnExit(true);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                                             handler.handLine(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return bufferedReader;
            }

            @Override
            protected void onPostExecute(BufferedReader s) {
                handler.onComplete();
            }
        };
        
        task.execute();
    }

    public interface LoadHandler {

        void handLine(String line);

        void onComplete();
    }
}
