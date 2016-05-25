package com.weibo.utils.csvUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.weibo.utils.bean.Weibo;

public class CSVUtils {
	public void saveCSV(String path, List<Weibo> wbs) {
		CsvWriter wr = new CsvWriter(path, ',', Charset.forName("GBK"));
		
		for (int i = 0; i < wbs.size(); i++) {
			Weibo wb = wbs.get(i);
			String[] contents = { wb.getTime(), wb.getUser(), wb.getContent(),
					wb.getTopic(), String.valueOf(wb.getCredit()) };
			
			try {
				wr.writeRecord(contents);
			} catch (IOException e) {
				System.out.println("Error occured when writing csv file");
				e.printStackTrace();
			}
		}
		wr.close();
	}
	
	public List<Weibo> readCSV (String path) {
		List<Weibo> wbs = new ArrayList<Weibo>();
		try {
			List<String[]> dataList = new ArrayList<String[]>(); // 用来保存数据
			CsvReader reader = new CsvReader(path, ',', Charset.forName("GBK"));
			while (reader.readRecord()) { // 逐行读入除表头的数据
				dataList.add(reader.getValues());
			}
			for (int i = 0; i < dataList.size(); i++) {
				Weibo wb = new Weibo();
				String[] data = dataList.get(i);
				wb.setTime(data[0]);
				wb.setUser(data[1]);
				wb.setContent(data[2]);
				wb.setTopic(data[3]);
				wb.setCredit(Double.parseDouble(data[4]));
				wbs.add(wb);
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Error occured when reading csv file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error occured when reading csv records");
			e.printStackTrace();
		}
		return wbs;
	}
}
