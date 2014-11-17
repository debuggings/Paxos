package net.team214.debugging.paxos;

import net.team214.debugging.paxos.packet.Packet;

public interface PacketHandler {
	public void handle(Packet p);
}
