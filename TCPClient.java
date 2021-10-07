import java.io.*;
import java.net.*;

public class TCPClient{
	public static void main(String[] args) throws IOException{
		if(args.length != 2){
			System.err.println("Provide Parameters: java TCPClient.java <host name> <port number>");
			System.exit(1);
		}

		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);

		try(Socket echoSocket = new Socket(hostName, portNumber);
			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
			BufferedReader in= new BufferedReader (new InputStreamReader(echoSocket.getInputStream()));
			BufferedReader stdIn= new BufferedReader( new InputStreamReader(System.in))){
			System.out.println("Waiting for a second player...");

			//User Input
			String userInput;
			String serverInput;
			if(((serverInput = in.readLine())!= null)){
				System.out.println(serverInput);
				out.println(serverInput);
			}
			while (((userInput = stdIn.readLine().toLowerCase()) != null) && !userInput.equals("rock") && !userInput.equals("paper") && !userInput.equals("scissors")) {
				System.out.println("Invalid move. Please enter one of the following: rock, paper, scissors\n");
			}

			out.println(userInput);
			
			if(((serverInput = in.readLine())!= null)){
				System.out.println(serverInput);
				out.println(serverInput);
			}
			while(((serverInput = in.readLine())!= null)){
				System.out.println(serverInput);
			}
		}
		catch (UnknownHostException e){
			System.err.println("Host Exception Error");
			System.exit(1);
		} catch (IOException e){
			System.err.println("IO Exception Error");
			System.exit(1);
		}
	}
}