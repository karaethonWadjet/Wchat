package wchat;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class WEntryDialog extends JFrame implements ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Wclient parent;
	private JTextField name = new JTextField(10);
	// private JTextField port = new JTextField(10);
	private JTextField ip = new JTextField(10);
	private final String PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$",
			path = "nameiplog.txt";
	private boolean sching = false;
	private JCheckBox save = new JCheckBox("Save info for next time");

	public WEntryDialog(Wclient par) {
		super("The login window");
		parent = par;
		Action sendo = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (name.getText().equals("")) {
					JOptionPane.showMessageDialog(null,
							"you didn't enter a name, dummy", "WHO ARE YOU?",
							JOptionPane.ERROR_MESSAGE);
				} else if (!validate(ip.getText())) {
					JOptionPane.showMessageDialog(null,
							"You didn't type a valid IPv4 address",
							"WHERE IS IT?", JOptionPane.ERROR_MESSAGE);
				} else {
					// save login info for next open
					if (sching) {
						try {
							PrintWriter macon = new PrintWriter(new File(path));
							macon.println(name.getText());
							macon.println(ip.getText());
							macon.close();
						} catch (FileNotFoundException e) {
							JOptionPane.showMessageDialog(null, "uh oh");
						}
					}
					parent.launch(name.getText(), ip.getText());
				}
			}
		};
		JLabel ins = new JLabel(
				"Enter your desired username and the server's IP address");
		JLabel n = new JLabel("Name");
		// JLabel p = new JLabel("Port");
		JButton paunch = new JButton("GO!");
		paunch.addActionListener(sendo);
		JPanel on = new JPanel();
		JPanel bon = new JPanel();
		JPanel con = new JPanel();
		JPanel don = new JPanel();
		// on.setLayout(new BoxLayout(on, BoxLayout.Y_AXIS));
		on.add(ins);
		bon.add(n);
		bon.add(name);
		on.add(bon);
		JLabel i = new JLabel("IP");
		con.add(i);
		con.add(ip);
		on.add(con);
		// don.add(p);
		// don.add(port);
		save.addItemListener(this);
		save.setSelected(true);
		don.add(save);
		on.add(don);
		on.add(paunch);

		ip.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "send");
		ip.getActionMap().put("send", sendo);
		
		paunch.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "send");
		paunch.getActionMap().put("send", sendo);
		
		name.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "send");
		name.getActionMap().put("send", sendo);
		add(on);

		// check if login information was saved
		Scanner hoi;
		try {
			hoi = new Scanner(new File(path));
			if (hoi.hasNext()) {
				name.setText(hoi.nextLine());
				ip.setText(hoi.nextLine());
			}
			hoi.close();
		} catch (FileNotFoundException e) {
		}
		setBounds(500, 300, 400, 240);
		// pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void clear() {
		ip.setText("");
	}

	public boolean validate(final String ip) {
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		sching = !sching;
	}
}
