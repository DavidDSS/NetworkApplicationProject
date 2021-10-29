import java.io.*;
import java.net.*;

public class UDPClient {
    public static void main(String args[]) {
        boolean gameOver=false;
        
        //Packet Size
        byte[] bufferLength = new byte[999];

        //Initialize BufferReader for User Input
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            //Open Connection Loop
            while(!gameOver) {
                System.out.print("Enter message to send: ");

                //Obtain client's input
                String clientMessage = (String)inputReader.readLine();

                //Build DatagramPacket for client's input
                DatagramPacket  clientPacket = new DatagramPacket(clientMessage.getBytes(), clientMessage.getBytes().length,
                        InetAddress.getByName("localhost"), 4000);

                //Send Client's Input
                clientSocket.send(clientPacket);

                //Build DatagramPacket for server's response
                DatagramPacket reply = new DatagramPacket(bufferLength, bufferLength.length);

                //Receive Server's response
                clientSocket.receive(reply);

                //Parse Server's response into a String
                String serverResponse = new String(reply.getData(), 0, reply.getLength());

                //Print Server's response
                System.out.println(serverResponse);
            }
        }
        //Handles IO Exception
        catch(IOException e) {
            System.out.println("There was an IO Exception" + e);
        }
    }
}