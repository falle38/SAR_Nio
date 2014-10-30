package nio.engineImpl;

//import java.net.InetSocketAddress;

import nio.engine.AcceptCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

public class AcceptCallbackImpl implements AcceptCallback {
	//first comment
	public void accepted(NioServer server, NioChannel channel) {
		//int port= server.getPort();
		//InetSocketAddress addr = channel.getRemoteAddress();
		System.out.println("AcceptCallbackImpl connexion acb accepted");//+addr.toString()+ "in port" +port);
	}

	public void closed(NioChannel channel) {
		//channel.close();
		System.out.println("AcceptCallbackImpl connexion closed");
	}

}