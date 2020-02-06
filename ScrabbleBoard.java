import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Component;

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;

public class ScrabbleBoard extends JFrame {
	
	

	private static String serverAddress;
	private static int serverPort;
	private static Socket socket;
	private static JPanel contentPaneGame;
	private static JPanel contentPaneInvitePlayers;
	private JPanel contentPaneUserLogin;
	private static JPanel contentPaneMainMenu;
	private static JButton[][] grid = new JButton[20][20];
	private static JButton[][] alphabetGrid = new JButton[1][26];
	private static JLabel[][] playerLabelGrid;
	private static JButton btnPassTurn;
	private static String userName;
	private static JLabel lblDisplay;
	private static JPanel panel_players;
	private static JScrollPane contentPanePlayers;
	private static JLabel lblInfo;
	
	private static JList listAllPlayers;
	private static JList listSelectedPlayers = new JList();
	private static DefaultListModel defaultAllPlayersList;
	private static DefaultListModel defaultInvitedPlayersList = new DefaultListModel();
	
	private static String status = "false";
	
	private static String[] selectedValues = new String[3];  

	private JTextField textFieldUserName;
	private static JTextArea txtAreaiInstructions;

	
	private static JButton btnNewGame;
	
	private static DataInputStream reader;
	private static DataOutputStream writer;
	
	static ScrabbleBoard frame;
	
	public static int ff;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		if(args.length != 2)		//if the command lacks any of the required parameters
		{
			System.err.println("Error! Port number or server address missing!");
			System.exit(1);
		}
		
		serverAddress = args[0];
		serverPort = Integer.parseInt(args[1]);
		try
		{
			socket = new Socket(serverAddress, serverPort);
		}
		catch(SocketException e)
		{
			System.err.println("Socket error! " + e);
			JOptionPane.showMessageDialog(null, "Socket error!");
			System.exit(1);
		}
		catch(IOException e)
		{
			System.err.println("Connection Failiure!: " + e);
			JOptionPane.showMessageDialog(null, "Connection Failed!");
			System.exit(1);
		}
		catch(Exception e)
		{
			System.err.println(e);
			e.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					 frame = new ScrabbleBoard();
					 frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		


	}

	/**
	 * Create the frame.
	 */
	public ScrabbleBoard() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 950, 700);
		contentPaneUserLogin = new JPanel();
