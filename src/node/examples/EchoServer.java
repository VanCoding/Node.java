package node.examples;


import node.*;
import node.events.Event0;
import node.net.*;

public class EchoServer{	
	public static void main(String[] args){
		
		
		SelectorPool.queue(new Event0(){
			public void call(){
				net.createServer().on("connection",new ConnectionEvent(){
					public void call(TcpSocket s){
						System.out.println("got connection");
						s.on("data",new DataEvent(){
							public void call(Buffer b){
								System.out.println("got data: "+b.toString());
							}
						});
						s.pipe(s);
					}
				}).on("error",new ErrorEvent(){
					public void call(Exception e){
						e.printStackTrace();
					}
				}).listen(70);


				

				net.connect(70).on("open", new ConnectionEvent(){
					public void call(TcpSocket s){
						System.out.println("connected");
						s.on("data", new DataEvent(){
							public void call(Buffer b){
								System.out.println("server returned: "+b.toString());
							}
						}).write("Hello!");
					}
				}).on("error", new ErrorEvent(){
					public void call(Exception e){
						e.printStackTrace();
					}
				});
				System.out.println("Server listening ont port 70...");
			}
		});
		
		
		
		
		try {
			System.in.read();
		} catch (Exception e) {}
	}
}



