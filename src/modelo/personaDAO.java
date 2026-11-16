package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class personaDAO {
    private final List<persona> listaContactos;

    public personaDAO() {
        this.listaContactos = Collections.synchronizedList(new ArrayList<>());
        listaContactos.add(new persona("George Suarez", "0991234567", "george@ups.edu.ec", "Trabajo"));
        listaContactos.add(new persona("Ana Torres", "0987654321", "ana.torres@mail.com", "Familia"));
        listaContactos.add(new persona("Carlos Mendoza", "0991112233", "cmendoza@empresa.com", "Amigos"));
    }

    public boolean existeContacto(String telefono, String email) {
        synchronized (listaContactos) {
            for (persona p : listaContactos) {
                if (p.getTelefono().equals(telefono.trim()) || p.getEmail().equalsIgnoreCase(email.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void agregarContacto(persona p) {
        listaContactos.add(p);
    }

    public void eliminarContacto(int index) {
        synchronized (listaContactos) {
            if (index >= 0 && index < listaContactos.size()) {
                listaContactos.remove(index);
            }
        }
    }

    public persona obtenerPorIndice(int index) {
        synchronized (listaContactos) {
            if (index >= 0 && index < listaContactos.size()) {
                return listaContactos.get(index);
            }
            return null;
        }
    }

    public List<persona> obtenerContactos() {
        synchronized (listaContactos) {
            return new ArrayList<>(listaContactos);
        }
    }
}