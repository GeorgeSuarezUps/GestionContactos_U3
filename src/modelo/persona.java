package modelo;

import java.time.LocalDate;

public class persona {
    private String nombre;
    private String telefono;
    private String email;
    private String categoria;
    private LocalDate fechaRegistro;
    
    // Atributo de control de concurrencia (Unidad 3)
    private boolean bloqueadoParaEdicion = false;

    public persona(String nombre, String telefono, String email, String categoria) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.categoria = categoria;
        this.fechaRegistro = LocalDate.now();
    }

    // Métodos sincronizados para bloqueo exclusivo de edición (Unidad 3)
    public synchronized boolean intentarBloquear() {
        if (bloqueadoParaEdicion) {
            return false;
        }
        bloqueadoParaEdicion = true;
        return true;
    }

    public synchronized void liberarBloqueo() {
        bloqueadoParaEdicion = false;
    }

    public synchronized boolean isBloqueadoParaEdicion() {
        return bloqueadoParaEdicion;
    }

    public synchronized void actualizarDatos(String nombre, String telefono, String email, String categoria) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.categoria = categoria;
    }

    public synchronized String getNombre() { return nombre; }
    public synchronized String getTelefono() { return telefono; }
    public synchronized String getEmail() { return email; }
    public synchronized String getCategoria() { return categoria; }
    public synchronized LocalDate getFechaRegistro() { return fechaRegistro; }
}