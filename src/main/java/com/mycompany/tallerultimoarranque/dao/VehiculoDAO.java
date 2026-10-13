
package com.mycompany.tallerultimoarranque.dao;

import com.mycompany.tallerultimoarranque.conexion.ConexionOracle;
import com.mycompany.tallerultimoarranque.modelo.Cliente;
import com.mycompany.tallerultimoarranque.modelo.Vehiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VehiculoDAO {

    public ArrayList<Vehiculo> listar() throws SQLException {

        ArrayList<Vehiculo> vehiculos = new ArrayList<>();

        String sql = "SELECT "
                + "V.ID_VEHICULO, V.PLACA, V.MARCA, "
                + "V.MODELO, V.ANIO, "
                + "C.ID_CLIENTE, C.NOMBRE, C.TELEFONO, "
                + "C.DIRECCION, C.CORREO "
                + "FROM TALLER_VEHICULOS V "
                + "INNER JOIN TALLER_CLIENTES C "
                + "ON V.ID_CLIENTE = C.ID_CLIENTE "
                + "ORDER BY V.ID_VEHICULO";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Cliente cliente = new Cliente(
                        rs.getInt("ID_CLIENTE"),
                        rs.getString("NOMBRE"),
                        rs.getString("TELEFONO"),
                        rs.getString("DIRECCION"),
                        rs.getString("CORREO")
                );

                Vehiculo vehiculo = new Vehiculo(
                        rs.getInt("ID_VEHICULO"),
                        rs.getString("PLACA"),
                        rs.getString("MARCA"),
                        rs.getString("MODELO"),
                        rs.getInt("ANIO"),
                        cliente
                );

                vehiculos.add(vehiculo);
            }
        }

        return vehiculos;
    }

    public boolean guardar(Vehiculo vehiculo) throws SQLException {

        String sql = "INSERT INTO TALLER_VEHICULOS "
                + "(PLACA, MARCA, MODELO, ANIO, ID_CLIENTE) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setString(1, vehiculo.getPlaca());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setInt(4, vehiculo.getAnio());
            ps.setInt(5, vehiculo.getCliente().getId());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean actualizar(Vehiculo vehiculo) throws SQLException {

        String sql = "UPDATE TALLER_VEHICULOS SET "
                + "PLACA = ?, "
                + "MARCA = ?, "
                + "MODELO = ?, "
                + "ANIO = ?, "
                + "ID_CLIENTE = ? "
                + "WHERE ID_VEHICULO = ?";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setString(1, vehiculo.getPlaca());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setInt(4, vehiculo.getAnio());
            ps.setInt(5, vehiculo.getCliente().getId());
            ps.setInt(6, vehiculo.getId());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM TALLER_VEHICULOS "
                + "WHERE ID_VEHICULO = ?";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() == 1;
        }
    }
}
