import larry.crypto.*;
import java.security.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Prova {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try{
			String text = "Succhiapalle";
			String secret = "suca";
			DoubleCipher util = new DoubleCipher();
			
			System.out.println("************** SIMMETRIC ENCODING *************");
			System.out.println("Plain text: "+text);
			
			Key simmKey = util.generateSimmKey(secret);
			final byte[] simmEncText = util.encrypt(simmKey, text.getBytes(), "AES");
			String simmEncPrint = new BASE64Encoder().encode(simmEncText);
			System.out.println("Encrypted text: " + simmEncPrint);
		
			final byte[] simmDecText = util.decrypt(simmKey, simmEncText, "AES");
			System.out.println("Decrypted text: "+ new String(simmDecText));


			System.out.println("************** ASIMMETRIC ENCODING *************");
		
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair kp = kpg.genKeyPair();
			Key publicKey1 = kp.getPublic();
			Key privateKey1 = kp.getPrivate();
			kpg.initialize(4096);
			KeyPair kp2 = kpg.genKeyPair();
			Key publicKey2 = kp2.getPublic();
			Key privateKey2 = kp2.getPrivate();
			//File pbk = new File("./public.key");
			//FileWriter writer = new FileWriter(pbk);
			//byte[] publicKeyBytes = publicKey.getEncoded();
			//Convert Public key to String
			//BASE64Encoder encoder = new BASE64Encoder();
			//String pubKeyStr = encoder.encode(publicKeyBytes);
			//System.out.println(pubKeyStr);
			//writer.write(pubKeyStr);
			//writer.flush();
			//writer.close();

      		final byte[] asimmEncText = util.encrypt(publicKey1, text.getBytes(), "RSA");
			System.out.println("Encripted text: " + asimmEncText);
			
			final byte[] asimmDecText = util.decrypt(privateKey1, asimmEncText, "RSA");
			System.out.println("Decrypted text: " + new String(asimmDecText));
			
			System.out.println("************** DOUBLE ENCODING *************");
			
			final byte[] encDoubMsg = util.doubleEncrypt(text, secret, publicKey1, privateKey2);
			System.out.println("Sono riuscito a criptare");
			String message = util.doubleDecrypt(publicKey2, privateKey1, encDoubMsg);
			int i;
			for(i=0; message.charAt(i)!= ','; i++);
			System.out.println("Double Encrypted Msg: "+ message.substring(0,i) +"\nDouble Encrypted seed: "+message.substring(i+1));
		}catch(Exception e){ System.out.println(e);}
	}

}
