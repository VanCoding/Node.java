package node;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import node.events.EventEmitter;
import node.net.TcpServer;
import node.net.TcpSocket;

public class SelectorPool{
	private static Selector selector;
	private static boolean started = false;
	
	
	private static void ensureStarted(){
		if(!started){
			run();
		}			
	}
	private static void run(){
		try{
			selector = Selector.open();

			new Thread(){
				public void run(){
					while(true){
						try{
							selector.select(10);
							Set<SelectionKey> readyKeys = selector.selectedKeys(); 
							Iterator<SelectionKey> it = readyKeys.iterator(); 
							
							
							while(it.hasNext()){
								SelectionKey sk = it.next();								
								if(sk.isAcceptable()){
									System.out.println("acceptable");
									((TcpServer)sk.attachment()).emit("acceptable");
								}
								if(sk.isReadable()){
									System.out.println("readable");
									((TcpSocket)sk.attachment()).emit("readable");
								}
								if(sk.isWritable()){
									System.out.println("writable");
									((TcpSocket)sk.attachment()).emit("writable");
								}
								if(sk.isConnectable()){
									System.out.println("connectable");
									((TcpSocket)sk.attachment()).emit("connectable");
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
	
	public static SelectionKey Add(SelectableChannel channel,EventEmitter emitter, int ops){
		ensureStarted();
		try{
			SelectionKey key = channel.register(selector, ops);
			key.attach(emitter);
			return key;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void Remove(SelectionKey key){
		ensureStarted();
		try{			
			key.cancel();
		}catch(Exception e){		
		}
	}
}