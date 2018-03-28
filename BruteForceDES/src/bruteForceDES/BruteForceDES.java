package bruteForceDES;

import java.util.Random;
import javax.crypto.SealedObject;

public class BruteForceDES {
	private static Thread[] threads;
	private static DecryptThread[] decryptThreads;
	private static int numThreads;
	private static long numKeyBits;
	private static long key, maxkey;
	private static String text;
	private static long startTime, endTime;

	private static void parseArgs(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: BruteForceDES #threads key_size_in_bits");
			System.exit(0);
		}
		try {
			numThreads = Integer.parseInt(args[0]);
			numKeyBits = Long.parseLong(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("Usage: BruteForceDES #threads key_size_in_bits");
		}
		maxkey = ~(0L);
		maxkey = maxkey >>> (64 - numKeyBits);
		key = new Random().nextLong();
		key = key & maxkey;
	}

	private static SealedObject getSealedText(String text) {
		SealedDES enccipher = new SealedDES();
		enccipher.setKey(key);
		SealedObject sealedObj = enccipher.encrypt(text);
		return sealedObj;
	}

	private static void initDecryptThreads() {
		long numKeysPerThread = maxkey / numThreads;
		long numRemainKeys = maxkey % numThreads;
		decryptThreads = new DecryptThread[numThreads];
		threads = new Thread[numThreads];
		long currentKey = 0;
		for (int i = 0; i < numThreads; i++) {
			long num = numKeysPerThread;
			if (numRemainKeys > 0) {
				num++;
				numRemainKeys--;
			}
			decryptThreads[i] = new DecryptThread(getSealedText(text), i, currentKey, currentKey + num);
			currentKey += num;
			threads[i] = new Thread(decryptThreads[i]);
		}
	}

	private static void decrypt() {
		System.out.println("Generated secret key " + key);
		/* startTime = System.currentTimeMillis(); */
		for (int i = 0; i < numThreads; i++) {
			/* decryptThreads[i].setStartTime(startTime); */
			threads[i].start();
		}
		for (int i = 0; i < numThreads; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				System.err.println("Thread interrupted");
			}
		}

		endTime = System.currentTimeMillis();
		System.out.println("Final elapsed time: " + (endTime - startTime));
	}

	public static void main(String[] args) {
		BruteForceDES.text = "Network Security?";
		parseArgs(args);
		startTime = System.currentTimeMillis();
		initDecryptThreads();
		decrypt();
	}
}