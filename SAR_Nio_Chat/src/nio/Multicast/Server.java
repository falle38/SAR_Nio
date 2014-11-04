package nio.Multicast;

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
	Hashtable<NioChannel, Integer > listPorts;
	
	ArrayList<String> group;
	
	ArrayList<NioChannel> registeredClients;

	//le nombre de connexions maximum
	int nbMaxClients;
	int portServer;
	String addrS;
	

	public String getAddrS() {
		return addrS;
	}

	public int getPortServer() {
		return portServer;
	}

	public Server (int port){
		this.nbMaxClients = 0;
		this.portServer = port;
		this.registeredClients = new ArrayList<NioChannel>();
		this.listPorts = new Hashtable<NioChannel, Integer>();
		this.group = new ArrayList<String>();
	}
	
	public Server (int port,int N){
		this.nbMaxClients = N;
		this.portServer = port;
		this.registeredClients = new ArrayList<NioChannel>();
		this.listPorts = new Hashtable<NioChannel, Integer>();
		this.group = new ArrayList<String>();
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
		System.out.println("server  sur le port" + server.getPort());
		//String port = "JOIN" + this.registeredClients.size();
		this.registeredClients.add(channel);
		String msg = "GROUP>";
		if (group.size() > 0)  {
		msg = msg+group.get(0);
		for (int i=1;i<group.size();i++)  {
		     msg = msg + "-" + group.get(i);
		}
		}
		else {
			msg = msg + "none";
		}
		channel.send(msg.getBytes(), 0, msg.getBytes().length);
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

//		String hostName;
//		String notification;
//		int portS=0;
		String msg = new String(bytes.array());
		String message[] = msg.split(">");

		if(message[0].equals("JOIN")){
			int port = Integer.parseInt(message[1]);
			this.listPorts.put(channel, port);
			//the server adds the new client to the group
			this.group.add(channel.getRemoteAddress().getHostString() + "@" +port);
		}
		
		
        /**
		if (this.listPorts.size() == this.nbMaxClients){
			 for(int i =0; i < this.registeredClients.size(); i++){
				for (int j = i+1; j < this.registeredClients.size(); j++){
					hostName = registeredClients.get(j).getRemoteAddress().getHostName();
					NioChannel ch = this.registeredClients.get(j);
					portS = this.listPorts.get(ch);
					notification = hostName + "and"+portS;
					registeredClients.get(i).send(notification.getBytes(), 0, notification.getBytes().length);
				}
			}
		} 
		**/
	}
	
	
	
}
