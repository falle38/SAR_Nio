package nio.engineMulticast;

import nio.engineImpl.NioEngineImpl;

public class Client implements Runnable {
	
	NioEngineImpl nioEngine;

	public int id;
	public int clock;
	public int port;
	public int timestamp;
	
	public boolean active;
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

}
