package p2p;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Pair {

	private HashMap<String,String> networkTable;
	private HashMap<String,String> dataSet;

	private String pre,next,mine;
	public Communication communication;
	private Thread thCom;
	
	public Pair(String ipServer){
		networkTable = new HashMap<String,String>();
		dataSet = new HashMap<String,String>();

		communication = new Communication(this);
		communication.ipServeur = ipServer;
//		next = String.valueOf(Integer.MAX_VALUE);
//		networkTable.put(next,"0.0.0.0");
		thCom = new Thread(communication);
		thCom.start();
	}
	
	public void setPre(String key, String ip){
		//networkTable.remove(this.pre);
		this.networkTable.put(key, ip);
		this.pre = key;
//		System.err.println("setPre key:"+key);

//		for(String key2 : networkTable.keySet()){
//			System.err.println(key2+" : "+networkTable.get(key2));
//		}
	}
	public void setNext(String key, String ip){
		//networkTable.remove(this.next);
		this.networkTable.put(key, ip);
		this.next = key;
//		System.err.println("setNext key:"+key);

//		for(String key2 : networkTable.keySet()){
//			System.err.println(key2+" : "+networkTable.get(key2));
//		}
	}
	
	public void setMine(String key, String ip){
		//networkTable.remove(this.mine);
		networkTable.put(key, this.getMineIp());
		mine = key;
	}
	
	public void setNetworkTable(HashMap<String,String> networkTable){
		this.networkTable = networkTable;
	}
	
	public void addNetworkTable(String key, String ip){
		networkTable.put(key, ip);
	}
	
	public String getNext(){
//		for(String key2 : networkTable.keySet()){
//			System.err.println(key2+" : "+networkTable.get(key2));
//			
//		}
//		System.err.println("retour getNext:"+this.next);

		return this.next;
	}
	public String getPre(){
//		for(String key2 : networkTable.keySet()){
//			System.err.println(key2+" : "+networkTable.get(key2));
//		}
//		System.err.println("retour getPre:"+this.pre);
		return this.pre;
	}
	
	public String getMine(){
		return this.mine;
	}
	
	public String getIp(String key){
		return networkTable.get(key);
	}
	
	public boolean isNext(String hash){
		return (Integer.getInteger(hash)> Integer.getInteger(this.next));
	}
	
	public HashMap<String,String> getNetworkTable() {
		return this.networkTable;

	}
	
	public void foreingConnexion(String hash,String ip){
		if (isNext(hash)){
			communication.send("conAccept:"+networkTable.get(getMine())+":"+getMine()+":"+networkTable.get(getNext())+":"+getNext(),ip,Communication.portPair);
		}
		else{
			communication.send("con:"+hash+":"+ip,networkTable.get(getNext()),Communication.portPair);
		}
	}
	
	public void conAccept(String ipPre,String ipNext, String hashPre, String hashNext){
		this.setPre(hashPre, ipPre);
		this.setNext(hashNext, ipNext);
//		System.out.println(this.getNext());
		this.communication.send("setSuc:"+this.getMineIp()+":"+this.getMine(), ipPre, Communication.portPair);
		this.communication.send("setPre:"+this.getMineIp()+":"+this.getMine(), ipNext, Communication.portPair);
	}
	
	public String getClosest(String hash){
//		int closest = -1;
//		int hashInt = Integer.valueOf(hash);
//		Set<String> keys = this.networkTable.keySet();
//		java.util.Iterator<String> ite = keys.iterator();
//		while (ite.hasNext()){
//			int tmp = Integer.valueOf(ite.next());
//			if (tmp > closest && tmp < hashInt){
//				closest = tmp;
//			}
//		}
//		System.err.println("retour getClosest:"+getIp(getNext()));
//		return this.getIp(String.valueOf(closest));
		

		return getIp(getNext());
	}
	public void sendMessage(String message, String hash, int port){
		this.communication.send("send:"+message+":"+hash, this.getIp(this.getClosest(hash)),port);
	}
	
	public boolean isTheEnd(){
		return (Integer.valueOf(getNext()) < Integer.valueOf(getMine()));
	}
	
	public String getMineIp(){
		try {
			return String.valueOf(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "wrq";
	}
	
	public void setHashFromServer(){
		this.communication.send(this.getMineIp(), Communication.ipServeur, Communication.portHash);
	}
	
	public void notifyToWelcomeServer(){
		this.communication.send("yo:"+this.getMine()+":"+this.getMineIp(), Communication.ipServeur, Communication.portWelcome);
	}
	
	public void sendPeerMessage(String hash_pair, String msg){
		try {
			int hash_dest = Integer.parseInt(hash_pair);
			if ( hash_dest > Integer.parseInt(this.mine) ) {
				this.communication.send("msg:"+msg+":"+this.mine+":"+hash_pair, this.networkTable.get(this.getNext()), Communication.portPair);
			} else {
				this.communication.send("msg:"+msg+":"+this.mine+":"+hash_pair, this.networkTable.get(this.getPre()), Communication.portPair);
			}
			
//			this.communication.send("msg:"+msg+":"+this.mine+":"+hash_pair, "192.168.0.48", Communication.portPair);
		} catch (Exception e) {
			System.err.println("Le Hash n'est pas un entier !");
		}

		

	}
	
	public void treatMessage(String msg, String hash_transmitter, String hash_dest) {
		if ( hash_dest.equals(mine) ) {
			try {
				System.err.println("--- Message recu:");
				TimeUnit.SECONDS.sleep(1);
				System.out.println("- De :"+ hash_transmitter +"   Pour :"+hash_dest);
				System.out.println("- " + msg );
				TimeUnit.SECONDS.sleep(1);
				System.err.println("--- Fin message.");
			} catch (Exception e) {
				// TODO: handle exception
			}
			

		} else if ( Integer.parseInt(hash_dest) > Integer.parseInt( mine)) {
			this.sendPeerMessage(getNext(), msg);
		} else  {
			this.sendPeerMessage(getPre(), msg);
		}
		
	}
	
	private void setHashDataFromServer(String data) {
		this.communication.send("data:"+data, Communication.ipServeur, Communication.portHash);	
	}
	
	public void addData(String data, String hash_data) {
		System.out.println("Hash de la donne: "+hash_data);
		this.communication.send("responsive:"+hash_data+":"+data, this.networkTable.get(this.getNext()), Communication.portPair);
		
	}
	
	public void addDataToSet(String hash_data, String data) {
		this.dataSet.put(hash_data, data);

	}
	


	private void getData(String hash_data, String hash_dest) {
		this.communication.send("getData:"+hash_data+":"+hash_dest, this.networkTable.get(this.getNext()), Communication.portPair);
	}
	
	public void treatData(String hash_data, String hash_dest) {
		if ( !hash_dest.equals(hash_dest)) {
			if ( this.dataSet.containsKey(hash_data) ) {
				this.sendPeerMessage(hash_dest, "DATA"+hash_data+"-"+this.dataSet.get(hash_data));
				
			} else {
				this.getData(hash_data, hash_dest);
			}
		}
	}

	
	private void stop() {
		this.communication.stopThread();
		this.thCom.interrupt();
	}
	
	
	
	
	
	
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	public static void main(String[] args) throws InterruptedException {

//		Pair pair = new Pair(args[0]);
		Pair pair = new Pair("192.168.0.48");
		
		while(pair.getMine() == null){
			pair.setHashFromServer();
			TimeUnit.SECONDS.sleep(1);
		}
		pair.setNext(pair.getMine(), pair.getMineIp());
		pair.setPre(pair.getMine(), pair.getMineIp());
		pair.notifyToWelcomeServer();
//		for(String key : pair.networkTable.keySet()){
//			System.err.println(key+" : "+pair.networkTable.get(key));
//		}

		boolean out = false;
		Scanner in = new Scanner( System.in );
		String cmd = "";
		String sep = " ";
		TimeUnit.SECONDS.sleep(2);

		while ( !out) {
			System.out.print(">>>");
			cmd = in.nextLine();
			String[] words = cmd.split(":");

			if (words.length <= 0) {
			    displayHelp();
			} else {
				switch (words[0]) {

			    case "h": 
			    	displayHelp();
				break;

			    case "q":
			    	pair.stop();
			    	out = true;
				break;
				
			    case "data":
			    	pair.setHashDataFromServer(words[1]);
				break;
				
			    case "getData":
			    	pair.getData(words[1], pair.getMine());
				break;

			    case "msg":
					if (words.length != 3) {

					    System.out.println(">Nombre d'arguments incorectes.");
					} else {
						String msg = words[1];
						String hash_dest = words[2];
					    if (  msg.length() > 0 &&  hash_dest.length() > 0) {
					    	pair.sendPeerMessage(hash_dest, msg);
					    
					    } else {
						    System.out.println(">Arguments incorectes.");
					    }
					}
				break;
				}
			}
				
		}
	}
		




		private static void displayHelp() {
			
			System.out.println("");
			System.out.println("*----------------------------------------------------------------*");
			System.out.println("|   Commandes disponibles:                                       |");
			System.out.println("|      . msg:votre_message:hash_dest -> Envoie un message        |");
			System.out.println("|      . data:votre_donnee -> Creer une donne sur le reseau      |");
			System.out.println("|      . getData:hash_donnee -> Recupere une donne sur le reseau |");
			System.out.println("|      . h -> aide                                               |");
			System.out.println("|                                                                |");
			System.out.println("*----------------------------------------------------------------*");

		    }

	
		

		
	

	

	
}
