package p2p;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;


public class Communication {
	
	private Recepteur recepteurMoniteur;
	private Recepteur recepteurPair;
	
	private Emetteur emetteur;
	
	private LinkedList<String> messages;

	public Communication() {
		this.recepteurMoniteur = new Recepteur(8002,this);
		this.recepteurPair = new Recepteur(8000,this);
		
		this.emetteur = new Emetteur(8000,this);
		
		// Lancer les thread
		Thread thRecepteurMoniteur = new Thread(this.recepteurMoniteur);
		Thread thRecepteurPair = new Thread(this.recepteurPair);
		
		thRecepteurMoniteur.start();
		thRecepteurPair.start();
		
	}
	
//	public void send(String message, String ip){
//		try {
//			Socket socketClient = new Socket(ip, 8080);
//			OutputStream outputStream = socketClient.getOutputStream();
//			PrintWriter writer = new PrintWriter(outputStream, true);
//			writer.println(message);
//			writer.close();
//			socketClient.close();
//			outputStream.close();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	
	
	/**
	 * Méthode qui traite les messages reçu des recepteurs.
	 * Message possibles:
	 * - con:Hash:IP -> message envoyée du nouveau pair, vers un autre
	 * - conAccept:IP_precedent:Hash_precedent:IP_suivant:Hash_suivant -> message renvoyé d'un pair au nouveau
	 * - setSucc:IP:Hash -> demande de mise à jour du nouveau pair à son precedent
	 * - setPre:IP:Hash ->demande de mise à jour du nouveau pair à son suivant
	 * @return message Un message
	 */
	public void receptionMessage() {
		
		
		while ( !this.messages.isEmpty() ) {
			String mes = this.messages.pop();
			String sep = ":";
			
			String[] action = mes.split(sep);
			switch (action[0]) {
			
			/*Message de pair à pair */
				case "con":				
				break;
				
				case "conAccept":
				break;
				
				case "setSucc":
				break;					
				
				case "setPre":			
				break;				
				
				
			/*Message du moniteur */				
				case "ip?:":	// Demande d'ip		
				break;
				
				case "ukh":			
				break;
					
				case "wrq":			
				break;
				
				case "rt?":		//Demande de table de routage	
					break;
				
				case "oups":			
				break;
					
				case "end":			
				break;
				
			/* Sinon */
				default:
				break;
			}
		}
		
		
		
	}
	
	public void receptMes(String s) {
		this.messages.addLast(s);
	}

	/**
	 * Méthode qui envoie un message à un autre paire
	 * @param message commande que l'envoie au pair
	 * @param ip du pair destinataire
	 */
	public void send(String message, String ip) {
		this.emetteur.sendTo(message, ip);
		
	}
}
