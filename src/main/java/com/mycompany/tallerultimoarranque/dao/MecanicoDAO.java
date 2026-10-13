
package com.mycompany.tallerultimoarranque.dao;

import com.mycompany.tallerultimoarranque.conexion.ConexionOracle;
import com.mycompany.tallerultimoarranque.modelo.Mecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MecanicoDAO {

    public ArrayList<Mecanico> listar() throws SQLException {

        ArrayList<Mecanico> mecanicos = new ArrayList<>();

        String sql = "SELECT ID_MECANICO, NOMBRE, "
                + "TELEFONO, ESPECIALIDAD "
                + "FROM TALLER_MECANICOS "
                + "ORDER BY ID_MECANICO";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Mecanico mecanico = new Mecanico(
                        rs.getInt("ID_MECANICO"),
                        rs.getString("NOMBRE"),
                        rs.getString("TELEFONO"),
                        rs.getString("ESPECIALIDAD")
                );

                mecanicos.add(mecanico);
            }
        }

        return mecanicos;
    }

    public boolean guardar(Mecanico mecanico) throws SQLException {

        String sql = "INSERT INTO TALLER_MECANICOS "
                + "(NOMBRE, TELEFONO, ESPECIALIDAD) "
                + "VALUES (?, ?, ?)";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setString(1, mecanico.getNombre());
            ps.setString(2, mecanico.getTelefono());
            ps.setString(3, mecanico.getEspecialidad());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean actualizar(Mecanico mecanico) throws SQLException {

        String sql = "UPDATE TALLER_MECANICOS SET "
                + "NOMBRE = ?, "
                + "TELEFONO = ?, "
                + "ESPECIALIDAD = ? "
                + "WHERE ID_MECANICO = ?";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setString(1, mecanico.getNombre());
            ps.setString(2, mecanico.getTelefono());
            ps.setString(3, mecanico.getEspecialidad());
            ps.setInt(4, mecanico.getId());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM TALLER_MECANICOS "
                + "WHERE ID_MECANICO = ?";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() == 1;
        }
    }
}
