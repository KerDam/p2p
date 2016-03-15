package p2p;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

public class Recepteur implements Runnable{
	
	private int port;
	private Communication com;

	public Recepteur(int portNumber, Communication com) {
		this.port = portNumber;
		this.com = com;		
		
	}
	/**
	 * Méthode qui retourne les messages reçu sur le port du recepteur.
	 * Message possibles:
	 * - con:Hash:IP -> message envoyée du nouveau pair, vers un autre
	 * - conAccept:IP_precedent:Hash_precedent:IP_suivant:Hash_suivant -> message renvoyé d'un pair au nouveau
	 * - setSucc:IP:Hash -> demande de mise à jour du nouveau pair à son precedent
	 * - setPre:IP:Hash ->demande de mise à jour du nouveau pair à son suivant
	 * @return message Un message
	 */
	public String getMessage() {
		
		String messageRecu = "";
		String str = "";
		try (  ServerSocket serverSock = new ServerSocket(this.port);
				) { 
			
			
			 Socket s = serverSock.accept();
			 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			 PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			 
			    if ((messageRecu = in.readLine()) != null) {
			    	
			    	this.com.receptMes(messageRecu);
			    	if ( messageRecu.equals("rt?")) {
			    		
			    		for(String key : this.sendNetworkTable().keySet()) {
			    			out.println(this.sendNetworkTable().get(key));
			    		}
			    		out.println("end");
			    	}
			    		
			    	out.println("ok");
			    }
		out.close();
		in.close();
		serverSock.close();
		} catch (Exception e) {
			System.out.println(e.getMessage()+"-"+e.getCause());
		}
		
		return str;
	}
	
	public HashMap<String, String> sendNetworkTable() {
		return this.com.getNetworkTable();

	}
	
	
	@Override
	public void run() {
		
		while (true) {
			this.getMessage();
		}
	}

}
