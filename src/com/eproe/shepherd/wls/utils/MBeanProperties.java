package com.eproe.shepherd.wls.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

//用于解析配置文件
//配置文件采用yaml格式

public class MBeanProperties {
	private String Server;
	private String Thread;
	private String JVM;
	private String Application;
	private String JDBC;
	private String WTC;
	public static List rtDomainList=null;
	public static List rtServerList=null;
	public static List rtJVMList=null;
	public static List rtThreadList=null;
	public static List rtApplicationList=null;
	public static List rtJDBCList=null;
	public static List rtWTCList=null;
	public static List cfgDomainList=null;
	public static List cfgServerList=null;
	public static List cfgServerLogList=null;
	public static List cfgJDBCDS=null;
	public static List cfgJDBCPOOL=null;
	public static List cfgJDBCDRI=null;
	public static List cfgJDBCXA=null;
	public static List cfgApplicationList=null;
	public static Map cfgWTCList=null;
	public static List cfgwtc_local=null;
	public static List cfgwtc_remote=null;
	public static List cfgwtc_export=null;
	public static List cfgwtc_import=null;
	
//		
//	public static void main(String args[]){
//		MBeanProperties.init();
//	}
	static{
		MBeanProperties.init();
	}
	public static void init(){
		try {
			new MBeanProperties();
		} catch (FileNotFoundException | YamlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//	public static MBeanProperties instance=new MBeanProperties();
	public MBeanProperties() throws FileNotFoundException, YamlException{
		YamlReader reader = new YamlReader(new FileReader("cfg/Mbean.properties"));
		Object object = reader.read();
//		System.out.println(object);
		Map map = (Map)object;
		Map cfgMap = null;
		Map rtMap=null;
//		System.out.println(map.get("Config"));
		if(map.containsKey("Config")){
			cfgMap=(Map)(map.get("Config"));
			
			/**CONFIG 配置**/
			
			if(cfgMap.containsKey("Domain"))
				cfgDomainList=(List)cfgMap.get("Domain");
			if(((Map)cfgMap.get("Server")).containsKey("Server"))
				cfgServerList=(List)((Map)cfgMap.get("Server")).get("Server");
			if(((Map)cfgMap.get("Server")).containsKey("Log"))
				cfgServerLogList=(List)((Map)cfgMap.get("Server")).get("Log");
			if(((Map)cfgMap.get("JDBC")).containsKey("DataSource"))
				cfgJDBCDS=(List)((Map)cfgMap.get("JDBC")).get("DataSource");
			if(((Map)cfgMap.get("JDBC")).containsKey("Pool_Param"))
				cfgJDBCPOOL=(List)((Map)cfgMap.get("JDBC")).get("Pool_Param");
			if(((Map)cfgMap.get("JDBC")).containsKey("Driver_Param"))
				cfgJDBCDRI=(List)((Map)cfgMap.get("JDBC")).get("Driver_Param");
			if(((Map)cfgMap.get("JDBC")).containsKey("XA_Param"))
				cfgJDBCXA=(List)((Map)cfgMap.get("JDBC")).get("XA_Param");
			if(cfgMap.containsKey("Application"))
				cfgApplicationList=(List)cfgMap.get("Application");
			if(cfgMap.containsKey("WTC_DM_Local"))
				cfgwtc_local=(List) cfgMap.get("WTC_DM_Local");
	//			System.out.println(cfgMap);
			if(cfgMap.containsKey("WTC_DM_Remote"))
				cfgwtc_remote=(List) cfgMap.get("WTC_DM_Remote");
			if(cfgMap.containsKey("WTC_DM_Export"))
				cfgwtc_export=(List) cfgMap.get("WTC_DM_Export");
			if(cfgMap.containsKey("WTC_DM_Import"))
				cfgwtc_import=(List) cfgMap.get("WTC_DM_Import");
		}
		if(map.containsKey("Runtime")){
			rtMap=(Map)(map.get("Runtime"));
			/**RUNTIME 配置**/
			if(rtMap.containsKey("Server"))
				rtServerList=(List)rtMap.get("Server");
			if(rtMap.containsKey("JVM"))
				rtJVMList=(List)rtMap.get("JVM");
			if(rtMap.containsKey("Thread"))
				rtThreadList=(List)rtMap.get("Thread");
			if(rtMap.containsKey("Application"))
				rtApplicationList=(List)rtMap.get("Application");
			if(rtMap.containsKey("JDBC"))
				rtJDBCList=(List)rtMap.get("JDBC");
//			if(rtMap.containsKey("Server"))
//				rtWTCList=(List)rtMap.get("Server");
			if(rtMap.containsKey("Domain"))
				rtDomainList=(List)rtMap.get("Domain");
		}
		
		
		
	}
}
