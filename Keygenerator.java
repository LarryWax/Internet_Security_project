import java.security.*;
import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Keygenerator{

	public static void main(String[] args){
		try{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.genKeyPair();
		Key publicKeyClient = kp.getPublic();
		Key privateKeyClient = kp.getPrivate();
		kpg.initialize(4096);
		KeyPair kp2 = kpg.genKeyPair();
		Key publicKeyServer = kp2.getPublic();
		Key privateKeyServer = kp2.getPrivate();
		File pbk = new File("./public.key");
		FileWriter fos = new FileWriter(pbk);
		byte[] publicKeyBytes = publicKeyClient.getEncoded();
		//Convert Public key to String
		BASE64Encoder encoder = new BASE64Encoder();
		String pubKeyStr = encoder.encode(publicKeyBytes);
		//System.out.println(pubKeyStr);
		fos.write(pubKeyStr);
		fos.flush();
		fos.close();
		}catch(Exception e){System.out.println(e);}
	}

}
