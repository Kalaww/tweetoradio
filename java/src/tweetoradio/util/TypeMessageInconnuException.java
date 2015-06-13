package tweetoradio.util;


/**
 * Exception si un message n'est pas encodé dans un des formats définis
 */
public class TypeMessageInconnuException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public TypeMessageInconnuException(String m){
		super("Type de message inconnu: "+m);
	}
}