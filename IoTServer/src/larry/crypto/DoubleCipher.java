package larry.crypto;

import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DoubleCipher {
	
	public DoubleCipher(){};

	public Key generateSimmKey(String secret){
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(secret.getBytes());
			final byte[] digest = md.digest();
			final Key simmKey = new SecretKeySpec(digest, "AES");
			return simmKey;
		}catch(Exception e){
			System.out.println(e);
			System.out.println(e.getStackTrace());
			return null;
		}
	}
	
	public byte[] encrypt(Key key, byte[] text, String Algorithm){
		try{
			final Cipher c = Cipher.getInstance(Algorithm);
			c.init(Cipher.ENCRYPT_MODE, key);
			final byte[] EncByte = c.doFinal(text);
			return EncByte;
		}catch(Exception e){
			System.out.println(e);
			System.out.println(e.getStackTrace());
			return null;
		}
	}
	
	public byte[] decrypt(Key key, byte[] text, String Algorithm){
		try{
			final Cipher c = Cipher.getInstance(Algorithm);
			c.init(Cipher.DECRYPT_MODE, key);
			final byte[] DecByte = c.doFinal(text);
			return DecByte;
		}catch(Exception e){
			System.out.println(e);
			System.out.println(e.getStackTrace());
			return null;
		}
	}
	
	public byte[] doubleEncrypt(String text, String seed, Key publicKey, Key privateKey){
		
		String concat = text + ',' + seed;
		final byte[] encText = encrypt(publicKey, concat.getBytes(), "RSA");
		final byte[] encDouble = encrypt(privateKey, encText, "RSA");
		return encDouble;
	}
	
	public String doubleDecrypt(Key publicKey, Key privateKey, byte[] text){
		
		final byte[] encByte = decrypt(publicKey, text, "RSA");
		String message = new String(decrypt(privateKey, encByte, "RSA"));
		return message;
	}
}
