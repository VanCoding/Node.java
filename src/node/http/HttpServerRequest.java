package node.http;

import java.util.LinkedHashMap;

import node.Buffer;
import node.events.Event0;
import node.net.DataEvent;
import node.net.TcpSocket;
import node.streams.ReadStream;

public class HttpServerRequest extends ReadStream<HttpServerRequest>{
	HttpServerRequest self;
	HttpServer server;
	TcpSocket socket;
	
	public String url = null;
	public String method = null;
	public String version = null;
	private LinkedHashMap<String,String> headers = new LinkedHashMap<String,String>();
	
	public HttpServerRequest(HttpServer server,TcpSocket sock){
		this.self = this;
		this.server = server;
		this.socket = sock;
		
		final StringBuilder data = new StringBuilder();
		
		socket.on("data", new DataEvent(){
			public void call(Buffer b){
				int i = data.length();
				data.append(b.toString());
				for(;i < data.length(); i++){
					char c = data.charAt(i);
					if(c == '\r' || c == '\n'){
						String line = data.substring(0, i);						
						data.delete(0, i+(c=='\r'?2:1));
						i = -1;
						
						if(url == null){
							String[] parts = line.split(" ");
							if(parts.length == 3){
								method = parts[0];
								url = parts[1];
								version = parts[2];
							}else{
								return;
							}
						}else if(line.length() > 0){
							String[] parts = line.split(" ");
							headers.put(parts[0].trim(), parts[1].trim());							
						}else{
							self.emit("open");							
							socket.off("data", this);
							
							if(method == "POST"){
								final int contentLength = (getHeader("Content-Lenght") != null?Integer.parseInt(getHeader("Content-Length")):-1);
								
								socket.on("data", new DataEvent(){
									private int gottenData = 0;
									public void call(Buffer b){
										self.emit("data",b);
										gottenData += b.length();
										if(gottenData == contentLength){
											self.emit("end");
											socket.clearListeners();
											self.server.handleConnection(socket);
										}else{
											System.out.println(gottenData+"/"+contentLength);
										}
									}
								});
								socket.on("end", new Event0(){
									public void call(){
										self.emit("end");
									}
								});
							}else{
								self.emit("end");
								self.server.handleConnection(socket);
							}
							
							if(data.length() > 0){
								self.emit("data", new Buffer(data.toString()));
							}
						}
						
					}
				}
				
			}
		});		
	}
	public String getHeader(String name){
		return headers.get(name);
	}
}
