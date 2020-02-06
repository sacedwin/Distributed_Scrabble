import java.util.ArrayList;
import java.util.List;



public class ScrabbleServerState {
	
	private static ScrabbleServerState instance;
	private List<ScrabbleActionThread> connectedClients;
	private static boolean GameState = false;
	private static boolean GameStatePlay = false;

	
	// client accepted invitation
	private List<ScrabbleActionThread> acceptedClients;
	
	private static int yesVotes = 1;
	private static int noVotes = 0;
	private static String currentTurn = "";
	private static int turnID = 0;
	private static int passControl = 0;
	

	private ScrabbleServerState()
	{
		connectedClients = new ArrayList<>();
		acceptedClients = new ArrayList<>();

	}
	
	public static synchronized ScrabbleServerState getInstance() {
		if(instance == null) {
			instance = new ScrabbleServerState();
		}
		return instance;
	}
	
	public synchronized void clientConnected(ScrabbleActionThread client) {
		connectedClients.add(client);
	}
	
	public synchronized void clientDisconnected(ScrabbleActionThread client) {
		connectedClients.remove(client);
	}
	
	public synchronized List<ScrabbleActionThread> getConnectedClients() {
		return connectedClients;
	}
	//to get the game state
	public synchronized boolean CheckGameState()
	{
		return GameState;
	}
	//to change the game state
	public synchronized void setGameState(boolean t)
	{
		GameState = t;
	}
	//to get the game state
	public synchronized boolean CheckGameStatePlay()
	{
			return GameStatePlay;
	}
	//to change the game state
	public synchronized void setGameStatePlay(boolean t)
	{
		GameStatePlay = t;
	}
	
	// add client who accepts invitation into a list
	public synchronized void clientAccepted(ScrabbleActionThread client) 
	{
		acceptedClients.add(client);
	}
		
	// get a list of clients who accept the invitation
	public synchronized List<ScrabbleActionThread> getAcceptedClients() 
	{
		return acceptedClients;
	}
	
	public synchronized int WordVoting(int choice)
	{
		if(choice == 0)
		{//if choice is yes, i.e agree with word
			yesVotes++;
		}
		else
		{
			noVotes++;
		}
		
		//if everyone has voted
		if(yesVotes == acceptedClients.size())
		{
			//then the score is added and the next player gets the turn
			return 1;
		}
		else if((yesVotes + noVotes) == acceptedClients.size()) 
		{
			return 2;
		}
		return 3; 
		
	}
	public synchronized void resetVoting()
	{
		yesVotes = 1;
		noVotes = 0;
	
	}
	public synchronized void turnSetter()
	{
		currentTurn = acceptedClients.get(turnID).userNameReturn();
		
		turnID++;
		
		if(turnID == acceptedClients.size())
			turnID = 0;
	}
	
	public synchronized String turnChecker()
	{
		return currentTurn;
	}
	//called when a user makes a pass
	public synchronized int passIncrement()
	{
		passControl++;
		if(passControl == acceptedClients.size())
		{//if all the players have passed
			return 0;
		}
		return 1;
	}
	
	public synchronized void passReset()
	{//if a player plays a turn
		passControl=0;
		
	}
	//resetting all 
	public synchronized void resetAll()
	{
		yesVotes = 1;
		noVotes = 0;
		currentTurn = "";
		turnID = 0;
		passControl = 0;
		GameState = false;
		GameStatePlay = false;
		acceptedClients = new ArrayList<>();

	}
	
}
