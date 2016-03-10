package p2p;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Pair {

	private HashMap<String,String> networkTable;
	private String pre,next,mine;
	public Communication communication;
	public Pair(){
		networkTable = new HashMap<String,String>();
		communication = new Communication(this);
	}
	
	public void setPre(String key, String ip){
		networkTable.remove(this.pre);
		networkTable.put(key, ip);
		pre = key;
	}
	public void setNext(String key, String ip){
		networkTable.remove(this.next);
		networkTable.put(key, ip);
		next = key;
	}
	
	public void setMine(String key, String ip){
		networkTable.remove(this.mine);
		networkTable.put(key, ip);
		mine = key;
	}
	
	public void setNetworkTable(HashMap<String,String> networkTable){
		this.networkTable = networkTable;
	}
	
	public void addNetworkTable(String key, String ip){
		networkTable.put(key, ip);
	}
	
	public String getNext(){
		return this.next;
	}
	public String getPre(){
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
		this.communication.send("setSuc:"+this.getIp(this.getMine())+":"+this.getMine(), this.getIp(this.getPre()), Communication.portPair);
		this.communication.send("setPre:"+this.getIp(this.getMine())+":"+this.getMine(), this.getIp(this.getNext()), Communication.portPair);
	}
	
	public String getClosest(String hash){
		int closest = -1;
		int hashInt = Integer.valueOf(hash);
		Set<String> keys = this.networkTable.keySet();
		java.util.Iterator<String> ite = keys.iterator();
		while (ite.hasNext()){
			int tmp = Integer.valueOf(ite.next());
			if (tmp > closest && tmp < hashInt){
				closest = tmp;
			}
		}
		return this.getIp(String.valueOf(closest));
	}
	public void sendMessage(String message, String hash, int port){
		this.communication.send("send:"+message+":"+hash, this.getIp(this.getClosest(hash)),port);
	}
	
	public String getIp(){
		try {
			return String.valueOf(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "wrq";
	}
	
	public void setHashFromServer(){
		this.communication.send(this.getIp(), "localhost", Communication.portHash);
	}
	
	public void notifyToWelcomeServer(){
		this.communication.send("yo:"+this.getIp(), "localhost", Communication.portWelcome);
	}
	
	public static void main(String[] args) throws InterruptedException {
		Pair pair = new Pair();
		while(pair.getMine() == null){
			pair.setHashFromServer();
			TimeUnit.SECONDS.sleep(1);
		}
		pair.notifyToWelcomeServer();
	}
}
