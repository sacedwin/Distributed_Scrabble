import java.util.List;
import java.util.ArrayList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.Reader;
import java.net.Socket;
import java.util.StringTokenizer;

//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

public class ScrabbleActionThread extends Thread{
	
	private Socket clientSocket;
	private int clientNumber;
	private ServerWindow serverWindow;
	private String userName = "";
	private DataInputStream reader;
	private DataOutputStream writer;
	private boolean status = false; 
	private int score = 0;
	
	public ScrabbleActionThread(Socket clientSocket, int clientNumber, ServerWindow serverWindow)
	{
		this.clientNumber = clientNumber;
		this.clientSocket = clientSocket;
		this.serverWindow = serverWindow;
		
	}
	
	@Override
	public void run()
	{
		try {
			reader = new DataInputStream(clientSocket.getInputStream());
			writer = new DataOutputStream(clientSocket.getOutputStream());
			
			String action;
			String clientMsg = null;

			
			while((clientMsg = reader.readUTF()) != null)
			{
				StringTokenizer clientRequest = new StringTokenizer(clientMsg," ");
				action = clientRequest.nextToken();
				
				if(action.equals("userName"))
				{

					String tempUserName = clientRequest.nextToken();

					List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();

					boolean f = false;
					for(ScrabbleActionThread client : clients)
					{

						if(client.userNameReturn().equals(tempUserName))
						{

							f = true;
							
						}

					}

					if(f == false)
					{

						writer.writeUTF("UsernameAccepted " + ScrabbleServerState.getInstance().CheckGameState());
						writer.flush();
						userName = tempUserName;
						serverWindow.setTxtAreaStatus("Player \""+ userName + "\" is online");
						
						// check is there a game starting already
						// yes
						if(ScrabbleServerState.getInstance().CheckGameState())
						{
							//send to room master
							List<ScrabbleActionThread> acceptedClients = ScrabbleServerState.getInstance().getAcceptedClients();
							String clientName = "newUserJoin " + userName;
							acceptedClients.get(0).sendStatus(clientName);
						}
						
						
					}
					else
					{

						writer.writeUTF("Username already exists!");
						writer.flush();
					}
					
				}
				else if(action.equals("newGame"))
				{
					ScrabbleServerState.getInstance().setGameState(true);
					//add room master into the accepted list
					ScrabbleServerState.getInstance().clientAccepted(this);


					List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();
					String temp = "playersToInvite";
					for(ScrabbleActionThread client : clients)
					{
						if(client.clientNumber != clientNumber)
						{
							client.sendStatus("inviting");
							temp=temp+" "+client.userName;

						}
					}
					
					writer.writeUTF(temp);
					writer.flush();

				}
				
				else if(action.equals("invite"))
				{
					status = true;
					

					List<String> invitedList = new ArrayList<>();
					while(clientRequest.hasMoreTokens())
					{
						invitedList.add(clientRequest.nextToken());
					}
					List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();
					for(ScrabbleActionThread client : clients)
					{
						for(String tempInvited : invitedList)
						{
						
							if(client.userName.equals(tempInvited))
							{
								client.sendStatus("invited");
							}
						}
					}

				}
				// player accepts the invitation
				else if(action.equals("inviteAccepted"))
				{
					if(ScrabbleServerState.getInstance().CheckGameStatePlay()!=true)
					{
					status = true;
					// player accepts the invitation
					// userName
					// add this thread to the clientAccepted List
					ScrabbleServerState.getInstance().clientAccepted(this);
					List<ScrabbleActionThread> acceptedClients = ScrabbleServerState.getInstance().getAcceptedClients();
					// thread t2 5
					// System.out.println(acceptedClients);
					String clientName = "updateAcceptedList";
					for(ScrabbleActionThread acceptedClient : acceptedClients)
					{
						clientName = clientName +" "+ acceptedClient.userName;
						System.out.println(clientName);
					}
					// sent to the room master
					acceptedClients.get(0).sendStatus(clientName);
					
					
					//for(ScrabbleActionThread acceptedClient : acceptedClients)
					//{
						
						//writer.writeUTF(clientName);
						//writer.flush();
					//}	
					}
											
				}
				// player declines the invitation
				else if(action.equals("inviteDeclined"))
				{
					//what does status means
					// status = false;
					
					// return the userName to client side
					String clientName = "updateAllPlayerList" ;
					clientName = clientName +" "+ userNameReturn();
					List<ScrabbleActionThread> acceptedClients = ScrabbleServerState.getInstance().getAcceptedClients();
					acceptedClients.get(0).sendStatus(clientName);
					
				}
				else if(action.equals("startGame"))
				{
					ScrabbleServerState.getInstance().setGameStatePlay(true);


					String temp = "";
					String usrnameList = "";
					
					List<ScrabbleActionThread> players = ScrabbleServerState.getInstance().getAcceptedClients();


					if(players.size() >= 2)
					{
						//game starts here
						for(ScrabbleActionThread client : players)
						{

								
								temp = temp + " " + client.userName+ " "+ client.score;
								usrnameList = usrnameList+" "+client.userName;
							
							
						}
                        serverWindow.setLblGameCount("1");
						serverWindow.setTxtAreaStatus("New game starts. Players are:"+usrnameList);
						ScrabbleServerState.getInstance().turnSetter();
						List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();

						for(ScrabbleActionThread client : clients)
						{
							client.sendStatus("startGame " + client.status +" "+ ScrabbleServerState.getInstance().turnChecker() + temp);

							
						}
					}
					else
					{
						writer.writeUTF("NoEnoughPlayers");
						writer.flush();
					}
				}
				else if(action.equals("addLetter"))
				{
					ScrabbleServerState.getInstance().passReset();
					List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();
					String temp = "addLetter "+clientRequest.nextToken();
					temp = temp +" " + clientRequest.nextToken();
					temp = temp +" " + clientRequest.nextToken();
					for(ScrabbleActionThread client : clients)
					{
						if(client.clientNumber != clientNumber)
							client.sendStatus(temp);
					}

				}
				else if(action.equals("voteWord"))
				{
					List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();
					String temp = "wordToVote "+clientRequest.nextToken();
					temp = temp +" " + clientRequest.nextToken();
					temp = temp +" " + clientRequest.nextToken();
					for(ScrabbleActionThread client : clients)
					{
						if(client.clientNumber != clientNumber)
							client.sendStatus(temp);
					}

				}
				else if(action.equals("vote"))
				{
					//voting
					int choice = Integer.parseInt(clientRequest.nextToken());
					int wordLength = Integer.parseInt(clientRequest.nextToken());
					
					int control = ScrabbleServerState.getInstance().WordVoting(choice);

					
					if(control == 1)	//if everyone has voted
					{	//if all players agree to the word
						//update the score and the next person gets the turn
						List<ScrabbleActionThread> players = ScrabbleServerState.getInstance().getAcceptedClients();
						String temp = "";

						for(ScrabbleActionThread player : players)
						{

							if(player.userName.equals(ScrabbleServerState.getInstance().turnChecker()))
							{
								player.score += wordLength;
							}
							temp = temp + " " + player.userName+ " "+ player.score;

						}

						//send information including all player informations 
						
						ScrabbleServerState.getInstance().turnSetter();
						
						List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();

						for(ScrabbleActionThread client : clients) //next turn being played
						{
							client.sendStatus("nextTurn " + client.status +" "+ ScrabbleServerState.getInstance().turnChecker() + temp);

							
						}
						

						ScrabbleServerState.getInstance().resetVoting();
					}
					else if(control == 2)
					{
						//if some players does not agree to the word
						//no score update and the next player is given the turn
						
						List<ScrabbleActionThread> players = ScrabbleServerState.getInstance().getAcceptedClients();
						String temp = "";

						for(ScrabbleActionThread player : players)
						{
							temp = temp + " " + player.userName+ " "+ player.score;

						}

						//send information including all player informations 
						
						ScrabbleServerState.getInstance().turnSetter();
						
						List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();

						for(ScrabbleActionThread client : clients) //next turn being played
						{
							client.sendStatus("nextTurn " + client.status + " "+ ScrabbleServerState.getInstance().turnChecker() + temp);

							
						}
						ScrabbleServerState.getInstance().resetVoting();

					}
					
				}
				else if(action.equals("noVoting"))
				{
					List<ScrabbleActionThread> players = ScrabbleServerState.getInstance().getAcceptedClients();
					String temp = "";

					for(ScrabbleActionThread player : players)
					{
						temp = temp + " " + player.userName+ " "+ player.score;

					}

					//send information including all player informations 
					
					ScrabbleServerState.getInstance().turnSetter();
					
					List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();

					for(ScrabbleActionThread client : clients) //next turn being played
					{
						client.sendStatus("nextTurn " + client.status + " "+ ScrabbleServerState.getInstance().turnChecker() + temp);

						
					}
					
					ScrabbleServerState.getInstance().resetVoting();

				}
				//when users press the pass button
				else if(action.equals("pass"))
				{
					//passIncrement
					int p = ScrabbleServerState.getInstance().passIncrement();
					if(p == 0)
					{
						//the game ends and the winner is declared
						//reset all the game stats, pass, votes, gamestate, statuses
						
						List<ScrabbleActionThread> players = ScrabbleServerState.getInstance().getAcceptedClients();
						String temp = "";

						for(ScrabbleActionThread player : players)
						{
							temp = temp + " " + player.userName+ " "+ player.score;

						}

						List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();

						for(ScrabbleActionThread client : clients) //next turn being played
						{
							client.sendStatus("gameEnds" + temp);
							client.score = 0;
							client.status = false;

							
						}
						ScrabbleServerState.getInstance().resetAll();

						
					}
					else
					{
						//start the next turn
						List<ScrabbleActionThread> players = ScrabbleServerState.getInstance().getAcceptedClients();
						String temp = "";

						for(ScrabbleActionThread player : players)
						{
							temp = temp + " " + player.userName+ " "+ player.score;

						}

						//send information including all player informations 
						
						ScrabbleServerState.getInstance().turnSetter();
						
						List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();

						for(ScrabbleActionThread client : clients) //next turn being played
						{
							client.sendStatus("nextTurn " + client.status + " "+ ScrabbleServerState.getInstance().turnChecker() + temp);

							
						}
						
						ScrabbleServerState.getInstance().resetVoting();

					}
				}
				else if(action.equals("leaveGame"))
				{
					//the game ends and the winner is declared
					//reset all the game stats, pass, votes, gamestate, statuses
					
					List<ScrabbleActionThread> players = ScrabbleServerState.getInstance().getAcceptedClients();
					String temp = "";

					for(ScrabbleActionThread player : players)
					{
						temp = temp + " " + player.userName+ " "+ player.score;

					}

					List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();

					for(ScrabbleActionThread client : clients) //next turn being played
					{
						client.sendStatus("gameEnds" + temp);
						client.score = 0;
						client.status = false;

						
					}
					ScrabbleServerState.getInstance().resetAll();
					serverWindow.setLblGameCount("0");
					serverWindow.setTxtAreaStatus("Game ends");

				}
				
				else if(action.equals("return"))
				{
					ScrabbleServerState.getInstance().setGameState(false);
					//reset all lists
					//inform all clients of the state
					ScrabbleServerState.getInstance().resetAll();
					
					List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();

					for(ScrabbleActionThread client : clients) 
					{
						client.sendStatus("return");

					}
				}
			}
		}
		catch(EOFException ef)
		{
			System.out.println("A client has disconnected");
			serverWindow.setTxtAreaStatus("Player \""+userName+"\" is offline");
			//when a client disconnects the game ends
			//the game ends and the winner is declared
			//reset all the game stats, pass, votes, gamestate, statuses
			if(ScrabbleServerState.getInstance().CheckGameState() && this.status == true)
			{
			
			List<ScrabbleActionThread> players = ScrabbleServerState.getInstance().getAcceptedClients();
			String temp = "";

			for(ScrabbleActionThread player : players)
			{
				temp = temp + " " + player.userName+ " "+ player.score;

			}
			ScrabbleServerState.getInstance().clientDisconnected(this);

			List<ScrabbleActionThread> clients = ScrabbleServerState.getInstance().getConnectedClients();

			for(ScrabbleActionThread client : clients) 
			{
				client.sendStatus("gameEnds" + temp);
				client.score = 0;
				client.status = false;

				
			}
			ScrabbleServerState.getInstance().resetAll();

			}
			else
			{
				ScrabbleServerState.getInstance().clientDisconnected(this);

			}
		}
		catch(Exception e)
		{
			System.out.println(e);
			

		}
		
	}
	
	public synchronized void sendStatus(String systemStatus)
	{
		try {
			writer.writeUTF(systemStatus);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String userNameReturn()
	{
		return userName;
	}
	
	/*public void sendInvites()
	{
		try {
			writer.writeUTF("invited");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	
}
