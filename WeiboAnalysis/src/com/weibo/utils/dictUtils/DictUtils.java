package com.weibo.utils.dictUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DictUtils {
	public List<String> readDict(String path) {
		File file = new File(path);
		
		BufferedReader reader = null;
		List<String> words = new ArrayList<String>();
		try {
			FileInputStream fis= new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis, "GBK"));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	words.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
		return words;
	}
}
