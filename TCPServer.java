import java.net.*;
import java.io.*;
import java.util.*;

public class TCPServer {

    public static  int portNumber;
    public static List<ClientThread> clientStack = new Stack<ClientThread>();
    public static String p1Move = "", p2Move = "";

    public static void main(String[] args) throws IOException {

        if(args.length != 1) {
            System.err.println("Provide Parameter: java JavaServer <port number>");
            System.exit(1);
        }
        portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(portNumber);) {
            //Infinite while loop to accept incoming clients
            while(true){

                Socket socket = serverSocket.accept();
                System.out.println("Client Connection Accepted");

                //Create client thread and add to client array
                ClientThread client = new ClientThread(socket);
                clientStack.add(client);

                //Start new client thread
                new Thread(client).start();
            }
        }
        catch (IOException e) {
            System.out.println("There was an exception "+e.getMessage());
        }
    }

    private static class ClientThread implements Runnable {

        private Socket socket;
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
                    //Winning/Losing test messages
                    if (inputLine.equals("winner")) this.printMessage("I won!:D");
                    if (inputLine.equals("loser")) this.printMessage("I lost:(");

                        //Print whatever the client said in upper case plus the port number
                    else {
                        System.out.println("Client said: " + inputLine);
                        out.println(inputLine.toUpperCase());
                    }
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