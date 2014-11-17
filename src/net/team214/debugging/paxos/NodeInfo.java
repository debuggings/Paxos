package net.team214.debugging.paxos;

import java.net.InetSocketAddress;

public class NodeInfo {
	private InetSocketAddress ip;

	public InetSocketAddress getIp() {
		return ip;
	}

	public NodeInfo(String ip) {
		String[] s = ip.split(":");
		this.ip = new InetSocketAddress(s[0], Integer.parseInt(s[1]));
	}

}
