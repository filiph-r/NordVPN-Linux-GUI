package thread;

import app.Login;
import execution.CommandExecutioner;
import javafx.application.Platform;

public class LoginRefresher implements Runnable {

	CommandExecutioner exec;
	Login cv;
	String user, pw;

	public LoginRefresher(Login cv, CommandExecutioner exec, String user, String pw) {
		this.cv = cv;
		this.exec = exec;
		this.user = user;
		this.pw = pw;
	}

	@Override
	public void run() {

		Thread t = new Thread(new LoginThread(user, pw));
		t.setDaemon(true);
		t.start();

		long time = System.currentTimeMillis();
		while ((System.currentTimeMillis() - time) < 5000) {
			try {
				Thread.sleep(1000);
				if (CommandExecutioner.isLogedIn()) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							cv.update(false);
						}
					});

				} else {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							cv.update(true);
						}
					});
				}
			} catch (InterruptedException e) {
			}
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				cv.update(false);
			}
		});

	}

}
