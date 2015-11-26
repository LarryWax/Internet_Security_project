package larry.IoTServer;
import java.io.*;
import java.net.Socket;
import java.security.*;

import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


import larry.crypto.*;

public class MultiThreadServer implements Runnable {

	protected Whitelist W;
	private Socket cliSocket;
	private DoubleCipher c;
	protected Key privateKey;
	protected Key publicKey;
	
	public MultiThreadServer(Socket cSocket, Whitelist w, Key priv, Key pub){
		this.cliSocket = cSocket;
		this.W = w;
		c = new DoubleCipher();
		this.privateKey = priv;
		this.publicKey = pub;
	}
	
	public void run(){
		try{
			//Gateway riceve l'identità del client cifrata con la chiave privata, la decodifica
			//con le chiavi pubbliche nella whitelist e autentica il client
			DataInputStream dIn = new DataInputStream(cliSocket.getInputStream());
			DataOutputStream dOut = new DataOutputStream(cliSocket.getOutputStream());
			byte [] encCliID = new byte[4096];
			dIn.read(encCliID);
			int i;
			for(i=0; i<W.clientPublicKey.length; i++){
				String message = new String(c.decrypt(W.clientPublicKey[i], encCliID, "RSA"));
				if(message == W.clientID[i])
					break;
			}
			//Gateway manda la sua identita e il segreto per la chiave simmetrica tutto cifrato prima
			//con la chiave pubblica del client e poi con la sua chiave privata
			String ID = "GATEWAY";
			String seed = "INTERNET_SECURITY_PROJECT";
			final byte[] encGatMsg = c.doubleEncrypt(ID, seed, W.clientPublicKey[i], privateKey);
			dOut.write(encGatMsg);
			
			//Gateway calcola la chiave simmetrica
			final Key simmKey = c.generateSimmKey(seed);
			
			//Il client risponde con la sua chiave pubblica criptata con la chiave simmetrica
			
			
			
			
			
			
			
			
			
			
		}catch(Exception e){
			System.out.println(e);
		}
	}
}
