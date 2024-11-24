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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.IOException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import javax.swing.JOptionPane;

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

    /**
     * Método para buscar un nodo por nombre completo en el árbol.
     * @param nombreCompleto Nombre completo del nodo a buscar.
     * @param actual Nodo actual desde donde comenzar la búsqueda.
     * @return NodoPersona encontrado o null si no se encuentra.
     */
    public NodoPersona buscarNodo(String nombreCompleto, NodoPersona actual) {
        if (actual == null) return null;
        if (actual.getNombreCompleto().equals(nombreCompleto)) return actual;

        NodoPersona resultado = null;
        ListaPersona hijos = actual.getHijos();
        ListaPersona.Nodo nodoHijo = hijos.getCabeza();

        while (nodoHijo != null && resultado == null) {
            resultado = buscarNodo(nombreCompleto, nodoHijo.getPersona());
            nodoHijo = nodoHijo.getSiguiente();
        }
        return resultado;
    }

    
    public void cargarArbolDesdeJSON(JsonObject jsonObject) {
        // Validar el JSON antes de procesarlo
        if (!validarJSON(jsonObject)) {
            JOptionPane.showMessageDialog(null, "El archivo JSON tiene datos malformados. Por favor, revisa su contenido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Procesar cada linaje dentro del JSON
        for (String nombreCasa : jsonObject.keySet()) {
            JsonArray integrantes = jsonObject.getAsJsonArray(nombreCasa);
            for (int i = 0; i < integrantes.size(); i++) {
                JsonObject personajeJson = integrantes.get(i).getAsJsonObject();

                // Iterar sobre cada personaje dentro de la casa
                for (String nombreCompleto : personajeJson.keySet()) {
                    JsonArray atributos = personajeJson.getAsJsonArray(nombreCompleto);

                    // Crear el nodo desde los atributos
                    NodoPersona nuevoNodo = parsearNodoPersona(nombreCompleto, atributos);

                    // Validar si ya existe el nodo en la tabla hash
                    if (!tablaHashPersonas.contiene(nuevoNodo.getNombreCompleto())) {
                        tablaHashPersonas.insertar(nuevoNodo);

                        // Establecer la raíz si no tiene padre
                        if (nuevoNodo.getPadre() == null) {
                            establecerRaiz(nuevoNodo);
                        } else {
                            // Agregar el nodo al árbol como hijo del padre
                            agregarNodo(nuevoNodo.getPadre().getNombreCompleto(), nuevoNodo);
                        }
                    }
                }
            }

            JOptionPane.showMessageDialog(null, "Linaje cargado: " + nombreCasa, "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
    * Método para validar que el archivo JSON tenga el formato esperado.
    */
    private boolean validarJSON(JsonObject jsonObject) {
        for (String nombreCasa : jsonObject.keySet()) {
            JsonElement casaElement = jsonObject.get(nombreCasa);

            if (!casaElement.isJsonArray()) {
                JOptionPane.showMessageDialog(null, "Error: '" + nombreCasa + "' no contiene un arreglo válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            JsonArray integrantes = casaElement.getAsJsonArray();
            for (JsonElement integranteElement : integrantes) {
                if (!integranteElement.isJsonObject()) {
                    JOptionPane.showMessageDialog(null, "Error: Uno de los integrantes en '" + nombreCasa + "' no es un objeto válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

            JsonObject integrante = integranteElement.getAsJsonObject();
            for (String nombreCompleto : integrante.keySet()) {
                JsonElement atributosElement = integrante.get(nombreCompleto);

                if (!atributosElement.isJsonArray()) {
                    JOptionPane.showMessageDialog(null, "Error: Los atributos de '" + nombreCompleto + "' no son un arreglo válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                JsonArray atributos = atributosElement.getAsJsonArray();
                boolean tieneOfHisName = false;

                for (JsonElement atributoElement : atributos) {
                    if (!atributoElement.isJsonObject()) {
                        JOptionPane.showMessageDialog(null, "Error: Uno de los atributos de '" + nombreCompleto + "' no es un objeto válido.", "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }

                    JsonObject atributo = atributoElement.getAsJsonObject();

                    // Validar si el campo "Of his name" está presente y es válido
                    if (atributo.has("Of his name") && atributo.get("Of his name").isJsonPrimitive()) {
                        tieneOfHisName = true;
                    }
                }

                // Si no se encontró el campo "Of his name", generar un error
                if (!tieneOfHisName) {
                    JOptionPane.showMessageDialog(null, "Error: El integrante '" + nombreCompleto + "' no contiene un nombre válido ('Of his name').", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
    }
    return true;
}


    private NodoPersona parsearNodoPersona(String nombreCompleto, JsonArray atributos) {
        // Valores predeterminados
        String numeral = "Desconocido";
        String mote = "Sin apodo";
        String tituloNobiliario = "Sin título";
        String antecedentes = "Sin antecedentes";

        // Buscar nodo existente en la tabla hash
        NodoPersona nuevoNodo = tablaHashPersonas.buscar(nombreCompleto);

        // Si no existe, crearlo con valores predeterminados
        if (nuevoNodo == null) {
            nuevoNodo = new NodoPersona(nombreCompleto, numeral, null, mote, tituloNobiliario, antecedentes);
        }

        // Listas para relaciones de padres e hijos
        ListaPersona padres = new ListaPersona();
        ListaPersona hijos = new ListaPersona();

        // Verificar que los atributos no sean nulos
        if (atributos == null) {
            System.out.println("No se encontraron detalles para " + nombreCompleto);
            return nuevoNodo;
        }

        // Procesar cada detalle en los atributos
        for (int j = 0; j < atributos.size(); j++) {
            JsonObject detalle = atributos.get(j).getAsJsonObject();

            // Actualizar atributos básicos
            if (detalle.has("Of his name")) numeral = detalle.get("Of his name").getAsString();
            if (detalle.has("Known throughout as")) mote = detalle.get("Known throughout as").getAsString();
            if (detalle.has("Held title")) tituloNobiliario = detalle.get("Held title").getAsString();
            if (detalle.has("Notes")) antecedentes = detalle.get("Notes").getAsString();

            // Procesar relación con padres
            if (detalle.has("Born to")) {
                String nombrePadre = detalle.get("Born to").getAsString();
                if (!nombrePadre.isEmpty()) {
                    NodoPersona padre = obtenerONuevoNodo(nombrePadre);
                    padres.agregar(padre);
                    if (!padre.getHijos().contiene(nuevoNodo)) {
                        padre.getHijos().agregar(nuevoNodo); // Relación inversa
                    }
                }
            }

            // Procesar relación con hijos
            if (detalle.has("Father to")) {
                JsonArray hijosArray = detalle.getAsJsonArray("Father to");
                if (hijosArray != null) {
                    for (int k = 0; k < hijosArray.size(); k++) {
                        String nombreHijo = hijosArray.get(k).getAsString();
                        if (!nombreHijo.isEmpty()) {
                            NodoPersona hijo = obtenerONuevoNodo(nombreHijo);
                            hijos.agregar(hijo);
                            if (!hijo.getPadres().contiene(nuevoNodo)) {
                                hijo.getPadres().agregar(nuevoNodo); // Relación inversa
                            }
                        }
                    }
                }
            }
        }

        // Actualizar las relaciones del nodo actual
        actualizarRelacionesNodo(nuevoNodo, padres, hijos);

        // Asignar los atributos finales al nodo
        nuevoNodo.setNumeral(numeral);
        nuevoNodo.setMote(mote);
        nuevoNodo.setTituloNobiliario(tituloNobiliario);
        nuevoNodo.setAntecedentes(antecedentes);

        // Insertar en la tabla hash si no existe
        if (!tablaHashPersonas.contiene(nombreCompleto)) {
            tablaHashPersonas.insertar(nuevoNodo);
        }

        return nuevoNodo;
    }
    
    // Crea un nuevo nodo si no existe en la tabla hash
    private NodoPersona obtenerONuevoNodo(String nombre) {
        NodoPersona nodo = tablaHashPersonas.buscar(nombre);
        if (nodo == null) {
            nodo = new NodoPersona(nombre, "Desconocido", null, "Sin apodo", "Sin título", "Sin antecedentes");
            tablaHashPersonas.insertar(nodo);
        }
        return nodo;
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

    public void mostrarArbolGraficamente(Graph graph) {
        try {
            // Limpiar el grafo antes de agregar nuevos nodos
            graph.clear();

            // Obtener la raíz del árbol genealógico
            NodoPersona raiz = obtenerRaiz(); // Método que obtendría la raíz del árbol
            if (raiz == null) {
                System.out.println("El árbol está vacío.");
                return;
            }

            // Llamar a la función recursiva para agregar nodos y enlaces
            agregarNodosYEnlacesGraficamente(raiz, graph);

            System.out.println("Árbol genealógico mostrado correctamente.");
        } catch (Exception e) {
            System.err.println("Error al mostrar el árbol gráficamente: " + e.getMessage());
        }
    }


    private void agregarNodosYEnlacesGraficamente(NodoPersona nodo, Graph grafo) {
        if (nodo == null) return;

        String nodoId = nodo.getNombreCompleto();
        if (grafo.getNode(nodoId) == null) {
            grafo.addNode(nodoId).setAttribute("ui.label", nodo.getNombreCompleto());
        }

        ListaPersona hijos = nodo.getHijos();
        if (hijos != null) {
            ListaPersona.Nodo nodoHijo = hijos.getCabeza();
            while (nodoHijo != null) {
                NodoPersona hijo = nodoHijo.persona;
                String hijoId = hijo.getNombreCompleto();

                if (grafo.getNode(hijoId) == null) {
                    grafo.addNode(hijoId).setAttribute("ui.label", hijo.getNombreCompleto());
                }

                if (grafo.getEdge(nodoId + "-" + hijoId) == null) {
                    grafo.addEdge(nodoId + "-" + hijoId, nodoId, hijoId);
                }

                agregarNodosYEnlacesGraficamente(hijo, grafo);
                nodoHijo = nodoHijo.siguiente;
            }
        }
    }

    
    /**
    * Obtiene una lista de antepasados para un nodo dado.
    * @param nodo NodoPersona del cual se quieren obtener los antepasados.
    * @return ListaPersona con los antepasados en orden jerárquico.
    */
    public ListaPersona obtenerAntepasados(NodoPersona nodo) {
        ListaPersona antepasados = new ListaPersona();

        if (nodo == null) {
            System.out.println("El nodo proporcionado es nulo. No se pueden obtener antepasados.");
            return antepasados;
        }

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

        ListaPersona antepasados = obtenerAntepasados(nodo);

        if (antepasados.estaVacia()) {
            System.out.println("No se encontraron antepasados para " + nombreIntegrante);
            return;
        }

        Graph grafo = new SingleGraph("Antepasados de " + nombreIntegrante);
        grafo.setStrict(false);
        grafo.setAutoCreate(true);

        String nodoId = nodo.getNombreCompleto();
        grafo.addNode(nodoId).setAttribute("ui.label", nodo.getNombreCompleto());

        ListaPersona.Nodo nodoAntepasado = antepasados.getCabeza();
        NodoPersona anterior = nodo;
        while (nodoAntepasado != null) {
            NodoPersona antepasado = nodoAntepasado.getPersona();
            String antepasadoId = antepasado.getNombreCompleto();

            if (grafo.getNode(antepasadoId) == null) {
                grafo.addNode(antepasadoId).setAttribute("ui.label", antepasado.getNombreCompleto());
            }

            if (grafo.getEdge(anterior.getNombreCompleto() + "-" + antepasadoId) == null) {
                grafo.addEdge(anterior.getNombreCompleto() + "-" + antepasadoId, anterior.getNombreCompleto(), antepasadoId);
            }

            anterior = antepasado;
            nodoAntepasado = nodoAntepasado.getSiguiente();
        }

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
    
    /**
    * Obtiene la lista de integrantes de una generación específica dentro del árbol genealógico.
    * @param nivelGeneracion El nivel de la generación a buscar (0 para la raíz, 1 para los hijos, etc.).
    * @return ListaPersona con los integrantes de la generación solicitada.
    */
    public ListaPersona obtenerIntegrantesDeGeneracion(int nivelGeneracion) {
        ListaPersona integrantes = new ListaPersona(); // Lista para almacenar los resultados
        if (raiz == null) {
            System.out.println("El árbol está vacío.");
            return integrantes;
        }

        // Implementación manual de una cola
        ListaPersona cola = new ListaPersona(); // Cola para el recorrido por niveles
        ListaPersona.Nodo nodoCola; // Nodo para recorrer la cola
        cola.agregar(raiz); // Agregamos la raíz como punto inicial
        int nivelActual = 0;

        while (!cola.estaVacia()) {
            int tamañoNivel = 0;

            // Contamos los nodos en la cola (tamaño del nivel actual)
            nodoCola = cola.getCabeza();
            while (nodoCola != null) {
                tamañoNivel++;
                nodoCola = nodoCola.getSiguiente();
            }

            // Si el nivel actual es el solicitado, añadimos los nodos al resultado
            if (nivelActual == nivelGeneracion) {
                nodoCola = cola.getCabeza();
                while (nodoCola != null) {
                    integrantes.agregar(nodoCola.persona); // Agregar todos los nodos actuales a la lista de resultados
                    nodoCola = nodoCola.getSiguiente();
                }
                break; // Terminamos porque ya encontramos la generación solicitada
            }

            // Avanzamos al siguiente nivel: procesamos los nodos actuales y añadimos sus hijos a la cola
            ListaPersona nuevaCola = new ListaPersona(); // Para almacenar los hijos del nivel actual
            nodoCola = cola.getCabeza();
            while (nodoCola != null) {
                ListaPersona hijos = nodoCola.persona.getHijos();
                ListaPersona.Nodo nodoHijo = hijos.getCabeza();
                while (nodoHijo != null) {
                    nuevaCola.agregar(nodoHijo.persona); // Añadir los hijos a la nueva cola
                    nodoHijo = nodoHijo.getSiguiente();
                }
                nodoCola = nodoCola.getSiguiente();
            }
            cola = nuevaCola; // Actualizamos la cola al siguiente nivel
            nivelActual++;
        }

        if (integrantes.estaVacia()) {
            System.out.println("No se encontraron integrantes en la generación " + nivelGeneracion + ".");
        }

        return integrantes;
    }
}
