package wchat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class Wclient extends JFrame implements ActionListener, Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// these string prefixes denote a user list update command
	private final String connect = "/5%3&";
	private final String disconnect = "^*";
	private final String afk = "&#$";
	private final String back = "$#&";
	private JTextField entry = new JTextField(40);
	private JTextArea messages = new JTextArea(20, 30);
	private JTextArea users = new JTextArea(2, 10);
	private Scanner in;
	private PrintWriter out;
	private ArrayList<String> names = new ArrayList<String>();
	private String name;

	public Wclient() {
		new WEntryDialog(this);
	}

	public void launch(String n, String i) {
		name = n;
		// Establish connection to server
		final int port = 6112;
		try {
			@SuppressWarnings("resource")
			Socket jeeves = new Socket(i, port);
			in = new Scanner(jeeves.getInputStream());
			out = new PrintWriter(jeeves.getOutputStream(), true);
			out.println(n);
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(this, "NO server was found :O");
			System.exit(0);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "IO Exception thrown :(");
			System.exit(0);
		}

		// Set up the window
		Action sendo = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (entry.getText() != "") {
					out.println(entry.getText());
					entry.setText("");
				}
			}
		};
		entry.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "send");
		entry.getActionMap().put("send", sendo);
		messages.setEditable(false);
		messages.setText("");
		users.setEditable(false);
		JPanel log = new JPanel();
		JPanel chat = new JPanel();
		JButton sen = new JButton("Sento!");
		sen.addActionListener(this);
		chat.add(entry);
		chat.add(sen);
		log.setLayout(new BoxLayout(log, BoxLayout.Y_AXIS));
		log.add(new JScrollPane(messages));
		log.add(chat);
		log.add(users);
		add(log);

		setTitle("Dubya Chat Pro! - Starring: " + name);
		setBounds(400, 200, 640, 480);
		// pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		new Thread(this).start();
	}

	public void play(String s) {
		try {
			AudioInputStream as = AudioSystem.getAudioInputStream(new File(s));
			DataLine.Info di = new DataLine.Info(Clip.class, as.getFormat());
			Clip c = (Clip) AudioSystem.getLine(di);
			c.open(as);
			c.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Wclient();
	}

	@Override
	public void run() {
		while (in.hasNext()) {
			String t = in.nextLine();
			if (!t.contains(":")) {
				// it's a server command
				if (t.contains(connect)) {
					names.add(t.substring(connect.length()));
					users.setText("Currently on the server: "
							+ names.toString());
					play("login.wav");
				} else if (t.contains(disconnect)) {
					names.remove(t.substring(disconnect.length()));
					users.setText("Currently on the server: "
							+ names.toString());
				} else if (t.contains(afk)
						&& names.contains(t.substring(afk.length()))) {
					names.set(names.indexOf(t.substring(afk.length())),
							t.substring(afk.length()) + "(AFK)");
					users.setText("Currently on the server: "
							+ names.toString());
				} else if (t.contains(back)
						&& names.contains(t.substring(back.length()) + "(AFK)")) {
					names.set(
							names.indexOf(t.substring(back.length()) + "(AFK)"),
							t.substring(back.length()));
					users.setText("Currently on the server: "
							+ names.toString());
				} else if (t.contains(".")) {
					if (messages.getText().length() > 10) {
						messages.append("\n");
						// System.out.println("The length is:" +
						// messages.getText().length());
					}
					messages.append(t);
				}
			} else {
				// it's a message, print it out
				messages.append("\n" + t);
				if (!t.contains(name + ":")) {
					play("note.wav");
				}
			}
			messages.setCaretPosition(messages.getDocument().getLength());
		}
		messages.append("\n" + "Server is kill :(");
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		if (entry.getText() != "") {
			out.println(entry.getText());
			entry.setText("");
		}
	}

}
