package larry.IoTServer;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;

public class Whitelist {
	
	public String[] clientID;
	public Key[] clientPublicKey;
	
	public Whitelist(String[] id, String[] key){
		try{
			for(int i=0; i<id.length; i++){
				this.clientID[i] = id[i];
				final byte[] bytesKey = new BASE64Decoder().decodeBuffer(key[i]);
				this.clientPublicKey[i] = new SecretKeySpec(bytesKey, 0, bytesKey.length, "RSA");
			}
		}catch(Exception e){System.out.println(e);}
	}
}
