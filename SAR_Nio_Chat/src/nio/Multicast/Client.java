package nio.Multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;

import nio.engine.AcceptCallback;
import nio.engine.ConnectCallback;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioEngine;
import nio.engine.NioServer;
import nio.engineImpl.NioEngineImpl;






public class Client implements ConnectCallback, DeliverCallback, AcceptCallback,Runnable {
	

	Server serv;
	int portC;
	int portS;
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
	
	public Client(int id,int portS,int portC,int n) throws UnknownHostException {
		super();
		this.id=id;
		this.clock=0;
		this.portC=portC;
		this.portS=portS;
		this.channels=new ArrayList<NioChannel>();
		this.MsgList= new ArrayList<MulticastMessage>();
		this.WaitingACK=new LinkedList<String>();
		this.neighbors=n;
	}
	
	public Client(int id,int portS,int portC) throws UnknownHostException {
		super();
		this.id=id;
		this.clock=0;
		this.portC=portC;
		this.portS=portS;
		this.channels=new ArrayList<NioChannel>();
		this.MsgList= new ArrayList<MulticastMessage>();
		this.WaitingACK=new LinkedList<String>();
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
		try {
			System.out.println("Client");
			engine.listen(portC, this);
			engine.connect(InetAddress.getByName("localhost"), portS, this);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//listen sur liste de port
		
		engine.mainloop();
		
	}

	@Override
	public void accepted(NioServer server, NioChannel channel) {
		System.out.println("Connexion accepted by NioClient by "+server.getPort()+" from "+ channel.getRemoteAddress().getPort());
		// TODO Auto-generated method stub
		channels.add(channel);
		channel.setDeliverCallback(this);
		sendToAllClient("hello");
		
		
	}

	@Override
	public void deliver(NioChannel channel, ByteBuffer bytes) {
		// TODO Auto-generated method stub
		String msg = new String(bytes.array());
		String[] m = msg.split(">");
		String header = m[0];
		System.out.println("HEADER: "+ m[0]+ " receveid by Client "+this.id+"");
		String queue = m[1];
		
		System.out.println(msg+ " received by Client "+this.id);


		if (header.equals("CHAT")) {
			String[] component = queue.split("@");
			System.out.println("Message " + component[2]+ " received by Client "+this.id+ " from Client " +component[0]);
			int receivedId = Integer.parseInt(component[0]);
			int receivedClock = Integer.parseInt(component[1]);
			String content = component[2];
			MulticastMessage Msg = new MulticastMessage(receivedId, receivedClock, content);
			MsgList.add(Msg);
			setNewClock(receivedClock);
			sendACKToClient();
			//retrieveWaitingACK();
		}
		if (header.equals("ACK")) {
			boolean test=header.equals("ACK");
			System.out.println("test= "+test);
			String[] componentA = queue.split("@");
			MulticastMessage res ;
			res=FindMessageByIdandClock(MsgList,Integer.parseInt(componentA[0]), Integer.parseInt(componentA[1]));
			if(!(res == null)){
				int sender = Integer.parseInt(componentA[0]);
				System.out.println("ACK received by Client "+this.id+ " from Client " +sender);
				res.setTrueACK(sender);
				Deliverer del = new Deliverer();
				del.AttemptDeliverMessage(MsgList);
			}else{
				System.out.println("ACK received  earlier by Client "+this.id+ " from Client " +componentA[0]);
				WaitingACK.add(msg);
			}
		}

		if (header.equals("GROUP")) {
			System.out.println("j'ai reçu un msg GROUP");
			System.out.println(msg);
			if (queue.equals("none")) {
				//the client have no one to connect to
			}
			else {
				String[] tab = queue.split("-"); 
				for (int i=0;i<tab.length;i++) {
					String[] element = tab[i].split("@");
					String ipC = element[0];
					String portC = element[1];					
					Join(ipC,portC);

				}

			}	
			//the client sent his port to everyone but only the server can interpret this message in order to be added in the group
			String joiner = "JOIN>"+this.portC;
			this.sendJointoAll(joiner);
		}
		
		else {
    	System.out.println(m[0]+" received by Client "+this.id+" is not a header identified");  
		}
	}

	private void Join(String ipC, String portC) {
		// TODO Auto-generated method stub

			try {
				this.engine.connect(InetAddress.getByName(ipC), Integer.parseInt(portC),this);
			} catch (NumberFormatException
					| SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
	}



	public void retrieveWaitingACK() {
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
				Deliverer d = new Deliverer();
				d.AttemptDeliverMessage(MsgList);
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
		System.out.println("Client "+this.id+" is sending to all client");
		String msg =buildMsg("CHAT", this.id, this.clock,content);
		MulticastMessage m = new MulticastMessage(this.id, this.clock,content);
		for (int i=0;i<channels.size();i++) {
			channels.get(i).send(msg.getBytes(), 0, msg.getBytes().length);
		}
	    MsgList.add(m);
		this.clock++;
	}
	
	
	public void sendJointoAll(String content) {
		for (int i=0;i<channels.size();i++) {
			channels.get(i).send(content.getBytes(), 0, content.getBytes().length);
		}
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
	
			
			public int HasBeenMember(SocketChannel clientSocket) {
		        int haslogged = -1;
		        int i = 0;

		        for (NioChannel ch : channels) {
		            if (ch.getChannel() == clientSocket) {
		            	haslogged = i;
		            }
		        }
		        return i; 
		    }
			
			
	public String buildMsg(String type,int id,int estampille,String content) {
		String result = type + ">" + id + "@" + estampille +"@" + content;
		return result;	
	}
	
	public String buildACK(String type,int id,int estampille) {
		String result = type + ">" + id + "@" + estampille;
		return result;	
	}
	
	
	public void setNewClock(int ck)  {
		int valuetoset = Math.max(ck+1, this.clock+1);
		this.clock=valuetoset;
	}
}
