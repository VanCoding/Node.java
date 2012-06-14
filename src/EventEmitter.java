import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



class EventEmitter<T extends EventEmitter<?>>{
	private Map<String,ArrayList<Event>> events;
	public EventEmitter(){ 
		events = new HashMap<String,ArrayList<Event>>();
	}
	public T on(String name,Event ev){
		if(!events.containsKey(name)){
			events.put(name, new ArrayList<Event>());
		}
		events.get(name).add(ev);		
		return (T)this;
	}
	public void emit(String name, Object... args){
		try{
			ArrayList<Event> e = events.get(name);		
			for(int i = 0; i < e.size(); i++){			
				Event ev = e.get(i);
				ev.emit(args);
			}
		}catch(Exception e){			
		}
	}
}

interface Event{
	public void emit(Object... args);
}

class Event0 implements Event{
	public void call(){
	}
	public void emit(Object...args){
		call();
	}
}
class Event1<T1> implements Event{
	public void call(T1 t1){		
	}
	public void emit(Object... args){
		call((T1)args[0]);
	}
}
class Event2<T1,T2> implements Event{
	public void call(T1 t1, T2 t2){		
	}
	public void emit(Object... args){
		call((T1)args[0],(T2)args[1]);
	}
}
class Event3<T1,T2,T3> implements Event{
	public void call(T1 t1, T2 t2,T3 t3){		
	}
	public void emit(Object... args){
		call((T1)args[0],(T2)args[1], (T3)args[2]);
	}
}
class Event4<T1,T2,T3,T4> implements Event{
	public void call(T1 t1, T2 t2,T3 t3,T4 t4){		
	}
	public void emit(Object... args){
		call((T1)args[0],(T2)args[1], (T3)args[2], (T4)args[3]);
	}
}
class Event5<T1,T2,T3,T4,T5> implements Event{
	public void call(T1 t1, T2 t2,T3 t3,T4 t4,T5 t5){		
	}
	public void emit(Object... args){
		call((T1)args[0],(T2)args[1], (T3)args[2], (T4)args[3],(T5)args[4]);
	}
}
class Event6<T1,T2,T3,T4,T5,T6> implements Event{
	public void call(T1 t1, T2 t2,T3 t3,T4 t4,T5 t5,T6 t6){		
	}
	public void emit(Object... args){
		call((T1)args[0],(T2)args[1], (T3)args[2], (T4)args[3],(T5)args[4],(T6)args[5]);
	}
}
class Event7<T1,T2,T3,T4,T5,T6,T7> implements Event{
	public void call(T1 t1, T2 t2,T3 t3,T4 t4,T5 t5,T6 t6,T7 t7){		
	}
	public void emit(Object... args){
		call((T1)args[0],(T2)args[1], (T3)args[2], (T4)args[3],(T5)args[4],(T6)args[5],(T7)args[6]);
	}
}
class Event8<T1,T2,T3,T4,T5,T6,T7,T8> implements Event{
	public void call(T1 t1, T2 t2,T3 t3,T4 t4,T5 t5,T6 t6,T7 t7,T8 t8){		
	}
	public void emit(Object... args){
		call((T1)args[0],(T2)args[1], (T3)args[2], (T4)args[3],(T5)args[4],(T6)args[5],(T7)args[6],(T8)args[7]);
	}
}