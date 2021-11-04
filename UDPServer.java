//Authors: DAVID SALDANA (6155964), LIAM YETHON (6255384), JESSE MASCIARELLI (6243109), KATIE LEE (6351696)

import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class UDPServer {

    	public static List<ClientThread> clientStack = new Stack<ClientThread>();
    	public static String p1Move = null, p2Move = null;
    	public static int numClients = 0;
    	public static List<PacketType> packetStack = new Stack<PacketType>(); // stores received packets
    	public static int sequenceNum = 1000;					 // global packet sequence numbers
    	
    
	public static void main(String args[]) throws IOException {

	    // Connection Parameters
	    DatagramSocket socket = null;
	    int maxThreads = 2;

	    if(args.length != 1) {
		System.err.println("Provide Parameter: java UDPServer.java <port number>");
		System.exit(1);
	    }
		
	    // Retrieve user input
	    int serverPort = Integer.parseInt(args[0]);

	    try {
		// Setup socket
		socket = new DatagramSocket(serverPort);
		
        	System.out.println("* ROCK, PAPER, SCISSORS *");
        	System.out.println("Waiting for 2 players to connect...");

            	// Wait to accept incoming clients
		while(clientStack.size() < 2) {
		   // Create client thread
		   ClientThread client = new ClientThread(socket);
		   // Add thread clientStack
		   clientStack.add(client);
		   // Start thread
		   new Thread(client).start();
		}
		
		// Accept two clients
		while (numClients < 2) {
		   System.out.print('\t');
		}
		
		// Send message to both clients
		String msg = "Starting Game! Choose 'rock', 'paper', or 'scissors':";
		clientStack.get(0).printLine(msg, clientStack.get(0).getPacket());
		clientStack.get(1).printLine(msg, clientStack.get(1).getPacket());

		// Wait for both players to respond
            	while(p1Move == null || p2Move == null){
            	   p1Move = clientStack.get(0).move;
            	   p2Move = clientStack.get(1).move;
            	   System.out.print('\t');
            	}
            	
            	System.out.println("P1Move: "+p1Move);
            	System.out.println("P2Move: "+p2Move);

		// Send player moves to both clients
		String p1msg = "You -> " + p1Move + " VS " +p2Move+ " <- Opponent";
		String p2msg = "You -> " + p2Move + " VS " +p1Move+ " <- Opponent";
            	clientStack.get(0).printLine(p1msg, clientStack.get(0).getPacket());
            	clientStack.get(1).printLine(p2msg, clientStack.get(1).getPacket());

		// Rock, Paper, Scissors Logic
            	if(p1Move.equals(p2Move)){
                clientStack.get(0).printLine("Its a TIE", clientStack.get(0).getPacket());
                clientStack.get(1).printLine("Its a TIE", clientStack.get(0).getPacket());
            	}
            	else if(p1Move.equals("rock")){
            	   if(p2Move.equals("paper")) sendResults(2); 	 // Player 2 Wins
                   else if(p2Move.equals("scissors")) sendResults(1); // Player 1 Wins
               }
            	else if(p1Move.equals("paper")){
            	   if(p2Move.equals("scissors")) sendResults(2); 	 // Player 2 Wins
                   else if(p2Move.equals("rock")) sendResults(1); 	 // Player 1 Wins
            	}
            	else if(p1Move.equals("scissors")){
            	   if(p2Move.equals("rock")) sendResults(2); 	 	 // Player 2 Wins
                   else if(p2Move.equals("paper")) sendResults(1); 	 // Player 1 Wins
               }
               
               // Send final messages to both clients
            	System.out.println("Game Over!");
            	System.out.println("Results sent to players");
            	System.exit(1);
	    }
	    
	    catch (Exception e) {
		System.out.println("Error: " + e.getMessage());
	    }
	    finally {
		if(socket != null) 
		    socket.close();
	    }
	}
	
	// Increments the total number of active clients
	public static void incrNumClients() {
	    numClients++;
	}
	
	// Winner is either 1 or 2 (indicating p1 and p2 respectively)
	public static void sendResults(int winner) {
	    String[] msg = {"You WON!", "You LOST!"};
	    if (winner == 2) {
	    	msg[0] = "You LOST!";
	    	msg[1] = "You WON!";
	    }
	    clientStack.get(0).printLine(msg[0], clientStack.get(0).getPacket());
	    clientStack.get(1).printLine(msg[1], clientStack.get(1).getPacket());
	}
}



/**  The clientThread class helps manage the multi-threading of clients threads. */

public class ClientThread implements Runnable {

	private DatagramSocket socket = null;
	private DatagramPacket packet;
	public String move;		// the player's move


	public ClientThread(DatagramSocket socket) {
	    this.socket = socket;
	}

	@Override
	public void run() { 

	    try {
		// Wait for client request
		DatagramPacket request = new DatagramPacket(new byte[1000], 1000);
		socket.receive(request);
		this.packet = request;
		
		// Client has joined the game
		UDPServer.incrNumClients();
		System.out.println("A client has joined the server");
		
		// Put reply into packet and send to client
		printLine("Waiting for another player to join...\n", request);
		
		while (true) {
                    // Checks for user input
                    request = new DatagramPacket(new byte[1000], 1000);
                    socket.receive(request);
                    
                    // Save the time the packet was received
                    Timestamp time = new Timestamp(System.currentTimeMillis());
                  
                    // Send the packet, it's timestamp, and it's sequence number to the server
                    this.packet = request;
                    sendPacket(request, time, UDPServer.sequenceNum);
                    
                    // Save the input as the client's move
                    String clientResponse = new String(request.getData(), 0, request.getLength());
                    this.move = clientResponse;
                    
                    if (this.move!=null) System.out.println("Player Move Accepted!");
               }
	    }
	    catch (Exception e) {
		System.out.println("Error: " + e.getMessage());
	    }
	}
	
	
	/** This method prints out a message on the Client side */
        public void printLine(String str, DatagramPacket request) {
            try {
               byte[] msg = new byte[1000];
               msg = str.getBytes();
               DatagramPacket reply = new DatagramPacket(msg, msg.length, request.getAddress(), request.getPort());
               this.socket.send(reply);
            }
            catch (Exception e) {
		System.out.println("Error: " + e.getMessage());
	    }
        }
        
	/** This method returns the DatagramPacket (called from UDPServer) */
        public DatagramPacket getPacket() {
            return this.packet;
        }
        
        public void sendPacket(DatagramPacket packet, Timestamp time, int num) {
            UDPServer.sequenceNum += 1;
            UDPServer.packetStack.add(new PacketType(packet, time, num));
        }
        
        
}


public class PacketType {

	public static DatagramPacket packet;
	public static Timestamp time;
	public static int sequenceNum;
	
	public PacketType(DatagramPacket packet, Timestamp time, int num) {
	    this.packet = packet;
	    this.time = time;
	    this.sequenceNum = num;
	}
	
}
