package app;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Form extends Stage {		
	
	public Form(String title) {
		this(title,null);
	}
	
	public Form(String title, Pane pane) {
		setTitle(title);		
		setOnCloseRequest((WindowEvent we) -> {
            onClose();
        });
		if(pane!=null) {
			setPane(pane);
			show();
		}	
	}
	
	public Pane getPane() {
		return (Pane)getScene().getRoot();
	}
	
	public void setMyPane(Pane pane) {		
		Scene scene=new Scene(pane);		
		setScene(scene);
	}
	
	public void setPane(Pane pane) {		
		Scene scene=new Scene(pane);		
		setScene(scene);
		if(!isShowing())
			show();
	}
	
	protected void onClose() {

    }

    @Override
    public final void close() {
        getOnCloseRequest().handle(null);
        super.close();
    }

}
