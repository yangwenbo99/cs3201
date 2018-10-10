package simpleMessenger;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.Scanner;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

public class Client implements ChatBack {
    private Socket      socket = null;
    private ChatFront   front = null;
    private Thread      waitingThread;
    private Scanner     in = null;
    private PrintWriter out = null;
    /** 
     * Connect to server.
     * @return true if successful
     */
	public void connect(String host, int port) 
            throws UnknownHostException, IOException {
        socket = new Socket(host, port);
        // socket.connect(); already connected when constructed
    }

    /**
     * Bind this client to a CharFront object.
     */
	public boolean bind(ChatFront chatFront) {
        this.front = chatFront;
        try {
            start();
        } catch (IOException e) {
            System.err.println(e);
            return false;
        }
        front.informBound(this);

        return true;
    }

    /** 
     * Star the clients' receiving message.
     *
     * This method should only be called after bind, and 
     */
	private boolean start() throws IOException {
        if (front == null) {
            return false;
        }

        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);

        Runnable waitingForGettingMessage = () -> {
            StringBuilder sb = new StringBuilder();
            synchronized (Client.this) {
                try {
                    while (!socket.isClosed()) {
                        sb.delete(0, sb.length());
                            String line = in.nextLine();
                            if (line.equals("END")) {
                                front.send(this, sb.toString());
                            } else {
                                sb.append(line);
                                sb.append('\n');
                            }
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    // stop the connection by the user
                    // no need to handle...
                } catch (NoSuchElementException e) {
                    front.end();
                } finally {
                    closeResources();
                    System.out.println("BACK STOPED");
                }
            }
        };

        waitingThread = new Thread(waitingForGettingMessage);
        waitingThread.start();
        return true;
    }

    synchronized private void closeResources() {
        in.close();
        out.close();
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(e.toString());
            Thread.dumpStack();
        }
    }

    /**
     * Stop the connection.
     *
     * After calling this method, all operation to the object is undefined
     */
	public boolean stop(){
        try {
            socket.close();
            waitingThread.interrupt();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Send message from this client to server.
     * @param cf the ChatFront object bounded to this client (only to make 
     *          sure that the called is or has the object.
     * @param message message to send
     *
     * This method should only be claaed by bound ChatFront object, cf should
     * be that object. Otherwise, an exception will be thrown.
     */
	public void send (ChatFront cf, String message) {
        if (cf != front) {
            throw new IllegalCallerException(
                    "Only corresponding front should call this method");
        } 
        out.print(message);
        out.flush();
	}

    public static void main (String[] args) throws Exception {
        System.out.println("Start");
        Client c = new Client();
        c.connect("127.0.0.1", 8000);
        ChatFront f = new ChatFrontTest("/tmp/Test.txt");
        f.bindTo(c);
        c.bind(f);
    }
}
