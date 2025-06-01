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

public class RegistrarseController implements Initializable {

    @FXML
    private PasswordField contrasenha;
    
    @FXML
    private PasswordField repetirContrasenha;

    @FXML
    private TextField empresa;

    @FXML
    private Button registrarse;

    @FXML
    private TextField usuario;
    
    @FXML
    private TextField palabraSecreta;

    @FXML
    private Button volver;
    
    @FXML
    private Button ayuda;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
		registrarse.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				//Evalúo si todas las casillas están vacías
				if (usuario.getText().isEmpty() || contrasenha.getText().isEmpty() || repetirContrasenha.getText().isEmpty() 
					|| palabraSecreta.getText().isEmpty() || empresa.getText().isEmpty()) {
					System.out.println("Casillas vacías");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Por favor, llena todas las casillas");
					alert.show();
				
					//Evalúo si el usuario escribe mal las contraseñas
				}else if(!repetirContrasenha.getText().equals(contrasenha.getText())) {
					System.out.println("Error de contraseña");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Las contraseñas no coinciden");
					alert.show();
					
					//Evalúo si el usuario respeta los límites de caracteres y longitud de cada una de las casillas.
				}else if(usuario.getText().length() > 16 || !usuario.getText().matches("[a-zA-Z0-9]+") 
						|| contrasenha.getText().length() > 16 || !contrasenha.getText().matches("[a-zA-Z0-9]+")
						|| empresa.getText().length() > 30
						|| palabraSecreta.getText().length() > 10 || !palabraSecreta.getText().matches("[a-zA-Z0-9]+")){
						
						System.out.println("Error en campos");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("Recuerda:\n\n"
						+ "\u2022 El usuario y contraseña no deben tener espacios, caracteres especiales ni tener más de 16 caracteres\n\n"
						+ "\u2022 La palabra secreta no debe tener espacios, caracteres especiales ni tener más de 10 caracteres\n\n"		
						+ "\u2022 El nombre de la empresa no debe tener más de 30 caracteres");
						alert.show();
				
				}else {
						//Finalmente, registro al usuario y lo redirijo a la ventana principal.
						DBUsuariosUtils.registrarUsuario(event, usuario.getText(), contrasenha.getText(), empresa.getText(), palabraSecreta.getText());
				}
			}
		}
	);
		//Vuelvo a la ventana Acceder
		volver.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUsuariosUtils.cambiarEscena(event, "/com/lightmanagement/app/fxml/Acceder.fxml", "Acceder", null, null);
			}
		});
		
		//Botón de ayuda para el usuario
		ayuda.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Ayuda");
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setContentText("Ten en cuenta los siguientes requisitos:\n\n" + "\u2022El usuario y contraseña no deben tener más de 16 caracteres.\n\n" 
				+ "\u2022 No se admiten caracteres especiales (+, -, @, .,...).\n\n"
				+ "\u2022 Solo se admiten espacios en el nombre de la empresa.\n\n"		
				+ "\u2022 El nombre de la empresa no debe tener más de 30 caracteres.\n\n"
				+ "\u2022 La palabra secreta no debe tener caracteres especiales ni tener más de 10 caracteres");
				alert.show();
			}
		});
	}
}