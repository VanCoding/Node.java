package node;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import node.events.Event0;
import node.events.EventEmitter;
import node.net.TcpServer;
import node.net.TcpSocket;

public class SelectorPool{
	private static Selector selector;
	private static boolean started = false;
	private static Queue<Event0> queue = new LinkedList<Event0>();
	
	
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
							boolean sleep = true;
							selector.selectNow();
							Set<SelectionKey> readyKeys = selector.selectedKeys(); 
							Iterator<SelectionKey> it = readyKeys.iterator();							
							
							while(it.hasNext()){		
								sleep = false;
								((Processable)it.next().attachment()).process();
								it.remove();
							}
							while(!queue.isEmpty()){
								sleep = false;
								queue.remove().call();
							}
							
							if(sleep){
								Thread.sleep(1);
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
	
	public static SelectionKey Add(SelectableChannel channel,Processable emitter, int ops){
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
	public static void queue(Event0 e){
		ensureStarted();
		queue.add(e);
	}
}