
package com.mycompany.tallerultimoarranque.modelo;

import java.math.BigDecimal;

public class DetalleRepuesto {

    private int id;
    private Repuesto repuesto;
    private int cantidad;
    private BigDecimal precioUnitario;

    public DetalleRepuesto() {
        this.precioUnitario = BigDecimal.ZERO;
    }

    public DetalleRepuesto(int id, Repuesto repuesto,
                           int cantidad) {
        setId(id);
        setRepuesto(repuesto);
        setCantidad(cantidad);
        setPrecioUnitario(repuesto.getPrecio());
    }

    public DetalleRepuesto(int id, Repuesto repuesto,
                           int cantidad, BigDecimal precioUnitario) {
        setId(id);
        setRepuesto(repuesto);
        setCantidad(cantidad);
        setPrecioUnitario(precioUnitario);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException(
                    "El identificador no puede ser negativo.");
        }
        this.id = id;
    }

    public Repuesto getRepuesto() {
        return repuesto;
    }

    public void setRepuesto(Repuesto repuesto) {
        if (repuesto == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un repuesto.");
        }
        this.repuesto = repuesto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario == null ||
                precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El precio no puede ser negativo.");
        }
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(
                BigDecimal.valueOf(cantidad));
    }

    @Override
    public String toString() {
        return repuesto.getNombre()
                + " | Cantidad: " + cantidad
                + " | Subtotal: Q" + getSubtotal();
    }
}
