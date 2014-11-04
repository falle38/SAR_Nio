package nio.Multicast;

import java.util.ArrayList;

/****** author fall & sambe ******/

public class Deliverer {
	
	
	public Deliverer() {
		super();
	}
	
	// on vérifie que le message avec la plus petite estampille dans la liste qui peut être délivré
			public MulticastMessage AttemptDeliverMessage(ArrayList<MulticastMessage> Messages){
			
				MulticastMessage mu = getMessageWithLowerClock(Messages);

				if(mu != null){
					if(mu.Ready()){
						Messages.remove(mu);
						return mu;
					}
					else{
						System.out.println("pas d'acquittement pour ce msg donc il ne peut pas être délivré");
						return null;
					}
				}
				else{
					return null;
				}

			}

			//Récupère dans la liste des messages celui qui a la plus petite clock
			public MulticastMessage getMessageWithLowerClock(ArrayList<MulticastMessage> MessageList){	
				MulticastMessage tmp = null;

				if(!MessageList.isEmpty()){
					tmp=MessageList.get(0);

					for(int i = 1; i < MessageList.size(); i++ ){
						MulticastMessage multiM= MessageList.get(i);

						if(tmp.clock == multiM.clock){
							if(multiM.idM < tmp.idM){
								tmp = multiM;
							}
						}
						else if(tmp.clock > multiM.clock){
							tmp = multiM;
						}

					}
				}

				return tmp;
			}
			
	

}
