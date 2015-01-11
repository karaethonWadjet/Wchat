package wchat;

import java.util.Scanner;

public class Wservreader implements Runnable {
	private Wserver parent;
	private Scanner input;
	private String name;
	private int id;
	private boolean online = true;
	private int time = 0;
	private boolean pres = false;

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
						pres = true;
						parent.broadcast(Wclient.afk + name);
					}
				}
			}
		}.start();

	}

	public boolean afk(){
		return pres;
	}
	public String name() {
		return name;
	}

	@Override
	public void run() {
		parent.broadcast(Wclient.connect + name);
		parent.broadcast("[" + name + " has connected]");
		while (input.hasNext()) {
			parent.broadcast(name + ": " + input.nextLine());
			pres = false;
			time = 0;
			parent.broadcast(Wclient.back + name);
		}
		online = false;
		parent.broadcast("[" + name + " has disconnected]");
		input.close();
		parent.broadcast(Wclient.disconnect + name);
		parent.disconnect(id, name);
	}

}