//		contentPaneUserLogin.setBackground(new Color(188, 217,236));
		contentPaneUserLogin.setBackground(Color.getHSBColor(203, 20, 92));
        contentPaneUserLogin.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "YOLO  SCRABBLE", 2,0,
                new Font("Zapfino", Font.BOLD, 30), Color.BLACK));
		//contentPaneUserLogin.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPaneUserLogin);
		GridBagLayout gbl_contentPaneUserLogin = new GridBagLayout();
		gbl_contentPaneUserLogin.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPaneUserLogin.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPaneUserLogin.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPaneUserLogin.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPaneUserLogin.setLayout(gbl_contentPaneUserLogin);

        JLabel picLabel = new JLabel(new ImageIcon("0.jpg"));
        GridBagConstraints gbc_picLabel = new GridBagConstraints();
        gbc_picLabel.anchor = GridBagConstraints.WEST;
        gbc_picLabel.insets = new Insets(0, 10, 20, 15);
        gbc_picLabel.gridx = 1;
        gbc_picLabel.gridy = 15;
        contentPaneUserLogin.add(picLabel, gbc_picLabel);

		JLabel lblEnterAUsername = new JLabel("Enter a username");
		lblEnterAUsername.setFont(new Font("Lucida Calligraphy", Font.ITALIC, 18));
		GridBagConstraints gbc_lblEnterAUsername = new GridBagConstraints();
		gbc_lblEnterAUsername.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblEnterAUsername.insets = new Insets(10, 0, 5, 15);
		gbc_lblEnterAUsername.gridx = 10;
		gbc_lblEnterAUsername.gridy = 11;
		contentPaneUserLogin.add(lblEnterAUsername, gbc_lblEnterAUsername);
		
		textFieldUserName = new JTextField();
		textFieldUserName.setFont(new Font("Palatino Linotype",0,22));
		GridBagConstraints gbc_textFieldUserName = new GridBagConstraints();
		gbc_textFieldUserName.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldUserName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUserName.gridx = 10;
		gbc_textFieldUserName.gridy = 14;
		contentPaneUserLogin.add(textFieldUserName, gbc_textFieldUserName);
		textFieldUserName.setColumns(20);
		
		JLabel lblUSernameOut = new JLabel("");
		GridBagConstraints gbc_lblUSernameOut = new GridBagConstraints();
		gbc_lblUSernameOut.insets = new Insets(0, 0, 5, 0);
        gbc_textFieldUserName.fill = GridBagConstraints.SOUTH;
		gbc_lblUSernameOut.gridx = 10;
		gbc_lblUSernameOut.gridy = 16;
		contentPaneUserLogin.add(lblUSernameOut, gbc_lblUSernameOut);
		
		JButton btnProceed = new JButton("Proceed");
		btnProceed.setFont(new Font("Luminari",0,22));
		btnProceed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					reader = new DataInputStream(socket.getInputStream());
					writer = new DataOutputStream(socket.getOutputStream());
					System.out.println("Check1");
					
					if(textFieldUserName.getText().equals(""))
					{
						lblUSernameOut.setText("Username field cannot be empty!");	
					}
					else
					{


					String clientMsg = "userName " + textFieldUserName.getText();
					writer.writeUTF(clientMsg);

					String temp = null;				
					temp = reader.readUTF();		//message received from the server
					
					if(temp.equals("Username already exists!"))
					{
						lblUSernameOut.setText("Username already exists!");		
					}
					
					else
					{
						userName = new String();
						userName = textFieldUserName.getText();
						
						StringTokenizer gameTemp = new StringTokenizer(temp," ");
						String a = gameTemp.nextToken();
						String gameState = gameTemp.nextToken();

						contentPaneUserLogin.setVisible(false);

						MainMenu(gameState);

					}
					}
				}
				catch(Exception ie)
				{
					System.out.println(ie);

				}
			}
		});
		GridBagConstraints gbc_btnProceed = new GridBagConstraints();
		gbc_btnProceed.insets = new Insets(0, 0, 5, 0);
		gbc_btnProceed.gridx = 10;
		gbc_btnProceed.gridy = 15;
		contentPaneUserLogin.add(btnProceed, gbc_btnProceed);

	}
	
	public static void MainMenu(String gameState)
	{
		//message listener can be invoked from here started and stopped accordingly
		ScrabblePlayerListener sl = new ScrabblePlayerListener(socket);
		sl.start();
		
		contentPaneMainMenu = new JPanel();
        contentPaneMainMenu.setBackground(Color.getHSBColor(203, 20, 92));
        contentPaneMainMenu.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "YOLO  SCRABBLE", 2,0,
                new Font("Zapfino", Font.BOLD, 30), Color.BLACK));
//		contentPaneMainMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPaneMainMenu);
		GridBagLayout gbl_contentPaneMainMenu = new GridBagLayout();
		gbl_contentPaneMainMenu.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPaneMainMenu.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPaneMainMenu.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPaneMainMenu.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPaneMainMenu.setLayout(gbl_contentPaneMainMenu);

        JLabel picLabel = new JLabel(new ImageIcon("0.jpg"));
        GridBagConstraints gbc_picLabel = new GridBagConstraints();
        gbc_picLabel.anchor = GridBagConstraints.WEST;
        gbc_picLabel.insets = new Insets(0, 10, 20, 15);
        gbc_picLabel.gridx = 1;
        gbc_picLabel.gridy = 15;
        contentPaneMainMenu.add(picLabel, gbc_picLabel);

		btnNewGame = new JButton("New Game");
        btnNewGame.setFont(new Font("Luminari",0,22));
        GridBagConstraints gbc_btnNewGame = new GridBagConstraints();
        gbc_btnNewGame.insets = new Insets(0, 0, 5, 5);
