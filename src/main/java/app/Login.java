package app;

import java.io.File;

import execution.CommandExecutioner;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import thread.LoginRefresher;

public class Login extends Form {

	Label loginStatus;

	public Login(String title) {
		super(title);
		try {
			new File("./NordVPN-gui_data/position").delete();
		} catch (Exception e) {
		}

		Image icon = new Image(getClass().getResourceAsStream("/img/icon.png"));
		this.getIcons().add(icon);

		CommandExecutioner exec = new CommandExecutioner();

		VBox mainPane = new VBox();
		Image logo = new Image(getClass().getResourceAsStream("/img/logo.png"));
		ImageView logoView = new ImageView(logo);
		HBox p = new HBox();
		p.getChildren().add(logoView);
		p.setPadding(new Insets(50, 0, 0, 80));

		mainPane.getChildren().add(p);

		GridPane pane = new GridPane();
		pane.setVgap(15);
		pane.setHgap(25);
		pane.setPadding(new Insets(50));
		Label lbl;
		lbl = new Label("Username/E-mail:");
		pane.add(lbl, 0, 0);
		lbl = new Label("Password:");
		pane.add(lbl, 0, 1);

		loginStatus = new Label("Login failed..");
		loginStatus.setTextFill(Color.web("#f45555"));
		loginStatus.setVisible(false);

		pane.add(loginStatus, 0, 2);

		TextField user = new TextField();
		pane.add(user, 1, 0);

		PasswordField pass = new PasswordField();
		pane.add(pass, 1, 1);

		Button btn = new Button("Log in");
		pane.add(btn, 1, 2);

		mainPane.getChildren().add(pane);
		setMyPane(mainPane);
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Login.this.getScene().setCursor(Cursor.WAIT);

				Thread t = new Thread(new LoginRefresher(Login.this, exec, user.getText(), pass.getText()));
				t.setDaemon(true);
				t.start();
			}
		});

		this.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					Login.this.getScene().setCursor(Cursor.WAIT);

					Thread t = new Thread(new LoginRefresher(Login.this, exec, user.getText(), pass.getText()));
					t.setDaemon(true);
					t.start();
				}
			}
		});

		this.show();
	}

	boolean isLoged = false;

	public void update(boolean isConnecting) {

		if (isLoged) {
			return;
		}

		if (isConnecting) {
			Login.this.getScene().setCursor(Cursor.WAIT);
		} else {
			Login.this.getScene().setCursor(Cursor.DEFAULT);
			if (CommandExecutioner.isLogedIn()) {
				isLoged = true;
				Login.this.close();
				new ClientView("NordVPN Client");
			} else {
				loginStatus.setVisible(true);
				try {
					File tempScript = new File("./NordVPN-gui_data/script");
					tempScript.delete();
				} catch (Exception e) {
				}
			}
		}
	}

}
