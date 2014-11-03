package nio.engineMulticast;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;

import nio.engine.AcceptCallback;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioEngine;
import nio.engine.NioServer;
import nio.engineImpl.NioEngineImpl;

public class Server implements AcceptCallback, DeliverCallback, Runnable{

	NioEngineImpl nioEngine;
	ArrayList<NioChannel> channels;
	Hashtable<NioChannel, Integer > listPorts;

	//le nombre de connexions
	int nbMaxClients;
	int portServer;

	public Server (int port){
		this.nbMaxClients = 0;
		this.portServer = port;
		this.channels = new ArrayList<NioChannel>();
		this.listPorts = new Hashtable<NioChannel, Integer>();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Server connecté sur le port : "+portServer);
		NioEngine engine = null;
		try {
			engine = new NioEngineImpl();
		} catch (Exception e) {
			NioEngine.panic("Problem in Engine");
		}

		try {
			engine.listen(portServer,this);

		} catch (IOException e) {
			NioEngine.panic("Problem during connecting the Engine");
		}

		engine.mainloop();

	}

	@Override
	public void accepted(NioServer server, NioChannel channel) {
		// TODO Auto-generated method stub
		String port = "SYN" + this.channels.size();
		this.channels.add(channel);
		channel.send(port.getBytes(), 0, port.getBytes().length);
		channel.setDeliverCallback(this);
	}

	@Override
	public void closed(NioChannel channel) {
		// TODO Auto-generated method stub
		channel.close();
	}

	@Override
	public void deliver(NioChannel channel, ByteBuffer bytes) {
		// TODO Auto-generated method stub

		String hostName;
		String notification;
		int portS=0;
		String msg = new String(bytes.array());
		String message[] = msg.split("@");

		if(message[0].equals("JOIN")){
			int port = Integer.parseInt(message[2]);
			this.listPorts.put(channel, port);
		}

		if (this.listPorts.size() == this.nbMaxClients){
			for(int i =0; i < this.channels.size(); i++){
				for (int j = i+1; j < this.channels.size(); j++){
					hostName = channels.get(j).getRemoteAddress().getHostName();
					NioChannel ch = this.channels.get(j);
					portS = this.listPorts.get(ch);
					notification = hostName + "and"+portS;
					channels.get(i).send(notification.getBytes(), 0, notification.getBytes().length);
				}
			}	
		} 
	}
}
