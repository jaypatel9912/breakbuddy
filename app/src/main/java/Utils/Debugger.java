package Utils;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Debugger {

    public static File dir = null, logFile = null;

    public static boolean isDebugMode = true;
    public static boolean isFileDebugMode = false;

    public static void debugE(String Tag, String Text) {
        if (isDebugMode)
            Log.e(Tag, Text + "");

        if (isFileDebugMode)
            addLogDetails(Tag + " :: " + Text + "\n");

    }

    public static void debugI(String Tag, String Text) {
        if (isDebugMode)
            Log.i(Tag, Text + "");

        if (isFileDebugMode)
            addLogDetails(Tag + " :: " + Text + "\n");
    }

    public static void debugD(String Tag, String Text) {
        if (isDebugMode)
            Log.d(Tag, Text + "");

        if (isFileDebugMode)
            addLogDetails(Tag + " :: " + Text + "\n");
    }

    public static File getDir() {
        if (dir == null) {
            //Find the dir to save cached images
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath(), "Qlumi");

                if (!dir.exists())
                    dir.mkdirs();
            }
        }
        return dir;
    }

    public static File getLogFile() {
        if (logFile == null) {
            if (getDir() != null) {
                logFile = new File(dir, "log.txt");

                if (!logFile.exists()) {
                    try {
                        logFile.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        return logFile;
    }

    public static void addLogDetails(String sBody) {
        try {
            if (getLogFile() != null) {
                FileWriter writer = new FileWriter(logFile, true);
                writer.append(sBody);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
