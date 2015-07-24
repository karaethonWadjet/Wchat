package wchat;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Wclient extends JFrame implements ActionListener, Runnable {
	/* these string prefixes denote server commands to update the user list */
	static final String connect = "{5%3&", // new user connect
			disconnect = "6^*T", // user disconnects
			afk = "&#$", back = "$#&", initcon = "(5%3&"; // initial user list
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField entry = new JTextField(40);
	private JTextArea users = new JTextArea(21, 10);
	private JTextPane crese = new JTextPane();
	private Scanner in;
	private PrintWriter out;
	private ArrayList<String> names = new ArrayList<String>();
	private String name;
	private WEntryDialog pop;
	private Thread inbound;
	private boolean connected = false, muted = false;
	private JMenuItem rec = new JMenuItem("Reconnect");
	private AbstractDocument mesdoc;
	private SimpleAttributeSet univ = new SimpleAttributeSet();

	public Wclient() {
		setVisible(false);
		pop = new WEntryDialog(this);
	}
	//unused constructor for launching with a predetermined IP and username
	public Wclient(String n, String i) {
		pop = new WEntryDialog(this);
		pop.setVisible(false);
		launch(n, i);
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
			return;
		} catch (IOException e) {
			JOptionPane
			.showMessageDialog(
					this,
					"Either server is kill or you have the wrong address.\nContact the server admin to make sure.",
					"Can't find tha server!",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		connected = true;
		// Close entry dialog and disable reconnect button
		rec.setEnabled(false);
		pop.setVisible(false);

		if (!isVisible()) {
			// Keybinding and other text area prep
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
			crese.setEditable(false);
			mesdoc = (AbstractDocument) crese.getStyledDocument();
			users.setEditable(false);
			TitledBorder t = BorderFactory.createTitledBorder(
					BorderFactory.createLineBorder(Color.RED),
					"Currently Online");
			t.setTitleJustification(TitledBorder.CENTER);
			StyleConstants.setForeground(univ, Color.BLACK);

			// Commence building window
			JMenuBar hey = new JMenuBar();
			JMenu one = new JMenu("Net"), two = new JMenu("Options"), twee = new JMenu("Help");
			rec.setActionCommand("rc");
			rec.addActionListener(this);
			JMenuItem dis = new JMenuItem("Disconnect"), mute = new JMenuItem(
					"Mute/Unmute"), halp = new JMenuItem("HALP"), about = new JMenuItem("About");
			dis.setActionCommand("dc");
			dis.addActionListener(this);
			mute.setActionCommand("mute");
			mute.addActionListener(this);
			halp.setActionCommand("halp");
			halp.addActionListener(this);
			about.setActionCommand("about");
			about.addActionListener(this);
			one.add(rec);
			one.add(dis);
			two.add(mute);
			twee.add(halp);
			twee.add(about);
			hey.add(one);
			hey.add(two);
			hey.add(twee);
			setJMenuBar(hey);

			JButton sen = new JButton("Sento!");
			sen.addActionListener(this);

			JPanel whole = new JPanel(), chat = new JPanel(), areas = new JPanel(), userlist = new JPanel();
			chat.add(entry);
			chat.add(sen);

			userlist.setLayout(new BoxLayout(userlist, BoxLayout.Y_AXIS));
			userlist.add(users);
			userlist.setBorder(t);

			areas.setLayout(new BoxLayout(areas, BoxLayout.X_AXIS));
			areas.add(Box.createHorizontalStrut(5));
			JScrollPane jaefw = new JScrollPane(crese);
			jaefw.setPreferredSize(new Dimension(450, 360));
			areas.add(jaefw);
			areas.add(Box.createHorizontalStrut(5));
			// areas.add(new JSeparator(JSeparator.VERTICAL));
			// areas.add(Box.createHorizontalStrut(5));
			areas.add(userlist);
			areas.add(Box.createHorizontalStrut(5));

			whole.setLayout(new BoxLayout(whole, BoxLayout.Y_AXIS));
			whole.add(Box.createVerticalStrut(10));
			whole.add(areas);
			whole.add(chat);
			add(whole);

			setBounds(400, 200, 640, 480);
			pack();
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
		}
		setTitle("Dubya Chat Pro! - Starring: " + name);
		inbound = new Thread(this);
		inbound.start();
	}

	// plays a sound file denoted by s
	private void play(String s) {
		if (!muted) {
			try {
				AudioInputStream as = AudioSystem.getAudioInputStream(new File(
						s));
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
	}

	// updates the list of currently online people
	private void updatelist() {
		users.setText("");
		for (String b : names) {
			users.append(b + "\n");
		}
	}

	private void print(String s) {
		try {
			if (s.contains(":") && !s.contains("[")) {
				int it = 0;
				StyleConstants.setBold(univ, true);
				while (s.charAt(it) != ':') {
					mesdoc.insertString(mesdoc.getLength(), s.charAt(it) + "",
							univ);
					it++;
				}
				StyleConstants.setBold(univ, false);
				mesdoc.insertString(mesdoc.getLength(), s.substring(it), univ);
			} else {
				mesdoc.insertString(mesdoc.getLength(), s, univ);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Wclient();
	}

	@Override
	public void run() {
		play("login.wav");
		while (in.hasNext()) {
			String t = in.nextLine();
			if (!t.contains(":") && !t.contains("[")) { // it's a server command
				if (t.contains(initcon)) {
					names.add(t.substring(initcon.length()));
				} else if (t.contains(connect)) {
					names.add(t.substring(connect.length()));
					if (!t.substring(connect.length()).equals(name)) {
						play("connect.wav");
					}
				} else if (t.contains(disconnect)) {
					play("logout.wav");
					names.remove(t.substring(disconnect.length()));
					names.remove(t.substring(disconnect.length()) + "(AFK)");

				} else if (t.contains(afk)
						&& names.contains(t.substring(afk.length()))) {
					names.set(names.indexOf(t.substring(afk.length())),
							t.substring(afk.length()) + "(AFK)");
				} else if (t.contains(back)
						&& names.contains(t.substring(back.length()) + "(AFK)")) {
					names.set(
							names.indexOf(t.substring(back.length()) + "(AFK)"),
							t.substring(back.length()));
				}
				updatelist();
			} else { // it's a message, print it out
				if (mesdoc.getLength() > 10) {
					print("\n");
				}
				print(t);
				if (!t.contains("[") && !t.contains(name + ":")
						&& !entry.isFocusOwner()) {
					play("note.wav");
				}
			}
			// scroll to bottom
			crese.setCaretPosition(mesdoc.getLength());
		}
		if (connected) {
			print("\n" + "[Server is kill :(]");
			names.clear();
			updatelist();
			connected = false;
			rec.setEnabled(true);
		} else {
			print("\n" + "[You are now disconnected]");
		}
		crese.setCaretPosition(mesdoc.getLength());
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		switch (a.getActionCommand()) {
		case "dc":
			if (connected) {
				connected = false;
				out.close();
				in.close();
				names.clear();
				updatelist();
				rec.setEnabled(true);
			}
			break;
		case "rc":
			pop.setVisible(true);
			break;
		case "mute":
			muted = !muted;
			JOptionPane.showMessageDialog(this, "Sounds are now O"
					+ (muted ? "FF" : "N"));
			break;
		case "halp":
			JOptionPane.showMessageDialog(this, "If you don't know how to use a chat client, please get off the internet kthx ;)");
			break;
		case "about":
			JOptionPane.showMessageDialog(this, "W Chat Client, Version 0.8 \n By Wayne Kueh");
			break;
		default: // from the sent button
			if (entry.getText() != "") {
				out.println(entry.getText());
				entry.setText("");
			}
		}
	}
}
