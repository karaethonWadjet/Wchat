package wchat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Wserver implements Runnable {
	private HashMap<Integer, PrintWriter> po = new HashMap<Integer, PrintWriter>();
	private ServerSocket lisna;
	private int count = 0;

	public Wserver() {
		final int port = 6112;
		try {
			lisna = new ServerSocket(port);
			new Thread(this).start();
			while (true) {
				Socket s = lisna.accept();
				System.out.println("Received connection from "
						+ s.getInetAddress());
				po.put(count, new PrintWriter(s.getOutputStream(), true));
				new Thread(new Wservreader(this,
						new Scanner(s.getInputStream()), count)).start();
				count++;
			}
		} catch (IOException e) {
		}

	}

	public void broadcast(String s) {
		for (PrintWriter p : po.values()){
			p.println(s);
		}
	}

	public void disconnect(int id) {
		po.get(id).close();
		po.remove(id);
		System.out.println("Some guy dc'd");
	}

	public static void main(String[] args) {
		new Wserver();
	}

	@Override
	public void run() {
		while (JOptionPane.YES_OPTION != JOptionPane.showOptionDialog(null,
				"Close the server?", "Server running...",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				new Object[] { "Yes", "No" }, null)) {
			System.out.println("running!");
		}
		System.exit(0);
	}

}
