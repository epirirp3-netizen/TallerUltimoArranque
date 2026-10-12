
package com.mycompany.tallerultimoarranque.modelo;

import java.time.LocalDateTime;

public class MovimientoInventario {

    public enum TipoMovimiento {
        ABASTECIMIENTO,
        CONSUMO_ORDEN,
        DEVOLUCION_ORDEN
    }

    private int id;
    private Repuesto repuesto;
    private TipoMovimiento tipo;
    private int cantidad;
    private LocalDateTime fecha;
    private Integer idOrdenTrabajo;

    public MovimientoInventario(
            int id,
            Repuesto repuesto,
            TipoMovimiento tipo,
            int cantidad,
            Integer idOrdenTrabajo) {

        this(id, repuesto, tipo, cantidad,
                idOrdenTrabajo, LocalDateTime.now());
    }

    public MovimientoInventario(
            int id,
            Repuesto repuesto,
            TipoMovimiento tipo,
            int cantidad,
            Integer idOrdenTrabajo,
            LocalDateTime fecha) {

        if (id < 0) {
            throw new IllegalArgumentException(
                    "El ID no puede ser negativo.");
        }

        if (repuesto == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un repuesto.");
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "Debe indicar el tipo de movimiento.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria.");
        }

        if (tipo == TipoMovimiento.ABASTECIMIENTO) {

            if (idOrdenTrabajo != null) {
                throw new IllegalArgumentException(
                        "El abastecimiento no requiere una orden.");
            }

        } else {

            if (idOrdenTrabajo == null ||
                    idOrdenTrabajo <= 0) {
                throw new IllegalArgumentException(
                        "El movimiento requiere una orden válida.");
            }
        }

        this.id = id;
        this.repuesto = repuesto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.idOrdenTrabajo = idOrdenTrabajo;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public Repuesto getRepuesto() {
        return repuesto;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Integer getIdOrdenTrabajo() {
        return idOrdenTrabajo;
    }

    public int getCantidadConSigno() {

        if (tipo == TipoMovimiento.CONSUMO_ORDEN) {
            return -cantidad;
        }

        return cantidad;
    }

    @Override
    public String toString() {

        return tipo + " | "
                + repuesto.getNombre()
                + " | Cantidad: "
                + getCantidadConSigno()
                + " | Fecha: " + fecha;
    }
}