//        gbc_btnNewGame.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewGame.anchor = GridBagConstraints.CENTER;
        gbc_btnNewGame.gridx = 11;
        gbc_btnNewGame.gridy = 13;
        contentPaneMainMenu.add(btnNewGame, gbc_btnNewGame);
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				contentPaneMainMenu.setVisible(false);
				try {
					defaultAllPlayersList = new DefaultListModel();
					writer = new DataOutputStream(socket.getOutputStream());
					writer.writeUTF("newGame");
					InvitingPage();

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		
		lblInfo = new JLabel("");
		lblInfo.setFont(new Font("Luminari",0,24));
		GridBagConstraints gbc_lblInfo = new GridBagConstraints();
		gbc_lblInfo.anchor = GridBagConstraints.CENTER;
		gbc_lblInfo.insets = new Insets(0, 0, 5, 5);
		gbc_lblInfo.gridx = 1;
		gbc_lblInfo.gridy = 14;
        contentPaneMainMenu.add(lblInfo, gbc_lblInfo);
		

		JButton btnInstructions = new JButton("Instructions");
        btnInstructions.setFont(new Font("Luminari",0,22));
        txtAreaiInstructions = new JTextArea("Welcome to YOLO Scrabble~\r\nHere is the instruction for this game:\r\n\r\n First pick a letter from the below bar.\r\nThen, place it to a spare grid on the board.\r\nIf satisfied, you can start voting.\r\nIf all players aggree to this word,\r\nyou will get the score.\r\nIf you don't want to inset any letter,\r\nyou can always pass turn.");
        txtAreaiInstructions.setFont(new Font("Luminari",2,20));
        txtAreaiInstructions.setBackground(Color.getHSBColor(203, 20, 92));
        GridBagConstraints gbc_instruction = new GridBagConstraints();
        gbc_instruction.anchor = GridBagConstraints.WEST;
        gbc_instruction.fill = GridBagConstraints.HORIZONTAL;
        gbc_instruction.insets = new Insets(0, 10, 20, 15);
        gbc_instruction.gridx = 11;
        gbc_instruction.gridy = 15;
        contentPaneMainMenu.add(txtAreaiInstructions, gbc_instruction);
        txtAreaiInstructions.setVisible(false);
        btnInstructions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtAreaiInstructions.isVisible()){
                    txtAreaiInstructions.setVisible(false);
                }
                else txtAreaiInstructions.setVisible(true);
            }
        });

		GridBagConstraints gbc_btnInstructions = new GridBagConstraints();
		gbc_btnInstructions.insets = new Insets(0, 0, 5, 5);
        gbc_instruction.anchor = GridBagConstraints.CENTER;
//		gbc_btnInstructions.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnInstructions.gridx = 11;
		gbc_btnInstructions.gridy = 14;
		contentPaneMainMenu.add(btnInstructions, gbc_btnInstructions);

		
		if(gameState.equals("true"))//&&gameStatePlay.equals("true"))
		{
			btnNewGame.setVisible(false);
			lblInfo.setText("A game room is active");
		}
		/*else if(gameState.equals("true")&&gameStatePlay.equals("false"))
		{
			btnNewGame.setVisible(false);
			lblInfo.setText("A game invitation is in progress");
		}*/

		
		
	}

	public static void InvitingPage()
	{
		contentPaneInvitePlayers = new JPanel();
        contentPaneInvitePlayers.setBackground(Color.getHSBColor(203, 20, 92));
        contentPaneInvitePlayers.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "YOLO  SCRABBLE", 2,0,
                new Font("Zapfino", Font.BOLD, 30), Color.BLACK));
