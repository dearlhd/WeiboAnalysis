package com.weibo.utils.thread;

import java.util.List;

import com.lhd.analysis.Analysis;
import com.lhd.analysis.ComputeCredit;
import com.weibo.utils.bean.Weibo;

public class MyThread extends Thread{
	private int left;
	private int right;
	List<Weibo> wbs;
	
	public MyThread (List<Weibo>w, int ll, int rr) {
		wbs = w;
		left = ll;
		right = rr;
	}
	
	public void run() {
		ComputeCredit cc = new ComputeCredit();
		
		for (int i = left; i <= right; i++) {
			Weibo wb = wbs.get(i);
			wb.setCredit(cc.getCredit(wb));
		}
	}
}
