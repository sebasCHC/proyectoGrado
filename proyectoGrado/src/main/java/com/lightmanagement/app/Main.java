package com.lightmanagement.app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("/com/lightmanagement/app/fxml/Acceder.fxml"));
			Scene scene = new Scene(root,607,676);
			scene.getStylesheets().add(getClass().getResource("/com/lightmanagement/app/css/estilos.css").toExternalForm());
			primaryStage.setTitle("Acceder");
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}