//		contentPaneInvitePlayers.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPaneInvitePlayers);
		contentPaneInvitePlayers.setVisible(true);
		
		
		GridBagLayout gbl_contentPaneInvitePLayers = new GridBagLayout();
		gbl_contentPaneInvitePLayers.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPaneInvitePLayers.rowHeights = new int[]{0, 0, 0, 0};
		gbl_contentPaneInvitePLayers.columnWeights = new double[]{1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPaneInvitePLayers.rowWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		contentPaneInvitePlayers.setLayout(gbl_contentPaneInvitePLayers);
		
		JLabel lblInvite = new JLabel("INVITE PLAYERS FOR NEW GAME");
		lblInvite.setFont(new Font("American Typewriter", Font.PLAIN, 22));
		GridBagConstraints gbc_lblInvite = new GridBagConstraints();
		gbc_lblInvite.gridwidth = 3;
		gbc_lblInvite.insets = new Insets(0, 0, 5, 0);
		gbc_lblInvite.gridx = 0;
		gbc_lblInvite.gridy = 0;
		contentPaneInvitePlayers.add(lblInvite, gbc_lblInvite);
		
		listAllPlayers = new JList(defaultAllPlayersList);
		GridBagConstraints gbc_listAllPlayers = new GridBagConstraints();
		gbc_listAllPlayers.insets = new Insets(0, 0, 5, 5);
		gbc_listAllPlayers.fill = GridBagConstraints.BOTH;
		gbc_listAllPlayers.gridx = 0;
		gbc_listAllPlayers.gridy = 1;
		contentPaneInvitePlayers.add(listAllPlayers, gbc_listAllPlayers);
		
		//defaultInvitedPlayersList = new DefaultListModel();

		JButton btnInvite = new JButton("Invite");
        btnInvite.setFont(new Font("Luminari",0,22));
		btnInvite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//defaultInvitedPlayersList = new DefaultListModel();
			
				int[] selectedPlayers = listAllPlayers.getSelectedIndices();
				String temp1 = "invite";
				for(int y = 0;y<selectedPlayers.length;y++)
				{
					temp1 = temp1 +" " +listAllPlayers.getModel().getElementAt(selectedPlayers[y]);
					defaultInvitedPlayersList.addElement(listAllPlayers.getModel().getElementAt(selectedPlayers[y]));
					
					// remove user invited from all player list
					defaultAllPlayersList.removeElementAt(selectedPlayers[y]);
				}
				
				
				
				
				//System.out.println(defaultInvitedPlayersList.getSize());
				
				
				
				try {
					writer = new DataOutputStream(socket.getOutputStream());
					writer.writeUTF(temp1);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}	

		});
		GridBagConstraints gbc_btnInvite = new GridBagConstraints();
		gbc_btnInvite.insets = new Insets(0, 0, 5, 5);
		gbc_btnInvite.gridx = 1;
		gbc_btnInvite.gridy = 1;
		contentPaneInvitePlayers.add(btnInvite, gbc_btnInvite);
		
		
		//listSelectedPlayers = new JList();
		listSelectedPlayers.setModel(defaultInvitedPlayersList);
		//System.out.println(defaultInvitedPlayersList.getSize());
		
		// ********************************
		//add(listSelectedPlayers);
		
		
		GridBagConstraints gbc_listSelectedPlayers = new GridBagConstraints();
		gbc_listSelectedPlayers.insets = new Insets(0, 0, 5, 0);
		gbc_listSelectedPlayers.fill = GridBagConstraints.BOTH;
		gbc_listSelectedPlayers.gridx = 2;
		gbc_listSelectedPlayers.gridy = 1;
		contentPaneInvitePlayers.add(listSelectedPlayers, gbc_listSelectedPlayers);
		
		JButton btnReturn = new JButton("Return");
        btnReturn.setFont(new Font("Luminari",0,22));
		GridBagConstraints gbc_btnReturn = new GridBagConstraints();
		gbc_btnReturn.insets = new Insets(0, 0, 0, 5);
		gbc_btnReturn.gridx = 0;
		gbc_btnReturn.gridy = 2;
		contentPaneInvitePlayers.add(btnReturn, gbc_btnReturn);
		btnReturn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    writer = new DataOutputStream(socket.getOutputStream());
                    writer.writeUTF("return");
                    contentPaneInvitePlayers.setVisible(false);
                    MainMenu("false");//,"false");

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
		btnReturn.setVisible(false);
		
		JButton btnStartGame = new JButton("Start Game");
        btnStartGame.setFont(new Font("Luminari",0,22));
		btnStartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				contentPaneInvitePlayers.setVisible(true);
				
				try {
					writer = new DataOutputStream(socket.getOutputStream());
					writer.writeUTF("startGame");

					//GameBoard();
					

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		GridBagConstraints gbc_btnStartGame = new GridBagConstraints();
		gbc_btnStartGame.gridx = 2;
		gbc_btnStartGame.gridy = 2;
		contentPaneInvitePlayers.add(btnStartGame, gbc_btnStartGame);
		
	}
	
	
	
	
	public static void GameBoard(String status1,String currentPlayer, List<String> playerNames, List<Integer> playerScores )
	{
		status = status1;
		contentPaneGame = new JPanel();
        contentPaneGame.setBackground(Color.getHSBColor(203, 20, 92));
        contentPaneGame.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "YOLO  SCRABBLE", 2,0,
                new Font("Zapfino", Font.BOLD, 30), Color.BLACK));
//		frame.contentPaneUserLogin.setBackground(Color.getHSBColor(203, 20, 92));

//		contentPaneGame.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPaneGame);
		
		//contentPaneGame.setVisible(true);
		

		GridBagLayout gbl_contentPaneGame = new GridBagLayout();
		gbl_contentPaneGame.columnWidths = new int[]{483, 527, 0};
		gbl_contentPaneGame.rowHeights = new int[]{0, 51, 374, 34, 0};
		gbl_contentPaneGame.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPaneGame.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		contentPaneGame.setLayout(gbl_contentPaneGame);
		
