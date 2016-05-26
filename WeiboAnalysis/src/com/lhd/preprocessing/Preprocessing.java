package com.lhd.preprocessing;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		
		if (content.length() >= 4) {
			title = content.substring(0,4);
			if (title.equals("转发微博")) {
				return false;
			}
			
//			title = content.substring(0,10);
//			if (title.equals("转发微博 【原微博】")) {
//				return false;
//			}
//			
//			title = content.substring(0, 11);
//			if (title.equals("转发微博。 【原微博】")) { 
//				return false;
//			}
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
		List<String> topics = new ArrayList<String>();
		String ret = "";
		
		for (int i = 0; i < content.length(); i++) {
			if (content.charAt(i) == '#' || content.charAt(i) == '#') {
				if (!f) {
					f = true;
					m = i;
				}
				else {
					f = false;
					topics.add(content.substring(m+1,i));
					content.replace(m, i+1, "");
					i = m-1;
				}
			}
		}
		
		for (int i = 0; i < topics.size(); i++) {
			for (int j = i+1; j < topics.size(); j++) {
				if (topics.get(i).equals(topics.get(j))) {
					topics.remove(j);
					j--;
				}
			}
		}
		
		if (topics.size() == 0) {
			return "";
		}
		ret += topics.get(0);
		
		for (int i = 1; i < topics.size(); i++) {
			ret += ',' + topics.get(i);
		}
		
		return ret;
	}
	
	String findRelative (StringBuffer content) {
		int m = 0;
		boolean f = false;
		String ret = "";
		List<String> relatives = new ArrayList<String>();
		
		for (int i = 0; i < content.length(); i++) {
			if (content.charAt(i) == '@') {
				if (!f) {
					f = true;
					m = i;
				}
			}
			else if (content.charAt(i) == ' ' || content.charAt(i) == ':' || content.charAt(i) == ':') {
				if (f) {
					f = false;
					relatives.add(content.substring(m+1,i));
					content.replace(m, i+1, "");
					i = m;
				}
			}
		}
		
		for (int i = 0; i < relatives.size(); i++) {
			for (int j = i+1; j < relatives.size(); j++) {
				if (relatives.get(i).equals(relatives.get(j))) {
					relatives.remove(j);
					j--;
				}
			}
		}
		
		if (relatives.size() == 0) {
			return "";
		}
		ret += relatives.get(0);
		
		for (int i = 1; i < relatives.size(); i++) {
			ret += ',' + relatives.get(i);
		}
		
		return ret;
	}
	
	String divideContent (StringBuffer content) {
		String origin = "";
		int mark = content.indexOf("//【原微博】");
		if (mark != -1) {
			origin = content.substring(mark+7, content.length()); 
			content.replace(mark, content.length(), "");
			return origin;
		}
		
		int mark1 = content.indexOf("//");
		if (mark1 >= 5) {
			if (content.substring(mark1-5, mark1).equals("http:")) {
				mark1 = 2 + mark1 + content.substring(mark1+2, content.length()).indexOf("//");
			}
		}
		
		int mark2 = content.indexOf("【原微博】");
		
		if (mark1 == -1 && mark2 == -1) {
			return origin;
		}
		else if (mark1 == -1 && mark2 != -1) {
			origin = content.substring(mark2+5, content.length()); 
			content.replace(mark2, content.length(), "");
		}
		else if (mark1 != -1 && mark2 == -1) {
			origin = content.substring(mark1+2, content.length()); 
			content.replace(mark1, content.length(), "");
		}
		else if (mark1 != -1 && mark2 != -1) {
			if (mark1 <= mark2) {
				origin = content.substring(mark1+2, content.length()); 
				content.replace(mark1, content.length(), "");
			}
			else {
				origin = content.substring(mark2+5, content.length()); 
				content.replace(mark2, content.length(), "");
			}
		}
		
		return origin;
		
	}
	
	// 返回删除了多少条冗余微博
	private int processByUser(int ll, int rr) {
		if ((rr - ll + 1) < 3) {
//			for (int i = rr; i >= ll; i--) {
//				wbs.remove(i);
//			}
//			return rr - ll + 1;
			return 0;
		}
		else {
			Map<Integer, Integer> hm = new HashMap<Integer, Integer>();
			for (int i = ll; i <= rr; i++) {
				hm.put(i, 0);
			}
			
			for (int i = ll; i < rr; i++) {
				String content = wbs.get(i).getContent();
				String contentOrigin = wbs.get(i).getContentOrigin();
				for (int j = i+1; j <= rr; j++) {
					if (content.equals(wbs.get(j).getContent()) || contentOrigin.equals(wbs.get(j).getContentOrigin())) {
						hm.put(j, hm.get(j)+ 1);
					}
				}
			}
			int cnt = 0;
			for (int i = rr; i >= ll; i--) {
				if (hm.get(i) != 0) {
					cnt++;
					wbs.remove(i);
				}
			}
			return cnt;
		}
		
	}
	
	private void doPreprocess() {
		// 单条处理
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
			wb.setRelativeUser(findRelative(sb));
			wb.setContentOrigin(divideContent(sb));
			wb.setContent(sb.toString());
			
			wb.setCredit(0.0);
			wbs.add(wb);
		}
		
		// 根据用户处理
		String name = wbs.get(0).getUser();
		int left = 0, right = 0;
		
		for (int i = 0; i < wbs.size(); i++){
			if (wbs.get(i).getUser().equals(name)) {
				right = i;
			}
			else {
				name = wbs.get(i).getUser();
				int cnt = processByUser(left, right);
				i -= cnt;
				left = i;
				i--;
			}
		}
		processByUser(left, right);
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
