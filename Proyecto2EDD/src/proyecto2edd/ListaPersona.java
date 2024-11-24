/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2edd;

/**
 * Clase ListaPersona que implementa una lista enlazada para almacenar objetos de tipo NodoPersona.
 * Proporciona métodos para agregar, buscar, eliminar y recorrer elementos en la lista.
 * 
 * @author Christian
 */
public class ListaPersona {

    /**
     * Clase interna Nodo que representa un elemento en la lista enlazada.
     */
    protected class Nodo { // Cambiado a protected para acceso desde otras clases del mismo paquete
        NodoPersona persona;
        Nodo siguiente;

        /**
         * Constructor del nodo.
         * 
         * @param persona Objeto de tipo NodoPersona que será almacenado en el nodo.
         */
        public Nodo(NodoPersona persona) {
            this.persona = persona;
            this.siguiente = null;
        }

        /**
         * Obtiene la persona almacenada en este nodo.
         * 
         * @return El objeto NodoPersona almacenado.
         */
        public NodoPersona getPersona() {
            return persona;
        }

        /**
         * Obtiene el nodo siguiente en la lista.
         * 
         * @return El siguiente nodo, o null si es el último nodo.
         */
        public Nodo getSiguiente() {
            return siguiente;
        }
    }

    private Nodo cabeza;  // Primer nodo de la lista
    private int size;     // Tamaño de la lista

    /**
     * Constructor de la lista enlazada.
     * Inicializa una lista vacía.
     */
    public ListaPersona() {
        this.cabeza = null;
        this.size = 0; // Inicialmente, la lista está vacía
    }

    /**
     * Agrega una nueva persona al final de la lista.
     * 
     * @param persona Objeto de tipo NodoPersona a agregar.
     */
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

    /**
     * Obtiene el nodo cabeza de la lista.
     * 
     * @return El primer nodo de la lista.
     */
    public Nodo getCabeza() {
        return cabeza;
    }

    /**
     * Elimina una persona de la lista.
     * Si la persona no se encuentra, no realiza ninguna acción.
     * 
     * @param persona Objeto de tipo NodoPersona a eliminar.
     */
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

    /**
     * Busca una persona en la lista por su nombre completo.
     * 
     * @param nombreCompleto Nombre completo de la persona a buscar.
     * @return El objeto NodoPersona encontrado, o null si no se encuentra.
     */
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

    /**
     * Imprime todos los elementos de la lista en la consola.
     */
    public void imprimirLista() {
        Nodo actual = cabeza;
        while (actual != null) {
            System.out.println(actual.persona);
            actual = actual.siguiente;
        }
    }

    /**
     * Verifica si la lista está vacía.
     * 
     * @return true si la lista no contiene elementos, false en caso contrario.
     */
    public boolean estaVacia() {
        return cabeza == null;
    }

    /**
     * Obtiene el tamaño actual de la lista.
     * 
     * @return El número de elementos en la lista.
     */
    public int getSize() {
        return size;
    }

    /**
     * Verifica si la lista contiene un nodo específico.
     * 
     * @param nodo El nodo de tipo NodoPersona a buscar.
     * @return true si el nodo está en la lista, false en caso contrario.
     */
    public boolean contiene(NodoPersona nodo) {
        Nodo actual = cabeza;
        while (actual != null) {
            if (actual.persona.equals(nodo)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }
}

