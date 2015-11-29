import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;

public class ClientIoT {
	private static Socket s;
	public static void main(String[] args) throws IOException {
		
		try{
			DoubleCipher c = new DoubleCipher();
			final String gatKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAhpgoWBUEsKjck+n5+IjT73YkmOjZe+rSmIWOOz1rl1NtOXtmF4xOtsYkgaBgGaDPgu6DT6MGZ0A9eBQbZXf+grd/sLlc0ZfGns2oNgjzN17EGVzXYK5pdq1wOcvC/DTfNFstJqRf3yXunNaJQV9VwK0DEI07Lu2d2sth4yXK5x/bYEUwTAKfsfo7nvw5Yt3NzI7w7RobH3TwKbSkKyYWDHqsMcFlMXKerJz3eRNBt7wMRHP4ppdM1YgewJY+oLd1fqY3deEYKH7JuZpRkQvUpqMLrKQZ+1WFXFQhBOO+7ITe408ZFs8iFVENhXEpWI5GtANIFrcHQR1dzLVA6WMhhdoJLylZqu+87SmI/rcCXz7D3ye/fXSTOJ8Uq0wmZ3V9E37BLW5lU7vCpXrB+c6RgGdgnhLXrctBN2oJEamMwPVOo0s0crDCO2sRMsNUIxIZclyjUZQ3g3iw9FBO/j5owdeUpfgiQlpUoRGPdWwTJEQJz56J6c4+sQHdPNENdZjJQ6AODZmxqBzzQ0YkuDf1ZHSZ/p9ThhEZ+HKfKU9D44yynlDbO5xi5rv1rC0VwspqL2fDfsLQvzheqMxQONLd0tjT1o/sBggpKlgQbitaRZgkc8ZQVaDts83+dcUSOHK/1jBE239S+M/85MsBIoHyNVPVQPwJ6gPHH1kZ4PB58TECAwEAAQ==";
			final String privKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJuYUuDustkR2vsIcF7ePeTxXs8P/PyNdiHVe0i4loBGEei5zhfIt1jyAbHUW04M3sT0rZdhOb0I3TW6yzjBJN83e+mCeXSbej13BdiaEamdXv+uyzRrLUCSefr9yChxKHD77M23UZVYBcC80bM+Fw9iq+j6OHWlWOtotk5EdBMBAgMBAAECgYBC0q51b85br3FucndgJu5KDud64hDyngZoDXk/CBeKy40ReArs6ATmLDlSGDOGV7ystbB/3P+hEdd/JdP1ce3VSNdAqXFU4tt6Iduk5QsJEgVbzLf+V1O7wd0mqNuoBjVl0O1a6RxbVF60Omz/wCIaRVETCSLOV8DxvKQIJS0oSQJBAPRnapB9zUKr9xXIPjP5rfjhwBhFqGE342KN/uGlO0NMSNhAwokY7t+bPqZh/osoi0nD54ZJjRxl1vcPxeNGK9cCQQCi+jbwgnaTUozELsn2LyOP1rZyROe+xmKl6uzLCefw/6Wd8LvdOVsQCV0PGCIpSGS0hFEJNXm2DRjw3H90gRznAkBiFE5TheilZNVXTHCJ8xy5z/6CvxF/UipSFqG+c8yAiVCCWBq5YeQan/ZYVuMLfU3IYHG9Fi8mwkeuBgpn1nUpAkA2RPmMZvUTI5bqMOZhitrdp9LDBDzjIu5wb2UAS0En2AkPKb108gdntKZ/QzsE4i1eivztAxiPTZczhUkPw3qbAkEAzhzqCVus9rcxMofXX2EFDcF0LA+saChuOK3AbSUSM2kI8v8Hz1sPmJwjjFoWq+mxbe6K1rQrxKgHLiVD9C6jpw==";
			final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbmFLg7rLZEdr7CHBe3j3k8V7PD/z8jXYh1XtIuJaARhHouc4XyLdY8gGx1FtODN7E9K2XYTm9CN01uss4wSTfN3vpgnl0m3o9dwXYmhGpnV7/rss0ay1Aknn6/cgocShw++zNt1GVWAXAvNGzPhcPYqvo+jh1pVjraLZORHQTAQIDAQAB";
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
