package com.mycompany.tallerultimoarranque.vista;

import com.mycompany.tallerultimoarranque.dao.MecanicoDAO;
import com.mycompany.tallerultimoarranque.modelo.Mecanico;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.mycompany.tallerultimoarranque.dao.RepuestoDAO;
import com.mycompany.tallerultimoarranque.modelo.Repuesto;

import java.math.BigDecimal;

public class FrmRepuestos extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmRepuestos.class.getName());

    public FrmRepuestos() {

        initComponents();

    setLocationRelativeTo(null);
    txtId.setEditable(false);

    txtBuscar.setText("");
    txtBuscar.setToolTipText(
            "Buscar repuesto por nombre o descripción"
    );

    cargarRepuestos();
    configurarBusqueda();
    }

    private Repuesto obtenerRepuestoFormulario(boolean requiereId) {

        int id = 0;

        if (requiereId) {

            if (txtId.getText().trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Debe seleccionar un repuesto de la tabla."
                );
            }

            try {
                id = Integer.parseInt(txtId.getText().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "El ID del repuesto no es válido."
                );
            }
        }

        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String textoPrecio = txtPrecio.getText().trim();
        String textoCantidad = txtCantidadDisponible.getText().trim();

        if (nombre.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe ingresar el nombre del repuesto."
            );
        }

        if (textoPrecio.isEmpty() || textoCantidad.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe ingresar el precio y la cantidad disponible."
            );
        }

        BigDecimal precio;

        try {
            precio = new BigDecimal(
                    textoPrecio.replace(',', '.')
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El precio debe ser numérico. Ejemplo: 125.50"
            );
        }

        if (precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El precio no puede ser negativo."
            );
        }

        if (precio.scale() > 2) {
            throw new IllegalArgumentException(
                    "El precio solo puede tener dos decimales."
            );
        }

        int cantidad;

        try {
            cantidad = Integer.parseInt(textoCantidad);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser un número entero."
            );
        }

        if (cantidad < 0) {
            throw new IllegalArgumentException(
                    "La cantidad no puede ser negativa."
            );
        }

        return new Repuesto(
                id,
                nombre,
                descripcion,
                precio,
                cantidad
        );
    }

    private void limpiarCampos() {

        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtCantidadDisponible.setText("");

        tblRepuestos.clearSelection();

        txtNombre.requestFocusInWindow();
    }

    private TableRowSorter<DefaultTableModel> ordenador;

    
