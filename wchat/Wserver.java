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
	private HashMap<Integer, Wservreader> readers = new HashMap<Integer, Wservreader>();
	private ServerSocket lisna;
	private int count = 0;
	private final String connect = "/5%3&";

	public Wserver() {
		final int port = 6112;
		try {
			lisna = new ServerSocket(port);
			new Thread(this).start();
			while (true) {
				Socket s = lisna.accept();
				po.put(count, new PrintWriter(s.getOutputStream(), true));
				for (Wservreader w : readers.values()){
					po.get(count).println(connect + w.name());
				}
				readers.put(count,
						new Wservreader(this, new Scanner(s.getInputStream()),
								count));
				new Thread(readers.get(count)).start();
				System.out.println(readers.get(count).name() + " connected from "
						+ s.getInetAddress());
				count++;
			
			}
		} catch (IOException e) {
		}

	}

	public void broadcast(String s) {
		for (PrintWriter p : po.values()) {
			p.println(s);
		}
	}

	public void disconnect(int id, String name) {
		po.get(id).close();
		po.remove(id);
		readers.remove(id);
		System.out.println(name + " disconnected");
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
