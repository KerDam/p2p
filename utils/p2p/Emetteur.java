package p2p;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Emetteur {
	
	private int port;
	private Communication com;

	public Emetteur(int portNumber, Communication com) {
		this.port = portNumber;
		this.com = com;		
		
	}
	
	/**
	 * Méthode qui envoie un message 
	 * @param message message que l'on envoie au pair
	 * @param ip du pair destinataire
	 */
	public void sendTo(String message, String ip) {
		try {
			Socket sock = new Socket(ip, this.port);
			OutputStream fluxOut = sock.getOutputStream();
			PrintWriter out = new PrintWriter(fluxOut, true);
			out.println(message);
 
			
		} catch (UnknownHostException e) {			
			e.printStackTrace();
			System.err.println("Hôte introuvable.");
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

}
