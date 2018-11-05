package simpleMessenger;

import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);
		System.out.print("***     SIMPLE TEXT MESSENGER   ***\r\n" + 
				"(C)onnect peer\r\n" + 
				"(W)ait for the other peer connecting\r\n" + 
				"(Q)uit\n" + "Please choose: ");
		String ins;
		ins = in.next();
		if (ins.equals("c")||ins.equals("C")) {
			while(true) {
				try {
					System.out.print("\nRemote IP: ");
					String ip = in.next();
					System.out.print("Port Number: ");
					int port = in.nextInt();
					Client c = new Client();			
				    c.connect(ip, port);
				    ChatFront f = new UserInterface("Client");
				    f.bindTo(c);
				    c.bind(f);
				    break;
				} catch (ConnectException e) {
					System.out.println("The port number is wrong");
				} catch (SocketException s) {
					System.out.println("Network is unreachable");
				}
			}
		} else if (ins.equals("W")||ins.equals("w")){
			System.out.print("\nInput local machine: ");
	        String r_ip = in.next();
	        while(true) {
	            try {
	                System.out.print("Input the port that you want to bind: ");
	                int port = in.nextInt();
	                Server s = new Server(r_ip,port);
                    Client c = new Client();
                    c.connect(r_ip,port);
                    ChatFront f = new UserInterface("Server");
                    f.bindTo(c);
                    c.bind(f);
                    while (true) {
                    	String op = in.next();
                    	if (op.equals("End")) {
                    		s.stop();
                    		break;
                    	}
                    }
	                System.out.println("Thanks for using");
	                break;
	            } catch (BindException e) {
	                System.out.println("The port is being used, please try another one");
	                continue;
	            }
	        }
		} else {
			System.out.println("Program terminated.");
		}
		in.close();
	}
}
