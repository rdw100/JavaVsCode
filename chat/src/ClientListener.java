package chat.src;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;

/**
 * Listens (receives) messages from the server.
 */
public class ClientListener implements Runnable 
{
   // The socket for a client to make request
   private Socket socket;
   
   // The client's username
   private String userName;
   
   // The new chat message
   private String message;
   
   // True if a new chat message; otherwise, false.
   private boolean hasMessages = false;
   
   // The implemented Lock object 
   private Lock messageLock;
   
   // The local formatted time for console output
   private String messageTime;

   /**
    * Initializes a ClientListener object
    * @param socket The socket for a client to make request
    * @param userName The client's username
    */
   public ClientListener(Socket socket, String userName)
   {
      this.socket = socket;
      this.userName = userName;
      this.messageLock = new ReentrantLock();
      this.messageTime = getMessageTime();
   }
   
   /**
    * Gets local formatted time for console output
    * @return Returns local formatted time for console output
    */
   public String getMessageTime() 
   {
      DateTimeFormatter timeFormatter = DateTimeFormatter
         .ofPattern("HH:mm:ss");
      LocalTime localTime = LocalTime.now();
      messageTime = "(" + localTime.format(timeFormatter) + ") ";

      return messageTime;
   }

   /**
    * Adds current chat message. Locking ensures 
    * only one thread can run this code at a time.
    * @param newMessage The new chat message
    */  
   public void addMessage(String newMessage)
   {    
      messageLock.lock();   
      try      
      {
         hasMessages = true;
         message = newMessage;
      }
      finally
      {
         messageLock.unlock();
      }     
   } 
 
   /**
    * Removes current chat message. Locking ensures 
    * only one thread can run this code at a time. 
    */
   public void removeMessage() 
   {
      messageLock.lock();
      try      
      {
         hasMessages = false;
         message = "";
      }
      finally
      {
         messageLock.unlock();
      }
   } 

   /**
    * Formats server response
    * @param text Server response
    * @return Returns formatted server response
    */
   public String serverFormatter(String text)
   {
      return "\u001B[90m" + text + "\u001B[0m"; 
   }

   /**
    * Formats server response
    * @param code Ansi escape code for background color
    * @param text Server response     
    * @return Returns formatted server response
    */
   public String serverFormatter(Integer code, String text)
   {
      return "\u001B[" + code + "m" + text + "\u001B[0m";
   }

   /**
    * Receives message from the server and sends to client.
    */
   public void run()
   {
      System.out.print("\n");
      System.out.println(serverFormatter("Chat Client is connecting."));
      System.out.println(serverFormatter("Local Port: " + socket.getLocalPort() ));
      System.out.println(serverFormatter("Server = " + socket.getRemoteSocketAddress() + ":" + socket.getPort()));
      System.out.println(serverFormatter("Chat Client is connected."));
      System.out.print("\n");
      System.out.println(messageTime + serverFormatter(94, userName + " has joined the conversation." ));

      try
      {
         PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), false);
         InputStream serverInStream = socket.getInputStream();
         Scanner serverIn = new Scanner(serverInStream);
      
         while (!socket.isClosed())
         {
            if (serverInStream.available() > 0)
            {
               if (serverIn.hasNextLine())
               {
                  System.out.println(serverIn.nextLine());
               }
            }
            if (hasMessages)  
            {
               String nextSend = "";
               nextSend = message;           
               removeMessage();
               
               serverOut.println(messageTime 
                                 + serverFormatter(94, userName + ":") 
                                 + serverFormatter(97, " > " 
                                 + nextSend));
               serverOut.flush();
            }
         }
         socket.close();
         serverIn.close();
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }
}