package node.streams;

import java.awt.Event;

import node.Buffer;
import node.events.EventEmitter;

public class WriteStream<T extends EventEmitter> extends EventEmitter<T> implements IWriteStream<T> {
	
	public static void write(IWriteStream ws, String a, String b){
		ws.write(new Buffer(a,b));
	}
	public static void write(IWriteStream ws, String a){
		ws.write(new Buffer(a));		
	}
	public static void end(IWriteStream ws, Buffer b) {
		ws.write(b);
		ws.end();
	}
	public static void end(IWriteStream ws, String a, String b){
		ws.write(a,b);
		ws.end();
	}
	public static void end(IWriteStream ws, String a){
		ws.write(a);
		ws.end();
	}

	public void write(Buffer b) {
	}

	public void write(String a, String b) {
		write(this,a,b);
	}

	public void write(String a) {
		write(this,a);
	}

	public void end() {
	}

	public void end(Buffer b) {
		end(this,b);
	}
	
	public void end(String a, String b) {
		end(this,a,b);
		
	}
	public void end(String a) {
		end(this,a);
	}

}
