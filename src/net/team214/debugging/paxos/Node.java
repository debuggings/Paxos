package net.team214.debugging.paxos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.team214.debugging.paxos.packet.AcceptRequest;
import net.team214.debugging.paxos.packet.AcceptResponse;
import net.team214.debugging.paxos.packet.ClientRequest;
import net.team214.debugging.paxos.packet.Packet;
import net.team214.debugging.paxos.packet.PrepareRequest;
import net.team214.debugging.paxos.packet.PrepareResponse;

public class Node {

	private Proposer proposer;
	private Accepter accepter;
	private List<NodeInfo> fellows;
	private NodeInfo me;
	private Sender sender;
	private Receiver receiver;

	public static void main(String[] args) {
		Map<String, String> config = null;
		if (args.length == 1) {
			config = readConfig();
		} else {
			starter();
			return;
		}
		if (config != null) {
			new Node(config).start();
		} else {
			System.out.println("argument error");
		}
	}

	private static void starter() {
		Map<String, String> config = readConfig();
		int count = Integer.parseInt(config.get("count"));
		for (int i = 0; i < count; i++) {
			config.put("index", "" + i);
			new Node(config).start();
		}
	}

	private static Map<String, String> readConfig() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("1", "127.0.0.1:1234");
		map.put("2", "127.0.0.1:1235");
		map.put("3", "127.0.0.1:1236");
		map.put("4", "127.0.0.1:1237");
		map.put("5", "127.0.0.1:1238");
		map.put("index", "");
		map.put("count", "" + map.size());
		return map;
	}

	private Node(Map<String, String> config) {
		String index = config.get("index");
		this.me = new NodeInfo(config.get(index));
		this.proposer = new Proposer(this);
		this.accepter = new Accepter(this);
		this.sender = new Sender(me);
		this.receiver = new Receiver(me, new PacketHandlerImp());
		this.fellows = new ArrayList<NodeInfo>();

		int count = Integer.parseInt(config.get("count"));
		for (int i = 0; i < count; i++) {
			if ("" + i == index)
				continue;
			String s = config.get("" + i);
			fellows.add(new NodeInfo(s));
		}
		start();
	}

	public void start() {
		new Thread(this.receiver).start();
	}

	public void stop() {
		this.receiver.turnOff();
	}

	private synchronized void receiveMsg(Packet p) {
		switch (p.getType()) {
		case A:
			accepter.receiveAccept((AcceptRequest) p);
			break;
		case AR:
			proposer.receiveAcceptResponse((AcceptResponse) p);
			break;
		case P:
			accepter.receivePrepare((PrepareRequest) p);
			break;
		case PR:
			proposer.receivePrepareResponse((PrepareResponse) p);
			break;
		case CR:
			proposer.startPrepare(sender,(ClientRequest)p);
			break;
		}
	}

	public class PacketHandlerImp implements PacketHandler {
		public void handle(Packet p) {
			receiveMsg(p);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		this.stop();
	}

}
