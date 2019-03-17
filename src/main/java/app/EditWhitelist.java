package app;

import execution.CommandExecutioner;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class EditWhitelist extends Form {

	public EditWhitelist(ClientView owner) {
		super("Edit DNS");

		CommandExecutioner exec = new CommandExecutioner();

		this.setWidth(300);
		this.setHeight(200);
		this.setX(owner.getX() + owner.getWidth() / 2 - this.getWidth() / 2);
		this.setY(owner.getY() + owner.getHeight() / 2 - this.getHeight() / 2);
		this.setResizable(false);

		initModality(Modality.APPLICATION_MODAL);
		initStyle(StageStyle.DECORATED);
		initOwner(owner);

		GridPane p = new GridPane();

		Label l1 = new Label("Port:");
		Label l2 = new Label("Protocol:");

		TextField port = new TextField();
		port.setMaxWidth(80);
		ComboBox<String> protocol = new ComboBox<String>();
		protocol.getItems().add("BOTH");
		protocol.getItems().add("TCP");
		protocol.getItems().add("UDP");
		protocol.getSelectionModel().selectFirst();

		p.add(l1, 0, 0);
		p.add(port, 1, 0);
		p.add(l2, 0, 1);
		p.add(protocol, 1, 1);

		GridPane gp = new GridPane();
		gp.setHgap(20);
		gp.setPadding(new Insets(0, 0, 0, 8));

		Button add = new Button("Add");
		Button remove = new Button("Remove");

		add.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					String resp = "";
					if (port.getText().equals("")) {
						EditWhitelist.this.close();
					} else {
						if (protocol.getSelectionModel().getSelectedItem().contentEquals("BOTH")) {
							resp = exec.whitelistAdd(port.getText());
						} else {
							resp = exec.whitelistAddProtocol(port.getText(),
									protocol.getSelectionModel().getSelectedItem().toString());
						}
					}

					if (resp.contains("successfully")) {
						exec.refreshSettigns();
						if (exec.settings.whitelistedPorts.equals("")) {
							owner.lbWhitelist.setText("\tWhitelisted Ports:\n\n\t\tNone");
						} else {
							owner.lbWhitelist.setText("\tWhitelisted Ports:\n" + exec.settings.whitelistedPorts);
						}
						EditWhitelist.this.close();
					} else {
						Alert a = new Alert(AlertType.ERROR);
						a.setTitle("DNS Error");
						a.setResizable(false);
						a.setContentText("The port you entered is not valid\n" + resp);
						a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
						a.showAndWait();
					}

				} catch (Exception e) {
					Alert a = new Alert(AlertType.ERROR);
					a.setTitle("Error");
					a.setResizable(false);
					a.setContentText("Error");
					a.showAndWait();
				}
			}
		});

		remove.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					String resp = "";
					if (port.getText().equals("")) {
						EditWhitelist.this.close();
					} else {
						if (protocol.getSelectionModel().getSelectedItem().contentEquals("BOTH")) {
							resp = exec.whitelistRemove(port.getText());
						} else {
							resp = exec.whitelistRemoveProtocol(port.getText(),
									protocol.getSelectionModel().getSelectedItem().toString());
						}
					}

					if (resp.contains("successfully")) {
						exec.refreshSettigns();
						if (exec.settings.whitelistedPorts.equals("")) {
							owner.lbWhitelist.setText("\tWhitelisted Ports:\n\n\t\tNone");
						} else {
							owner.lbWhitelist.setText("\tWhitelisted Ports:\n" + exec.settings.whitelistedPorts);
						}
						EditWhitelist.this.close();
					} else {
						Alert a = new Alert(AlertType.ERROR);
						a.setTitle("DNS Error");
						a.setResizable(false);
						a.setContentText("The port you entered is not valid\n" + resp);
						a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
						a.showAndWait();
					}

				} catch (Exception e) {
					Alert a = new Alert(AlertType.ERROR);
					a.setTitle("Error");
					a.setResizable(false);
					a.setContentText("Error");
					a.showAndWait();
				}
			}
		});

		gp.add(add, 0, 0);
		gp.add(remove, 1, 0);

		p.add(gp, 0, 4, 2, 1);

		p.setVgap(10);
		p.setPadding(new Insets(40, 40, 20, 70));
		setPane(p);
	}

}
