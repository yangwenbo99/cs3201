public class Client implements ChatBack {
	// return true iff sucesssful
	public boolean connect(ipaddr, port);
	private boolean start ();
	public boolean stop ();

	public void send (String m);
	public bind (ChatFront chatFront);
}
