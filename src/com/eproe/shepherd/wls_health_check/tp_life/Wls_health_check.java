package com.eproe.shepherd.wls_health_check.tp_life;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.eproe.shepherd.wls_health_check.tp_life.CI.CI;
import com.eproe.shepherd.wls_health_check.tp_life.CI.Domain;
import com.eproe.shepherd.wls_health_check.tp_life.collector.Collector;
import com.eproe.shepherd.wls_health_check.tp_life.communication.ShepherdConnector;
import com.eproe.shepherd.wls_health_check.tplife.utils.ShepherdProperties;

import net.sf.json.JSONObject;

//主程序
public class Wls_health_check {
	public static int submit_CNT=0;
	public static int finish_CNT=0;
	public static int total_CNT=CI.domainMap.size();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("初始化shepherd连接");
		if(ShepherdConnector.connection()==0){
			System.err.println("shepherd服务端连接失败");
			System.exit(-1);
		}
		System.out.println("初始化CI信息");
		CI.initDomains();
		CI.initServers();
		System.out.println("初始化完成");
		
		ExecutorService fixedThreadPool= Executors.newFixedThreadPool(ShepherdProperties.MaxActive);
		for(String domID:CI.domainMap.keySet()){
			Wls_health_check.submit_CNT++;
			Domain dom=CI.domainMap.get(domID);
			Runnable runner = new Collector(dom,Wls_health_check.submit_CNT+"/"+CI.domainMap.size()+"\t"); 
			fixedThreadPool.execute(runner);
		}
		fixedThreadPool.shutdown();
		while(true){
			if(fixedThreadPool.isTerminated()){
				System.exit(0);
			}
			try {
				System.out.println("总数："+Wls_health_check.total_CNT+"提交总数："+Wls_health_check.submit_CNT+"完成总数："+Wls_health_check.finish_CNT);
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

}

