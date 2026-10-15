package com.mycompany.tallerultimoarranque.dao;

import com.mycompany.tallerultimoarranque.conexion.ConexionOracle;
import com.mycompany.tallerultimoarranque.modelo.OrdenTrabajo;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrdenTrabajoDAO {

    public record ResumenOrden(int id, int idVehiculo, int idMecanico,
            String vehiculo, String mecanico, String descripcionProblema,
            String estado, LocalDateTime fechaIngreso) {

    }

    public record ServicioOrden(int idDetalle, String descripcion,
            BigDecimal costo) {

    }

    public record RepuestoOrden(int idDetalle, String nombre, int cantidad,
            BigDecimal precioUnitario, BigDecimal subtotal) {

    }

    public record Totales(BigDecimal manoObra, BigDecimal repuestos,
            BigDecimal total) {

    }

    @FunctionalInterface
    private interface Trabajo<T> {

        T ejecutar(Connection conexion) throws SQLException;
    }

    private <T> T transaccion(Trabajo<T> trabajo) throws SQLException {
        try (Connection conexion = ConexionOracle.conectar()) {
            conexion.setAutoCommit(false);
            try {
                T resultado = trabajo.ejecutar(conexion);
                conexion.commit();
                return resultado;
            } catch (SQLException | RuntimeException e) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
                throw e;
            }
        }
    }

    private String bloquearOrden(Connection conexion, int id) throws SQLException {
        if (id <= 0) {
            throw new SQLException("Seleccione una orden válida.");
        }
        String sql = "SELECT ESTADO FROM TALLER_ORDENES WHERE ID_ORDEN = ? FOR UPDATE";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("La orden no existe.");
                }
                return rs.getString("ESTADO");
            }
        }
    }

    private void exigirEstado(String actual, String esperado) throws SQLException {
        if (!esperado.equals(actual)) {
            throw new SQLException("Operación no permitida: estado actual " + actual
                    + "; debe estar " + esperado + ".");
        }
    }

    public boolean crear(OrdenTrabajo orden) throws SQLException {
        if (orden == null || orden.getVehiculo() == null || orden.getMecanico() == null
                || orden.getDescripcionProblema() == null
                || orden.getDescripcionProblema().isBlank()
                || orden.getDescripcionProblema().length() > 500
                || orden.getEstado() != OrdenTrabajo.Estado.PENDIENTE) {
            throw new IllegalArgumentException("La nueva orden debe ser válida y estar PENDIENTE.");
        }
        String sql = "INSERT INTO TALLER_ORDENES "
                + "(ID_VEHICULO, ID_MECANICO, DESCRIPCION_PROBLEMA) VALUES (?, ?, ?)";
        try (Connection c = ConexionOracle.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, orden.getVehiculo().getId());
            ps.setInt(2, orden.getMecanico().getId());
            ps.setString(3, orden.getDescripcionProblema());
            return ps.executeUpdate() == 1;
        }
    }

    public ArrayList<ResumenOrden> listar() throws SQLException {
        ArrayList<ResumenOrden> resultado = new ArrayList<>();
        String sql = "SELECT O.ID_ORDEN, O.ID_VEHICULO, O.ID_MECANICO, "
                + "V.PLACA, V.MARCA, V.MODELO, M.NOMBRE AS NOMBRE_MECANICO, "
                + "O.DESCRIPCION_PROBLEMA, O.ESTADO, O.FECHA_INGRESO "
                + "FROM TALLER_ORDENES O "
                + "JOIN TALLER_VEHICULOS V ON O.ID_VEHICULO = V.ID_VEHICULO "
                + "JOIN TALLER_MECANICOS M ON O.ID_MECANICO = M.ID_MECANICO "
                + "ORDER BY O.ID_ORDEN DESC";
        try (Connection c = ConexionOracle.conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp fecha = rs.getTimestamp("FECHA_INGRESO");
                resultado.add(new ResumenOrden(
                        rs.getInt("ID_ORDEN"), rs.getInt("ID_VEHICULO"),
                        rs.getInt("ID_MECANICO"),
                        rs.getString("PLACA") + " - " + rs.getString("MARCA")
                        + " " + rs.getString("MODELO"),
                        rs.getString("NOMBRE_MECANICO"),
                        rs.getString("DESCRIPCION_PROBLEMA"),
                        rs.getString("ESTADO"),
                        fecha == null ? null : fecha.toLocalDateTime()));
            }
        }
        return resultado;
    }

    public void actualizarDatos(int id, int idVehiculo, int idMecanico,
            String descripcion) throws SQLException {
        if (idVehiculo <= 0 || idMecanico <= 0 || descripcion == null
                || descripcion.isBlank() || descripcion.trim().length() > 500) {
            throw new IllegalArgumentException("Complete vehículo, mecánico y descripción (máximo 500 caracteres).");
        }
        transaccion(c -> {
            String estado = bloquearOrden(c, id);
            if (!"PENDIENTE".equals(estado) && !"EN_PROCESO".equals(estado)) {
                throw new SQLException("No se puede modificar una orden finalizada o cancelada.");
            }
            String sql = "UPDATE TALLER_ORDENES SET ID_VEHICULO = ?, "
                    + "ID_MECANICO = ?, DESCRIPCION_PROBLEMA = ? WHERE ID_ORDEN = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idVehiculo);
                ps.setInt(2, idMecanico);
                ps.setString(3, descripcion.trim());
                ps.setInt(4, id);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("No se pudo actualizar la orden.");
                }
            }
            return null;
        });
    }

    public void iniciarReparacion(int id) throws SQLException {
        transaccion(c -> {
            exigirEstado(bloquearOrden(c, id), "PENDIENTE");
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE TALLER_ORDENES SET ESTADO = 'EN_PROCESO' WHERE ID_ORDEN = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            return null;
        });
    }

    public String obtenerEstado(int id) throws SQLException {
        try (Connection c = ConexionOracle.conectar(); PreparedStatement ps = c.prepareStatement(
                "SELECT ESTADO FROM TALLER_ORDENES WHERE ID_ORDEN = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Orden no encontrada.");
                }
                return rs.getString(1);
            }
        }
    }

    public ArrayList<ServicioOrden> listarServicios(int idOrden) throws SQLException {
        ArrayList<ServicioOrden> resultado = new ArrayList<>();
        String sql = "SELECT D.ID_DETALLE_SERVICIO, S.DESCRIPCION, D.COSTO_MANO_OBRA "
                + "FROM TALLER_DETALLE_SERVICIOS D "
                + "JOIN TALLER_SERVICIOS S ON S.ID_SERVICIO = D.ID_SERVICIO "
                + "WHERE D.ID_ORDEN = ? ORDER BY D.ID_DETALLE_SERVICIO";
        try (Connection c = ConexionOracle.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(new ServicioOrden(rs.getInt(1),
                            rs.getString(2), rs.getBigDecimal(3)));
                }
            }
        }
        return resultado;
    }

    public ArrayList<RepuestoOrden> listarRepuestos(int idOrden) throws SQLException {
        ArrayList<RepuestoOrden> resultado = new ArrayList<>();
        String sql = "SELECT D.ID_DETALLE_REPUESTO, R.NOMBRE, D.CANTIDAD, "
                + "D.PRECIO_UNITARIO, D.SUBTOTAL "
                + "FROM TALLER_DETALLE_REPUESTOS D "
                + "JOIN TALLER_REPUESTOS R ON R.ID_REPUESTO = D.ID_REPUESTO "
                + "WHERE D.ID_ORDEN = ? ORDER BY D.ID_DETALLE_REPUESTO";
        try (Connection c = ConexionOracle.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(new RepuestoOrden(rs.getInt(1),
                            rs.getString(2), rs.getInt(3),
                            rs.getBigDecimal(4), rs.getBigDecimal(5)));
                }
            }
        }
        return resultado;
    }

    public Totales obtenerTotales(int idOrden) throws SQLException {
        String sql = "SELECT TOTAL_MANO_OBRA, TOTAL_REPUESTOS, COSTO_TOTAL "
                + "FROM TALLER_ORDENES WHERE ID_ORDEN = ?";
        try (Connection c = ConexionOracle.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Orden no encontrada.");
                }
                return new Totales(rs.getBigDecimal(1),
                        rs.getBigDecimal(2), rs.getBigDecimal(3));
            }
        }
    }

    private void recalcularTotales(Connection c, int idOrden) throws SQLException {
        BigDecimal mano = BigDecimal.ZERO;
        BigDecimal repuestos = BigDecimal.ZERO;
        String sqlMano = "SELECT NVL(SUM(COSTO_MANO_OBRA),0) "
                + "FROM TALLER_DETALLE_SERVICIOS WHERE ID_ORDEN = ?";
        String sqlRepuestos = "SELECT NVL(SUM(SUBTOTAL),0) "
                + "FROM TALLER_DETALLE_REPUESTOS WHERE ID_ORDEN = ?";
        try (PreparedStatement ps = c.prepareStatement(sqlMano)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    mano = rs.getBigDecimal(1);
                }
            }
        }
        try (PreparedStatement ps = c.prepareStatement(sqlRepuestos)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    repuestos = rs.getBigDecimal(1);
                }
            }
        }
        String update = "UPDATE TALLER_ORDENES SET TOTAL_MANO_OBRA = ?, "
                + "TOTAL_REPUESTOS = ?, COSTO_TOTAL = ? WHERE ID_ORDEN = ?";
        try (PreparedStatement ps = c.prepareStatement(update)) {
            ps.setBigDecimal(1, mano);
            ps.setBigDecimal(2, repuestos);
            ps.setBigDecimal(3, mano.add(repuestos));
            ps.setInt(4, idOrden);
            ps.executeUpdate();
        }
    }

    public void agregarServicio(int idOrden, int idServicio) throws SQLException {
        if (idServicio <= 0) {
            throw new IllegalArgumentException("Seleccione un servicio válido.");
        }
        transaccion(c -> {
            exigirEstado(bloquearOrden(c, idOrden), "EN_PROCESO");
            BigDecimal costo;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COSTO_MANO_OBRA FROM TALLER_SERVICIOS WHERE ID_SERVICIO = ?")) {
                ps.setInt(1, idServicio);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("El servicio no existe.");
                    }
                    costo = rs.getBigDecimal(1);
                }
            }
            String sql = "INSERT INTO TALLER_DETALLE_SERVICIOS "
                    + "(ID_ORDEN, ID_SERVICIO, COSTO_MANO_OBRA) VALUES (?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idOrden);
                ps.setInt(2, idServicio);
                ps.setBigDecimal(3, costo);
                ps.executeUpdate();
            }
            recalcularTotales(c, idOrden);
            return null;
        });
    }

    public void quitarServicio(int idOrden, int idDetalle) throws SQLException {
        transaccion(c -> {
            exigirEstado(bloquearOrden(c, idOrden), "EN_PROCESO");
            String sql = "DELETE FROM TALLER_DETALLE_SERVICIOS "
                    + "WHERE ID_ORDEN = ? AND ID_DETALLE_SERVICIO = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idOrden);
                ps.setInt(2, idDetalle);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("Seleccione un servicio agregado a la orden.");
                }
            }
            recalcularTotales(c, idOrden);
            return null;
        });
    }

    private void registrarMovimiento(Connection c, int idRepuesto,
            String tipo, int cantidad, int idOrden) throws SQLException {
        String sql = "INSERT INTO TALLER_MOVIMIENTOS "
                + "(ID_REPUESTO, TIPO, CANTIDAD, ID_ORDEN) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idRepuesto);
            ps.setString(2, tipo);
            ps.setInt(3, cantidad);
            ps.setInt(4, idOrden);
            ps.executeUpdate();
        }
    }

    private void devolverExistencia(Connection c, int idRepuesto, int cantidad,
            int idOrden) throws SQLException {
        String sql = "UPDATE TALLER_REPUESTOS "
                + "SET CANTIDAD_DISPONIBLE = CANTIDAD_DISPONIBLE + ? "
                + "WHERE ID_REPUESTO = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idRepuesto);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("El repuesto ya no existe.");
            }
        }
        registrarMovimiento(c, idRepuesto, "DEVOLUCION_ORDEN", cantidad, idOrden);
    }

    public void agregarRepuesto(int idOrden, int idRepuesto, int cantidad) throws SQLException {
        if (idRepuesto <= 0 || cantidad <= 0) {
            throw new IllegalArgumentException("Seleccione un repuesto y una cantidad mayor que cero.");
        }
        transaccion(c -> {
            exigirEstado(bloquearOrden(c, idOrden), "EN_PROCESO");
            BigDecimal precio;
            int disponible;
            String obtener = "SELECT PRECIO, CANTIDAD_DISPONIBLE "
                    + "FROM TALLER_REPUESTOS WHERE ID_REPUESTO = ? FOR UPDATE";
            try (PreparedStatement ps = c.prepareStatement(obtener)) {
                ps.setInt(1, idRepuesto);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("El repuesto no existe.");
                    }
                    precio = rs.getBigDecimal(1);
                    disponible = rs.getInt(2);
                }
            }
            if (disponible < cantidad) {
                throw new SQLException("Stock insuficiente. Disponible: " + disponible);
            }
            String descuento = "UPDATE TALLER_REPUESTOS SET "
                    + "CANTIDAD_DISPONIBLE = CANTIDAD_DISPONIBLE - ? "
                    + "WHERE ID_REPUESTO = ? AND CANTIDAD_DISPONIBLE >= ?";
            try (PreparedStatement ps = c.prepareStatement(descuento)) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idRepuesto);
                ps.setInt(3, cantidad);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("Stock insuficiente.");
                }
            }
            String sql = "INSERT INTO TALLER_DETALLE_REPUESTOS "
                    + "(ID_ORDEN, ID_REPUESTO, CANTIDAD, PRECIO_UNITARIO, SUBTOTAL) "
                    + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idOrden);
                ps.setInt(2, idRepuesto);
                ps.setInt(3, cantidad);
                ps.setBigDecimal(4, precio);
                ps.setBigDecimal(5, precio.multiply(BigDecimal.valueOf(cantidad)));
                ps.executeUpdate();
            }
            registrarMovimiento(c, idRepuesto, "CONSUMO_ORDEN", cantidad, idOrden);
            recalcularTotales(c, idOrden);
            return null;
        });
    }

    public void quitarRepuesto(int idOrden, int idDetalle) throws SQLException {
        transaccion(c -> {
            exigirEstado(bloquearOrden(c, idOrden), "EN_PROCESO");
            int repuesto;
            int cantidad;
            String obtener = "SELECT ID_REPUESTO, CANTIDAD FROM "
                    + "TALLER_DETALLE_REPUESTOS WHERE ID_ORDEN = ? "
                    + "AND ID_DETALLE_REPUESTO = ? FOR UPDATE";
            try (PreparedStatement ps = c.prepareStatement(obtener)) {
                ps.setInt(1, idOrden);
                ps.setInt(2, idDetalle);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Seleccione un repuesto agregado a la orden.");
                    }
                    repuesto = rs.getInt(1);
                    cantidad = rs.getInt(2);
                }
            }
            String borrar = "DELETE FROM TALLER_DETALLE_REPUESTOS "
                    + "WHERE ID_ORDEN = ? AND ID_DETALLE_REPUESTO = ?";
            try (PreparedStatement ps = c.prepareStatement(borrar)) {
                ps.setInt(1, idOrden);
                ps.setInt(2, idDetalle);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("No se pudo retirar el repuesto.");
                }
            }
            devolverExistencia(c, repuesto, cantidad, idOrden);
            recalcularTotales(c, idOrden);
            return null;
        });
    }

    public void finalizarOrden(int idOrden) throws SQLException {
        transaccion(c -> {
            exigirEstado(bloquearOrden(c, idOrden), "EN_PROCESO");
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*) FROM TALLER_DETALLE_SERVICIOS WHERE ID_ORDEN = ?")) {
                ps.setInt(1, idOrden);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        throw new SQLException("No se puede finalizar sin al menos un servicio.");
                    }
                }
            }
            recalcularTotales(c, idOrden);
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE TALLER_ORDENES SET ESTADO = 'FINALIZADA', "
                    + "FECHA_FINALIZACION = LOCALTIMESTAMP WHERE ID_ORDEN = ?")) {
                ps.setInt(1, idOrden);
                ps.executeUpdate();
            }
            return null;
        });
    }

    public void cancelarOrden(int idOrden) throws SQLException {
        transaccion(c -> {
            String estado = bloquearOrden(c, idOrden);
            if (!"PENDIENTE".equals(estado) && !"EN_PROCESO".equals(estado)) {
                throw new SQLException("Solo se pueden cancelar órdenes pendientes o en proceso.");
            }
            if ("EN_PROCESO".equals(estado)) {
                String sql = "SELECT ID_REPUESTO, CANTIDAD "
                        + "FROM TALLER_DETALLE_REPUESTOS "
                        + "WHERE ID_ORDEN = ? ORDER BY ID_REPUESTO FOR UPDATE";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, idOrden);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            devolverExistencia(c, rs.getInt(1), rs.getInt(2), idOrden);
                        }
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE TALLER_ORDENES SET ESTADO = 'CANCELADA' WHERE ID_ORDEN = ?")) {
                ps.setInt(1, idOrden);
                ps.executeUpdate();
            }
            return null;
        });
    }
}
