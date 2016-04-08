package memory.example.com.memory;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class FileOperations extends ContextWrapper {

    private static String memTitle = "";
    private static String memVideo = "";
    private static String memPicture = "";
    private static String memDateAndTime = "";
    String filename = "ElephantMemories.txt";
    File file = new File(this.getFilesDir(), filename);

    private static final String className= MenuActivity.class.getSimpleName();

    public FileOperations(Context base) {
        super(base);
    }

    public static String getMemTitle() {
        Log.i(App.TAG, className + "---> " + "getMemTitle called");
        return memTitle;
    }

    public static void setMemTitle(String memTitle) {
        Log.i(App.TAG, className + "---> " + "setMemTitle called");
        FileOperations.memTitle = memTitle;
    }

    public static String getMemVideo() {
        Log.i(App.TAG, className + "---> " + "getMemVideo called");
        return memVideo;
    }

    public static void setMemVideo(String memVideo) {
        Log.i(App.TAG, className + "---> " + "setMemVideo called");
        FileOperations.memVideo = memVideo;
    }

    public static String getMemPicture() {
        Log.i(App.TAG, className + "---> " + "getMemPicture called");
        return memPicture;
    }

    public static void setMemPicture(String memPicture) {
        Log.i(App.TAG, className + "---> " + "setMemPicture called");
        FileOperations.memPicture = memPicture;
    }

    public static String getMemDateAndTime() {
        Log.i(App.TAG, className + "---> " + "getMemDateAndTime called");
    	return memDateAndTime;
    }

    public static void setMemDateAndTime(String memDateAndTime) {
        Log.i(App.TAG, className + "---> " + "setMemDateAndTime called");
    	FileOperations.memDateAndTime = memDateAndTime;
    }

    public static void clearMemoryArtifacts() {
        Log.i(App.TAG, className + "---> " + "clearMemoryArtifacts called");
        memTitle = "";
        memDateAndTime = "";
        memPicture = "";
        memVideo = "";
    }


    public boolean writeToFile() {
        Log.i(App.TAG, className + "---> " + "writeToFile called");
        Log.i("FileOperations", "started writeToFile()"+file.getAbsolutePath());

        //if no internet connection present, uncomment line below to mock spoken title
        //when creating memory.
    if(memTitle==null)
        memTitle="mock title";

        boolean result = false;

        if ("".equals(memTitle) || "".equals(memDateAndTime) || "".equals(memPicture) || "".equals(memVideo)) {
            Log.e("FileOperations: writeToFile() - ", "Either the memory's spoken title, or the memory's date/time, or the memory's " +
                    "picture, or the memory's video were not captured successfully.  Please start from the " +
                    "beginning and create memory again.");
            return result;
        } else {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(filename, Context.MODE_APPEND));

                outputStreamWriter.write(memDateAndTime + "+++");
                outputStreamWriter.write(memTitle + "+++");
                outputStreamWriter.write(memPicture + "+++");
                outputStreamWriter.write(memVideo + "\n");

                outputStreamWriter.close();
                result = true;

            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
        return result;
    }


    public String readFromFile() {
        Log.i(App.TAG, className + "---> " + "readFromFile called");

        Log.i("FileOperations: ", "started readFromFile()");

        String ret = "";

        try {
            InputStream inputStream = this.openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    //each return from readLine() is a valid memory from the text file
                    Log.i("FileOperations: readFromFile() line: ",receiveString);
                    stringBuilder.append(receiveString + "\n");

                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        Log.i("FileOperations: ", "return statement contains - " + ret);
        return ret;
    }


    public boolean deleteContentsOfFile() {
        Log.i(App.TAG, className + "---> " + "deleteContentsOfFile called");
        boolean result = false;
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
            result = true;
            Log.i("FileOperations: deleteContentsOfFile()", "Cleared file containing memories.");
        } catch (IOException e) {
            Log.e("Exception", "Delete file failed: " + e.toString());
        }
        return result;
    }


}

