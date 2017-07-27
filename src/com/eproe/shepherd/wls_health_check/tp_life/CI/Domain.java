package com.eproe.shepherd.wls_health_check.tp_life.CI;

import java.util.ArrayList;

public class Domain {
	public String host;
	public int port;
	public String username;
	public String password;
	public ArrayList<Server> servers;
	public Domain(String host,int port,String username,String password){
		this.host=host;
		this.port=port;
		this.username=username;
		this.password=password;
		this.servers=new ArrayList<Server>();
	}
	public void addServer(String serverName){
		this.servers.add(new Server(serverName));
	}
	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public ArrayList<Server> getServers() {
		return servers;
	}
	
}
