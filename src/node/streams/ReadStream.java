package node.streams;

import node.events.IEventEmitter;

public interface ReadStream<T> extends IEventEmitter<T>{
	public void pipe(WriteStream ws);
}