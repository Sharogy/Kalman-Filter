package application;
	
import java.io.IOException;

import util.AlertBox;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import view.mainviewcontroller;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {
	
	private Stage primaryStage;
	private mainviewcontroller controller;    
    private Scene scene;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Kalman Filter Test");
			
		initRootLayout();
		this.primaryStage.setOnCloseRequest(e -> {
        	e.consume();
        	safeExit();
        });
			//this.primaryStage.getIcons().add(new Image("themes/icon.png"));

	}
	
	public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/mainview.fxml"));
            AnchorPane rootLayout = (AnchorPane) loader.load();
            
            // Show the scene containing the root layout.
            scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            controller = loader.getController();
            controller.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	 public Stage getPrimaryStage() {
	        return primaryStage;
	    }
	    

	 public static void main(String[] args) {
	        launch(args);
	    }
	    
	 public void safeExit()
	    {
	    	Boolean answer = AlertBox.display("Exiting Program", "Are you sure you want to exit?");
	    	if (answer)
	    	{
	        	//running = false;
	        	this.primaryStage.close();
	        	System.exit(0); 		
	    	}
	    }
	

}
