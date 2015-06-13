package tweetoradio.util;

/**
 * Encode les differentes donnees transmises
 * selon les restrictions imposees par le sujet
 */
public abstract class Encode{
	
	/**
	 * Encode le numero de message
	 * @param  numero le numero
	 * @return        version encode
	 */
	public static String numMessage(int numero){
		String m = numero+"";
		while(m.length() < 4)
			m = "0"+m;

		return m;
	}

	/**
	 * Encode l'identifiant
	 * @param  identifiant l'identifiant
	 * @return             version encode
	 */
	public static String id(String identifiant){
		String m = new String(identifiant);
		while(m.length() < 8)
			m = m + "#";

		return m;
	}

	/**
	 * Encode le contenu d'un message
	 * @param  contenu contenu d'un message
	 * @return         version encode
	 */
	public static String message(String contenu){
		String m = new String(contenu);
		while(m.length() < 140)
			m = m + "#";

		return m;
	}

	/**
	 * Encode le nombre de message
	 * @param  nb nombre de message
	 * @return    version encode
	 */
	public static String nbMess(int nb){
		String m = nb + "";
		while(m.length() < 3)
			m = "0"+m;

		return m;
	}


	/**
	 * Encode ipv4
	 * @param  addresse ip
	 * @return          ip encodé
	 */
	public static String ip(String addresse){
		String m = "";
		String[] split = addresse.split("\\.");
		for(int i = 0; i < split.length; i++){
			m += nbMess(Integer.parseInt(split[i]));
			if(m.length() < 15)
				m += ".";
		}
		return m;
	}

	/**
	 * Encode un port
	 * @param  p le port
	 * @return   version encode
	 */
	public static String port(int p){
		return new String(p+"");
	}


	/**
	 * Encode le nombre de diffuseur
	 * @param  num nombre de diffuseur
	 * @return     version encode
	 */
	public static String numDiff(int num){
		String m = num + "";
		while(m.length() < 2)
			m = "0"+m;

		return m;
	}	
}