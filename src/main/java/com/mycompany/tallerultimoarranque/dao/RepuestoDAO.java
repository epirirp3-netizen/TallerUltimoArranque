
package com.mycompany.tallerultimoarranque.dao;

import com.mycompany.tallerultimoarranque.conexion.ConexionOracle;
import com.mycompany.tallerultimoarranque.modelo.Repuesto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RepuestoDAO {

    public ArrayList<Repuesto> listar() throws SQLException {

        ArrayList<Repuesto> repuestos = new ArrayList<>();

        String sql = "SELECT ID_REPUESTO, NOMBRE, "
                + "DESCRIPCION, PRECIO, CANTIDAD_DISPONIBLE "
                + "FROM TALLER_REPUESTOS "
                + "ORDER BY ID_REPUESTO";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Repuesto repuesto = new Repuesto(
                        rs.getInt("ID_REPUESTO"),
                        rs.getString("NOMBRE"),
                        rs.getString("DESCRIPCION"),
                        rs.getBigDecimal("PRECIO"),
                        rs.getInt("CANTIDAD_DISPONIBLE")
                );

                repuestos.add(repuesto);
            }
        }

        return repuestos;
    }

    public boolean guardar(Repuesto repuesto) throws SQLException {

        String sql = "INSERT INTO TALLER_REPUESTOS "
                + "(NOMBRE, DESCRIPCION, PRECIO, "
                + "CANTIDAD_DISPONIBLE) "
                + "VALUES (?, ?, ?, ?)";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setString(1, repuesto.getNombre());
            ps.setString(2, repuesto.getDescripcion());
            ps.setBigDecimal(3, repuesto.getPrecio());
            ps.setInt(4, repuesto.getCantidadDisponible());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean actualizar(Repuesto repuesto) throws SQLException {

        String sql = "UPDATE TALLER_REPUESTOS SET "
                + "NOMBRE = ?, "
                + "DESCRIPCION = ?, "
                + "PRECIO = ?, "
                + "CANTIDAD_DISPONIBLE = ? "
                + "WHERE ID_REPUESTO = ?";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setString(1, repuesto.getNombre());
            ps.setString(2, repuesto.getDescripcion());
            ps.setBigDecimal(3, repuesto.getPrecio());
            ps.setInt(4, repuesto.getCantidadDisponible());
            ps.setInt(5, repuesto.getId());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM TALLER_REPUESTOS "
                + "WHERE ID_REPUESTO = ?";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() == 1;
        }
    }
}