private void filtrarRepuestos() {

    if (ordenador == null) {
        return;
    }

    String texto = txtBuscar.getText().trim();

    if (texto.isEmpty()) {

        ordenador.setRowFilter(null);

    } else {

        ordenador.setRowFilter(
                RowFilter.regexFilter(
                        "(?i)" + Pattern.quote(texto)
                )
        );
    }
}

   
private void configurarBusqueda() {

    txtBuscar.getDocument().addDocumentListener(
            new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            filtrarRepuestos();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            filtrarRepuestos();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            filtrarRepuestos();
        }
    });
}

    private void cargarRepuestos() {

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "Nombre",
                    "Precio (Q)",
                    "Stock",
                    "Descripción"
                }, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        RepuestoDAO dao = new RepuestoDAO();

        try {

            for (Repuesto repuesto : dao.listar()) {

                modelo.addRow(new Object[]{
                    repuesto.getId(),
                    repuesto.getNombre(),
                    repuesto.getPrecio(),
                    repuesto.getCantidadDisponible(),
                    repuesto.getDescripcion()
                });
            }

            tblRepuestos.setModel(modelo);

            ordenador = new TableRowSorter<>(modelo);
            tblRepuestos.setRowSorter(ordenador);
            filtrarRepuestos();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar repuestos:\n"
                    + e.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblPrecio = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        lblCantidadDisponible = new javax.swing.JLabel();
        txtCantidadDisponible = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        txtBuscar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRepuestos = new javax.swing.JTable();
        btnVolver = new javax.swing.JButton();
        lblDescripción = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gestión de Repuestos");
        setResizable(false);

        lblTitulo.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        lblTitulo.setText("GESTIÓN DE REPUESTOS");

        lblId.setText("ID Repuesto");

        txtId.setEditable(false);
        txtId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdActionPerformed(evt);
            }
        });

        lblNombre.setText("Nombre del repuesto");

        lblPrecio.setText("Precio unitario");

        lblCantidadDisponible.setText("Cantidad disponible");

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });

        tblRepuestos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre ", "Precio ( Q )", "Stock", "Descripción"
            }
        ));
        tblRepuestos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRepuestosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblRepuestos);

        btnVolver.setText("Volver");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        lblDescripción.setText("Descripcion");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 565, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(192, 192, 192)
                            .addComponent(lblTitulo))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblId, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblDescripción, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblCantidadDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                                .addComponent(txtCantidadDisponible)
                                .addComponent(lblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(lblTitulo)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(lblNombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblDescripción)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPrecio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCantidadDisponible)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCantidadDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnVolver)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed

        try {

            Repuesto repuesto = obtenerRepuestoFormulario(false);

            RepuestoDAO dao = new RepuestoDAO();

            if (dao.guardar(repuesto)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Repuesto registrado correctamente."
                );

                txtBuscar.setText("");
                cargarRepuestos();
                limpiarCampos();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo registrar el repuesto."
                );
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Datos incorrectos",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al registrar repuesto:\n"
                    + e.getMessage(),
                    "Error de Oracle",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdActionPerformed

    private void tblRepuestosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRepuestosMouseClicked
        // TODO add your handling code here:

        int filaVista = tblRepuestos.getSelectedRow();

        if (filaVista == -1) {
            return;
        }

        int fila = tblRepuestos.convertRowIndexToModel(filaVista);

        DefaultTableModel modelo
                = (DefaultTableModel) tblRepuestos.getModel();

        txtId.setText(
                String.valueOf(modelo.getValueAt(fila, 0))
        );

        txtNombre.setText(
                java.util.Objects.toString(
                        modelo.getValueAt(fila, 1), "")
        );

        txtPrecio.setText(
                String.valueOf(modelo.getValueAt(fila, 2))
        );

        txtCantidadDisponible.setText(
                String.valueOf(modelo.getValueAt(fila, 3))
        );

        txtDescripcion.setText(
                java.util.Objects.toString(
                        modelo.getValueAt(fila, 4), "")
        );


    }//GEN-LAST:event_tblRepuestosMouseClicked

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed

        try {

            Repuesto repuesto = obtenerRepuestoFormulario(true);

            RepuestoDAO dao = new RepuestoDAO();

            if (dao.actualizar(repuesto)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Repuesto actualizado correctamente."
                );

                txtBuscar.setText("");
                cargarRepuestos();
                limpiarCampos();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el repuesto seleccionado."
                );
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Datos incorrectos",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al actualizar repuesto:\n"
                    + e.getMessage(),
                    "Error de Oracle",
                    JOptionPane.ERROR_MESSAGE
            );
        }


    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed

        if (txtId.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un repuesto de la tabla."
            );
            return;
        }

        int id;

        try {

            id = Integer.parseInt(txtId.getText().trim());

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "El ID del repuesto no es válido."
            );
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este repuesto?\n"
                + txtNombre.getText(),
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        RepuestoDAO dao = new RepuestoDAO();

        try {

            if (dao.eliminar(id)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Repuesto eliminado correctamente."
                );

                txtBuscar.setText("");
                cargarRepuestos();
                limpiarCampos();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el repuesto seleccionado."
                );
            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 2292) {

                JOptionPane.showMessageDialog(
                        this,
                        "No se puede eliminar este repuesto porque "
                        + "está relacionado con órdenes o "
                        + "movimientos de inventario.",
                        "Eliminación no permitida",
                        JOptionPane.WARNING_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Error al eliminar repuesto:\n"
                        + e.getMessage(),
                        "Error de Oracle",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }


    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:

        limpiarCampos();

    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        // TODO add your handling code here:

        this.dispose();

    }//GEN-LAST:event_btnVolverActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrmRepuestos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnVolver;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCantidadDisponible;
    private javax.swing.JLabel lblDescripción;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTable tblRepuestos;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCantidadDisponible;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
