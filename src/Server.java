import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;

import javax.crypto.*;

public class Server {
	static String requestBuffer = " ";
	static String responseBuffer = " ";
	final static Object requestMonitor = new Object();
	final static Object responseMonitor = new Object();
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException{
	
		Thread serverIoT = new ServerIoT();
		
		serverIoT.start();
		
		InetAddress netIoTAddr = InetAddress.getByName("10.10.10.1");
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
	}
	/*Questa classe sta in ascolto di nuovi device.
	Ogni qualvolta arriva una richiesta inizia la cerimonia per l'autenticazione e lo scambio del segreto per generare la chiave di sessione*/
	static class ServerIoT extends Thread {
		
		final Whitelist W;
		final DoubleCipher c;
		final protected PrivateKey privateKey;
		final protected PublicKey publicKey;
		private ServerSocket listenerIoT;
		
		public ServerIoT() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
			
			//La chiavi sono fornite come stringhe, in seguito verranno trasformate in oggetti chiave.
			//Non è il modo più elegante, ma è quello più rapido da implementare.

			//Di seguito abbiamo le chiavi e gli identificatori dei device che saranno salvati nella whitelist. Le chiavi sono state generate con RSA 1024.
			String[] key = {"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbmFLg7rLZEdr7CHBe3j3k8V7PD/z8jXYh1XtIuJaARhHouc4XyLdY8gGx1FtODN7E9K2XYTm9CN01uss4wSTfN3vpgnl0m3o9dwXYmhGpnV7/rss0ay1Aknn6/cgocShw++zNt1GVWAXAvNGzPhcPYqvo+jh1pVjraLZORHQTAQIDAQAB","MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGNymNF3th2fsrCYJ6bbndf+YibapiIYV/Jx55Mkegm+EsBQOPmTjh5pXgoUqFuNR8co15beTVxK17OcQ4BTBzU7ui8ssUeCRvozYHQWlMFgnSztzi6+yH740UVlCVL7USochLLQRkgMJ4oCAhXT1sSOSGhoiW3bmlnj321FJY0QIDAQAB"};
			String[] ID = {"ALARM","LIGHT"};
			W = new Whitelist(ID, key);

			//Di seguito abbiamo chiave pubblica e privata del server (router), sono generate con RSA 2048.			
			String privateKeyStr = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDcSu5zUOuayv2QyHa0YOs4LHzuv77a17x3dFGmXTHf5P9R+GZ1j/miBiKgAAADRbvcTWLTrIpa6jR+rn3Y6oQfuiIB2P4nAAuf6h3QhWr/5KJeP2bQmnfDMWXiARbK5SWVs4vyXh9VWK2YQ1jivNy8eyv1x0gybyiK1Ux7gzIzNAxDPdWhbttbUfRNYQUkvloMzNZVl62ss45n6Zk7n03kXCoVCESxSuxA4cxxuySikuokuT1O5mFjhS8lR5NS8d7d2mUwV73mCulWYBjqpOdU8Wt4jfnSbTePaROmAK+FWtEO2RTupXiMahx/3gRtOcgR1fbpgbOLnHJUWiTAF/0nAgMBAAECggEBAJsHXKHou7XWzvWaLfepEUAaBt3aMQ0/r9F9GYzznwrK1GeyYSCw7uP4X1usRFYJODjEeR0wGrn6gnD4iXYjLFJLom0ccVeu739duheqHO/yzdV31vpNCqViWSngzFKDSpz8mys40+IzIw+5rY77jHhUUk6ILSXZFnYFv+pYHPbshl3/ZUvTaBvcdFI1SXstaPsNJuazPXUGYMrS8GE9z1lmflFzD4LIRhk9r44Oq42AZ3NAm9l5sOym5ETfV1SYK1qALGOs5k1igzt8UwzsMu5xdfzKSIinXwC5iaidEbkbdi/hd2TzD/BrNRB+HG2XdGfMZU4J6c0ZtNivhlnzS3ECgYEA+aqd8A6Zj7wiATCh8Ttueuu9btzSTds7Jpz23oEppInGwVRDONzaNnSZSA1RRx1b08dnKZN3mPBz07lsRpm34qs9nkHeo/HXQLiY8HXJEWjkAM0XouzIh4cJjIvLTp0XqoRQYTDMDa0R+vIk4pfEL//a1BKHfDZJsClTYyvbxMkCgYEA4eGOMyRnwof891rWeET9/O7yzsOWQ+zHwKVa9pt/SeBjPokLgvJrnBtbyj+Is37mGTxxqNaBcpAZOnOzqsu2fFuUHmS4xzb/uq2T1YSN8z97RxPsYfLcH6CYh8udAErRarH9XQtRFAqdtf7TvgTLsI13eCZ7tRaGvYO0344nWm8CgYAaH0DhIhXtFQkVyMnY3JgFom6yj8YtQTfiJWArDaaWwZebGL7jzy/BrNdw03vije7SrLVdg0FrLhVgXLE4fdLgk3wh99BcpOwZqHbQCfLcsHXSo6LKeC3PCFD2dJX0Brm4LJngYWOoNqh/8h/r2Wc8gm+Z6spkM6XoGZrQ8IDRCQKBgDgTlQfhS9khhY2rAX+OMTfv4luudsmaKOUMqA59ABbJHBpUT4VMxaZVrG4DY96b7Gfrd5Qg3LYiTAEU5E/MjxtEaPs6qhza7JXdrcveQpdW9TXqHk8ayccYSYW86Nc/u8mvMmU8//3nvMuH3e1Yp3DHTheIBkIou36lCexOEDsFAoGBAIomJlZ5YkIxHGW7fLtUG0AcsFDh2k982IzvAUeicvmkQ3a2FQ1eQMQebB65Y9ggpJZYK95twzmDKcYjWXqMaZB97AQ/ARpoIFcPjxl6fx8ivpC1L5b02hnBs0Co/ts9QjZQ7BxxFaRjegI8vHPYlP1tg/Mf/QPrCfo4e/t4zLUm";

