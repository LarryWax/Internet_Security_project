package larry.IoTServer;

public class Server {
	String[] key = {"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzxx5FEM7achj13lNmwX1EQZO25dEOO5Q27YjaVFyoDe8ofgiu4NL0mRsE3WeMB83GafTqyUp1+ICr8mqkdJm0QalmAjfmda+cb0qTXH7yGBy09i0R6WwuOIusG8nQOgeg7x5gAOs6JLkciFKc10l0vsuD3vfSlYHl7pYDIItW40NkS8XKIOf6ZS5uFgWQ0QCTnI+naGZPkTqF1q09TijSBFObKVarWhsQbmi7paIAibPi4EcAmYunfZtHHcdckdtffr5kGHgmSLZdzriVHvqKlFWCbYOv4q7cPOBgOXCQEp8aKwNY7b/ZdB+gHCqxsEjMp9KQpEN5WGMKj4RbQk6awIDAQAB"};
	String[] ID = {"IOT"};
	Whitelist W = new Whitelist(ID, key);
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
