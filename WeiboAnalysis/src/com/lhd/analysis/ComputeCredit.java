package com.lhd.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.SegToken;
import com.weibo.utils.bean.Weibo;
import com.weibo.utils.dictUtils.DictUtils;

public class ComputeCredit {
	List<List<String>> sentences;
	Map<String, Double> adv;
	Map<String, Double> negAdv;
	Map<String, Double> negative;
	Map<String, Double> positive;

	public ComputeCredit() {
		init();
	}
	
	public double getCredit (Weibo wb) {
		double d1 = 0.0, d2 = 0.0;
		if (wb.getContent() != null) {
			d1 = getCreditBySentence (wb.getContent());
		}
		if (wb.getContentOrigin() != null) {
			d2 = getCreditBySentence (wb.getContentOrigin());
		}
		
//		if (d1 == 0.0) {
//			return d2;
//		}
//		if (d2 == 0.0) {
//			return d1;
//		}
		
		return (2.0 * d1 + d2)/3;
	}
	
	public double getCreditBySentence(String content) {
		segmentSentence(content);
		List<Double> credit = new ArrayList<Double>();
		for (int i = 0; i < sentences.size(); i++)
			credit.add(parseSentence(sentences.get(i)));
		
		double sum = 0;
		for (int i = 0; i < credit.size(); i++) {
			sum += credit.get(i);
		}
		sum = sum/credit.size() *(1.0+ 0.1*credit.size());
		return sum;
	}
	
	private void init () {
		sentences = new ArrayList<List<String>>();
		loadDict();
	}
	
	private void loadDict () {
		final String advPath = "Dict/adv/";
		final String negativePath = "Dict/negative/negative.txt";
		final String positivePath = "Dict/positive/positive.txt";
		
		adv = new HashMap<String, Double>();
		negAdv = new HashMap<String, Double>();
		negative = new HashMap<String, Double>();
		positive = new HashMap<String, Double>();
		
		List<String> ls;
		DictUtils du = new DictUtils();
		
		ls = du.readDict(negativePath);
		for (int i = 0; i < ls.size(); i++) {
			negative.put(ls.get(i), -0.8);
		}
		
		ls = du.readDict(positivePath);
		for (int i = 0; i < ls.size(); i++) {
			positive.put(ls.get(i), 0.8);
		}
		
		ls = du.readDict(advPath + "most_0_9.txt");
		for (int i = 0; i < ls.size();i++) {
			adv.put(ls.get(i), 0.9);
		}
		
		ls = du.readDict(advPath + "negNormal_-0_5.txt");
		for (int i = 0; i < ls.size();i++) {
			adv.put(ls.get(i), -0.5);
		}
		
		ls = du.readDict(advPath + "negVery_-0_8.txt");
		for (int i = 0; i < ls.size();i++) {
			negAdv.put(ls.get(i), -0.8);
		}
		
		ls = du.readDict(advPath + "normal_0_5.txt");
		for (int i = 0; i < ls.size();i++) {
			adv.put(ls.get(i), 0.5);
		}
		
		ls = du.readDict(advPath + "soft_0_3.txt");
		for (int i = 0; i < ls.size();i++) {
			adv.put(ls.get(i), 0.3);
		}
		
		ls = du.readDict(advPath + "super_0_9.txt");
		for (int i = 0; i < ls.size();i++) {
			adv.put(ls.get(i), 0.9);
		}
		
		ls = du.readDict(advPath + "very_0_7.txt");
		for (int i = 0; i < ls.size();i++) {
			adv.put(ls.get(i), 0.7);
		}
	}

	private void segmentSentence(String str) {
		sentences.clear();
		sentences.add(new ArrayList<String>());
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List<SegToken> ls = segmenter.process(str, SegMode.SEARCH);
	
		int index = 0;
		for (int i = 0; i < ls.size(); i++) {
			List<String> sentence = sentences.get(index);
			if (!ls.get(i).word.equals(".") && !ls.get(i).word.equals("。")
					&& !ls.get(i).word.equals(";")
					&& !ls.get(i).word.equals("，")
					&& !ls.get(i).word.equals(",")) {
				sentence.add(ls.get(i).word);
			}
			else {
				index++;
				sentences.add(new ArrayList<String>());
			}
		}
		
//		System.out.println(cnt);
//		for (int i = 0; i < sentences.size(); i++) {
//			List<String> sentence = sentences.get(i); 
//			for (int j = 0; j < sentence.size(); j++) {
//				System.out.print(sentence.get(j) + " ");
//			}
//			System.out.println();
//		}
	}
	
	private double parseSentence (List<String> words) {
//		for (int i = 0; i < words.size(); i++) {
//			System.out.print(words.get(i) + " ");
//		}
//		System.out.println();
		
		double credit = 0.0;
		double base = 0.0;
		List<MyPair> posDegree = new ArrayList<MyPair>();
		List<MyPair> negDegree = new ArrayList<MyPair>();
		
		for (int i = 0; i < words.size(); i++) {
			if (positive.containsKey(words.get(i))) {
				base = 0.8;
			}
			else if (negative.containsKey(words.get(i))){
				base = -0.8;
			}
		}
		
		for (int i = 0; i < words.size(); i++) {
			if(adv.containsKey(words.get(i))) {
				double degree = adv.get(words.get(i));
				posDegree.add(new MyPair(i, degree));
			}
			if (negAdv.containsKey(words.get(i))) {
				double degree = negAdv.get(words.get(i)); 
				negDegree.add(new MyPair(i, degree));
			}
		}
		
		int posSz = posDegree.size(), negSz = negDegree.size() ; 
		
		if (posSz == 0 && negSz == 0) {
			credit = base;
		}
		else if (posSz == 0 && negSz == 1) {
			credit = base * negDegree.get(0).degree;
		}
		else if (posSz == 0 && negSz == 2) {
			credit = base * negDegree.get(0).degree * negDegree.get(1).degree;
		}
		else if (posSz > 0) {
			List<Double> nas = new ArrayList<Double>();
			
			for (int i = 0; i < posDegree.size(); i++) {
				nas.add(posDegree.get(i).degree);
			}
			
			if (negSz == 1) {
				if (negDegree.get(0).snum > posDegree.get(posSz-1).snum)
					base = base * (-0.8);
				else {
					for (int i = 0; i < nas.size(); i++) {
						if (nas.get(i) < 0) {
							nas.set(i, nas.get(i)+0.2);
						}
						else {
							nas.set(i, nas.get(i)-0.2);
						}
					}
				}
			}
			if (base > 0) {
				credit = base;
				double left = 1-base;
				for (int i = 0; i < nas.size(); i++) {
					credit += left * nas.get(i);
					left = 1- credit;
				}
			}
			else {
				credit = base;
				double left = -1-base;
				for (int i = 0; i < nas.size(); i++) {
					credit += left * nas.get(i);
					left = -1- credit;
				}
			}
		}
		
		return credit;
	}
	
	private class MyPair{
		public int snum;
		public double degree;
		
		public MyPair(int n, double d) {
			snum = n;
			degree = d;
		}
	}

//	public static void main(String[] args) {
//		new ComputeCredit();
//	}
}
