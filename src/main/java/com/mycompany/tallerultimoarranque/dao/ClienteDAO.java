package com.mycompany.tallerultimoarranque.dao;

import com.mycompany.tallerultimoarranque.conexion.ConexionOracle;
import com.mycompany.tallerultimoarranque.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClienteDAO {

    public ArrayList<Cliente> listar() throws SQLException {

        ArrayList<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT ID_CLIENTE, NOMBRE, TELEFONO, "
                + "DIRECCION, CORREO "
                + "FROM TALLER_CLIENTES "
                + "ORDER BY ID_CLIENTE";

        try (
                Connection conexion = ConexionOracle.conectar(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("ID_CLIENTE");
                String nombre = rs.getString("NOMBRE");
                String telefono = rs.getString("TELEFONO");
                String direccion = rs.getString("DIRECCION");
                String correo = rs.getString("CORREO");

                Cliente cliente = new Cliente(
                        id,
                        nombre,
                        telefono,
                        direccion,
                        correo
                );

                clientes.add(cliente);
            }
        }

        return clientes;
    }

    public boolean guardar(Cliente cliente) throws SQLException {

        String sql = "INSERT INTO TALLER_CLIENTES "
                + "(NOMBRE, TELEFONO, DIRECCION, CORREO) "
                + "VALUES (?, ?, ?, ?)";

        try (
                Connection conexion = ConexionOracle.conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getDireccion());
            ps.setString(4, cliente.getCorreo());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean actualizar(Cliente cliente) throws SQLException {

        String sql = "UPDATE TALLER_CLIENTES SET "
                + "NOMBRE = ?, "
                + "TELEFONO = ?, "
                + "DIRECCION = ?, "
                + "CORREO = ? "
                + "WHERE ID_CLIENTE = ?";

        try (
                Connection conexion = ConexionOracle.conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getDireccion());
            ps.setString(4, cliente.getCorreo());
            ps.setInt(5, cliente.getId());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM TALLER_CLIENTES "
                + "WHERE ID_CLIENTE = ?";

        try (
                Connection conexion = ConexionOracle.conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() == 1;
        }
    }

}
