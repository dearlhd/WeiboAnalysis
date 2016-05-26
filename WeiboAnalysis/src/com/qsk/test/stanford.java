package com.qsk.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.weibo.utils.bean.Weibo;
import com.weibo.utils.csvUtils.*;
import com.weibo.utils.txtUtils.*;

import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;


public class stanford {
	
	  private static final String basedir = System.getProperty("SegDemo", "WebContent/dict");

	  public static void main(String[] args) throws Exception {
	    Properties props = new Properties();
	    props.setProperty("sighanCorporaDict", basedir);
	    // props.setProperty("NormalizationTable", "data/norm.simp.utf8");
	    // props.setProperty("normTableEncoding", "UTF-8");
	    // below is needed because CTBSegDocumentIteratorFactory accesses it
	    props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
	    if (args.length > 0) {
	      props.setProperty("testFile", args[0]);
	    }
	    props.setProperty("inputEncoding", "GBK");
	    props.setProperty("sighanPostProcessing", "true");

	    CRFClassifier<CoreLabel> segmenter = new CRFClassifier<CoreLabel>(props);
	    segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
	    for (String filename : args) {
	      segmenter.classifyAndWriteAnswers(filename);
	    }

	    String sample = "长春市长春药店";
	    List<String> segmented = segmenter.segmentString(sample);
	    System.out.println(segmented);
	    
	    String sample1 = "她长得不可能不漂亮";
	    List<String> segmented1 = segmenter.segmentString(sample1);
	    System.out.println(segmented1);
	  }

	
/*	private static final TXTUtils txt = new TXTUtils();
	private static final List<String> posWord = txt.readTXT("WebContent/dict/正面词/正面词（0.8）.txt");
	private static final List<String> negWord = txt.readTXT("WebContent/dict/负面词/负面词（-0.8）.txt");
	
	public static void main(String[] args) {
//		JiebaSegmenter segmenter = new JiebaSegmenter();

	    String path = "data/resources/data_processed.csv";
	    List<Weibo> wb = new ArrayList<Weibo>();
	    CSVUtils csv = new CSVUtils();
	    wb = csv.readCSV(path);
	    //System.out.println(wb.get(0).getTime() + " : " + wb.get(0).getUser() + " : " + wb.get(0).getContent());
	    //System.out.print(wb.size());
	    
//	    String sentences ="我不怎么可能不得不去看看了";//"主要的问题是她长得不是很漂亮";
//	    System.out.println(segmenter.process(sentences, SegMode.INDEX).toString());
	    //System.out.println(segmenter.process(sentences, SegMode.INDEX).get(2).word);
	    
	    System.out.print(posWord.size());
	}*/
}
