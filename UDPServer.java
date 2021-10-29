import java.io.*;
import java.net.*;

public class UDPServer {
    public static void main(String args[]) {
        try {
            boolean gameOver=false;

            //Packet Size
            byte[] bufferLength = new byte[999];

            //Datagram Socket
            DatagramSocket dSocket = new DatagramSocket(4000);

            //Datagram Packet
            DatagramPacket dPacket = new DatagramPacket(bufferLength, bufferLength.length);

            System.out.println("Waiting for 2 players to join the game...");

            //Server waits for incoming connections
            while(!gameOver) {
                //Receive Packet from a Client
                dSocket.receive(dPacket);

                //Convert incoming packet into string
                String clientMessage = new String(dPacket.getData(), 0, dPacket.getLength());

                //Print Details Server Side
                System.out.println("Client said: "+clientMessage);

                //Send a message back to client
                clientMessage = "Server received your message: " + clientMessage;

                //Build DatagramPacket that will be sent back to client
                DatagramPacket responsePacket = new DatagramPacket(clientMessage.getBytes() , clientMessage.getBytes().length ,
                        dPacket.getAddress() , dPacket.getPort());

                //Send DatagramPacket back to client
                dSocket.send(responsePacket);
            }
        }
        //Handles IO Exception
        catch(IOException e) {
            System.out.println("There was an IO Exception" + e);
        }
    }
}