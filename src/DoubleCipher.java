import java.io.IOException;
import java.security.*;
import java.security.spec.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class DoubleCipher {
	
	public DoubleCipher(){};

	public Key generateSimmKey(String secret) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(secret.getBytes());
		final byte[] digest = md.digest();
		final Key simmKey = new SecretKeySpec(digest, "AES");
		return simmKey;
	}
	
	public byte[] encryptSimm(Key key, byte[] text, String Algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		final Cipher c = Cipher.getInstance(Algorithm);
		c.init(Cipher.ENCRYPT_MODE, key);
		final byte[] EncByte = c.doFinal(text);
		return EncByte;
	}
	
	public byte[] encryptAsimmPriv(PrivateKey key, byte[] text, String Algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		final Cipher c = Cipher.getInstance(Algorithm);
		c.init(Cipher.ENCRYPT_MODE, key);
		final byte[] EncByte = c.doFinal(text);
		return EncByte;
	}
	
	public byte[] encryptAsimmPub(PublicKey key, byte[] text, String Algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
			final Cipher c = Cipher.getInstance(Algorithm);
			c.init(Cipher.ENCRYPT_MODE, key);
			final byte[] EncByte = c.doFinal(text);
			return EncByte;
	}
	
	public byte[] decryptSimm(Key key, byte[] text, String Algorithm){
		try{
			final Cipher c = Cipher.getInstance(Algorithm);
			c.init(Cipher.DECRYPT_MODE, key);
			final byte[] DecByte = c.doFinal(text);
			return DecByte;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] decryptAsimmPriv(PrivateKey key, byte[] text, String Algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
			final Cipher c = Cipher.getInstance(Algorithm);
			c.init(Cipher.DECRYPT_MODE, key);
			final byte[] DecByte = c.doFinal(text);
			return DecByte;
	}
	
	public byte[] decryptAsimmPub(PublicKey key, byte[] text, String Algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
			final Cipher c = Cipher.getInstance(Algorithm);
			c.init(Cipher.DECRYPT_MODE, key);
			final byte[] DecByte = c.doFinal(text);
			return DecByte;
	}
	
	public byte[] doubleEncrypt(String seed, PublicKey publicKey, PrivateKey privateKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		
		final byte[] encText = encryptAsimmPub(publicKey, seed.getBytes(), "RSA");
		final byte[] encDouble = encryptAsimmPriv(privateKey, encText, "RSA");
		return encDouble;
	}
	
	public String doubleDecrypt(PublicKey publicKey, PrivateKey privateKey, byte[] text) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		
		final byte[] encByte = decryptAsimmPub(publicKey, text, "RSA");
		String message = new String(decryptAsimmPriv(privateKey, encByte, "RSA"));
		return message;
	}
	
	public PrivateKey genPrivKeyFromStr(String privKeyStr) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
		byte[] privateKeyBytes = new BASE64Decoder().decodeBuffer(privKeyStr);
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		KeyFactory privKeyFactory = KeyFactory.getInstance("RSA");
		return privKeyFactory.generatePrivate(privKeySpec);
	}
	
	public PublicKey genPubKeyFromStr(String pubKeyStr) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
		byte[] publicKeyBytes = new BASE64Decoder().decodeBuffer(pubKeyStr);
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		KeyFactory pubKeyFactory = KeyFactory.getInstance("RSA");
		return pubKeyFactory.generatePublic(pubKeySpec);
	}
	
	public String decodePublicKey(PublicKey publicKey){
		return new String(new BASE64Encoder().encode(publicKey.getEncoded()));
	}
}
