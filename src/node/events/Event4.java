package node.events;

public class Event4<T1,T2,T3,T4> implements Event{
	public void call(T1 t1, T2 t2,T3 t3,T4 t4){		
	}
	public void emit(Object... args){
		call((T1)args[0],(T2)args[1], (T3)args[2], (T4)args[3]);
	}
}