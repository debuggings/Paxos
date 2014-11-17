package net.team214.debugging.paxos;

import net.team214.debugging.paxos.packet.AcceptRequest;
import net.team214.debugging.paxos.packet.PrepareRequest;

public class Accepter {
	private Node me;

	public Accepter(Node me) {
		this.me = me;
	}

	public void receivePrepare(PrepareRequest prepare) {

	}

	public void receiveAccept(AcceptRequest accept) {

	}

}
