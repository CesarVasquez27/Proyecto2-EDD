/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2edd;

/**
 *
 * @author Christian
 */
public class ListaPersona {
    protected class Nodo { // Cambiado a protected para acceso desde otras clases del mismo paquete
        NodoPersona persona;
        Nodo siguiente;

        public Nodo(NodoPersona persona) {
            this.persona = persona;
            this.siguiente = null;
        }

        public NodoPersona getPersona() {
            return persona;
        }

        public Nodo getSiguiente() {
            return siguiente;
        }
    }

    private Nodo cabeza;  // Primer nodo de la lista
    private int size;     // Tamaño de la lista

    // Constructor
    public ListaPersona() {
        this.cabeza = null;
        this.size = 0; // Inicialmente, la lista está vacía
    }

    // Método para agregar una persona a la lista
    public void agregar(NodoPersona persona) {
        Nodo nuevoNodo = new Nodo(persona);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        size++; // Incrementa el tamaño de la lista
    }

    // Método para obtener el nodo cabeza
    public Nodo getCabeza() {
        return cabeza;
    }

    // Método para eliminar una persona de la lista
    public void eliminar(NodoPersona persona) {
        if (cabeza == null) return;

        if (cabeza.persona.equals(persona)) {
            cabeza = cabeza.siguiente;
            size--; // Decrementa el tamaño de la lista
            return;
        }

        Nodo actual = cabeza;
        while (actual.siguiente != null && !actual.siguiente.persona.equals(persona)) {
            actual = actual.siguiente;
        }

        if (actual.siguiente != null) {
            actual.siguiente = actual.siguiente.siguiente;
            size--; // Decrementa el tamaño de la lista
        }
    }

    // Método para buscar una persona en la lista por nombre completo
    public NodoPersona buscar(String nombreCompleto) {
        Nodo actual = cabeza;
        while (actual != null) {
            if (actual.persona.getNombreCompleto().equals(nombreCompleto)) {
                return actual.persona;
            }
            actual = actual.siguiente;
        }
        return null; // Retorna null si no se encuentra
    }

    // Método para imprimir todos los miembros en la lista
    public void imprimirLista() {
        Nodo actual = cabeza;
        while (actual != null) {
            System.out.println(actual.persona);
            actual = actual.siguiente;
        }
    }

    // Método para verificar si la lista está vacía
    public boolean estaVacia() {
        return cabeza == null;
    }

    // Método para obtener el tamaño de la lista
    public int getSize() {
        return size;
    }
    
    public boolean contiene(NodoPersona persona) {
    Nodo actual = cabeza; // Cabeza de la lista enlazada
    while (actual != null) {
        if (actual.persona.equals(persona)) {
            return true; // Encontramos la persona
        }
        actual = actual.siguiente; // Pasar al siguiente nodo
    }
    return false; // No se encontró la persona
    }
}
