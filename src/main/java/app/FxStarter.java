package app;

import execution.CommandExecutioner;
import javafx.application.Application;
import javafx.stage.Stage;

public class FxStarter extends Application{
	
	@Override
	public void start(Stage primaryStage) {
		if (!CommandExecutioner.isLogedIn()) {
			new Login("NordVPN client");
		} else {
			new ClientView("NordVPN client");
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
