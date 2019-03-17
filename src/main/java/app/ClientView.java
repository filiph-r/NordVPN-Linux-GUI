package app;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import execution.CommandExecutioner;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.WindowEvent;
import thread.Connecter;
import thread.Disconnecer;
import thread.Refresher;

public class ClientView extends Form {

	CommandExecutioner exec = new CommandExecutioner();
	Label statusLb = new Label(exec.getStatus());
	Label connectFailed = new Label(
			"Whoops!\nWe can't connect you to this location.\nPlease try again.\nIf the problem persists,\ncontact NordVPN customer support.");
	Label plsDisconnect = new Label("\t\t\tPlease disconnect in order to change Settings");
	ComboBox<String> cb;
	ComboBox<String> cb1;

	TabPane TP;
	Tab status = new Tab("Status");
	Tab settings = new Tab("Settings");
	Connecter connecter = new Connecter(exec);
	Disconnecer disconnecter = new Disconnecer(exec);

	public Label lbWhitelist;
	public Label dnsLbl = new Label();

	Button editDNS = new Button("Edit DNS");
	Button editWhitelist = new Button("Edit Whitelist");

	ComboBox<String> protocol = new ComboBox<String>();
	ComboBox<String> killswitch = new ComboBox<String>();
	ComboBox<String> cyberSec = new ComboBox<String>();
	ComboBox<String> obfuscate = new ComboBox<String>();
	ComboBox<String> autoconnect = new ComboBox<String>();

