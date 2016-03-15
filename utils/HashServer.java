/*
 *
 * Serveur de Hachage
 *   1. Démarrer le serveur en indiquant un port et le nombre n de hash différents à fournir
 *   2. Pour obtenir un hash 
 *         a. Connecter un client au serveur
 *         b. Envoyer en un message le texte à hasher (i.e. l'IP du client)
 *         c. Recevoir le hash
 *         d. FERMER LA CONNEXION !
 *   3. Garanties
 *         a. Pour un texte donné (et une instance du serveur) on obtient toujours le même hash
 *         b. Le même hash n'est pas attribué à deux textes différents
 *         c. Les hashs sont des entiers entre 0 et n-1
 *
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class HashServer {

    public static void main(String[] args) throws IOException {

	// Il faut deux arguments : le numéro de port et la taille du système (nombre de hashs différents demandés)
	if (args.length != 2) {
	    System.err.println("Usage: java HashServer <port number> <system max size>");
	    System.exit(1);
	}
	int portNumber = Integer.parseInt(args[0]);
	int circleSize = Integer.parseInt(args[1]);

	// Les hashs possibles sont stockés dans une liste et mélangés
	LinkedList<Integer> availableHashes = new LinkedList<Integer>();
	for (int i = 0; i < circleSize; i++) {
	    availableHashes.add(i);
	}
	Collections.shuffle(availableHashes);
	
	
	/** AJOUT IAN DEB*/
	// Les hashs des donn�es possibles sont stockés dans une liste et mélangés
		LinkedList<Integer> availableHashesData = new LinkedList<Integer>();
		for (int i = 0; i < circleSize; i++) {
		    availableHashesData.add(i);
		}
		Collections.shuffle(availableHashesData);
	/*** AJOUT IAN FIN*/
		

	// Les hashs déjà attribués seront stockés dans une table de hachage
	// Clé : texte 
        // Valeur : hash correspondant
	HashMap<String,String> knownHashes = new HashMap<String,String>();
	
	/** AJOUT IAN DEB*/
	HashMap<String,String> knownHashesData = new HashMap<String,String>();
	/** AJOUT IAN FIN*/

	
	try (
	     // Tout ce qui est dans ce bloc sera fermé automatiquement à la fin du try
	    ServerSocket serverSock = new ServerSocket(portNumber);
	     ) {
	    
		while (true) {
		    try (
			 // Tout ce qui est dans ce bloc sera fermé automatiquement à la fin du try
			 Socket s = serverSock.accept();
			 PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			 ) {
			    
			    // Après avoir accepté une connexion on lit immédiatement un message
			    String inputLine;
			    if ((inputLine = in.readLine()) != null) {
				
				String toSend;
				
				String ip = inputLine;
		/** AJOUT IAN DEB*/
//				System.err.println(inputLine);
				String[] words = ip.split(":");
				if (words[0].equals("data")) {
					
					if (knownHashesData.containsKey(words[1])) {
					    toSend = knownHashesData.get(words[1]);
					} else {
					    if (availableHashesData.isEmpty()) {
						toSend = "aht";
					    } else {
						toSend = "dataHash:"+words[1]+":"+availableHashesData.removeFirst().toString();
						knownHashesData.put(words[1],toSend);
					    }
					}
				} else {
		/** AJOUT IAN FIN*/
				// Si le texte reçu a déjà un hash on l'enverra
				// Sinon, s'il reste au moins un hash disponible, on lui en attribue un et on l'enverra
				// Sinon on enverra le message « aht » (all hashes taken)
					if (knownHashes.containsKey(ip)) {
					    toSend = knownHashes.get(ip);
					} else {
					    if (availableHashes.isEmpty()) {
						toSend = "aht";
					    } else {
						toSend = "hash:"+availableHashes.removeFirst().toString();
						knownHashes.put(ip,toSend);
					    }
					}
					
					
					
				    }
				// Finalement on envoie le message construit auparavant
				out.println(toSend);
			    }
			}
		    catch (IOException e) {
			System.err.println("Exception pendant la communication avec un pair (HashServer)");
			System.err.println(e.getMessage());
			System.exit(1);
		    }
		}
	    } 
	catch (IOException e) {
	    System.err.println("Exception à la création de la socquette serveur (HashServer)");
	    System.err.println(e.getMessage());
	    System.exit(1);
	} 
    }   
}
