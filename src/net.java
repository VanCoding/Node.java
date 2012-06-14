import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class net{
	public static TcpServer createServer(){
		return new TcpServer();
	}
	public static TcpSocket connect(int port){
		return new TcpSocket(port);
	}
	public static TcpSocket connect(int port, String address){
		return new TcpSocket(port,address);
	}
}

//Public Socket Events
class ConnectionEvent extends Event1<TcpSocket>{}
class DataEvent extends Event1<Buffer>{}
class ErrorEvent extends Event1<Exception>{}



class SelectorPool{
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

class TcpServer extends EventEmitter<TcpServer> {
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

class TcpSocket extends EventEmitter<TcpSocket>{
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
}