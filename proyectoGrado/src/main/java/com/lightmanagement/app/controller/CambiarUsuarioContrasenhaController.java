package com.lightmanagement.app.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.lightmanagement.app.util.DBUsuariosUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CambiarUsuarioContrasenhaController implements Initializable {

    @FXML
    private Button ayuda;

    @FXML
    private PasswordField nuevaContrasenha;

    @FXML
    private TextField palabraSecreta;

    @FXML
    private Button actualizar;

    @FXML
    private PasswordField repetirContrasenha;

    @FXML
    private TextField usuario;

    @FXML
    private Button volver;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	actualizar.setOnAction(new EventHandler<ActionEvent>() {
    		@Override
    		public void handle (ActionEvent event) {
    			
    			//Condición para evaluar si las casillas están vacías
    			if(usuario.getText().isEmpty() || nuevaContrasenha.getText().isEmpty() || repetirContrasenha.getText().isEmpty() 
    					|| palabraSecreta.getText().isEmpty()) {
    				
    				System.out.println("Casillas vacías");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Por favor, llena todas las casillas");
					alert.show();
				
					//Condición para evaluar si las contraseñas no coinciden
    			} else if(!repetirContrasenha.getText().equals(nuevaContrasenha.getText())) {
					System.out.println("Error de contraseña");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Las contraseñas no coinciden");
					alert.show(); 
    				
					//Condición para evaluar si la nueva contraseña supera el límite permitido	
    			} else if(nuevaContrasenha.getText().length() > 16 || !nuevaContrasenha.getText().matches("[a-zA-Z0-9]+")) {
    				System.out.println("Error en campos");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Recuerda:\n\n"
					+ "\u2022 La contraseña no debe tener espacios, caracteres especiales ni tener más de 16 caracteres\n\n");
					alert.show();
    				
					//Finalmente, si no se cumplen ninguna de las condiciones anteriores, cambio la contraseña al usuario.
    			} else {
    			DBUsuariosUtils.cambiarContrasenha(event, usuario.getText(), nuevaContrasenha.getText(), palabraSecreta.getText());
    				
    			}
    		}
    	});
    	//Devuelvo al usuario a la ventana Acceder
    	volver.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUsuariosUtils.cambiarEscena(event, "/com/lightmanagement/app/fxml/Acceder.fxml", "Acceder", null, null );
			}
		});
		
    	//Botón de ayuda para el usuario
		ayuda.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Ayuda");
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setContentText("Ten en cuenta los siguientes requisitos:\n\n" + "\u2022 La contraseña no debe tener más de 16 caracteres.\n\n" 
				+ "\u2022 No se admiten caracteres especiales (+, -, @, .,...).\n\n");
				alert.show();
			}
		});
	}
}

