package chat.src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *The server broadcasts client's sent messages.  
 */
public class Server 
{
   // The server port constant
   private static final int PORT = 8888;
   
   // The server port
   private int serverPort;
   
   // The arraylist collection of connencted clients
   private ArrayList<ClientHandler> clients;
   
   // The object to listen on a specific port
   private ServerSocket serverSocket;
   
   // The object reading from and writing to the socket
   private Socket socket;

   /**
    * Creates the chat server.
    * @param args nothing
    */    
   public static void main(String[] args)
   {
      Server server = new Server();
      server.start();
   }

   /**
    * Initializes the server
    */
   public Server()
   {
      serverPort = PORT;
   }

   /**
    * Gets a collection of connected client
    */
   public ArrayList<ClientHandler> getClients()
   {
      return clients;
   }

   /**
    * Attempts to open a local port
    * @param port The local port number
    * @return Returns true if available; otherwise, false.
    */
   private boolean isPortOpen(int port) {
      try 
      {
          new ServerSocket(port).close();          
          return true;
      } catch(IOException e) 
      {
          return false;
      }
  }

   /**
    * Starts a server that listens for clients
    */
   private void start()
   {
      clients = new ArrayList<ClientHandler>();
      serverSocket = null;
      
      if (isPortOpen(serverPort)) 
      {
         try 
         {
            System.out.println("Binding to port: " + serverPort + ", please wait  ...");
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Chat Server started: " + serverSocket.getLocalSocketAddress() + "\n");
            
            // Wait for client connections.
            while (true)
            {
               try
               {
                  System.out.println("Waiting for clients to connect ..."); 
                  socket = serverSocket.accept();
                  System.out.println("Remote client connected: " + socket.getRemoteSocketAddress() + "\n");
                  ClientHandler client = new ClientHandler(this, socket);
                  Thread thread = new Thread(client);
                  thread.start();
                  clients.add(client);
               } 
               catch (IOException ex)
               {
                  System.out.println("Client connection failed: " + serverPort);
               }
            }                     
         } 
         catch (IOException e)
         {         
            System.out.println("Server unable to bind to port: " + serverPort);
            System.exit(1);
         }
      }
      else
      {
         System.out.println("Server unable to bind to port: " + serverPort);
         System.out.println("Port is in use.");
         System.exit(1);
      }      
   }
}