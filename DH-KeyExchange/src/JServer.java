import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class JServer {
	public static void main(String argv[]) throws Exception {
		String clientSentence;
		String capitalizedSentence;
		String line = null;
		BigInteger results, s = null;
		BigInteger p = null, g = null;
		ServerSocket welcomeSocket = new ServerSocket(6789);
		Socket connectionSocket = welcomeSocket.accept();
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		while (true) {
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			clientSentence = inFromClient.readLine();
			System.out.println("Received: " + clientSentence);
			if (clientSentence.contains(";")) {
				String[] splited = clientSentence.split(";");
				// System.out.println("splitString[" + i + "] is " +
				// splited[i]);
				p = BigInteger.valueOf(Long.parseLong(splited[0]));
				g = BigInteger.valueOf(Long.parseLong(splited[1]));

				System.out.println("splitString is " + p + "hello " + g);
				// Socket to send data back to client
				s = BigInteger.valueOf(100);
				// server's calculation
				// This is the value that will get sent from server to
				// client.
				// This value does NOT compromise the value of a easily.
				results = g.modPow(s, p);
				String Computedvalue = results.toString() + '\n';
				System.out.println("results " + results.toString());
				outToClient.writeBytes(Computedvalue);
			} else {
				// client computed key to compute secret
				System.out.println("Next Step : " + clientSentence);
				BigInteger KeyServerCalculates = new BigInteger(clientSentence).modPow(s, p);
				System.out.println("A takes " + clientSentence + " raises it to the power " + s + " mod " + p);
				System.out.println("The Key A calculates is " + KeyServerCalculates + ".");
			}
		}
	}
}
