package com.vmware.vim25;

import java.util.List;

public class EnvironmentBrowserConfigOptionQuerySpec {

	public String key;
	public ManagedObjectReference host;
	public String[] guestId;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public ManagedObjectReference getHost() {
		return host;
	}
	public void setHost(ManagedObjectReference host) {
		this.host = host;
	}
	public String[] getGuestId() {
		return guestId;
	}
	public void setGuestId(String[] guestId) {
		this.guestId = guestId;
	}
	
	
}
