package simpleMessenger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {
	
    private Map<Socket,String> clients = new HashMap<Socket,String>();
    private ServerSocket serverSocket;
    private Scanner in;    //can be changed to the front input stream 
    final static int MAX_USER = 500;
    private static int id = 1;
    /**
     * Construct a new server
     * @param ip the IP address and the port number
     * @param port the input stream (you can change my code), that can be used to terminate the server
     * @param in when there is no client in the hash map, you can type "Quit" to the terminal to stop 
     *      the server program
     */
    
    public Server(String ip, int port, Scanner in) {
        this.in = in;
        System.out.println("Wait for connecting...");
        try {
            this.serverSocket = new ServerSocket(
                port,MAX_USER,InetAddress.getByName(ip)
            );
            System.out.println("Bind IP and port successfully\n"
                + "Start listening...");
            // add Thread here
            Thread t = new Thread(() -> addClient());
            t.start();
            while (true) {
            	// this is handled by server
                //if (clients.isEmpty()) {
                    String op = in.next();
                    if (op.equals("Quit")) {
                        t.interrupt();
                        break;
                    }
                //}                
            }
            stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    // return a hashmap of client socket and address pair
    public Map<Socket,String> getClientList() {
    	return this.clients;
    }
    
    // return the total number of clients
    public int getClientNumber() {
    	return clients.size();
    }
    
 // close all the threads (you can close the server from outside)
    public void stop() {
    	in.close();
    	for (Socket client:clients.keySet()) {
    		stop(client);
    	}
    	try {
			serverSocket.close();
		} catch (IOException e) {
			// close by user, don't handle
		}
    }
    
    // keep on listening and add client to the hash map
    private void addClient() {
        while (true) {
            try {
                Socket incoming = serverSocket.accept();
                String address = String.format("#%d: (%s)",id ,  
                		incoming.getInetAddress().toString());
                id++;
                clients.put(incoming, address);
                System.out.println("Accepting " + address);
                
                Thread t = new Thread(() -> {
                	StringBuilder message = new StringBuilder();
                    try {
                        Scanner in = new Scanner(incoming.getInputStream());
                        while (!incoming.isClosed() && in.hasNextLine()) {
                            String line = in.nextLine();
                            if (line.trim().equals("END")
                            		||line.trim().equals("Bye")||line.trim().equals("bye")) {
                            	
                            	 if(!line.trim().equals("Bye") && !line.trim().equals("bye"))
                                     synchronized (this) {
                                    	 broadCast(message.toString(),address);
                                     	 message.delete(0, message.length());
                                     	 message.append("\n");
                                     }
                                     else {
                                         stop(incoming);
                                         in.close();
                                         break;
                                     }
                            } else {
                                message.append(line);
                                message.append('\n');
                            }
                        }
                    } catch (IOException e) {
                        // where is your justification????????
                        // Justify whenever you catch an exception but do nothing!!!!!!!
                    	stop(incoming);
                    	in.close();
                    } 
                        
                });
                t.start();
            } catch (IOException e) {
                break;
            }
        }
    }
    
    // to stop one of the client
    private void stop(Socket s){
        try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        System.out.println(clients.get(s) + " is left");
        clients.remove(s);
    }
     
    // to send the message to all the clients
    private void broadCast(String msg, String address) {
	   for (Socket client : clients.keySet()) {
	       /*
	       try {
		       PrintWriter out = new PrintWriter(
		       new OutputStreamWriter(client.getOutputStream()),
		       true    //auto flush
		       );
		       out.println(address + ": " + msg);
		       } catch (IOException e) {
		       e.printStackTrace();
	       }
	       */
	       try {
		       client.getOutputStream().write(
		       String.format("%s:\n%s\n", 
		                         address,
		                         msg).getBytes());
		       client.getOutputStream().flush();
	       } catch (IOException e) {
	           stop(client);
	       } catch (ConcurrentModificationException e) {
	    	   stop(client);
	       }
	    }
    }
    
    // just for test
    public static void main(String[] args) {
        Scanner cin = new Scanner(System.in);
        System.out.print("Input local machine: ");
        String ip = cin.next();
        System.out.print("Input the port that you want to bind: ");
        int port = cin.nextInt();
        Server s = new Server(ip,port,cin);
        System.out.println("Thanks for using");
    }
}
