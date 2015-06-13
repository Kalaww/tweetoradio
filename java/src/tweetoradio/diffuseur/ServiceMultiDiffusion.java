package tweetoradio.diffuseur;

import tweetoradio.util.*;

import java.lang.Runnable;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import java.io.IOException;
import java.lang.InterruptedException;
import java.net.SocketException;

/**
 * Sous partie d'un diffuseur qui s'occupe de la
 * diffusion des messages
 */
public class ServiceMultiDiffusion implements Runnable{

	/**
	 * Temps de pause entre l'envoi de deux messages
	 */
	private static final long SLEEP_TIME = 5000;
	
	/**
	 * Reference du diffuseur
	 */
	private Diffuseur diffuseur;

	/**
	 * Constructeur
	 * @param  _diffuseur reference du diffuseur
	 */
	public ServiceMultiDiffusion(Diffuseur _diffuseur){
		diffuseur = _diffuseur;
	}

	public void run(){
		DatagramSocket socket;
		try{
			socket = new DatagramSocket();
		}catch(SocketException e){
			Log.printLog("[Service MultiDiff] "+e.getMessage());
			return;
		}

		Log.printLog("[Service MultiDiff] Serveur multi-diffusion démarré sur "+diffuseur.getIPMultiDiffusion()+":"+diffuseur.getPortMultiDiffusion());

		InetSocketAddress address = new InetSocketAddress(diffuseur.getIPMultiDiffusion(), diffuseur.getPortMultiDiffusion());
		DatagramPacket paquet = null;

		while(true){
			Message message = diffuseur.getMessagesList().getCourant();
			if(message != null){
				String send = MessageType.DIFF + " " + message.encoder() + "\r\n";
				byte[] data = send.getBytes();

				Log.printDebug("[Service MultiDiff] Envoi du message: "+send.substring(0, send.length()-2));

				try{
					paquet = new DatagramPacket(data, data.length, address);
				}catch(SocketException e){
					Log.printLog("[Service MultiDiff] "+e.getMessage());
					return;
				}

				try{
					socket.send(paquet);
				}catch(IOException e){
					Log.printLog("[Service MultiDiff] "+e.getMessage());
					return;
				}
			}else{
				Log.printDebug("[Service MultiDiff] Aucun message à envoyer");
			}

			try{
				Thread.sleep(SLEEP_TIME);
			}catch(InterruptedException e){
				Log.printLog("[Service MultiDiff] "+e.getMessage());
			}
		}
	}

}