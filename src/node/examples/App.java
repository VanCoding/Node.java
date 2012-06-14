package node.examples;


import node.*;
import node.net.*;

public class App{	
	public static void main(String[] args){
		
		System.out.println("running...");
		net.createServer().on("connection",new ConnectionEvent(){
			public void call(TcpSocket s){
				System.out.println("got connection");
				s.on("data",new DataEvent(){
					public void call(Buffer b){
						System.out.println("got data: "+b.toString());
					}
				});
			}
		}).on("error",new ErrorEvent(){
			public void call(Exception e){
				e.printStackTrace();
			}
		}).listen(70);


		

		net.connect(70).on("open", new ConnectionEvent(){
			public void call(TcpSocket s){
				System.out.println("connected");
				s.write("Hello!");
			}
		}).on("error", new ErrorEvent(){
			public void call(Exception e){
				e.printStackTrace();
			}
		});
		
		try {
			System.in.read();
		} catch (Exception e) {}
	}
}



