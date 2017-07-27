package com.eproe.shepherd.wls.mbean.jdbc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.eproe.shepherd.wls.connector.WeblogicConnector;
import com.eproe.shepherd.wls.mbeans.domain.DomainConfigurationMbean;
import com.eproe.shepherd.wls.utils.MBeanProperties;
import com.eproe.shepherd.wls_health_check.tp_life.collector.Collector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class jdbcConfigurationMbeans {
	WeblogicConnector connector;
	JSONObject allJDBCJson=null;
	public jdbcConfigurationMbeans(WeblogicConnector connector){
		this.connector=connector;
	}
	//按照jdbc mbean获取单个jdbc的配置信息
	public JSONObject getJdbcConfiguration(ObjectName jdbcObj){
		JSONObject jdbcjson=new JSONObject();
		ArrayList<String> targetList=new ArrayList<String>();
		try {
			ObjectName[] targetsMbean=(ObjectName[]) connector.getConnection().getAttribute(jdbcObj, "Targets");
			//获取target
			for(int i=0;i<targetsMbean.length;i++){
				targetList.add((String) connector.getConnection().getAttribute(targetsMbean[i], "Name"));
			}
			ObjectName resourceMbean=(ObjectName) connector.getConnection().getAttribute(jdbcObj, "Resource");
			ObjectName connectionPoolMbean=(ObjectName) connector.getConnection().getAttribute(resourceMbean, "JDBCConnectionPoolParams");
			ObjectName dataSourceParamsMbean=(ObjectName) connector.getConnection().getAttribute(resourceMbean, "JDBCDataSourceParams");
			ObjectName driverParamsMbean=(ObjectName) connector.getConnection().getAttribute(resourceMbean, "JDBCDriverParams");
//			ObjectName oracleParamsMbean=(ObjectName) WeblogicConnector.getConnection().getAttribute(resourceMbean, "JDBCOracleParams");
			ObjectName XAParamsMbean=(ObjectName) connector.getConnection().getAttribute(resourceMbean, "JDBCXAParams");
			
			JSONObject dsJson=new JSONObject();
			JSONObject poolParamJson=new JSONObject();
			JSONObject driverParamJson=new JSONObject();
			JSONObject xaParamJson=new JSONObject();
			if(MBeanProperties.cfgJDBCDS==null)
				return null;
			if(MBeanProperties.cfgJDBCDS != null){
				for(int i=0;i<MBeanProperties.cfgJDBCDS.size();i++){
					dsJson.put(MBeanProperties.cfgJDBCDS.get(i), connector.getConnection().getAttribute(jdbcObj, (String) MBeanProperties.cfgJDBCDS.get(i)));
				}
			}
			if(MBeanProperties.cfgJDBCPOOL != null){
				for(int i=0;i<MBeanProperties.cfgJDBCPOOL.size();i++){
					poolParamJson.put(MBeanProperties.cfgJDBCPOOL.get(i), connector.getConnection().getAttribute(connectionPoolMbean, (String) MBeanProperties.cfgJDBCPOOL.get(i)));
				}
			}
			if(MBeanProperties.cfgJDBCDRI != null){
				for(int i=0;i<MBeanProperties.cfgJDBCDRI.size();i++){
					driverParamJson.put(MBeanProperties.cfgJDBCDRI.get(i), connector.getConnection().getAttribute(driverParamsMbean, (String) MBeanProperties.cfgJDBCDRI.get(i)));
				}
			}
			if(MBeanProperties.cfgJDBCXA != null){
				for(int i=0;i<MBeanProperties.cfgJDBCXA.size();i++){
					xaParamJson.put(MBeanProperties.cfgJDBCXA.get(i), connector.getConnection().getAttribute(XAParamsMbean, (String) MBeanProperties.cfgJDBCXA.get(i)));
				}
			}
			dsJson.put("Targets", targetList);
			jdbcjson.put("Datasource", dsJson);
			jdbcjson.put("JDBCConnectionPoolParams", poolParamJson);
			jdbcjson.put("JDBCXAParams", xaParamJson);
			jdbcjson.put("JDBCDriverParams", driverParamJson);

		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			// TODO Auto-generated catch block
			Collector.Message.add(e.getMessage());
			e.printStackTrace();
		}
		
		return jdbcjson;
		
	}
	//获取所有jdbc配置信息
	public JSONObject getAllJdbcConfiguration(){
		JSONObject json=new JSONObject();
		//获取domain config mbean
		ObjectName domainMbean=new DomainConfigurationMbean(connector).service;
		try {
			ObjectName[] jdbcMbeans=(ObjectName[]) connector.getConnection().getAttribute(domainMbean, "JDBCSystemResources");
			for(int i=0;i<jdbcMbeans.length;i++){
				String jdbcName=((String)connector.getConnection().getAttribute(jdbcMbeans[i], "Name")).replace(".", "_");
				json.put(jdbcName, getJdbcConfiguration(jdbcMbeans[i]));
			}
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			// TODO Auto-generated catch block
			Collector.Message.add(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	//按照server名获取
	public JSONObject getAllJdbcConfiguration(String serverName){
		if(this.allJDBCJson==null)
			allJDBCJson=this.getAllJdbcConfiguration();
		JSONObject myobj=allJDBCJson;
		ArrayList<String> needToBeDeletedDsList=new ArrayList<String>();
		Iterator jsoniter=myobj.keys();
		while(jsoniter.hasNext()){
			String currds=(String) jsoniter.next();
			JSONObject dsJson=(JSONObject)myobj.get(currds);
			if(!((JSONArray)((JSONObject)dsJson.get("Datasource")).get("Targets")).contains(serverName)){
				needToBeDeletedDsList.add(currds);
			}
		}
		for(int i=0;i<needToBeDeletedDsList.size();i++){
			myobj.remove(needToBeDeletedDsList.get(i));
		}
		return myobj;
	}
	public ArrayList<ObjectName>getJdbcList(String serverName){
		ArrayList<ObjectName> jdbcList=new ArrayList<ObjectName>();
		ObjectName domainMbean=new DomainConfigurationMbean(connector).service;

			ObjectName[] jdbcMbeans;
			try {
				jdbcMbeans = (ObjectName[]) connector.getConnection().getAttribute(domainMbean, "JDBCSystemResources");
				for(int i=0;i<jdbcMbeans.length;i++){
					ObjectName[] targets=(ObjectName[]) connector.getConnection().getAttribute(jdbcMbeans[i], "Targets");
					for(int j=0;j<targets.length;j++){
						if(connector.getConnection().getAttribute(targets[j],"Name").equals(serverName))
							jdbcList.add(jdbcMbeans[i]);
					}
				}
			} catch (AttributeNotFoundException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				System.out.println("Attribute Not Found"+e.getMessage());
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				System.out.println("Jdbc Not Exist"+e.getMessage());
			} catch (MBeanException e) {
				Collector.Message.add(e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReflectionException e) {
				Collector.Message.add(e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				Collector.Message.add(e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return jdbcList;
	}
	public String getJdbcName(ObjectName jdbcObj){
		try {
			return (String) connector.getConnection().getAttribute(jdbcObj, "Name");
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			// TODO Auto-generated catch block
			Collector.Message.add(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
