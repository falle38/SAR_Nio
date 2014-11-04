package nio.engineMulticast;

public enum MessageType {
	MSG, ACK, JOIN, NEWMEMBER;
	
	
	public int getMessageTypeValue() {
		return this.ordinal();
	}
}

