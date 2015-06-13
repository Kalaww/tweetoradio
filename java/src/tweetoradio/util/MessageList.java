package tweetoradio.util;

import java.util.ArrayList;
import java.lang.IndexOutOfBoundsException;

/**
 * Liste de message
 */
public class MessageList{

	/**
	 * Nombre maximal de message dans la liste
	 */
	private static final int MAX_MESSAGES = 10000;
	
	/**
	 * Compteur du numero courant du prochain message
	 */
	private int compteur;

	/**
	 * Indice du message courant
	 */
	private int courant;

	/**
	 * Liste des messages
	 */
	private ArrayList<Message> messages;

	/**
	 * Constructeur d'une liste de mesage vide
	 */
	public MessageList(){
		compteur = 0;
		courant = 0;
		messages = new ArrayList<Message>();
	}

	/**
	 * Ajoute un message à la liste
	 * @param id      identifiant de l'auteur
	 * @param message contenu du message
	 */
	public synchronized void addMessage(String id, String message){
		removeByNumMess(compteur);
		messages.add(new Message(compteur, id, message));
		compteurPP();
	}

	/**
	 * Récupère les n derniers messages
	 * @param  nb nombre de message à récupérer
	 * @return    list des n derniers messages encodés
	 */
	public synchronized ArrayList<String> getLastMessages(int nb){
		ArrayList<String> list = new ArrayList<String>();

		for(int i = nb-1; i >= 0; i--){
			int num = courant - i -1;
			if(num < 0) num += MAX_MESSAGES;
			Message m = getByNumMess(num);
			if(m != null)
				list.add(m.encoder());
		}

		return list;
	}

	/**
	 * Recupere le message avec le numero correspondant
	 * @param  numMess numero du message
	 * @return         message
	 */
	public synchronized Message getByNumMess(int numMess){
		for(Message m : messages){
			if(m.getNumero() == numMess)
				return m;
		}
		return null;
	}

	public synchronized Message getCourant(){
		Message m = null;
		try{
			m = messages.get(courant);
		}catch(IndexOutOfBoundsException e){
			m = null;
		}

		if(m != null)
			courantPP();
		return m;
	}


	/**
	 * Supprime le message avec le numero correspondant de la liste
	 * @param numMess numero du message
	 */
	public synchronized void removeByNumMess(int numMess){
		for(int i = 0; i < messages.size(); i++){
			Message m = messages.get(i);
			if(m.getNumero() == numMess){
				messages.remove(i);
				return;
			}
		}
	}

	/**
	 * Incremente le compteur
	 */
	private void compteurPP(){
		compteur++;
		if(compteur == MAX_MESSAGES)
			compteur = 0;
	}

	/**
	 * Increment l'index courant
	 */
	private void courantPP(){
		courant++;
		if(courant == MAX_MESSAGES)
			courant = 0;
	}

}