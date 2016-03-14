package p2p;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

public class Pair {

	private HashMap<String,String> networkTable;
	private String pre,next,mine;
	public Communication communication;
	public Pair(){
		networkTable = new HashMap<String,String>();
		communication = new Communication(this);
//		next = String.valueOf(Integer.MAX_VALUE);
//		networkTable.put(next,"0.0.0.0");
		Thread thCom = new Thread(communication);
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
			this.communication.send("msg:"+msg+":"+this.mine+":"+hash_pair, this.networkTable.get(this.getNext()), Communication.portWelcome);		
	}
	
	public void treatMessage(String msg, String hash_transmitter, String hash_dest) {
		if ( hash_dest.equals(mine) ) {
			System.err.println("--- Message recu:");
			System.out.println("- De :"+ hash_transmitter +"   Pour :"+hash_dest);
			System.out.println("- " + msg );
			System.err.println("--- Fin message.");

		} else if ( Integer.parseInt(hash_dest) > Integer.parseInt( mine)) {
			this.sendPeerMessage(getNext(), msg);
		} else  {
			this.sendPeerMessage(getPre(), msg);
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		Pair pair = new Pair();
		
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
	}

	

	
}
