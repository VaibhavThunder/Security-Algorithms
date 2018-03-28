import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

class TCPServer {
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	public static void main(String argv[]) throws Exception {
		String clientSentence;
		ServerSocket welcomeSocket = new ServerSocket(6789);

		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			clientSentence = inFromClient.readLine();
			long startTime = System.nanoTime();
			while ((clientSentence = inFromClient.readLine()) != null) {
				System.out.println("Received: " + clientSentence);
				String[] splitMessgaeandHmac = clientSentence.split(";");
				String message = splitMessgaeandHmac[0];
				String hmacForMessage = splitMessgaeandHmac[1];
				String calculatedHash = calculateHMAC(message, "message integrity");
				if (!hmacForMessage.equals(calculatedHash)) {
					break;
				}
				else
				{
					System.out.println("Passed");
				}
				if (message.equalsIgnoreCase("poison"))
				{
					long stopTime = System.nanoTime();
					System.out.println("Took " + (stopTime - startTime) + " ns");
					break;
				}
			}
		}
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