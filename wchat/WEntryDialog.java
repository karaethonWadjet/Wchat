package wchat;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class WEntryDialog extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Wclient parent;
	private JTextField name = new JTextField(10);
	//private JTextField port = new JTextField(10);
	private JTextField ip = new JTextField(10);

	public WEntryDialog(Wclient par) {
		super("The login window");
		parent = par;
		JLabel ins = new JLabel("Enter your desired username and the server's IP address");
		JLabel n = new JLabel("Name");
		//JLabel p = new JLabel("Port");
		JButton paunch = new JButton("GO!");
		paunch.addActionListener(this);
		JPanel on = new JPanel();
		JPanel bon = new JPanel();
		JPanel con = new JPanel();
		//JPanel don = new JPanel();
		//on.setLayout(new BoxLayout(on, BoxLayout.Y_AXIS));
		on.add(ins);
		bon.add(n);
		bon.add(name);
		on.add(bon);
		JLabel i = new JLabel("IP");
		con.add(i);
		con.add(ip);
		
		on.add(con);
		//don.add(p);
		//don.add(port);
		//on.add(don);
		on.add(paunch);
		Action sendo = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.launch(name.getText(), ip.getText());
			}
		};
		ip.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "send");
		ip.getActionMap().put("send", sendo);
		add(on);

		setBounds(500, 300, 400, 240);
		//pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void clear(){
		ip.setText("");
	}
	
	@Override
	public void actionPerformed(ActionEvent a) {
			parent.launch(name.getText(), ip.getText());
	}

}
