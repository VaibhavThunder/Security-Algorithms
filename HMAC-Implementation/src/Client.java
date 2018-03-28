import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

class TCPClient {
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	@SuppressWarnings("unused")
	public static void main(String argv[]) throws Exception {
		String sentence;
		String modifiedSentence;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		Socket clientSocket = new Socket("localhost", 6789);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		sentence = "Network Security";
		String calculatedHash = calculateHMAC(sentence, "message integrity");
		outToServer.writeBytes((sentence + ";" + calculatedHash) + '\n');

		long startTime = System.nanoTime();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File("10000words.txt")));
			String availalbe;
			while ((availalbe = br.readLine()) != null) {
				calculatedHash = calculateHMAC(availalbe, "message integrity");
				outToServer.writeBytes((availalbe + ";" + calculatedHash) + '\n');
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		long stopTime = System.nanoTime();
		System.out.println("Took " + (stopTime - startTime) + " ns");
		clientSocket.close();
	}

	public static String calculateHMAC(String message, String key)
			throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKeySpec singingkey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
		Mac Hmac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		Hmac.init(singingkey);
		return toHexString(Hmac.doFinal(message.getBytes()));
	}

	@SuppressWarnings("resource")
	private static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}
}