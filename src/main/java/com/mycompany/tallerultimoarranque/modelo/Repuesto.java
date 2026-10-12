
package com.mycompany.tallerultimoarranque.modelo;

import java.math.BigDecimal;

public class Repuesto {

    private int id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int cantidadDisponible;

    public Repuesto() {
        this.precio = BigDecimal.ZERO;
    }

    public Repuesto(int id, String nombre, String descripcion,
                    BigDecimal precio, int cantidadDisponible) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        setPrecio(precio);
        setCantidadDisponible(cantidadDisponible);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El precio no puede ser negativo ni nulo.");
        }
        this.precio = precio;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        if (cantidadDisponible < 0) {
            throw new IllegalArgumentException(
                    "La cantidad no puede ser negativa.");
        }
        this.cantidadDisponible = cantidadDisponible;
    }

    public boolean estaAgotado() {
        return cantidadDisponible == 0;
    }

    public boolean tieneExistencia(int cantidad) {
        return cantidad > 0 && cantidad <= cantidadDisponible;
    }

    public void abastecer(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad a ingresar debe ser mayor que cero.");
        }

        this.cantidadDisponible =
                Math.addExact(this.cantidadDisponible, cantidad);
    }
    
    @Override
    public String toString() {
        return nombre + " - Disponible: " + cantidadDisponible;
    }
}
