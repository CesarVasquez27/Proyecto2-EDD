/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto2;

/**
 *
 * @author Cesar Augusto
 */
public class Hash {
    // Tamaño inicial de la tabla hash
    private final int TAMANO_TABLA = 100;
    // Arreglo que representa la tabla hash de listas enlazadas
    private ListaPersona[] tabla;

    // Constructor
    public Hash() {
        // Inicializamos la tabla con un tamaño fijo de cubetas
        tabla = new ListaPersona[TAMANO_TABLA];
        for (int i = 0; i < TAMANO_TABLA; i++) {
            tabla[i] = new ListaPersona();  // Inicializamos cada cubeta como una lista enlazada vacía
        }
    }

    // Función hash: convierte el mote en un índice en la tabla
    private int funcionHash(String clave) {
        int hash = 0;
        for (int i = 0; i < clave.length(); i++) {
            hash = (31 * hash + clave.charAt(i)) % TAMANO_TABLA;
        }
        return Math.abs(hash);
    }

    // Método para insertar un NodoPersona en la tabla de dispersión
    public void insertar(NodoPersona persona) {
        String mote = persona.getMote();
        int indice = funcionHash(mote);  // Calculamos el índice usando la función hash
        tabla[indice].agregar(persona);  // Agregamos la persona a la lista enlazada de ese índice
    }

    // Método para buscar un NodoPersona en la tabla de dispersión por su mote
    public NodoPersona buscar(String mote) {
        int indice = funcionHash(mote);  // Calculamos el índice usando la función hash
        return tabla[indice].buscar(mote);  // Buscamos en la lista enlazada de ese índice
    }

    // Método para eliminar un NodoPersona en la tabla de dispersión por su mote
    public void eliminar(String mote) {
        int indice = funcionHash(mote);  // Calculamos el índice usando la función hash
        NodoPersona persona = tabla[indice].buscar(mote);
        if (persona != null) {
            tabla[indice].eliminar(persona);  // Eliminamos la persona de la lista enlazada de ese índice
        }
    }
}
