package node.http;

import node.events.Event0;
import node.events.EventEmitter;
import node.net.ConnectionEvent;
import node.net.TcpServer;
import node.net.TcpSocket;
import node.net.net;

public class HttpServer extends EventEmitter<HttpServer>{
	private HttpServer self;
	private TcpServer server;
	
	public HttpServer(){
		self = this;
		server = net.createServer();
		server.on("connection", new ConnectionEvent(){
			public void call(TcpSocket sock){
				handleConnection(sock);
			}
		});
	}
	public void handleConnection(final TcpSocket sock){
		final HttpServerRequest r = new HttpServerRequest(this,sock);
		r.on("open", new Event0(){
			public void call(){
				self.emit("request", r,new HttpServerResponse(sock));
			}
		});
	}
	public void listen(int port){
		server.listen(port);
	}
}
