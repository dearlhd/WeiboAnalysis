package com.qsk.analysis;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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


public class Analysis{
	
	  private static final String basedir = System.getProperty("SegDemo", "WebContent/dict");
	  
	  private static final TXTUtils txt = new TXTUtils();
	  private static final HashMap<String, String> posWord = txt.readTXT("WebContent/dict/正面词/正面词（0.8）.txt");
	  private static final HashMap<String, String> negWord = txt.readTXT("WebContent/dict/负面词/负面词（-0.8）.txt");
	  private static final HashMap<String, String> negAdv = txt.readTXT("WebContent/dict/否定副词/否定（-0.8）.txt");
	  private static final HashMap<String, String> degsupAdv = txt.readTXT("WebContent/dict/程度词语/超（0.9）.txt");
	  private static final HashMap<String, String> degmosAdv = txt.readTXT("WebContent/dict/程度词语/最（0.9）.txt");
	  private static final HashMap<String, String> degverAdv = txt.readTXT("WebContent/dict/程度词语/很（0.7）.txt");
	  private static final HashMap<String, String> degmorAdv = txt.readTXT("WebContent/dict/程度词语/较（0.5）.txt");
	  private static final HashMap<String, String> deglitAdv = txt.readTXT("WebContent/dict/程度词语/稍（0.3）.txt");  
	  private static final HashMap<String, String> degnegAdv = txt.readTXT("WebContent/dict/程度词语/欠（-0.5）.txt");
	  private static final HashMap<String, String> negExp = txt.readTXT("WebContent/dict/expression/neg.txt");
	  private static final HashMap<String, String> posExp = txt.readTXT("WebContent/dict/expression/pos.txt");  
	  private static final HashMap<String, String> neuExp = txt.readTXT("WebContent/dict/expression/neutral.txt");
	  
	  private static CRFClassifier<CoreLabel> segmenter;
	  
	  public static void loadSegmenter(String[] args) throws IOException{
		  Properties props = new Properties();
		  props.setProperty("sighanCorporaDict", basedir);
		  props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
		  if (args.length > 0) {
		    props.setProperty("testFile", args[0]);
		  }
		  props.setProperty("inputEncoding", "GBK");
		  props.setProperty("sighanPostProcessing", "true");
		  segmenter = new CRFClassifier<CoreLabel>(props);
		  segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
		  for (String filename : args) {
		    segmenter.classifyAndWriteAnswers(filename);
		  }
	  }
	  
	  public boolean isNA(String str){
		  if (negAdv.containsKey(str)) return true;
		  else return false;
	  }
	  
	  public boolean isDA(String str){
		  if ((degsupAdv.containsKey(str)) || (degmosAdv.containsKey(str)) || (degverAdv.containsKey(str))
				  || (degmorAdv.containsKey(str)) || (deglitAdv.containsKey(str)) || (degnegAdv.containsKey(str)))
				  return true;
		  else return false;
	  }

	  public double getNAScr(){
		  return 0.8;
	  }
	  
	  public double getDAScr(String str){
		  if (degsupAdv.containsKey(str)) return 0.9;
		  if (degmosAdv.containsKey(str)) return 0.9;
		  if (degverAdv.containsKey(str)) return 0.7;
		  if (degmorAdv.containsKey(str)) return 0.5;
		  if (deglitAdv.containsKey(str)) return 0.3;
		  if (degnegAdv.containsKey(str)) return -0.5;
		  return 1.0;
	  }
	  
	  public double getEmotionScore(List<String> seg, int head, int tail, double scr){
		  double PWscore = scr;
		  if (head > tail) return PWscore;
		  List<String> phraseTyp = new ArrayList<String>();
		  List<Double> phraseScr = new ArrayList<Double>();
		  
		  for (int i = head; i <= tail; ++i){
			  if (isNA(seg.get(i))){
				  phraseTyp.add("NA");
				  phraseScr.add(getNAScr());
			  }
			  else if (isDA(seg.get(i))){
				  phraseTyp.add("DA");
				  phraseScr.add(getDAScr(seg.get(i)));
			  }
		  }
		  
		  int cur = phraseTyp.size() - 1;
		  while (cur >= 0){
			  if (phraseTyp.get(cur).equals("NA")){
				  PWscore *= phraseScr.get(cur);
				  cur--;
			  }
			  else break;
		  }		
		  
		  boolean NAflag = false;
		  boolean DAflag = false;
		  for (int i = 0; i <= cur; ++i){
			  if (phraseTyp.get(i).equals("NA")){
				  NAflag = true; 
			  }
			  if (phraseTyp.get(i).equals("DA")){
				  DAflag = true; 
			  }
		  }
//		  System.out.println(NAflag);
//		  System.out.println(DAflag);
		  if ((NAflag == false) && (DAflag == false)) return PWscore;
		  else if ((NAflag == false) && (DAflag == true)){
			  double score = PWscore;
			  for (int i = 0; i <= cur; ++i){
				  if (score > 0){
					  score += (1 - score) * phraseScr.get(i); 
				  }
				  else{
					  score += (-1 - score) * phraseScr.get(i);
				  }					  
			  }
			  return score;
		  }
		  else if (DAflag == true){
			  double score = PWscore;
			  int cur_begin = cur;
			  while (phraseTyp.get(cur_begin).equals("DA")){
				  cur_begin--;
			  }
			  cur_begin++;
			  for (int i = cur_begin; i <= cur; ++i){
				  double new_L;
				  if (phraseScr.get(i) > 0)
					  new_L = phraseScr.get(i) - 0.2;
				  else new_L = phraseScr.get(i) + 0.2;
				  if (score > 0){					
					  score += (1 - score) * new_L; 
				  }
				  else{
					  score += (-1 - score) * new_L;
				  }					  
			  }
			  return score;
		  }
		  
		  return 0.0;
	  }
	  
