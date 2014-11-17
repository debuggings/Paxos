package net.team214.debugging.paxos.packet;

public class PrepareResponse {
	public static enum Type {
		AcceptWithProposal, AcceptWithoutProposal, Deny
	}
	
	private Type type;
	private AcceptRequest ar;
	private PrepareRequest pr;

}
