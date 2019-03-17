package app;

import execution.CommandExecutioner;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class EditDNS extends Form {

	public EditDNS(ClientView owner) {
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

		Label l1 = new Label("Primary:");
		Label l2 = new Label("Backup:");

		TextField t1 = new TextField();
		TextField t2 = new TextField();

		exec.refreshSettigns();
		if (!exec.settings.dns.contains("disabled")) {
			String[] dns = exec.settings.dns.split(",");
			try {
				t1.setText(dns[0]);
				t2.setText(dns[1]);
			} catch (Exception e) {
			}
		}

		Button disable = new Button("Disable");
		disable.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				exec.disableDNS();

				exec.refreshSettigns();
				if (exec.settings.dns.contains("disabled")) {
					owner.dnsLbl.setText("Disabled");
				} else {
					owner.dnsLbl.setText(exec.settings.dns);
				}
				EditDNS.this.close();
			}

		});

		Button set = new Button("   Set   ");
		set.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					String resp;
					if (t2.getText().contentEquals("")) {
						resp = exec.setDNS(t1.getText());
					} else {
						resp = exec.setDNS(t1.getText() + " " + t2.getText());
					}

					if (resp.contains("successfully")) {
						exec.refreshSettigns();
						if (exec.settings.dns.contains("disabled")) {
							owner.dnsLbl.setText("Disabled");
						} else {
							owner.dnsLbl.setText(exec.settings.dns);
						}
						EditDNS.this.close();
					} else {
						Alert a = new Alert(AlertType.ERROR);
						a.setTitle("DNS Error");
						a.setResizable(false);
						a.setContentText("The DNS you entered is not valid.\n" + resp);
						a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
						a.showAndWait();
					}
				} catch (Exception e) {
					Alert a = new Alert(AlertType.ERROR);
					a.setTitle("DNS Error");
					a.setResizable(false);
					a.setContentText("DNS Error");
					a.showAndWait();
				}

			}

		});

		double width = 50;
		set.minWidth(width);
		set.maxWidth(width);
		disable.minWidth(width);
		disable.maxWidth(width);

		p.add(l1, 0, 0);
		p.add(t1, 0, 1);
		p.add(l2, 0, 2);
		p.add(t2, 0, 3);

		GridPane gp = new GridPane();
		gp.setHgap(20);
		gp.add(set, 0, 0);
		gp.add(disable, 1, 0);
		gp.setPadding(new Insets(0, 0, 0, 8));

		p.add(gp, 0, 4, 2, 1);

		p.setVgap(10);
		p.setPadding(new Insets(20, 40, 20, 65));
		setPane(p);
	}

}
