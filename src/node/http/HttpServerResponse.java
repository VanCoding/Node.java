package node.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import node.Buffer;
import node.net.TcpSocket;
import node.streams.WriteStream;

public class HttpServerResponse extends WriteStream<HttpServerResponse> {
	private TcpSocket socket;
	private boolean sentheader = false;
	private LinkedHashMap<String,String> headers = new LinkedHashMap<String,String>();
	
	public int statusCode = 200;
	public String message = "OK";
	public String version = "HTTP/1.1";
	public HttpServerResponse(TcpSocket s){
		this.socket = s;
		
		setHeader("Connection","keep-alive");
		setHeader("Transfer-Encoding","chunked");
	}
	
	private void sendHeader(){
		
		String data = version+" "+statusCode+" "+message+"\r\n";
		
		ArrayList<String> keys = new ArrayList<String>(headers.keySet());
		ArrayList<String> values =new ArrayList<String>(headers.values());
		
		for(int i = 0; i < keys.size(); i++){
			data += keys.get(i)+": "+values.get(i)+"\r\n";
		}
		data += "\r\n";
		
		socket.write(new Buffer(data));
		sentheader = true;
	}
	public void sendHeader(int code){
		statusCode = code;
		sendHeader();
	}
	public void sendHeader(int code, String message){
		this.message = message;
		sendHeader(code);
	}
	
	public String getHeader(String name){
		return headers.get(name);
	}
	public void setHeader(String name, String value){
		headers.put(name,value);
	}
	
	public void write(Buffer b){
		if(!sentheader){
			sendHeader();
		}
		if(getHeader("Transfer-Encoding").equals("chunked")){
			b = new Buffer(Integer.toHexString(b.length())+"\r\n"+b.toString()+"\r\n");
		}
		socket.write(b);
	}
	public void end(){
		if(getHeader("Transfer-Encoding").equals("chunked")){
			socket.write("0\r\n\r\n");
		}
		if(getHeader("Connection").equals("close")){
			socket.end();
		}
	}
}
