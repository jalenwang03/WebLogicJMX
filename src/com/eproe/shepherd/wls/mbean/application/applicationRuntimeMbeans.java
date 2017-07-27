package com.eproe.shepherd.wls.mbean.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.eproe.shepherd.wls.connector.WeblogicConnector;
import com.eproe.shepherd.wls.mbeans.domain.DomainRuntimeMbean;
import com.eproe.shepherd.wls.utils.MBeanProperties;
import com.eproe.shepherd.wls_health_check.tp_life.collector.Collector;

import net.sf.json.JSONObject;

public class applicationRuntimeMbeans {
	WeblogicConnector connector;
	public applicationRuntimeMbeans(WeblogicConnector connector){
		this.connector=connector;
	}
	public JSONObject getAllApplicationRuntimes(String serverName){
		
		DomainRuntimeMbean dm=new DomainRuntimeMbean(connector);
		ObjectName serverObj=dm.getServerRTObj(serverName);
		if(serverObj==null){
			JSONObject json=new JSONObject();
			applicationConfigurationMbeans apcfm=new applicationConfigurationMbeans(connector);
			List<ObjectName> appList=apcfm.getappList(serverName);
			for(int i=0;i<appList.size();i++){
//				System.out.println(this.connector.connectionInfo+"-----"+serverName+"----"+appList.get(i));
				JSONObject j=new JSONObject();
				j.put("Status", "SHUTDOWN");
				try {
					json.put(connector.getConnection().getAttribute(appList.get(i),"Name"), j);
				} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
						| IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Collector.Message.add(e.getMessage());
				}
			}
			return json;
		}else
			return getAllApplicationRuntimes(serverObj);
	}
	public JSONObject getAllApplicationRuntimes(ObjectName serverObj){
		JSONObject json=new JSONObject();
		ArrayList<ObjectName> appObjList=this.getappList(serverObj);
		for(int i=0;i<appObjList.size();i++){
			try {
				if(appObjList.get(i)==null)
					break;
				json.put(((String)connector.getConnection().getAttribute(appObjList.get(i), "Name")).replace(".", "_"), this.getApplicationRuntime(appObjList.get(i)));
			} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Collector.Message.add(e.getMessage());
			}
		}
		return json;
		
	}
	public JSONObject getAllApplicationRuntimes(){
		DomainRuntimeMbean dm=new DomainRuntimeMbean(connector);
		ObjectName[] servers=dm.getServerRTList();
		JSONObject json=new JSONObject();
		for(int i=0;i<servers.length;i++){
			try {
				json.put(((String)connector.getConnection().getAttribute(servers[i], "Name")).replace(".", "_"),getAllApplicationRuntimes(servers[i]));
			} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Collector.Message.add(e.getMessage());
			}
		}
		return json;
	}
	public JSONObject getApplicationRuntime(ObjectName applicationObj){
		JSONObject json=new JSONObject();
		if(MBeanProperties.rtApplicationList==null){
			return null;
		}
		for(int j=0;j<MBeanProperties.rtApplicationList.size();j++){
			try {
				
				json.put(MBeanProperties.rtApplicationList.get(j), connector.getConnection().getAttribute(applicationObj, (String) MBeanProperties.rtApplicationList.get(j)));
			} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Collector.Message.add(e.getMessage());
			}
		}
		return json;
	}
	public JSONObject getApplicationRuntime(String serverName,String applicationName){
		JSONObject json=new JSONObject();
		ObjectName appObj=null;
//		try {
//			appObj=new ObjectName("com.bea:ServerRuntime="+serverName+",Name="+serverName+"_/"+applicationName+",Location="+serverName+",Type=WebAppComponentRuntime,ApplicationRuntime="+applicationName+"");
//		} catch (MalformedObjectNameException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		DomainRuntimeMbean dm=new DomainRuntimeMbean(connector);
		ObjectName serverObj=dm.getServerRTObj(serverName);
		ArrayList appList=this.getappList(serverObj);
		if(appList.contains(applicationName))
			return this.getApplicationRuntime(appObj);
		else
			return null;
		
	}
	public ArrayList getappList(ObjectName serverObj){
//		ArrayList<String> l=new ArrayList<String>();
		ArrayList<ObjectName> appObjList=new ArrayList<ObjectName>();
		ObjectName[] allApplicationRuntimeMBeans;
		try {
			if(serverObj==null){
				return null;
			}
			allApplicationRuntimeMBeans = (ObjectName[])connector.getConnection().getAttribute(serverObj,"ApplicationRuntimes");
			String appName=null;
	        for(int i=0;i<allApplicationRuntimeMBeans.length;i++){
	        	appName=(String)connector.getConnection().getAttribute(allApplicationRuntimeMBeans[i], "Name");
	        	 if ("consoleapp".equals(appName))
	                 continue;
	             if ("bea_wls_deployment_internal".equals(appName))
	                 continue;
	             if ("wls-wsat".equals(appName))
	                 continue;
	             if ("uddiexplorer".equals(appName))
	                 continue;
	             if ("bea_wls_diagnostics".equals(appName))
	                 continue;
	             if ("bea_wls_internal".equals(appName))
	                 continue;
	             if ("bea_wls9_async_response".equals(appName))
	                 continue;
	             if ("bea_wls_management_internal2".equals(appName))
	                 continue;
	             if ("uddi".equals(appName))
	                 continue;
	             if ("wls-management-services".equals(appName))
	            	 continue;
	             ObjectName[] componentRuntimes =  (ObjectName[]) connector.getConnection().getAttribute(allApplicationRuntimeMBeans[i], "ComponentRuntimes");
	             for (int compNumber=0; compNumber < componentRuntimes.length; compNumber++){
	            	 if(((String)connector.getConnection().getAttribute(componentRuntimes[compNumber], "Type")).equals("WebAppComponentRuntime")){
	            		 appObjList.add(componentRuntimes[compNumber]);
	            	 }
	             }
	        }
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Collector.Message.add(e.getMessage());
		}
		return appObjList;
	}
	
	public ArrayList getappList(String serverName){
		DomainRuntimeMbean dm=new DomainRuntimeMbean(connector);
		ObjectName serverObj=dm.getServerRTObj(serverName);
		return this.getappList(serverObj);
	}
	public String getappName(ObjectName appObj){
		try {
			return (String) connector.getConnection().getAttribute(appObj, "Name");
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Collector.Message.add(e.getMessage());
		}
		return null;
	}
}
