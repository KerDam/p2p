package p2p;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;

public class Pair {

	private HashMap<String,String> networkTable;
	private String pre,next,mine;
	private Communication communication;
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
			communication.send("conAccept:"+networkTable.get(getMine())+":"+getMine()+":"+networkTable.get(getNext())+":"+getNext(),ip);
		}
		else{
			communication.send("con:"+hash+":"+ip,networkTable.get(getNext()));
		}
	}
	
	public void conAccept(String ipPre,String ipNext, String hashPre, String hashNext){
		this.setPre(hashPre, ipPre);
		this.setNext(hashNext, ipNext);
		this.communication.send("setSuc:"+this.getIp(this.getMine())+":"+this.getMine(), this.getIp(this.getPre()));
		this.communication.send("setPre:"+this.getIp(this.getMine())+":"+this.getMine(), this.getIp(this.getNext()));
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
	public void sendMessage(String message, String hash){
		this.communication.send("send:"+message+":"+hash, this.getIp(this.getClosest(hash)));
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
}
