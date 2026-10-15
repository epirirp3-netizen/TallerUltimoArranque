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

import com.mycompany.tallerultimoarranque.dao.VehiculoDAO;
import java.util.ArrayList;
import com.mycompany.tallerultimoarranque.modelo.Vehiculo;

import com.mycompany.tallerultimoarranque.dao.OrdenTrabajoDAO;
import com.mycompany.tallerultimoarranque.modelo.OrdenTrabajo;

import com.mycompany.tallerultimoarranque.dao.ServicioDAO;
import com.mycompany.tallerultimoarranque.dao.RepuestoDAO;
import com.mycompany.tallerultimoarranque.modelo.Servicio;
import com.mycompany.tallerultimoarranque.modelo.Repuesto;

public class FrmOrdenes extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmOrdenes.class.getName());

    private final ArrayList<OrdenTrabajoDAO.ResumenOrden> ordenesDisponibles = new ArrayList<>();

    public FrmOrdenes() {

        initComponents();

        setLocationRelativeTo(null);

        txtIdOrden.setEditable(false);
        txtEstado.setEditable(false);
        txtEstado.setText("PENDIENTE");

        prepararDetalles();

        cargarVehiculosCombo();
        cargarMecanicosCombo();
        cargarOrdenes();

        limpiarNuevaOrden();
    }

    private final ArrayList<Servicio> serviciosDisponibles
            = new ArrayList<>();

    private final ArrayList<Repuesto> repuestosDisponibles
            = new ArrayList<>();

    @FunctionalInterface
    private interface AccionOrden {

        void ejecutar(int idOrden) throws SQLException;
    }

    private final ArrayList<Vehiculo> vehiculosDisponibles
            = new ArrayList<>();

    private final ArrayList<Mecanico> mecanicosDisponibles
            = new ArrayList<>();

    private void limpiarCampos() {

    }

    private TableRowSorter<DefaultTableModel> ordenador;

    private void prepararDetalles() {

        tblServiciosOrden.setModel(new DefaultTableModel(
                new Object[]{"ID Detalle", "Servicio", "Costo (Q)"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        });

        tblRepuestosOrden.setModel(new DefaultTableModel(
                new Object[]{"ID Detalle", "Repuesto", "Cantidad",
                    "Precio (Q)", "Subtotal (Q)"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        });

        txtTotalManoObra.setEditable(false);
        txtTotalRepuestos.setEditable(false);
        txtCostoTotal.setEditable(false);

        cmbRepuesto.addActionListener(
                e -> actualizarStockDisponible()
        );

        cargarServiciosCombo();
        cargarRepuestosCombo();
        limpiarDetalles();
    }

    private void cargarServiciosCombo() {

        cmbServicio.removeAllItems();
        cmbServicio.addItem("Seleccione un servicio");
        serviciosDisponibles.clear();

        try {
            serviciosDisponibles.addAll(
                    new ServicioDAO().listar()
            );

            for (Servicio servicio : serviciosDisponibles) {
                cmbServicio.addItem(
                        servicio.getDescripcion()
                        + " - Q" + servicio.getCostoManoObra()
                );
            }

            cmbServicio.setSelectedIndex(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this, "Error al cargar servicios:\n"
                    + e.getMessage()
            );
        }
    }

    private void cargarRepuestosCombo() {

        cmbRepuesto.removeAllItems();
        cmbRepuesto.addItem("Seleccione un repuesto");
        repuestosDisponibles.clear();

        try {
            repuestosDisponibles.addAll(
                    new RepuestoDAO().listar()
            );

            for (Repuesto repuesto : repuestosDisponibles) {
                cmbRepuesto.addItem(
                        repuesto.getNombre()
                        + " - Q" + repuesto.getPrecio()
                );
            }

            cmbRepuesto.setSelectedIndex(0);
            actualizarStockDisponible();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this, "Error al cargar repuestos:\n"
                    + e.getMessage()
            );
        }
    }

    private void actualizarStockDisponible() {

        int posicion = cmbRepuesto.getSelectedIndex() - 1;

        if (posicion < 0
                || posicion >= repuestosDisponibles.size()) {

            lblStockDisponible.setText(
                    "Stock disponible: —"
            );
            return;
        }

        Repuesto repuesto = repuestosDisponibles.get(posicion);

        lblStockDisponible.setText(
                "Stock disponible: "
                + repuesto.getCantidadDisponible()
        );
    }

    private int idOrdenSeleccionada() {

        String texto = txtIdOrden.getText().trim();

        if (texto.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una orden de la tabla."
            );
        }

        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El ID de la orden no es válido."
            );
        }
    }

    private void limpiarDetalles() {

        ((DefaultTableModel) tblServiciosOrden.getModel())
                .setRowCount(0);

        ((DefaultTableModel) tblRepuestosOrden.getModel())
                .setRowCount(0);

        txtTotalManoObra.setText("0.00");
        txtTotalRepuestos.setText("0.00");
        txtCostoTotal.setText("0.00");

        txtCantidadRepuesto.setText("");
    }

    private void cargarDetallesOrden(int idOrden) {

        OrdenTrabajoDAO dao = new OrdenTrabajoDAO();

        try {
            ArrayList<OrdenTrabajoDAO.ServicioOrden> servicios
                    = dao.listarServicios(idOrden);

            ArrayList<OrdenTrabajoDAO.RepuestoOrden> repuestos
                    = dao.listarRepuestos(idOrden);

            OrdenTrabajoDAO.Totales totales
                    = dao.obtenerTotales(idOrden);

            DefaultTableModel modeloServicios
                    = (DefaultTableModel) tblServiciosOrden.getModel();

            DefaultTableModel modeloRepuestos
                    = (DefaultTableModel) tblRepuestosOrden.getModel();

            modeloServicios.setRowCount(0);
            modeloRepuestos.setRowCount(0);

            for (OrdenTrabajoDAO.ServicioOrden servicio : servicios) {
                modeloServicios.addRow(new Object[]{
                    servicio.idDetalle(),
                    servicio.descripcion(),
                    servicio.costo()
                });
            }

            for (OrdenTrabajoDAO.RepuestoOrden repuesto : repuestos) {
                modeloRepuestos.addRow(new Object[]{
                    repuesto.idDetalle(),
                    repuesto.nombre(),
                    repuesto.cantidad(),
                    repuesto.precioUnitario(),
                    repuesto.subtotal()
                });
            }

            txtTotalManoObra.setText(
                    totales.manoObra().toPlainString()
            );

            txtTotalRepuestos.setText(
                    totales.repuestos().toPlainString()
            );

            txtCostoTotal.setText(
                    totales.total().toPlainString()
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar los detalles:\n"
                    + e.getMessage()
            );
        }
    }

    private void actualizarBotonesEstado() {

        boolean seleccionada
                = !txtIdOrden.getText().trim().isEmpty();

        String estado = txtEstado.getText().trim();

        boolean pendiente
                = seleccionada && estado.equals("PENDIENTE");

        boolean enProceso
                = seleccionada && estado.equals("EN_PROCESO");

        btnCrearOrden.setEnabled(!seleccionada);

        btnActualizarOrden.setEnabled(
                pendiente || enProceso
        );

        btnIniciarReparacion.setEnabled(pendiente);

        btnCancelarOrden.setEnabled(
                pendiente || enProceso
        );

        btnAgregarServicio.setEnabled(enProceso);
        btnQuitarServicio.setEnabled(enProceso);

        btnAgregarRepuesto.setEnabled(enProceso);
        btnQuitarRepuesto.setEnabled(enProceso);

        btnFinalizarOrden.setEnabled(enProceso);
    }

    private void mostrarOrdenSeleccionada() {

        int filaVista = tblOrdenes.getSelectedRow();

        if (filaVista < 0) {
            return;
        }

        int filaModelo
                = tblOrdenes.convertRowIndexToModel(filaVista);

        if (filaModelo < 0
                || filaModelo >= ordenesDisponibles.size()) {
            return;
        }

        OrdenTrabajoDAO.ResumenOrden orden
                = ordenesDisponibles.get(filaModelo);

        txtIdOrden.setText(String.valueOf(orden.id()));
        txtEstado.setText(orden.estado());

        txtDescripcionProblema.setText(
                orden.descripcionProblema()
        );

        cmbVehiculo.setSelectedIndex(0);

        for (int i = 0; i < vehiculosDisponibles.size(); i++) {
            if (vehiculosDisponibles.get(i).getId()
                    == orden.idVehiculo()) {

                cmbVehiculo.setSelectedIndex(i + 1);
                break;
            }
        }

        cmbMecanico.setSelectedIndex(0);

        for (int i = 0; i < mecanicosDisponibles.size(); i++) {
            if (mecanicosDisponibles.get(i).getId()
                    == orden.idMecanico()) {

                cmbMecanico.setSelectedIndex(i + 1);
                break;
            }
        }

        cargarDetallesOrden(orden.id());
        actualizarBotonesEstado();
    }

    private void recargarYSeleccionar(int idOrden) {

        cargarOrdenes();
        cargarRepuestosCombo();

        for (int i = 0; i < ordenesDisponibles.size(); i++) {

            if (ordenesDisponibles.get(i).id() == idOrden) {

                int filaVista
                        = tblOrdenes.convertRowIndexToView(i);

                if (filaVista >= 0) {
                    tblOrdenes.setRowSelectionInterval(
                            filaVista, filaVista
                    );

                    mostrarOrdenSeleccionada();
                }

                return;
            }
        }

        limpiarNuevaOrden();
    }

    private void limpiarNuevaOrden() {

        limpiarFormularioOrden();
        limpiarDetalles();
        actualizarBotonesEstado();
    }

    private void ejecutarOperacion(
            String mensajeCorrecto,
            AccionOrden accion) {

        try {
            int id = idOrdenSeleccionada();

            accion.ejecutar(id);

            recargarYSeleccionar(id);

            JOptionPane.showMessageDialog(
                    this, mensajeCorrecto
            );

        } catch (SQLException | IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Operación no permitida",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void cargarOrdenes() {

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "Vehículo",
                    "Mecánico",
                    "Estado"
                }, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        OrdenTrabajoDAO dao = new OrdenTrabajoDAO();

        try {

            ArrayList<OrdenTrabajoDAO.ResumenOrden> datos
                    = dao.listar();

            ordenesDisponibles.clear();
            ordenesDisponibles.addAll(datos);

            for (OrdenTrabajoDAO.ResumenOrden orden
                    : ordenesDisponibles) {

                modelo.addRow(new Object[]{
                    orden.id(),
                    orden.vehiculo(),
                    orden.mecanico(),
                    orden.estado()
                });
            }

            tblOrdenes.setModel(modelo);

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar órdenes:\n"
                    + e.getMessage(),
                    "Error de Oracle",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarFormularioOrden() {

        txtIdOrden.setText("");
        txtEstado.setText("PENDIENTE");
        txtDescripcionProblema.setText("");

        cmbVehiculo.setSelectedIndex(0);
        cmbMecanico.setSelectedIndex(0);

        tblOrdenes.clearSelection();

        txtDescripcionProblema.requestFocusInWindow();
    }

    private void cargarVehiculosCombo() {

        cmbVehiculo.removeAllItems();
        cmbVehiculo.addItem("Seleccione un vehículo");

        vehiculosDisponibles.clear();

        VehiculoDAO dao = new VehiculoDAO();

        try {

            vehiculosDisponibles.addAll(dao.listar());

            for (Vehiculo vehiculo : vehiculosDisponibles) {

                String texto = vehiculo.getPlaca()
                        + " - " + vehiculo.getMarca()
                        + " " + vehiculo.getModelo();

                cmbVehiculo.addItem(texto);
            }

            cmbVehiculo.setSelectedIndex(0);

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar vehículos:\n"
                    + e.getMessage(),
                    "Error de Oracle",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void cargarMecanicosCombo() {

        cmbMecanico.removeAllItems();
        cmbMecanico.addItem("Seleccione un mecánico");

        mecanicosDisponibles.clear();

        MecanicoDAO dao = new MecanicoDAO();

        try {

            mecanicosDisponibles.addAll(dao.listar());

            for (Mecanico mecanico : mecanicosDisponibles) {

                String texto = mecanico.getNombre()
                        + " - " + mecanico.getEspecialidad();

                cmbMecanico.addItem(texto);
            }

            cmbMecanico.setSelectedIndex(0);

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar mecánicos:\n"
                    + e.getMessage(),
                    "Error de Oracle",
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
        lblIdOrden = new javax.swing.JLabel();
        txtIdOrden = new javax.swing.JTextField();
        lblEstado = new javax.swing.JLabel();
        txtEstado = new javax.swing.JTextField();
        lblVehiculo = new javax.swing.JLabel();
        lblMecanico = new javax.swing.JLabel();
        btnCrearOrden = new javax.swing.JButton();
        btnIniciarReparacion = new javax.swing.JButton();
        btnActualizarOrden = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrdenes = new javax.swing.JTable();
        cmbMecanico = new javax.swing.JComboBox<>();
        cmbVehiculo = new javax.swing.JComboBox<>();
        lblDescripcionDelProblema = new javax.swing.JLabel();
        btnCancelarOrden = new javax.swing.JButton();
        btnNuevaOrden = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescripcionProblema = new javax.swing.JTextArea();
        tabDetalles = new javax.swing.JTabbedPane();
        pnlServicios = new javax.swing.JPanel();
        cmbServicio = new javax.swing.JComboBox<>();
        lblSeleccionarServicio = new javax.swing.JLabel();
        btnAgregarServicio = new javax.swing.JButton();
        btnQuitarServicio = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblServiciosOrden = new javax.swing.JTable();
        pnlRepuestos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbRepuesto = new javax.swing.JComboBox<>();
        lblCantidad = new javax.swing.JLabel();
        lblStockDisponible2 = new javax.swing.JLabel();
        txtCantidadRepuesto = new javax.swing.JTextField();
        lblStockDisponible = new javax.swing.JLabel();
        btnAgregarRepuesto = new javax.swing.JButton();
        btnQuitarRepuesto = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblRepuestosOrden = new javax.swing.JTable();
        btnVolver = new javax.swing.JButton();
        btnFinalizarOrden = new javax.swing.JButton();
        lblManoDeObra = new javax.swing.JLabel();
        txtTotalManoObra = new javax.swing.JTextField();
        lblTotalRepuestos = new javax.swing.JLabel();
        txtTotalRepuestos = new javax.swing.JTextField();
        lblCostoTotal = new javax.swing.JLabel();
        txtCostoTotal = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gestión de Órdenes de Trabajo");
        setResizable(false);

        lblTitulo.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        lblTitulo.setText("GESTIÓN DE ÓRDENES DE TRABAJO");

        lblIdOrden.setText("ID Orden");

        txtIdOrden.setEditable(false);
        txtIdOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdOrdenActionPerformed(evt);
            }
        });

        lblEstado.setText("Estado");

        lblVehiculo.setText("Vehículo");

        lblMecanico.setText("Mecánico");

        btnCrearOrden.setText("Crear orden");
        btnCrearOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearOrdenActionPerformed(evt);
            }
        });

        btnIniciarReparacion.setText("Iniciar reparación");
        btnIniciarReparacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarReparacionActionPerformed(evt);
            }
        });

        btnActualizarOrden.setText("Actualizar datos");
        btnActualizarOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarOrdenActionPerformed(evt);
            }
        });

        tblOrdenes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Vehiculo", "Mecanico", "Estado"
            }
        ));
        tblOrdenes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOrdenesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblOrdenes);

        cmbMecanico.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione un mecánico" }));

        cmbVehiculo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione un vehículo" }));

        lblDescripcionDelProblema.setText("Descripción del problema");

        btnCancelarOrden.setText("Cancelar orden");
        btnCancelarOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarOrdenActionPerformed(evt);
            }
        });

        btnNuevaOrden.setText("Nueva / Limpiar");
        btnNuevaOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaOrdenActionPerformed(evt);
            }
        });

        txtDescripcionProblema.setColumns(20);
        txtDescripcionProblema.setRows(5);

        cmbServicio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione un servicio" }));

        lblSeleccionarServicio.setText("Seleccionar servicio");

        btnAgregarServicio.setText("Agregar servicio");
        btnAgregarServicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarServicioActionPerformed(evt);
            }
        });

        btnQuitarServicio.setText("Quitar servicio");
        btnQuitarServicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarServicioActionPerformed(evt);
            }
        });

        tblServiciosOrden.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID Detalle", "Servicio", "Costo (Q)"
            }
        ));
        jScrollPane3.setViewportView(tblServiciosOrden);

        javax.swing.GroupLayout pnlServiciosLayout = new javax.swing.GroupLayout(pnlServicios);
        pnlServicios.setLayout(pnlServiciosLayout);
        pnlServiciosLayout.setHorizontalGroup(
            pnlServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlServiciosLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbServicio, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlServiciosLayout.createSequentialGroup()
                        .addComponent(btnAgregarServicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnQuitarServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addGroup(pnlServiciosLayout.createSequentialGroup()
                        .addGroup(pnlServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSeleccionarServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 12, Short.MAX_VALUE))))
        );
        pnlServiciosLayout.setVerticalGroup(
            pnlServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlServiciosLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(lblSeleccionarServicio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addGroup(pnlServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregarServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnQuitarServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        tabDetalles.addTab("Servicios", pnlServicios);

        jLabel1.setText("Seleccionar repuesto");

        cmbRepuesto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione un repuesto" }));

        lblCantidad.setText("Cantidad");

        lblStockDisponible2.setText("Stock disponible");

        btnAgregarRepuesto.setText("Agregar repuesto");
        btnAgregarRepuesto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarRepuestoActionPerformed(evt);
            }
        });

        btnQuitarRepuesto.setText("Quitar repuesto");
        btnQuitarRepuesto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarRepuestoActionPerformed(evt);
            }
        });

        tblRepuestosOrden.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Repuesto", "Cant.", "Precio", "Subtotal"
            }
        ));
        jScrollPane4.setViewportView(tblRepuestosOrden);

        javax.swing.GroupLayout pnlRepuestosLayout = new javax.swing.GroupLayout(pnlRepuestos);
        pnlRepuestos.setLayout(pnlRepuestosLayout);
        pnlRepuestosLayout.setHorizontalGroup(
            pnlRepuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRepuestosLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(pnlRepuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRepuestosLayout.createSequentialGroup()
                        .addGroup(pnlRepuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbRepuesto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnlRepuestosLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(pnlRepuestosLayout.createSequentialGroup()
                        .addComponent(lblCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblStockDisponible2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(72, 72, 72))
                    .addGroup(pnlRepuestosLayout.createSequentialGroup()
                        .addGroup(pnlRepuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCantidadRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAgregarRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlRepuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlRepuestosLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(btnQuitarRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblStockDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(pnlRepuestosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlRepuestosLayout.setVerticalGroup(
            pnlRepuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRepuestosLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(pnlRepuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlRepuestosLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(pnlRepuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCantidad)
                            .addComponent(lblStockDisponible2))
                        .addGap(14, 14, 14)
                        .addComponent(lblStockDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtCantidadRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlRepuestosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregarRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnQuitarRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabDetalles.addTab("Repuestos", pnlRepuestos);

        btnVolver.setText("Volver");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        btnFinalizarOrden.setText("Finalizar orden");
        btnFinalizarOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarOrdenActionPerformed(evt);
            }
        });

        lblManoDeObra.setText("Mano de obra");

        txtTotalManoObra.setEditable(false);

        lblTotalRepuestos.setText("Total repuestos");

        txtTotalRepuestos.setEditable(false);

        lblCostoTotal.setText("Costo total");

        txtCostoTotal.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(162, 162, 162)
                        .addComponent(lblTitulo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblIdOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtIdOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblVehiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(194, 194, 194)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblMecanico, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbMecanico, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lblDescripcionDelProblema, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDescripcionProblema, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblManoDeObra, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(101, 101, 101)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTotalRepuestos, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTotalRepuestos, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblCostoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(623, 623, 623))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(txtCostoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnFinalizarOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(55, 55, 55)
                                .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(38, 38, 38))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtTotalManoObra, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnCancelarOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnNuevaOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnCrearOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnActualizarOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnIniciarReparacion, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 16, Short.MAX_VALUE)
                                .addComponent(tabDetalles, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(35, 35, 35)
                    .addComponent(cmbVehiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(924, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(lblTitulo)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIdOrden)
                    .addComponent(lblEstado))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIdOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblVehiculo)
                        .addGap(64, 64, 64)
                        .addComponent(lblDescripcionDelProblema))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMecanico)
                        .addGap(18, 18, 18)
                        .addComponent(cmbMecanico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(lblTotalRepuestos)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTotalRepuestos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tabDetalles)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(lblManoDeObra))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtDescripcionProblema, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(31, 31, 31)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(btnCrearOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnActualizarOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnIniciarReparacion, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(btnCancelarOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnNuevaOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblCostoTotal)
                                        .addGap(5, 5, 5)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTotalManoObra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCostoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(955, 955, 955))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(428, 428, 428)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnVolver)
                            .addComponent(btnFinalizarOrden))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(176, 176, 176)
                    .addComponent(cmbVehiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(1444, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCrearOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearOrdenActionPerformed
        // TODO add your handling code here:
        if (!txtIdOrden.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ya tiene una orden seleccionada.\n"
                    + "Presione Nueva / Limpiar para registrar otra."
            );
            return;
        }

        if (cmbVehiculo.getSelectedIndex() <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un vehículo.",
                    "Campo obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (cmbMecanico.getSelectedIndex() <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un mecánico.",
                    "Campo obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String descripcion
                = txtDescripcionProblema.getText().trim();

        if (descripcion.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe ingresar la descripción del problema.",
                    "Campo obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (descripcion.length() > 500) {

            JOptionPane.showMessageDialog(
                    this,
                    "La descripción no puede superar 500 caracteres."
            );
            return;
        }

        int posicionVehiculo = cmbVehiculo.getSelectedIndex() - 1;
        int posicionMecanico = cmbMecanico.getSelectedIndex() - 1;

        if (posicionVehiculo >= vehiculosDisponibles.size()
                || posicionMecanico >= mecanicosDisponibles.size()) {

            JOptionPane.showMessageDialog(
                    this,
                    "La selección de vehículo o mecánico no es válida."
            );
            return;
        }

        Vehiculo vehiculo
                = vehiculosDisponibles.get(posicionVehiculo);

        Mecanico mecanico
                = mecanicosDisponibles.get(posicionMecanico);

        try {

            OrdenTrabajo orden = new OrdenTrabajo(
                    0,
                    vehiculo,
                    mecanico,
                    descripcion
            );

            OrdenTrabajoDAO dao = new OrdenTrabajoDAO();

            if (dao.crear(orden)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Orden de trabajo registrada correctamente."
                );

                cargarOrdenes();
                limpiarFormularioOrden();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo registrar la orden."
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
                    "Error al registrar orden:\n"
                    + e.getMessage(),
                    "Error de Oracle",
                    JOptionPane.ERROR_MESSAGE
            );
        }


    }//GEN-LAST:event_btnCrearOrdenActionPerformed

    private void txtIdOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdOrdenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdOrdenActionPerformed

    private void tblOrdenesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrdenesMouseClicked
        // TODO add your handling code here:

        mostrarOrdenSeleccionada();


    }//GEN-LAST:event_tblOrdenesMouseClicked

    private void btnActualizarOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarOrdenActionPerformed
        // TODO add your handling code here:

        ejecutarOperacion(
                "Datos de la orden actualizados.",
                id -> {

                    int posVehiculo
                    = cmbVehiculo.getSelectedIndex() - 1;

                    int posMecanico
                    = cmbMecanico.getSelectedIndex() - 1;

                    if (posVehiculo < 0
                    || posVehiculo >= vehiculosDisponibles.size()
                    || posMecanico < 0
                    || posMecanico >= mecanicosDisponibles.size()) {

                        throw new IllegalArgumentException(
                                "Seleccione vehículo y mecánico."
                        );
                    }

                    new OrdenTrabajoDAO().actualizarDatos(
                            id,
                            vehiculosDisponibles.get(posVehiculo).getId(),
                            mecanicosDisponibles.get(posMecanico).getId(),
                            txtDescripcionProblema.getText().trim()
                    );
                }
        );


    }//GEN-LAST:event_btnActualizarOrdenActionPerformed

    private void btnIniciarReparacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarReparacionActionPerformed
        // TODO add your handling code here:

        ejecutarOperacion(
                "Reparación iniciada correctamente.",
                id -> new OrdenTrabajoDAO().iniciarReparacion(id)
        );


    }//GEN-LAST:event_btnIniciarReparacionActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        // TODO add your handling code here:

        this.dispose();

    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnCancelarOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarOrdenActionPerformed
        // TODO add your handling code here:
        try {

            int id = idOrdenSeleccionada();

            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Cancelar la orden #" + id
                    + "?\nLos repuestos consumidos se devolverán al inventario.",
                    "Cancelar orden",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }

            ejecutarOperacion(
                    "Orden cancelada correctamente.",
                    idOrden -> new OrdenTrabajoDAO()
                            .cancelarOrden(idOrden)
            );

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }//GEN-LAST:event_btnCancelarOrdenActionPerformed

    private void btnNuevaOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaOrdenActionPerformed
        // TODO add your handling code here:
        limpiarNuevaOrden();

    }//GEN-LAST:event_btnNuevaOrdenActionPerformed

    private void btnAgregarServicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarServicioActionPerformed
        // TODO add your handling code here:
        ejecutarOperacion(
                "Servicio agregado correctamente.",
                id -> {

                    int posicion = cmbServicio.getSelectedIndex() - 1;

                    if (posicion < 0
                    || posicion >= serviciosDisponibles.size()) {

                        throw new IllegalArgumentException(
                                "Seleccione un servicio."
                        );
                    }

                    int idServicio
                    = serviciosDisponibles.get(posicion).getId();

                    new OrdenTrabajoDAO().agregarServicio(
                            id, idServicio
                    );
                }
        );

    }//GEN-LAST:event_btnAgregarServicioActionPerformed

    private void btnFinalizarOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarOrdenActionPerformed
        // TODO add your handling code here:
        try {

            int id = idOrdenSeleccionada();

            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de finalizar la orden #" + id
                    + "?\nDespués no podrá modificarla.",
                    "Finalizar orden",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }

            ejecutarOperacion(
                    "Orden finalizada correctamente.",
                    idOrden -> new OrdenTrabajoDAO()
                            .finalizarOrden(idOrden)
            );

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }//GEN-LAST:event_btnFinalizarOrdenActionPerformed

    private void btnQuitarServicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarServicioActionPerformed
        // TODO add your handling code here:
        ejecutarOperacion(
                "Servicio retirado correctamente.",
                id -> {

                    int fila = tblServiciosOrden.getSelectedRow();

                    if (fila < 0) {
                        throw new IllegalArgumentException(
                                "Seleccione un servicio de la tabla."
                        );
                    }

                    int idDetalle = Integer.parseInt(
                            tblServiciosOrden.getValueAt(fila, 0).toString()
                    );

                    new OrdenTrabajoDAO().quitarServicio(
                            id, idDetalle
                    );
                }
        );

    }//GEN-LAST:event_btnQuitarServicioActionPerformed

    private void btnAgregarRepuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarRepuestoActionPerformed
        // TODO add your handling code here:
        ejecutarOperacion(
                "Repuesto agregado y descontado del inventario.",
                id -> {

                    int posicion = cmbRepuesto.getSelectedIndex() - 1;

                    if (posicion < 0
                    || posicion >= repuestosDisponibles.size()) {

                        throw new IllegalArgumentException(
                                "Seleccione un repuesto."
                        );
                    }

                    int cantidad;

                    try {
                        cantidad = Integer.parseInt(
                                txtCantidadRepuesto.getText().trim()
                        );
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "Ingrese una cantidad entera válida."
                        );
                    }

                    if (cantidad <= 0) {
                        throw new IllegalArgumentException(
                                "La cantidad debe ser mayor que cero."
                        );
                    }

                    Repuesto repuesto
                    = repuestosDisponibles.get(posicion);

                    new OrdenTrabajoDAO().agregarRepuesto(
                            id, repuesto.getId(), cantidad
                    );
                }
        );

    }//GEN-LAST:event_btnAgregarRepuestoActionPerformed

    private void btnQuitarRepuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarRepuestoActionPerformed
        // TODO add your handling code here:
        ejecutarOperacion(
                "Repuesto retirado y devuelto al inventario.",
                id -> {

                    int fila = tblRepuestosOrden.getSelectedRow();

                    if (fila < 0) {
                        throw new IllegalArgumentException(
                                "Seleccione un repuesto de la tabla."
                        );
                    }

                    int idDetalle = Integer.parseInt(
                            tblRepuestosOrden.getValueAt(fila, 0).toString()
                    );

                    new OrdenTrabajoDAO().quitarRepuesto(
                            id, idDetalle
                    );
                }
        );

    }//GEN-LAST:event_btnQuitarRepuestoActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new FrmOrdenes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarOrden;
    private javax.swing.JButton btnAgregarRepuesto;
    private javax.swing.JButton btnAgregarServicio;
    private javax.swing.JButton btnCancelarOrden;
    private javax.swing.JButton btnCrearOrden;
    private javax.swing.JButton btnFinalizarOrden;
    private javax.swing.JButton btnIniciarReparacion;
    private javax.swing.JButton btnNuevaOrden;
    private javax.swing.JButton btnQuitarRepuesto;
    private javax.swing.JButton btnQuitarServicio;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cmbMecanico;
    private javax.swing.JComboBox<String> cmbRepuesto;
    private javax.swing.JComboBox<String> cmbServicio;
    private javax.swing.JComboBox<String> cmbVehiculo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblCostoTotal;
    private javax.swing.JLabel lblDescripcionDelProblema;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblIdOrden;
    private javax.swing.JLabel lblManoDeObra;
    private javax.swing.JLabel lblMecanico;
    private javax.swing.JLabel lblSeleccionarServicio;
    private javax.swing.JLabel lblStockDisponible;
    private javax.swing.JLabel lblStockDisponible2;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotalRepuestos;
    private javax.swing.JLabel lblVehiculo;
    private javax.swing.JPanel pnlRepuestos;
    private javax.swing.JPanel pnlServicios;
    private javax.swing.JTabbedPane tabDetalles;
    private javax.swing.JTable tblOrdenes;
    private javax.swing.JTable tblRepuestosOrden;
    private javax.swing.JTable tblServiciosOrden;
    private javax.swing.JTextField txtCantidadRepuesto;
    private javax.swing.JTextField txtCostoTotal;
    private javax.swing.JTextArea txtDescripcionProblema;
    private javax.swing.JTextField txtEstado;
    private javax.swing.JTextField txtIdOrden;
    private javax.swing.JTextField txtTotalManoObra;
    private javax.swing.JTextField txtTotalRepuestos;
    // End of variables declaration//GEN-END:variables
}
