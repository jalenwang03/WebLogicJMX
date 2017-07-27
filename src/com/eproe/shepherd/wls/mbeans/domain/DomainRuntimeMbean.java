package com.eproe.shepherd.wls.mbeans.domain;


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
import com.eproe.shepherd.wls.utils.MBeanProperties;
import com.eproe.shepherd.wls_health_check.tp_life.collector.Collector;

import net.sf.json.JSONObject;

public class DomainRuntimeMbean {
	public  ObjectName service;
	// Initializing the object name for DomainRuntimeServiceMBean
	   // so it can be used throughout the class.
	WeblogicConnector connector;
	public DomainRuntimeMbean(WeblogicConnector connector){
		this.connector=connector;
		 try {
	         service = new ObjectName(
	            "com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");
	      }catch (MalformedObjectNameException e) {
	    	  Collector.Message.add(e.getMessage());
	         throw new AssertionError(e.getMessage());
	      }
	}
	   public JSONObject getDomainRuntime(){
		   JSONObject resultjson = new JSONObject();
//		   System.out.println(MBeanProperties.rtDomainList);
		   if(MBeanProperties.rtDomainList==null){
			   return null;
		   }
		   List domainRuntimeList = MBeanProperties.rtDomainList;
//		   System.out.println(domainConfigcfgList);
		   for(int i=0;i<domainRuntimeList.size();i++){
			   try {
				resultjson.put(domainRuntimeList.get(i), connector.getConnection().getAttribute(service, (String) domainRuntimeList.get(i)));
			} catch (AttributeNotFoundException e) {
				Collector.Message.add(e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			} catch (MBeanException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			} catch (ReflectionException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			}
		   }
		   return resultjson;
	   }
	   public ObjectName[] getServerRTList(){
			ObjectName[] servers = null;
			try {
				servers=(ObjectName[]) connector.getConnection().getAttribute(service, "ServerRuntimes");
			} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			}
			return servers;	
		}
	   public  ObjectName getServerRTObj(String serverName){
		   ObjectName[] servers=this.getServerRTList();
		   for(int i=0;i<servers.length;i++){
			   try {
				if(connector.getConnection().getAttribute(servers[i],"Name").equals(serverName))
					   return servers[i];
			} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			}
		   }
		return null;
	   }
}
