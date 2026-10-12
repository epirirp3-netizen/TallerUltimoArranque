
package com.mycompany.tallerultimoarranque.modelo;

import java.math.BigDecimal;

public class DetalleServicio {

    private int id;
    private Servicio servicio;
    private BigDecimal costoManoObra;

    public DetalleServicio() {
        this.costoManoObra = BigDecimal.ZERO;
    }

    public DetalleServicio(int id, Servicio servicio) {
        setId(id);
        setServicio(servicio);
        setCostoManoObra(servicio.getCostoManoObra());
    }

    public DetalleServicio(int id, Servicio servicio,
                           BigDecimal costoManoObra) {
        setId(id);
        setServicio(servicio);
        setCostoManoObra(costoManoObra);
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

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un servicio.");
        }
        this.servicio = servicio;
    }

    public BigDecimal getCostoManoObra() {
        return costoManoObra;
    }

    public void setCostoManoObra(BigDecimal costoManoObra) {
        if (costoManoObra == null ||
                costoManoObra.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El costo no puede ser negativo.");
        }
        this.costoManoObra = costoManoObra;
    }

    public BigDecimal getSubtotal() {
        return costoManoObra;
    }

    @Override
    public String toString() {
        return servicio.getDescripcion()
                + " - Q" + costoManoObra;
    }
}
