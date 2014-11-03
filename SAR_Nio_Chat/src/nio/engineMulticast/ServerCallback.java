package nio.engineMulticast;

import java.nio.ByteBuffer;

import nio.engine.AcceptCallback;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

public class ServerCallback implements AcceptCallback, DeliverCallback{

	@Override
	public void accepted(NioServer server, NioChannel channel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closed(NioChannel channel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliver(NioChannel channel, ByteBuffer bytes) {
		// TODO Auto-generated method stub
		
	}

}
