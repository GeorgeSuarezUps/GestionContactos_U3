package controlador;

import modelo.persona;
import modelo.personaDAO;
import vista.ventana;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class logica_ventana {
    private final ventana vista;
    private final personaDAO modelo;
    private Locale localeActual = new Locale("es", "EC");
    
    // Objeto monitor dedicado para garantizar exclusión mutua en la exportación CSV
    private final Object lockExportacion = new Object();
    
    // Referencia al contacto actualmente bloqueado durante su edición
    private persona contactoEnEdicion = null;

    public logica_ventana(ventana vista, personaDAO modelo) {
        this.vista = vista;
        this.modelo = modelo;
        inicializarEventos();
        refrescarTabla(modelo.obtenerContactos());
    }

    private void inicializarEventos() {
        // Cambio dinámico de idioma (Unidad 2: Internacionalización con ResourceBundle)
        vista.getCmbIdioma().addActionListener(e -> {
            int idx = vista.getCmbIdioma().getSelectedIndex();
            if (idx == 0) localeActual = new Locale("es", "EC");
            else if (idx == 1) localeActual = new Locale("en", "US");
            else localeActual = new Locale("pt", "BR");
            vista.aplicarIdioma(localeActual);
            refrescarTabla(modelo.obtenerContactos());
        });

        // 1. VALIDACIÓN Y GUARDADO DE CONTACTOS EN SEGUNDO PLANO (Thread + SwingUtilities.invokeLater)
        vista.getBtnGuardar().addActionListener(e -> {
            String nombre = vista.getTxtNombre().getText().trim();
            String telefono = vista.getTxtTelefono().getText().trim();
            String email = vista.getTxtEmail().getText().trim();
            String categoria = (String) vista.getCmbCategoria().getSelectedItem();

            if (nombre.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
                mostrarNotificacionAsync(vista.getBundle().getString("msg.empty"), ventana.COLOR_ALERTA);
                return;
            }

            vista.getProgressBar().setVisible(true);
            vista.getProgressBar().setIndeterminate(true);

            // Se lanza un hilo independiente para verificar duplicados sin bloquear el hilo gráfico (EDT)
            new Thread(() -> {
                boolean duplicado = modelo.existeContacto(telefono, email);
                if (duplicado) {
                    // Toda actualización sobre componentes Swing se delega al Event Dispatch Thread (EDT)
                    SwingUtilities.invokeLater(() -> {
                        vista.getProgressBar().setVisible(false);
                        mostrarNotificacionAsync(vista.getBundle().getString("msg.duplicate"), ventana.COLOR_ALERTA);
                    });
                } else {
                    persona nuevo = new persona(nombre, telefono, email, categoria);
                    modelo.agregarContacto(nuevo);
                    SwingUtilities.invokeLater(() -> {
                        limpiarCampos();
                        refrescarTabla(modelo.obtenerContactos());
                        vista.getProgressBar().setVisible(false);
                        mostrarNotificacionAsync(vista.getBundle().getString("msg.saved"), ventana.COLOR_EXITO);
                    });
                }
            }).start();
        });

        // 2. BÚSQUEDA DE CONTACTOS EN SEGUNDO PLANO (SwingWorker)
        vista.getTxtBuscar().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String criterio = vista.getTxtBuscar().getText().trim().toLowerCase();
                buscarEnSegundoPlano(criterio);
            }
        });

        // 3. EXPORTACIÓN CONCURRENTE SINCRONIZADA A CSV (Thread + bloque synchronized)
        vista.getBtnExportar().addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getAbsolutePath().endsWith(".csv")) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }
                final File archivoDestino = file;
                vista.getProgressBar().setVisible(true);
                vista.getProgressBar().setIndeterminate(true);

                new Thread(() -> {
                    // Sincroniza el acceso de escritura al archivo para evitar corrupción si hay múltiples exportaciones
                    synchronized (lockExportacion) {
                        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoDestino))) {
                            pw.println("Nombre,Telefono,Email,Categoria,Fecha");
                            for (persona p : modelo.obtenerContactos()) {
                                pw.printf("%s,%s,%s,%s,%s%n",
                                        p.getNombre(), p.getTelefono(), p.getEmail(), p.getCategoria(), p.getFechaRegistro());
                            }
                            SwingUtilities.invokeLater(() -> {
                                vista.getProgressBar().setVisible(false);
                                mostrarNotificacionAsync(vista.getBundle().getString("msg.exported"), ventana.COLOR_EXITO);
                            });
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(() -> {
                                vista.getProgressBar().setVisible(false);
                                mostrarNotificacionAsync("Error CSV: " + ex.getMessage(), ventana.COLOR_ALERTA);
                            });
                        }
                    }
                }).start();
            }
        });

        // 4. BLOQUEO DE RECURSOS Y SINCRONIZACIÓN EN LA EDICIÓN DE CONTACTOS
        vista.getBtnEditar().addActionListener(e -> {
            int fila = vista.getTablaContactos().getSelectedRow();
            if (fila >= 0) {
                persona seleccionado = modelo.obtenerPorIndice(fila);
                // Adquiere el bloqueo exclusivo del contacto mediante método synchronized
                if (seleccionado != null && seleccionado.intentarBloquear()) {
                    contactoEnEdicion = seleccionado;
                    vista.getTxtNombre().setText(seleccionado.getNombre());
                    vista.getTxtTelefono().setText(seleccionado.getTelefono());
                    vista.getTxtEmail().setText(seleccionado.getEmail());
                    vista.getBtnActualizar().setEnabled(true);
                    vista.getBtnGuardar().setEnabled(false);
                } else {
                    mostrarNotificacionAsync(vista.getBundle().getString("msg.locked"), ventana.COLOR_ALERTA);
                }
            }
        });

        vista.getBtnActualizar().addActionListener(e -> {
            if (contactoEnEdicion != null) {
                // Actualización thread-safe de los atributos y liberación posterior del bloqueo
                contactoEnEdicion.actualizarDatos(
                        vista.getTxtNombre().getText().trim(),
                        vista.getTxtTelefono().getText().trim(),
                        vista.getTxtEmail().getText().trim(),
                        (String) vista.getCmbCategoria().getSelectedItem()
                );
                contactoEnEdicion.liberarBloqueo();
                contactoEnEdicion = null;
                vista.getBtnActualizar().setEnabled(false);
                vista.getBtnGuardar().setEnabled(true);
                limpiarCampos();
                refrescarTabla(modelo.obtenerContactos());
                mostrarNotificacionAsync(vista.getBundle().getString("msg.updated"), ventana.COLOR_EXITO);
            }
        });

        vista.getBtnEliminar().addActionListener(e -> eliminarSeleccionado());
        vista.getMenuEliminar().addActionListener(e -> eliminarSeleccionado());

        // Simulación de carga masiva en segundo plano para pruebas de rendimiento (5,000 registros)
        vista.getBtnSimularCarga().addActionListener(e -> {
            new Thread(() -> {
                long inicio = System.currentTimeMillis();
                for (int i = 1; i <= 5000; i++) {
                    modelo.agregarContacto(new persona("Usuario Prueba " + i, "099000" + i, "user" + i + "@test.com", "Trabajo"));
                }
                long fin = System.currentTimeMillis();
                SwingUtilities.invokeLater(() -> {
                    refrescarTabla(modelo.obtenerContactos());
                    mostrarNotificacionAsync("5,000 contactos cargados en " + (fin - inicio) + " ms", ventana.COLOR_SECUNDARIO);
                });
            }).start();
        });
    }

    // Ejecuta el filtrado en un hilo trabajador (doInBackground) y actualiza la JTable al finalizar (done)
    private void buscarEnSegundoPlano(String criterio) {
        SwingWorker<List<persona>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<persona> doInBackground() {
                List<persona> filtrados = new ArrayList<>();
                for (persona p : modelo.obtenerContactos()) {
                    if (p.getNombre().toLowerCase().contains(criterio) ||
                        p.getTelefono().contains(criterio) ||
                        p.getEmail().toLowerCase().contains(criterio)) {
                        filtrados.add(p);
                    }
                }
                return filtrados;
            }

            @Override
            protected void done() {
                try {
                    refrescarTabla(get());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // 5. NOTIFICACIONES EN TIEMPO REAL MEDIANTE SUBPROCESOS (Thread + SwingUtilities.invokeLater)
    private void mostrarNotificacionAsync(String mensaje, Color color) {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                vista.getLblNotificacion().setForeground(color);
                vista.getLblNotificacion().setText(mensaje);
            });
            try {
                Thread.sleep(3500);
            } catch (InterruptedException ignored) {}
            SwingUtilities.invokeLater(() -> vista.getLblNotificacion().setText(" "));
        }).start();
    }

    private void eliminarSeleccionado() {
        int fila = vista.getTablaContactos().getSelectedRow();
        if (fila >= 0) {
            modelo.eliminarContacto(fila);
            refrescarTabla(modelo.obtenerContactos());
        }
    }

    private void refrescarTabla(List<persona> lista) {
        vista.getTableModel().setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(localeActual);
        for (persona p : lista) {
            vista.getTableModel().addRow(new Object[]{
                    p.getNombre(),
                    p.getTelefono(),
                    p.getEmail(),
                    p.getCategoria(),
                    p.getFechaRegistro().format(fmt)
            });
        }
        vista.getLblEstadisticasTotal().setText(vista.getBundle().getString("stats.total") + " " + lista.size());
    }

    private void limpiarCampos() {
        vista.getTxtNombre().setText("");
        vista.getTxtTelefono().setText("");
        vista.getTxtEmail().setText("");
    }
}