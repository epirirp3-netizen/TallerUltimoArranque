package com.mycompany.tallerultimoarranque.vista;

import com.mycompany.tallerultimoarranque.dao.ClienteDAO;
import com.mycompany.tallerultimoarranque.dao.VehiculoDAO;
import com.mycompany.tallerultimoarranque.modelo.Cliente;
import com.mycompany.tallerultimoarranque.modelo.Vehiculo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class FrmVehiculos extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmVehiculos.class.getName());

    private final ArrayList<Cliente> clientesDisponibles = new ArrayList<>();
    private final ArrayList<Vehiculo> vehiculosDisponibles = new ArrayList<>();

    public FrmVehiculos() {
        initComponents();

        setLocationRelativeTo(null);
        txtId.setEditable(false);

        txtBuscar.setText("");
        txtBuscar.setToolTipText("Buscar vehículo");

        cargarClientesCombo();
        cargarVehiculos();
        configurarBusqueda();
    }

    private void cargarClientesCombo() {

        cmbCliente.removeAllItems();
        cmbCliente.addItem("Seleccione un cliente");

        clientesDisponibles.clear();

        ClienteDAO dao = new ClienteDAO();

        try {

            clientesDisponibles.addAll(dao.listar());

            for (Cliente cliente : clientesDisponibles) {
                cmbCliente.addItem(cliente.getNombre());
            }

            cmbCliente.setSelectedIndex(0);

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar clientes:\n" + e.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarCampos() {

        txtId.setText("");
        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtAnio.setText("");

        cmbCliente.setSelectedIndex(0);
        tblVehiculos.clearSelection();

        txtPlaca.requestFocusInWindow();
    }

    private TableRowSorter<DefaultTableModel> ordenador;

    private void filtrarVehiculos() {

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
                filtrarVehiculos();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarVehiculos();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarVehiculos();
            }
        });
        JOptionPane.showMessageDialog(
                this,
                "Clientes encontrados: " + clientesDisponibles.size()
                + "\nOpciones en ComboBox: " + cmbCliente.getItemCount()
        );

    }

    private void cargarVehiculos() {

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "Placa",
                    "Marca",
                    "Modelo",
                    "Año",
                    "Cliente"
                }, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        VehiculoDAO dao = new VehiculoDAO();

        try {
            vehiculosDisponibles.clear();

            for (Vehiculo vehiculo : dao.listar()) {
                vehiculosDisponibles.add(vehiculo);
                modelo.addRow(new Object[]{
                    vehiculo.getId(),
                    vehiculo.getPlaca(),
                    vehiculo.getMarca(),
                    vehiculo.getModelo(),
                    vehiculo.getAnio(),
                    vehiculo.getCliente().getNombre()
                });
            }

            tblVehiculos.setModel(modelo);

            ordenador = new TableRowSorter<>(modelo);
            tblVehiculos.setRowSorter(ordenador);

            filtrarVehiculos();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar vehículos:\n"
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
        lblIdVehiculo = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblPlaca = new javax.swing.JLabel();
        txtPlaca = new javax.swing.JTextField();
        lblMarca = new javax.swing.JLabel();
        txtMarca = new javax.swing.JTextField();
        lblModelo = new javax.swing.JLabel();
        txtModelo = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        txtBuscar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVehiculos = new javax.swing.JTable();
        btnVolver = new javax.swing.JButton();
        lblCliente = new javax.swing.JLabel();
        txtAnio = new javax.swing.JTextField();
        lblAnio = new javax.swing.JLabel();
        cmbCliente = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gestión de Vehículos");
        setResizable(false);

        lblTitulo.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        lblTitulo.setText("GESTIÓN DE VEHÍCULOS");

        lblIdVehiculo.setText("ID Vehículo");

        txtId.setEditable(false);
        txtId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdActionPerformed(evt);
            }
        });

        lblPlaca.setText("Placa");

        lblMarca.setText("Marca");

        lblModelo.setText("Modelo");

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

        txtBuscar.setToolTipText("Buscar vehículo por placa, marca, modelo o cliente");
        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });

        tblVehiculos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Placa", "Marca", "Modelo", "Año", "Cliente"
            }
        ));
        tblVehiculos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVehiculosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblVehiculos);

        btnVolver.setText("Volver");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        lblCliente.setText("Cliente");

        lblAnio.setText("Año");

        cmbCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione un cliente" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(192, 192, 192)
                        .addComponent(lblTitulo))
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
                        .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblIdVehiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAnio, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblModelo, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPlaca, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                            .addComponent(txtModelo)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cmbCliente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(4, 4, 4)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(35, 35, 35)
                    .addComponent(lblAnio, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(455, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(lblTitulo)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIdVehiculo)
                    .addComponent(lblPlaca))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMarca)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblModelo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(lblCliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
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
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(204, 204, 204)
                    .addComponent(lblAnio)
                    .addContainerGap(387, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:

        String placa = txtPlaca.getText().trim().toUpperCase();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String textoAnio = txtAnio.getText().trim();

        if (placa.isEmpty() || marca.isEmpty()
                || modelo.isEmpty() || textoAnio.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe completar todos los datos del vehículo.",
                    "Campos obligatorios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (cmbCliente.getSelectedIndex() <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un cliente.",
                    "Cliente obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int anio;

        try {
            anio = Integer.parseInt(textoAnio);
        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "El año debe contener únicamente números."
            );
            return;
        }

        int anioMaximo = java.time.Year.now().getValue() + 1;

        if (anio < 1886 || anio > anioMaximo) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un año válido entre 1886 y "
                    + anioMaximo + "."
            );
            return;
        }

        int posicionCliente = cmbCliente.getSelectedIndex() - 1;

        if (posicionCliente >= clientesDisponibles.size()) {

            JOptionPane.showMessageDialog(
                    this,
                    "El cliente seleccionado no es válido."
            );
            return;
        }

        Cliente cliente = clientesDisponibles.get(posicionCliente);

        Vehiculo vehiculo = new Vehiculo(
                0,
                placa,
                marca,
                modelo,
                anio,
                cliente
        );

        VehiculoDAO dao = new VehiculoDAO();

        try {

            if (dao.guardar(vehiculo)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vehículo registrado correctamente."
                );

                cargarVehiculos();

                txtId.setText("");
                txtPlaca.setText("");
                txtMarca.setText("");
                txtModelo.setText("");
                txtAnio.setText("");

                cmbCliente.setSelectedIndex(0);
                tblVehiculos.clearSelection();

                txtPlaca.requestFocusInWindow();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo registrar el vehículo."
                );
            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ya existe un vehículo con esa placa.",
                        "Placa duplicada",
                        JOptionPane.WARNING_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Error al registrar vehículo:\n"
                        + e.getMessage(),
                        "Error de base de datos",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }


    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdActionPerformed

    private void tblVehiculosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVehiculosMouseClicked
        // TODO add your handling code here:

        int filaVista = tblVehiculos.getSelectedRow();

        if (filaVista == -1) {
            return;
        }

        int filaModelo = tblVehiculos.convertRowIndexToModel(filaVista);

        if (filaModelo >= vehiculosDisponibles.size()) {
            return;
        }

        Vehiculo vehiculo = vehiculosDisponibles.get(filaModelo);

        txtId.setText(String.valueOf(vehiculo.getId()));
        txtPlaca.setText(vehiculo.getPlaca());
        txtMarca.setText(vehiculo.getMarca());
        txtModelo.setText(vehiculo.getModelo());
        txtAnio.setText(String.valueOf(vehiculo.getAnio()));

        cmbCliente.setSelectedIndex(0);

        for (int i = 0; i < clientesDisponibles.size(); i++) {

            Cliente cliente = clientesDisponibles.get(i);

            if (cliente.getId() == vehiculo.getCliente().getId()) {

                cmbCliente.setSelectedIndex(i + 1);
                break;
            }
        }

    }//GEN-LAST:event_tblVehiculosMouseClicked

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        // TODO add your handling code here:

        if (txtId.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un vehículo de la tabla."
            );
            return;
        }

        int id;

        try {
            id = Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "El ID del vehículo no es válido."
            );
            return;
        }

        String placa = txtPlaca.getText().trim().toUpperCase();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String textoAnio = txtAnio.getText().trim();

        if (placa.isEmpty() || marca.isEmpty()
                || modelo.isEmpty() || textoAnio.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe completar todos los datos del vehículo.",
                    "Campos obligatorios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (cmbCliente.getSelectedIndex() <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar el propietario del vehículo."
            );
            return;
        }

        int anio;

        try {
            anio = Integer.parseInt(textoAnio);
        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "El año debe ser un número válido."
            );
            return;
        }

        int anioMaximo = java.time.Year.now().getValue() + 1;

        if (anio < 1886 || anio > anioMaximo) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un año entre 1886 y "
                    + anioMaximo + "."
            );
            return;
        }

        int posicionCliente = cmbCliente.getSelectedIndex() - 1;

        if (posicionCliente < 0
                || posicionCliente >= clientesDisponibles.size()) {

            JOptionPane.showMessageDialog(
                    this,
                    "El cliente seleccionado no es válido."
            );
            return;
        }

        Cliente cliente = clientesDisponibles.get(posicionCliente);

        Vehiculo vehiculo = new Vehiculo(
                id,
                placa,
                marca,
                modelo,
                anio,
                cliente
        );

        VehiculoDAO dao = new VehiculoDAO();

        try {

            if (dao.actualizar(vehiculo)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vehículo actualizado correctamente."
                );

                cargarVehiculos();

                txtId.setText("");
                txtPlaca.setText("");
                txtMarca.setText("");
                txtModelo.setText("");
                txtAnio.setText("");

                cmbCliente.setSelectedIndex(0);
                tblVehiculos.clearSelection();

                txtPlaca.requestFocusInWindow();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el vehículo seleccionado."
                );
            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ya existe otro vehículo con esa placa.",
                        "Placa duplicada",
                        JOptionPane.WARNING_MESSAGE
                );

            } else if (e.getErrorCode() == 2291) {

                JOptionPane.showMessageDialog(
                        this,
                        "El propietario seleccionado ya no existe.",
                        "Cliente no encontrado",
                        JOptionPane.WARNING_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Error al actualizar vehículo:\n"
                        + e.getMessage(),
                        "Error de base de datos",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }


    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:

        if (txtId.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un vehículo de la tabla."
            );
            return;
        }

        int id;

        try {
            id = Integer.parseInt(txtId.getText().trim());
        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "El ID del vehículo no es válido."
            );
            return;
        }

        String placa = txtPlaca.getText().trim();

        int confirmar = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este vehículo?\n"
                + "Placa: " + placa,
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        VehiculoDAO dao = new VehiculoDAO();

        try {

            if (dao.eliminar(id)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vehículo eliminado correctamente."
                );

                cargarVehiculos();

                txtId.setText("");
                txtPlaca.setText("");
                txtMarca.setText("");
                txtModelo.setText("");
                txtAnio.setText("");

                cmbCliente.setSelectedIndex(0);
                tblVehiculos.clearSelection();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el vehículo seleccionado."
                );
            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 2292) {

                JOptionPane.showMessageDialog(
                        this,
                        "No se puede eliminar este vehículo "
                        + "porque tiene órdenes de trabajo asociadas.",
                        "Eliminación no permitida",
                        JOptionPane.WARNING_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Error al eliminar vehículo:\n"
                        + e.getMessage(),
                        "Error de base de datos",
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
        java.awt.EventQueue.invokeLater(() -> new FrmVehiculos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cmbCliente;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAnio;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblIdVehiculo;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblModelo;
    private javax.swing.JLabel lblPlaca;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTable tblVehiculos;
    private javax.swing.JTextField txtAnio;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JTextField txtModelo;
    private javax.swing.JTextField txtPlaca;
    // End of variables declaration//GEN-END:variables
}
