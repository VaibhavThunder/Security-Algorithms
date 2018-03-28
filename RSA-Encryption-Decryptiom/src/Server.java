import java.io.*;
import java.math.BigInteger;
import java.net.*;

class TCPServer {
	public static void main(String argv[]) throws Exception {
		String clientSentence;
		String capitalizedSentence;
		BigInteger e, n = null, d = null;
		BigInteger ClientPK = null, ClientModu = null;
		int backlogSize = 50;
		ServerSocket welcomeSocket = new ServerSocket(6789, backlogSize);
		Socket connectionSocket = welcomeSocket.accept();
		while (true) {
			// if(connectionSocket.isConnected())
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			long startTime = System.nanoTime();
			while ((clientSentence = inFromClient.readLine()) != null) {
				System.out.println("recevied Data " + clientSentence);
				if (clientSentence.equalsIgnoreCase("HI")) {
					// return public key to client
					System.out.println("Return public key");
					RSA aRsa = new RSA(1024);
					e = aRsa.getE();
					n = aRsa.getN();
					d = aRsa.getD();
					String ServerPK = new StringBuilder(e + "," + n).toString();
					outToClient.writeBytes(ServerPK + '\n');
				} else if (clientSentence.contains(",")) {
					String[] publickKeyForClient = clientSentence.split(",");
					ClientPK = new BigInteger(publickKeyForClient[0]);
					ClientModu = new BigInteger(publickKeyForClient[1]);

				} else {
					// Received encrypted message Let's decrypt the
					// message
					RSA aRsa = new RSA(1024);
					String decrptedMessage = aRsa.decrypt(clientSentence, d, n);
					System.out.println("decrptedMessage " + decrptedMessage);
					if (decrptedMessage.equalsIgnoreCase("poison")) {
						long stopTime = System.nanoTime();
						System.out.println("Took " + (stopTime - startTime) + " ns");
						String replyToClient = "Decrytion Completed";
						String ciphertext = aRsa.encrypt(replyToClient, ClientPK, ClientModu);
						outToClient.writeBytes(ciphertext + '\n');
					}
				}
			}
		}
	}
}
