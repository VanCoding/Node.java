package node.net;

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