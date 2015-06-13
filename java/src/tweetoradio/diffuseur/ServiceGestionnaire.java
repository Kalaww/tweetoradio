package tweetoradio.diffuseur;

import tweetoradio.util.*;

import java.lang.Runnable;
import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class ServiceGestionnaire implements Runnable{
	
	/**
	 * Référence du diffuseur
	 */
	private Diffuseur diffuseur;

	/**
	 * Ip du gestionnaire
	 */
	private String ipGestionnaire;

	/**
	 * Port du gestionnaire
	 */
	private int portGestionnaire;

	/**
	 * Constructeur
	 * @param  _diffuseur référence du diffuseur
	 * @param  _ip        ip du gestionnaire
	 * @param  _port      port du gestionnaire
	 */
	public ServiceGestionnaire(Diffuseur _diffuseur, String _ip, int _port){
		diffuseur = _diffuseur;
		ipGestionnaire = _ip;
		portGestionnaire = _port;
	}

	public void run(){
		Socket socket = null;

		try{
			socket = new Socket(ipGestionnaire, portGestionnaire);
		}catch(IOException e){
			Log.printLog("[Service Gestionnaire] "+e.getMessage());
			return;
		}

		Log.printLog("[Service Gestionnaire] Connexion avec "+ipGestionnaire+":"+portGestionnaire);

		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(IOException e){
			Log.printLog("[Service Gestionnaire] "+e.getMessage());
			try{
				socket.close();
			}catch(IOException ee){
				Log.printLog("[Service Gestionnaire] "+ee.getMessage());
			}
			return;
		}

		PrintWriter pw = null;
		try{
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		}catch(IOException e){
			Log.printLog("[Service Gestionnaire] "+e.getMessage());
			try{
				socket.close();
			}catch(IOException ee){
				Log.printLog("[Service Gestionnaire] "+ee.getMessage());
			}
			return;
		}

		String message = MessageType.REGI+" "
						+Encode.id(diffuseur.getID())+" "
						+Encode.ip(diffuseur.getIPMultiDiffusion())+" "
						+Encode.port(diffuseur.getPortMultiDiffusion())+" "
						+Encode.ip(diffuseur.getIP())+" "
						+Encode.port(diffuseur.getPort())
						+"\r\n";


		Log.printDebug("[Service Gestionnaire] Envoi de: "+message.substring(0, message.length()-2));
		pw.print(message);
		pw.flush();

		String rep = null;
		try{
			rep = br.readLine();
		}catch(IOException e){
			Log.printLog("[Service Gestionnaire] "+e.getMessage());
			try{
				br.close();
				pw.close();
				socket.close();
			}catch(IOException ee){
				Log.printLog("[Service Gestionnaire] "+ee.getMessage());
			}
			return;
		}

		Log.printDebug("[Service Gestionnaire] Réception de: "+rep);
		rep += "\r\n";

		MessageParser mp = null;
		try{
			mp = Parse.parser(rep);
		}catch(TypeMessageInconnuException e){
			Log.printLog("[Service Gestionnaire] "+e.getMessage());
			mp = new MessageParser();
		}

		if(mp.type.equals(MessageType.REOK)){
			Log.printLog("[Service Gestionnaire] Inscription au gestionnaire réussi");
		}else if(mp.type.equals(MessageType.RENO)){
			Log.printLog("[Service Gestionnaire] Inscription au gestionnaire refusé");
			return;
		}else{
			Log.printLog("[Service Gestionnaire] Message recu inconnu");
			return;
		}

		while(true){
			String recv = null;
			try{
				recv = br.readLine();
			}catch(IOException e){
				Log.printLog("[Service Gestionnaire] "+e.getMessage());
				try{
					br.close();
					pw.close();
					socket.close();
				}catch(IOException ee){
					Log.printLog("[Service Gestionnaire] "+ee.getMessage());
				}
				return;
			}

			if(recv == null)
				break;

			Log.printDebug("[Service Gestionnaire] Réception de: "+recv);
			recv += "\r\n";

			MessageParser mpp = null;
			try{
				mpp = Parse.parser(recv);
			}catch(TypeMessageInconnuException e){
				Log.printLog("[Service Gestionnaire] "+e.getMessage());
				mpp = new MessageParser();
			}

			if(mpp.type.equals(MessageType.RUOK)){
				String m = MessageType.IMOK+"\r\n";
				Log.printDebug("[Service Gestionnaire] Envoi de: "+m.substring(0, m.length()-2));
				pw.print(m);
				pw.flush();
			}else{
				Log.printLog("[Service Gestionnaire] Message inconnu ");
				break;
			}
		}

		Log.printLog("[Service Gestionnaire] Déconnecté du gestionnaire");

	}

}