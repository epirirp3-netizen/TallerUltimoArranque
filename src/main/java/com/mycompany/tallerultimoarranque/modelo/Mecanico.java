
package com.mycompany.tallerultimoarranque.modelo;

public class Mecanico extends Persona {

    private String especialidad;

    public Mecanico() {
    }

    public Mecanico(int id, String nombre, String telefono,
                    String especialidad) {
        super(id, nombre, telefono);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String mostrarInformacion() {
        return "Mecánico: " + getNombre()
                + " | Teléfono: " + getTelefono()
                + " | Especialidad: " + especialidad;
    }
}
