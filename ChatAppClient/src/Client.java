import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

	static Socket communicationSocket = null;
	static PrintStream outStreamToServer = null;
	static BufferedReader inStreamFromServer = null;
	static BufferedReader console = null;
	static boolean kraj = false;

	public static void main(String[] args) {
		try {
			int port = 23789;

			if (args.length > 0)
				port = Integer.parseInt(args[0]);

			communicationSocket = new Socket("localhost", port);

			console = new BufferedReader(new InputStreamReader(System.in));
			outStreamToServer = new PrintStream(communicationSocket.getOutputStream());
			inStreamFromServer = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));

			new Thread(new Client()).start();

			while (!kraj) {
				outStreamToServer.println(console.readLine());
			}
			communicationSocket.close();
			
		}catch (UnknownHostException e) {
			System.err.println("Don't know about host");
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	@Override
	public void run() {
		String textlineFromServer;

		try {
			while ((textlineFromServer = inStreamFromServer.readLine()) != null) {

				System.out.println(textlineFromServer);
				
				if (textlineFromServer.startsWith("***Goodbye ")) {
					kraj = true;
					return;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
