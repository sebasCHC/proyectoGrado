package com.lightmanagement.app.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.lightmanagement.app.util.DBPaginaPrincUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class EnviosController implements Initializable{		
	
	@FXML
    private TextField cantidadSalida;

    @FXML
    private TextArea direccionCliente;

    @FXML
    private DatePicker fechaSalida;

    @FXML
    private Button registrarSalida;

    @FXML
    private Button generarInforme;

    @FXML
    private TextField idProducto;

    @FXML
    private TextField nombreProducto;

    @FXML
    private TextField nombreCliente;

    @FXML
    private TextField precioVenta;

    @FXML
    private TextField telefonoCliente;
    
    @FXML
    private TextField monto;

    @FXML
    private Button vaciarCasillas;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
		// Solo números enteros (para cantidad)
		UnaryOperator<TextFormatter.Change> filtroEnteros = change -> {
		    if (change.getControlNewText().matches("[0-9]*")) {
		        return change;
		    }
		    return null;
		};
		
		//Formateo el número de teléfono. No permito más de 9 números en la casilla, y solo números
        UnaryOperator<TextFormatter.Change> filtroTelefono = change ->{
        	Pattern patternTelefono = Pattern.compile("\\d{0,9}");
        	Matcher matcherTelefono = patternTelefono.matcher(change.getControlNewText());
        	if(matcherTelefono.matches()) {
        		return change;
        	}
        	return null;
        };
        
        //Filtro para la casilla monto. Solo permite que se escriban números decimales o cadenas vacías
        UnaryOperator<TextFormatter.Change> filtroDecimales = change ->{ 
            Pattern patternDecimales = Pattern.compile("^$|^\\d+(\\.\\d{0,2})?$");
            Matcher matcherDecimales = patternDecimales.matcher(change.getControlNewText());
            if(matcherDecimales.matches()) {
            	return change;
            }
            return null; // Rechazar el cambio si no coincide
        };
		// Aplicar los filtros a los campos correspondientes
		cantidadSalida.setTextFormatter(new TextFormatter<>(filtroEnteros));
		telefonoCliente.setTextFormatter(new TextFormatter<>(filtroTelefono));
		monto.setTextFormatter(new TextFormatter<>(filtroDecimales));
		
		registrarSalida.setOnAction(new EventHandler <ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					if(idProducto.getText().trim().isEmpty()
					   ||nombreProducto.getText().trim().isEmpty()
					   ||cantidadSalida.getText().trim().isEmpty()
					   ||fechaSalida.getValue() == null
					   ||nombreCliente.getText().trim().isEmpty()){
						
						Alert alert = new Alert(AlertType.ERROR);
						alert.setContentText("Por favor, llena correctamente todas las casillas obligatorias para registrar la salida");
						alert.show();
						return;
					}
					//transformación de datos String a enteros o decimales
					int cantidadSalidaEntero = Integer.valueOf(cantidadSalida.getText().trim());
					int idProductoEntero = Integer.valueOf(idProducto.getText().trim());
					
					//El siguiente TextField (telefonoCliente), al ser opcional, merece un enfoque distinto:
					
					//Primero, tiene que pasar por una condición para posteriormente transformar los datos. Se usarán operadores ternarios (?)*
					
                    int telefonoEntero = telefonoCliente.getText().trim().isEmpty() //esta línea es la equivalente a if()
                    		
                    		? 0 //si se cumple la condición de que está vacía, se transforma en 0 entero
                    				
                    		: (Integer.valueOf(telefonoCliente.getText().trim())); //esta línea es la equivalente a else{}
                    
                    /*CONCLUSIÓN: si el usuario deja el campo vacío, este se transformará a un 0 Int para que el programa no de un error
          		  		de transformación de formato.
          		  		Si el usuario NO deja el campo vacío(escribe cualquier número), se sustrae el valor escrito en String, para luego convertir su valor en entero.
          		  		Esto se debe hacer, ya que un String " " (vacío) da errores de transformación a números. 
                     */
                    
                  DBPaginaPrincUtils.registrarSalida(event, idProductoEntero, nombreProducto.getText(), cantidadSalidaEntero, nombreCliente.getText(),
                		  telefonoEntero, direccionCliente.getText());  
					
				}catch(NumberFormatException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error en formato de número");
                    alert.show();
                }
			}
			
		});
		
		//Genero el informe .pdf de la salida o envío del producto
		generarInforme.setOnAction(new EventHandler <ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
			    // Definir el documento PDF y la ruta de escritorio
			    Document document = new Document();
			    String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
			    
			    try {
			        // Validación de campos obligatorios
			        if (!idProducto.getText().trim().isEmpty() &&
			            !nombreProducto.getText().trim().isEmpty() &&
			            !cantidadSalida.getText().trim().isEmpty() &&
			            !nombreCliente.getText().trim().isEmpty() &&
			            fechaSalida.getValue() != null) {

			            // Generar un nombre único para el archivo
			            String baseName = "informe_salida";
			            String extension = ".pdf";
			            int count = 1;
			            File file;
			            do {
			                file = new File(desktopPath + File.separator + baseName + "_" + count + extension);
			                count++;
			            } while (file.exists());

			            // Crear el archivo PDF en la ruta generada
			            PdfWriter.getInstance(document, new FileOutputStream(file));
			            document.open();

			            // Título
			            document.add(new Paragraph("Informe de salida"));
			            document.add(new Paragraph(" "));

			            // Datos ingresados por el usuario
			            document.add(new Paragraph("ID de producto: " + idProducto.getText()));
			            document.add(new Paragraph("Nombre del Producto: " + nombreProducto.getText()));
			            document.add(new Paragraph("Cantidad: " + cantidadSalida.getText()));			            			           			            
			            document.add(new Paragraph("Cliente: " + nombreCliente.getText()));
			            document.add(new Paragraph("Teléfono: " + telefonoCliente.getText()));
			            document.add(new Paragraph("Dirección: " + direccionCliente.getText()));
			            document.add(new Paragraph("Fecha de Salida: " + fechaSalida.getValue()));
			            document.add(new Paragraph("Monto: " + (monto.getText() != null ? (monto.getText() + "€") : " ")));

			            // Cerrar el documento
			            document.close();

			            // Mostrar alerta de éxito con la ruta del archivo generado
			            Alert alert = new Alert(Alert.AlertType.INFORMATION);
			            alert.setTitle("Documento Generado");
			            alert.setHeaderText(null);
			            alert.setContentText("Documento generado con éxito en:\n" + file.getAbsolutePath());
			            alert.show();

			        } else {
			            // Si faltan campos, mostrar alerta de error
			            Alert alert = new Alert(Alert.AlertType.ERROR);
			            alert.setTitle("Campos incompletos");
			            alert.setHeaderText(null);
			            alert.setContentText("Llena todos los campos obligatorios para generar el documento.");
			            alert.show();
			        }

			    } catch (Exception e) {
			        e.printStackTrace();
			        Alert alert = new Alert(Alert.AlertType.ERROR);
			        alert.setTitle("Error");
			        alert.setContentText("Ocurrió un error al generar el documento.");
			        alert.show();
			    }
			}
	    });
		
		//Vacío las casillas al pulsar el botón Vaciar casillas
		vaciarCasillas.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				//Al pulsar botón vaciar casillas, todos pasan a tener un String vacío "".
				idProducto.setText("");
				nombreProducto.setText("");
				cantidadSalida.setText("");
				fechaSalida.setValue(null);
				nombreCliente.setText("");
				telefonoCliente.setText("");
				direccionCliente.setText("");
				monto.setText("");
			}
			
		});
	}

}
