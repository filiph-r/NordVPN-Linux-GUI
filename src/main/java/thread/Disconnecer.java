package thread;

import execution.CommandExecutioner;

public class Disconnecer implements Runnable {

	CommandExecutioner exec;

	public Disconnecer(CommandExecutioner exec) {
		this.exec = exec;
	}

	@Override
	public void run() {
		exec.disconnect();
	}

}
