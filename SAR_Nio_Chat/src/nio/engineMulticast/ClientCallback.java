package nio.engineMulticast;

import java.nio.ByteBuffer;

import nio.engine.AcceptCallback;
import nio.engine.ConnectCallback;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

public class ClientCallback implements ConnectCallback, AcceptCallback, DeliverCallback{

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
		channel.close();
	}

	@Override
	public void connected(NioChannel channel) {
		// TODO Auto-generated method stub
		
	}

}
