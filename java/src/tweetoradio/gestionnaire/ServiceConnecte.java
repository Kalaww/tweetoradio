package tweetoradio.gestionnaire;

import tweetoradio.util.*;
import tweetoradio.diffuseur.InfoDiffuseur;

import java.lang.Runnable;
import java.util.ArrayList;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


/**
 * Service de communication avec les clients et les diffuseurs
 */
public class ServiceConnecte implements Runnable{
	
	/**
	 * Référence du gestionnaire
	 */
	private Gestionnaire gestionnaire;

	/**
	 * Constructeur
	 * @param  _gestionnaire référence du gestionnaire
	 */
	public ServiceConnecte(Gestionnaire _gestionnaire){
		gestionnaire = _gestionnaire;
	}

	public void run(){
		ServerSocket server = null;
		try{
			server = new ServerSocket(gestionnaire.getPort());
		}catch(IOException e){
			Log.printLog(e.getMessage());
			Log.printLog("Echec du démarrage du service");
			return;
		}

		Log.printLog("Serveur TCP en écoute sur le port "+gestionnaire.getPort());

		while(true){
			Socket socketClient = null;
			try{
				socketClient = server.accept();
			}catch(IOException e){
				Log.printLog(e.getMessage());
				continue;
			}

			Log.printLog("Nouvelle connexion d'un client/diffuseur");

			Thread t = new Thread(new SocketConnecte(socketClient, gestionnaire));
			t.start();
		}
	}
}


/**
 * Sous service pour les accepts des diffuseurs/clients
 */
class SocketConnecte implements Runnable{

	/**
	 * Temps de pause entre l'envoi de message aux diffuseurs
	 */
	private static final long SLEEP_TIME = 30 * 1000;


	/**
	 * Temps d'attente maximal du gestionnaire pour la reponse IMOK des diffuseurs
	 */
	private static final int TIMEOUT_READ = 10 * 1000;

	/**
	 * Socket du diffuseur connecté
	 */
	private Socket socket;

	/**
	 * Référence du gestionnaire
	 */
	private Gestionnaire gestionnaire;

	/**
	 * Informations du diffuseur connecté
	 */
	private InfoDiffuseur infos;


	/**
	 * Constructeur
	 * @param  _socket       socket du diffuseur
	 * @param  _gestionnaire référence du gestionnaire
	 */
	public SocketConnecte(Socket _socket, Gestionnaire _gestionnaire){
		socket = _socket;
		try{
			socket.setSoTimeout(TIMEOUT_READ);
		}catch(SocketException e){
			Log.printLog(e.getMessage());
			System.exit(1);
		}
		gestionnaire = _gestionnaire;
	}

