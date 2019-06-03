package com.gmail.sewahezi.worthitapp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.TreeMap;

public class SaveDataObject {
    static String saveFileName = "save.txt";
    final static String SEPARATOR = ";;";


    public static void createSaveFile(Context context, String fileName) {
        BufferedWriter bufferedWriter = null;
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write("");
            File saveFile = new File(context.getFilesDir(), fileName);
        } catch (Exception e) {
            System.out.println("===Failed during initial saveFile creation===");
            e.printStackTrace();
        } finally {
            try {
                if(bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception e) { }
        }
    }

    public static void createSaveFile(Context context) {
        createSaveFile(context, saveFileName);
    }

    public static void saveDataToFile(Context context, TreeMap<String, ItemObject> itemMap, String saveFileName) {
        BufferedWriter bufferedWriter = null;
        //save all existed data in the item Map to the file
        //this overwrites previous data, essentially doing a clean rewrite of all the data
        String saveString = "";
        //build string
        for (String key : itemMap.keySet()) {
            saveString += itemMap.get(key).itemName + SEPARATOR + itemMap.get(key).itemCost + "\n";
        }

        //write string to save file
        try {
            FileOutputStream fos = context.openFileOutput(saveFileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(saveString);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception e) { }
        }
    }

    public static void saveDataToFile(Context context, TreeMap<String, ItemObject> itemMap) {
        saveDataToFile(context, itemMap, saveFileName);
    }

    public static void clearSaveFile(Context context, String fileName) {
        BufferedWriter bufferedWriter = null;
        try {
            FileOutputStream fos = context.openFileOutput(saveFileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write("");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception e) { }
        }
    }

    public static void clearSaveFile(Context context) {
        clearSaveFile(context, saveFileName);
    }

    public static void loadSaveToMap(Context context, TreeMap<String, ItemObject> itemMap, String saveFileName) {
        BufferedReader bufferedReader = null;
        try {
            FileInputStream fis = context.openFileInput(saveFileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            bufferedReader = new BufferedReader(inputStreamReader);

            String lineData = null;
            //bufferedReader.close();
            while((lineData = bufferedReader.readLine()) != null){
                String item = lineData.split(SEPARATOR)[0];
                String cost = lineData.split(SEPARATOR)[1];
                itemMap.put(item, new ItemObject(item, cost));

                //for testing, print each item:
                System.out.print("=====ITEM PRINT: " + lineData + "=====\n");
            }
            //System.out.println("READ FILE: " + lineData);
            //context.deleteFile(saveFileName);
            //SaveDataObject.clearSaveFile(context);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) { }
        }
    }

    public static void loadSaveToMap(Context context, TreeMap<String, ItemObject> itemMap) {
        loadSaveToMap(context, itemMap, saveFileName);
    }
}
