package node.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import node.Buffer;
import node.SelectorPool;
import node.events.Event0;
import node.events.EventEmitter;
import node.streams.ReadStream;
import node.streams.WriteStream;

public class TcpSocket extends EventEmitter<TcpSocket> implements ReadStream<TcpSocket>,WriteStream<TcpSocket>{
	private TcpSocket self;
	private ArrayList<Buffer> writequeue = new ArrayList<Buffer>();
	private SelectionKey connectkey;
	private SelectionKey readkey;
	private SelectionKey writekey;
	private SocketChannel channel;
	private boolean endafterwrite = false;
	public TcpSocket(int port, String address){
		this.self = this;
		try{			
			this.on("connectable", new Event0(){
				public void call(){
					SelectorPool.Remove(connectkey);
					try {
						channel.finishConnect();
						startRead();
					} catch (Exception e) {
						this.emit("error",e);
						close();
					}
				}
			});
			
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress(InetAddress.getByName(address),port));
			connectkey = SelectorPool.Add(channel, this, SelectionKey.OP_CONNECT);
			
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
		startRead();
			
			
	}
	
	private void startRead(){
		
		try{
			readkey = SelectorPool.Add(channel,this,SelectionKey.OP_READ);
			this.on("readable",new Event0(){
				public void call(){
					try{
						ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
						((SocketChannel)readkey.channel()).read(buffer);
						buffer.flip();
						byte[] buf = new byte[buffer.remaining()];
						buffer.get(buf);
						self.emit("data",new Buffer(buf));
					}catch(Exception e){
						close();
					}
					
				}
			}).on("writable",new Event0(){
				public void call(){
					try{
						if(writequeue.size() > 0){
							channel.write(ByteBuffer.wrap((writequeue.get(0).array())));
							writequeue.remove(0);
						}else{
							SelectorPool.Remove(writekey);
							writekey = null;
							if(endafterwrite){
								destroy();
							}
						}
					}catch(Exception e){
						e.printStackTrace();
						self.emit("error",e);
						close();
					}
				}
			});
		}catch(Exception e){			
		}
		this.emit("open",this);
	}
	
	private void close(){
		try{
			channel.close();
			SelectorPool.Remove(readkey);
			if(writekey != null){
				SelectorPool.Remove(writekey);
			}
			this.emit("end");
			this.emit("close");
		}catch(Exception e){
		}
		
		channel = null;
		connectkey = null;
		readkey = null;
		writekey = null;
		writequeue = null;
		self = null;
	}
	
	public void write(Buffer b){
		writequeue.add(b);
		if(writekey == null){
			writekey = SelectorPool.Add(channel, this, SelectionKey.OP_WRITE);
		}
	}
	public void write(String data, String encoding){
		write(new Buffer(data,encoding));
	}
	public void write(String data){
		write(new Buffer(data));
	}
	
	public void end(){
		if(writequeue.size() > 0){
			endafterwrite = true;
		}else{
			destroy();
		}
	}
	
	public void end(Buffer data){
		write(data);
		end();
	}
	public void end(String data, String encoding){
		write(data,encoding);
		end();
	}
	public void end(String data){
		write(data);
		end();
	}	
	public void destroy(){
		close();
	}

	public void pipe(final WriteStream ws) {
		this.on("data", new DataEvent(){
			public void call(Buffer b){
				ws.write(b);
			}
		}).on("end", new Event0(){
			public void call(){
				ws.end();
			}
		});
	}	
}