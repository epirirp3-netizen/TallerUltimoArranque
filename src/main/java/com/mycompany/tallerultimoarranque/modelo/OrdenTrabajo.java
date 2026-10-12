
package com.mycompany.tallerultimoarranque.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrdenTrabajo {

    public enum Estado {
        PENDIENTE,
        EN_PROCESO,
        FINALIZADA,
        CANCELADA
    }

    private int id;
    private Vehiculo vehiculo;
    private Mecanico mecanico;
    private String descripcionProblema;
    private Estado estado;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaFinalizacion;

    private final ArrayList<DetalleServicio> servicios;
    private final ArrayList<DetalleRepuesto> repuestos;

    public OrdenTrabajo(int id, Vehiculo vehiculo,
                        Mecanico mecanico, String descripcionProblema) {

        if (id < 0) {
            throw new IllegalArgumentException(
                    "El identificador no puede ser negativo.");
        }

        this.id = id;
        setVehiculo(vehiculo);
        setMecanico(mecanico);
        setDescripcionProblema(descripcionProblema);

        this.estado = Estado.PENDIENTE;
        this.fechaIngreso = LocalDateTime.now();

        this.servicios = new ArrayList<>();
        this.repuestos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un vehículo.");
        }

        this.vehiculo = vehiculo;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public void setMecanico(Mecanico mecanico) {
        if (mecanico == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un mecánico.");
        }

        this.mecanico = mecanico;
    }

    public String getDescripcionProblema() {
        return descripcionProblema;
    }

    public void setDescripcionProblema(String descripcionProblema) {
        if (descripcionProblema == null ||
                descripcionProblema.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe ingresar la descripción del problema.");
        }

        this.descripcionProblema = descripcionProblema.trim();
    }

    public Estado getEstado() {
        return estado;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public LocalDateTime getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public ArrayList<DetalleServicio> getServicios() {
        return new ArrayList<>(servicios);
    }

    public ArrayList<DetalleRepuesto> getRepuestos() {
        return new ArrayList<>(repuestos);
    }

    public void iniciarReparacion() {
        if (estado != Estado.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden iniciar órdenes pendientes.");
        }

        estado = Estado.EN_PROCESO;
    }

    public void agregarServicio(DetalleServicio detalle) {
        validarEnProceso();

        if (detalle == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un servicio.");
        }

        servicios.add(detalle);
    }

    public void agregarRepuesto(DetalleRepuesto detalle) {
        validarEnProceso();

        if (detalle == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un repuesto.");
        }

        repuestos.add(detalle);
    }

    public void eliminarRepuesto(DetalleRepuesto detalle) {
        validarEnProceso();
        repuestos.remove(detalle);
    }

    public BigDecimal calcularManoObra() {
        BigDecimal total = BigDecimal.ZERO;

        for (DetalleServicio detalle : servicios) {
            total = total.add(detalle.getSubtotal());
        }

        return total;
    }

    public BigDecimal calcularRepuestos() {
        BigDecimal total = BigDecimal.ZERO;

        for (DetalleRepuesto detalle : repuestos) {
            total = total.add(detalle.getSubtotal());
        }

        return total;
    }

    public BigDecimal calcularTotal() {
        return calcularManoObra().add(calcularRepuestos());
    }

    public void finalizarOrden() {
        validarEnProceso();

        if (servicios.isEmpty()) {
            throw new IllegalStateException(
                    "No se puede finalizar una orden sin servicios.");
        }

        estado = Estado.FINALIZADA;
        fechaFinalizacion = LocalDateTime.now();
    }

    public void cancelarOrden() {
        if (estado == Estado.FINALIZADA ||
                estado == Estado.CANCELADA) {
            throw new IllegalStateException(
                    "Esta orden no se puede cancelar.");
        }

        estado = Estado.CANCELADA;
    }

    private void validarEnProceso() {
        if (estado != Estado.EN_PROCESO) {
            throw new IllegalStateException(
                    "La orden debe estar EN PROCESO.");
        }
    }

    @Override
    public String toString() {
        return "Orden #" + id + " - "
                + estado.toString().replace("_", " ");
    }
}
