package com.lhd.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.SegToken;
import com.weibo.utils.dictUtils.DictUtils;

public class ComputeCredit {
	List<List<String>> sentences;
	Map<String, Double> adv;
	Map<String, Double> negative;
	Map<String, Double> positive;

	public ComputeCredit() {
		init();
		segmentSentence("我觉得这个很不准啊");
		parseSentence(sentences.get(0));
	}
	
	private void init () {
		sentences = new ArrayList<List<String>>();
		sentences.add(new ArrayList<String>());
		loadDict();
	}
	
	private void loadDict () {
		final String advPath = "Dict/adv/";
		final String negativePath = "Dict/negative/negative.txt";
		final String positivePath = "Dict/positive/positive.txt";
		
		adv = new HashMap<String, Double>();
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
			adv.put(ls.get(i), -0.8);
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
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List<SegToken> ls = segmenter.process(str, SegMode.INDEX);
	

		int cnt = 0;
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
				cnt++;
				sentences.add(new ArrayList<String>());
			}
		}
		
		System.out.println(cnt);
		for (int i = 0; i < sentences.size(); i++) {
			List<String> sentence = sentences.get(i); 
			for (int j = 0; j < sentence.size(); j++) {
				System.out.print(sentence.get(j) + " ");
			}
			System.out.println();
		}
		
	}
	
	private double parseSentence (List<String> words) {
		for (int i = 0; i < words.size(); i++) {
			System.out.print(words.get(i));
		}
		System.out.println();
		
		double credit = 0.0;
		double base = 0.0;
		List<Double> degree = new ArrayList<Double>();
		
		for (int i = 0; i < words.size(); i++) {
			if (positive.containsKey(words.get(i))) {
				System.out.println("Base " + words.get(i));
				base = 0.8;
			}
			else if (negative.containsKey(words.get(i))){
				System.out.println("Base " + words.get(i));
				base = -0.8;
			}
		}
		
		for (int i = 0; i < words.size(); i++) {
			if(adv.containsKey(words.get(i))) {
				System.out.println("Degree " + words.get(i));
				degree.add(adv.get(words.get(i)));
			}
		}
		
		credit = base;
		for (int i = 0; i < degree.size(); i++) {
			credit *= degree.get(i);
		}
		
		System.out.println("Credit: " + credit);
		
		return credit;
	}

	public static void main(String[] args) {
		new ComputeCredit();
	}
}
