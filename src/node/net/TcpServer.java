package node.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

import node.Processable;
import node.SelectorPool;
import node.events.Event0;
import node.events.EventEmitter;

public class TcpServer extends EventEmitter<TcpServer> implements Processable {
	private TcpServer self;
	private SelectionKey acceptkey = null;
	private ServerSocketChannel channel;
	public TcpServer(){
		self = this;
	}
	public void process(){
		try{
			TcpSocket sock = new TcpSocket(channel.accept());
			emitConnection(sock);
		}catch(Exception e){
			emitError(e);
		}		
	}
	public void listen(final int port){
		if(acceptkey == null){
			try{			
				channel = ServerSocketChannel.open();
				channel.configureBlocking(false);
				channel.socket().bind(new InetSocketAddress(InetAddress.getByName("localhost"),port));
				acceptkey = SelectorPool.Add(channel, this,SelectionKey.OP_ACCEPT);
			}catch(Exception e){
				emitError(e);
			}
		}
	}
	public void stop(){
		try{
			SelectorPool.Remove(acceptkey);
			acceptkey = null;	
			channel.close();
		}catch(Exception e){			
		}
	}
	
	private void emitConnection(final TcpSocket s){
		SelectorPool.queue(new Event0(){
			public void call(){
				self.emit("connection",s);
			}
		});
	}
	private void emitError(final Exception e){
		SelectorPool.queue(new Event0(){
			public void call(){
				self.emit("error",e);
			}
		});
	}

}