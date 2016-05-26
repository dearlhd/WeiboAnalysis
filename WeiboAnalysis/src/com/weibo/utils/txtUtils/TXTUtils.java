package com.weibo.utils.txtUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TXTUtils {
	public HashMap<String, String> readTXT(String path){
		HashMap<String, String> dic = new HashMap<String, String>();
		try{
			String encoding = "GBK";
		    File file = new File(path);
		    if (file.isFile() && file.exists()){
		    	InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
		    	BufferedReader bufferedReader = new BufferedReader(read);
		    	String lineTxt = null;
		    	while ((lineTxt = bufferedReader.readLine()) != null){
		    		dic.put(lineTxt, "yes");
		    	}
		    	read.close();
		    }
		    else{
		    	System.out.println("找不到指定的文件!");
		    }
		    return dic;
		}
		catch(Exception e){
			System.out.println("读取文件时出错!");
		    e.printStackTrace();
		}
		return dic;
	}
	
	public void writeTXT(String path, String str) throws IOException{
		File file = new File(path);
		if (!file.exists()){
			file.createNewFile();			
		}
		FileOutputStream fos = new FileOutputStream(file);
		fos.write((str).getBytes("GBK"));
		fos.close();
	}
}
