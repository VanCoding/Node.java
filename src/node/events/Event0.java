package node.events;

public class Event0 implements Event{
	public void call(){
	}
	public void emit(Object...args){
		call();
	}
}