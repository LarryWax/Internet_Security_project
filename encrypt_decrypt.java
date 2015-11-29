import java.security.*;
import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.util.*;
import larry.crypto.DoubleCipher;

class encrypt_decrypt{
	public static void main(String[] args){
	 	try{
			String text = "Succhiapalle";
			String secret = "suca";

			System.out.println("************** SIMMETRIC ENCODING *************");
			System.out.println("Plain text: "+text);
			
			Key simmKey = generateSimmKey(secret);
			final byte[] encBytes = Encrypt(simmKey, text.getBytes(), "AES");
        	String simmEncText = new BASE64Encoder().encode(encBytes);	
			System.out.println("Encrypted text: " + simmEncText);
		
			simmCipher.init(Cipher.DECRYPT_MODE, simmKey);
			final byte[] simmDecoderText = new BASE64Decoder().decodeBuffer(simmEncText);
			final byte[] simmDecByte = simmCipher.doFinal(simmDecoderText);
			String simmDecText = new String(simmDecByte);
			System.out.println("Decrypted text: "+ simmDecText);


			System.out.println("************** ASIMMETRIC ENCODING *************");
		
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair kp = kpg.genKeyPair();
			Key publicKey = kp.getPublic();
			Key privateKey = kp.getPrivate();
			File pbk = new File("./public.key");
			FileWriter writer = new FileWriter(pbk);
			byte[] publicKeyBytes = publicKey.getEncoded();
			//Convert Public key to String
			BASE64Encoder encoder = new BASE64Encoder();
			String pubKeyStr = encoder.encode(publicKeyBytes);
			//System.out.println(pubKeyStr);
			writer.write(pubKeyStr);
			writer.flush();
			writer.close();

      		//Cipher cipher = Cipher.getInstance("RSA");
			//cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] cipherByte = cipher.doFinal(text.getBytes());
			String asimmEncText = new BASE64Encoder().encode(cipherByte);
			System.out.println("Encripted text: " + asimmEncText);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] plainText = cipher.doFinal(cipherByte);
			System.out.println("Decrypted text: " + new String(plainText));
		}catch(Exception e){ System.out.println(e);}
	}
}
