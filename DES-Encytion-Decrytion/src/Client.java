import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.PBEKeySpec;

class TCPClient {
	public static void main(String argv[]) throws Exception {
		byte[] sentence, textEncrypted;
		String modifiedSentence;
		String password;
//		sBufferedReader inFromUser = null;
		// InputStreamReader(System.in));
		Socket clientSocket = new Socket("localhost", 6789);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		password = "Passcode";
		byte[] salt = new byte[64];
		Random rnd = new Random();
		rnd.nextBytes(salt);
		byte[] data = deriveKey(password, salt, 64);

		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		// System.out.println("Enter the Data to be transmisted to server\n");
		// sentence = inFromUser.readLine().getBytes();
		SecretKey desKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(data));
		System.out.println("Secret key " + desKey.toString());
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

		FileOutputStream writeStream = new FileOutputStream(new File("key.txt"));
		byte[] kb = desKey.getEncoded();
		writeStream.write(kb);
		writeStream.close();
		cipher.init(Cipher.ENCRYPT_MODE, desKey);

		long startTime = System.nanoTime();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File("10000words.txt")));
			String availalbe;
			while ((availalbe = br.readLine()) != null) {
				textEncrypted = cipher.doFinal(availalbe.getBytes());
				outToServer.writeInt(textEncrypted.length);
				outToServer.write(textEncrypted);
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

		// textEncrypted = cipher.doFinal(word.getBytes());
		// outToServer.writeInt(textEncrypted.length);
		// outToServer.write(textEncrypted);
		long stopTime = System.nanoTime();
		System.out.println(stopTime - startTime);
		// outToServer.writeBytes(textEncrypted + '\n');
		clientSocket.close();
	}

	public static byte[] deriveKey(String password, byte[] salt, int keyLen) {
		SecretKeyFactory kf = null;
		try {
			kf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		KeySpec specs = new PBEKeySpec(password.toCharArray(), salt, 1024, keyLen);
		SecretKey key = null;
		try {
			key = kf.generateSecret(specs);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return key.getEncoded();
	}
}