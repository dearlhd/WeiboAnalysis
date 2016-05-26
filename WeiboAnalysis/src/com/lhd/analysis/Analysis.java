package com.lhd.analysis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.weibo.utils.bean.Weibo;
import com.weibo.utils.csvUtils.CSVUtils;
import com.weibo.utils.thread.MyThread;

public class Analysis {
	List<Weibo> wbs;
	final String dataPath = "data/resources/data_processed.csv";
	final String resultPath = "data/results/result_lhd.csv";
	
	public Analysis () {
		init();
	}
	
	private void init() {
		System.out.println("Initing analysis...");
		
		CSVUtils csv = new CSVUtils();
		wbs = csv.readCSV(dataPath);
	}
	
	public void doAnalysis() {
		System.out.println("Do analysis...");
//		int testCnt = 100;
//		MyThread t1 = new MyThread (wbs, 0, testCnt / 4);
//		MyThread t2 = new MyThread (wbs, testCnt / 4 + 1, testCnt / 2);
//		MyThread t3 = new MyThread (wbs, testCnt / 2 + 1, testCnt / 4 * 3);
//		MyThread t4 = new MyThread (wbs, testCnt / 4 * 3 + 1, testCnt - 1);
		
		MyThread t1 = new MyThread (wbs, 0, wbs.size() / 4);
		MyThread t2 = new MyThread (wbs, wbs.size() / 4 + 1, wbs.size() / 2);
		MyThread t3 = new MyThread (wbs, wbs.size() / 2 + 1, wbs.size() / 4 * 3);
		MyThread t4 = new MyThread (wbs, wbs.size() / 4 * 3 + 1, wbs.size() - 1);
		
		System.out.println("Preparing thread pool...");
		ExecutorService pool = Executors.newCachedThreadPool();
		
		System.out.println("Thread executing...");
		pool.execute(t1);
		pool.execute(t2);
		pool.execute(t3);
		pool.execute(t4);
		
		pool.shutdown();
		
		while (true) {
			if (pool.isTerminated()) {
				System.out.println("All thread executed!");
				
				System.out.println("Saving to CSV...");
				CSVUtils cu = new CSVUtils();
				cu.saveCSV(resultPath, wbs);
				
				System.out.println("Finished!");
				break;
			}
		}
	}
	
	public static void main (String[] args) {
		Date now1 = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		
		
		Analysis a = new Analysis();
		a.doAnalysis();
		
		Date now2 = new Date(); 
		
		System.out.println(dateFormat.format( now1 ));
		
		System.out.println(dateFormat.format( now2 ));
	}
}
