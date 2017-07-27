package com.eproe.shepherd.wls_health_check.tp_life.CI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eproe.shepherd.wls_health_check.tp_life.communication.Getter;
import com.eproe.shepherd.wls_health_check.tplife.utils.ShepherdProperties;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class CI {
	public static HashMap<String,Domain> domainMap=new HashMap<String,Domain>();
	public static void addDomain(String domainName,String host,int port,String username,String password){
		CI.domainMap.put(domainName,new Domain(host,port,username,password));
	}
	public static Domain getDomain(String domainID){
		return CI.domainMap.get(domainID);
	}
	public static boolean containDomain(String domainID){
		return CI.domainMap.containsKey(domainID);
	}
	public static void initDomains(){
		String domStr=Getter.getData(ShepherdProperties.DomainType);
		if (domStr==null){
			return;
		}
		JSONArray myJsonArray = null;
		try{
			myJsonArray=JSONArray.fromObject(domStr);
		}catch (Exception e){
			System.err.println("Domain JSON数据解析错误，请检查数据:"+domStr+"   "+e.getMessage());
			System.exit(-1);
		}
		for(int i=0;i<myJsonArray.size();i++){
			JSONObject obj=JSONObject.fromObject(myJsonArray.get(i));
			if(!CI.isIP(obj.getString("adminAddress"))){
				System.err.println("输入IP格式错误："+obj.getString("adminAddress"));
				break;
			}
			if(obj.containsKey("name") && obj.containsKey("adminAddress") && obj.containsKey("adminPort") && obj.containsKey("adminUser") && obj.containsKey("adminPassword"))
				CI.addDomain(
						obj.getString("name"),
						obj.getString("adminAddress"),
						Integer.valueOf(obj.getString("adminPort")),
						obj.getString("adminUser"),
						obj.getString("adminPassword"));
		}
		System.out.println(CI.domainMap);
	}
	public static void initServers(){
		String srvStr=Getter.getData( ShepherdProperties.ServerType);
		if (srvStr==null){
			return;
		}
		JSONArray myJsonArray = null;
		try{
			myJsonArray=JSONArray.fromObject(srvStr);
		}catch (Exception e){
			System.err.println("Server JSON数据解析错误，请检查数据:"+srvStr+"   "+e.getMessage());
		}
		for(int i=0;i<myJsonArray.size();i++){
			JSONObject obj=JSONObject.fromObject(myJsonArray.get(i));
			if(obj.containsKey("domainName")){
				String domainName=obj.getString("domainName");
				if(CI.containDomain(domainName))
					CI.getDomain(domainName).addServer(obj.getString("iname"));
				else
					System.err.println(domainName+" not found");
			}
		}
	}
	public static void PrintCI() {
		// TODO Auto-generated method stub
		String str = "";
		for(String domID:CI.domainMap.keySet()){
			Domain dom=CI.domainMap.get(domID);
			System.out.println(domID+"\t"+dom.host+":"+dom.port+dom.username+"/"+dom.password);
			ArrayList<Server> serverList=dom.getServers();
			for(int i=0;i<serverList.size();i++){
				System.out.println("----"+serverList.get(i).serverName);
			}
		}
	}
	 public static boolean isIP(String addr)  
     {  
         if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))  
         {  
             return false;  
         }  
         /** 
          * 判断IP格式和范围 
          */  
         String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";  

         Pattern pat = Pattern.compile(rexp);    

         Matcher mat = pat.matcher(addr);    

         boolean ipAddress = mat.find();  

         return ipAddress;  
     }  
	
}

