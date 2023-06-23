package osProject;

import java.io.Serializable;

public class Word  implements Serializable{
    
	String data;
	String name;
	
	public Word(String name,String Data) {
		data=Data;
		this.name=name;
	}
	
	
}
