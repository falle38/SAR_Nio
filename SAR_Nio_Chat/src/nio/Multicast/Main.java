package nio.Multicast;

import java.net.UnknownHostException;
import java.util.Scanner;


public class Main {
	
public static void main(String[] args) throws UnknownHostException {
		
	/*
	    System.out.println("here we start");
		Scanner Serverport = new Scanner(System.in);
		System.out.println("Veuillez saisir le port du serveur :");
		final String str = Serverport.nextLine();
		System.out.println("Le port du serveur vaut : " + str);
	*/	
		
		
		
		new Thread(new Server(1999)).start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		new Thread(new Client(1,1999,2222)).start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		new Thread(new Client(2,1999,3333)).start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		new Thread(new Client(2,1999,4444)).start();
		

	}
	

}

