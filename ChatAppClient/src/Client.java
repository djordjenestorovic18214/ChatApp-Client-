import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

	static Socket communicationSocket = null;
	static PrintStream outStreamToServer = null;
	static BufferedReader inStreamFromServer = null;
	static BufferedReader console = null;
	static boolean kraj = false;
	//udp
	static DatagramPacket packetFromServer=null;
	static DatagramSocket socketUDP = null;
	static byte[] dataForServer = new byte[1024];
	static byte[] dataFromServer = new byte[1024]; 
	static String host;

	public static void main(String[] args) {
		try {
			int port = 23789;

			if (args.length > 0)
				port = Integer.parseInt(args[0]);

			communicationSocket = new Socket("localhost", port);

			console = new BufferedReader(new InputStreamReader(System.in));
			outStreamToServer = new PrintStream(communicationSocket.getOutputStream());
			inStreamFromServer = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
			
			//udp
			socketUDP=new DatagramSocket();
			outStreamToServer.println(socketUDP.getLocalPort()+" "+InetAddress.getLocalHost());

			new Thread(new Client()).start();

			while (!kraj) {
				outStreamToServer.println(console.readLine());
			}
			communicationSocket.close();

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host");
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	@Override
	public void run() {
		String textlineFromServer, udp;
		packetFromServer= new  DatagramPacket(dataFromServer, dataFromServer.length);
		
		try {
			while ((textlineFromServer = inStreamFromServer.readLine()) != null) {
				//udp part
				if(textlineFromServer.startsWith("udp")){
					packetFromServer= new  DatagramPacket(dataFromServer, dataFromServer.length);
					socketUDP.receive(packetFromServer);
					udp=new String(packetFromServer.getData()).trim();
					System.out.println(udp);
					continue;
				}

				System.out.println(textlineFromServer);

				if (textlineFromServer.startsWith("•••Goodbye ")) {
					kraj = true;
					return;
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
