package node.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

import node.SelectorPool;
import node.events.Event0;
import node.events.EventEmitter;

public class TcpServer extends EventEmitter<TcpServer> {
	private TcpServer self;
	private SelectionKey acceptkey = null;
	private ServerSocketChannel channel;
	public TcpServer(){
		self = this;
		try{
			this.on("acceptable",new Event0(){
				public void call(){
					try{
						TcpSocket sock = new TcpSocket(channel.accept());
						self.emit("connection",sock);
					}catch(Exception e){
						self.emit("error",e);
					}
				}
			});
		}catch(Exception e){
			self.emit("error",e);
		}
	}
	public void listen(int port){
		if(acceptkey == null){
			try{			
				channel = ServerSocketChannel.open();
				channel.configureBlocking(false);
				channel.socket().bind(new InetSocketAddress(InetAddress.getByName("localhost"),port));			
				acceptkey = SelectorPool.Add(channel, this,SelectionKey.OP_ACCEPT);
			}catch(Exception e){
				this.emit("error",e);
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

}