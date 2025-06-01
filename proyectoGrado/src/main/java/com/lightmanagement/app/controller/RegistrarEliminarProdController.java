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

public class RegistrarEliminarProdController implements Initializable {
	
	@FXML
	private Button vaciarCasillas;
	
	@FXML
	private TextField telefono;
	
	@FXML
	private TextArea direccion;
	
	@FXML
	private Button mostrarValorTotal;
	
	@FXML
	private Button generarInforme;
	
	@FXML
	private Button agregarProducto;
	
	@FXML
	private Button eliminarProducto;

    @FXML
    private TextField cantidadAgg;
    
    @FXML
    private TextField valorTotal;
    
    @FXML
    private TextField monto;

    @FXML
    private TextField cantidadElim;

    @FXML
    private TextArea descripcion;

    @FXML
    private DatePicker fechaImport;

    @FXML
    private TextField nombreProducto;

    @FXML
    private TextField idNombreElim;

    @FXML
    private TextField precioUnit;

    @FXML
    private TextField proveedor;

    @FXML
    private TextField ubicacion;
    
    //Método con TextFormatter para limitar las cadenas de caracteres a máximo 45 (para nombreProducto, descripcion, proveedor, ubicación y dirección)
    //Tienen que ir por fuera de initialize ya que este es otro método. Java no permite metodos dentro de otros métodos.
    private TextFormatter<String> textoFormateado() {
        return new TextFormatter<>(change -> {
            String nuevoTextoFormateado = change.getControlNewText();
            return (nuevoTextoFormateado.length() <= 45) ? change : null;
        });
    }
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
    	
    	//llamo al textoFormateado anteriormente definido, Tiene que ir dentro del initialize para poder inicializarlo en sus respectivos textField's
    	nombreProducto.setTextFormatter(textoFormateado());//para el nombre del producto
        descripcion.setTextFormatter(textoFormateado());// para la descripción de este
        ubicacion.setTextFormatter(textoFormateado()); // para su ubicación
        proveedor.setTextFormatter(textoFormateado()); // para su proveedor
        direccion.setTextFormatter(textoFormateado()); //para la dirección del provedor
        
    	//No permito que el usuario escriba cualquier caracter en las casillas cantidad y precio. Solo enteros y decimales, respectivamente
    	UnaryOperator<TextFormatter.Change> filtroEnteros = change -> {
    		
    		// Expresión regular para permitir solo números enteros en la cantidad agregada del producto.
            Pattern patternEnteros = Pattern.compile("[0-9]*"); // Permite números enteros
            Matcher matcherEnteros = patternEnteros.matcher(change.getControlNewText());
            if (matcherEnteros.matches()) {
                return change;
            }
            return null;
    	};
    	// Formateo para los números decimales de precios. Solo permite que el usuario escriba números decimales con 2 números después del punto
        UnaryOperator<TextFormatter.Change> filtroDecimales = change ->{ //esta línea crea un objeto filtro
            Pattern patternDecimales = Pattern.compile("^$|^\\d+(\\.\\d{0,2})?$");
            Matcher matcherDecimales = patternDecimales.matcher(change.getControlNewText());
            if(matcherDecimales.matches()) {
            	return change;
            }
            return null; // Rechazar el cambio si no coincide
        };
      //Formateo para el número de teléfono. no permito más de 9 números en la casilla
        UnaryOperator<TextFormatter.Change> filtroTelefono = change ->{
        	Pattern patternTelefono = Pattern.compile("\\d{0,9}");
        	Matcher matcherTelefono = patternTelefono.matcher(change.getControlNewText());
        	if(matcherTelefono.matches()) {
        		return change;
        	}
        	return null;
        };
        
        //Implemento el filtro para enteros en cantidad
        TextFormatter<String> textFormatterCantidad = new TextFormatter<>(filtroEnteros);
        cantidadAgg.setTextFormatter(textFormatterCantidad);
        
        //Implemento el filtro para decimales en precios
        TextFormatter<String> textFormatterPrecio = new TextFormatter<>(filtroDecimales);
        precioUnit.setTextFormatter(textFormatterPrecio);
        
        //Implemento el mismo filtro para decimales en monto
        TextFormatter<String> textFormatterMonto = new TextFormatter<>(filtroDecimales);
        monto.setTextFormatter(textFormatterMonto);
        
