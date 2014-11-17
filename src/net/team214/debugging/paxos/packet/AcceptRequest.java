package net.team214.debugging.paxos.packet;

public class AcceptRequest implements Packet {
	private Type type;
	private String msg;

	public AcceptRequest(String str) {
		this.type = Type.A;
		this.msg = str;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return type.GetName() + ":" + msg;
	}
}
