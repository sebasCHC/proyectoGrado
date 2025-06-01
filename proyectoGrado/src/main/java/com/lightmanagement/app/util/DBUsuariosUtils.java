package com.lightmanagement.app.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.lightmanagement.app.controller.VentanaPrincipalController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class DBUsuariosUtils {
	
	//Método para cambiar de escena al pulsar un botón
	public static void cambiarEscena(ActionEvent event, String fxmlArchivo, String tituloVentana, String empresa, String usuario) {
		
	    try {
	        FXMLLoader loader = new FXMLLoader(DBUsuariosUtils.class.getResource(fxmlArchivo));
	        Parent root = loader.load();
	        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	        Scene scene = new Scene(root);
	        
	        scene.getStylesheets().add(DBUsuariosUtils.class.getResource("/com/lightmanagement/app/css/estilos.css").toExternalForm());
	        
	        if (fxmlArchivo.equals("/com/lightmanagement/app/fxml/VentanaPrincipal.fxml")) {
	            VentanaPrincipalController controller = loader.getController();
	            controller.setUserInformation(empresa, usuario);
	            
	        }
	        stage.setTitle(tituloVentana);
	        stage.setScene(scene);
	        stage.sizeToScene();  
	        stage.setResizable(false); 
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	//Método para establecer conexión con la base de datos y realizar las consultas y actualizaciones en ella. En este caso, registra un nuevo usuario
	public static void registrarUsuario(ActionEvent event, String usuario, String contrasenha, String empresa, String palabraSecreta) {
		Connection connection = null;
		PreparedStatement psInsertar = null;
		PreparedStatement psUsuarioExiste = null;
		ResultSet resultSet = null;
		
		try {
			//Realizo la conexión con la información proporcionada: protocolo jdbc, usuario y contraseña
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lightmanagement", "root", "LM2025GestorInv");
			
			psUsuarioExiste = connection.prepareStatement("SELECT * FROM usuarios WHERE usuario = ?");
			psUsuarioExiste.setString(1, usuario);
			resultSet = psUsuarioExiste.executeQuery();
			
			Alert alert = new Alert(Alert.AlertType.ERROR);
			
			//i el usuario ya existe, le genero una ventana emergente al usuario
			if (resultSet.isBeforeFirst()) {
				System.out.println("El usuario ya existe");
				alert.setContentText("El usuario ya existe");
				alert.show();
			
			} else {
				
				// Hago la inserción de datos del usuario nuevo en la BBDD.
				psInsertar = connection.prepareStatement("INSERT INTO usuarios (usuario, contrasenha, empresa, palabra_secreta) VALUES (?, ?, ?, ?)");
				psInsertar.setString(1, usuario);
				psInsertar.setString(2, contrasenha);
				psInsertar.setString(3, empresa);
				psInsertar.setString(4, palabraSecreta);
				psInsertar.executeUpdate();
				
				cambiarEscena(event, "/com/lightmanagement/app/fxml/VentanaPrincipal.fxml", "Light Management", empresa, usuario);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		
		//Cierre de recursos
		} finally {
				try {
					if(resultSet != null) {
						resultSet.close();
					}
				}catch(SQLException e) {
					e.printStackTrace();
				}
				try {
					if(psInsertar != null) {
						psInsertar.close();
					}
				}catch(SQLException e) {
					e.printStackTrace();
				}
				try {
					if(psUsuarioExiste != null){
						psUsuarioExiste.close();
					}
				}catch(SQLException e) {
					e.printStackTrace();
				}
				try {
					if(connection != null) {
						connection.close();
					}
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
				
	//Método para iniciar sesión. 
	public static void iniciarSesion (ActionEvent event, String usuario, String contrasenha) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lightmanagement", "root", "LM2025GestorInv");
			preparedStatement = connection.prepareStatement ("SELECT contrasenha, empresa, usuario FROM usuarios WHERE usuario =?");
			preparedStatement.setString(1, usuario);
			resultSet = preparedStatement.executeQuery();
			
			//Si el usuario no existe, le genero una ventana emergente al usuario
			if (!resultSet.isBeforeFirst()) {
				System.out.println("El usuario no existe");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Datos incorrectos");
				alert.show();
				
			} else {
				//Si existe, le autorizo el acceso al usuario y lo redirijo a la ventana principal
				while (resultSet.next()) {
					String seleccionarContrasenha = resultSet.getString("contrasenha");
					String seleccionarEmpresa = resultSet.getString("empresa");
					String seleccionarUsuario = resultSet.getString("usuario");
					if (seleccionarContrasenha.equals(contrasenha)) {
						cambiarEscena(event, "/com/lightmanagement/app/fxml/VentanaPrincipal.fxml", "Light Management", seleccionarEmpresa, seleccionarUsuario);
						
					} else {
						System.out.println("Contraseña incorrecta");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("Información proporcionada incorrecta");
						alert.show();
					}
				}
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
			
		}finally {
			if(resultSet != null) {
				try {
					resultSet.close();
				} catch(SQLException e ) {
					e.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				}catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//Método para cambiar de contraseña si el usuario existe
	public static void cambiarContrasenha (ActionEvent event, String usuario, String nuevaContrasenha, String palabraSecreta) {
		Connection connection = null;
		PreparedStatement psUsuarioNoExiste = null; 
		PreparedStatement psUpdate = null;
		ResultSet resultSet = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lightmanagement", "root", "LM2025GestorInv");
			
			psUsuarioNoExiste = connection.prepareStatement("SELECT usuario, palabra_secreta FROM usuarios WHERE usuario = ?");
			psUsuarioNoExiste.setString(1, usuario);
			resultSet = psUsuarioNoExiste.executeQuery();
			
			if (!resultSet.isBeforeFirst()) {
				System.out.println("El usuario no existe");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("El usuario no existe");
				alert.show();
				
				}else {
				
				//Dirijo el resultSet al siguiente elemento de la consulta, la palabra secreta, y la comparo con la escrita por el usuario
				resultSet.next();
				String palabraSecretaDB = resultSet.getString("palabra_secreta");
					
					//Si la palabra secreta coincide, actualizo la contraseña
					if(palabraSecretaDB.equals(palabraSecreta)) {
						
						psUpdate = connection.prepareStatement("UPDATE usuarios SET contrasenha = ? WHERE usuario = ?");
			            psUpdate.setString(1, nuevaContrasenha);  // Nueva contraseña
			            psUpdate.setString(2, usuario);      // Usuario que se va a actualizar
			            psUpdate.executeUpdate();            // Ejecutar la actualización
			            
			            System.out.println("¡Correcto!");
		    			Alert alert = new Alert(Alert.AlertType.INFORMATION);
		    			alert.setContentText("¡La contraseña del usuario " + usuario + " ha sido actualizada con éxito!");
		    			alert.show();
			            
			            cambiarEscena(event, "Acceder.fxml", "Light Management", null, null);
			            
			        } else {
			            // Si las palabras no coinciden, genero una ventana emergente
			            System.out.println("Palabra secreta incorrecta");
			            Alert alert = new Alert(Alert.AlertType.ERROR);
			            alert.setContentText("La palabra secreta no coincide");
			            alert.show();
			        }
			    }
			} catch (SQLException e) {
			    e.printStackTrace();
			}
		
			finally {
				if (resultSet != null) {
					try {
						resultSet.close();
					} catch (SQLException e) {
				e.printStackTrace();
					}
				}
				if (psUsuarioNoExiste != null) {
					try {
						psUsuarioNoExiste.close();
					} catch (SQLException e) {
				e.printStackTrace();
					}
				}
		
				if (psUpdate != null) {
					try {
						psUpdate.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
				e.printStackTrace();
			}
		 }
	  }
					
   }
	//método para cambiar de empresa
	public static void cambiarEmpresa (ActionEvent event, String usuario, String nuevaEmpresa, String palabraSecreta) {
		Connection connection =null;
		PreparedStatement psUsuarioNoExiste = null;
		PreparedStatement psCambiarEmpresa = null;
		ResultSet resultSet = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lightmanagement", "root", "LM2025GestorInv");
			
			psUsuarioNoExiste = connection.prepareStatement("SELECT usuario, palabra_secreta FROM usuarios WHERE usuario = ?");
			psUsuarioNoExiste.setString(1, usuario);
			resultSet = psUsuarioNoExiste.executeQuery();
			
			//Si el usuario no existe, no cambio de empresa
			if(!resultSet.isBeforeFirst()) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Usuario no encontrado");
				alert.show();
			}else {
				//También solicito la palabra secreta
				resultSet.next();
				String palabraSecretaDB = resultSet.getString("palabra_secreta");
				
				if(palabraSecreta.equals(palabraSecretaDB)) {
				
				//Si todo está bien, actualizo los datos.
				psCambiarEmpresa = connection.prepareStatement("UPDATE usuarios SET empresa = ? WHERE usuario = ?");
				psCambiarEmpresa.setString(1, nuevaEmpresa);
				psCambiarEmpresa.setString(2, usuario);
				psCambiarEmpresa.executeUpdate();
				
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setContentText("Empresa actualizada!");
				alert.show();
				}else {
					//Error si la palabra secreta no coincide
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Palabra secreta incorrecta");
					alert.show();
				}
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if(resultSet != null) {
					resultSet.close();
				}
				
			}catch(SQLException e) {
					e.printStackTrace();
				}
			try {
				if(psCambiarEmpresa != null) {
					psCambiarEmpresa.close();
					}
				}catch(SQLException e) {
					e.printStackTrace();
				}
			try {
				if(psUsuarioNoExiste != null) {
					psUsuarioNoExiste.close();
				}
			}catch(SQLException e) {
				
			}
			try {
				if(connection != null) {
					connection.close();
					}
				}catch(SQLException e) {
					e.printStackTrace();
				}
		}
	}
}
				

