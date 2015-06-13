package tweetoradio.client;

import tweetoradio.diffuseur.InfoDiffuseur;
import tweetoradio.util.*;

import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.lang.InterruptedException;

public class Client{
	
	/**
	 * Identifiant
	 */
	private String id;

	/**
	 * Ip du gestionnaire de diffuseur
	 */
	private String ipGestionnaire;

	/**
	 * Port d'un gestionnaire de diffuseur
	 */
	private int portGestionnaire;

	/**
	 * Informations du diffuseur choisi par le client
	 */
	private InfoDiffuseur infoDiffuseurCourant;

	/**
	 * Liste des diffuseurs connus
	 */
	private ArrayList<InfoDiffuseur> diffuseurs;


	public static void main(String[] args){
		Client c = new Client();

		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-c")){
				HashMap<String, String> map = Config.read(args[++i]);
				for(Entry<String, String> entry : map.entrySet()){
					String k = entry.getKey();
					String v = entry.getValue();
					if(k.equals("id"))
						c.id = v;
					else if(k.equals("ip_gestionnaire"))
						c.ipGestionnaire = v;
					else if(k.equals("port_gestionnaire"))
						c.portGestionnaire = Integer.parseInt(v);
					else if(k.equals("debug") && (v.equals("1") || v.equals("true")))
						Log.DEBUG = true;
					else if(k.equals("out_log"))
						Log.LOG_OUT = v;
					else if(k.equals("out_debug"))
						Log.DEBUG_OUT = v;
					else if(k.equals("out_1"))
						Log.OUT_1 = v;
					else if(k.equals("out_2"))
						Log.OUT_2 = v;
				}
			}
		}

		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-id"))
				c.id = args[++i];
			else if(args[i].equals("-ipG"))
				c.ipGestionnaire = args[++i];
			else if(args[i].equals("-pG"))
				c.portGestionnaire = Integer.parseInt(args[++i]);
			else if(args[i].equals("-d"))
				Log.DEBUG = true;
			else if(args[i].equals("-outL"))
				Log.LOG_OUT = args[++i];
			else if(args[i].equals("-outD"))
				Log.DEBUG_OUT = args[++i];
			else if(args[i].equals("-out1"))
				Log.OUT_1 = args[++i];
			else if(args[i].equals("-out2"))
				Log.OUT_2 = args[++i];
		}

		if(Log.LOG_OUT.equals(""))
			Log.LOG_OUT = Log.OUT_2;

		if(Log.DEBUG_OUT.equals(""))
			Log.DEBUG_OUT = Log.OUT_2;

		c.start();
	}
	
	/**
	 * Constructeur
	 */
	public Client(){
		diffuseurs = new ArrayList<InfoDiffuseur>();
	}

	/**
	 * Boucle du client
	 */
	public void start(){
		Thread multiDiff = null;
		ServiceDiffusion servMultiDiff = null;

		Scanner sc = new Scanner(System.in);
		InfoDiffuseur oldInfoDiffuseur = infoDiffuseurCourant;
		
		while(true){
			// Change le thread de communication avec le multi diffuseur
			if(oldInfoDiffuseur != infoDiffuseurCourant){
				if(servMultiDiff != null) servMultiDiff.close();
				try{
					if(multiDiff != null) multiDiff.join();
				}catch (InterruptedException e){
					Log.printLog("[Client] "+e.getMessage());
				}

				servMultiDiff = new ServiceDiffusion(this);
				multiDiff = new Thread(servMultiDiff);
				multiDiff.start();

				oldInfoDiffuseur = infoDiffuseurCourant;
			}

			//Test si les infos d'un gestionnaire sont présentes
			boolean newGestio = (ipGestionnaire == null || ipGestionnaire == "") && portGestionnaire == 0;

			// Menu
			Log.print1("== Menu ==");
			if(newGestio)
				Log.print1("[c] Connexion à un gestionnaire");
			else{
				Log.print1("[c] Modifier le gestionnaire ("+ipGestionnaire+":"+portGestionnaire+")");
				Log.print1("[l] Liste des diffuseurs");
			}

			if(multiDiff != null){
				Log.print1("[m] Envoyer un message");
				Log.print1("[o] Récupérer les n derniers messages");
			}

			Log.print1("[q] Quitter");

			String choix = sc.nextLine();
			
			// Changement de gestionnaire
			if(choix.equals("c")){
				Log.print1("IPv4 du gestionnaire :");
				ipGestionnaire = sc.nextLine();

				Log.print1("Port du gestionnaire :");
				int newport = 0;
				try{
					newport = Integer.parseInt(sc.nextLine());
				}catch(NumberFormatException e){
					Log.printLog("[Client] "+e.getMessage());
				}
				if(newport != 0)
					portGestionnaire = newport;

			// List des diffuseurs
			}else if(choix.equals("l") && !newGestio){
				Thread tGEstio = new Thread(new ServiceGestionnaire(this));
				tGEstio.start();
				try{
					tGEstio.join();
				}catch (InterruptedException e){
					Log.printLog("[Client] "+e.getMessage());
				}

				HashMap<Integer, InfoDiffuseur> map = new HashMap<Integer, InfoDiffuseur>();
				int cpt = 1;
				Log.print1("== Connexion à un diffuseur ==");
				for(InfoDiffuseur i : diffuseurs){
					Log.print1("["+cpt+"] "+i);
					map.put(cpt, i);
					cpt++;
				}
				Log.print1("[0] Ne pas choisir de diffuseur");

				int cptChoix = 0;
				try{
					cptChoix = Integer.parseInt(sc.nextLine());
				}catch(NumberFormatException e){
					Log.printLog("[Client] "+e.getMessage());
				}

				if(cptChoix == 0)
					continue;
				else if(map.containsKey(new Integer(cptChoix))){
					infoDiffuseurCourant = map.get(new Integer(cptChoix));
				}

			// Envoi message
			}else if(choix.equals("m") && multiDiff != null){
				Thread connecte = new Thread(new ServiceDiffuseur(this, MessageType.MESS));
				connecte.start();
				try{
					connecte.join();
				}catch(InterruptedException e){
					Log.printLog("[Client] "+e.getMessage());
				}

			// Anciens messages
			}else if(choix.equals("o") && multiDiff != null){
				Thread connecte = new Thread(new ServiceDiffuseur(this, MessageType.LAST));
				connecte.start();
				try{
					connecte.join();
				}catch(InterruptedException e){
					Log.printLog("[Client] "+e.getMessage());
				}

			// Quitter
			}else if(choix.equals("q")){
				System.exit(0);
			}
		}
	}


	/**
	 * Getter de l'identifiant du client
	 * @return identifiant
	 */
	public String getID(){
		return id;
	}

	/**
	 * Getter ip du gestionnaire de diffuseur
	 * @return ip du gestionnaire
	 */
	public String getIpGestionnaire(){
		return ipGestionnaire;
	}

	/**
	 * Getter port du gestionnaire de diffuseur
	 * @return port de gestionnaire
	 */
	public int getPortGestionnaire(){
		return portGestionnaire;
	}

	/**
	 * Getter liste des diffuseurs
	 * @return liste des diffuseurs
	 */
	public ArrayList<InfoDiffuseur> getDiffuseurs(){
		return diffuseurs;
	}

	/**
	 * Getter des informations du diffuseur courant
	 * @return informations diffuseur
	 */
	public InfoDiffuseur getInfoDiffuseurCourant(){
		return infoDiffuseurCourant;
	}

}