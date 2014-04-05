package com.hatfat.dota.util;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.model.DotaDiskGson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    private static final String OLD_FILE_SUFFIX = "_old";
    private static final String NEW_FILE_SUFFIX = "_new";

    public static void dumpFileDirectoryContents() {
        //make sure the directory exists already
        File fileDir = DotaFriendApplication.CONTEXT.getFilesDir();
        fileDir.mkdirs();

        Log.v("FileUtil", "-------------------");
        Log.v("FileUtil", fileDir.getName());

        for (File file : fileDir.listFiles()) {
            Log.v("FileUtil", "-- " + file.getName() + ",   " + file.length());
        }
    }

    public static <T> T loadObjectFromDisk(String fileName, Class<T> typeClass) {
        File fileDir = DotaFriendApplication.CONTEXT.getFilesDir();

        File oldFile = new File(fileDir, fileName + OLD_FILE_SUFFIX);
        File newFile = new File(fileDir, fileName + NEW_FILE_SUFFIX);
        File currentFile = new File(fileDir, fileName);

        //attempt to load the object from the "current" file
        T obj = loadObjectFromFile(currentFile, typeClass);

        if (obj == null) {
            //current file failed, so try the "new" file
            obj = loadObjectFromFile(newFile, typeClass);
        }

        if (obj == null) {
            //new file failed, so try the "old" file
            obj = loadObjectFromFile(oldFile, typeClass);
        }

        return obj;
    }

    private static <T> T loadObjectFromFile(File file, Class<T> typeClass) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            JsonReader jsonReader = new JsonReader(br);

            Gson gson = DotaDiskGson.getDotaDiskGson();
            T obj = gson.fromJson(jsonReader, typeClass);

            return obj;
        }
        catch (FileNotFoundException e) {
            Log.e("FileUtil", "FileNotFoundException: " + file.getName());
        }
        catch (Exception e) {
            //failed to parse valid json
            Log.e("FileUtil", "Exception: " + e.toString() + ", " + file.getName());
        }

        return null;
    }

    public static <T> void saveObjectToDisk(final String fileName, final T obj) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //make sure the directory exists already
                    File fileDir = DotaFriendApplication.CONTEXT.getFilesDir();
                    fileDir.mkdirs();

                    File oldFile = new File(fileDir, fileName + OLD_FILE_SUFFIX);
                    File newFile = new File(fileDir, fileName + NEW_FILE_SUFFIX);
                    File currentFile = new File(fileDir, fileName);

                    //first write the object to the new file
                    BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
                    JsonWriter jsonWriter = new JsonWriter(bw);

                    Gson gson = DotaDiskGson.getDotaDiskGson();
                    gson.toJson(obj, obj.getClass(), jsonWriter); // Write to file using BufferedWriter
                    jsonWriter.close();

                    //write to the new file is complete
                    //move the current saved object (if there is one) to the old spot
                    currentFile.renameTo(oldFile);

                    //rename the new file just written to the current spot
                    newFile.renameTo(currentFile);

                    //clean up the old files?
                }
                catch (IOException e) {
                    Log.e("FileUtil", "Error saving to disk: " + e.toString());
                }

                return null;
            }
        }.execute();
    }
}