			String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3Eruc1Drmsr9kMh2tGDrOCx87r++2te8d3RRpl0x3+T/UfhmdY/5ogYioAAAA0W73E1i06yKWuo0fq592OqEH7oiAdj+JwALn+od0IVq/+SiXj9m0Jp3wzFl4gEWyuUllbOL8l4fVVitmENY4rzcvHsr9cdIMm8oitVMe4MyMzQMQz3VoW7bW1H0TWEFJL5aDMzWVZetrLOOZ+mZO59N5FwqFQhEsUrsQOHMcbskopLqJLk9TuZhY4UvJUeTUvHe3dplMFe95grpVmAY6qTnVPFreI350m03j2kTpgCvhVrRDtkU7qV4jGocf94EbTnIEdX26YGzi5xyVFokwBf9JwIDAQAB";
			
			//L'oggetto DoubleCipher è una classe che implenta diversi metodi per la generazione delle chiavi e l'encrypt/decrypt.
			c = new DoubleCipher();
			privateKey = c.genPrivKeyFromStr(privateKeyStr);
			publicKey = c.genPubKeyFromStr(publicKeyStr);
			
			InetAddress netIoTAddr = InetAddress.getByName("127.0.1.1");
			listenerIoT = new ServerSocket(9999,0,netIoTAddr);
		}
		