        //Implemento el filtro para enteros en número de teléfono
        TextFormatter<String> textFormatterTelefono = new TextFormatter<>(filtroTelefono);
        telefono.setTextFormatter(textFormatterTelefono);
        
        agregarProducto.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    
                    if (nombreProducto.getText().trim().isEmpty()
                        || cantidadAgg.getText().trim().isEmpty()
                        || precioUnit.getText().trim().isEmpty()
                        || ubicacion.getText().trim().isEmpty()
                        || fechaImport.getValue() == null
                        || proveedor.getText().trim().isEmpty()) {

                        System.out.println("Error en casillas");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Por favor, llena correctamente todas las casillas obligatorias para agregar el producto");
                        alert.show();
                        return; // Si se cumple la condición, este return detiene la ejecución del método handle.
                        
                    }
                   
                    //transformación de datos String a enteros o decimales
                    int cantidadAggEntero = Integer.valueOf(cantidadAgg.getText().trim()); // para cantidad
                    double precioUnitDouble = Double.valueOf(precioUnit.getText().trim()); // para precio
                    double valorTotalDouble = cantidadAggEntero * precioUnitDouble; //para valor total
                    
                    /*Los siguientes TextField, al ser opcionales, merecen un enfoque distinto:
                     Primero, tienen que pasar por una condición para posteriormente transformar los datos. Se usarán operadores ternarios(?)*/
                    
                    double montoDouble = monto.getText().trim().isEmpty()//esta línea es la equivalente a if()	
                    		
                    		? 0.0 //si se cumple la condición, transformo el resultado a 0 entero	
                    		
                    		: Double.valueOf(monto.getText().trim()); //esta línea es la equivalente a else{}
                    
                    		/*CONCLUSIÓN: si el usuario deja el campo vacío, este se transforma a un 0.0 decimal para que el programa no de un error
                    		  de transformación de formato.
                    		  Si el usuario no deja el campo vacío(escribe un numero), se sustrae el valor escrito en String, para luego convertir su valor en double.
                    		  Esto se debe hacer, ya que un String " " (vacío) da errores de transformación a números. 
                    		  */
                    
                    //Esta línea implementa la misma lógica, pero transformando a entero
                    int telefonoInt = telefono.getText().trim().isEmpty() 
                    		? 0
                    		: (Integer.valueOf(telefono.getText().trim()));
                    
                    String valorTotalTextField = String.valueOf(valorTotalDouble); // le inserto el objeto valorTotalDouble al nuevo objeto String ValorTotalTextField.
                    															   // Este también tiene que pasar por conversión de dato, ya que, en un principo, es un String.
                    
                    valorTotal.setText(valorTotalTextField); //finalmente, imprimo por la ventana de la aplicación el valorTotal transformado a decimal.

