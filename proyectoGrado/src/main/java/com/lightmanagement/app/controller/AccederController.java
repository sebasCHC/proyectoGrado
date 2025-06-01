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

public class AccederController implements Initializable {

    @FXML
    private Button acceder;

    @FXML
    private PasswordField contrasenha;

    @FXML
    private Button registrarse;
    
    @FXML
    private Button cambiarContrasenhaUsuario;

    @FXML
    private TextField usuario;
    
    @Override
    public void initialize (URL location, ResourceBundle resources) {
    	
    	//Le permito el acceso a la app al usuario
    	acceder.setOnAction(new EventHandler<ActionEvent>() {
    		@Override
    			public void handle(ActionEvent event) {
    			
    			//Si ninguna casilla está vacía, envío los datos obtenidos al método iniciarSesion de DBUsuariosUtils, para que este los compare
    			if(!usuario.getText().trim().isEmpty() && !contrasenha.getText().trim().isEmpty()) {
    				DBUsuariosUtils.iniciarSesion(event, usuario.getText(), contrasenha.getText());
    		} else {
    			
    			//Si alguna casilla está mal escrita, genero una ventana emergente
				System.out.println("Usuario o contraseña incorrectos");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Usuario o contraseña incorrectos");
				alert.show();
    			}	
			}
    	}		
    );
    	//Redirigo a la ventana "registrarse"
    	registrarse.setOnAction(new EventHandler<ActionEvent>() {
    		@Override
    		public void handle(ActionEvent event) {
    			DBUsuariosUtils.cambiarEscena(event, "/com/lightmanagement/app/fxml/Registrarse.fxml", "Registrarse", null, null);
    		}
    	});
    	
    	//Redirijo a la ventana cambiarConstraseñaUsuario
    	cambiarContrasenhaUsuario.setOnAction(new EventHandler<ActionEvent>() {
    		@Override
    		public void handle(ActionEvent event) {
    			DBUsuariosUtils.cambiarEscena(event, "/com/lightmanagement/app/fxml/CambiarUsuarioContrasenha.fxml", "Cambiar la contraseña", null, null);
    		}
    	}); 
    	
    }

}
