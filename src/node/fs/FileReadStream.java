package node.fs;

import java.nio.channels.AsynchronousFileChannel;

import node.streams.ReadStream;

public class FileReadStream extends ReadStream<FileReadStream> {
	private AsynchronousFileChannel channel;
	public FileReadStream(String path){
	}
}
