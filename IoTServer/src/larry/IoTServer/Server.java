package larry.IoTServer;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;

import larry.crypto.DoubleCipher;

public class Server {
	static String requestBuffer = " ";
	static String responseBuffer = " ";
	final static Object requestMonitor = new Object();
	final static Object responseMonitor = new Object();
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException{
		// TODO Auto-generated method stub
	
		Thread serverIoT = new ServerIoT();
		//Thread netServer = new NetServer(messageBuffer);
		
		serverIoT.start();
		//netServer.start();
		//System.out.println("Sto andando a dormire");
		//Thread.sleep(10000);
		//System.out.println("Mi sono svegliato");
		
		InetAddress netIoTAddr = InetAddress.getByName("127.0.0.1");
		ServerSocket listener = new ServerSocket(9999,0,netIoTAddr);
		System.out.print("System initialized.");
		
		while(true){
			Socket client;
			try {
				client = listener.accept();
				System.out.println("New client connected to system.");
				Thread clientManager = new ClientManager(client);
				clientManager.start();
			} catch (IOException e) {
				e.printStackTrace();
				listener.close();
			}
		}
		
		/*synchronized(requestMonitor){
			requestBuffer = "ALARM GET";
			requestMonitor.notifyAll();
		}
		
		synchronized(responseMonitor){
			if(responseBuffer == " ")
				responseMonitor.wait();
			System.out.println(responseBuffer);
			responseBuffer = " ";
			responseMonitor.notify();
		}*/
		
	}
	
	static class ServerIoT extends Thread {
		
		final Whitelist W;
		final DoubleCipher c;
		final protected PrivateKey privateKey;
		final protected PublicKey publicKey;
		private ServerSocket listenerIoT;
		
		public ServerIoT() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
			
			String[] key = {"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbmFLg7rLZEdr7CHBe3j3k8V7PD/z8jXYh1XtIuJaARhHouc4XyLdY8gGx1FtODN7E9K2XYTm9CN01uss4wSTfN3vpgnl0m3o9dwXYmhGpnV7/rss0ay1Aknn6/cgocShw++zNt1GVWAXAvNGzPhcPYqvo+jh1pVjraLZORHQTAQIDAQAB"};
			String[] ID = {"ALARM"};
			W = new Whitelist(ID, key);
			
			String privateKeyStr = "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";
			String publicKeyStr = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAhpgoWBUEsKjck+n5+IjT73YkmOjZe+rSmIWOOz1rl1NtOXtmF4xOtsYkgaBgGaDPgu6DT6MGZ0A9eBQbZXf+grd/sLlc0ZfGns2oNgjzN17EGVzXYK5pdq1wOcvC/DTfNFstJqRf3yXunNaJQV9VwK0DEI07Lu2d2sth4yXK5x/bYEUwTAKfsfo7nvw5Yt3NzI7w7RobH3TwKbSkKyYWDHqsMcFlMXKerJz3eRNBt7wMRHP4ppdM1YgewJY+oLd1fqY3deEYKH7JuZpRkQvUpqMLrKQZ+1WFXFQhBOO+7ITe408ZFs8iFVENhXEpWI5GtANIFrcHQR1dzLVA6WMhhdoJLylZqu+87SmI/rcCXz7D3ye/fXSTOJ8Uq0wmZ3V9E37BLW5lU7vCpXrB+c6RgGdgnhLXrctBN2oJEamMwPVOo0s0crDCO2sRMsNUIxIZclyjUZQ3g3iw9FBO/j5owdeUpfgiQlpUoRGPdWwTJEQJz56J6c4+sQHdPNENdZjJQ6AODZmxqBzzQ0YkuDf1ZHSZ/p9ThhEZ+HKfKU9D44yynlDbO5xi5rv1rC0VwspqL2fDfsLQvzheqMxQONLd0tjT1o/sBggpKlgQbitaRZgkc8ZQVaDts83+dcUSOHK/1jBE239S+M/85MsBIoHyNVPVQPwJ6gPHH1kZ4PB58TECAwEAAQ==";
			
			c = new DoubleCipher();
			privateKey = c.genPrivKeyFromStr(privateKeyStr);
			publicKey = c.genPubKeyFromStr(publicKeyStr);
			
			InetAddress netIoTAddr = InetAddress.getByName("127.0.1.1");
			listenerIoT = new ServerSocket(9999,0,netIoTAddr);
		}
		
