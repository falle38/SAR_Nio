package nio.engineImpl;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

import nio.engine.AcceptCallback;
import nio.engine.NioServer;

public class NioServerImpl extends NioServer {
	
	ServerSocketChannel serverSocketChannel;
	AcceptCallback callBack;

	public NioServerImpl (ServerSocketChannel serverSocketChannel){
		this.serverSocketChannel = serverSocketChannel;
	}

	public NioServerImpl (ServerSocketChannel serverSocketChannel, AcceptCallback callback){
		this.serverSocketChannel = serverSocketChannel;
		this.callBack = callback;
	}
	

	public int getPort() {
		return this.serverSocketChannel.socket().getLocalPort();
	}
	
	
	public void close() {
		try {
			this.serverSocketChannel.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public AcceptCallback getCallback(){
		return this.callBack;
	}
	
	
	public void setCallback(AcceptCallback cb){
		this.callBack=cb;
	}


}
