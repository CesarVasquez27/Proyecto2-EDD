/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2edd;

/**
 *
 * @author Cesar Augusto
 */
public class Hash {
    private final double FACTOR_CARGA = 0.75; // Factor de carga para redimensionar
    private int numElementos = 0; // Número actual de elementos
    private int tamanoTabla; // Tamaño actual de la tabla
    private ListaPersona[] tabla; // Arreglo que representa la tabla hash

    // Constructor inicializa la tabla con el tamaño inicial
    public Hash() {
        this.tamanoTabla = 100; // Tamaño inicial de la tabla
        this.tabla = new ListaPersona[tamanoTabla];
        inicializarCubetas();
    }

    // Método para inicializar las cubetas de la tabla
    private void inicializarCubetas() {
        for (int i = 0; i < tamanoTabla; i++) {
            tabla[i] = new ListaPersona();
        }
    }

    // Nueva función hash optimizada
    private int funcionHash(String clave) {
        int hash = 0;
        for (int i = 0; i < clave.length(); i++) {
            hash = (hash * 37 + clave.charAt(i)) & 0x7fffffff; // Usa un primer número diferente y evita valores negativos
        }
        return hash % tamanoTabla;
    }

    // Método para redimensionar la tabla cuando se supera el factor de carga
    private void redimensionar() {
        int nuevoTamano = tamanoTabla * 2;
        ListaPersona[] nuevaTabla = new ListaPersona[nuevoTamano];
        for (int i = 0; i < nuevoTamano; i++) {
            nuevaTabla[i] = new ListaPersona();
        }

        // Rehash de todos los elementos en la nueva tabla
        for (int i = 0; i < tamanoTabla; i++) {
            ListaPersona lista = tabla[i];
            ListaPersona.Nodo nodo = lista.getCabeza();
            while (nodo != null) {
                int nuevoIndice = funcionHash(nodo.persona.getMote()) % nuevoTamano;
                nuevaTabla[nuevoIndice].agregar(nodo.persona);
                nodo = nodo.siguiente;
            }
        }

        // Actualizar la tabla y el tamaño
        this.tabla = nuevaTabla;
        this.tamanoTabla = nuevoTamano;
    }

    // Método para insertar un NodoPersona en la tabla de dispersión
    public void insertar(NodoPersona persona) {
        if (numElementos >= tamanoTabla * FACTOR_CARGA) {
            redimensionar(); // Redimensionar si se supera el factor de carga
        }
        
        int indice = funcionHash(persona.getMote()); // Calculamos el índice usando la función hash
        tabla[indice].agregar(persona); // Agregamos la persona a la lista enlazada de ese índice
        numElementos++;
    }

    // Método para buscar un NodoPersona en la tabla de dispersión por su mote
    public NodoPersona buscar(String mote) {
        int indice = funcionHash(mote); // Calculamos el índice usando la función hash
        return tabla[indice].buscar(mote); // Buscamos en la lista enlazada de ese índice
    }
    
    // Método para verificar si una persona está en la tabla hash (nuevo)
    public boolean contiene(String mote) {
        int indice = funcionHash(mote); // Calculamos el índice usando la función hash
        NodoPersona persona = tabla[indice].buscar(mote); // Buscar en la lista enlazada del índice
        return persona != null; // Si no es null, entonces existe
    }
    
    
    // Método para eliminar un NodoPersona en la tabla de dispersión por su mote
    public void eliminar(String mote) {
        int indice = funcionHash(mote); // Calculamos el índice usando la función hash
        NodoPersona persona = tabla[indice].buscar(mote);
        if (persona != null) {
            tabla[indice].eliminar(persona); // Eliminamos la persona de la lista enlazada de ese índice
            numElementos--;
        }
    }
}
