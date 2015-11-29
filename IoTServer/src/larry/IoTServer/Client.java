package larry.IoTServer;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		
		Socket s = new Socket("127.0.0.1",9999);
		DataInputStream dIn = new DataInputStream(s.getInputStream());
		DataOutputStream dOut = new DataOutputStream(s.getOutputStream());
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Connected to IoT System!");
		System.out.println("We currently support only 'ALARM' device\nCommand supported: 'GET' 'SET' 'QUIT'");
		while(true){
			//String request = stdIn.readLine();
			String request = "ALARM GET";
			dOut.write(request.getBytes());
			if(request.equals("QUIT")){
				System.out.println("Goodbye!");
				break;
			}
			byte[] res = new byte[4096];
			int len = dIn.read(res);
			Arrays.copyOf(res, len);
			String response = new String(res);
			System.out.println("<<"+response);
		}
		s.close();

	}

}
