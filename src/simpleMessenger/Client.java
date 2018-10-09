package simpleMessenger;

public class Client implements ChatBack {
    /** 
     * Connect to server.
     * @return true if successful
     */
	public boolean connect(ipaddr, port);

    /**
     * Bind this client to a CharFront object.
     */
	public bind (ChatFront chatFront);

    /** 
     * Star the clients' receiving message.
     *
     * This method should only be called after bind, and 
     */
	private boolean start ();
    /**
     * Stop the connection.
     *
     * After calling this method, all operation to the object is undefined
     */
	public boolean stop ();

    /**
     * Send message from this client to server.
     * @param cf the ChatFront object bounded to this client (only to make 
     *          sure that the called is or has the object.
     * @param message message to send
     *
     * This method should only be claaed by bound ChatFront object, cf should
     * be that object. Otherwise, an exception will be thrown.
     */
	public void send (ChatFront cf, String message);
}
