import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

class TCPServer {
	public static void main(String argv[]) throws Exception {
		String password = null;
		String capitalizedSentence;
		ServerSocket welcomeSocket = new ServerSocket(6789);

		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());
			// KeyStore keystore = KeyStore.getInstance("JCEKS");
			// keystore.load(new FileInputStream("key.store"),
			// "12345".toCharArray());
			// Key key = keystore.getKey("t1", "12345".toCharArray());
			int len = inFromClient.readInt();
			// System.out.println("KEy is" + key.toString());
			// password = "Passcode";
			// byte[] salt = new byte[64];
			// Random rnd = new Random();
			// rnd.nextBytes(salt);
			// byte[] data = deriveKey(password, salt, 64);
			// SecretKey desKey =
			// SecretKeyFactory.getInstance("DES").generateSecret(new
			// DESKeySpec(data));
			FileInputStream readFile = new FileInputStream(new File("key.txt"));
			int len1 = readFile.available();
			byte[] data = new byte[len1];

			readFile.read(data);
			KeySpec ks = null;
			SecretKey ky = null;
			SecretKeyFactory kf = null;
			ks = new DESKeySpec(data);
			kf = SecretKeyFactory.getInstance("DES");
			ky = kf.generateSecret(ks);
			SecretKey originalKey = new SecretKeySpec(data, 0, data.length, "DES");
			System.out.println("Read KEy is" + ky);
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, ky);
			// Decrypt the text
			byte[] textDecrypted = null;
			if (len > 0) {
				byte[] message = new byte[len];
				inFromClient.readFully(message, 0, message.length);
				System.out.println("Text Received " + message);
				textDecrypted = cipher.doFinal(message);
			}
			System.out.println("Text Decryted : " + new String(textDecrypted));

		}
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