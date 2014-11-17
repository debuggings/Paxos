package net.team214.debugging.paxos.packet;

public interface Packet {
	public static enum Type {
		A("AS"), AR("AR"), P("PS"), PR("PR"), CR("CR");
		private String type;

		private Type(String type) {
			this.type = type;
		}

		public String GetName() {
			return type;
		}
	}

	Type getType();
}
