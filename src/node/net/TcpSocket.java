package node.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import node.Buffer;
import node.Processable;
import node.SelectorPool;
import node.events.Event0;
import node.events.EventEmitter;
import node.streams.IReadStream;
import node.streams.IWriteStream;
import node.streams.ReadWriteStream;

public class TcpSocket extends ReadWriteStream<TcpSocket> implements Processable{
	private TcpSocket self;
	private ArrayList<Buffer> writequeue = new ArrayList<Buffer>();
	private SelectionKey key;
	private SocketChannel channel;
	private boolean endafterwrite = false;
	private boolean writing = false;
	public TcpSocket(int port, String address){
		this.self = this;
		try{
			
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress(InetAddress.getByName(address),port));
			key = SelectorPool.Add(channel, this, SelectionKey.OP_CONNECT);
			
		}catch(Exception e){
			
		}	
	}
	public TcpSocket(int port){
		this(port,"localhost");
	}
	public TcpSocket(SocketChannel channel){
		this.self = this;
		this.channel = channel;
		try{
			channel.configureBlocking(false);
		}catch(Exception e){}
		key = SelectorPool.Add(channel, this, SelectionKey.OP_READ);
		
		emitOpen();		
	}
	
	private void emitOpen(){
		SelectorPool.queue(new Event0(){
			public void call(){
				self.emit("open",self);
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
	private void emitData(final Buffer b){
		SelectorPool.queue(new Event0(){
			public void call(){
				self.emit("data",b);
			}
		});
	}
	private void emitEnd(){
		SelectorPool.queue(new Event0(){
			public void call(){
				self.emit("end");
			}
		});
	}
	private void emitClose(){
		SelectorPool.queue(new Event0(){
			public void call(){
				self.emit("close");
			}
		});
	}
	

	public void process() {	
			if(key.isConnectable()){
				key.interestOps(0);
				try {
					channel.finishConnect();
					key.interestOps(SelectionKey.OP_READ);
					emitOpen();
				} catch (Exception e) {
					emitError(e);
					close();
				}
			}

			if(key.isReadable()){
				try{
					ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
					channel.read(buffer);
					buffer.flip();
					byte[] buf = new byte[buffer.remaining()];
					buffer.get(buf);
					
					emitData(new Buffer(buf));
				}catch(Exception e){
					close();
				}
			}
			if(key.isWritable()){
				try{
					if(writequeue.size() > 0){
						channel.write(ByteBuffer.wrap((writequeue.get(0).array())));
						writequeue.remove(0);
					}else{
						writing = false;
						key.interestOps(SelectionKey.OP_READ);
						if(endafterwrite){
							destroy();
						}
					}
				}catch(Exception e){
					emitError(e);
					close();
				}
			}

	}
	
	private void close(){
		try{
			channel.close();
			SelectorPool.Remove(key);
			emitEnd();
			emitClose();
		}catch(Exception e){
		}
		
		channel = null;
		key = null;
		writequeue = null;
		self = null;
	}
	
	public void write(Buffer b){
		writequeue.add(b);
		if(!writing){
			key.interestOps(SelectionKey.OP_WRITE|SelectionKey.OP_READ);
			writing = true;
		}
	}
	
	public void end(){
		if(writequeue.size() > 0){
			endafterwrite = true;
		}else{
			destroy();
		}
	}
	
	public void destroy(){
		close();
	}

}