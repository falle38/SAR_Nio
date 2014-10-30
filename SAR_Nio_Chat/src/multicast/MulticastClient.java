package multicast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import nio.engine.AcceptCallback;
import nio.engine.ConnectCallback;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioEngine;
import nio.engine.NioServer;
import nio.engineImpl.NioEngineImpl;


public class MulticastClient implements ConnectCallback, DeliverCallback, AcceptCallback,Runnable {
	

	String address;
	int port;
	//Un message est composé d'un type, de l'id de l'expéditeur et son estampille
	LinkedList<String> MsgList;
	//sert à déterminer si le client est tjs vivant
	boolean isalive;
	//nioEngine
	NioEngineImpl engine;
	int id;
	int clock;
	//connexion avec les autres clients
	ArrayList<NioChannel> channels;
	//ACK de la part des autres clients
	LinkedList<String> ACKlist;
	//nombre de clients que l'on connait
	int neighbors;
	
	public MulticastClient(int id,String address,int port,int n) {
		super();
		this.id=id;
		this.clock=0;
		this.address=address;
		this.port=port;
		this.channels=new ArrayList<NioChannel>();
		this.MsgList=new LinkedList<String>();
		this.ACKlist=new LinkedList<String>();
		this.neighbors=n;
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		engine = null;

		try{
			engine = new NioEngineImpl();
		}catch (Exception e) {
			NioEngine.panic("Problem in Multicast Engine Client");
		}
		
		engine.mainloop();
		
	}

	@Override
	public void accepted(NioServer server, NioChannel channel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliver(NioChannel channel, ByteBuffer bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closed(NioChannel channel) {
		// TODO Auto-generated method stub
		System.out.println("MulticastClient channel closed");
		channel.close();	
	}

	@Override
	public void connected(NioChannel channel) {
		// TODO Auto-generated method stub
		channels.add(channel);
		channel.setDeliverCallback(this);
		
	}
	
	
	public void sendToAllClient(String type) {
		String msg =buildMsg(type, this.id, this.clock);
		for (int i=0;i<channels.size();i++) {
			channels.get(i).send(msg.getBytes(), 0, msg.getBytes().length);
		}
		MsgList.add(msg);
		this.clock++;
		
	}
	
	
	public String buildMsg(String content,int id,int estampille) {
		String result = content + "@" + this.id + "@" + estampille;
		return result;	
	}
	
	
	

	public void setNewClock(int ck)  {
		int valuetoset = Math.max(ck+1, this.clock+1);
		this.clock=valuetoset;
	}
	

}
