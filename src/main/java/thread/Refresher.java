package thread;

import app.ClientView;
import execution.CommandExecutioner;
import javafx.application.Platform;

public class Refresher implements Runnable {

	CommandExecutioner exec;
	ClientView cv;

	public Refresher(ClientView cv, CommandExecutioner exec) {
		this.cv = cv;
		this.exec = exec;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(3000);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						cv.update();
					}
				});

			} catch (Exception e) {
				System.out.println("thread failure");
			}
		}

	}

}
