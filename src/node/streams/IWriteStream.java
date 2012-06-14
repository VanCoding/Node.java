package node.streams;

import node.Buffer;
import node.events.IEventEmitter;

public interface IWriteStream<T> extends IEventEmitter<T>{
	public void write(Buffer b);
	public void write(String a, String b);
	public void write(String a);
	public void end();
	public void end(Buffer b);
	public void end(String a, String b);
	public void end(String a);
}
