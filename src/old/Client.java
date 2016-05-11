import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		
		Socket s = new Socket("10.10.10.1",9999);
		BufferedReader dIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
		BufferedWriter dOut = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Connected to IoT System!");
		System.out.println("We currently support only 'ALARM' device\nCommand supported: 'GET' 'SET value' 'QUIT'");
		while(true){
			System.out.print(">>");
			String request = stdIn.readLine();
			//String request = "ALARM GET";
			dOut.write(request);
			dOut.newLine();
			dOut.flush();
			if(request.equals("QUIT")){
				System.out.println("Goodbye!");
				break;
			}
			String response = dIn.readLine();
			System.out.println("<<"+response);
		}
		s.close();

	}

}