	  public double getSentenceEmotionScore(String sentence){
		  double sentScr = 0.0;
		  double exprScr = 0.0;
		  int sect_sum = 0;
		  int expr_sum = 0;
		  List<String> segmented = segmenter.segmentString(sentence);
		  
		  //System.out.println(segmented);
		  
		  Analysis stan = new Analysis();
	      int lastPW = 0;
	      int len = segmented.size();
	      int i = 0;
	      while (i < len){
	    	  Double score = 1.0; 
	    	  if (posWord.containsKey(segmented.get(i))){
	    		  score *= 0.8;
	    		  sect_sum++;
	    		 // DecimalFormat df = new DecimalFormat("#.00");  
	              //System.out.println(df.format(stan.getEmotionScore(segmented, lastPW, i - 1, score)));
	    		  sentScr += stan.getEmotionScore(segmented, lastPW, i - 1, score);
	    		  //System.out.println(segmented.get(i));
	    		  lastPW = i + 1;
	    	  }
	    	  else if (negWord.containsKey(segmented.get(i))){
	    		  score *= -0.8;
	    		  sect_sum++;
	    		  sentScr += stan.getEmotionScore(segmented, lastPW, i - 1, score);
	    		  lastPW = i + 1;
	    	  }
	    	  else if (segmented.get(i).equals("[")){
	    		  
	    		  i++;
	    		  int _head = i;
	    		  while ((i < len) && (!segmented.get(i).equals("]"))){
	    			  i++;
	    		  }
	    		  String expression = "";
	    		  while (_head < i){
	    			  expression += segmented.get(_head++);	    			  
	    		  }
    			  //System.out.println(expression);
	    		  if (posExp.containsKey(expression)){
		    		  exprScr += 0.1;
		    		  expr_sum++;
		    	  }
		    	  else if (negExp.containsKey(expression)){
		    		  exprScr += -0.1;
		    		  expr_sum++;
		    	  }
		    	  else if (neuExp.containsKey(expression)){
		    		  expr_sum++;
		    	  }
	    	  }
	    	  else{
	    		  i++;
	    		  continue;
	    	  }
	    	  i++;
	      }
	      double Scr = 0;
	      if (sect_sum != 0) Scr += (sentScr / sect_sum);	    
	      if (expr_sum != 0) Scr += (exprScr / expr_sum);
//	      System.out.println(sentScr);
//	      System.out.println(exprScr);
	      return Scr;
	  }
	  
	  public static void main(String[] args) throws Exception {

		  loadSegmenter(args);
		  Analysis stan = new Analysis();
	    
		  String path = "data/resources/data_processed.csv";
		  CSVUtils CSV = new CSVUtils();
		  List<Weibo> wb = CSV.readCSV(path);
		  
//		  File file = new File("data/resources/haha.txt");
//		  if (!file.exists()){
//			  file.createNewFile();			
//		  }
//		  FileOutputStream fos = new FileOutputStream(file);
			
		  for (int i = 0; i < wb.size(); ++i){
			  String str = wb.get(i).getContent();
			  if ((str == "") || (str == null)){
				  str = wb.get(i).getContentOrigin();
			  }
				
			  wb.get(i).setCredit(stan.getSentenceEmotionScore(str));
			  //System.out.println(stan.getSentenceEmotionScore("上海上港说：＂嘿嘿，小许啊，孔卡你不要我就要了，埃尔克森你不要我还要。3轮过后我小组第一了，你第几呢， ＂。恒大老许回道：＂哼哼，俺滴心思你不懂，老夫足球玩腻了，在玩岛呢，学着点……[阴险]＂。 不管结果如何，这个赛季结束，亚洲人民都知道＂吉安＂了，呵呵。 自编笑话一则……"));
			 // String str = "[笑cry][笑cry]哈哈";
			 // System.out.println(stan.getSentenceEmotionScore(str));
			 // fos.write((str + "\n").getBytes("GBK"));
			 // fos.write((stan.getSentenceEmotionScore(str) + "\n").getBytes("GBK"));
		  }
		  
		  CSV.saveCSV("data/results/result_qsk.csv", wb);
	//		  System.out.println(str);
	//		  System.out.println(stan.getSentenceEmotionScore(str));
			/*  int hd = 0, tl = str.length() - 1;
			  while (hd <= tl){				 
				  int ls = 0;
				  if (str.charAt(hd) == '['){					  
					  hd++;
					  ls = hd;
					  while ((hd <= tl) && (str.charAt(hd) != ']')){
						  hd++;
					  }
					  //System.out.println(str.substring(ls, hd));
					  fos.write((str.substring(ls, hd) + "\n").getBytes("GBK"));
				  }
				  else hd++;
				  
			  }*/

		  
		//  fos.close();
		  
	
		//  txt.writeTXT("data/resources/haha.txt", "aasd");
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
