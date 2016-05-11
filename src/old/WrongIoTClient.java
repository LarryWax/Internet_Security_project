
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;

public class WrongIoTClient {
	private static Socket s;
	public static void main(String[] args) throws IOException {
		
		try{
			DoubleCipher c = new DoubleCipher();
			final String gatKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAhpgoWBUEsKjck+n5+IjT73YkmOjZe+rSmIWOOz1rl1NtOXtmF4xOtsYkgaBgGaDPgu6DT6MGZ0A9eBQbZXf+grd/sLlc0ZfGns2oNgjzN17EGVzXYK5pdq1wOcvC/DTfNFstJqRf3yXunNaJQV9VwK0DEI07Lu2d2sth4yXK5x/bYEUwTAKfsfo7nvw5Yt3NzI7w7RobH3TwKbSkKyYWDHqsMcFlMXKerJz3eRNBt7wMRHP4ppdM1YgewJY+oLd1fqY3deEYKH7JuZpRkQvUpqMLrKQZ+1WFXFQhBOO+7ITe408ZFs8iFVENhXEpWI5GtANIFrcHQR1dzLVA6WMhhdoJLylZqu+87SmI/rcCXz7D3ye/fXSTOJ8Uq0wmZ3V9E37BLW5lU7vCpXrB+c6RgGdgnhLXrctBN2oJEamMwPVOo0s0crDCO2sRMsNUIxIZclyjUZQ3g3iw9FBO/j5owdeUpfgiQlpUoRGPdWwTJEQJz56J6c4+sQHdPNENdZjJQ6AODZmxqBzzQ0YkuDf1ZHSZ/p9ThhEZ+HKfKU9D44yynlDbO5xi5rv1rC0VwspqL2fDfsLQvzheqMxQONLd0tjT1o/sBggpKlgQbitaRZgkc8ZQVaDts83+dcUSOHK/1jBE239S+M/85MsBIoHyNVPVQPwJ6gPHH1kZ4PB58TECAwEAAQ==";
			final String privKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAILXk9HW9iIZbD99EENp5ekHIJSERMukyFLYfqSACS1c1pVnto075eoAsJouTYEjP5oIUho0EjFJdPp8d/wB2fQy3sk5C2nQO8phlnO5hFIIoSGpuNUmqj8id/uUzdb9wxPeqTkqaeg5+re9luYEnrJZnLZgw97hLL7NB9DE/tCfAgMBAAECgYB1j8d0osWyq88UWIW1XMBEGbBPYW0C4QNRVM6tdZFJXgI8adyfl/Mjxwzm67Wd05MBDnreqmyyNkNSEWYpW2x98dXrz8VEbcO+HSQv7lKD16LCTL/6W7pvpbJhxtppKnsHgMyfgxV2lqgkShq462qMwNGxBk7u+pOX5tXRBQsh4QJBAL5m8+WCGvTV6I/PtSVykMwSGVMfoW+Z/rNmGt3G3yAWPs8chLHz/EaUcsZ8gxvC6SoyAdGSk2GIADMnQ1QJEu8CQQCv649OuS+KyoEomRz8TVKuGUBlgk7OLOrNKoNFClm4DSeTgwR5RhIWDeLFw4PvxxQFT0yz9HAejtglFprlsV1RAkBexElCgzY9hDZDVeNYZyRQnfWC3Oxx6pjN29UlazVk+A188jnNhJ2c14gk6JYs/B8nREqJb8XxNkq7KyCks/t7AkAOAFGHknWWNkfRU15bNkw+TwQxEA31mt2VNqz78bbyTxm3Q9Y3ULxfxPsngkgd7VpKkeUo7Wt/Ax9w1Du/zD4BAkBOvFeJlzhHu1v1HySCfJ1sOmvwPPAw8Dr+N2ci3jmY4zis+7hgvLyWpO/kyUokP867kN8IebAmrac58npJ4A6j";

			final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCC15PR1vYiGWw/fRBDaeXpByCUhETLpMhS2H6kgAktXNaVZ7aNO+XqALCaLk2BIz+aCFIaNBIxSXT6fHf8Adn0Mt7JOQtp0DvKYZZzuYRSCKEhqbjVJqo/Inf7lM3W/cMT3qk5KmnoOfq3vZbmBJ6yWZy2YMPe4Sy+zQfQxP7QnwIDAQAB";
			final PublicKey publicKey = c.genPubKeyFromStr(pubKey);
			final PrivateKey privateKey = c.genPrivKeyFromStr(privKey);
			final PublicKey gatewayPublicKey = c.genPubKeyFromStr(gatKey);
			
			String name = args[0];
		
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
