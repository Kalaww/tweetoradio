package tweetoradio.gestionnaire;

import tweetoradio.util.*;
import tweetoradio.diffuseur.InfoDiffuseur;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;

public class Gestionnaire{
	
	/**
	 * Port de communication
	 */
	private int port;

	/**
	 * Nombre maximal de diffuseurs enregistrés sur le gestionnaire
	 */
	private int maxDiffuseurs = 10;


	/**
	 * Liste des diffuseurs enregistrés
	 */
	private ArrayList<InfoDiffuseur> diffuseurs;


	public static void main(String[] args){
		Gestionnaire g = new Gestionnaire();

		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-c")){
				HashMap<String, String> map = Config.read(args[++i]);
				for(Entry<String, String> entry : map.entrySet()){
					String k = entry.getKey();
					String v = entry.getValue();
					if(k.equals("port_tcp"))
						g.port = Integer.parseInt(v);
					else if(k.equals("max_diffuseurs"))
						g.maxDiffuseurs = Integer.parseInt(v);
					else if(k.equals("debug") && (v.equals("1") || v.equals("true")))
						Log.DEBUG = true;
					else if(k.equals("out_log"))
						Log.LOG_OUT = v;
					else if(k.equals("out_debug"))
						Log.DEBUG_OUT = v;
				}
			}
		}

		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-p"))
				g.port = Integer.parseInt(args[++i]);
			else if(args[i].equals("-m"))
				g.maxDiffuseurs = Integer.parseInt(args[++i]);
			else if(args[i].equals("-d"))
				Log.DEBUG = true;
			else if(args[i].equals("-outL"))
				Log.LOG_OUT = args[++i];
			else if(args[i].equals("-outD"))
				Log.DEBUG_OUT = args[++i];
		}

		g.start();
	}


	/**
	 * Constructeur
	 */
	public Gestionnaire(){
		diffuseurs = new ArrayList<InfoDiffuseur>();
	}


	/**
	 * Boucle du gestionanire
	 */
	public void start(){
		Thread threadConnecte = new Thread(new ServiceConnecte(this));

		threadConnecte.start();
	}


	/**
	 * Ajoute un diffuseur à la liste des diffuseurs enregistrés
	 * sur le gestionnaire
	 * @param  infos informations du diffuseur
	 * @return       true si l'ajout est possible, false sinon
	 */
	public boolean addDiffuseur(InfoDiffuseur infos){
		if(diffuseurs.size() >= maxDiffuseurs)
			return false;

		for(InfoDiffuseur i : diffuseurs){
			if(i.id.equals(infos.id))
				return false;
		}

		diffuseurs.add(infos);
		return true;
	}

	/**
	 * Supprime le diffuseur de la liste des diffuseurs enregistrés
	 * @param  infos informations du diffuseur
	 * @return       true s'il a été supprimé, false sinon
	 */
	public boolean removeDiffuseur(InfoDiffuseur infos){
		for(int k = 0; k < diffuseurs.size(); k++){
			InfoDiffuseur i = diffuseurs.get(k);
			if(i.id.equals(infos.id)){
				diffuseurs.remove(k);
				return true;
			}
		}
		return false;
	}

	/**
	 * Getter de la liste des diffuseurs
	 * @return liste des diffuseurs
	 */
	public ArrayList<InfoDiffuseur> getDiffuseurs(){
		return diffuseurs;
	}

	/**
	 * Getter du port de communication
	 * @return port
	 */
	public int getPort(){
		return port;
	}
}