/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto2;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.IOException;

public class Arbol {
    private NodoPersona raiz;
    private Hash tablaHashPersonas;  // Tabla hash para búsqueda rápida

    public Arbol() {
        this.raiz = null;
        this.tablaHashPersonas = new Hash();
    }

    public void establecerRaiz(NodoPersona persona) {
        this.raiz = persona;
    }

    public NodoPersona obtenerRaiz() {
        return raiz;
    }

    public void agregarNodo(String nombrePadre, NodoPersona nuevoNodo) {
        NodoPersona padre = buscarNodo(nombrePadre, raiz);
        if (padre != null) {
            padre.agregarHijo(nuevoNodo);
        } else {
            System.out.println("El padre especificado no se encontró en el árbol.");
        }
    }

    private NodoPersona buscarNodo(String nombreCompleto, NodoPersona actual) {
        if (actual == null) return null;
        if (actual.getNombreCompleto().equals(nombreCompleto)) return actual;
        
        NodoPersona resultado = null;
        ListaPersona hijos = actual.getHijos();
        ListaPersona.Nodo nodoHijo = hijos.getCabeza();

        while (nodoHijo != null && resultado == null) {
            resultado = buscarNodo(nombreCompleto, nodoHijo.persona);
            nodoHijo = nodoHijo.siguiente;
        }
        return resultado;
    }

    public void cargarArbolDesdeJSON(String rutaArchivo) {
        try (FileReader reader = new FileReader(rutaArchivo)) {
            Gson gson = new Gson();
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            for (String nombreCasa : jsonObject.keySet()) {
                JsonArray integrantes = jsonObject.getAsJsonArray(nombreCasa);
                for (int i = 0; i < integrantes.size(); i++) {
                    JsonObject integranteJson = integrantes.get(i).getAsJsonObject();
                    for (String nombreCompleto : integranteJson.keySet()) {
                        NodoPersona nuevoNodo = parsearNodoPersona(integranteJson, nombreCompleto);
                        tablaHashPersonas.insertar(nuevoNodo);

                        if (nuevoNodo.getPadre() == null) {
                            establecerRaiz(nuevoNodo);
                        } else {
                            agregarNodo(nuevoNodo.getPadre().getNombreCompleto(), nuevoNodo);
                        }
                    }
                }
                System.out.println("Linaje cargado: " + nombreCasa);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    private NodoPersona parsearNodoPersona(JsonObject integranteJson, String nombreCompleto) {
        String numeral = "", mote = "", tituloNobiliario = "", antecedentes = "";
        NodoPersona padre = null;

        JsonArray detalles = integranteJson.getAsJsonArray(nombreCompleto);
        for (int j = 0; j < detalles.size(); j++) {
            JsonObject detalle = detalles.get(j).getAsJsonObject();
            if (detalle.has("Of his name")) numeral = detalle.get("Of his name").getAsString();
            if (detalle.has("Born to")) padre = tablaHashPersonas.buscar(detalle.get("Born to").getAsString());
            if (detalle.has("Known throughout as")) mote = detalle.get("Known throughout as").getAsString();
            if (detalle.has("Held title")) tituloNobiliario = detalle.get("Held title").getAsString();
            if (detalle.has("Notes")) antecedentes = detalle.get("Notes").getAsString();
        }
        
        NodoPersona nuevoNodo = new NodoPersona(nombreCompleto, numeral, padre, mote, tituloNobiliario, antecedentes);
        tablaHashPersonas.insertar(nuevoNodo);
        return nuevoNodo;
    }
}



