package nio.engineMulticast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;

import nio.engine.AcceptCallback;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

public class ServerCallback implements AcceptCallback, DeliverCallback{
	
	ArrayList<NioChannel> channels;
	Hashtable<NioChannel, Integer > listPorts;
	//le nombre de connexions
	int nbMaxClients;

	@Override
	public void accepted(NioServer server, NioChannel channel) {
		// TODO Auto-generated method stub
		System.out.println("server  sur le port" + server.getPort());
		String port = "JOIN" + this.channels.size();
		this.channels.add(channel);
		channel.send(port.getBytes(), 0, port.getBytes().length);
		channel.setDeliverCallback(this);
		
	}

	@Override
	public void closed(NioChannel channel) {
		// TODO Auto-generated method stub
		System.out.println("MulticastServer channel closed");
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
