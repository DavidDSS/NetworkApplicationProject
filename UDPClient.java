//Authors: DAVID SALDANA (6155964), LIAM YETHON (6255384), JESSE MASCIARELLI (6243109), KATIE LEE (6351696)

import java.net.*;
import java.io.*;
import java.util.*;


public class UDPClient {

    public static void main(String args[]) throws IOException {
    
        boolean gameOver = false;
        int turn = 0;
        
        if(args.length != 1) {
	    System.err.println("Provide Parameter: java UDPServer.java <port number>");
	    System.exit(1);
	}
        
        // Packet Size
        byte[] bufferLength = new byte[1000];

        // Initialize BufferReader for User Input
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            // Create Client socket
            DatagramSocket clientSocket = new DatagramSocket();
            
            // Open connection loop
            while(!gameOver) {
            
                if(turn==0) System.out.println("Type 'start' to begin the game");

                // Obtain Client's input
                String input = (String)inputReader.readLine();

                while(turn==0 && !input.toLowerCase().equals("start")){
                    input = (String)inputReader.readLine();
                }

                // Build DatagramPacket for Client's input
                DatagramPacket packet = new DatagramPacket(input.getBytes(), input.getBytes().length,
                        InetAddress.getByName("localhost"), 4000);

                // Send Client's input
                clientSocket.send(packet);

                for (int i=0; i<2; i++) {
                    // Build DatagramPacket for server's response
                    DatagramPacket reply = new DatagramPacket(bufferLength, bufferLength.length);

                    // Receive and print server's response
                    clientSocket.receive(reply);
                    String serverResponse = new String(reply.getData(), 0, reply.getLength());
                    System.out.println(serverResponse);
                }

                // Waits for user input (rock/paper/scissors)
                while ((input = (String)inputReader.readLine().toLowerCase()) != null && !input.equals("rock") && 
                	!input.equals("paper") && !input.equals("scissors")) {
			   System.out.println("Invalid move. Please enter one of the following: rock, paper, scissors\n");
		}

		// Build new DatagramPacket for Client's input
		packet = new DatagramPacket(input.getBytes(), input.getBytes().length,
                        InetAddress.getByName("localhost"), 4000);

                // Send Client's Input
                clientSocket.send(packet);
                
                // Start timer (for delayed segment condition check)
                long startTime = System.currentTimeMillis();
                Thread t = new Thread();
            	t.start();
            	
                for (int i=0; i<2; i++) {
			
                    //one second timer
		    while (System.currentTimeMillis() < Long.sum(startTime,1000L)) { }
			
		    if (!t.isAlive()) {  
			// Build DatagramPacket for server's response
                      	DatagramPacket reply = new DatagramPacket(bufferLength, bufferLength.length);

                    	// Receive and print server's response
                    	clientSocket.receive(reply);
                    	String serverResponse = new String(reply.getData(), 0, reply.getLength());
                    	System.out.println(serverResponse);
                    
                    	if(serverResponse.equals("You WON!") || serverResponse.equals("You LOST!")) System.exit(1);
	  	    }
	  	    else {
	  		t.interrupt();
	  		System.out.println("Packet was lost");
	  	    }
                }
                turn++;
            }
        }
        catch (Exception e) {
		System.out.println("Error: " + e.getMessage());
	}
    }
	
}//UDPClient;
