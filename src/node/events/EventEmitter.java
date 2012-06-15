package node.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventEmitter<T extends EventEmitter> implements IEventEmitter<T>{
	private Map<String,ArrayList<Event>> events;
	public EventEmitter(){ 
		clearListeners();
	}
	public T on(String name,Event ev){
		if(!events.containsKey(name)){
			events.put(name, new ArrayList<Event>());
		}
		events.get(name).add(ev);		
		return (T)this;
	}
	public T addListener(String name, Event ev){
		return on(name,ev);
	}
	public void off(String name, Event ev){
		if(events.containsKey(name)){
			events.get(name).remove(ev);
		}
	}
	public void removeListener(String name, Event ev){
		off(name,ev);
	}
	public void clearListeners(String name){
		events.remove(name);
	}
	public void clearListeners(){
		events = new HashMap<String,ArrayList<Event>>();
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