package node.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventEmitter<T extends EventEmitter> implements IEventEmitter<T>{
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