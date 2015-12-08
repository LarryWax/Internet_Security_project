import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;

public class ClientIoT {
	private static Socket s;
	public static void main(String[] args) throws IOException {
		
		try{
			DoubleCipher c = new DoubleCipher();
			final String gatKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3Eruc1Drmsr9kMh2tGDrOCx87r++2te8d3RRpl0x3+T/UfhmdY/5ogYioAAAA0W73E1i06yKWuo0fq592OqEH7oiAdj+JwALn+od0IVq/+SiXj9m0Jp3wzFl4gEWyuUllbOL8l4fVVitmENY4rzcvHsr9cdIMm8oitVMe4MyMzQMQz3VoW7bW1H0TWEFJL5aDMzWVZetrLOOZ+mZO59N5FwqFQhEsUrsQOHMcbskopLqJLk9TuZhY4UvJUeTUvHe3dplMFe95grpVmAY6qTnVPFreI350m03j2kTpgCvhVrRDtkU7qV4jGocf94EbTnIEdX26YGzi5xyVFokwBf9JwIDAQAB";

			final String privKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJuYUuDustkR2vsIcF7ePeTxXs8P/PyNdiHVe0i4loBGEei5zhfIt1jyAbHUW04M3sT0rZdhOb0I3TW6yzjBJN83e+mCeXSbej13BdiaEamdXv+uyzRrLUCSefr9yChxKHD77M23UZVYBcC80bM+Fw9iq+j6OHWlWOtotk5EdBMBAgMBAAECgYBC0q51b85br3FucndgJu5KDud64hDyngZoDXk/CBeKy40ReArs6ATmLDlSGDOGV7ystbB/3P+hEdd/JdP1ce3VSNdAqXFU4tt6Iduk5QsJEgVbzLf+V1O7wd0mqNuoBjVl0O1a6RxbVF60Omz/wCIaRVETCSLOV8DxvKQIJS0oSQJBAPRnapB9zUKr9xXIPjP5rfjhwBhFqGE342KN/uGlO0NMSNhAwokY7t+bPqZh/osoi0nD54ZJjRxl1vcPxeNGK9cCQQCi+jbwgnaTUozELsn2LyOP1rZyROe+xmKl6uzLCefw/6Wd8LvdOVsQCV0PGCIpSGS0hFEJNXm2DRjw3H90gRznAkBiFE5TheilZNVXTHCJ8xy5z/6CvxF/UipSFqG+c8yAiVCCWBq5YeQan/ZYVuMLfU3IYHG9Fi8mwkeuBgpn1nUpAkA2RPmMZvUTI5bqMOZhitrdp9LDBDzjIu5wb2UAS0En2AkPKb108gdntKZ/QzsE4i1eivztAxiPTZczhUkPw3qbAkEAzhzqCVus9rcxMofXX2EFDcF0LA+saChuOK3AbSUSM2kI8v8Hz1sPmJwjjFoWq+mxbe6K1rQrxKgHLiVD9C6jpw==";

			final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbmFLg7rLZEdr7CHBe3j3k8V7PD/z8jXYh1XtIuJaARhHouc4XyLdY8gGx1FtODN7E9K2XYTm9CN01uss4wSTfN3vpgnl0m3o9dwXYmhGpnV7/rss0ay1Aknn6/cgocShw++zNt1GVWAXAvNGzPhcPYqvo+jh1pVjraLZORHQTAQIDAQAB";

			final PublicKey publicKey = c.genPubKeyFromStr(pubKey);
			final PrivateKey privateKey = c.genPrivKeyFromStr(privKey);
			final PublicKey gatewayPublicKey = c.genPubKeyFromStr(gatKey);
			
			String name = "ALARM";
		
			int value = (int)(Math.random()*100);
		
			s = new Socket("127.0.1.1",9999);
			DataInputStream dIn = new DataInputStream(s.getInputStream());
			DataOutputStream dOut = new DataOutputStream(s.getOutputStream());
		
			//PASSO 1			
			//Dopo aver creato la connessione il device genera un messaggio con la sua identita, cifrata con la sua chiave privata.
			//Il messaggio viene inviato al router.
			final byte[] encIdMsg = c.encryptAsimmPriv(privateKey, name.getBytes(), "RSA");
			dOut.write(encIdMsg);
		
			//PASSO 2
			//Il device riceve una risposta dal router. Il messaggio conterrà il seme per la generazione della chiave di sessione.
			//Il device decripta con chiave pubblica del router e poi con la sua chiave privata.
			byte[] dEncGatMsg = new byte[1024];
			int len = dIn.read(dEncGatMsg);
			dEncGatMsg = Arrays.copyOf(dEncGatMsg, len);
			String gatMsg = new String(c.doubleDecrypt(gatewayPublicKey, privateKey, dEncGatMsg));
			
			//Il client calcola la chiave
			final Key simmKey = c.generateSimmKey(gatMsg);
		
			//PASSO 3			
			//Il device conclude la cerimonia con messaggio di conferma(sua chiave pubblica cifrata con chiave simmetrica)
			final byte[] confMsg = c.encryptSimm(simmKey, c.decodePublicKey(publicKey).getBytes(), "AES");
			dOut.write(confMsg);
		
			//Cerimonia conclusa
			System.out.println(name+" authenticated with Gateway");
			
			//Da questo momento in poi il device comunicherà con la classe IoTManager utilizzando la chiave di sessione.
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
