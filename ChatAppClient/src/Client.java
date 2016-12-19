import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client implements Runnable {

	static Socket communicationSocket = null;
	static PrintStream outStreamToServer = null;
	static BufferedReader inStreamFromServer = null;
	static BufferedReader console = null;
	static boolean kraj = false;

	public static void main(String[] args) {
		try {

			int port = 2003;

			if (args.length > 0)
				port = Integer.parseInt(args[0]);

			communicationSocket = new Socket("localhost", port);

			console = new BufferedReader(new InputStreamReader(System.in));
			outStreamToServer = new PrintStream(communicationSocket.getOutputStream());
			inStreamFromServer = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));

			new Thread(new Client()).start();

			while (!kraj)
				outStreamToServer.print(console.readLine());

			communicationSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		try {

			String textlineFromServer;

			while (inStreamFromServer.readLine() != null) {
				textlineFromServer = inStreamFromServer.readLine();

				System.out.println(textlineFromServer);
				if (textlineFromServer.indexOf("***Goodbye ") == 0) {
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
