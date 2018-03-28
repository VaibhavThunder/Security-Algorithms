import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;

public class JClient {
	final static BigInteger one = new BigInteger("1");

	public static void main(String[] args) throws Exception {
		String sentence;
		BigInteger p, g;
		String modifiedSentence;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		// Get a start spot to pick a prime from the user.
		System.out.println("Enter the approximate value of p you want.\n");
		String ans = inFromUser.readLine();
		p = getNextPrime(ans);
		System.out.println("Your prime is " + p + ".");
		// Get the base for exponentiation from the user.
		System.out.println("Now, enter a base number.");
		g = new BigInteger(inFromUser.readLine());
		String setOfData = new StringBuilder(p + ";" + g).toString();

		// Create a socket and transmit the agreed values.
		Socket clientSocket = new Socket("localhost", 6789);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeBytes(setOfData + '\n');

		// accept reply from server
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		modifiedSentence = inFromServer.readLine();
		System.out.println("FROM SERVER: " + modifiedSentence);

		// This is the value that will get sent from client to server.
		// This value does NOT compromise the value of a easily.
		BigInteger a = BigInteger.valueOf(233);
		BigInteger resulta = g.modPow(a, p);

		outToServer.writeBytes(resulta.toString() );
		// Once client and server receive their values, they make their new calculations.
		// This involved getting their new numbers and raising them to the
		// same power as before, their secret number.
		BigInteger KeyClientCalculates = new BigInteger(modifiedSentence).modPow(a,p);
		System.out.println("A takes "+modifiedSentence+" raises it to the power "+a+" mod "+p);
		System.out.println("The Key A calculates is "+KeyClientCalculates+".");
		clientSocket.close();

	}

	private static BigInteger getNextPrime(String ans) {
		BigInteger test = new BigInteger(ans);
		while (!test.isProbablePrime(99))
			test = test.add(one);
		return test;
	}

}
