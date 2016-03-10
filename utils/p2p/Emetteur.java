package p2p;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Emetteur {
	
	

	private Communication com;

	public Emetteur(Communication com) {
		this.com = com;		
		
	}
	
	/**
	 * Méthode qui envoie un message 
	 * @param message message que l'on envoie au pair
	 * @param ip du pair destinataire
	 */
	public void sendTo(String message, String ip, int port) {
		try {
			Socket sock = new Socket(ip, port);
			OutputStream fluxOut = sock.getOutputStream();
			PrintWriter out = new PrintWriter(fluxOut, true);
			out.println(message);
			sock.close();
			
		} catch (UnknownHostException e) {			
			e.printStackTrace();
			System.err.println("Hôte introuvable.");
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * Méthode qui envoie un message au serveur de hash pour recevoir un hash
	 * yo:
	 * @param message message que l'on envoie au pair
	 * @param ip du pair destinataire
//	 */
//	public void sendToHashServeur() {
//		try {
//			Socket sock = new Socket(this.com.ipHash, 8001);
//			OutputStream fluxOut = sock.getOutputStream();
//			PrintWriter out = new PrintWriter(fluxOut, true);			
//			out.println("yo:");
//			
//			
//		} catch (UnknownHostException e) {			
//			e.printStackTrace();
//			System.err.println("Hôte introuvable.");
//		} catch (IOException e) {			
//			e.printStackTrace();
//		}
//	}

}
