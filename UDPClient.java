//Authors: DAVID SALDANA (6155964), LIAM YETHON (6255384), JESSE MASCIARELLI (6243109), KATIE LEE (6351696)

import java.net.*;
import java.io.*;
import java.util.*;

public class UDPClient {

    public static void main(String args[]) throws IOException {
    
        boolean gameOver = false, valid= true, testFlag=false;
        int turn = 0;
        
        //Packet Size
        byte[] bufferLength = new byte[1000];

        //Initialize BufferReader for User Input
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            // Create client socket
            DatagramSocket clientSocket = new DatagramSocket();
            
            //Open connection loop
            while(!gameOver) {
            
                if(turn==0) System.out.println("Type 'start' to begin the game or 'test' to begin the game in testing mode");

                //Obtain client's input
                String input = "";

                while(turn==0 && valid){
                    input = (String)inputReader.readLine();

                    if(input.equals("start")) valid=false;
                    if(input.equals("test")){
                        valid=false;
                        testFlag=true;
                    }
                }

                //Build DatagramPacket for client's input
                DatagramPacket packet = new DatagramPacket(input.getBytes(), input.getBytes().length,
                        InetAddress.getByName("localhost"), 4000);

                //Send client's input
                clientSocket.send(packet);

                for (int i=0; i<2; i++) {
                    //Build DatagramPacket for server's response
                    DatagramPacket reply = new DatagramPacket(bufferLength, bufferLength.length);

                    //Receive and print server's response
                    clientSocket.receive(reply);
                    String serverResponse = new String(reply.getData(), 0, reply.getLength());
                    System.out.println(serverResponse);
                }

                //Waits for user input (rock/paper/scissors)
                while ((input = (String)inputReader.readLine().toLowerCase()) != null && !input.equals("rock") && 
                	!input.equals("paper") && !input.equals("scissors")) {
			   System.out.println("Invalid move. Please enter one of the following: rock, paper, scissors\n");
		}

		//Build new DatagramPacket for client's input
		packet = new DatagramPacket(input.getBytes(), input.getBytes().length,
                        InetAddress.getByName("localhost"), 4000);

                //Send Client's Input
                clientSocket.send(packet);
                
                for (int i=0; i<2; i++) {
                    //Build DatagramPacket for server's response
                    DatagramPacket reply = new DatagramPacket(bufferLength, bufferLength.length);

                    //Receive and print server's response
                    clientSocket.receive(reply);
                    String serverResponse = new String(reply.getData(), 0, reply.getLength());
                    System.out.println(serverResponse);
                    
                    if(serverResponse.equals("You WON!") || serverResponse.equals("You LOST!")) System.exit(1);
                }
                turn++;
            }
        }
        catch (Exception e) {
		System.out.println("Error: " + e.getMessage());
	}
    }
}
