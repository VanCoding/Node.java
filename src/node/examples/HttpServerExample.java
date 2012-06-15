package node.examples;

import node.SelectorPool;
import node.events.Event0;
import node.http.HttpServer;
import node.http.HttpServerRequest;
import node.http.HttpServerResponse;
import node.http.RequestEvent;
import node.http.http;

public class HttpServerExample {
	public static void main(String[] args){
		SelectorPool.queue(new Event0(){
			public void call(){
				http.createServer().on("request",new RequestEvent(){
					public void call(HttpServerRequest req, HttpServerResponse res){
						System.out.println("got request");
						res.end("Hello World");
					}
				}).listen(80);
			}
		});
	}
}
