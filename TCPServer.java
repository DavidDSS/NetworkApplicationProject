//Authors: DAVID SALDANA (6155964), LIAM YETHON (6255384), JESSE MASCIARELLI (6243109), KATIE LEE (6351696)

import java.net.*;
import java.io.*;
import java.util.*;

public class TCPServer {
    public static  int portNumber;
    public static List<ClientThread> clientStack = new Stack<ClientThread>();
    public static String p1Move = null, p2Move = null;

    public static void main(String[] args) throws IOException {
        if(args.length != 1) {
            System.err.println("Provide Parameter: java JavaServer <port number>");
            System.exit(1);
        }
        System.out.println("ROCK, PAPER, SCISSORS");
        System.out.println("Waiting for 2 players to connect...");

        portNumber = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(portNumber);) {
            //Infinite while loop to accept incoming clients
            while(clientStack.size()<2){
                Socket socket = serverSocket.accept();
                System.out.println("Client Connection Accepted");

                //Create client thread and add to client array
                ClientThread client = new ClientThread(socket);
                clientStack.add(client);

                //Start new client thread
                new Thread(client).start();
            }

            clientStack.get(0).printMessage("Starting Game, choose 'rock', 'paper', or 'scissors':");
            clientStack.get(1).printMessage("Starting Game, choose 'rock', 'paper', or 'scissors':");
            
            //Wait for both players to respond
            while(p1Move==null || p2Move==null){
                p1Move=clientStack.get(0).playerMove;
                p2Move=clientStack.get(1).playerMove;
            }

            clientStack.get(0).printMessage("Checking who won...");
            clientStack.get(1).printMessage("Checking who won...");

            clientStack.get(0).printMessage("You -> " + p1Move + " VS " +p2Move+ " <- Opponent");
            clientStack.get(1).printMessage("You -> " + p2Move + " VS " +p1Move+ " <- Opponent");

            //Rock, Paper, Scissors Logic
            if(p1Move.equals(p2Move)){
                clientStack.get(0).printMessage("Its a TIE");
                clientStack.get(1).printMessage("Its a TIE");
            }
            else if(p1Move.equals("rock")){
                if(p2Move.equals("paper")){
                    //Player 2 Wins
                    clientStack.get(0).printMessage("You LOSE!");
                    clientStack.get(1).printMessage("You WIN!");
                }
                else if(p2Move.equals("scissors")){
                    //Player 1 Wins
                    clientStack.get(1).printMessage("You LOSE!");
                    clientStack.get(0).printMessage("You WIN!");
                }
            }
            else if(p1Move.equals("paper")){
                if(p2Move.equals("scissors")){
                    //Player 2 Wins
                    clientStack.get(0).printMessage("You LOSE!");
                    clientStack.get(1).printMessage("You WIN!");
                }
                else if(p2Move.equals("rock")){
                    //Player 1 Wins
                    clientStack.get(1).printMessage("You LOSE!");
                    clientStack.get(0).printMessage("You WIN!");
                }
            }
            else if(p1Move.equals("scissors")){
                if(p2Move.equals("rock")){
                    //Player 2 Wins
                    clientStack.get(0).printMessage("You LOSE!");
                    clientStack.get(1).printMessage("You WIN!");
                }
                else if(p2Move.equals("paper")){
                    //Player 1 Wins
                    clientStack.get(1).printMessage("You LOSE!");
                    clientStack.get(0).printMessage("You WIN!");
                }
            }
            System.out.println("Game Over!");
            System.out.println("Results sent to players");
            System.exit(1);
        }
        catch (IOException e) {
            System.out.println("Game Closed "+e.getMessage());
        }
    }

    private static class ClientThread implements Runnable {

        private Socket socket;
        public String playerMove;
        public ClientThread(Socket socket){this.socket = socket;}

        public void run() {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {

                    //Disconnect if client quits
                    if (inputLine.equals("/quit")){
                        System.out.println("Client disconnected");
                        socket.close();
                        break;
                    }
                    //Checks for user input
                    playerMove=in.readLine();
                    if(playerMove!=null) System.out.println("Player Move Accepted!");
                }
            }
            catch (IOException e) {
                System.out.println("There was an exception "+e.getMessage());
            }
        }

        //This method prints out a message on the Client side
        public void printMessage(String str) {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(str);
            }
            catch (IOException e) {
                System.out.println("There was an exception "+e.getMessage());
            }
        }
    }
}