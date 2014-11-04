package nio.Multicast;

import java.util.ArrayList;

/****** author fall & sambe ******/

public class MulticastMessage {
	

    public final static int NbMaxMember = 3;
	
	int idM;
	int clock;
	boolean valid;
	boolean[] ack;
	String content;
	
	public MulticastMessage() {
		super();
	}
	
	
	
	public MulticastMessage(int idm,int clock,int neighbours,String content) {
		this.idM=idm;
		this.clock=clock;
		this.valid=true;
		this.ack=fillTable(neighbours,false);
		this.content=content;
	}
	
	public MulticastMessage(int idm,int clock,String content) {
		this.idM=idm;
		this.clock=clock;
		this.valid=true;
		this.ack=fillTable(NbMaxMember,false);
		this.content=content;
	}
	
	
	
	
	//supprimer un message particulier d'id IdM
	public ArrayList<MulticastMessage>  removeMessage(ArrayList<MulticastMessage> MessageList, int idM){
		for(int i = 0; i<MessageList.size(); i++){
			if (MessageList.get(i).idM == idM)  {
				MessageList.remove(i);
			}	
		}
		return MessageList;
	}
	
    //remplit le tableau des booléens à value (true or false)
	public boolean[] fillTable( int n, boolean value) {
		boolean[] tab = new boolean[n];
		for(int i = 0; i < n; i++){
		tab[i] = false;
		}
		return tab;
	}
	
	
	
		
		public boolean Ready() {
			// TODO Auto-generated method stub
			return ensure(this);
		}


		public boolean ensure(MulticastMessage multicastMessage) {
			// TODO Auto-generated method stub
			for (int i=0;i<this.ack.length;i++) {
				if(ack[i] == true && valid) { //on vérifie qu'on a reçu tous les ack pour le msg et que le message est considéré comme valide
					return true;
				}
			}
			return false;
		}
	
		
		
		
		
		public int getIdM() {
			return idM;
		}



		public void setIdM(int idM) {
			this.idM = idM;
		}



		public int getClock() {
			return clock;
		}



		public void setClock(int clock) {
			this.clock = clock;
		}



		public boolean isValid() {
			return valid;
		}



		public void setValid(boolean valid) {
			this.valid = valid;
		}



		public boolean[] getAck() {
			return ack;
		}



		public void setAck(boolean[] ack) {
			this.ack = ack;
		}



		public String getContent() {
			return content;
		}



		public void setContent(String content) {
			this.content = content;
		}



		public void setTrueACK(int from) {
			// TODO Auto-generated method stub
			ack[from]=true;
		}


	
}
