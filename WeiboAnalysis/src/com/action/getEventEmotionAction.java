package com.action;


import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.opensymphony.xwork2.ActionSupport;
import com.weibo.utils.bean.Weibo;
import com.weibo.utils.csvUtils.CSVUtils;

public class getEventEmotionAction  extends ActionSupport {	

	private String eventContent;
	private String disType;
	private String result;
	private List<Integer> EmotionAmo = new ArrayList<Integer>();
	private List<String> EmotionCla = new ArrayList<String>();
	
	public String getEventContent() {
		return eventContent;
	}

	public void setEventContent(String eventContent) {
		this.eventContent = eventContent;
	}

	public String getDisType() {
		return disType;
	}

	public void setDisType(String disType) {
		this.disType = disType;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public int isLatterDate(String str1, String str2){
		int res = 0;
		String[] date1 = str1.split("/");
		String[] date2 = str2.split("/");
		for (int i = 0; i < 3; ++i){
			if (Integer.valueOf(date1[i]) > Integer.valueOf(date2[i])) return 1;
			else if (Integer.valueOf(date1[i]) < Integer.valueOf(date2[i])) return -1;
		}
		return res;
	}
	
	public int isLatterTime(String str1, String str2){
		int res = 0;
		String[] time1 = str1.split(":");
		String[] time2 = str2.split(":");
		for (int i = 0; i < 2; ++i){
			if (Integer.valueOf(time1[i]) > Integer.valueOf(time2[i])) return 1;
			else if (Integer.valueOf(time1[i]) < Integer.valueOf(time2[i])) return -1;
		}
		return res;
	}
	
	public boolean isLatter(String str1, String str2){
		String[] spl1 = str1.split(" ");
		String[] spl2 = str2.split(" ");
		String date1 = spl1[0], time1 = spl1[1];
		String date2 = spl2[0], time2 = spl2[1];
		
		if (isLatterDate(date1, date2) > 0){
			return true;
		}
		else if (isLatterDate(date1, date2) < 0){
			return false;			
		}
		else{
			if (isLatterTime(time1, time2) > 0){
				return true;
			}
			else return false;
		}		
	}

	public void InitClassify(){
		EmotionCla.add("-1.0~-0.8");
		EmotionCla.add("-0.8~-0.6");
		EmotionCla.add("-0.6~-0.2");
		EmotionCla.add("-0.2~0.0");
		EmotionCla.add("0.0~0.2");
		EmotionCla.add("0.2~0.6");
		EmotionCla.add("0.6~0.8");
		EmotionCla.add("0.8~1.0");
		for (int i = 0; i < 8; ++i){
			EmotionAmo.add(0);
		}
	}

	public String execute() throws Exception{	
		try{
			CSVUtils CSV = new CSVUtils();		
			List<Weibo> wb = null;
			if (disType.equals("Stanford")){
				wb = CSV.readCSV("G:/work/Java/WeiboAnalysis/WeiboAnalysis/data/results/result_qsk.csv");
			}
			else if (disType.equals("JieBa")){
				wb = CSV.readCSV("G:/work/Java/WeiboAnalysis/WeiboAnalysis/data/results/result_lhd.csv");
			}
			
			//System.out.println("fuck in");
			//System.out.println(eventContent);
			
			List<Double> EmotionVal = new ArrayList<Double>();
			List<String> EmotionDat = new ArrayList<String>();
			
			//System.out.println(wb.size());
			
			int len = wb.size();
			for (int i = 0; i < wb.size(); ++i){
				String[] eventSet = wb.get(i).getTopic().split(",");
				boolean refer = false;
				for (int j = 0; j < eventSet.length; ++j){
					//System.out.println(eventSet[j]);
					if (eventSet[j].equals(eventContent)){
						refer = true;
						break;
					}
				}
				if (refer == true){
					EmotionVal.add(wb.get(i).getCredit());
					EmotionDat.add(wb.get(i).getTime());
				}
				//if (EmotionVal.size() > 80) break;
			}
			
			//System.out.println(EmotionVal);
			
			
			len = EmotionDat.size();
		/*	for (int i = 0; i < len - 1; ++i){
				for (int j = i + 1; j < len; ++j){
					if (isLatter(EmotionDat.get(i), EmotionDat.get(j))){
						String Datetmp = EmotionDat.get(i);
						EmotionDat.set(i, EmotionDat.get(j));
						EmotionDat.set(j, Datetmp);
						double Valtmp = EmotionVal.get(i);
						EmotionVal.set(i, EmotionVal.get(j));
						EmotionVal.set(j, Valtmp);
					}
				}
			}*/
			
			InitClassify();
			//System.out.println(len);
			
			for (int i = 0; i < len; ++i){
				double val = EmotionVal.get(i);
				//System.out.println(val);
				if (((-1.0) <= val) && (val < (-0.8))){
					EmotionAmo.set(0, EmotionAmo.get(0) + 1);
				}
				else if (((-0.8) <= val) && (val < (-0.6))){
					EmotionAmo.set(1, EmotionAmo.get(1) + 1);
				}
				else if (((-0.6) <= val) && (val < (-0.2))){
					EmotionAmo.set(2, EmotionAmo.get(2) + 1);
				}
				else if (((-0.2) <= val) && (val < 0.0)){
					EmotionAmo.set(3, EmotionAmo.get(3) + 1);
				}
				else if ((0.0 <= val) && (val < 0.2)){
					EmotionAmo.set(4, EmotionAmo.get(4) + 1);
				} 
				else if ((0.2 <= val) && (val < 0.6)){
					EmotionAmo.set(5, EmotionAmo.get(5) + 1);
				} 
				else if ((0.6 <= val) && (val < 0.8)){
					EmotionAmo.set(6, EmotionAmo.get(6) + 1);
				} 
				else if ((0.8 <= val) && (val <= 1.0)){
					EmotionAmo.set(7, EmotionAmo.get(7) + 1);
				} 
				
			}
			
//			System.out.println(EmotionVal);
//			System.out.println(EmotionDat);
//			System.out.println(EmotionCla);
//			System.out.println(EmotionAmo);
			
			JSONObject json = new JSONObject();
			json.put("Amo", EmotionAmo);
			json.put("Cla", EmotionCla);
			
			result = json.toString();
			return SUCCESS; 	     
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} 		
	}

}
