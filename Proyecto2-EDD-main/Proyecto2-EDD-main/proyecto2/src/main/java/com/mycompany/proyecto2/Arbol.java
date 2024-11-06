/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto2;

/**
 *
 * @author Cesar Augusto
 */
public class Arbol {
    private NodoPersona raiz;  // La raíz del árbol genealógico

    // Constructor
    public Arbol() {
        this.raiz = null;
    }

    // Método para establecer la raíz del árbol
    public void establecerRaiz(NodoPersona persona) {
        this.raiz = persona;
    }

    // Método para obtener la raíz del árbol
    public NodoPersona obtenerRaiz() {
        return raiz;
    }

    // Método para agregar un nuevo NodoPersona al árbol
    public void agregarNodo(String nombrePadre, NodoPersona nuevoNodo) {
        NodoPersona padre = buscarNodo(nombrePadre, raiz);
        if (padre != null) {
            padre.agregarHijo(nuevoNodo);  // Agrega el nuevo nodo como hijo del nodo padre
        } else {
            System.out.println("El padre especificado no se encontró en el árbol.");
        }
    }

    // Método de búsqueda en profundidad (DFS) para localizar un NodoPersona en el árbol por nombre completo
    private NodoPersona buscarNodo(String nombreCompleto, NodoPersona actual) {
        if (actual == null) return null;
        
        if (actual.getNombreCompleto().equals(nombreCompleto)) return actual;
        
        NodoPersona resultado = null;
        ListaPersona hijos = actual.getHijos();
        ListaPersona.Nodo nodoHijo = hijos.getCabeza(); // Accede a la cabeza de la lista

        // Recorremos la lista de hijos
        while (nodoHijo != null && resultado == null) {
            resultado = buscarNodo(nombreCompleto, nodoHijo.getPersona());
            nodoHijo = nodoHijo.getSiguiente();
        }

        return resultado;
    }

    // Método para mostrar antepasados de un NodoPersona en el árbol, usando su nombre completo
    public String mostrarAntepasados(String nombre) {
        NodoPersona persona = buscarNodo(nombre, raiz);
        if (persona == null) return "Integrante no encontrado.";

        StringBuilder antepasados = new StringBuilder();
        NodoPersona actual = persona.getPadre();
        
        while (actual != null) {
            antepasados.append(actual.getNombreCompleto()).append(" -> ");
            actual = actual.getPadre();
        }

        return antepasados.toString().isEmpty() ? "No tiene antepasados." : antepasados.toString();
    }

    // Método para mostrar la descendencia de un NodoPersona en el árbol, usando su nombre completo
    public String mostrarDescendencia(String nombre) {
        NodoPersona persona = buscarNodo(nombre, raiz);
        if (persona == null) return "Integrante no encontrado.";

        StringBuilder descendencia = new StringBuilder();
        listarDescendientes(persona, descendencia, "");

        return descendencia.toString();
    }

    // Método recursivo para listar descendientes en el árbol
    private void listarDescendientes(NodoPersona persona, StringBuilder sb, String prefijo) {
        sb.append(prefijo).append(persona.getNombreCompleto()).append("\n");

        ListaPersona hijos = persona.getHijos();
        ListaPersona.Nodo nodoHijo = hijos.getCabeza();

        while (nodoHijo != null) {
            listarDescendientes(nodoHijo.getPersona(), sb, prefijo + "  ");
            nodoHijo = nodoHijo.getSiguiente();
        }
    }
}

