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
 * @author Cesar Augusto, Christian Goncalves, Tomas Paraco
 */
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.IOException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class Arbol {
    private NodoPersona raiz;
    private final Hash tablaHashPersonas;  // Tabla hash para búsqueda rápida

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
            // Utilizar Gson para deserializar
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            for (String nombreCasa : jsonObject.keySet()) {
                JsonArray integrantes = jsonObject.getAsJsonArray(nombreCasa);
                for (int i = 0; i < integrantes.size(); i++) {
                    JsonObject integranteJson = integrantes.get(i).getAsJsonObject();
                    for (String nombreCompleto : integranteJson.keySet()) {
                        NodoPersona nuevoNodo = parsearNodoPersona(integranteJson, nombreCompleto);
                        if (!tablaHashPersonas.contiene(nuevoNodo.getNombreCompleto())) {
                            tablaHashPersonas.insertar(nuevoNodo);

                            if (nuevoNodo.getPadre() == null) {
                                establecerRaiz(nuevoNodo);
                            } else {
                                agregarNodo(nuevoNodo.getPadre().getNombreCompleto(), nuevoNodo);
                            }
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
    // Valores por defecto
    String numeral = "Desconocido";
    String mote = "Sin apodo";
    String tituloNobiliario = "Sin título";
    String antecedentes = "Sin antecedentes";

    // Buscar si ya existe el nodo
    NodoPersona nuevoNodo = tablaHashPersonas.buscar(nombreCompleto);

    if (nuevoNodo == null) { 
        // Crear el nodo si no existe
        nuevoNodo = new NodoPersona(nombreCompleto, numeral, null, mote, tituloNobiliario, antecedentes);
    }

    // Listas para padres e hijos
    ListaPersona padres = new ListaPersona();
    ListaPersona hijos = new ListaPersona();

    JsonArray detalles = integranteJson.getAsJsonArray(nombreCompleto);
    if (detalles == null) {
        System.out.println("No se encontraron detalles para " + nombreCompleto);
        return nuevoNodo;
    }

    for (int j = 0; j < detalles.size(); j++) {
        JsonObject detalle = detalles.get(j).getAsJsonObject();

        // Asignar valores si existen, si no, se usan los valores por defecto
        if (detalle.has("Of his name")) numeral = detalle.get("Of his name").getAsString();
        if (detalle.has("Known throughout as")) mote = detalle.get("Known throughout as").getAsString();
        if (detalle.has("Held title")) tituloNobiliario = detalle.get("Held title").getAsString();
        if (detalle.has("Notes")) antecedentes = detalle.get("Notes").getAsString();

        // Procesar padres
        if (detalle.has("Born to")) {
            String nombrePadre = detalle.get("Born to").getAsString();
            if (!nombrePadre.isEmpty()) {
                NodoPersona padre = tablaHashPersonas.buscar(nombrePadre);
                if (padre == null) { 
                    padre = new NodoPersona(nombrePadre, "Desconocido", null, "Sin apodo", "Sin título", "Sin antecedentes");
                    tablaHashPersonas.insertar(padre); // Insertar en la tabla hash
                }
                padres.agregar(padre);
            }
        }

        // Procesar hijos
        if (detalle.has("Father to")) {
            JsonArray hijosArray = detalle.getAsJsonArray("Father to");
            if (hijosArray != null) {
                for (int k = 0; k < hijosArray.size(); k++) {
                    String nombreHijo = hijosArray.get(k).getAsString();
                    if (!nombreHijo.isEmpty()) {
                        NodoPersona hijo = tablaHashPersonas.buscar(nombreHijo);
                        if (hijo == null) { 
                            hijo = new NodoPersona(nombreHijo, "Desconocido", null, "Sin apodo", "Sin título", "Sin antecedentes");
                            tablaHashPersonas.insertar(hijo); // Insertar en la tabla hash
                        }
                        hijos.agregar(hijo);
                    }
                }
            }
        }
    }

    // Actualizar relaciones de padres e hijos
    actualizarRelacionesNodo(nuevoNodo, padres, hijos);

    // Actualizar atributos del nodo con la información obtenida
    nuevoNodo.setNumeral(numeral);
    nuevoNodo.setMote(mote);
    nuevoNodo.setTituloNobiliario(tituloNobiliario);
    nuevoNodo.setAntecedentes(antecedentes);

    // Insertar en la tabla hash si no estaba previamente
    if (!tablaHashPersonas.contiene(nombreCompleto)) {
        tablaHashPersonas.insertar(nuevoNodo);
    }

    return nuevoNodo;
    }

    /**
    * Actualiza las relaciones del nodo con sus padres e hijos evitando duplicados.
    * @param nodo Nodo actual.
    * @param padres Lista de nodos padres.
    * @param hijos Lista de nodos hijos.
    */
    private void actualizarRelacionesNodo(NodoPersona nodo, ListaPersona padres, ListaPersona hijos) {
        // Relacionar padres con el nodo
        ListaPersona.Nodo nodoPadre = padres.getCabeza();
        while (nodoPadre != null) {
            NodoPersona padre = nodoPadre.getPersona();
            if (!padre.getHijos().contiene(nodo)) { 
                padre.agregarHijo(nodo);
            }
            nodoPadre = nodoPadre.getSiguiente();
        }

        // Relacionar hijos con el nodo
        ListaPersona.Nodo nodoHijo = hijos.getCabeza();
        while (nodoHijo != null) {
            NodoPersona hijo = nodoHijo.getPersona();
            if (hijo.getPadre() == null) { 
                hijo.setPadre(nodo);
            }
            if (!nodo.getHijos().contiene(hijo)) { 
                nodo.agregarHijo(hijo);
            }
            nodoHijo = nodoHijo.getSiguiente();
        }
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
    
    /**
    * Obtiene una lista de antepasados para un nodo dado.
    * @param nodo NodoPersona del cual se quieren obtener los antepasados.
    * @return ListaPersona con los antepasados en orden jerárquico.
    */
    public ListaPersona obtenerAntepasados(NodoPersona nodo) {
        ListaPersona antepasados = new ListaPersona();
        NodoPersona actual = nodo.getPadre(); // Comenzar con el padre inmediato

        while (actual != null) {
            antepasados.agregar(actual); // Agregar a la lista
            actual = actual.getPadre(); // Subir un nivel
        }
        return antepasados;
    }
    
    /**
    * Muestra gráficamente los antepasados de un integrante específico.
    * @param nombreIntegrante Nombre del integrante cuyo linaje ascendente se mostrará.
    */
    public void mostrarAntepasadosGraficamente(String nombreIntegrante) {
        NodoPersona nodo = tablaHashPersonas.buscar(nombreIntegrante);

        if (nodo == null) {
            System.out.println("El integrante especificado no se encuentra en el árbol.");
            return;
        }

        // Obtener la lista de antepasados
        ListaPersona antepasados = obtenerAntepasados(nodo);

        // Configurar el grafo
        Graph grafo = new SingleGraph("Antepasados de " + nombreIntegrante);
        grafo.setStrict(false);
        grafo.setAutoCreate(true);

        // Agregar nodo raíz
        String nodoId = nodo.getNombreCompleto();
        grafo.addNode(nodoId).setAttribute("ui.label", nodo.getNombreCompleto());

        // Construir el grafo a partir de los antepasados
        ListaPersona.Nodo nodoAntepasado = antepasados.getCabeza();
        NodoPersona anterior = nodo;
        while (nodoAntepasado != null) {
            NodoPersona antepasado = nodoAntepasado.getPersona();
            String antepasadoId = antepasado.getNombreCompleto();

            grafo.addNode(antepasadoId).setAttribute("ui.label", antepasado.getNombreCompleto());
            grafo.addEdge(anterior.getNombreCompleto() + "-" + antepasadoId, anterior.getNombreCompleto(), antepasadoId);

            anterior = antepasado; // Actualizar el nodo anterior
            nodoAntepasado = nodoAntepasado.getSiguiente();
        }

        // Mostrar el grafo
        grafo.display();
    }
    
    /**
    * Busca y muestra los registros que tienen un título nobiliario específico.
    * @param tituloNobiliario Título nobiliario a buscar.
    */
    public void buscarPorTitulo(String tituloNobiliario) {
        if (raiz == null) {
            System.out.println("El árbol genealógico no ha sido cargado.");
            return;
        }

        // Crear una lista para almacenar los resultados de la búsqueda
        ListaPersona resultados = new ListaPersona();

        // Realizar una búsqueda recursiva en el árbol
        buscarPorTituloRecursivo(raiz, tituloNobiliario, resultados);

        // Verificar si hay resultados
        if (resultados.estaVacia()) {
            System.out.println("No se encontraron registros con el título: " + tituloNobiliario);
            return;
        }

        // Mostrar los resultados gráficamente
        Graph grafo = new SingleGraph("Registros con título: " + tituloNobiliario);
        grafo.setStrict(false);
        grafo.setAutoCreate(true);

        // Agregar nodos y enlaces al grafo
        ListaPersona.Nodo nodoResultado = resultados.getCabeza();
        while (nodoResultado != null) {
            NodoPersona persona = nodoResultado.getPersona();
            String nodoId = persona.getNombreCompleto();

            grafo.addNode(nodoId).setAttribute("ui.label", persona.getNombreCompleto());
            nodoResultado = nodoResultado.getSiguiente();
        }

        // Mostrar el grafo
        grafo.display();
    }

    /**
    * Método recursivo para buscar nodos con un título nobiliario específico.
    * @param nodo Nodo actual del árbol.
    * @param tituloNobiliario Título a buscar.
    * @param resultados Lista donde se almacenan los nodos encontrados.
    */
    private void buscarPorTituloRecursivo(NodoPersona nodo, String tituloNobiliario, ListaPersona resultados) {
        if (nodo == null) return;

        // Comparar el título del nodo actual
        if (nodo.getTituloNobiliario().equalsIgnoreCase(tituloNobiliario)) {
            resultados.agregar(nodo);
        }

        // Continuar buscando en los hijos
        ListaPersona hijos = nodo.getHijos();
        ListaPersona.Nodo nodoHijo = hijos.getCabeza();
        while (nodoHijo != null) {
            buscarPorTituloRecursivo(nodoHijo.getPersona(), tituloNobiliario, resultados);
            nodoHijo = nodoHijo.getSiguiente();
        }
    }
}

