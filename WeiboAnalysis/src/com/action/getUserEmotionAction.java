package com.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

public class getUserEmotionAction  extends ActionSupport {	

	private String userName;
	private String result;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String execute() throws Exception{	
		try{
			System.out.println(userName);
			List<Integer> EmotionVal = new ArrayList<Integer>();
			EmotionVal.add(12);
			EmotionVal.add(24);
			EmotionVal.add(29);
			JSONArray jsonArray = JSONArray.fromObject(EmotionVal);
			System.out.println(jsonArray.toString());
			result = jsonArray.toString();
			return SUCCESS; 	     
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} 		
	}

}
