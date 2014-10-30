package nio.engineTests;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import nio.engine.ConnectCallback;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioEngine;
import nio.engineImpl.NioEngineImpl;

public class NioClientTest implements Runnable, ConnectCallback, DeliverCallback {

	//je fais un test
	NioChannel clientChannel = null;
	String addressConnection;
	int portConnection;
	private int numSeq;

	public NioClientTest(String addressConnection, int portConnection) {
		super();
		this.addressConnection = addressConnection;
		this.portConnection = portConnection;
		this.numSeq=0;
	}

	@Override
	public void run() {
		NioChannel channel = null;
		NioEngine engine = null;
		try {
			engine = new NioEngineImpl();
		} catch (Exception e) {
			NioEngine.panic("Problem in Engine");
		}

			try {
				engine.connect(InetAddress.getByName(addressConnection), portConnection, this);
			} catch (IOException e) {
				NioEngine.panic("Problem during connecting the Engine");
			}

			engine.mainloop();

	}

	@Override
	
	public void closed(NioChannel channel) {		
		System.out.println("host has closed the connexion");

	}

	@Override
	public void connected(NioChannel channel) {
		clientChannel = channel;
		clientChannel.setDeliverCallback(this);
		String message = "Packet "+numSeq+" delivered";
		numSeq++;
		channel.send(message.getBytes(),0,message.getBytes().length);

	}

	public void deliver(NioChannel channel, ByteBuffer bytes) {

		String msg_send=new String(bytes.array());
		if(msg_send.equals("SERVERREQUESTCLOSE")){
			channel.close();	
		}
		else{
			System.out.println("Client send : "+ msg_send);
			String message = "Packet "+numSeq+" delivered";
			numSeq++;
			channel.send(message.getBytes(),0,message.getBytes().length);
		}
	}

}
