package node.events;

public class Event2<T1,T2> implements Event{
	public void call(T1 t1, T2 t2){		
	}
	public void emit(Object... args){
		call((T1)args[0],(T2)args[1]);
	}
}