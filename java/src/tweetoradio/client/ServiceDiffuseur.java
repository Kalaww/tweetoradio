package tweetoradio.client;

import tweetoradio.util.*;
import tweetoradio.diffuseur.InfoDiffuseur;

import java.lang.Runnable;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ServiceDiffuseur implements Runnable{

	/**
	 * Référence dy cient
	 */
	private Client client;

	/**
	 * Type du message à envoyer
	 */
	private String choix;
	
	/**
	 * Constructeur
	 * @param  _client référence du client
	 */
	public ServiceDiffuseur(Client _client, String _choix){
		client = _client;

		if(!_choix.equals(MessageType.MESS) && !_choix.equals(MessageType.LAST)){
			Log.printDebug("[Service Diffuseur] Impossible d'envoyer un message de type "+_choix);
			System.exit(1);
		}
		choix = _choix;
	}

	public void run(){
		Socket socket = null;
		InfoDiffuseur infos = client.getInfoDiffuseurCourant();
		
		try{
			socket = new Socket(infos.ipMachine, infos.portMachine);
		}catch(IOException e){
			Log.printLog("[Service Diffuseur] "+e.getMessage());
			return;
		}

		Log.printDebug("[Service Diffuseur] Connexion avec "+infos.ipMachine+":"+infos.portMachine);


		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(IOException e){
			Log.printLog("[Service Diffuseur] "+e.getMessage());
			try{
				socket.close();
			}catch(IOException ee){
				Log.printLog("[Service Diffuseur] "+ee.getMessage());
			}
			return;
		}

		PrintWriter pw = null;
		try{
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		}catch(IOException e){
			Log.printLog("[Service Diffuseur] "+e.getMessage());
			try{
				socket.close();
			}catch(IOException ee){
				Log.printLog("[Service Diffuseur] "+ee.getMessage());
			}
			return;
		}


		if(choix.equals(MessageType.MESS)){
			String contenu = readMessage();

			String message = MessageType.MESS+" "+Encode.id(client.getID())+" "+Encode.message(contenu)+"\r\n";

			Log.printDebug("[Service Diffuseur] Envoi de: "+message.substring(0, message.length()-2));

			pw.print(message);
			pw.flush();

			String reponse = null;
			try{
				reponse = br.readLine();
			}catch(IOException e){
				Log.printLog("[Service Diffuseur] "+e.getMessage());
				try{
					br.close();
					pw.close();
					socket.close();
				}catch(IOException ee){
					Log.printLog("[Service Diffuseur] "+ee.getMessage());
				}
				return;
			}

			Log.printDebug("[Service Diffuseur] Réception de: "+reponse);
			reponse += "\r\t";

			MessageParser mp = null;
			try{
				mp = Parse.parser(reponse);
			}catch(TypeMessageInconnuException e){
				Log.printLog("[Service Diffuseur] "+e.getMessage());
				mp = new MessageParser();
			}

			if(!mp.type.equals(MessageType.ACKM))
				Log.printLog("[Service Diffuseur] Le diffuseur n'a pas répondu ACKM");

		}else if(choix.equals(MessageType.LAST)){
			int val = readNbLastMess();

			String message = MessageType.LAST+" "+Encode.nbMess(val)+"\r\t";

			Log.printDebug("[Service Diffuseur] Envoi de: "+message.substring(0, message.length()-2));

			pw.print(message);
			pw.flush();

			while(true){
				String reponse = null;
				try{
					reponse = br.readLine();
				}catch(IOException e){
					Log.printLog("[Service Diffuseur] "+e.getMessage());
					try{
						br.close();
						pw.close();
						socket.close();
					}catch(IOException ee){
						Log.printLog("[Service Diffuseur] "+ee.getMessage());
					}
					return;
				}

				Log.printDebug("[Service Diffuseur] Réception de: "+reponse);
				reponse += "\r\t";

				MessageParser mp = null;
				try{
					mp = Parse.parser(reponse);
				}catch(TypeMessageInconnuException e){
					Log.printLog("[Service Diffuseur] "+e.getMessage());
					mp = new MessageParser();
				}

				if(mp.type.equals(MessageType.OLDM)){
					Log.print1(new Message(mp.id, mp.contenu).afficher());
				}else if(mp.type.equals(MessageType.ENDM)){
					break;
				}
			}
		}

		try{
			br.close();
			pw.close();
			socket.close();
		}catch(IOException e){
			Log.printLog("[Service Diffuseur] "+e.getMessage());
		}		

	}

	/**
	 * Récupère le message que souhaite envoyer le client
	 * @return le message
	 */
	private String readMessage(){
		Scanner sc = new Scanner(System.in);
		String message = "";
		do{
			Log.print1("Tapez le message à envoyer :");
			message = sc.nextLine();
			if(message.length() <= 140)
				break;
			Log.print1("Le message doit être de 140 caratères maximum");
		}while(true);
		return message;
	}

	/**
	 * Récupère le nombre de dernier message que souhaite le client
	 * @return nombre de dernier message
	 */
	private int readNbLastMess(){
		Scanner sc = new Scanner(System.in);
		int val = -1;
		do{
			Log.print1("Combien de voulez-vous récupérer de derniers messages ?");
			val = sc.nextInt();
			if(val >= 0 && val < 1000)
				break;
			Log.print1("La valeur doit être entre 0 et 999");
		}while(true);
		return val;
	}


}