	public ClientView(String title) {
		super(title);
		Image icon = new Image(getClass().getResourceAsStream("/img/icon.png"));
		this.getIcons().add(icon);

		restorePosition();

		TP = new TabPane(status, settings);
		TP.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		this.setResizable(false);
		connectFailed.setVisible(false);
		plsDisconnect.setTextFill(Color.web("#f45555"));
		if (statusLb.getText().contains("Connected")) {
			statusLb.setTextFill(Color.web("#05ba32"));
		} else {
			statusLb.setTextFill(Color.web("#f45555"));
		}

		TP.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
				if (t1 == settings) {
					exec.refreshSettigns();
					if (exec.settings.whitelistedPorts.equals("")) {
						lbWhitelist.setText("\tWhitelisted Ports:\n\n\t\tNone");
					} else {
						lbWhitelist.setText("\tWhitelisted Ports:\n" + exec.settings.whitelistedPorts);
					}

					if (exec.settings.dns.contains("disabled")) {
						dnsLbl.setText("Disabled");
					} else {
						dnsLbl.setText(exec.settings.dns);
					}

					if (exec.settings.cyberSec.contains("disabled")) {
						cyberSec.getSelectionModel().select(1);
					} else {
						cyberSec.getSelectionModel().select(0);
					}

					if (exec.getStatus().contains("Disconnected")) {
						plsDisconnect.setVisible(false);
						protocol.setDisable(false);
						killswitch.setDisable(false);
						cyberSec.setDisable(false);
						obfuscate.setDisable(false);
						autoconnect.setDisable(false);
						editDNS.setDisable(false);
						editWhitelist.setDisable(false);
					} else {
						plsDisconnect.setVisible(true);
						protocol.setDisable(true);
						killswitch.setDisable(true);
						cyberSec.setDisable(true);
						obfuscate.setDisable(true);
						autoconnect.setDisable(true);
						editDNS.setDisable(true);
						editWhitelist.setDisable(true);
					}
				}
			}
		});

		setStatus();
		setSettings();

		Thread ref = new Thread(new Refresher(this, exec));
		ref.setDaemon(true);
		ref.start();

		Pane p = new Pane();
		p.getChildren().add(TP);
		status.setStyle("-fx-background: White;");
		settings.setStyle("-fx-background: White;");

		this.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				try {
					CommandExecutioner.execute("mkdir NordVPN-gui_data");
					PrintWriter pw = new PrintWriter(new File("./NordVPN-gui_data/position"));
					pw.println(ClientView.this.getX() + ";" + ClientView.this.getY());
					pw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		setPane(p);
	}

	private void setStatus() {
		HBox mainPane = new HBox();
		mainPane.setMinHeight(400);
		mainPane.setMinWidth(600);

		plsDisconnect.setPadding(new Insets(15));

		GridPane gp = new GridPane();
		gp.setVgap(10);
		gp.setHgap(10);
		gp.setPadding(new Insets(50, 0, 20, 50));

		Label selectCountry = new Label("Select Country:");

		cb = new ComboBox<String>();
		List<String> countries = exec.getCountries();
		Collections.sort(countries);

		for (String s : countries) {
			cb.getItems().add(s);
		}
		cb.getSelectionModel().selectFirst();
		cb.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				cb1.getItems().clear();
				List<String> cities = exec.getCities(t1.replaceAll(" ", "_"));
				Collections.sort(cities);

				for (String s : cities) {
					cb1.getItems().add(s);
				}
				cb1.getSelectionModel().selectFirst();

			}
		});

		Label selectCity = new Label("Select City:");

		cb1 = new ComboBox<String>();
		List<String> cities = exec.getCities(countries.get(0));
		Collections.sort(cities);

		for (String s : cities) {
			cb1.getItems().add(s);
		}
		cb1.getSelectionModel().selectFirst();

		Button connect = new Button("Connect");
		connect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (cb1.getValue() == null) {
					connectFailed.setVisible(true);
				} else {
					connectFailed.setVisible(false);
					statusLb.setText("Status: Connecting");
					statusLb.setTextFill(Color.web("#f45555"));
					connecter.setValue(cb.getValue().replaceAll(" ", "_"), cb1.getValue().replaceAll(" ", "_"));
					Thread t1 = new Thread(connecter);
					t1.setDaemon(true);
					t1.start();
				}
			}
		});

		Button disconnect = new Button("Disconnect");
		disconnect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				statusLb.setText("Status: Disconnecting");
				statusLb.setTextFill(Color.web("#f45555"));
				Thread t2 = new Thread(disconnecter);
				t2.setDaemon(true);
				t2.start();
			}
		});

		// ------------------------------------------------------------------------
		VBox right = new VBox();
		try {
			Image logo = new Image(getClass().getResourceAsStream("/img/logo.png"));
			ImageView logoView = new ImageView(logo);
			right.getChildren().add(logoView);
			right.setPadding(new Insets(40, 0, 0, 50));
		} catch (Exception e) {
			e.printStackTrace();
		}
		right.getChildren().add(statusLb);
		statusLb.setPadding(new Insets(50, 0, 0, 0));
		statusLb.setFont(new Font(16));

		gp.add(selectCountry, 0, 0);
		gp.add(cb, 0, 1);
		gp.add(selectCity, 0, 2);
		gp.add(cb1, 0, 3);
		gp.add(connect, 0, 8);
		gp.add(disconnect, 0, 9);

		gp.add(connectFailed, 0, 10);

		right.setStyle("-fx-background-color: White;");
		gp.setStyle("-ffx-background-color: White;");
		mainPane.setStyle("-fx-background-color: White;");
		mainPane.getChildren().add(gp);
		mainPane.getChildren().add(right);
		status.setContent(mainPane);
	}

	private void setSettings() {
		exec.refreshSettigns();
		HBox mainPane = new HBox();
		mainPane.setMinHeight(400);
		mainPane.setMinWidth(600);

		GridPane gp = new GridPane();
		gp.setVgap(10);
		gp.setHgap(10);
		gp.setPadding(new Insets(20));

		protocol.getItems().add("UDP");
		protocol.getItems().add("TCP");
		if (exec.settings.protocol.contentEquals("TCP")) {
			protocol.getSelectionModel().select(1);
		} else {
			protocol.getSelectionModel().select(0);
		}
		protocol.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				exec.setProtocol(t1);
			}
		});

		killswitch.getItems().add("Enabled");
		killswitch.getItems().add("Disabled");
		if (exec.settings.killswitch.contains("enabled")) {
			killswitch.getSelectionModel().select(0);
		} else {
			killswitch.getSelectionModel().select(1);
		}
		killswitch.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				if (t1.contentEquals("Enabled")) {
					exec.setKillSwitch(true);
				} else {
					exec.setKillSwitch(false);
				}
			}
		});

		cyberSec.getItems().add("Enabled");
		cyberSec.getItems().add("Disabled");
		if (exec.settings.cyberSec.contains("enabled")) {
			cyberSec.getSelectionModel().select(0);
		} else {
			cyberSec.getSelectionModel().select(1);
		}
		cyberSec.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				if (t1.contentEquals("Enabled")) {
					exec.setCybersec(true);
					dnsLbl.setText("Disabled");
				} else {
					exec.setCybersec(false);
				}
			}
		});

		obfuscate.getItems().add("Enabled");
		obfuscate.getItems().add("Disabled");
		if (exec.settings.obfuscate.contains("enabled")) {
			obfuscate.getSelectionModel().select(0);
		} else {
			obfuscate.getSelectionModel().select(1);
		}
		obfuscate.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				if (t1.contentEquals("Enabled")) {
					exec.setObfuscate(true);
				} else {
					exec.setObfuscate(false);
				}
			}
		});

		autoconnect.getItems().add("Enabled");
		autoconnect.getItems().add("Disabled");
		if (exec.settings.autoConnect.contains("enabled")) {
			autoconnect.getSelectionModel().select(0);
		} else {
			autoconnect.getSelectionModel().select(1);
		}
		autoconnect.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				if (t1.contentEquals("Enabled")) {
					exec.setAutoconnect(true);
				} else {
					exec.setAutoconnect(false);
				}
			}
		});

		editDNS.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new EditDNS(ClientView.this);

			}

		});

		editWhitelist.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new EditWhitelist(ClientView.this);

			}

		});

		Label lb1 = new Label("Protocol:");
		Label lb2 = new Label("Kill Switch:");
		Label lb3 = new Label("CyberSec:");
		Label lb4 = new Label("Obfuscate:");
		Label lb5 = new Label("Auto connect:");
		Label lb6 = new Label("DNS:");

		VBox left = new VBox();
		left.getChildren().add(plsDisconnect);
		left.getChildren().add(gp);

		gp.add(lb1, 0, 0);
		gp.add(protocol, 1, 0);
		gp.add(lb2, 0, 1);
		gp.add(killswitch, 1, 1);
		gp.add(lb3, 0, 2);
		gp.add(cyberSec, 1, 2);
		gp.add(lb4, 0, 3);
		gp.add(obfuscate, 1, 3);
		gp.add(lb5, 0, 4);
		gp.add(autoconnect, 1, 4);
		gp.add(lb6, 0, 5);
		gp.add(dnsLbl, 1, 5);
		gp.add(editDNS, 0, 6);
		gp.add(editWhitelist, 1, 6);

		Button logout = new Button("Logout");
		logout.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				exec.logout();
				ClientView.this.close();
				new Login("NordVPN client");
			}
		});
		gp.add(logout, 0, 8);

		ScrollPane sc = new ScrollPane();
		sc.setMinWidth(180);
		sc.setMaxWidth(180);

		lbWhitelist = new Label();
		Pane right = new Pane();
		right.getChildren().add(lbWhitelist);
		gp.setPadding(new Insets(15, 200, 20, 150));

		gp.setStyle("-fx-background-color: White;");
		mainPane.setStyle("-fx-background-color: White;");
		left.setStyle("-fx-background-color: White;");

		sc.setContent(right);
		mainPane.getChildren().add(left);
		mainPane.getChildren().add(sc);
		settings.setContent(mainPane);
	}

	public void update() {
		if (status.isSelected() && !CommandExecutioner.isConnecting) {
			statusLb.setText(exec.getStatus());
			if (statusLb.getText().contains("Connected")) {
				statusLb.setTextFill(Color.web("#05ba32"));
			} else {
				statusLb.setTextFill(Color.web("#f45555"));
			}
		}
	}

	private void restorePosition() {
		try {
			Scanner sc = new Scanner(new File("./NordVPN-gui_data/position"));
			String[] pos = sc.next().split(";");
			double x = Double.parseDouble(pos[0]);
			double y = Double.parseDouble(pos[1]);

			this.setX(x);
			this.setY(y);

			sc.close();
		} catch (Exception e) {
		}
	}

}
