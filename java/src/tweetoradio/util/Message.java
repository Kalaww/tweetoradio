package tweetoradio.util;

public class Message{
	
	/**
	 * Numero du message
	 */
	private int numero;

	/**
	 * Identifiant de l'entite ayant redige le message
	 */
	private String id;

	/**
	 * Le contenu du message
	 */
	private String contenu;

	/**
	 * Constructeur
	 * @param  _numero  numero du message
	 * @param  _id      identifiant de l'auteur
	 * @param  _contenu message
	 */
	public Message(int _numero, String _id, String _contenu){
		numero = _numero;
		id = _id;
		contenu = _contenu;
	}

	/**
	 * Constructeur
	 * @param  _id      identifiant de l'auteur
	 * @param  _contenu message
	 */
	public Message(String _id, String _contenu){
		id = _id;
		contenu = _contenu;
	}


	/**
	 * Encode le message
	 * @return version encode
	 */
	public String encoder(){
		return Encode.numMessage(numero)+" "+Encode.id(id)+" "+Encode.message(contenu);
	}

	/**
	 * Version affichable du message
	 * @return version affichable
	 */
	public String afficher(){
		return "["+id+"] "+contenu;
	}

	public String toString(){
		return "[num: "+numero+"] [id: "+id+"] [message: "+contenu+"]";
	}


	/**
	 * Getter numero
	 * @return numero
	 */
	public int getNumero(){
		return numero;
	}

	/**
	 * Getter identifiant
	 * @return id
	 */
	public String getId(){
		return id;
	}

	/**
	 * Getter contenu
	 * @return contenu
	 */
	public String getContenu(){
		return contenu;
	}
}