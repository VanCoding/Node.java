package node.events;

public interface IEventEmitter<T>{
	public void emit(String ev, Object... args);
	public T on(String ev, Event e);
}
