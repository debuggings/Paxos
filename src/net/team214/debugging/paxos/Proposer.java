package net.team214.debugging.paxos;

import net.team214.debugging.paxos.packet.AcceptResponse;
import net.team214.debugging.paxos.packet.ClientRequest;
import net.team214.debugging.paxos.packet.PrepareResponse;

public class Proposer {
	private Node me;

	public Proposer(Node me) {
		this.me = me;
	}

	public void startPrepare(Sender sender, ClientRequest request) {
	}

	public void receivePrepareResponse(PrepareResponse response) {
		judgePrepareReady();
	}

	private void judgePrepareReady() {

	}

	private void sendAcceptRequest() {

	}

	public void receiveAcceptResponse(AcceptResponse response) {

		judgeAccepted();
	}

	private void judgeAccepted() {
	}
}
