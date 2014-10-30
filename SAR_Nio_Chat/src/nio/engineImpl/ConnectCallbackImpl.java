package nio.engineImpl;

import nio.engine.ConnectCallback;
import nio.engine.NioChannel;


public class ConnectCallbackImpl implements ConnectCallback {


	public void closed(NioChannel channel) {
		//channel.close();
		System.out.println("ConnectCallbackImpl connexion closed");
	}


	public void connected(NioChannel channel) {
		//channel.getChannel();
		System.out.println("ConnectCallbackImpl connexion connected");
	}

}
