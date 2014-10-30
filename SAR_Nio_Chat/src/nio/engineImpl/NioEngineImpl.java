package nio.engineImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Iterator;

import nio.engine.AcceptCallback;
import nio.engine.ConnectCallback;
import nio.engine.NioEngine;
import nio.engine.NioServer;

public class NioEngineImpl extends NioEngine {

	Selector selector;
	Hashtable<SocketChannel, NioChannelImpl> channels;
	Hashtable<ServerSocketChannel, NioServerImpl> servers;
    Hashtable<SocketChannel, ConnectCallback> callbacks;

	

	public NioEngineImpl() throws Exception {
		super();
		this.selector = Selector.open();
		this.channels = new Hashtable <SocketChannel, NioChannelImpl>();
		this.servers = new Hashtable <ServerSocketChannel, NioServerImpl>();
		this.callbacks = new Hashtable <SocketChannel, ConnectCallback>();
	}
	
	@Override
	public void mainloop() {
		System.out.println("Nio... running");
		while (true) {
			try {
				selector.select();
				Iterator<?> selectedKeys = this.selector.selectedKeys().iterator();

				while (selectedKeys.hasNext()) {

					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						System.out.println(" cle valide");
						continue;
					} 
					if (key.isConnectable()) {
						System.out.println("je suis connecté");
						handleConnection(key);
					}
					if (key.isAcceptable()) {
						System.out.println("connection accepté");
						handleAccept(key);

					} else if (key.isReadable()) {
						System.out.println("je peux lire");
						handleRead(key);

					} else if (key.isWritable()) {
						System.out.println("je peux écrire");
						handleWrite(key);

					} else 
						System.out.println("  ---> unknow key=");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}		
	}
	

	@Override
	public NioServer listen(int port, AcceptCallback callback)
			throws IOException {
		NioServerImpl nsi = null;

		System.out.println("Listening on port " + port);

		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		InetSocketAddress isa = new InetSocketAddress("localhost", port);
		serverChannel.socket().bind(isa);

		nsi = new NioServerImpl (serverChannel);
		nsi.setCallback(callback);

		serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		servers.put(serverChannel, nsi);
		return nsi;
	}

	

	@Override
	public void connect(InetAddress hostAddress, int port,
			ConnectCallback callback) throws UnknownHostException,
			SecurityException, IOException {

		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);

		socketChannel.connect(new InetSocketAddress(hostAddress, port));
		socketChannel.register(this.selector, SelectionKey.OP_CONNECT);
		System.out.println(" Connection reussi :");

		callbacks.put(socketChannel, callback);
	}
	private void handleRead(SelectionKey key) {
		SocketChannel channel = (SocketChannel) key.channel();
		channels.get(channel).ReadChannel();;
	}

	private void handleWrite(SelectionKey key) {
        boolean empty;
		SocketChannel socketChannel = (SocketChannel) key.channel();
		NioChannelImpl channel = channels.get(socketChannel);
		empty = channel.WriteChannel();
		
		if(empty){
			key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
		}
		
	}

	private void handleAccept(SelectionKey key) {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel = null;

		try {
			socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);
			socketChannel.register(this.selector, SelectionKey.OP_READ);

			System.out.println("je recupere" + servers.size());
			NioServerImpl server = this.servers.get(serverSocketChannel);
			AcceptCallback callback = server.getCallback();

			NioChannelImpl channel = new NioChannelImpl(socketChannel, this);
			channels.put(socketChannel, channel);
			callback.accepted(server, channel);
		}

		catch(IOException e){
			// as if there was no accept done
			System.out.println("no connexion ");
			//System.exit(1);
			return;
		}
	}

	private void handleConnection(SelectionKey key) {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		try {
			socketChannel.finishConnect();
		}
		catch(IOException e){
			key.cancel();
			return;
		}
		key.interestOps(SelectionKey.OP_READ);

		NioChannelImpl channel = new NioChannelImpl(socketChannel, this);
		channels.put(socketChannel, channel);
		callbacks.get(socketChannel).connected(channel);
	}

	public void WriteInterest(NioChannelImpl myChannel) {
		try {
			myChannel.getChannel().register(this.selector,
					SelectionKey.OP_WRITE | SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			e.printStackTrace();
		}
	}


}
