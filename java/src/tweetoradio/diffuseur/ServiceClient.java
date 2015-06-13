package tweetoradio.diffuseur;

import tweetoradio.util.*;

import java.lang.Runnable;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;


/**
 * Service du diffuseur pour le mode Client TCP
 */
public class ServiceClient implements Runnable{
	
	/**
	 * Référence du diffuseur
	 */
	private Diffuseur diffuseur;

	/**
	 * Constructeur
	 * @param  _diffuseur référence du diffuseur
	 */
	public ServiceClient(Diffuseur _diffuseur){
		diffuseur = _diffuseur;
	}

	public void run(){
		ServerSocket server = null;
		try{
			server = new ServerSocket(diffuseur.getPort());
		}catch(IOException e){
			Log.printLog("[Service Client] "+e.getMessage());
			Log.printLog("[Service Client] Echec du démarrage du service");
			return;
		}

		Log.printLog("[Service Client] Serveur TCP en écoute sur le port "+diffuseur.getPort());

		while(true){
			Socket socketClient = null;
			try{
				socketClient = server.accept();
			}catch(IOException e){
				Log.printLog("[Service Client] "+e.getMessage());
				continue;
			}

			Log.printLog("[Service Client] Nouveau client connecté");

			Thread t = new Thread(new ClientConnecte(socketClient, diffuseur));
			t.start();
		}

	}

}

/**
 * Sous service pour les accepts des clients
 */
class ClientConnecte implements Runnable{

	/**
	 * Socket du client Client
	 */
	private Socket socket;

	/**
	 * Référence du diffuseur
	 */
	private Diffuseur diffuseur;

	/**
	 * Constructeur
	 * @param  _socket socket du client
	 * @param  _diffuseur référence du diffuseur
	 */
	public ClientConnecte(Socket _socket, Diffuseur _diffuseur){
		socket = _socket;
		diffuseur = _diffuseur;
	}

	public void run(){
		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(IOException e){
			Log.printLog("[Service Client] "+e.getMessage());
			try{
				socket.close();
			}catch(IOException ee){
				Log.printLog("[Service Client] "+ee.getMessage());
			}
			return;
		}

		PrintWriter pw = null;
		try{
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		}catch(IOException e){
			Log.printLog("[Service Client] "+e.getMessage());
			try{
				socket.close();
			}catch(IOException ee){
				Log.printLog("[Service Client] "+ee.getMessage());
			}
			return;
		}

		String recv = null;
		try{
			recv = br.readLine();
		}catch(IOException e){
			Log.printLog("[Service Client] "+e.getMessage());
			try{
				br.close();
				pw.close();
				socket.close();
			}catch(IOException ee){
				Log.printLog("[Service Client] "+ee.getMessage());
			}
			return;
		}

		Log.printDebug("[Service Client] Réception de: "+recv);
		recv += "\r\t";

		MessageParser mp = null;
		try{
			mp = Parse.parser(recv);
		}catch(TypeMessageInconnuException e){
			Log.printLog("[Service Client] "+e.getMessage());
			mp = new MessageParser();
		}

		// MESS
		if(mp.type.equals(MessageType.MESS)){
			diffuseur.getMessagesList().addMessage(mp.id, mp.contenu);

			pw.print(MessageType.ACKM+"\r\n");
			pw.flush();

		// LAST
		}else if(mp.type.equals(MessageType.LAST)){
			ArrayList<String> list = diffuseur.getMessagesList().getLastMessages(mp.nbLast);
			String envoi = "";
			for(String s : list){
				envoi = MessageType.OLDM+" "+s+"\r\n";
				Log.printDebug("[Service Client] Envoi de: "+envoi.substring(0, envoi.length()-2));

				pw.print(envoi);
				pw.flush();
			}

			envoi = MessageType.ENDM+"\r\n";
			Log.printDebug("[Service Client] Envoi de: "+envoi.substring(0, envoi.length()-2));
			pw.print(envoi);
			pw.flush();

		}else{
			Log.printLog("[Service Client] Ce service ne gère que des messages de type MESS ou LAST");
		}

		try{
			br.close();
			pw.close();
			socket.close();
		}catch(IOException e){
			Log.printLog("[Service Client] "+e.getMessage());
		}
	}
}