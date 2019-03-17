package thread;

import execution.CommandExecutioner;

public class LoginThread implements Runnable {

	private CommandExecutioner exec;
	private String username;
	private String password;

	public LoginThread(String username, String password) {
		exec = new CommandExecutioner();
		this.username = username;
		this.password = password;
	}

	@Override
	public void run() {
		exec.login(username, password);
	}

}
