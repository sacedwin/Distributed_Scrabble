import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public class ScrabblePlayerListener extends Thread {
	
	private DataInputStream reader;
	private Socket socket;
	private DataOutputStream writer1;


	
	public ScrabblePlayerListener(Socket socket)
	{
		this.socket = socket;
	}
	

	@Override
	public void run() {
		
		try {
			
			String action = null;
			String clientMsg = null;
			//Read messages from the server while the end of the stream is not reached
			reader = new DataInputStream(socket.getInputStream());

			while((clientMsg = reader.readUTF()) != null) 
			{
				//actions in different scenarios 
				ScrabbleBoard.ff=0;
				StringTokenizer clientRequest = new StringTokenizer(clientMsg," ");
				action = clientRequest.nextToken();
				
				if(action.equals("inviting"))
				{
					//call function to remove the new game button
					ScrabbleBoard.btnNewGameControl(false);
					ScrabbleBoard.labelInfoPrint("A game room is active!");
				}
				// server sends invitation to client
				else if(action.equals("invited"))
				{
					//pop up for accepting invite
					int choice = JOptionPane.showConfirmDialog(null, "Do you wanna join game", "invitation", JOptionPane.YES_NO_OPTION);
					
					// new code here
					// yes
					if(choice == 0)
					{
						writer1 = new DataOutputStream(socket.getOutputStream());
						writer1.writeUTF("inviteAccepted");
						ScrabbleBoard.labelInfoPrint("Waiting for game to start!");

						
						// here should jump to a waiting page
						
					}
					// no
					else if(choice == 1)
					{
						writer1 = new DataOutputStream(socket.getOutputStream());
						writer1.writeUTF("inviteDeclined");

						
						
						
						
					}
				}
				// new player joins the list, update the list
				else if(action.equals("updateAcceptedList"))
				{
					List<String> clientAccepted = new ArrayList<>();
					while(clientRequest.hasMoreTokens())
					{
						clientAccepted.add(clientRequest.nextToken());
						
					}
					
					ScrabbleBoard.updateSelectedPlayers(clientAccepted);
						
				}
				
				// back player to the all players list
				else if(action.equals("updateAllPlayerList")){
					String userName = clientRequest.nextToken();
					ScrabbleBoard.updateAllPlayerList(userName);
				}
				//new user join, refresh the all players list
				else if(action.equals("newUserJoin")){
					String userName = clientRequest.nextToken();
					ScrabbleBoard.updateNewUser(userName);
				}
				else if(action.equals("playersToInvite"))
				{
					
					while(clientRequest.hasMoreTokens())
					{
						ScrabbleBoard.listAllPlayersToInvite(clientRequest.nextToken());
					}

				}
				//no enough players
				else if(action.equals("NoEnoughPlayers"))
				{
					//pop up message
					JOptionPane.showConfirmDialog(null, "Not enough players", "Warning", JOptionPane.DEFAULT_OPTION);
				}
				else if(action.equals("addLetter"))
				{
					String l = clientRequest.nextToken();
					int q = Integer.parseInt(clientRequest.nextToken());
					int w = Integer.parseInt(clientRequest.nextToken());
					ScrabbleBoard.addNewLetter(l, q, w);
				}
				
				else if(action.equals("startGame"))
				{
					
					String status = clientRequest.nextToken();
					String currentPlayer = clientRequest.nextToken();
					
					//int numberOfPlayers = 0;
					List<String> name = new ArrayList<>();
					List<Integer> playerScore = new ArrayList<>(); 
					while(clientRequest.hasMoreTokens())
					{
						 //name = name + " " + clientRequest.nextToken();
						name.add(clientRequest.nextToken());
						playerScore.add(Integer.parseInt(clientRequest.nextToken()));
						//ScrabbleBoard.currentPlayers(name, 0);
						
						//numberOfPlayers++;
					}
					//*/
					//int score = 0;//Integer.parseInt(clientRequest.nextToken());
					//ScrabbleBoard.currentPlayers(name,score,numberOfPlayers);
					

					
					ScrabbleBoard.GameBoard(status, currentPlayer, name, playerScore);
					
				}
				else if(action.equals("wordToVote"))
				{
					//voting
					String l = clientRequest.nextToken();
					int q = Integer.parseInt(clientRequest.nextToken());
					int w = Integer.parseInt(clientRequest.nextToken());
					ScrabbleBoard.wordToVote(l, q, w);
				}
				else if(action.equals("nextTurn"))
				{
					String status = clientRequest.nextToken();
					String currentPlayer = clientRequest.nextToken();
					List<String> name = new ArrayList<>();
					List<Integer> playerScore = new ArrayList<>();
					while(clientRequest.hasMoreTokens())
					{
						 //name = name + " " + clientRequest.nextToken();
						name.add(clientRequest.nextToken());
						playerScore.add(Integer.parseInt(clientRequest.nextToken()));				
						//ScrabbleBoard.currentPlayers(name, 0);
						
						//numberOfPlayers++;
					}
					ScrabbleBoard.playTurn(status, currentPlayer, name, playerScore);

				}
				//when a game ends
				else if(action.equals("gameEnds"))
				{
					List<String> name = new ArrayList<>();
					List<Integer> playerScore = new ArrayList<>();
					while(clientRequest.hasMoreTokens())
					{
						name.add(clientRequest.nextToken());
						playerScore.add(Integer.parseInt(clientRequest.nextToken()));				
						
					}
					
					ScrabbleBoard.gameEnds(name, playerScore);
				}
				else if(action.equals("return"))
				{
					//call function to add the new game button
					ScrabbleBoard.btnNewGameControl(true);
				}
				
		
			}
		}catch(EOFException ei)
		{
			
			ScrabbleBoard.serverCrash();

		}
		catch (SocketException e) {
			System.out.println("Socket closed");
			//change
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Socket closed");

		} 
		
	}
}
