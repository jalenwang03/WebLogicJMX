package com.eproe.shepherd.wls.mbean.jdbc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.eproe.shepherd.wls.connector.WeblogicConnector;
import com.eproe.shepherd.wls.mbean.application.applicationConfigurationMbeans;
import com.eproe.shepherd.wls.mbeans.domain.DomainRuntimeMbean;
import com.eproe.shepherd.wls.utils.MBeanProperties;
import com.eproe.shepherd.wls_health_check.tp_life.collector.Collector;

import net.sf.json.JSONObject;

public class jdbcRuntimeMbeans {
	WeblogicConnector connector;
	public ArrayList<String> jdbcList=new ArrayList<String>();
	public jdbcRuntimeMbeans(WeblogicConnector connector){
		this.connector=connector;
	}
	public JSONObject getJdbcRuntime(ObjectName jdbcObj){
		JSONObject json=new JSONObject();
		return json;
	}
	public void initJDBCList(WeblogicConnector connector,String serverName){
		this.connector=connector;
		jdbcConfigurationMbeans jcm=new jdbcConfigurationMbeans(connector);
		ArrayList jdbcobjList=jcm.getJdbcList(serverName);
		for(int i=0;i<jdbcobjList.size();i++){
			try {
				String jdbcName=((String)connector.getConnection().getAttribute((ObjectName) jdbcobjList.get(i), "Name")).replace(".", "_");
				jdbcList.add(jdbcName);
			} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Collector.Message.add(e.getMessage());
			}
		}
	}
	public JSONObject getAllJdbcRuntime(){
		JSONObject json=new JSONObject();
		DomainRuntimeMbean dm=new DomainRuntimeMbean(connector);
		ObjectName[] ServerList=dm.getServerRTList();
		for(int i=0;i<ServerList.length;i++){
			
			try {
				String jdbcName=((String)connector.getConnection().getAttribute(ServerList[i], "Name")).replace(".", "_");
				json.put(
						jdbcName, 
						getAllJdbcRuntime(ServerList[i])
						);
			} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Collector.Message.add(e.getMessage());
			}
		}
		return json;
	}
	
	public JSONObject getAllJdbcRuntime(ObjectName serverObj){
		
		JSONObject json=new JSONObject();
		try {
			ObjectName jdbcMbeans=(ObjectName) connector.getConnection().getAttribute(serverObj, "JDBCServiceRuntime");
			ObjectName[] dsMbeans=(ObjectName[])connector.getConnection().getAttribute(jdbcMbeans, "JDBCDataSourceRuntimeMBeans");
			for(int i=0;i<dsMbeans.length;i++){
				String jdbcName=((String)connector.getConnection().getAttribute(dsMbeans[i], "Name")).replace(".", "_");
//				System.out.println(WeblogicConnector.getConnection().getAttribute(dsMbeans[i], "Name"));
				json.put(jdbcName, getJDBCRuntime(dsMbeans[i]));
			}
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Collector.Message.add(e.getMessage());
		}
		try {
			String jdbcName=((String)connector.getConnection().getAttribute(serverObj, "Name")).replace(".", "_");
			this.initJDBCList(connector,jdbcName);
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Collector.Message.add(e.getMessage());
		}
		for(int i=0;i<this.jdbcList.size();i++){
			if(!json.containsKey(jdbcList.get(i))){
				JSONObject j=new JSONObject();
				j.put("State", "SHUTDOWN");
				json.put(jdbcList.get(i), j);
			}
		}
		
		return json;
	}
	public JSONObject getJDBCRuntime(ObjectName jdbcObj){
		if(MBeanProperties.rtJDBCList==null){
			return null;
		}
		List jdbcrtcfgs=MBeanProperties.rtJDBCList;
		JSONObject dsjson=new JSONObject();
		for(int j=0;j<jdbcrtcfgs.size();j++){
			try {
				dsjson.put(
						jdbcrtcfgs.get(j), 
						connector.getConnection().getAttribute(jdbcObj, (String) jdbcrtcfgs.get(j))
						);
			} catch (javax.management.RuntimeMBeanException |AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				Collector.Message.add(e.getMessage());
				System.err.println("["+jdbcObj+":"+(String) jdbcrtcfgs.get(j)+"] Error occured:"+e.getMessage());
			}
		}
		return dsjson;
		
	}
	public JSONObject getAllJdbcRuntime(String serverName){
		DomainRuntimeMbean dm=new DomainRuntimeMbean(connector);
		ObjectName serverObj=dm.getServerRTObj(serverName);
		if(serverObj==null){
			JSONObject json=new JSONObject();
			jdbcConfigurationMbeans jdmb=new jdbcConfigurationMbeans(connector);
			List<ObjectName> jdbcList=jdmb.getJdbcList(serverName);
			for(int i=0;i<jdbcList.size();i++){
//				System.out.println(this.connector.connectionInfo+"-----"+serverName+"----"+jdbcList.get(i));
				JSONObject j=new JSONObject();
				j.put("State", "SHUTDOWN");
				try {
					String jdbcName=((String)connector.getConnection().getAttribute(jdbcList.get(i), "Name")).replace(".", "_");
					json.put(jdbcName, j);
				} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
						| IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Collector.Message.add(e.getMessage());
				}
			}
			return json;
		}else
			return this.getAllJdbcRuntime(serverObj);
		}
	public ArrayList<ObjectName> getJdbcList(String serverName){
		ArrayList<ObjectName> jdbcList=new ArrayList<ObjectName>();
		DomainRuntimeMbean dm=new DomainRuntimeMbean(connector);
		ObjectName serverObj=dm.getServerRTObj(serverName);
		ObjectName jdbcMbeans;
		try {
			jdbcMbeans = (ObjectName) connector.getConnection().getAttribute(serverObj, "JDBCServiceRuntime");
			ObjectName[] dsMbeans=(ObjectName[])connector.getConnection().getAttribute(jdbcMbeans, "JDBCDataSourceRuntimeMBeans");
			for(int i=0;i<dsMbeans.length;i++){
				jdbcList.add(dsMbeans[i]);
			}
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			// TODO Auto-generated catch block
			Collector.Message.add(e.getMessage());
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
