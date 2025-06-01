package com.lightmanagement.app.model;

import java.sql.Date;

public class ProductoInventario {
	private int idProducto;
	private String nombreProducto;
	private int cantidad;
	private double precioUnitario;
	private double valorTotal;
	private Date fechaIncorporacion;
	private String proveedor;
	private String ubicacion;
	
	public ProductoInventario(int idProducto, String nombreProducto, int cantidad,
			double precioUnitario, double valorTotal, Date fechaIncorporacion, String proveedor, String ubicacion) {
		this.idProducto = idProducto;
		this.cantidad = cantidad;
		this.fechaIncorporacion = fechaIncorporacion;
		this.nombreProducto = nombreProducto;
		this.precioUnitario = precioUnitario;
		this.proveedor = proveedor;
		this.valorTotal = valorTotal;
		this.ubicacion = ubicacion;
	}
	
	public int getIdProducto() {
		return idProducto;
	}
	public int getCantidad() {
		return cantidad;
	}
	public Date getFechaIncorporacion() {
		return fechaIncorporacion;
	}
	public String getNombreProducto() {
		return nombreProducto;
	}
	public double getPrecioUnitario() {
		return precioUnitario;
	}
	public String getProveedor() {
		return proveedor;
	}
	public double getValorTotal() {
		return valorTotal;
	}
	public String getUbicacion() {
		return ubicacion;
	}
}