		public void run(){
			
			while(true){
				try{
					
					Socket clientIoT = listenerIoT.accept();
				
					//Inizio della cerimonia
					//Il server si aspetta un messaggio che è possibile decifrare con le chiavi pubbliche della whitelist
					DataInputStream dIn = new DataInputStream(clientIoT.getInputStream());
					DataOutputStream dOut = new DataOutputStream(clientIoT.getOutputStream());
					
					//PASSO 1
					//Ricezione del messsaggio criptato come array di byte.
					//Dopo la ricezione viene eseguito un ridimensionamento dell'array, perchè la decryption deve ottenere in input il corretto numero di byte.
					byte [] encCliID = new byte[1024];
					int len = dIn.read(encCliID);
					encCliID = Arrays.copyOf(encCliID, len);

					/*Qui il server prova a decriptare il messaggio con le chiavi della whitelist, una alla volta.
					Se la chiave utilizzata non è corretta la decryption genera un eccezzione 
					che viene gestita dal try/catch per non fermare l'esecuzione del for*/
					int i;
						for(i=0; i<W.clientPublicKey.length; i++){
							try{
								String message = new String(c.decryptAsimmPub(W.clientPublicKey[i], encCliID, "RSA"));
								if(message.equals(W.clientID[i]))
									break;
							}
							catch(BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException e){
								continue;
							}
						}
						
						if(i>=W.clientID.length){
							System.out.println("\nUnknown IoT Device, closing connection.");
							clientIoT.close();
						}
					
						//PASSO 2
						//Il router manda la sua identita e il segreto per la chiave simmetrica tutto cifrato prima
						//con la chiave pubblica del client e poi con la sua chiave privata
						String seed = "INTERNET_SECURITY";
						final byte[] encGatMsg = c.doubleEncrypt(seed, W.clientPublicKey[i], privateKey);
						dOut.write(encGatMsg);
				
						//Gateway calcola la chiave simmetrica
						final Key simmKey = c.generateSimmKey(seed);
				
						//PASSO 3
						//Il client risponde con la sua chiave pubblica criptata con la chiave simmetrica
						byte[] encCliKey = new byte[1024];
						len = dIn.read(encCliKey);
						encCliKey = Arrays.copyOf(encCliKey, len);
						String cliKeyStr = new String(c.decryptSimm(simmKey, encCliKey, "AES"));
						
						//Controllo sul contenuto del messaggio. Se la chiave di sessione è corretta, ma la chiave pubblica contenuta nel messaggio
						//non è la stessa la cerimonia viene interrotta.
						//Viene interrotta l'esecuzione del thread, perchè la chiave potrebbe essere stata compromessa		
						if(!(cliKeyStr.equals(c.decodePublicKey(W.clientPublicKey[i])))){
							System.out.println("\n"+cliKeyStr+"\n");
							System.out.println("\n"+c.decodePublicKey(W.clientPublicKey[i])+"\n");
							clientIoT.close();
							break;
						}
						
						//Il device è stato autenticato con successo, viene lanciato un thread che si occuperà dello scambio dei messaggi
						//utilizzando la chiave di sessione.
						System.out.println("\n"+W.clientID[i]+" IoT client authenticated!");
						Thread t = new IoTManager(W.clientID[i], clientIoT, simmKey);		
						t.start();
					
				}
				catch(Exception e){
				}
			}
		}
	}
	
	//Questa classe si occupa di scambiare messaggi con il device già autenticato usando la chiave simmetrica generata durante il protocollo.
	//Le richieste arrivano da un client remote e vengono passate a questa classe tramite buffer condivisi, per questo è stato necessario implementare dei monitor
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
						if(requestBuffer.equals(" ") || !(requestBuffer.substring(0,5).equals(currentThread().getName())))
							requestMonitor.wait();
		
						else{

							String request = requestBuffer;
							requestBuffer = " ";
							final byte[] encMsg = c.encryptSimm(simmKey, request.substring(6).getBytes(), "AES");
							dOut.write(encMsg);
							byte[] encRcv = new byte[1024];
							int len = dIn.read(encRcv);
							encRcv = Arrays.copyOf(encRcv, len);
						
							synchronized(responseMonitor){

								responseBuffer = new String(c.decryptSimm(simmKey, encRcv, "AES"));
								responseMonitor.notifyAll();
							}
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				try {
					cliSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	//La seguente classe si occuppa di scambiare messaggi con i client remoti, 
	//ogni volta che riceve una richiesta dal client, la inserisce nel buffer condiviso e risveglia i thread che gestiscono i device.
	//il thread chiama la wait sul buffer di risposta. 
	//Appena i gestori dei device ottengono la risposta, la mettono nel buffer condiviso e notificano i gestori dei client che si risvegliano.
	static class ClientManager extends Thread{
		
		private Socket client;
		
		public ClientManager(Socket cli){
			this.client = cli;
		}
		
		public void run(){
			
			try {
				BufferedReader dIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
				BufferedWriter dOut = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				
				while(true){
					String request = dIn.readLine();
					System.out.println(request);
					if(request.equals("QUIT")){
						client.close();
						System.out.println("Client Quitted");
						return;
					}
				
				
					synchronized (requestMonitor){
						requestBuffer = request;
						requestMonitor.notifyAll();
					}

					synchronized (responseMonitor){
						if(responseBuffer.equals(" "));
							responseMonitor.wait();
						dOut.write(responseBuffer);
						dOut.newLine();
						dOut.flush();
						responseBuffer = " ";
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				try {
					client.close();
				}
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}
