
package com.mycompany.tallerultimoarranque.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class ConexionOracle {

    private static final String URL =
            "jdbc:oracle:thin:@//localhost:1521/xepdb1";

    private static final String USUARIO = "TALLER_APP";

    private static String contrasenaSesion;

    public static Connection conectar() throws SQLException {

        if (contrasenaSesion == null) {

            JPasswordField campo = new JPasswordField(20);

            int opcion = JOptionPane.showConfirmDialog(
                    null,
                    campo,
                    "Contraseña Oracle - TALLER_APP",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcion != JOptionPane.OK_OPTION) {
                throw new SQLException(
                        "Se canceló la conexión a Oracle.");
            }

            char[] clave = campo.getPassword();

            try {
                if (clave.length == 0) {
                    throw new SQLException(
                            "La contraseña es obligatoria.");
                }

                contrasenaSesion = new String(clave);

            } finally {
                Arrays.fill(clave, '\0');
                campo.setText("");
            }
        }

        try {
            return DriverManager.getConnection(
                    URL,
                    USUARIO,
                    contrasenaSesion
            );

        } catch (SQLException e) {
            contrasenaSesion = null;
            throw e;
        }
    }   
}
