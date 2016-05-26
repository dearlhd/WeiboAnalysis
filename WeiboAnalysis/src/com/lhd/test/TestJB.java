package com.lhd.test;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

public class TestJB {

	public static void main(String[] args) {
		JiebaSegmenter segmenter = new JiebaSegmenter();
		System.out.println(segmenter.sentenceProcess("今天心情很愉快"));
		String[] sentences = new String[] { "他长的很美" };
		// for (String sentence : sentences) {
		System.out.println(segmenter.process(sentences[0], SegMode.INDEX).toString());
		System.out.println(segmenter.process(sentences[0], SegMode.SEARCH).toString());
		// }
	}

}
