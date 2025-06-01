package com.lightmanagement.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

//Abro ventanas dentro de la página principal
public class DBPaginaPrincUtils {
	public static void abrirVentana(ActionEvent event, String fxmlArchivo, String tituloVentana) {
		try {
	        
			FXMLLoader loader = new FXMLLoader(DBPaginaPrincUtils.class.getResource(fxmlArchivo));
			Parent root = loader.load();
			
	        // Crear el nuevo Stage
	        Stage ventanaModal = new Stage();
	        ventanaModal.setTitle(tituloVentana);
	        ventanaModal.setScene(new Scene(root));
	        ventanaModal.setResizable(false);
	        ventanaModal.show();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	//Agrego todo lo relacionado con el producto a la base de datos: su nombre, cantidad, precio, proveedor, ubicación, etc...
	public static void agregarProducto(ActionEvent event, String nombre, int cantidadAgg, double precioUnit, String descripcion, 
			String ubicacion, LocalDate fechaIncorp, String proveedor, double monto, int telefono, String direccion) {
		Connection connection = null;
		PreparedStatement psInsertarProductos = null;
		PreparedStatement psInsertarProveedor = null;
		PreparedStatement psInsertarEntradaProducto = null;
		PreparedStatement psInsertarUbicacion = null;
		ResultSet rsInsertarProductos = null;
		ResultSet rsInsertarProveedor = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lightmanagement", "root", "LM2025GestorInv");
				
				//Inserto en base de datos información del producto
				psInsertarProductos = connection.prepareStatement("INSERT INTO productos(nombre, cantidad, valor_unitario, descripcion) VALUES (?, ?, ?, ?)", 
						Statement.RETURN_GENERATED_KEYS);
				psInsertarProductos.setString(1, nombre);
				psInsertarProductos.setInt(2, cantidadAgg);
				psInsertarProductos.setDouble(3, precioUnit);
				psInsertarProductos.setString(4, descripcion);
				psInsertarProductos.executeUpdate();
				
				//Generar llave (id_producto)
				rsInsertarProductos = psInsertarProductos.getGeneratedKeys();
				int productoId = 0;
				
					if (rsInsertarProductos.next()) {
						productoId = rsInsertarProductos.getInt(1);
					}
					
				//inserto en base de datos información del proveedor
				psInsertarProveedor = connection.prepareStatement("INSERT INTO proveedores(nombre, telefono, direccion) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				psInsertarProveedor.setString(1, proveedor);
				psInsertarProveedor.setInt(2, telefono);
				psInsertarProveedor.setString(3, direccion);
				psInsertarProveedor.executeUpdate();
				
				//Generar llave (id_proveedor)
				rsInsertarProveedor = psInsertarProveedor.getGeneratedKeys();
				int proveedorId = 0;
					if (rsInsertarProveedor.next()) {
						proveedorId = rsInsertarProveedor.getInt(1);
					}
				
				//uso las llaves(id_produco, id_proveedor) anteriormente generadas para insertar datos en tabla intermedia "entrada_producto"
				psInsertarEntradaProducto = connection.prepareStatement("INSERT INTO entrada_producto(id_producto, id_proveedor, fecha_entrada, cantidad_entrada, monto) VALUES (?, ?, ?, ?, ?)");
				psInsertarEntradaProducto.setInt(1, productoId);
				psInsertarEntradaProducto.setInt(2, proveedorId);
				psInsertarEntradaProducto.setDate(3, java.sql.Date.valueOf(fechaIncorp));
				psInsertarEntradaProducto.setInt(4, cantidadAgg);
				psInsertarEntradaProducto.setDouble(5, monto);
				psInsertarEntradaProducto.executeUpdate();
				
				//inserto en base da datos la información de la ubicación del producto. Necesito la llave generada(id_producto) de la consulta psInsertarProductos.
				psInsertarUbicacion = connection.prepareStatement("INSERT INTO ubicaciones (id_producto, nombre) VALUES (?, ?)");
				psInsertarUbicacion.setInt(1, productoId);
				psInsertarUbicacion.setString(2, ubicacion);
				psInsertarUbicacion.executeUpdate();
				
				System.out.println("Correcto!");
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setContentText("Los datos se han agregado correctamente!");
				alert.show();
			
		}catch(SQLException e){
			e.printStackTrace();
		
		//CIERRE DE RECURSOS	
		}finally {
			if(rsInsertarProductos != null) {
				try {
					rsInsertarProductos.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
			if(rsInsertarProveedor != null) {
				try {
					rsInsertarProveedor.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
			if(psInsertarProductos != null) {
				try {
					psInsertarProductos.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
			if(psInsertarProveedor != null) {
				try {
					psInsertarProveedor.close();
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}
			if(psInsertarEntradaProducto != null) {
				try {
					psInsertarEntradaProducto.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
			
			}
			
			if(connection != null) {
				try {
					connection.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}
	
	//Método para eliminar cierta cantidad de un producto en la BBDD
	public static void eliminarProducto(ActionEvent event, int idProducto, int cantidadEliminar) {
		Connection connection = null;
		PreparedStatement psEliminarProducto = null;
		PreparedStatement psProductoNoExiste = null;
		ResultSet resultSet = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lightmanagement", "root", "LM2025GestorInv");
			
			psProductoNoExiste = connection.prepareStatement("SELECT cantidad FROM productos WHERE id_producto = ?");
			psProductoNoExiste.setInt(1, idProducto);
			resultSet = psProductoNoExiste.executeQuery();
			
			//Si el producto no existe, genero una ventana de alerta
			if(!resultSet.isBeforeFirst()) {
				System.out.println("No se ha encontrado el producto para eliminar");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("No se ha encontrado el producto para eliminar");
				alert.show();
				
			}else{
				resultSet.next();
				int cantidadActual = resultSet.getInt("cantidad");
				
				//Si la cantidad a eliminar supera la cantidad actual, genero una ventana de error
				if(cantidadEliminar > cantidadActual) {
					System.out.println("No hay suficientes existencias");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("No hay suficientes existencias");
					alert.show();
					
				}else {
					
					int cantidadRestada = cantidadActual - cantidadEliminar;
					
					psEliminarProducto = connection.prepareStatement("UPDATE productos SET cantidad = ? WHERE id_producto = ?");
					psEliminarProducto.setInt(1, cantidadRestada);
					psEliminarProducto.setInt(2, idProducto);
					psEliminarProducto.executeUpdate();
					
					System.out.println("Correcto!");
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setContentText("Los datos se han eliminado correctamente!");
					alert.show();
				}
				
			}
				
		}catch(SQLException e) {
			e.printStackTrace();
			
		//CIERRE DE RECURSOS	
		}finally { 
			try {
				if(resultSet != null) {
					resultSet.close();
					}
				
				}catch(SQLException e) {
					e.printStackTrace();
					}
			try {
				if(psEliminarProducto != null) {
					psEliminarProducto.close();
				}
			}catch(SQLException e) {
				e.printStackTrace();
				}
			try {
				if(psProductoNoExiste != null) {
					psProductoNoExiste.close();
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
		
		//Método para registrar la salida de un producto del almacén
		public static void registrarSalida(ActionEvent event, int idProducto, String nombreProducto, int cantidadEliminar,
				String cliente, int telefono, String direccion) {
			Connection connection = null;
			PreparedStatement psActualizarProductos = null;
			PreparedStatement psSinExistencias = null;
			PreparedStatement psActualizarClientes = null;
			ResultSet rsSinExistencias = null;
			
			try {
				connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lightmanagement", "root", "LM2025GestorInv");
				
				psSinExistencias = connection.prepareStatement("SELECT id_producto, cantidad FROM productos WHERE id_producto = ?");
				psSinExistencias.setInt(1, idProducto);
				rsSinExistencias = psSinExistencias.executeQuery();
				
				//Si el producto no existe, se lo notifico al usuario
				if(!rsSinExistencias.isBeforeFirst()) {
					System.out.println("No se ha encontrado el ID del producto para eliminar");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("No se ha encontrado el ID del producto para eliminar");
					alert.show();
					
				}else{
					rsSinExistencias.next();
					int cantidadActual = rsSinExistencias.getInt("cantidad");
					
					//Si la cantidad a eliminar ingresada por el usuario supera la actual, genero una ventana de error.
					if(cantidadEliminar > cantidadActual) {
						System.out.println("No hay suficientes existencias");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("No hay suficientes existencias");
						alert.show();
						
					}else {
						//Si se cumple la condición, se elimina la cantidad del producto de la base de datos
						int cantidadRestada = cantidadActual - cantidadEliminar;
						psActualizarProductos = connection.prepareStatement("UPDATE productos SET cantidad = ? WHERE id_producto = ?");
						psActualizarProductos.setInt(1, cantidadRestada);
						psActualizarProductos.setInt(2, idProducto);
						psActualizarProductos.executeUpdate();
						
						//Asimismo, se inserta la información del cliente
						psActualizarClientes = connection.prepareStatement("INSERT INTO clientes(nombre_cliente, telefono, direccion) VALUES(?, ?, ?)");
						psActualizarClientes.setString(1, cliente);
						psActualizarClientes.setInt(2, telefono);
						psActualizarClientes.setString(3, direccion);
						psActualizarClientes.executeUpdate();
						
						System.out.println("Salida registrada con éxito!");
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setContentText("Salida registrada con éxito!");
						alert.show();
					}
				}
		
			} catch(SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			//Cierre de recursos
			}finally {
				try {
					if(rsSinExistencias != null) {
						rsSinExistencias.close();
					}
				}catch(SQLException e) {
					e.printStackTrace();
				}
				try {
					if(psActualizarProductos != null) {
						psActualizarProductos.close();
					}
				}catch(SQLException e) {
					e.printStackTrace();
				}
				try {
					if(psSinExistencias != null) {
						psSinExistencias.close();
					}
				}catch(SQLException e) {
					e.printStackTrace();
				}
				try {
					if(psActualizarClientes != null) {
						psActualizarClientes.close();
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
	
}

	
	
	

