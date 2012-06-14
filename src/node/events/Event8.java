package node.events;

public class Event8<T1,T2,T3,T4,T5,T6,T7,T8> implements Event{
	public void call(T1 t1, T2 t2,T3 t3,T4 t4,T5 t5,T6 t6,T7 t7,T8 t8){		
	}
	public void emit(Object... args){
		call((T1)args[0],(T2)args[1], (T3)args[2], (T4)args[3],(T5)args[4],(T6)args[5],(T7)args[6],(T8)args[7]);
	}
}