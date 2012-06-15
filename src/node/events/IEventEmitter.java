package node.events;

public interface IEventEmitter<T>{
	public void emit(String ev, Object... args);
	public T on(String ev, Event e);
	public T addListener(String ev, Event e);
	public void off(String ev, Event e);
	public void removeListener(String ev, Event e);
}
