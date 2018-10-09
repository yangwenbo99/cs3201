package simpleMessenger;

public interface ChatBack {
    // private ChatFont front;

	// return true iff sucessfully bind and start the 
	// return false iff not bind and fail to start
	public boolean bind (ChatFront chatFront);
	public boolean stop ();

	// send newly received message to the chat end.
	// cf is just to ensure that the caller has a CharFront object 
	// and this CHatFront object is in the chatFronts list.
	void send (ChatFront chatFront, String m);
}
/*
 * At the end of binding, the back end should use informBounded 
 * method of ChatFront object to inform that object has been
 * sucessfully bound to this object, so that message fron the 
 * front end can be sent.
 *
 * after binding, it should keep waiting a message fron remote 
 * side, and if it is
 * arriveed, it directly send the message to all the chatfronts, and
 * once one chatFront send the message to the ChatBack object, it 
 * will send them in queue.
 *
 * ChatBack may have different front in its life.
 */
