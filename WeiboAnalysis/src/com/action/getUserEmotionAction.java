package com.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.opensymphony.xwork2.ActionSupport;
import com.weibo.utils.bean.Weibo;
import com.weibo.utils.csvUtils.CSVUtils;

import org.apache.struts2.ServletActionContext;

public class getUserEmotionAction  extends ActionSupport {	

	private String userName;
	private String disType;
	private String result;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
		
		
	//	System.out.print("||" + date1 + " ___ " + time1 + "||");
	//	System.out.print("||" + date2 + " ___ " + time2 + "||");
		
		if (isLatterDate(date1, date2) > 0){
			return true;
		}
		else if (isLatterDate(date1, date2) < 0){
			return false;			
		}
		else{
			//System.out.println("In it");
			if (isLatterTime(time1, time2) > 0){
				return true;
			}
			else return false;
		}		
		//System.out.println(date1 + " ___ " + time1);
	}

	public String execute() throws Exception{	
		try{
//			System.out.println(userName);
//			System.out.println(disType);
			CSVUtils CSV = new CSVUtils();
			
			/*File dic = new File("");
			System.out.println(dic.getCanonicalPath());
			System.out.println(dic.getAbsolutePath());
			
			String pa = ServletActionContext.getServletContext().getContextPath();
			System.out.println(pa);		
			String pb = ServletActionContext.getServletContext().getRealPath("result_qsk.csv");
			System.out.println(pb);	*/
			
			List<Weibo> wb = null;
			if (disType.equals("Stanford")){
				wb = CSV.readCSV("G:/work/Java/WeiboAnalysis/WeiboAnalysis/data/results/result_qsk.csv");
			}
			else if (disType.equals("JieBa")){
				wb = CSV.readCSV("G:/work/Java/WeiboAnalysis/WeiboAnalysis/data/results/result_lhd.csv");
			}
			
			List<Double> EmotionVal = new ArrayList<Double>();
			List<String> EmotionDat = new ArrayList<String>();
			int len = wb.size();
			for (int i = 0; i < len; ++i){
				if (wb.get(i).getUser().equals(userName)){
					EmotionVal.add(wb.get(i).getCredit());
					EmotionDat.add(wb.get(i).getTime());
					//System.out.println(wb.get(i).getTime());
				}
			}
			
			len = EmotionDat.size();
		
			//System.out.println(isLatter("2016/3/16 22:54", "2016/3/16 9:50")); 
			for (int i = 0; i < len - 1; ++i){
				for (int j = i + 1; j < len; ++j){
//					System.out.println();
//					System.out.print(EmotionDat.get(i) + " ");
//					System.out.print(EmotionDat.get(j) + " __");
//					System.out.print(isLatter(EmotionDat.get(i), EmotionDat.get(j)) + "__ ");
					if (isLatter(EmotionDat.get(i), EmotionDat.get(j))){
						String Datetmp = EmotionDat.get(i);
						EmotionDat.set(i, EmotionDat.get(j));
						EmotionDat.set(j, Datetmp);
						double Valtmp = EmotionVal.get(i);
						EmotionVal.set(i, EmotionVal.get(j));
						EmotionVal.set(j, Valtmp);
					}
//					System.out.print("     " + EmotionDat.get(i) + " ");
//					System.out.print(EmotionDat.get(j) + " ");
//					System.out.println();
				}
			}
			
//			for (int i = 0; i < len - 1; ++i){
//				System.out.println(EmotionDat.get(i));
//			}
			
			JSONObject json = new JSONObject();
			json.put("Dat", EmotionDat);
			json.put("Val", EmotionVal);
			
			//System.out.println(json.get("1"));		
		//	JSONArray jsonArray = JSONArray.fromObject(EmotionVal);
		//	System.out.println(jsonArray.toString());
			result = json.toString();
			return SUCCESS; 	     
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} 		
	}

}
