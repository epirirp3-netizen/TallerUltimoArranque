
package com.mycompany.tallerultimoarranque.conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class PruebaConexion {

    public static void main(String[] args) {

        String sql = "SELECT ID_CLIENTE, NOMBRE, TELEFONO "
                + "FROM TALLER_CLIENTES ORDER BY ID_CLIENTE";

        try (
            Connection conexion = ConexionOracle.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            System.out.println("CONEXION CORRECTA CON ORACLE");
            System.out.println("-----------------------------");

            int cantidad = 0;

            while (rs.next()) {

                int id = rs.getInt("ID_CLIENTE");
                String nombre = rs.getString("NOMBRE");
                String telefono = rs.getString("TELEFONO");

                System.out.println("ID: " + id);
                System.out.println("Nombre: " + nombre);
                System.out.println("Telefono: " + telefono);
                System.out.println("-----------------------------");

                cantidad++;
            }

            System.out.println("Total de clientes: " + cantidad);

            JOptionPane.showMessageDialog(
                    null,
                    "Conexion correcta con Oracle.\n"
                    + "Clientes encontrados: " + cantidad,
                    "Prueba de conexion",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al conectar o consultar Oracle:\n"
                    + e.getMessage(),
                    "Error de conexion",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
