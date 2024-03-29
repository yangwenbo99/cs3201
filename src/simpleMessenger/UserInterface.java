package simpleMessenger;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.DefaultCaret;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;

public class UserInterface implements ChatFront
{
	private ChatBack chatBack;
	private boolean is_bound;
	private String identity;
	private static int id = 1;
	
	private JFrame frame;
	private JPanel inputPanel;
	private JButton button;
	private JScrollPane scrollPane_output;
	private JScrollPane scrollPane_input;
	private JTextArea outputField;
	private JTextArea editor;
	private DefaultCaret caret_output;
	private DefaultCaret caret_input;
	
	/**
	 * Create the application.
	 */
	public UserInterface(String id) {
		this.identity = id;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		chatBack = null;
		
		frame = new JFrame();
		if (this.identity.equals("Server")) {
			frame.setTitle("Simple Messenger - " + "Server");
		}
		else {
			frame.setTitle("Simple Menssenger - " + "Client " + id);
		}
		frame.setBounds(100, 100, 1000, 650);	
		if (this.identity.equals("Client")) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		}
		frame.setMinimumSize(new Dimension(700,475));
	
		outputField = new JTextArea(20,20);
		outputField.setToolTipText("");
		outputField.setLineWrap(true);
		outputField.setFont(new Font("Consolas", Font.PLAIN, 17));
		outputField.setEditable(false);
		outputField.setBackground(Color.LIGHT_GRAY);
		if (this.identity.equals("Server")){
			outputField.append("Instructions:\n"
					+ "Press <ENTER> to break lines.\nPress "
					+ "<CRTL+ENTER> to send messages.\n"
					+ "Enter END to terminate the program.\n\nServer\n");
		}
		else {
			outputField.append("Instructions:\nPress <ENTER> to break lines.\n"
					+ "Press <CRTL+ENTER> to send messages.\n"
					+ "Enter END to terminate the program.\n\nClient " + id+"");
			id++;
		}
		outputField.append("\n\n\n\n");
		
		editor = new JTextArea(4,20);
		editor.setToolTipText("Press <ENTER> to break lines. Press <CTRL+ENTER> to send messages.");
		editor.setLineWrap(true);
		editor.setFont(new Font("Consolas", Font.PLAIN, 15));
		editor.addAncestorListener(new RequestFocusListener());
		editor.addKeyListener(new multiKeyPressListener());
		
		scrollPane_output = new JScrollPane(outputField);
		scrollPane_output.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//frame.getContentPane().add(scrollPane_output,BorderLayout.CENTER);
		
		scrollPane_input = new JScrollPane(editor);
		scrollPane_input.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		caret_output = new DefaultCaret();
		caret_output = (DefaultCaret)outputField.getCaret();
		caret_output.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);		
		
		caret_input = new DefaultCaret();
		caret_input = (DefaultCaret)editor.getCaret();
		caret_input.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
				
		button = new JButton("Send");
		button.addActionListener(new inputAction());		
		
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(scrollPane_output,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
								.addComponent(scrollPane_input,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
								.addComponent(button,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE))));
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(scrollPane_output,0,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(scrollPane_input,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
						.addComponent(button)));
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public void bindTo(ChatBack b) {
		if (this.chatBack == null)
			this.chatBack = b;
		else {
			throw new IllegalArgumentException(
					"Do not bind to more than one ChatBacks.");			
		}
	}

	public void send(ChatBack cb, String m) {
		if (cb != this.chatBack)
			throw new IllegalArgumentException("You must send messages to the ChatBack you binded!");
		else {
			pushToOutput(m);
		}
	}
	
	public void informBound(ChatBack cb) {
		if (cb == this.chatBack) {
			is_bound = true;
		} else { 
			throw new IllegalArgumentException(
                    "Only corresponding back should call this method");		
		}
	}
	
	public void end() {
		chatBack.stop();
		System.exit(0);
	}
	
	private class RequestFocusListener implements AncestorListener
	{
		private boolean removeListener;

		/*
		 *  Convenience constructor. The listener is only used once and then it is
		 *  removed from the component.
		 */
		private RequestFocusListener()
		{
			this(true);
		}

		/*
		 *  Constructor that controls whether this listen can be used once or
		 *  multiple times.
		 *
		 *  @param removeListener when true this listener is only invoked once
		 *                        otherwise it can be invoked multiple times.
		 */
		private RequestFocusListener(boolean removeListener)
		{
			this.removeListener = removeListener;
		}

		@Override
		public void ancestorAdded(AncestorEvent e)
		{
			JComponent component = e.getComponent();
			component.requestFocusInWindow();

			if (removeListener)
				component.removeAncestorListener( this );
		}

		@Override
		public void ancestorMoved(AncestorEvent e) {}

		@Override
		public void ancestorRemoved(AncestorEvent e) {}
	}
	
	private class inputAction implements ActionListener {	
		
		public void actionPerformed (ActionEvent event) {
			pushToOutput();
		}
	}
	
	
	private class multiKeyPressListener implements KeyListener {
	    @Override
	    public synchronized void keyPressed(KeyEvent e) {
	    	if (e.isControlDown()&&e.getKeyCode() == KeyEvent.VK_ENTER) {
	    		pushToOutput();
	    	}
	    }

	    @Override
	    public synchronized void keyReleased(KeyEvent e) {/* Not used */ }

	    @Override
	    public void keyTyped(KeyEvent e) {/* Not used */ }
	}
	
	private void pushToOutput() {
		String input;
		input = editor.getText();
		if (input.equals("")) {
			outputField.append("The input should not be empty!\n");
			outputField.setCaretPosition(outputField.getDocument().getLength());
		}
		else {
			boolean is_end_contained = false;
			Scanner sin = new Scanner(input);
			while(sin.hasNext()) {
				if(sin.nextLine().trim().equals("END"))
					is_end_contained = true;
			}
			if (is_end_contained) {
				outputField.append("Any single line in input should not be exactly as 'END'!\n");
			}
			else {
				chatBack.send(this, input + "\n");
			}
		}
		editor.setText("");
		editor.requestFocus();
	}
	
	private void pushToOutput(String s) {
		outputField.append(s);
		outputField.setCaretPosition(outputField.getDocument().getLength());
	}
}
