
package com.mycompany.tallerultimoarranque.modelo;

public class Cliente extends Persona {

    private String direccion;
    private String correo;

    public Cliente() {
    }

    public Cliente(int id, String nombre, String telefono,
                   String direccion, String correo) {
        super(id, nombre, telefono);
        this.direccion = direccion;
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String mostrarInformacion() {
        return "Cliente: " + getNombre()
                + " | Teléfono: " + getTelefono()
                + " | Dirección: " + direccion
                + " | Correo: " + correo;
    }
}