	public void run(){
		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(IOException e){
			Log.printLog(e.getMessage());
			try{
				socket.close();
			}catch(IOException ee){
				Log.printLog(ee.getMessage());
			}
			return;
		}

		PrintWriter pw = null;
		try{
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		}catch(IOException e){
			Log.printLog(e.getMessage());
			try{
				socket.close();
			}catch(IOException ee){
				Log.printLog(ee.getMessage());
			}
			return;
		}


		String recv = null;
		try{
			recv = br.readLine();
		}catch(IOException e){
			Log.printLog(e.getMessage());
			try{
				br.close();
				pw.close();
				socket.close();
			}catch(IOException ee){
				Log.printLog(ee.getMessage());
			}
			return;
		}

		Log.printDebug("Réception de: "+recv);
		recv += "\r\n";

		MessageParser mp = null;
		try{
			mp = Parse.parser(recv);
		}catch(TypeMessageInconnuException e){
			Log.printLog(e.getMessage());
			mp = new MessageParser();
		}

		// REGI
		if(mp.type.equals(MessageType.REGI)){
			infos = new InfoDiffuseur();
			infos.id = mp.id;
			infos.ipMachine = mp.ipMachine;
			infos.portMachine = mp.portMachine;
			infos.ipMultiDiffusion = mp.ipMultiDiffusion;
			infos.portMultiDiffusion = mp.portMultiDiffusion;

			Log.printLog("[Service Diffuseur] ["+infos.id+"] Demande d'inscription");

			if(gestionnaire.addDiffuseur(infos)){
				Log.printLog("[Service Diffuseur] ["+infos.id+"] Inscription autorisée");
				String m = MessageType.REOK+"\r\n";
				Log.printDebug("[Service Diffuseur] ["+infos.id+"] Envoi de: "+m.substring(0, m.length()-2));
				pw.print(m);
				pw.flush();

				boolean connected = true;
				while(connected){
					try{
						Thread.sleep(SLEEP_TIME);
					}catch(InterruptedException e){
						Log.printLog("[Service Diffuseur] ["+infos.id+"] "+e.getMessage());
					}

					String m1 = MessageType.RUOK+"\r\n";
					Log.printDebug("[Service Diffuseur] ["+infos.id+"] Envoi de: "+m1.substring(0, m1.length()-2));
					pw.print(m1);
					pw.flush();

					String rep = null;
					try{
						rep = br.readLine();
					}catch(SocketTimeoutException e){
						connected = false;
						continue;
					}catch(IOException e){
						Log.printLog("[Service Diffuseur] ["+infos.id+"] "+e.getMessage());
						try{
							br.close();
							pw.close();
							socket.close();
						}catch(IOException ee){
							Log.printLog("[Service Diffuseur] ["+infos.id+"] "+ee.getMessage());
						}
						return;
					}

					if(rep == null){
						connected = false;
						continue;
					}

					Log.printDebug("[Service Diffuseur] ["+infos.id+"] Réception de: "+rep);
					rep += "\r\n";

					MessageParser mpp = null;
					try{
						mpp = Parse.parser(rep);
					}catch(TypeMessageInconnuException e){
						Log.printLog("[Service Diffuseur] ["+infos.id+"] "+e.getMessage());
						mpp = new MessageParser();
					}

					if(!mpp.type.equals(MessageType.IMOK)){
						Log.printLog("[Service Diffuseur] ["+infos.id+"]  Message recu inconnu");
						connected = false;
					}

				}

				gestionnaire.removeDiffuseur(infos);
				Log.printLog("[Service Diffuseur] ["+infos.id+"] Le diffuseur est déconnecté");
			}else{
				Log.printLog("[Service Diffuseur] ["+infos.id+"] Inscription refusée");
				String m2 = MessageType.RENO+"\r\n";
				Log.printDebug("[Service Diffuseur] ["+infos.id+"] Envoi de: "+m2.substring(0, m2.length()-2));
				pw.print(m2);
				pw.flush();
			}

		// LIST
		}else if(mp.type.equals(MessageType.LIST)){
			ArrayList<InfoDiffuseur> list = gestionnaire.getDiffuseurs();

			String send = MessageType.LINB+" "+Encode.numDiff(list.size())+"\r\n";
			Log.printDebug("[Service Client] Envoi de: "+send.substring(0, send.length()-2));
			Log.printLog("[Service Client] Envoi de la liste des diffuseurs");
			pw.print(send);
			pw.flush();

			for(InfoDiffuseur i : list){
				send = MessageType.ITEM+" "
						+Encode.id(i.id)+" "
						+Encode.ip(i.ipMultiDiffusion)+" "
						+Encode.port(i.portMultiDiffusion)+" "
						+Encode.ip(i.ipMachine)+" "
						+Encode.port(i.portMachine)
						+"\r\n";
				Log.printDebug("[Service Client] Envoi de: "+send.substring(0, send.length()-2));
				pw.print(send);
				pw.flush();
			}
		}

		try{
			br.close();
			pw.close();
			socket.close();
		}catch(IOException e){
			Log.printLog(e.getMessage());
		}
	}
}