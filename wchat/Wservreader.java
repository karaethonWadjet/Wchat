package wchat;

import java.util.Scanner;

public class Wservreader implements Runnable {
	private Wserver parent;
	private Scanner input;
	private String name;
	private int id;
	private final String connect = "/5%3&";
	private final String disconnect = "^*";

	public Wservreader(Wserver p, Scanner s, int i) {
		parent = p;
		input = s;
		name = s.nextLine();
		id = i;
	}
	
	public String name(){
		return name;
	}

	@Override
	public void run() {
		parent.broadcast(connect + name);
		parent.broadcast(name + " has connected.");
		while (input.hasNext()) {
			parent.broadcast(name + ": " + input.nextLine());
		}
		parent.broadcast(name + " has disconnected.");
		input.close();
		parent.broadcast(disconnect + name);
		parent.disconnect(id, name);
	}

}
