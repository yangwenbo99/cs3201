package simpleMessenger;

import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

class ChatFrontTest implements ChatFront {
    private ChatBack    back = null;
    private Scanner     in;
    private PrintWriter out;
    private Thread      watingThread = null;
    private boolean     isBounded = false;

    /**
     * Constructor.....
     * @param outFilename regarded as output to user
     *
     * This method regard System.in as input....
     *
     * Note: if instance presentation to user is needed, make sure out 
     * can automitially flush itself....
     */
    ChatFrontTest (String outFilename) throws FileNotFoundException {
        this.in  = new Scanner(System.in);
        this.out = new PrintWriter(
                new File(outFilename));
    }

    synchronized private void closeResources() {
        // synchronized, otherwise it may be called when writing to the 
        // user
        //
        // this method can be executed more than once, since closing a
        // closed Scanner or PrintWriter does not matter.
        this.out.println("Stopped");
        this.in.close();
        this.out.close();
    }

	@Override
	public void bindTo(ChatBack back) {
        if (this.back == null) {
            this.back = back;
        } else {
            throw new IllegalArgumentException(
                    "Do not call bindTo() more than once!");
        }
	}

    private void startWaitingUserInput() {
        Runnable watingForUser = () -> {
            StringBuilder sb = new StringBuilder();
            synchronized (ChatFrontTest.this) {
                try {
                    while (true) {
                        String line = in.nextLine();
                        if (line.equals("END")) {
                            back.send(this, sb.toString());
                            System.out.println("-----------");
                            out.println("You said:");
                            out.print(sb.toString());
                            out.println("---------");
                            out.flush();
                            sb.delete(0, sb.length());
                        } else if (line.equals("STOP")) {
                            back.stop();
                            closeResources();
                            break;
                        } else {
                            sb.append(line);
                            sb.append('\n');
                        }
                        Thread.sleep(20);
                    }
                } catch (InterruptedException ie) {
                    closeResources();
                }
            }
        };

        watingThread = new Thread(watingForUser);
        watingThread.start();
    }
	@Override
	public void informBound(ChatBack cb) {
        if (cb == this.back) {
            isBounded = true;
            startWaitingUserInput();
        } else {
            throw new IllegalCallerException(
                    "Only corresponding back should call this method");
        }
	}

	@Override
	public void send(ChatBack cb, String m) {
        // to do throw exception...
        out.println("The other end said:");
        out.print(m);
        out.flush();
	}

	@Override
	public void end() {
        watingThread.interrupt();
        closeResources();
	}

}
