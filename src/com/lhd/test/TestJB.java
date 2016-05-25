package com.lhd.test;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

public class TestJB {

	public static void main(String[] args) {
		JiebaSegmenter segmenter = new JiebaSegmenter();
	    String[] sentences =
	        new String[] {"老王今天去上班了", "�Ҳ�ϲ���ձ��ͷ���", "�׺�ع��˼䡣",
	                      "���Ŵ�Ů����ÿ�¾����������Ҷ�Ҫ�׿ڽ���24�ڽ������ȼ����������İ�װ����", "�����ĺ���δ������"};
	    //for (String sentence : sentences) {
	        System.out.println(segmenter.process(sentences[0], SegMode.INDEX).toString());
	    //}
	}

}
