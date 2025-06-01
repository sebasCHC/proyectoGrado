package com.lightmanagement.app.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.lightmanagement.app.model.ProductoInventario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

//Clase verInventarioController para generar una ventana con los productos de la BBDD
public class VerInventarioController implements Initializable {
	
	//Defino el nombre de la tabla y cada una de sus columnas, con su tipo de dato
	@FXML
	private TableView<ProductoInventario> inventario;

    @FXML
    private TableColumn<ProductoInventario, Integer> cantidadProducto;
    
    @FXML 
    private TableColumn<ProductoInventario, String> ubicacion;

    @FXML
    private TableColumn<ProductoInventario, Date> fechaIncorporacion;

    @FXML
    private TableColumn<ProductoInventario, Integer> idProducto;

    @FXML
    private TableColumn<ProductoInventario, String> nombreProducto;

    @FXML
    private TableColumn<ProductoInventario, Double> precioUnitario;

    @FXML
    private TableColumn<ProductoInventario, String> proveedor;

    @FXML
    private TableColumn<ProductoInventario, Double> valorTotal;
    
    @FXML
    private TextField casillaBusqueda;
    
    @FXML
    private Button buscar;
    
    //Método para sustraer los datos de la BBDD. Anteriormente, se debió haber creado una clase ProductoInventario para crear un objeto tipo ProductoInventario
    public static ObservableList<ProductoInventario> obtenerDatos() {
    	
    	//Creo una lista observable llamada datos. Esta almacenará los datos de la consulta
        ObservableList<ProductoInventario> datos = FXCollections.observableArrayList();
        
		Connection connection = null;
		PreparedStatement psDatosProducto = null;
		ResultSet resultSet = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lightmanagement", "root", "LM2025GestorInv");
			
			//Consulta a varias tablas relacionadas, con sus respectivas claves primarias y foráneas
			psDatosProducto = connection.prepareStatement("SELECT productos.id_producto AS id_producto, "
					+ "productos.nombre AS nombre_producto, "
					+ "productos.cantidad AS cantidad, "
					+ "productos.valor_unitario AS valor_unitario, "
					+ "productos.valor_total AS valor_total, "
					+ "ubicaciones.nombre AS ubicacion, "
					+ "entrada_producto.fecha_entrada AS fecha_entrada, "
					+ "proveedores.nombre AS nombre_proveedor "
					+ "FROM productos "
					
					//Unión de tablas de productos, ubicaciones, proveedores y tabla intermedia: entrada_producto
					+ "LEFT JOIN entrada_producto ON productos.id_producto = entrada_producto.id_producto "
					+ "LEFT JOIN proveedores ON entrada_producto.id_proveedor = proveedores.id_proveedor "
					+ "LEFT JOIN ubicaciones ON productos.id_producto = ubicaciones.id_producto");
			resultSet = psDatosProducto.executeQuery();
			
			//Obtengo todos los resultados de cada producto de la BBDD y los almaceno en una variable "p", para posteriormente agregarlos a la lista "datos"
			while(resultSet.next()) {
				ProductoInventario p = new ProductoInventario(resultSet.getInt("id_producto"),
							resultSet.getString("nombre_producto"),
							resultSet.getInt("cantidad"), 
							resultSet.getDouble("valor_unitario"), 
							resultSet.getDouble("valor_total"),
							resultSet.getDate("fecha_entrada"), 
							resultSet.getString("nombre_proveedor"),
							resultSet.getString("ubicacion"));
							datos.add(p);
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
				if(psDatosProducto != null) {
					psDatosProducto.close();
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
		return datos;
		
		}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
		//Asociación de cada columna con los datos de la clase ProductoInventario. Son getters que obtienen los datos para imprimirlos por pantalla, en este caso
		idProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
	    nombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
	    cantidadProducto.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
	    ubicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
	    precioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
	    valorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
	    fechaIncorporacion.setCellValueFactory(new PropertyValueFactory<>("fechaIncorporacion"));
	    proveedor.setCellValueFactory(new PropertyValueFactory<>("proveedor"));
	    
	    //Le paso los datos obtenidos a una lista observable "datos"
	    ObservableList<ProductoInventario> datos = obtenerDatos();
	    
	    //finalmente, le envío los datos obtenidos a la tabla "inventario" 
	    inventario.setItems(datos);
	    
	    //Acción para buscar un producto al pulsar el botón buscar.
	    buscar.setOnAction(e -> buscarProducto());
	}

	private void buscarProducto() {
		
		String productoBuscar = casillaBusqueda.getText().trim().toLowerCase();
		
		//Si el usuario no escribió nada, se muestra la lista completa del inventario
		if(productoBuscar.isEmpty()) {
		inventario.setItems(obtenerDatos());
		return;
		}
		
		//Lista observable que contendrá los productos que coincidan con la búsqueda
		ObservableList<ProductoInventario> filtrado = FXCollections.observableArrayList();
		
		//bucle for-each para recorrer todos los datos del método obtenerDatos()
		for(ProductoInventario p : obtenerDatos()) {
			
			//Si el producto obtenido por el índice p coincide con el nombre de la búsqueda, se agrega el nombre del producto a la lista "filtrado".
			if (p.getNombreProducto().toLowerCase().contains(productoBuscar)) {
				filtrado.add(p);
			}
		}
		//Finalmente, le establezco los datos obtenidos de la búsqueda a la tabla "inventario", para mostrarlos por pantalla.
		inventario.setItems(filtrado);
  }
}
