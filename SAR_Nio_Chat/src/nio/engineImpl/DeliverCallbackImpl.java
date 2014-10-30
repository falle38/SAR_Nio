package nio.engineImpl;

import java.nio.ByteBuffer;

import nio.engine.DeliverCallback;
import nio.engine.NioChannel;

public class DeliverCallbackImpl implements DeliverCallback {
	public void deliver(NioChannel channel, ByteBuffer bytes) {
		System.out.println("DeliverCallbackImpl message delivered");
	}
}
