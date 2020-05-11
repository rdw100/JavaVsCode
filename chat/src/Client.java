package chat.src;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * The client displays all messages received from the server 
 */
public class Client
{   
   // The host's server address
   private static final String HOST = "localhost";
   
   // The server port value
   private static final int PORT = 8888;
   
   // The chat client's username     
   private final String userName;

   // The server host address 
   private final String serverHost;

   // The assigned server port 
   private final int serverPort;

   /**
    * Creates the chat client.
    * 
    * @param args nothing
    */
   public static void main(final String[] args) {
      String user = null;
      final Scanner scan = new Scanner(System.in);
      System.out.println("Please enter your screen name:");

      while (user == null || user.isEmpty()) {
         user = scan.nextLine();
         if (user.trim().equals("")) {
            System.out.println("Invalid. Please enter your screen name:");
         }
      }

      final Client client = new Client(user, HOST, PORT);
      client.start(scan);
   }

   /**
    * Initializes a client object
    * 
    * @param userName   The client's username
    * @param host       The server host
    * @param portNumber The server port
    */
   private Client(final String userName, final String host, final int portNumber) {
      this.userName = userName;
      this.serverHost = host;
      this.serverPort = portNumber;
   }

   /**
    * Starts a client connection
    * 
    * @param scan The client's input for socket
    */
   private void start(final Scanner scan) {
      try {
         final Socket socket = new Socket(serverHost, serverPort);
         final ClientListener clientEar = new ClientListener(socket, userName);
         final Thread clientChatThread = new Thread(clientEar);
         clientChatThread.start();

         while (clientChatThread.isAlive()) {
            if (scan.hasNextLine()) {
               clientEar.addMessage(scan.nextLine());
            }
         }
         socket.close();
      } catch (final IOException ex)
      {
         System.out.println("Fatal Connection error!");
      }
   }
}