//		JLabel lblScrabble = new JLabel("BOARD");
//		lblScrabble.setFont(new Font("American Typewriter", Font.PLAIN, 22));
//		GridBagConstraints gbc_lblScrabble = new GridBagConstraints();
//		gbc_lblScrabble.gridwidth = 2;
//		gbc_lblScrabble.insets = new Insets(0, 0, 5, 0);
//		gbc_lblScrabble.gridx = 0;
//		gbc_lblScrabble.gridy = 0;
//		contentPaneGame.add(lblScrabble, gbc_lblScrabble);
		
		lblDisplay = new JLabel("Welcome!");
		lblDisplay.setFont(new Font("Luminari",0,22));
		GridBagConstraints gbc_lblDisplay = new GridBagConstraints();
		gbc_lblDisplay.insets = new Insets(0, 0, 5, 5);
		gbc_lblDisplay.gridx = 0;
		gbc_lblDisplay.gridy = 1;
		contentPaneGame.add(lblDisplay, gbc_lblDisplay);
		
		JPanel panel = new JPanel();
		panel.setSize(300,300);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridheight = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		contentPaneGame.add(panel, gbc_panel);
		panel.setLayout(new GridLayout(20,20));
		
		/*JPanel alphabetPanel = new JPanel();
		panel.setSize(400,20);
		GridBagConstraints gbc_alphabetPanel = new GridBagConstraints();
		gbc_alphabetPanel.gridwidth = 2;
		gbc_alphabetPanel.insets = new Insets(0, 0, 5, 0);
		gbc_alphabetPanel.fill = GridBagConstraints.BOTH;
		gbc_alphabetPanel.gridx = 0;
		gbc_alphabetPanel.gridy = 3;
		contentPaneGame.add(alphabetPanel, gbc_alphabetPanel);
		panel.setLayout(new GridLayout(1,20));*/

		
		contentPanePlayers = new JScrollPane();
		contentPanePlayers.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_contentPanePlayers = new GridBagConstraints();
		gbc_contentPanePlayers.insets = new Insets(0, 0, 5, 5);
		gbc_contentPanePlayers.fill = GridBagConstraints.BOTH;
		gbc_contentPanePlayers.gridx = 0;
		gbc_contentPanePlayers.gridy = 2;
		contentPaneGame.add(contentPanePlayers, gbc_contentPanePlayers);
		//contentPanePlayers.setLayout(new GridLayout(0, 1, 0, 0));
		
		panel_players = new JPanel();
		GridBagConstraints gbc_panel_players = new GridBagConstraints();
		gbc_panel_players.gridwidth = 2;
		gbc_panel_players.insets = new Insets(0, 0, 5, 5);
		gbc_panel_players.fill = GridBagConstraints.BOTH;
		gbc_panel_players.gridx = 0;
		gbc_panel_players.gridy = 3;
		contentPanePlayers.setViewportView(panel_players);//, gbc_panel_players);
		panel_players.setLayout(new GridLayout(0, 1, 0, 0));
		

		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 3;
		contentPaneGame.add(panel_1, gbc_panel_1);
		panel_1.setLayout(new GridLayout(1, 0, 0, 0));
		
		btnPassTurn = new JButton("Pass Turn");
        btnPassTurn.setFont(new Font("Luminari",0,22));
        btnPassTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//if a person passes his turn
				try {
					writer = new DataOutputStream(socket.getOutputStream());
					writer.writeUTF("pass");
					btnPassTurn.setEnabled(false);
					for(int i = 0;i<26;++i)
					{
						alphabetGrid[0][i].setEnabled(false);
					}


				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				

			}
		});
		GridBagConstraints gbc_btnPassTurn = new GridBagConstraints();
		gbc_btnPassTurn.insets = new Insets(0, 0, 0, 5);
		gbc_btnPassTurn.gridx = 0;
		gbc_btnPassTurn.gridy = 4;
		contentPaneGame.add(btnPassTurn, gbc_btnPassTurn);
		
		JButton btnLeaveGame = new JButton("Stop Game");
        btnLeaveGame.setFont(new Font("Luminari",0,22));
        btnLeaveGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//if a person leaves the game, take him to the main menu and the rest of the players will be shown the winner
				try {
					writer = new DataOutputStream(socket.getOutputStream());
					writer.writeUTF("leaveGame");
					btnPassTurn.setEnabled(false);
					
					
					
					

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				

			}
		});
		GridBagConstraints gbc_btnLeaveGame = new GridBagConstraints();
		gbc_btnLeaveGame.gridx = 1;
		gbc_btnLeaveGame.gridy = 4;
		contentPaneGame.add(btnLeaveGame, gbc_btnLeaveGame);
		
		for(int i = 0;i<20;++i)
			for(int j = 0;j<20;++j)
			{
				grid[i][j] = new JButton("");
				grid[i][j].setBorder(new LineBorder(Color.BLACK));
				grid[i][j].setMinimumSize(new Dimension(15,15));
				grid[i][j].setMaximumSize(new Dimension(15,15));
				grid[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//set actions for each button
						try {
							if(((JButton)e.getSource()).getBackground()==Color.RED)
							{
								writer = new DataOutputStream(socket.getOutputStream());
								writer.writeUTF("voteWord "+ "Vertical" + selectedValues[1] + selectedValues[2]);
								btnPassTurn.setEnabled(false);

								
							}
							else if(((JButton)e.getSource()).getBackground()==Color.GREEN)
							{
								writer = new DataOutputStream(socket.getOutputStream());
								writer.writeUTF("voteWord "+ "Horizontal" + selectedValues[1] + selectedValues[2]);
								btnPassTurn.setEnabled(false);

							}
							else
							{
								btnPassTurn.setEnabled(false);
								for(int i = 0;i<26;++i)
								{
									alphabetGrid[0][i].setEnabled(false);
								}
								
								((JButton)e.getSource()).setText(selectedValues[0]);
								int r = 0,w = 0;
								for(int l = 0;l<20;++l)
									for(int m = 0;m<20;++m)
									{
										if(grid[l][m] == ((JButton)e.getSource()))
										{
											((JButton)e.getSource()).setBackground(Color.BLUE);
											((JButton)e.getSource()).setOpaque(true);
									
											selectedValues[1] = " " + l;
											selectedValues[2] = " " + m;
											r = l;
											w = m;
										}
									}
								//game play
						
								writer = new DataOutputStream(socket.getOutputStream());
								writer.writeUTF("addLetter "+ selectedValues[0] + selectedValues[1] + selectedValues[2]);
							
								//selecting word, highlight row and column
								int choice = JOptionPane.showConfirmDialog(null, "Do you wanna put a word for voting?", "Voting", JOptionPane.YES_NO_OPTION);
								if(choice == 0)
								{
									votingSelection(r,w);
								}
								else
								{		//if no then pass the turn to the next person
									writer = new DataOutputStream(socket.getOutputStream());
									writer.writeUTF("noVoting");
								
								}
							
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						

					}
				});

				panel.add(grid[i][j]);
			}
		
		char r = 'A';
		
		for(int i = 0;i<26;++i)
			{
				alphabetGrid[0][i] = new JButton(Character.toString(r));
				r++;
				alphabetGrid[0][i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//set actions for each button
						
						for(int i = 0; i<20;++i)	//the board is enabled as a letter is selected
							for(int j = 0; j < 20 ;j++)
							{
								if(grid[i][j].getText() == "")
								{
									grid[i][j].setEnabled(true);
								}
							}
						
						selectedValues[0] = ((JButton)e.getSource()).getText(); 

					}
				});

				panel_1.add(alphabetGrid[0][i]);
			}
		
		playerLabelGrid = new JLabel[playerNames.size()][1];
		for(int i = 0;i<playerNames.size();++i)
		{
			if(currentPlayer.equals(playerNames.get(i)))
			{	
				playerLabelGrid[i][0] = new JLabel(playerNames.get(i)+ "'s turn!!  Score : "+ playerScores.get(i));
				playerLabelGrid[i][0].setBackground(Color.ORANGE);
				playerLabelGrid[i][0].setOpaque(true);
				panel_players.add(playerLabelGrid[i][0]);

			}
			else
			{
				playerLabelGrid[i][0] = new JLabel(playerNames.get(i)+ "  Score : "+ playerScores.get(i));
				playerLabelGrid[i][0].setBackground(Color.GRAY);
				playerLabelGrid[i][0].setOpaque(true);
				panel_players.add(playerLabelGrid[i][0]);

			}

		}
		lblDisplay.setText(currentPlayer+" is playing!");

		if(status.equals("false"))
		{
			//if not playing
			//disable button and hide stuff
			//the person can have a button to go back to the main menu, can be implemented using two different action listeners
			
			panel_1.setVisible(false);
			btnPassTurn.setVisible(false);
			for(int i = 0;i<26;++i)
			{
				alphabetGrid[0][i].setVisible(false);
			}
			
			
			
		}
		else if(currentPlayer.equals(userName)!=true)
		{
			//disable buttons for player waiting for turn, disable pass button, board and alphabets
			for(int i = 0;i<26;++i)
			{
				alphabetGrid[0][i].setEnabled(false);
			}

			//panel_1.setEnabled(false);
			btnPassTurn.setEnabled(false);
			
		}
		else
		{
			lblDisplay.setText("It's your turn!");
		}
		//the board is enabled only after a letter has been selected
		for(int i = 0; i<20;++i)
			for(int j = 0; j < 20 ;j++)
			{
				grid[i][j].setEnabled(false);
			}
		
		frame.setVisible(true);
		
		
		
	}
	
	//for playing next turns
	public static void playTurn(String status1,String currentPlayer, List<String> playerNames, List<Integer> playerScores)	//for the next turns after start game
	{
		int numOfLetters = 0;
		for(int i = 0; i<20;++i)		//board is disabled
			for(int j = 0; j < 20 ;j++)
			{
				if(grid[i][j].getText()!="")
				{
					grid[i][j].setBackground(Color.CYAN);
					grid[i][j].setOpaque(true);
					numOfLetters++;
				}
				grid[i][j].setEnabled(false);
			}
		if(numOfLetters == 400)
		{//if the board has become full
			try {
				writer = new DataOutputStream(socket.getOutputStream());
				writer.writeUTF("leaveGame");
				btnPassTurn.setEnabled(false);
				for(int i = 0;i<26;++i)
				{
					alphabetGrid[0][i].setEnabled(false);
				}


			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else
		{
		
		if(currentPlayer.equals(userName))
		{
			lblDisplay.setText("It's your turn!");
			
			for(int i = 0;i<26;++i)
			{
				alphabetGrid[0][i].setEnabled(true);
			}
			btnPassTurn.setEnabled(true);

		}
		else
		{
			lblDisplay.setText(currentPlayer+" is playing!");
		}
		for(int i = 0;i<playerNames.size();++i)
		{
			if(currentPlayer.equals(playerNames.get(i)))
			{	
				playerLabelGrid[i][0].setText(playerNames.get(i) + "'s turn!!  Score : "+ playerScores.get(i));
				playerLabelGrid[i][0].setBackground(Color.ORANGE);
				playerLabelGrid[i][0].setOpaque(true);

			}
			else
			{
				playerLabelGrid[i][0].setText(playerNames.get(i)+ "  Score : "+ playerScores.get(i));
				playerLabelGrid[i][0].setBackground(Color.GRAY);
				playerLabelGrid[i][0].setOpaque(true);

			}

		}
		
		contentPanePlayers.getViewport().revalidate();
		}
	}
	
	public static void votingSelection(int r, int w)
	{
		for(int i = 0; i<20;++i)
			for(int j = 0; j < 20 ;j++)
			{
				grid[i][j].setEnabled(false);
			}
		int numberOfLetters = 0;
		for(int l = r;l<20 && grid[l][w].getText()!="";++l)
		{
			grid[l][w].setBackground(Color.RED);
			grid[l][w].setOpaque(true);
			grid[l][w].setEnabled(true);
			numberOfLetters++;

		}
		for(int l = r;l>-1 && grid[l][w].getText()!="";--l)
		{
			grid[l][w].setBackground(Color.RED);
			grid[l][w].setOpaque(true);
			grid[l][w].setEnabled(true);
			numberOfLetters++;


		}
		for(int l = w;l<20 && grid[r][l].getText()!="";++l)
		{
			grid[r][l].setBackground(Color.GREEN);
			grid[r][l].setOpaque(true);
			grid[r][l].setEnabled(true);
			numberOfLetters++;


		}
		for(int l = w;l>-1 && grid[r][l].getText()!="";--l)
		{
			grid[r][l].setBackground(Color.GREEN);
			grid[r][l].setOpaque(true);
			grid[r][l].setEnabled(true);
			numberOfLetters++;


		}
		if(numberOfLetters > 4)
		{
			grid[r][w].setBackground(Color.BLUE);
			grid[r][w].setOpaque(true);
			grid[r][w].setEnabled(false);
		}

		

	}

	public static void btnNewGameControl(boolean f) {
		// TODO Auto-generated method stub
		btnNewGame.setVisible(f);
		
	}
	//print info into label at main menu
	public static void labelInfoPrint(String a)
	{
		 lblInfo.setText(a);
	}
	
	public static void listAllPlayersToInvite(String temp)
	{
		//listAllPlayers.(listOfPlayers);
		defaultAllPlayersList.addElement(temp);
		
		
	}
	
	public static void addNewLetter(String l, int i, int j)
	{
		grid[i][j].setText(l);
		grid[i][j].setBackground(Color.BLUE);
		grid[i][j].setOpaque(true);
	}
	
	public static void wordToVote(String alignment, int r, int w)
	{
		int wordLength = -1;
		String word = "";

		if(alignment.equals("Vertical"))
		{
			for(int l = r;l<20 && grid[l][w].getText()!="";++l)
			{
				grid[l][w].setBackground(Color.RED);
				grid[l][w].setOpaque(true);
				grid[l][w].setEnabled(true);
				wordLength++;
				word = word + grid[l][w].getText();


			}
			for(int l = r;l>-1 && grid[l][w].getText()!="";--l)
			{
				grid[l][w].setBackground(Color.RED);
				grid[l][w].setOpaque(true);
				grid[l][w].setEnabled(true);
				wordLength++;
				if(l != r)
					word = grid[l][w].getText() + word;



			}
		}
		else if(alignment.equals("Horizontal"))
		{
			for(int l = w;l<20 && grid[r][l].getText()!="";++l)
			{
				grid[r][l].setBackground(Color.GREEN);
				grid[r][l].setOpaque(true);
				grid[r][l].setEnabled(true);
				wordLength++;
				word = grid[r][l].getText() + word;


			}
			for(int l = w;l>-1 && grid[r][l].getText()!="";--l)
			{
				grid[r][l].setBackground(Color.GREEN);
				grid[r][l].setOpaque(true);
				grid[r][l].setEnabled(true);
				wordLength++;
				if(l != w)
					word = grid[r][l].getText() + word;


			}
		}
			//panel
			if(status.equals("true"))		//if the player is playing ask for vote
			{
				int choice = JOptionPane.showConfirmDialog(null, "Do you agree with the word?","Voting!",JOptionPane.YES_NO_OPTION);
			
				try {
					
					
					writer = new DataOutputStream(socket.getOutputStream());
					writer.writeUTF("vote "+choice+" "+wordLength);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		
	}
	//when the game end scenario happens
	public static void gameEnds(List<String> playerNames, List<Integer> playerScores)
	{
		//end the game, find the highest score and declare the winner,  return to the main menu, 
		
		int maxScore = Collections.max(playerScores);
		int indexW = playerScores.indexOf(maxScore);
		lblDisplay.setText(playerNames.get(indexW)+" is the winner!!\r\nExiting to the Main Menu in 10 seconds!");
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//countdown and then exit to main menu
		contentPaneGame.setVisible(false);
		contentPaneMainMenu.setVisible(true);
		btnNewGameControl(true);
		frame.setContentPane(contentPaneMainMenu);
		
		frame.setVisible(true);
		defaultInvitedPlayersList = new DefaultListModel();
		defaultAllPlayersList = new DefaultListModel();
		//MainMenu("false");
		
		
		  
		
		
	}
	//the server has shutdown suddenly
	public static void serverCrash()
	{
		JOptionPane.showConfirmDialog(null, "The server has shutdown! Closing the program!", "Warning", JOptionPane.DEFAULT_OPTION);
		//frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		System.exit(1);
	}
	// method for return listSelectedPlayers
		public static void updateSelectedPlayers(List<String> acceptedClients)
		{	
			System.out.println("size of accept list");
			System.out.println(listSelectedPlayers.getModel().getSize());
			
			for(int i = 0; i < listSelectedPlayers.getModel().getSize(); i++){
				for(int j = 1; j < acceptedClients.size(); j++){
					if(listSelectedPlayers.getModel().getElementAt(i).equals(acceptedClients.get(j))){
						// update jlist					
						String userName = listSelectedPlayers.getModel().getElementAt(i).toString();
						System.out.println(userName);
						defaultInvitedPlayersList.setElementAt(userName + " -- accepted --", i);
						
						
					}
				}
			}
			listSelectedPlayers.setModel(defaultInvitedPlayersList);
		
		}
		
		//add user back to all players list
		public static void updateAllPlayerList(String userName){
			defaultAllPlayersList.addElement(userName);
			listAllPlayers.setModel(defaultAllPlayersList);
			defaultInvitedPlayersList.removeElement(userName);
			listSelectedPlayers.setModel(defaultInvitedPlayersList);
		}
		
		//add new user into list
		public static void updateNewUser(String userName){
					defaultAllPlayersList.addElement(userName);
					listAllPlayers.setModel(defaultAllPlayersList);
				}

}
