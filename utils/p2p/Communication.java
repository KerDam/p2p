package p2p;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;


public class Communication {
	
	
	public static String ipHash = "localhost";
	public static String ipWelcome = "localhost";
	public static String ipMonitor = "localhost";
	public static int portPair = 9051;
	public static int portMoniteur = 8002;
	public static int portHash = 8001;
	public static int portWelcome = 8000;
	private Recepteur recepteurMoniteur;
	private Recepteur recepteurPair;
	private Recepteur recepteurHash;
	
	private Emetteur emetteur;
	private Pair pair;
	private LinkedList<String> messages;

	public Communication(Pair p) {
		this.recepteurPair = new Recepteur(portPair,this);
		this.pair = p;
		this.emetteur = new Emetteur(this);
		
		// Lancer les thread
		Thread thRecepteurPair = new Thread(this.recepteurPair);
		
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
					if (Integer.valueOf(pair.getMine()) < Integer.valueOf(action[1]) && Integer.valueOf(pair.getNext()) > Integer.valueOf(action[1]))
						this.send("conAccept:"+pair.getIp(pair.getMine())+":"+pair.getMine()+":"+pair.getIp(pair.getNext())+":"+pair.getNext(), action[2], portPair);
					else
						pair.sendMessage(mes, action[1],portPair);
				break;
				
				case "conAccept":
					pair.conAccept(action[1], action[3], action[2], action[4]);
				break;
				
				case "setSucc":
					pair.setNext(action[2], action[1]);
				break;					
				
				case "setPre":			
					pair.setPre(action[2], action[1]);
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
				
				case "yaf":
				break;
				case "hash":
				try {
					pair.setMine(action[1], InetAddress.getLocalHost().getHostAddress().toString());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
				case "ip":
					this.send("con:"+pair.getMine()+":"+pair.getIp(pair.getMine()), action[1], portPair);
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
	public void send(String message, String ip,int port) {
		this.emetteur.sendTo(message, ip, port);
		
	}

	//public void askHash() {
		//this.emetteur.sendToHashServeur();
		
	//}
}
