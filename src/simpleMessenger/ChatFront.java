package simpleMessenger;

public interface ChatFront {
	// private CheckBack cb;
	// giving the back end to the front end
	// this backend is for receiving massage

    /**
     * This method should only be called once. 
     * If it is called more than once, an exception should be thrown.
     * (For example, IllegalArgumentException)
     */
    public void bindTo(ChatBack cb);

	// send newly received message to this chat front
	public void  send(ChatBack cb, String m);

	// to inform the front end that it is successfully bound 
	// to the back end, and it is only after this method called 
	// by the backend that it can send message to the backend.
	public void informBound(ChatBack cb);

	// to inform this end that thge connection is ewnd3ed
	void end();
}
/*
 * it can only serve one back end in its life
 *
 * When generating a Front - Back relationship, the procedure must be 
 * followed:
 *     1. construct a back end and a front end, 
 *     2. front.bindTo(back)
 *     3. back.bind(front)
 *     4. back
 *
 *     in back.bind(), front.informBounded() must be called.
 */

