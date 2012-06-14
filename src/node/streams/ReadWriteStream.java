package node.streams;

import node.Buffer;
import node.events.Event;
import node.events.EventEmitter;

public class ReadWriteStream<T extends EventEmitter> extends EventEmitter<T> implements IReadStream<T>, IWriteStream<T> {
	public void write(Buffer b) {
	}

	public void write(String a, String b) {
		WriteStream.write(this, a, b);
	}

	public void write(String a) {
		WriteStream.write(this, a);
	}
	
	public void end() {	
	}

	public void end(Buffer b) {
		WriteStream.end(this,b);
	}

	public void end(String a, String b) {
		WriteStream.end(this,a, b);
	}

	public void end(String a) {
		WriteStream.end(this,a);
	}

	public void pipe(IWriteStream ws) {
		ReadStream.pipe(this,ws);
	}
}
