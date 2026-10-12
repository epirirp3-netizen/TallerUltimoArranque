
package com.mycompany.tallerultimoarranque.modelo;

import java.math.BigDecimal;

public class Servicio {

    private int id;
    private String descripcion;
    private BigDecimal costoManoObra;

    public Servicio() {
        this.costoManoObra = BigDecimal.ZERO;
    }

    public Servicio(int id, String descripcion,
                    BigDecimal costoManoObra) {
        this.id = id;
        setDescripcion(descripcion);
        setCostoManoObra(costoManoObra);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La descripción del servicio es obligatoria.");
        }
        this.descripcion = descripcion.trim();
    }

    public BigDecimal getCostoManoObra() {
        return costoManoObra;
    }

    public void setCostoManoObra(BigDecimal costoManoObra) {
        if (costoManoObra == null ||
                costoManoObra.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El costo de mano de obra no puede ser negativo.");
        }
        this.costoManoObra = costoManoObra;
    }

    @Override
    public String toString() {
        return descripcion + " - Q" + costoManoObra;
    }
}
