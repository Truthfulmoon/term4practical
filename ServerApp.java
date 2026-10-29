/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package za.ac.cput.serverapp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Scanner;
import za.ac.cput.serverapp.ServerDAO.StudentDAO;

/**
 *
 * @author 
 */
public class ServerApp
{

   private static final int PORT=6666;
    private ServerSocket listener;
    private Socket client;
    private final Scanner scn = new Scanner(System.in);
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private StudentDAO dao;
    
   
    public ServerApp()
    {
        dao=new StudentDAO();
        dao.createTable();
       
        try {
            listener = new ServerSocket(PORT, 1);
            System.out.println("Server listening on port "+PORT);
            
        }
        catch (IOException ioe)
        {
            System.out.println("Could not start server on port 6666: " + ioe.getMessage());
        }
    }

    public void listen()
    {
        if (listener == null)
        {
            System.out.println("Server socket not created - is another ServerApp still running?");
            return;
        }

        // Start listening for client connections
        try {
            System.out.println("Server is listening");
            client = listener.accept();
            System.out.println("Client connection has been accepted");

            processClient();

            // Chat is over - close the server socket too
            listener.close();
            dao.closeConnection();
            System.out.println("Server closed.");
        }
        catch (IOException ioe)
        {
            System.out.println("IO Exception: " + ioe.getMessage());
        }
    }

    public void processClient()
    {
        // First step: initiate channels
        System.out.println("Creating input and output streams on Server");

        try (ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(client.getInputStream()))
        {
            out.flush();

            // Step 2: chat loop - keeps going until the client sends "exit"
            String msg = (String) in.readObject();

            while (!msg.equalsIgnoreCase("exit"))
            {
                System.out.println("From CLIENT>> " + msg);

                // Let the server user type a reply (blank lines are ignored)
                String reply;
                do
                {
                    System.out.print("Reply to client>> ");
                    reply = scn.nextLine().trim();
                }
                while (reply.isEmpty());

                out.writeObject(reply);
                out.flush();

                // Wait for the client's next message
                msg = (String) in.readObject();
            }

            System.out.println("Client has left the chat. Closing connection.");
        }
        catch (IOException ioe)
        {
            System.out.println("IO Exception: " + ioe.getMessage());
        }
        catch (ClassNotFoundException cnfe)
        {
            System.out.println("Class not found: " + cnfe.getMessage());
        }
        finally
        {
            // Step 3: close down (closing the streams already closed the socket,
            // this is just a safety net)
            try
            {
                client.close();
            }
            catch (IOException ioe)
            {
                System.out.println("IO Exception: " + ioe.getMessage());
            }
        }
    }

    public static void main(String[] args)
    {
        // Create application
        ServerApp server = new ServerApp();

        // Start waiting for connections
        server.listen();
    }
}