
package com.mycompany.tallerultimoarranque.dao;

import com.mycompany.tallerultimoarranque.conexion.ConexionOracle;
import com.mycompany.tallerultimoarranque.modelo.Servicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServicioDAO {

    public ArrayList<Servicio> listar() throws SQLException {

        ArrayList<Servicio> servicios = new ArrayList<>();

        String sql = "SELECT ID_SERVICIO, DESCRIPCION, "
                + "COSTO_MANO_OBRA "
                + "FROM TALLER_SERVICIOS "
                + "ORDER BY ID_SERVICIO";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Servicio servicio = new Servicio(
                        rs.getInt("ID_SERVICIO"),
                        rs.getString("DESCRIPCION"),
                        rs.getBigDecimal("COSTO_MANO_OBRA")
                );

                servicios.add(servicio);
            }
        }

        return servicios;
    }

    public boolean guardar(Servicio servicio) throws SQLException {

        String sql = "INSERT INTO TALLER_SERVICIOS "
                + "(DESCRIPCION, COSTO_MANO_OBRA) "
                + "VALUES (?, ?)";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setString(1, servicio.getDescripcion());
            ps.setBigDecimal(2, servicio.getCostoManoObra());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean actualizar(Servicio servicio) throws SQLException {

        String sql = "UPDATE TALLER_SERVICIOS SET "
                + "DESCRIPCION = ?, "
                + "COSTO_MANO_OBRA = ? "
                + "WHERE ID_SERVICIO = ?";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setString(1, servicio.getDescripcion());
            ps.setBigDecimal(2, servicio.getCostoManoObra());
            ps.setInt(3, servicio.getId());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int id) throws SQLException {

        String sql = "DELETE FROM TALLER_SERVICIOS "
                + "WHERE ID_SERVICIO = ?";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() == 1;
        }
    }
}
