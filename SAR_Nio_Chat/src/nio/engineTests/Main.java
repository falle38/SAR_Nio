package nio.engineTests;


/*** Main NIO ***/

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		Scanner Serverport = new Scanner(System.in);
		System.out.println("Veuillez saisir le port du serveur :");
		final String str = Serverport.nextLine();
		System.out.println("Le port du serveur vaut : " + str);
		
		
		
		new Thread(new NioServerTest(Integer.parseInt(str))).start();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		new Thread(new NioClientTest("localhost",Integer.parseInt(str))).start();
	}
	
	
}
