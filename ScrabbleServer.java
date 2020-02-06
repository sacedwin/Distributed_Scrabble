import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ScrabbleServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length != 1)
		{
			System.err.println("Error! Port number!");
			System.exit(1);
		}
		
		int portNumber = Integer.parseInt(args[0]);
		ServerSocket serverListeningSocket = null;
        ServerWindow serverWindow = new ServerWindow();
        serverWindow.setVisible(true);
		try {
			int clientNumber = 0;
			serverListeningSocket = new ServerSocket(portNumber);		//socket starts listening on the port provided
			System.out.println("Server is now running!");
			serverWindow.setTxtAreaStatus("Server is now running!");
			serverWindow.setLblUserCount("0");
			serverWindow.setLblGameCount("0");
			while(true)
			{		//server keeps waiting for connections till its terminated
				Socket clientSocket = serverListeningSocket.accept();
					//threads are assigned for each client new client
				clientNumber++;
				serverWindow.setLblUserCount(Integer.toString(clientNumber));
				serverWindow.setTxtAreaStatus("Client"+clientSocket.getInetAddress()+" Port:"+clientSocket.getPort()+": is connected to the server");
				ScrabbleActionThread t = new ScrabbleActionThread(clientSocket,clientNumber, serverWindow);
				t.setName("T"+clientNumber);
				t.start();						
				
				ScrabbleServerState.getInstance().clientConnected(t);

			}		
		}
		catch(IOException e)
		{
			System.err.println("Error! Unable to prepare socket!");
            serverWindow.setTxtAreaStatus("Error! Unable to prepare socket!");

		}
		catch(Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
			System.out.println("At the server");


		}
	}

}
