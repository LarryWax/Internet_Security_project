import java.security.*;

public class Whitelist {
	
	protected String[] clientID = new String[100];
	protected PublicKey[] clientPublicKey = new PublicKey[100];
	private DoubleCipher c = new DoubleCipher();
	
	public Whitelist(String[] id, String[] key){
		try{
			for(int i=0; i<id.length; i++){
				this.clientID[i] = id[i];
				this.clientPublicKey[i] = c.genPubKeyFromStr(key[i]);
			}
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			}
	}
}
