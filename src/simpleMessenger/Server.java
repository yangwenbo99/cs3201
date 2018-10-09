public class Server {
	Server (ChatFrontGenrator cg);
	ChatFrontGenrator generator;
	void start();
}
/*
 * Whenever a new connection established, this sever will genrate 
 * a new ChatBack object and ask the ChatFrontGenerator to generate 
 * a ChatFront object and then bind them together. 
 *
 * When a ChatBack receive message from the remote side, it will 
 * send the message to its ChatFront object right away.
 */
