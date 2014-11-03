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
	//sert à déterminer si le client est tjs vivant
	boolean isalive;
	//nioEngine
	NioEngineImpl engine;
	int id;
	int clock;
	//connexion avec les autres clients
	ArrayList<NioChannel> channels;
	//ACK de la part des autres clients
	LinkedList<String> WaitingACK;
	//nombre de clients que l'on connait
	int neighbors;
	//liste des messages
	ArrayList<MulticastMessage> MsgList;
	
	
	
	public MulticastClient(int id,String address,int port,int n) {
		super();
		this.id=id;
		this.clock=0;
		this.address=address;
		this.port=port;
		this.channels=new ArrayList<NioChannel>();
		this.MsgList= new ArrayList<MulticastMessage>();
		this.WaitingACK=new LinkedList<String>();
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
		channels.add(channel);
		channel.setDeliverCallback(this);
		
	}

	@Override
	public void deliver(NioChannel channel, ByteBuffer bytes) {
		// TODO Auto-generated method stub
		String msg = new String(bytes.array());
		String[] m = msg.split("@");
		String header = m[0];
		
		switch (header)
		{
		  case "CHAT":
			  System.out.println("Message" + m[3]+ "received by Client "+this.id+ " from Client " +m[1]);
				int receivedId = Integer.parseInt(m[1]);
				int receivedClock = Integer.parseInt(m[2]);
				String content = m[3];
				MulticastMessage Msg = new MulticastMessage(receivedId, receivedClock, content);
				MsgList.add(Msg);
				setNewClock(receivedClock);
				sendACKToClient();
				retrieveWaitingACK();
				
		    break;   
		  case "ACK":
			    MulticastMessage res ;
			    res=FindMessageByIdandClock(MsgList,Integer.parseInt(m[1]), Integer.parseInt(m[2]));
			    if(!(res == null)){
					int sender = Integer.parseInt(m[1]);
					System.out.println("ACK" + m[3]+ "received by Client "+this.id+ " from Client " +sender);
					res.setTrueACK(sender);
					res.AttemptDeliverMessage(MsgList);
				}else{
					System.out.println("ACK" + m[3]+ "received by Client "+this.id+ " from Client " +m[1]);
					WaitingACK.add(msg);
				}
			    break;  
		  default:
		    System.out.println("header not identified received by Client "+this.id);  
		}
			
		
	}

	private void retrieveWaitingACK() {
		// TODO Auto-generated method stub
		for(int i = 0; i < WaitingACK.size(); i++){
			String msg = WaitingACK.element();
			String m[] = msg.split("@");		

			MulticastMessage res;
			res=FindMessageByIdandClock(MsgList,Integer.parseInt(m[1]), Integer.parseInt(m[2]));
		    if(!(res == null)){
		    	int sender = Integer.parseInt(m[1]);
				System.out.println(" Retrieving ACK" + m[3]+ "received by Client "+this.id+ " from Client " +m[1]);
				res.setTrueACK(sender);
				WaitingACK.pop();
				res.AttemptDeliverMessage(MsgList);
		    }
		    else System.out.println(" Retrieving non coming message ACK" + m[3]+ "received by Client "+this.id+ " from Client " +m[1]);
		}
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
	
	
	public void sendToAllClient(String content) {
		String msg =buildMsg("MSG", this.id, this.clock,content);
		MulticastMessage m = new MulticastMessage(this.id, this.clock,content);
		for (int i=0;i<channels.size();i++) {
			channels.get(i).send(msg.getBytes(), 0, msg.getBytes().length);
		}
	    MsgList.add(m);
		this.clock++;
		
	}
	
	protected void sendACKToClient(){
		String ack = buildACK("ACK",this.id,this.clock);
		for(int i = 0; i < channels.size(); i++){
			channels.get(i).send(ack.getBytes(), 0, ack.getBytes().length);
		}
	}
	
	//rechercher un message particulier dans la liste
			public MulticastMessage FindMessageByIdandClock(ArrayList<MulticastMessage> Messages,int id, int clock) {
			MulticastMessage result = null;
			for(int i = 0; i < Messages.size(); i++){
				if(Messages.get(i).idM == id && Messages.get(i).clock == clock){
					result = Messages.get(i);
				}
			}
			return result;
			
			}
	
	public String buildMsg(String type,int id,int estampille,String content) {
		String result = type + "@" + this.id + "@" + estampille +"@" + content;
		return result;	
	}
	
	public String buildACK(String type,int id,int estampille) {
		String result = type + "@" + this.id + "@" + estampille;
		return result;	
	}
	
	

	public void setNewClock(int ck)  {
		int valuetoset = Math.max(ck+1, this.clock+1);
		this.clock=valuetoset;
	}
	

}
