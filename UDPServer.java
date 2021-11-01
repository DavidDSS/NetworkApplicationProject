//Authors: DAVID SALDANA (6155964), LIAM YETHON (6255384), JESSE MASCIARELLI (6243109), KATIE LEE (6351696)

import java.io.*;
import java.net.*;
import java.util.Random;

public class UDPServer {
    public static void main(String args[]) {
        Random rand = new Random();
        int randomNum = rand.nextInt((10 - 0) + 1) + 0;

        try {
            boolean gameOver=false;
            int turn=0;
            //Packet Size
            byte[] bufferLength = new byte[999];

            //Datagram Socket
            DatagramSocket dSocket = new DatagramSocket(4000);

            //Datagram Packet
            DatagramPacket dPacket = new DatagramPacket(bufferLength, bufferLength.length);

            System.out.println("GUESSING GAME");
            System.out.println("-------------");
            System.out.println("Waiting for 1 player to join the game...");

            //Server waits for incoming connections
            while(!gameOver) {

                //Receive Packet from a Client
                dSocket.receive(dPacket);

                //Convert incoming packet into string
                String clientMessage = new String(dPacket.getData(), 0, dPacket.getLength());

                //Print Details Server Side
                System.out.println("Client said: "+clientMessage);

                if(turn==0){
                    clientMessage = "The server chose a random number between 1-10. You have 3 attempts to guess it!"+randomNum;
                }
                else if(turn<3 && !clientMessage.equals(String.valueOf(randomNum))){
                    clientMessage = "Try Again! (Attempts Left: "+(3-turn)+")";

                }
                else if (clientMessage.equals(String.valueOf(randomNum))){
                    clientMessage = "You WON!";
                    gameOver=true;
                }
                else{
                    clientMessage = "You LOST!";
                    gameOver=true;
                }

                //Build DatagramPacket that will be sent back to client
                DatagramPacket responsePacket = new DatagramPacket(clientMessage.getBytes() , clientMessage.getBytes().length ,
                        dPacket.getAddress() , dPacket.getPort());
                
                //Send DatagramPacket back to client
                dSocket.send(responsePacket);

                turn++;
            }
        }
        //Handles IO Exception
        catch(IOException e) {
            System.out.println("There was an IO Exception" + e);
        }
    }
}