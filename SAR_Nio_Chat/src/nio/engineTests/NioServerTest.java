package nio.engineTests;

import java.io.IOException;
import java.nio.ByteBuffer;

import nio.engine.AcceptCallback;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioEngine;
import nio.engine.NioServer;
import nio.engineImpl.NioEngineImpl;

public class NioServerTest implements Runnable,AcceptCallback,DeliverCallback {

	int portConnection;
	int numACK;

	public NioServerTest (int portConnection) {
		this.portConnection = portConnection;
		numACK=1;
	}

	
	public void run() {
		System.out.println("Server connecté sur le port : "+portConnection);
		NioEngine engine = null;
		try {
			engine = new NioEngineImpl();
		} catch (Exception e) {
			NioEngine.panic("Problem in Engine");
		}

		try {
			engine.listen(portConnection,this);

		} catch (IOException e) {
			NioEngine.panic("Problem during connecting the Engine");
		}

		engine.mainloop();

	}	
	
	@Override
	
	public void closed(NioChannel channel) {
				
		System.out.println("server has closed the connexion");

	}

	@Override
	public void accepted(NioServer server, NioChannel channel) {
		System.out.println("Server has accepted the connexion");
		channel.setDeliverCallback(this);
	}


	@Override
	public void deliver(NioChannel channel, ByteBuffer bytes) {
		String msg_recu=new String(bytes.array());
		if(msg_recu.equals("SERVERREQUESTCLOSE")){
			channel.close();
		}
		else{
			System.out.println("Server send ACK: "+ numACK);
			String ack = "ACK "+numACK;
			numACK++;
			if(numACK==100){
				channel.send("SERVERREQUESTCLOSE".getBytes(),0,"SERVERREQUESTCLOSE".getBytes().length);
			}
			else{
				channel.send(ack.getBytes(),0,ack.getBytes().length);
			}

		}
	}




}
