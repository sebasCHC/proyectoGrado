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

public class ConfiguracionController implements Initializable {

    @FXML
    private Button aceptar;

    @FXML
    private Button ayuda;

    @FXML
    private PasswordField nuevaContrasenha;
    
    @FXML
    private TextField usuario;

    @FXML
    private TextField nuevaEmpresa;

    @FXML
    private TextField palabraSecreta;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		aceptar.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				
				//Condición para actualizar el nombre de la empresa. Si los campos están vaciós, se lo notifico al usuario
				if(usuario.getText().trim().isEmpty()
				 ||nuevaEmpresa.getText().trim().isEmpty()
				 ||palabraSecreta.getText().trim().isEmpty()) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Por favor, llena todas las casillas");
					alert.show();
				
				//Si la nueva empresa sobrepasa el límite de caracteres permitido, genero una ventana emergente
				}else if(nuevaEmpresa.getText().length() > 30) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Recuerda:\n\n"
							+ "\\u2022 El nombre de empresa no puede sobrepasar 30 caracteres");
					alert.show();
				
				//Finalmente, si no se cumplen ninguna de las condiciones, actualizo el nombre de la empresa
				}else {
					DBUsuariosUtils.cambiarEmpresa(event, usuario.getText(), nuevaEmpresa.getText(), palabraSecreta.getText());
				}
			}

			
			
		});
		
		//Botón de ayuda para el usuario
		ayuda.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setContentText("• Recuerda que el nombre de la empresa no debe tener más de 30 caracteres\n\n"
									+"• Escribe tu nombre de usuario y palabra secreta");
				alert.show();
			}
			
		});
	}

}

