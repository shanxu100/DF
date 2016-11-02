package com.luluteam.utils;

import com.luluteam.main.Statistics;
import org.omg.CORBA.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by guan on 11/1/16.
 */
public class WriteToFile {

    /***
     *把数据写入文件
     *
     */
    public static void writeToSD(String fileName, String s,boolean isAppend) {
        try {
            //String pathName = Environment.getExternalStorageDirectory().getPath() + "/BetterUse/webview";
            String pathName= Statistics.DIR;
            //String fileName="json.txt";
            File path = new File(pathName);
            File file = new File(pathName + "/" + fileName);
            if(!path.exists()) {
                path.mkdir();
            }
            if(!file.exists()) {
                file.createNewFile();
            }


            FileOutputStream stream = new FileOutputStream(file, isAppend);
            Writer out = new OutputStreamWriter(stream, "UTF-8");
            //byte[] buf = s.getBytes();
            //stream.write(buf);
            out.write(s);
            out.close();
            stream.close();

            //ApmLogger.info("write to sd card success, fileName: " + fileName);
            System.out.println("write to sd card success, fileName: " + fileName);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}
