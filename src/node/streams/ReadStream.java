package node.streams;

import node.Buffer;
import node.events.Event;
import node.events.Event0;
import node.events.EventEmitter;
import node.net.DataEvent;

public class ReadStream<T extends EventEmitter> extends EventEmitter<T> implements IReadStream<T> {
	
	public static void pipe(final IReadStream rs, final IWriteStream ws){
		rs.on("data", new DataEvent(){
			public void call(Buffer b){
				ws.write(b);
			}
		});
		rs.on("end", new Event0(){
			public void call(){
				ws.end();
			}
		});
	}	
	
	public void pipe(IWriteStream ws) {
		pipe(this,ws);
	}	
}
