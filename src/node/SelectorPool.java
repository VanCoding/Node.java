package node;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import node.events.EventEmitter;
import node.net.TcpServer;
import node.net.TcpSocket;

public class SelectorPool{
	private static Selector selector;
	private static boolean started = false;
	private static Map<SelectionKey,EventEmitter<?>> listeners;
	
	
	private static void ensureStarted(){
		if(!started){
			run();
		}			
	}
	private static void run(){
		try{
			selector = Selector.open();
			listeners = new HashMap<SelectionKey,EventEmitter<?>>();
			new Thread(){
				public void run(){
					while(true){
						try{
							selector.select();
							Set<SelectionKey> readyKeys = selector.selectedKeys(); 
							Iterator<SelectionKey> it = readyKeys.iterator(); 
							
							
							while(it.hasNext()){
								SelectionKey sk = it.next();								
								if(sk.isAcceptable()){
									((TcpServer)listeners.get(sk)).emit("acceptable");
								}
								if(sk.isReadable()){
									((TcpSocket)listeners.get(sk)).emit("readable");
								}
								if(sk.isWritable()){
									((TcpSocket)listeners.get(sk)).emit("writable");
								}
								if(sk.isConnectable()){
									((TcpSocket)listeners.get(sk)).emit("connectable");
								}
								it.remove();
							}						
						}catch(Exception e){
						}						
					}
				}
			}.start();
			started = true;
		}catch(Exception e){			
		}
	}
	
	public static SelectionKey Add(SelectableChannel channel,EventEmitter<?> emitter, int ops){
		ensureStarted();
		try{			
			SelectionKey key = channel.register(selector, ops);
			listeners.put(key, emitter);
			return key;
		}catch(Exception e){
		}
		return null;
	}
	
	public static void Remove(SelectionKey key){
		ensureStarted();
		try{
			listeners.remove(key);
		}catch(Exception e){		
		}
	}
}