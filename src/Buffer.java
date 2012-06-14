class Buffer{
	byte[] data;
	enum Encoding{binary,ascii,utf8,hex,base64}
	
	public Buffer(byte[] data){
		this.data = data;
	}
	public Buffer(String data, String encoding){
		try{
			switch(Encoding.valueOf(encoding)){
			case binary:
				this.data = data.getBytes();
				break;
			case ascii:
				this.data = data.getBytes("ASCII");	
				break;
			case utf8:
				this.data = data.getBytes("UTF8");
				break;
			}
		}catch(Exception e){			
		}
	}
	public Buffer(String data){
		this(data,"utf8");
	}
	public String toString(String encoding){
		try{
			switch(Encoding.valueOf(encoding)){
				case binary:
					return data.toString();
				case ascii:
					return new String(data,"ASCII");
				case utf8:
					return new String(data,"UTF8");
				default:
					return "";
			}
		}catch(Exception e){
			return "";
		}
	}
	public String toString(){
		return this.toString("utf8");
	}
	public int length(){
		return data.length;
	}
	public byte get(int i){
		return data[i];
	}
	public byte[] array(){
		return data;
	}
}