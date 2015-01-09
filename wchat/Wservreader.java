package wchat;

import java.util.Scanner;

public class Wservreader implements Runnable {
	private Wserver parent;
	private Scanner input;
	private String name;
	private int id;
	private final String connect = "/5%3&";
	private final String disconnect = "^*";
	private final String afk = "&#$";
	private final String back = "$#&";
	private boolean online = true;
	private int time = 0;

	public Wservreader(Wserver p, Scanner s, int i) {
		parent = p;
		input = s;
		name = s.nextLine();
		id = i;

		new Thread() {
			public void run() {
				while (online) {
					try {
						Thread.sleep(60000);
						time++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (time > 5) {
						parent.broadcast(afk + name);
					}
				}
			}
		}.start();

	}

	public String name() {
		return name;
	}

	@Override
	public void run() {
		parent.broadcast(connect + name);
		parent.broadcast(name + " has connected.");
		while (input.hasNext()) {
			parent.broadcast(name + ": " + input.nextLine());
			time = 0;
			parent.broadcast(back + name);
		}
		online = false;
		parent.broadcast(name + " has disconnected.");
		input.close();
		parent.broadcast(disconnect + name);
		parent.disconnect(id, name);
	}

}
