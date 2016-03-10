package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.sun.java_cup.internal.runtime.Scanner;

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
		System.out.println("port:"+port +"   ip:"+ip);
		try {
			Socket s = new Socket(ip, port);
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		    System.out.println("e  ->- "+message);
		    out.println(message);
			String inputLine;
			inputLine = in.readLine();
//			while ((inputLine = in.readLine()) == null) {
//			    // Si l'ip est bien reçue et correcte (i.e. le serveur n'a pas répondu « ukh » ou « wrq »)
//			    // On l'enregistre pour la retourner
//			    //System.out.println("waiting");
//			} 
		    
		    this.com.receptMes(inputLine);
		    out.close();
		    in.close();
			s.close();
			
		} catch (UnknownHostException e) {			
			e.printStackTrace();
			System.err.println("Hôte introuvable.");
		} catch (ConnectException e) {
			e.printStackTrace();
			System.err.println("Connexion refusée");
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