		public void run(){
			try{
				while(true){
			
					//Listen for someone
					Socket clientIoT = listenerIoT.accept();
				
					//Inizio della cerimonia
					//Il server si aspetta un messaggio che è possibile decifrare con le chiavi pubbliche della whitelist
					DataInputStream dIn = new DataInputStream(clientIoT.getInputStream());
					DataOutputStream dOut = new DataOutputStream(clientIoT.getOutputStream());
				
					byte [] encCliID = new byte[1024];
					int len = dIn.read(encCliID);
					encCliID = Arrays.copyOf(encCliID, len);
					int i;
					for(i=0; i<W.clientPublicKey.length; i++){
						String message = new String(c.decryptAsimmPub(W.clientPublicKey[i], encCliID, "RSA"));
						if(message.equals(W.clientID[i]))
							break;
					}
					if(i==W.clientPublicKey.length){
						clientIoT.close();
						break;
					}
				
					//Gateway manda la sua identita e il segreto per la chiave simmetrica tutto cifrato prima
					//con la chiave pubblica del client e poi con la sua chiave privata
					String seed = "INTERNET_SECURITY";
					final byte[] encGatMsg = c.doubleEncrypt(seed, W.clientPublicKey[i], privateKey);
					dOut.write(encGatMsg);
				
					//Gateway calcola la chiave simmetrica
					final Key simmKey = c.generateSimmKey(seed);
				
					//Il client risponde con la sua chiave pubblica criptata con la chiave simmetrica
					byte[] encCliKey = new byte[1024];
					len = dIn.read(encCliKey);
					encCliKey = Arrays.copyOf(encCliKey, len);
					String cliKeyStr = new String(c.decryptSimm(simmKey, encCliKey, "AES"));
					
					if(!(cliKeyStr.equals(c.decodePublicKey(W.clientPublicKey[i])))){
						System.out.println("\n"+cliKeyStr+"\n");
						System.out.println("\n"+c.decodePublicKey(W.clientPublicKey[i])+"\n");
						clientIoT.close();
						break;
					}
				
					System.out.println("IoT client authenticated!");
					Thread t = new IoTManager(W.clientID[i], clientIoT, simmKey);		
					t.start();
				}
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
				try {
					listenerIoT.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					}
			}
		}
	}
	
	static class IoTManager extends Thread {

		private Socket cliSocket;
		private DoubleCipher c;
		protected Key simmKey;
		
		public IoTManager(String name, Socket cSocket, Key simmetric){
			super(name);
			this.cliSocket = cSocket;
			c = new DoubleCipher();
			this.simmKey = simmetric;
		}
		
		public void run(){
			try{
				DataInputStream dIn = new DataInputStream(cliSocket.getInputStream());
				DataOutputStream dOut = new DataOutputStream(cliSocket.getOutputStream());
				while(true){
					synchronized (requestMonitor){
						if(requestBuffer.equals(" ") || !(requestBuffer.substring(0,5).equals(currentThread().getName()))){
							requestMonitor.wait();
						}
						else{
							System.out.println("Il buffer non è piu vuoto");
							String request = requestBuffer;
							requestBuffer = " ";
							requestMonitor.notify();
							//dovrei liberare requestBuffer
									
							//dovrei prendere il lock di response buffer
							synchronized(responseMonitor){
								final byte[] encMsg = c.encryptSimm(simmKey, request.substring(6).getBytes(), "AES");
								//System.out.println(request.substring(5+1, request.length()-1));
								System.out.println(request.substring(6));
								dOut.write(encMsg);
							
								byte[] encRcv = new byte[1024];
								int len = dIn.read(encRcv);
								encRcv = Arrays.copyOf(encRcv, len);
								responseBuffer = new String(c.decryptSimm(simmKey, encRcv, "AES"));
								System.out.println(responseBuffer);
								responseMonitor.notify();
							}
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				try {
					cliSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	static class ClientManager extends Thread{
		
		private Socket client;
		
		public ClientManager(Socket cli){
			this.client = cli;
		}
		
		public void run(){
			
			try {
				DataInputStream dIn = new DataInputStream(client.getInputStream());
				DataOutputStream dOut = new DataOutputStream(client.getOutputStream());
				
				byte[] req = new byte[4096];
				int len = dIn.read(req);
				Arrays.copyOf(req, len);
				String request = new String(req);
				System.out.println(request);
				if(request.equals("QUIT")){
					client.close();
					return;
				}
				
				synchronized (requestMonitor){
					requestBuffer = request;
					requestMonitor.notifyAll();
				}
				
				synchronized (responseMonitor){
					if(responseBuffer.equals(" ")){
						responseMonitor.wait();
					}
					else{
						dOut.write(responseBuffer.getBytes());
						responseBuffer = " ";
						responseMonitor.wait();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
		
	}
}
