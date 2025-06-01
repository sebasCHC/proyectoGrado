package com.lightmanagement.app.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lightmanagement.app.model.ProductoInventario;
import com.lightmanagement.app.util.DBPaginaPrincUtils;
import com.lightmanagement.app.util.DBUsuariosUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class VentanaPrincipalController implements Initializable {

    @FXML
    private Label nombreEmpresa;
    
    @FXML
    private Label nombreUsuario;

    @FXML
    private Button registrarEntradaOEliminarProd;

    @FXML
    private Button ayuda;

    @FXML
    private Button configuracion;

    @FXML
    private Button generarInformePdf;

    @FXML
    private Button registrarSalida;

    @FXML
    private Button salir;

    @FXML
    private Button verInventario;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
		ayuda.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				//Botón de ayuda al usuario en la ventana principal
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setContentText("Cada botón realiza las siguientes funciones: "
									+ "\n\n• Registrar salida de producto: elimina de la base de datos x cantidad de un producto y genera"
									+ "  un informe PDF del producto respectivo, junto a los datos de su comprador(cliente)\n\n"
									+ "• Registrar o eliminar nuevo producto: También genera informes; en este caso, del"
									+ "  producto a ingresar o importar, con los datos de su respectivo proveedor. También"
									+ "  permite eliminar cierta cantidad de un producto\n\n"
									+ "• Generar informe .PDF: Genera un informe .pdf con la información de los productos"
									+ "  registrados en el almacén.\n\n"
									+ "• Ver inventario: Genera una tabla de los productos registrados en el almacén\n\n"
									+ "• Configuración: Ventana para cambiar el nombre de empresa del usuario\n\n"
									+ "• Salir: Cierra sesión. Te direcciona a la ventana de acceso\n\n"
									+ "• Ten en cuenta que los pdf's generados se guardan en el escritorio del usuario");
				alert.show();
			}
			
		});
		
		//Cada botón abre una ventana, pero no cambia de escenario. Se mantienen en el mismo. El método lo contiene la clase DBPaginaPrincUtils
		registrarEntradaOEliminarProd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				  DBPaginaPrincUtils.abrirVentana(event,"/com/lightmanagement/app/fxml/RegistrarEliminarProd.fxml",
						  "Agregar o eliminar un producto");
			}});
	
		registrarSalida.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
					DBPaginaPrincUtils.abrirVentana(event,"/com/lightmanagement/app/fxml/Envios.fxml",
						  "Envíos");
				}
		});
		
		verInventario.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
					DBPaginaPrincUtils.abrirVentana(event,"/com/lightmanagement/app/fxml/VerInventario.fxml",
						  "Inventario");
				}
		});
		
		configuracion.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBPaginaPrincUtils.abrirVentana(event,"/com/lightmanagement/app/fxml/Configuracion.fxml",
						  "Configuración");
				}
		});
		
		salir.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle (ActionEvent event) {
				DBUsuariosUtils.cambiarEscena(event, "/com/lightmanagement/app/fxml/Acceder.fxml", "Acceder", null, null);
			
			}
		});
		
		generarInformePdf.setOnAction(e -> generarTablaPDF()); //Genera informe de la tabla verInventario en pdf al pulsar
	}
	
	//Código para generar un informe pdf de la tabla verInventario
	private void generarTablaPDF() {
	    List<ProductoInventario> productos = VerInventarioController.obtenerDatos();

	    Document document = new Document();
	    
	    //Obtengo la ruta del escritorio del usuario
	    String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";

	    try {
	    	String baseName = "informe_inventario";
            String extension = ".pdf";
            int count = 1;
            File file;
            do {
                file = new File(desktopPath + File.separator + baseName + "_" + count + extension); //Genero un archivo pdf en el escritorio del usuario
                count++;
            } while (file.exists());
	    	
	        PdfWriter.getInstance(document, new FileOutputStream(file));
	        document.open();

	        // Título
	        document.add(new Paragraph("Reporte de Inventario"));
	        document.add(new Paragraph(" ")); // Línea en blanco

	        // Crear tabla con 8 columnas
	        PdfPTable table = new PdfPTable(8);
	        table.setWidthPercentage(100);

	        // Agregar encabezados
	        Stream.of("ID", "Nombre", "Cantidad", "Valor Unitario", "Valor Total", "Ubicación", "Fecha Entrada", "Proveedor")
	                .forEach(titulo -> {
	                    PdfPCell header = new PdfPCell();
	                    header.setPhrase(new Phrase(titulo));
	                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
	                    table.addCell(header);
	                });

	        // Agregar filas
	        for (ProductoInventario p : productos) {
	            table.addCell(String.valueOf(p.getIdProducto()));
	            table.addCell(p.getNombreProducto());
	            table.addCell(String.valueOf(p.getCantidad()));
	            table.addCell(String.format("%.2f", p.getPrecioUnitario()));
	            table.addCell(String.format("%.2f", p.getValorTotal()));
	            table.addCell(p.getUbicacion());
	            table.addCell(String.valueOf(p.getFechaIncorporacion()));
	            table.addCell(p.getProveedor());
	        }

	        document.add(table);
	        document.close();

	        // Confirmación opcional
	        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
	        alerta.setTitle("Documento generado");
	        alerta.setHeaderText(null);
	        alerta.setContentText("Documento generado con éxito en:\n" + file.getAbsolutePath());
	        alerta.showAndWait();

	    } catch (Exception e) {
	        e.printStackTrace();
	        Alert alerta = new Alert(Alert.AlertType.ERROR);
	        alerta.setTitle("Error");
	        alerta.setHeaderText(null);
	        alerta.setContentText("Hubo un error al generar el PDF.");
	        alerta.showAndWait();
	    }
	}
	
	public void setUserInformation(String empresa, String usuario) {
		nombreEmpresa.setText(empresa);
		nombreUsuario.setText(usuario);
	}
}
