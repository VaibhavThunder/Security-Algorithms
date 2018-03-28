package bruteForceDES;

import java.io.PrintStream;

import javax.crypto.SealedObject;

public class DecryptThread implements Runnable {
	private SealedDES deccipher;
	private SealedObject sealedObj;
	private long threadId;
	private long startKey, endKey;
	private long startTime;

	public DecryptThread(SealedObject sealedObj, long threadId, long startKey, long endKey) {
		this.sealedObj = sealedObj;
		this.threadId = threadId;
		this.startKey = startKey;
		this.endKey = endKey;
		this.deccipher = new SealedDES();
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getThreadId() {
		return this.threadId;
	}

	public void run() {
		// System.out.println("Startkey: " + startKey + "EndKey: " + endKey);
		System.out.println("Thread " + threadId + " StartKey: " + startKey + " EndKey: " + endKey);
		for (long i = startKey; i < endKey; i++) {
			this.deccipher.setKey(i);
			String decryptstr = this.deccipher.decrypt(sealedObj);
			if (decryptstr != null) {
				System.out.println(
						"Thread " + threadId + " found decrypt key " + i + " producing message: " + decryptstr);
			}
		}
	}
}