package com.lhd.test;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.lhd.analysis.ComputeCredit;
import com.weibo.utils.bean.Weibo;

public class TestJB {

	public static void main(String[] args) {
//		JiebaSegmenter segmenter = new JiebaSegmenter();
//		System.out.println(segmenter.sentenceProcess("今天心情很愉快[心]"));
//		String[] sentences = new String[] { "他长的很美" };
//		// for (String sentence : sentences) {
//		System.out.println(segmenter.process(sentences[0], SegMode.INDEX).toString());
//		System.out.println(segmenter.process(sentences[0], SegMode.SEARCH).toString());
//		// }
		ComputeCredit cc = new ComputeCredit();
		String content = "我宣布我暴毙了 我要是五月不去上海我真的会爆哭";
		String origin = "?? 【原微博】  嘉尔宝宝很怕辣哦[doge] http://t.cn/RGBPATz .";
		Weibo wb = new Weibo();
		wb.setContent(content);
		wb.setContentOrigin(origin);
		double d1 = cc.getCreditBySentence(content);
		double d2 = cc.getCreditBySentence(origin);
		double d3 = cc.getCredit(wb);
		System.out.println("d1 " + d1 + " d2 " + d2 + " d3 " + d3);
		
	}

}
