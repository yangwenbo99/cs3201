import java.net.*;
import java.util.*;
import java.io.*;
public class Server {
	private Map<Socket,String> clients = new HashMap<Socket,String>();
	private ServerSocket serverSocket;
	private Scanner in;    //can be changed to the front input stream 
	final static int MAX_USER = 5;
	
	/* IMPORTANT!!! => the constructor takes 3 variables
	 *  (1) the IP address and the port number
	 *  (2) the input stream (you can change my code), that can be used to terminate the server
	 *  (3) when there is no client in the hash map, you can type "Quit" to the terminal to stop 
	 *  	the server program
	 */
	public Server(String ip,int port,Scanner in) {
		this.in = in;
		System.out.println("Wait for connecting...");
		try{
			this.serverSocket = new ServerSocket(
			    port,MAX_USER,InetAddress.getByName(ip)
			);
			System.out.println("Bind IP and port successfully\n"
			    + "Start listening...");
			// add Thread here
			Thread t = new Thread(()->{addClient();});
			t.start();
			while(true) {
				if(clients.isEmpty()) {
					String op = in.next();
					if(op.equals("Quit"))
						t.interrupt();
						break;
				}				
			}
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// keep on listening and add client to the hash map
	private void addClient() {
		while (true) {
			try {
				Socket incoming = serverSocket.accept();
				String address = incoming.getInetAddress().toString();
				clients.put(incoming, address);
				System.out.println("Accepting "+address);
				
				Thread t = new Thread(()->{
				    try {
				    	Scanner in = new Scanner(incoming.getInputStream());
						boolean done = false;
						while(!incoming.isClosed()&&!done && in.hasNextLine()) {
							String line = in.nextLine();
							if(!line.trim().equals("Bye")&&!line.trim().equals("bye"))
								broadCast(line,address);
							else {
								stop(incoming);
							}
						}
				    } catch (IOException e) {
				    	
				    } 
						
				});
				
				t.start();
			} catch (IOException e) {
				break;
			}
		}
	}
	
	// to stop one of the client
	private void stop(Socket s) throws IOException {
		s.close();
		System.out.println(clients.get(s)+" is left");
		clients.remove(s);
	}
	
	// to send the message to all the clients
	private void broadCast(String msg,String address) {
		for (Socket client:clients.keySet()) {
			try {
				PrintWriter out = new PrintWriter(
				    new OutputStreamWriter(client.getOutputStream()),
				    true    //auto flush
				);
				out.println(address+": "+msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
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
