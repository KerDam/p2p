import p2p.Communication;
import p2p.Pair;



public class Main {


	public static void main(String[] args) {
		/* Test de pair avec le serveur welcom */
		Pair pair = new Pair();
//		Communication com = new Communication(pair);
//		WelcomeServer ws = new WelcomeServer();
		pair.communication.send("yo:5:192.168.18.21", "localhost", 8000);
//		com.askHash();
		/* Test de connexion avec serveur hash et welcome*/
		
		
	}

}
