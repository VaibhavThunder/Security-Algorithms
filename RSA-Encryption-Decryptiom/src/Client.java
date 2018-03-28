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
		System.out.println("Please enter 'HI' to start communication");
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
				
				long startTime = System.nanoTime();
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(new File("10000words.txt")));
					String availalbe;
					while ((availalbe = br.readLine()) != null) {
						String ciphertext = aRsa.encrypt(availalbe, ServerPK, ServerModu);
						outToServer.writeBytes(ciphertext + '\n');
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
				System.out.println(stopTime - startTime);
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