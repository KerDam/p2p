package p2p;
import java.awt.List;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;


public class Communication implements Runnable {
	
	
	public static String ipServeur = "0";
	
	public static int portPair = 8004;
	public static int portMoniteur = 8002;
	public static int portHash = 8001;
	public static int portWelcome = 8000;
	private Recepteur recepteurMoniteur;
	private Recepteur recepteurPair;
	private Recepteur recepteurHash;
	
	private Emetteur emetteur;
	private Pair pair;
	private LinkedList<String> messages;
	
	private Thread thRecepteurPair;
	private Thread thRecepteurMon;


	public Communication(Pair p) {
		this.recepteurPair = new Recepteur(portPair,this);
		this.recepteurMoniteur = new Recepteur(portMoniteur,this);
		this.pair = p;
		this.emetteur = new Emetteur(this);
		this.messages = new LinkedList<String>();
		
		// Lancer les thread
		thRecepteurPair = new Thread(this.recepteurPair);
		thRecepteurPair.start();
		thRecepteurMon = new Thread(this.recepteurMoniteur);
		thRecepteurMon.start();
		
		
	}
	

	
	
	
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
		
		
		while (true){
//			System.out.println("hello");
			//System.out.println(messages.isEmpty());
			if (messages.isEmpty() == false){
				String mes = this.messages.pop();
				System.out.println("t  --- "+mes);
//				System.out.println(mes.getClass());
			if ( !mes.equals("null")) {
				String sep = ":";
				
				String[] action = mes.split(sep);
				
				switch (action[0]) {
				
				/*Message de pair à pair */
					case "con":	
						if ((Integer.valueOf(pair.getMine()) < Integer.valueOf(action[1]) && Integer.valueOf(pair.getNext()) > Integer.valueOf(action[1])) 
								|| (pair.getMine() == pair.getNext() && pair.getMine() == pair.getPre())
								|| (pair.isTheEnd() && (Integer.valueOf(pair.getNext()) > Integer.valueOf(action[1])) && (Integer.valueOf(pair.getMine()) > Integer.valueOf(action[1])))
								|| (pair.isTheEnd() && (Integer.valueOf(action[1]) > Integer.valueOf(pair.getMine()) && (Integer.valueOf(action[1]) > Integer.valueOf(pair.getNext()))))){
							String data = pair.getData(action[1]);
							this.send("conAccept:"+pair.getIp(pair.getMine())+":"+pair.getMine()+":"+pair.getIp(pair.getNext())+":"+pair.getNext()+":"+data, action[2], portPair);
						}
						else 	
							this.send(mes, pair.getClosest(action[1]),portPair);
					break;
					
					case "conAccept":
						if (action[5] != null){
						String[] datas = action[5].split("/");
						int i = 0;
						for (i = 0; i < datas.length; i++){
							String[] data = datas[i].split(",");
							pair.addData(data[1], data[0]);
						}
						pair.conAccept(action[1], action[3], action[2], action[4]);
						}
					break;
					
					case "setSuc":
						pair.setNext(action[2], action[1]);
					break;					
					
					case "setPre":			
						pair.setPre(action[2], action[1]);
					break;				
				/* Message d'un pair */
					case "msg":
						pair.treatMessage(action[1],action[2], action[3]);
					break;
					
					case "responsive":
						pair.addDataToSet(action[1],action[2]);
					break;
					
					case "getData":
						pair.treatData(action[1],action[2]);
					break;
				/*Message du moniteur */				
					case "ip?:":	// Demande d'ip		
					break;
					
					case "ukh":			
					break;
						
					case "wrq":			
					break;
					
					case "rt?":		//Demande de table de routage
//						pair.sendRoutingTable();
						break;
					
					case "oups":			
					break;
						
					case "end":			
					break;
					
					case "dataHash":	
						this.pair.addData(action[1],action[2]);
					break;
					
					case "yaf":
					break;
					case "hash":
						pair.setMine(action[1], pair.getMineIp());
					break;
					
					case "ip":
						if (!action[1].equals("yaf"))
							this.send("con:"+pair.getMine()+":"+pair.getIp(pair.getMine()), action[1], portPair);
					break;
					
				/* Sinon */
					default:
					break;
				}
			}
		}
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		
	}
	
	public void receptMes(String s) {
		System.out.println("r  -<- "+s);
		this.messages.addLast(s);
//		System.out.println(messages.isEmpty());
		//for(String s2 : this.messages) {
			//System.out.println("->-"+s2);
		//}
		
	}

	/**
	 * Méthode qui envoie un message à un autre paire
	 * @param message commande que l'envoie au pair
	 * @param ip du pair destinataire
	 */
	public void send(String message, String ip,int port) {
		this.emetteur.sendTo(message, ip, port);
		
	}





	@Override
	public void run() {
		this.receptionMessage();
		
		
	}





	public HashMap<String,String> getNetworkTable() {
		HashMap<String,String> returnTable = new HashMap<String,String>();
		String mine = this.pair.getMine();
		for(String key : this.pair.getNetworkTable().keySet()) {
			returnTable.put(key,mine+":"+key+":"+this.pair.getNetworkTable().get(key));
		}
		return returnTable;
		
	}





	public void stopThread() {
		this.thRecepteurMon.interrupt();
		this.thRecepteurPair.interrupt();

	}
	

	//public void askHash() {
		//this.emetteur.sendToHashServeur();
		
	//}
}