                    if (cantidadAggEntero > 1000) {
                        System.out.println("Error en cantidad agregada");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("No puedes agregar un número mayor a 1000 en cantidad de producto");
                        alert.show();
                        return; //ver para qué sirve
                    }
                    DBPaginaPrincUtils.agregarProducto(event, nombreProducto.getText(), cantidadAggEntero, precioUnitDouble, descripcion.getText(),
                    		ubicacion.getText(), fechaImport.getValue(), proveedor.getText(), montoDouble, telefonoInt, direccion.getText());

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error en formato de número");
                    alert.show();
                }
            }
        });
    	
    	mostrarValorTotal.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//se crea un try/catch para capturar la excepcion de formato. Puede que el usuario no escriba nada(espacio en blanco) en las casillas
				//cantidad y precio unitario y el programa de error de formato, ya que lo interpreta como cadena String " " vacío. 
				try {
					
				int cantidadAggEntero = Integer.valueOf(cantidadAgg.getText().trim());	//transformo el valor obtenido en cantidadAgg(String),
																						//para convertirlo en entero (int).
				
    	    	if(cantidadAggEntero > 1000) { //si el valor introducido es mayor a 1000, se muestra la alerta
    	    		Alert alert = new Alert(AlertType.ERROR);
    	    		alert.setContentText("No puedes agregar un valor mayor de 1000 en cantidad");
    	    		alert.show();
    	    		
    	    	} else {
    	    		
    	    		double precioUnitDouble = Double.valueOf(precioUnit.getText().trim()); //Transformo el valor obtenido en precioUnit (String) a su valor en decimal (double)
        	    	double valorTotalDouble = cantidadAggEntero * precioUnitDouble;
        	    	String valorTotalFormateado = String.format("%.2f", Double.valueOf(valorTotalDouble));
        	    	valorTotal.setText(valorTotalFormateado);
    	    	}
    	   
			}catch(NumberFormatException e) { // capturo la excepción de formato
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Por favor, llena todas las casillas de cantidad y precio unitario");
				alert.show();
			}
    		
    	}});
    	
    	generarInforme.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
			    // Definir el documento PDF y la ruta de escritorio
			    Document document = new Document();
			    String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
			    
			    try {
			        // Validación de campos obligatorios
			        if (!nombreProducto.getText().trim().isEmpty() &&
			            !cantidadAgg.getText().trim().isEmpty() &&
			            !precioUnit.getText().trim().isEmpty() &&
			            !proveedor.getText().trim().isEmpty() &&
			            !ubicacion.getText().trim().isEmpty() &&
			             fechaImport.getValue() != null) {

			            // Generar un nombre único para el archivo
			            String baseName = "informe_importacion";
			            String extension = ".pdf";
			            int count = 1;
			            File file;
			            do {
			                file = new File(desktopPath + File.separator + baseName + "_" + count + extension); //Genero un archivo pdf en el escritorio del usuario
			                count++;
			            } while (file.exists());

			            // Crear el archivo PDF en la ruta generada
			            PdfWriter.getInstance(document, new FileOutputStream(file));
			            document.open();

			            // Título
			            document.add(new Paragraph("Informe de importación"));
			            document.add(new Paragraph(" "));

			            // Datos ingresados por el usuario
			            document.add(new Paragraph("Nombre del Producto: " + nombreProducto.getText()));
			            document.add(new Paragraph("Cantidad: " + cantidadAgg.getText()));
			            document.add(new Paragraph("Precio unitario: " + precioUnit.getText() + "€"));
			            document.add(new Paragraph("Valor total: " + valorTotal.getText() + "€"));
			            document.add(new Paragraph("Monto: " + (monto.getText() != null ? (monto.getText() + "€") : " ")));
			            document.add(new Paragraph("Descripción: " + (descripcion.getText() != null ? descripcion.getText() : " ")));
			            document.add(new Paragraph("Proveedor: " + proveedor.getText()));
			            document.add(new Paragraph("Teléfono: " + (telefono.getText() != null ? telefono.getText() : " ")));
			            document.add(new Paragraph("Dirección: " + (direccion.getText() != null ? direccion.getText() : " "))); 
			            document.add(new Paragraph("Fecha de Importación: " + fechaImport.getValue()));

			            // Cerrar el documento
			            document.close();

			            // Mostrar alerta de confirmación con la ruta del archivo generado
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
    	
    	eliminarProducto.setOnAction(new EventHandler<ActionEvent>(){
    		@Override
    		public void handle(ActionEvent event) {
    			
    			try {
    				
    				if(idNombreElim.getText().trim().isEmpty() 
    				   || cantidadElim.getText().trim().isEmpty()) {
    					Alert alert = new Alert(Alert.AlertType.ERROR);
    			        alert.setTitle("campos vacíos");
    			        alert.setContentText("No puede dejar campos vacíos.");
    			        alert.show();
    				}else {
    					
    				String idNombreElimString = idNombreElim.getText();
    				int idNombreElimInt = Integer.parseInt(idNombreElimString);
    				String cantidadElimString = cantidadElim.getText();
    				int cantidadElimInt = Integer.parseInt(cantidadElimString);
    				
    				DBPaginaPrincUtils.eliminarProducto(event, idNombreElimInt, cantidadElimInt);
    				}
    				
    			}catch(NumberFormatException e) {
    				e.printStackTrace();
    				
    			}
    		}

    	});
    	
    	vaciarCasillas.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				//Al pulsar botón vaciar casillas, todos pasan a tener un String vacío "".
				telefono.setText("");
				direccion.setText("");
				cantidadAgg.setText("");
				valorTotal.setText("");
				monto.setText("");
				descripcion.setText("");
				fechaImport.setValue(null);
				nombreProducto.setText("");
				precioUnit.setText("");
				proveedor.setText("");
				ubicacion.setText("");
				
			}
    		
    	});
    }
    
   
}

