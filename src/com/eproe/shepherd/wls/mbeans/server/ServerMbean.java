package com.eproe.shepherd.wls.mbeans.server;

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
import com.eproe.shepherd.wls.mbeans.domain.DomainConfigurationMbean;
import com.eproe.shepherd.wls.mbeans.domain.DomainRuntimeMbean;
import com.eproe.shepherd.wls.utils.MBeanProperties;
import com.eproe.shepherd.wls_health_check.tp_life.collector.Collector;

import net.sf.json.JSONObject;
import weblogic.management.NoAccessRuntimeException;

public class ServerMbean {
	public ObjectName runtimeservice;
	public ObjectName configrationservice;
	public ObjectName jvmruntimeservice;
	public ObjectName threadruntimeservice;
	public int serverStatus;
	//1 正常 2 server down 3 server不存在
	String serverName;
	String domainName;
	WeblogicConnector connector;
	public ServerMbean(WeblogicConnector connector,String domainName,String serverName){
		this.connector=connector;
		this.domainName=domainName;
		this.serverName=serverName;
		
		try {
			ObjectName dmRuntimeService=new ObjectName("com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");
			ObjectName dmConfigurationService=(ObjectName) connector.getConnection().getAttribute(new DomainRuntimeMbean(connector).service,"DomainConfiguration");
			
			ObjectName[] serverrtObjs=(ObjectName[]) connector.getConnection().getAttribute(dmRuntimeService, "ServerRuntimes");
			for(int i=0;i<serverrtObjs.length;i++){
				if(((String) connector.getConnection().getAttribute(serverrtObjs[i], "Name")).equals(this.serverName)){
					this.runtimeservice=serverrtObjs[i];
					break;
				}
			}
			ObjectName[] servercfgObjs=(ObjectName[]) connector.getConnection().getAttribute(dmConfigurationService, "Servers");
			
			for(int i=0;i<servercfgObjs.length;i++){
				if(((String) connector.getConnection().getAttribute(servercfgObjs[i], "Name")).equals(this.serverName)){
					this.configrationservice=servercfgObjs[i];
					break;
				}
			}
			if(configrationservice==null){
				this.serverStatus=3;
				return;
			}
			if(runtimeservice==null){
				this.serverStatus=2;
//				return;
			}
			this.serverStatus=1;
//			this.runtimeservice=new ObjectName("com.bea:Name="+serverName+",Location="+serverName+",Type=ServerRuntime");
//			this.configrationservice=new ObjectName("com.bea:Name="+serverName+",Location="+domainName+",Type=Server");
			this.jvmruntimeservice=new ObjectName("com.bea:ServerRuntime="+serverName+",Name="+serverName+",Location="+serverName+",Type=JVMRuntime");
			this.threadruntimeservice=new ObjectName("com.bea:ServerRuntime="+serverName+",Name=ThreadPoolRuntime,Location="+serverName+",Type=ThreadPoolRuntime");
		} catch (MalformedObjectNameException | AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException | IOException e) {
			// TODO Auto-generated catch block
			Collector.Message.add(e.getMessage());
			e.printStackTrace();
		}	
	}
//	public String getServerName(){
	public JSONObject getServerRuntimeData(){
		JSONObject json=new JSONObject();
		if(this.serverStatus==2){
			json.put("State", "SHUTDOWN");
			return json;
		}
		if(MBeanProperties.rtServerList==null){
			return null;
		}
		if(MBeanProperties.rtJVMList==null){
			return null;
		}
		if(MBeanProperties.rtThreadList==null){
			return null;
		}
		
		List serverRuntimecfg=MBeanProperties.rtServerList;
		List jvmRuntimecfg=MBeanProperties.rtJVMList;
		List threadRuntimecfg=MBeanProperties.rtThreadList;
		DomainConfigurationMbean dc=new DomainConfigurationMbean(connector);
		ArrayList<String> serverCFGList = null;
		try {
			serverCFGList = dc.getServersList();
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e1) {
			// TODO Auto-generated catch block
			Collector.Message.add(e1.getMessage());
			e1.printStackTrace();
		}
			try {
				for(int i=0;i<serverRuntimecfg.size();i++){
//					System.out.println(this.connector.connectionInfo+"-----"+this.serverName+"----"+serverRuntimecfg.get(i));
				try{
					json.put(
						serverRuntimecfg.get(i), connector.getConnection().getAttribute(
								runtimeservice, (String) serverRuntimecfg.get(i)
								)
						);
				}catch (NoAccessRuntimeException e2){
					Collector.Message.add(this.connector.connectionInfo+":"+serverRuntimecfg.get(i)+":"+e2.getMessage());
				}
				}
				
				for(int i=0;i<jvmRuntimecfg.size();i++){
//					System.out.println(this.connector.connectionInfo+"-----"+this.serverName+"----"+jvmRuntimecfg.get(i));
					json.put(
							jvmRuntimecfg.get(i), connector.getConnection().getAttribute(
									jvmruntimeservice, (String) jvmRuntimecfg.get(i)
									)
							);
					}
				
				for(int i=0;i<threadRuntimecfg.size();i++){
//					System.out.println(this.connector.connectionInfo+"-----"+this.serverName+"----"+threadRuntimecfg.get(i));
					json.put(
							threadRuntimecfg.get(i), connector.getConnection().getAttribute(
									threadruntimeservice, (String) threadRuntimecfg.get(i)
									)
							);
					}
//				wtcMbean wm=new wtcMbean(connector);
//				json.put("WtcRuntime", wm.getWTCRuntime(runtimeservice));
				
			} catch (AttributeNotFoundException e) {
				// TODO Auto-generated catch block
//				System.out.println(this.connector.connectionInfo+":"+"Attribute Not Found"+e.getMessage());
				Collector.Message.add(e.getMessage());
//				e.printStackTrace();
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				if(serverCFGList.contains(this.serverName)){
					json.put("Name", this.serverName);
					json.put("State", "SHUTDOWN");
//					e.printStackTrace();
					return json;
				}else{
					json.put("Name", this.serverName);
					json.put("State", "NOT FOUND");
					return json;
				}
				
//				e.printStackTrace();
			} catch (MBeanException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
//				System.out.println(this.connector.connectionInfo+":");
				e.printStackTrace();
			} catch (ReflectionException e) {
				// TODO Auto-generated catch block
				
				Collector.Message.add(e.getMessage());
//				System.out.println(this.connector.connectionInfo+":");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				System.out.println(this.connector.connectionInfo+":");
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			}
		
		return json;
	}
	public String getLogPath(){
		try {
			ObjectName logObj=(ObjectName) connector.getConnection().getAttribute(this.configrationservice, "Log");
			return (String) connector.getConnection().getAttribute(logObj, "FileName");
		} catch (AttributeNotFoundException e) {
			// TODO Auto-generated catch block
			Collector.Message.add(e.getMessage());
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
		return null;
	}
	public JSONObject getServerConfigurationData(){
		JSONObject json=new JSONObject();
		if(MBeanProperties.cfgServerList==null){
			return null;
		}
		List serverConfigurationcfg=MBeanProperties.cfgServerList;

		for(int i=0;i<serverConfigurationcfg.size();i++){
//			System.out.println(this.connector.connectionInfo+"-----"+this.serverName+"----"+serverConfigurationcfg.get(i));
			try {
				json.put(
						serverConfigurationcfg.get(i), connector.getConnection().getAttribute(
								configrationservice, (String) serverConfigurationcfg.get(i)
								)
						);
			} catch (AttributeNotFoundException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				System.err.println("Attribute Not Found"+e.getMessage());
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				System.err.println("Server "+this.connector.connectionInfo+" "+this.serverName+" Not Exist");
				return null;
			} catch (MBeanException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			} catch (ReflectionException e) {
				Collector.Message.add(e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			}
		}
		return json;
	}
//	public JSONO
	
}
