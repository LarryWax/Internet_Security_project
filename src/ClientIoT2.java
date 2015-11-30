import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;

public class ClientIoT2 {
	private static Socket s;
	public static void main(String[] args) throws IOException {
		
		try{
			DoubleCipher c = new DoubleCipher();
			final String gatKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAhpgoWBUEsKjck+n5+IjT73YkmOjZe+rSmIWOOz1rl1NtOXtmF4xOtsYkgaBgGaDPgu6DT6MGZ0A9eBQbZXf+grd/sLlc0ZfGns2oNgjzN17EGVzXYK5pdq1wOcvC/DTfNFstJqRf3yXunNaJQV9VwK0DEI07Lu2d2sth4yXK5x/bYEUwTAKfsfo7nvw5Yt3NzI7w7RobH3TwKbSkKyYWDHqsMcFlMXKerJz3eRNBt7wMRHP4ppdM1YgewJY+oLd1fqY3deEYKH7JuZpRkQvUpqMLrKQZ+1WFXFQhBOO+7ITe408ZFs8iFVENhXEpWI5GtANIFrcHQR1dzLVA6WMhhdoJLylZqu+87SmI/rcCXz7D3ye/fXSTOJ8Uq0wmZ3V9E37BLW5lU7vCpXrB+c6RgGdgnhLXrctBN2oJEamMwPVOo0s0crDCO2sRMsNUIxIZclyjUZQ3g3iw9FBO/j5owdeUpfgiQlpUoRGPdWwTJEQJz56J6c4+sQHdPNENdZjJQ6AODZmxqBzzQ0YkuDf1ZHSZ/p9ThhEZ+HKfKU9D44yynlDbO5xi5rv1rC0VwspqL2fDfsLQvzheqMxQONLd0tjT1o/sBggpKlgQbitaRZgkc8ZQVaDts83+dcUSOHK/1jBE239S+M/85MsBIoHyNVPVQPwJ6gPHH1kZ4PB58TECAwEAAQ==";
			final String privKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIY3KY0Xe2HZ+ysJgnptud1/5iJtqmIhhX8nHnkyR6Cb4SwFA4+ZOOHmleChSoW41HxyjXlt5NXErXs5xDgFMHNTu6LyyxR4JG+jNgdBaUwWCdLO3OLr7IfvjRRWUJUvtRKhyEstBGSAwnigICFdPWxI5IaGiJbduaWePfbUUljRAgMBAAECgYEAheVgQY7Mm1kg1tWc4tC2nUAscepgEZgJm0mGgDhl9ter8wNpgwfEFLG6kie2teMnsKODekHlClI1E/sUQyMCKoMaTf3hPUzoi3EzDwTsQ82qksmb9KT5VUvtHXZ+KZNXngL7CbyOtPVxMNYqebh3wRwwO9/kgtteLJFFa72rr7ECQQDgcf/n5eSR/OJ9UZGk2W6t9Obl0uJwSyHh/JotzOvUE1tkTeDyKRhJ/yGkoBe2xzRXUdrR+a71+RN+njTaUlFVAkEAmRW0gUii7qfaevh4glWzdsFt55BdlRHS2i30Hb+Eh9RIgNUlcp2dWb1eFnIKSee0E8oLKztflFUIE5h8j+FZjQJAJUSIm3bexybYbLqYlmjMKfJaTJsBome3TpDlAOnf1q0IJdWQnmUYdWD5sx4qEd1t4Xnj/a2Z0Zn0FLgsSnwUqQJANvoEiSl82hiN7futy6DbgpqUzcSa1x4/ivtV4BkXj3A9WYcym6bfC4tmbsklyMTddv3oZO49tReUfvUURte3QQJAZrx8oFmEDplQvPcUrQJG7ajKyXb2qnwOigIed4Fn91sWzsCjLyuFVZj6P88RGRzxnAvbZRxSQZ+RZYfnJV+yHQ==";

			final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGNymNF3th2fsrCYJ6bbndf+YibapiIYV/Jx55Mkegm+EsBQOPmTjh5pXgoUqFuNR8co15beTVxK17OcQ4BTBzU7ui8ssUeCRvozYHQWlMFgnSztzi6+yH740UVlCVL7USochLLQRkgMJ4oCAhXT1sSOSGhoiW3bmlnj321FJY0QIDAQAB";
			final PublicKey publicKey = c.genPubKeyFromStr(pubKey);
			final PrivateKey privateKey = c.genPrivKeyFromStr(privKey);
			final PublicKey gatewayPublicKey = c.genPubKeyFromStr(gatKey);
			
			String name = "LIGHT";
		
			int value = (int)(Math.random()*100);
		
			s = new Socket("127.0.1.1",9999);
			DataInputStream dIn = new DataInputStream(s.getInputStream());
			DataOutputStream dOut = new DataOutputStream(s.getOutputStream());
		
			//Il client genera un messaggio con la sua identita cifrata con la sua chiave privata
			final byte[] encIdMsg = c.encryptAsimmPriv(privateKey, name.getBytes(), "RSA");
			dOut.write(encIdMsg);
		
			//Il client si aspetta come risposta un messaggio contenente l'ID del gateway e un seme
			//tutto cifrato prima con la chiave pubblica dello stesso client e poi con la chiave privata del Gateway
			byte[] dEncGatMsg = new byte[1024];
			int len = dIn.read(dEncGatMsg);
			dEncGatMsg = Arrays.copyOf(dEncGatMsg, len);
			String gatMsg = new String(c.doubleDecrypt(gatewayPublicKey, privateKey, dEncGatMsg));
			
			//Il client calcola la chiave
			final Key simmKey = c.generateSimmKey(gatMsg);
		
			//Il client conclude la cerimonia con messaggio di conferma(sua chiave pubblica cifrata con chiave simmetrica)
			final byte[] confMsg = c.encryptSimm(simmKey, c.decodePublicKey(publicKey).getBytes(), "AES");
			dOut.write(confMsg);
		
			//Cerimonia conclusa
			System.out.println(name+" authenticated with Gateway");
			

			while(true){
				byte[] encRequest = new byte[2048];
				len = dIn.read(encRequest);
				encRequest = Arrays.copyOf(encRequest, len);
			
				String request = new String(c.decryptSimm(simmKey, encRequest, "AES"));
				System.out.print("Ho ricevuto una richiesta ");
				if(request.equals("GET")){
					System.out.println(request);
					String response = "Value: "+ value;
					byte[] encResponse = c.encryptSimm(simmKey, response.getBytes(), "AES");
					dOut.write(encResponse);
				}
				else if(request.substring(0,3).equals("SET")){
					System.out.println(request);
					value = (int)Integer.parseInt(request.substring(4));
					String response = "Value: "+ value;
					byte[] encResponse = c.encryptSimm(simmKey, response.getBytes(), "AES");
					dOut.write(encResponse);
					}
				else{
					System.out.println(request);
					String response = "UNKNOWN";
					byte[] encResponse = c.encryptSimm(simmKey, response.getBytes(), "AES");
					dOut.write(encResponse);
					}
			}
		}catch(Exception e){
			e.printStackTrace();
			s.close();
		}
	}

}
