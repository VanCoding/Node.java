package node.streams;

import node.events.IEventEmitter;

public interface IReadStream<T> extends IEventEmitter<T>{
	public void pipe(IWriteStream ws);
}