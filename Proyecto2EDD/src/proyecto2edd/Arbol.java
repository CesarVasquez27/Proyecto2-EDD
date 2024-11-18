/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2edd;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Cesar Augusto, Christian
 */
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.IOException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

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
         NodoPersona nuevoNodo;
        ListaPersona padres = new ListaPersona();
        ListaPersona hijos = new ListaPersona();

        JsonArray detalles = integranteJson.getAsJsonArray(nombreCompleto);
        for (int j = 0; j < detalles.size(); j++) {
            JsonObject detalle = detalles.get(j).getAsJsonObject();
            if (detalle.has("Of his name")) numeral = detalle.get("Of his name").getAsString();
            if (detalle.has("Born to")) {
                String nombrePadre = detalle.get("Born to").getAsString();
                NodoPersona padre = tablaHashPersonas.buscar(nombrePadre);
                if (padre != null) {
                    padres.agregar(padre);
                } else {
                    padre = new NodoPersona(nombrePadre, "", null, "", "", "");
                    tablaHashPersonas.insertar(padre);
                    padres.agregar(padre);
                }
            }
            if (detalle.has("Known throughout as")) mote = detalle.get("Known throughout as").getAsString();
            if (detalle.has("Held title")) tituloNobiliario = detalle.get("Held title").getAsString();
            if (detalle.has("Father to")) {
                JsonArray hijosArray = detalle.getAsJsonArray("Father to");
                for (int k = 0; k < hijosArray.size(); k++) {
                    String nombreHijo = hijosArray.get(k).getAsString();
                    NodoPersona hijo = tablaHashPersonas.buscar(nombreHijo);
                    if (hijo == null) {
                        hijo = new NodoPersona(nombreHijo, "", null, "", "", "");
                        tablaHashPersonas.insertar(hijo);
                    }
                    hijos.agregar(hijo);
                }
            }
            if (detalle.has("Notes")) antecedentes = detalle.get("Notes").getAsString();
        }

        // Aqui se crea el nuevo nodo
        nuevoNodo = new NodoPersona(nombreCompleto, numeral, null, mote, tituloNobiliario, antecedentes);

        // Relacionar padres con el nuevo nodo
        ListaPersona.Nodo nodoPadre = padres.getCabeza();
        while (nodoPadre != null) {
            NodoPersona padre = nodoPadre.getPersona();
            nuevoNodo.setPadre(padre);
            padre.agregarHijo(nuevoNodo);
            nodoPadre = nodoPadre.getSiguiente();
        }

        // Guardar el nodo en la tabla hash
        tablaHashPersonas.insertar(nuevoNodo);

        // Relacionar hijos con el nuevo nodo
        ListaPersona.Nodo nodoHijo = hijos.getCabeza();
        while (nodoHijo != null) {
            NodoPersona hijo = nodoHijo.getPersona();
            hijo.setPadre(nuevoNodo);
            nuevoNodo.agregarHijo(hijo);
            nodoHijo = nodoHijo.getSiguiente();
        }

        return nuevoNodo;
        }

    public void mostrarArbolGraficamente() {
        if (raiz == null) {
            System.out.println("No hay un árbol cargado para mostrar.");
            return;
        }

        Graph grafo = new SingleGraph("Árbol Genealógico");
        grafo.setStrict(false);
        grafo.setAutoCreate(true);

        agregarNodosYEnlacesGraficamente(raiz, grafo);
        grafo.display();
    }

    private void agregarNodosYEnlacesGraficamente(NodoPersona nodo, Graph grafo) {
        if (nodo == null) return;

        String nodoId = nodo.getNombreCompleto();
        grafo.addNode(nodoId).setAttribute("ui.label", nodo.getNombreCompleto());

        ListaPersona hijos = nodo.getHijos();
        ListaPersona.Nodo nodoHijo = hijos.getCabeza();

        while (nodoHijo != null) {
            NodoPersona hijo = nodoHijo.persona;
            String hijoId = hijo.getNombreCompleto();
            
            grafo.addNode(hijoId).setAttribute("ui.label", hijo.getNombreCompleto());
            grafo.addEdge(nodoId + "-" + hijoId, nodoId, hijoId);

            agregarNodosYEnlacesGraficamente(hijo, grafo);
            nodoHijo = nodoHijo.siguiente;
        }
    }
}
