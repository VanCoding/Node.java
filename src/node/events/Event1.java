package node.events;

public class Event1<T1> implements Event{
	public void call(T1 t1){		
	}
	public void emit(Object... args){
		call((T1)args[0]);
	}
}