package wchat;

import java.util.Scanner;

public class Wservreader implements Runnable {
	private Wserver parent;
	private Scanner input;
	private String name;
	private int id;

	public Wservreader(Wserver p, Scanner s, int i) {
		parent = p;
		input = s;
		name = s.nextLine();
		id = i;
	}

	@Override
	public void run() {
		parent.broadcast(name + " has connected.");
		while (input.hasNext()) {
			parent.broadcast(name + ": " + input.nextLine());
		}
		parent.broadcast(name + " has disconnected.");
		input.close();
		parent.disconnect(id);
	}

}
