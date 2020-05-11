package chat.src;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * The server echoes back everything that is sent by the client.  
 * Receives message from the client processed by the server and 
 * sends it to all connected clients.
 */
public class ClientHandler implements Runnable 
{
   /**
   The socket for a client to make request
   */
   private Socket socket;
   
   /**
   The output from the socket
   */
   private PrintWriter out;
   
   /**
   The input from the socket
   */
   private Scanner in;
   
   /**
   The chat server location
   */
   private Server server;
   
   /**
   Initializes the client handler
   @param server The chat server location
   @param socket The socket for a client to make request
   */
   public ClientHandler(Server server, Socket socket)
   {
      this.server = server;
      this.socket = socket;
   }
   
   /**
   Retrieves socket output stream
   @return Returns output stream from server
   */
   private PrintWriter getWriter()
   {
      return out;
   }
   
   /**
   Send and get text/data from the client
   */
   public void run() 
   {
      try
      {
         try
         {
            // Send and get message to from the client
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), false);
         
            // Getting the input and output streams 
            while (!socket.isClosed())
            {
               if (in.hasNextLine())
               {
                  String input = in.nextLine();
               
                  for (ClientHandler client : server.getClients())
                  {
                     PrintWriter outClient = client.getWriter();
                     if (outClient != null)
                     {
                        outClient.write(input + "\r\n");
                        outClient.flush(); 
                     }
                  }
               }
            }
         }
         finally
         {
            socket.close();
         }
      } 
      catch (IOException e) 
      {
         System.out.println("Error opening client chat thread.");
      }
   }
}