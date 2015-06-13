package tweetoradio.util;

/**
 * Parse les donnees transmises sur le reseau
 * selon les restrictions imposees par le sujet
 */
public class Parse{
	
	/**
	 * Parse un message encodé
	 * @param  mess                        message encodé
	 * @return                             message parsé
	 * @throws TypeMessageInconnuException exception si le message est mal encodé
	 */
	public static MessageParser parser(String mess) throws TypeMessageInconnuException{
		String type = mess.substring(0, 4);
		MessageParser m = new MessageParser();

		// MESS
		if(type.equals(MessageType.MESS) && mess.length() == MessageType.SIZE_MESS){
			m.type = MessageType.MESS;
			m.id = parserID(mess.substring(5, 13));
			m.contenu = parserContenu(mess.substring(14, 154));

		// DIFF
		}else if(type.equals(MessageType.DIFF) && mess.length() == MessageType.SIZE_DIFF){
			m.type = MessageType.DIFF;
			m.id = parserID(mess.substring(10, 18));
			m.contenu = parserContenu(mess.substring(19, 160));

		// ACKM
		}else if(type.equals(MessageType.ACKM) && mess.length() == MessageType.SIZE_ACKM){
			m.type = MessageType.ACKM;

		// LAST
		}else if(type.equals(MessageType.LAST) && mess.length() == MessageType.SIZE_LAST){
			m.type = MessageType.LAST;
			m.nbLast = parserNbMess(mess.substring(5, 8));

		// OLDM
		}else if(type.equals(MessageType.OLDM) && mess.length() == MessageType.SIZE_OLDM){
			m.type = MessageType.OLDM;
			m.id = parserID(mess.substring(10, 18));
			m.contenu = parserContenu(mess.substring(19, 160));

		// ENDM
		}else if(type.equals(MessageType.ENDM) && mess.length() == MessageType.SIZE_ENDM){
			m.type = MessageType.ENDM;

		// REGI
		}else if(type.equals(MessageType.REGI) && mess.length() == MessageType.SIZE_REGI){
			m.type = MessageType.REGI;
			m.id = parserID(mess.substring(5, 13));
			m.ipMultiDiffusion = mess.substring(14, 29);
			m.portMultiDiffusion = parserPort(mess.substring(30, 34));
			m.ipMachine = mess.substring(35, 50);
			m.portMachine = parserPort(mess.substring(51, 55));

		// REOK
		}else if(type.equals(MessageType.REOK) && mess.length() == MessageType.SIZE_REOK){
			m.type = MessageType.REOK;

		// RENO
		}else if(type.equals(MessageType.RENO) && mess.length() == MessageType.SIZE_RENO){
			m.type = MessageType.RENO;

		// RUOK
		}else if(type.equals(MessageType.RUOK) && mess.length() == MessageType.SIZE_RUOK){
			m.type = MessageType.RUOK;

		// IMOK
		}else if(type.equals(MessageType.IMOK) && mess.length() == MessageType.SIZE_IMOK){
			m.type = MessageType.IMOK;

		// LIST
		}else if(type.equals(MessageType.LIST) && mess.length() == MessageType.SIZE_LIST){
			m.type = MessageType.LIST;

		// LINB
		}else if(type.equals(MessageType.LINB) && mess.length() == MessageType.SIZE_LINB){
			m.type = MessageType.LINB;
			m.nbDiff = parserNbDiff(mess.substring(5, 7));

		// ITEM
		}else if(type.equals(MessageType.ITEM) && mess.length() == MessageType.SIZE_ITEM){
			m.type = MessageType.ITEM;
			m.id = parserID(mess.substring(5, 13));
			m.ipMultiDiffusion = mess.substring(14, 29);
			m.portMultiDiffusion = parserPort(mess.substring(30, 34));
			m.ipMachine = mess.substring(35, 50);
			m.portMachine = parserPort(mess.substring(51, 55));
			
		// Message inconnu
		}else{
			throw new TypeMessageInconnuException(mess);
		}

		return m;
	}


	/**
	 * Décode l'identifiant
	 * @param  m identifiant encodé
	 * @return   identifiant décodé
	 */
	private static String parserID(String m){
		String res = "";
		for(int i = 0; i < m.length(); i++){
			if(m.charAt(i) != '#')
				res += m.charAt(i);
		}
		return res;
	}


	/**
	 * Décode le nombre de message
	 * @param  m nombre de message encodé
	 * @return   nombre de message décodé
	 */
	private static int parserNbMess(String m){
		return Integer.parseInt(m);
	}


	/**
	 * Décode le nombre de diffuseur
	 * @param  m nombre diffuseur encodé
	 * @return   nombre de diffuseur décodé
	 */
	private static int parserNbDiff(String m){
		return Integer.parseInt(m);
	}


	/**
	 * Décode le port
	 * @param  m port codé
	 * @return   port décodé
	 */
	private static int parserPort(String m){
		return Integer.parseInt(m);
	}


	/**
	 * Decode le contenu d'un message
	 * @param  m contenu encodé
	 * @return   contenu décodé
	 */
	private static String parserContenu(String m){
		String res = "";
		for(int i = 0; i < m.length(); i++){
			if(m.charAt(i) != '#')
				res += m.charAt(i);
		}
		return res;
	}
}