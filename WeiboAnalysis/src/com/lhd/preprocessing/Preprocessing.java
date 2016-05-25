package com.lhd.preprocessing;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.csvreader.CsvReader;
import com.weibo.utils.bean.Weibo;
import com.weibo.utils.csvUtils.CSVUtils;

public class Preprocessing {
	private List<Weibo> wbs;
	ArrayList<String[]> dataList;
	String preDataPath = "data/resources/data_sorted.csv";
	String processedDataPath = "data/resources/data_processed.csv";

	private void init() {
		wbs = new ArrayList<Weibo>();
		dataList = new ArrayList<String[]>();
	}
	
	private void readFromFile() {
		try {
			CsvReader reader = new CsvReader(preDataPath, ',', Charset.forName("GBK")); // 一般用这编码读就可以了

			while (reader.readRecord()) { // 逐行读入数据
				dataList.add(reader.getValues());
			}
			
			reader.close();

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
	
	boolean checkForward (String content) {
		if (content.length() < 2) {
			return false;
		}
		
		String title = content.substring(0,2);
		if (title.equals("//")) {
			return false;
		}
		
		if (content.length() >= 11) {
			title = content.substring(0,10);
			if (title.equals("转发微博 【原微博】")) {
				return false;
			}
			
			title = content.substring(0, 11);
			if (title.equals("转发微博。 【原微博】")) { 
				return false;
			}
		}
		
		return true;
	}
	
	boolean checkUser (String name) {
		if (name.equals("#NAME?")) {
			return false;
		}
		return true;
	}
	
	String findTopic (StringBuffer content) {
		int m = 0;
		boolean f = false;
		String ret = "";
		
		for (int i = 0; i < content.length(); i++) {
			if (content.charAt(i) == '#' || content.charAt(i) == '#') {
				if (!f) {
					f = true;
					m = i;
				}
				else {
					f = false;
					if (ret.length() == 0) {
						ret = content.substring(m+1,i);
					}
					else {
						ret = ret + "," + content.substring(m+1,i);
					}
					content.replace(m, i+1, "");
					i = m;
				}
			}
		}
		
		return ret;
		
	}
	
	private void doPreprocess() {
		for (int i = 0; i < dataList.size(); i++) {
			if (!checkForward(dataList.get(i)[2])) {
				continue;
			}
			
			if (!checkUser(dataList.get(i)[8])) {
				continue;
			}
			
			Weibo wb = new Weibo();
			wb.setTime(dataList.get(i)[3]);
			wb.setUser(dataList.get(i)[8]);
			
			StringBuffer sb = new StringBuffer(dataList.get(i)[2]);
			wb.setTopic(findTopic(sb));
			
			wb.setContent(sb.toString());
			wb.setCredit(0.0);
			wbs.add(wb);
		}
	}
	
	private void saveData() {
		CSVUtils cu = new CSVUtils();
		cu.saveCSV(processedDataPath, wbs);
	}
	
	public Preprocessing() {
		System.out.println("Initing...");
		init();
		System.out.println("Read primitive data...");
		readFromFile();
		System.out.println("Do preprocessing...");
		doPreprocess();
		System.out.println("Saving data...");
		saveData();
		System.out.println("Finished!");
	}
	
	public static void main (String[] arg) {
		new Preprocessing();
	}
}
