package multicast;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import nio.engine.AcceptCallback;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;


public class MulticastServer implements AcceptCallback, DeliverCallback, Runnable{
	
	
	ArrayList<NioChannel> listChannel;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliver(NioChannel channel, ByteBuffer bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accepted(NioServer server, NioChannel channel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closed(NioChannel channel) {
		// TODO Auto-generated method stub
		
	}

}
