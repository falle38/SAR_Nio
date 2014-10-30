package nio.engineImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import nio.engine.*;


public class NioChannelImpl extends NioChannel {
    
	SocketChannel socketChannel;
	NioEngineImpl nioEngine;
	DeliverCallback callback;

	
	private LinkedList<ByteBuffer> listBuff;
	private ByteBuffer outBufferWrite;
	private ByteBuffer lenBufferWrite;
	private ByteBuffer lengthBuffer;
	private ByteBuffer readingBuffer = null;
	
    /*states */
	
	/*states of reading*/
	private int READING_LENGTH=1; 
	private int READING_MSG=2;
	private int READING_DONE=3; 
	

	/*states of writing*/
	private int WRITING_LENGTH=1; 
	private int WRITING_MSG=2;
	private int WRITING_DONE=3; 
	
    /* starting states */
	int currentReadState = READING_DONE;
	int currentWriteState = WRITING_DONE;

	public NioChannelImpl(SocketChannel mySocketChannel, NioEngineImpl myEngine) {
		this.socketChannel = mySocketChannel;
		this.nioEngine = myEngine;
		this.lenBufferWrite = ByteBuffer.allocate(4);
		this.lengthBuffer= ByteBuffer.allocate(4);
		this.listBuff= new LinkedList<ByteBuffer>();
	}

	
	@Override
	public SocketChannel getChannel() {
		return socketChannel;
	}

	
	@Override
	public void setDeliverCallback(DeliverCallback callback) {

		this.callback = callback;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		InetSocketAddress isa = null;
		try {
			isa =(InetSocketAddress) this.socketChannel.getRemoteAddress();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return isa;
	}


	@Override
	public void send(ByteBuffer buf) {
		    listBuff.add(buf);
			nioEngine.WriteInterest(this);
		
	}

	
	@Override
	public void send(byte[] bytes, int offset, int length) {
		if(bytes.length <= length && offset < bytes.length){
			int i;
			ByteBuffer bb = ByteBuffer.allocate(length);

			for (i = offset; i < offset + length; i++) {
				bb.put(bytes[i]);
			}
			bb.toString();
			send(bb);
			}
			else {
				System.out.println("Error in methode send");
			}
		
	}

	@Override
	public void close() {
		try {
			this.socketChannel.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public void ReadChannel() {
		if (currentReadState == READING_DONE) {
			readingBuffer = null;
			lengthBuffer.position(0); 
			currentReadState = READING_LENGTH;
		}

		if (currentReadState == READING_LENGTH) { 
			try {
				socketChannel.read(lengthBuffer);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			if (lengthBuffer.remaining() == 0) {
				lengthBuffer.position(0);
				int length = lengthBuffer.getInt();
				readingBuffer = ByteBuffer.allocate(length);
				currentReadState = READING_MSG;
			}
		}

		if (currentReadState == READING_MSG) {
			try {
				socketChannel.read(readingBuffer);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			if (readingBuffer.remaining() == 0) {
				callback.deliver(this, readingBuffer.duplicate()); 

				currentReadState = READING_DONE;
			}

		}

	}
	
	
	public boolean WriteChannel() {
		if(currentWriteState == WRITING_DONE)
		{
			outBufferWrite = listBuff.pop();
			outBufferWrite.position(0);
			lenBufferWrite.position(0);
			lenBufferWrite.putInt(outBufferWrite.capacity());
			lenBufferWrite.position(0);
			currentWriteState = WRITING_LENGTH;
		}
		
		if (currentWriteState == WRITING_LENGTH) { 
			try {
				socketChannel.write(lenBufferWrite);
			} catch (IOException e) {
				System.out.println("Error in writing length");
			}
			if(lenBufferWrite.remaining()==0){
				currentWriteState = WRITING_MSG;
			}
		}
		
		if(currentWriteState == WRITING_MSG)
		{
			try {
				socketChannel.write(outBufferWrite);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			if( outBufferWrite.remaining() == 0 ) {
				currentWriteState = WRITING_DONE;
			}
		}
		

		return listBuff.size()==0;
	}

	
	

}
