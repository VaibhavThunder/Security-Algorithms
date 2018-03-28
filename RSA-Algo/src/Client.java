import java.io.*;
import java.math.BigInteger;
import java.net.*;

class TCPClient {
	@SuppressWarnings("unused")
	public static void main(String argv[]) throws Exception {
		String sentence;
		String modifiedSentence;
		BigInteger ServerPK, ServerModu, ClientPk, ClientModu = null, ClientPrivateKey = null;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		Socket clientSocket = new Socket("localhost", 6789);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		sentence = inFromUser.readLine();
		outToServer.writeBytes(sentence + '\n');

		while ((modifiedSentence = inFromServer.readLine()) != null)
		{
		
			System.out.println("FROM SERVER: " + modifiedSentence);
			if(modifiedSentence != null && modifiedSentence.contains(","))
			{
				String[] ExtractPublickeyAndModu = modifiedSentence.split(",");
				ServerPK = new BigInteger(ExtractPublickeyAndModu[0]);
				ServerModu = new BigInteger(ExtractPublickeyAndModu[1]);
				//Send Client public key for communication
				RSA aRsa = new RSA(1024);
				ClientPk = aRsa.getE();
				ClientModu = aRsa.getN();
				ClientPrivateKey = aRsa.getD();
				String PublicKeyToServer = new StringBuilder(ClientPk + "," + ClientModu).toString();
				outToServer.writeBytes(PublicKeyToServer + '\n');
				
				//Since Keys are exchanged let's start communication 
				System.out.println("Please enter message for server");
				String msg = inFromUser.readLine();
				System.out.println("Message for encryption " + msg);
				String ciphertext = aRsa.encrypt(msg, ServerPK, ServerModu);
				outToServer.writeBytes(ciphertext + '\n');
			}
			else
			{
				RSA aRsa = new RSA(1024);
				String decrptedMessage = aRsa.decrypt(modifiedSentence, ClientPrivateKey, ClientModu);
				System.out.println("Operation Successfull "  + decrptedMessage);
			}
		}
		//clientSocket.close();
	}
}