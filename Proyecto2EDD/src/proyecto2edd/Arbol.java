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
import org.graphstream.ui.view.Viewer;

/**
 * Clase Arbol que representa un árbol genealógico. 
 * Permite manejar relaciones familiares mediante la estructura de nodos, una tabla hash 
 * para búsqueda rápida y la funcionalidad para cargar datos desde un archivo JSON.
 * 
 * @author Cesar Augusto, Christian Goncalves, Tomas Paraco
 */
public class Arbol {
    private NodoPersona raiz;
    private final Hash tablaHashPersonas;  // Tabla hash para búsqueda rápida

    /**
     * Constructor de la clase Arbol. Inicializa el árbol sin una raíz definida 
     * y prepara la tabla hash para la búsqueda rápida de nodos.
     */
    public Arbol() {
        this.raiz = null;
        this.tablaHashPersonas = new Hash();
    }

    /**
     * Establece el nodo raíz del árbol.
     * 
     * @param persona NodoPersona que se asignará como raíz del árbol.
     */
    
    public void establecerRaiz(NodoPersona persona) {
        this.raiz = persona;
    }

    /**
     * Obtiene el nodo raíz del árbol.
     * 
     * @return NodoPersona que representa la raíz del árbol.
     */
    public NodoPersona obtenerRaiz() {
        return raiz;
    }

    /**
     * Agrega un nuevo nodo al árbol bajo un nodo padre especificado.
     * 
     * @param nombrePadre Nombre del nodo padre donde se agregará el nuevo nodo.
     * @param nuevoNodo NodoPersona a agregar al árbol.
     */
    public void agregarNodo(String nombrePadre, NodoPersona nuevoNodo) {
        NodoPersona padre = buscarNodo(nombrePadre, raiz);
        if (padre != null) {
            padre.agregarHijo(nuevoNodo);
        } else {
            System.out.println("El padre especificado no se encontró en el árbol.");
        }
    }

    /**
     * Busca un nodo en el árbol por su nombre completo.
     * 
     * @param nombreCompleto Nombre completo del nodo a buscar.
     * @param actual Nodo actual desde el cual se iniciará la búsqueda.
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

    /**
     * Carga los datos del árbol desde un archivo JSON.
     * 
     * @param rutaArchivo Ruta al archivo JSON que contiene los datos del árbol.
     */
    public void cargarArbolDesdeJSON(String rutaArchivo) {
        try (FileReader reader = new FileReader(rutaArchivo)) {
            // Leer el archivo JSON usando Gson
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            // Validar el JSON antes de procesarlo
            if (!validarJSON(jsonObject)) {
                System.out.println("El archivo JSON tiene datos malformados. Por favor, revisa su contenido.");
                return;
            }

            // Procesar cada linaje dentro del JSON
            for (String nombreCasa : jsonObject.keySet()) {
                JsonArray integrantes = jsonObject.getAsJsonArray(nombreCasa);

                for (int i = 0; i < integrantes.size(); i++) {
                    JsonObject personajeJson = integrantes.get(i).getAsJsonObject();

                    for (String nombreCompleto : personajeJson.keySet()) {
                        JsonArray atributos = personajeJson.getAsJsonArray(nombreCompleto);

                        // Crear el nodo desde los atributos
                        NodoPersona nuevoNodo = parsearNodoPersona(nombreCompleto, atributos);

                        // Insertar el nodo en la tabla hash si no existe
                        if (!tablaHashPersonas.contiene(nuevoNodo.getNombreCompleto())) {
                            System.out.println("Insertando nodo con nombre: " + nuevoNodo.getNombreCompleto());
                            tablaHashPersonas.insertar(nuevoNodo);

                            // Verificar si el nodo tiene padre y establecer relaciones
                            if (nuevoNodo.getPadre() != null) {
                                NodoPersona padre = tablaHashPersonas.buscar(nuevoNodo.getPadre().getMote());
                                if (padre != null) {
                                    padre.agregarHijo(nuevoNodo);
                                    nuevoNodo.setPadre(padre);
                                }
                            } else {
                                // Si no tiene padre, se considera raíz
                                establecerRaiz(nuevoNodo);
                            }
                        }
                    }
                }
                System.out.println("Linaje cargado: " + nombreCasa);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado al procesar el archivo JSON: " + e.getMessage());
        }
    }



    /**
     * Valida el formato del archivo JSON antes de procesarlo.
     * 
     * @param jsonObject Objeto JsonObject que representa el contenido del archivo JSON.
     * @return true si el JSON tiene un formato válido; de lo contrario, false.
     */
    private boolean validarJSON(JsonObject jsonObject) {
        for (String nombreCasa : jsonObject.keySet()) {
            JsonElement casaElement = jsonObject.get(nombreCasa);

            if (!casaElement.isJsonArray()) {
                System.err.println("Error: '" + nombreCasa + "' no contiene un arreglo válido.");
                return false;
            }

            JsonArray integrantes = casaElement.getAsJsonArray();
            for (JsonElement integranteElement : integrantes) {
                if (!integranteElement.isJsonObject()) {
                    System.err.println("Error: Uno de los integrantes en '" + nombreCasa + "' no es un objeto válido.");
                    return false;
                }

                JsonObject integrante = integranteElement.getAsJsonObject();
                for (String nombreCompleto : integrante.keySet()) {
                    JsonElement atributosElement = integrante.get(nombreCompleto);

                    if (!atributosElement.isJsonArray()) {
                        System.err.println("Error: Los atributos de '" + nombreCompleto + "' no son un arreglo válido.");
                        return false;
                    }

                    JsonArray atributos = atributosElement.getAsJsonArray();
                    boolean tieneOfHisName = false;

                    for (JsonElement atributoElement : atributos) {
                        if (!atributoElement.isJsonObject()) {
                            System.err.println("Error: Uno de los atributos de '" + nombreCompleto + "' no es un objeto válido.");
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
                        System.err.println("Error: El integrante '" + nombreCompleto + "' no contiene un nombre válido ('Of his name').");
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
    * Método para parsear un nodo a partir de los datos JSON.
    * Este método interpreta los atributos asociados a una persona en el árbol genealógico,
    * crea o actualiza el nodo correspondiente y establece las relaciones familiares 
    * (padres e hijos) según los datos proporcionados.
    * 
    * @param nombreCompleto Nombre completo de la persona representada por el nodo.
    * @param atributos JsonArray que contiene los detalles y relaciones familiares de la persona.
    * @return NodoPersona creado o actualizado con base en los datos proporcionados.
    */
    private NodoPersona parsearNodoPersona(String nombreCompleto, JsonArray atributos) {
        // Valores predeterminados
        String numeral = "Desconocido";
        String mote = "Sin apodo";
        String tituloNobiliario = "Sin título";
        String antecedentes = "Sin antecedentes";

        // Obtener o crear el nodo
        NodoPersona nuevoNodo = obtenerONuevoNodo(nombreCompleto);

        // Listas para relaciones de padres e hijos
        ListaPersona padres = new ListaPersona();
        ListaPersona hijos = new ListaPersona();

        if (atributos != null) {
            // Procesar cada atributo
            for (int j = 0; j < atributos.size(); j++) {
                JsonObject detalle = atributos.get(j).getAsJsonObject();

                // Asignar atributos básicos
                if (detalle.has("Of his name")) numeral = detalle.get("Of his name").getAsString();
                if (detalle.has("Known throughout as")) mote = detalle.get("Known throughout as").getAsString();
                if (detalle.has("Held title")) tituloNobiliario = detalle.get("Held title").getAsString();
                if (detalle.has("Notes")) antecedentes = detalle.get("Notes").getAsString();

                // Relaciones padre-hijo
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

                if (detalle.has("Father to")) {
                    JsonArray hijosArray = detalle.getAsJsonArray("Father to");
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

        // Actualizar relaciones en el nodo
        actualizarRelacionesNodo(nuevoNodo, padres, hijos);

        // Asignar atributos finales
        nuevoNodo.setNumeral(numeral);
        nuevoNodo.setMote(mote);
        nuevoNodo.setTituloNobiliario(tituloNobiliario);
        nuevoNodo.setAntecedentes(antecedentes);

        return nuevoNodo;
    }
    
    /**
    * Método para obtener un nodo de la tabla hash o crear uno nuevo si no existe.
    * 
    * @param nombre Nombre completo de la persona representada por el nodo.
    * @return NodoPersona existente en la tabla hash o un nuevo nodo creado si no se encuentra.
    */
    private NodoPersona obtenerONuevoNodo(String nombre) {
        NodoPersona nodo = tablaHashPersonas.buscar(nombre);
        if (nodo == null) {
            nodo = new NodoPersona(nombre, "Desconocido", null, "Sin apodo", "Sin título", "Sin antecedentes");
            tablaHashPersonas.insertar(nodo);
        }
        return nodo;
    }

    /**
    * Método para obtener el registro de una persona en el árbol genealógico.
    * 
    * @param nombreCompleto Nombre completo de la persona cuyo registro se desea consultar.
    * @return NodoPersona que contiene los datos del integrante, o null si no se encuentra.
    */
    public NodoPersona verRegistro(String nombreCompleto) {
        // Buscar el nodo en la tabla hash para obtener la persona
        NodoPersona nodo = tablaHashPersonas.buscar(nombreCompleto);
        if (nodo != null) {
            // Aquí puedes retornar el nodo o los detalles del registro
            return nodo;
        } else {
            System.out.println("No se encontró el integrante en el árbol.");
            return null;
        }
    }


    /**
    * Actualiza las relaciones del nodo con sus padres e hijos evitando duplicados.
    * Este método asegura que los nodos relacionados estén correctamente vinculados
    * y evita la creación de relaciones redundantes en el árbol genealógico.
    * 
    * @param nodo Nodo actual que se actualizará con las relaciones familiares.
    * @param padres Lista de nodos que representan los padres del nodo actual.
    * @param hijos Lista de nodos que representan los hijos del nodo actual.
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

    /**
    * Asigna posiciones a los nodos en el grafo de forma recursiva.
    * 
    * Esta función recorre el árbol genealógico de manera recursiva, asignando posiciones
    * a cada nodo en función de su nivel y su posición relativa en el árbol. El grafo
    * generado es utilizado para mostrar visualmente el árbol.
    *
    * @param nodo El nodo actual que representa una persona en el árbol genealógico.
    * @param grafo El grafo donde se están asignando las posiciones.
    * @param nivel El nivel del nodo en el árbol genealógico (profundidad).
    * @param x La posición horizontal inicial para el nodo.
    */
    private void asignarPosiciones(NodoPersona nodo, Graph grafo, int nivel, double x) {
        if (nodo == null) return;

        // Asigna la posición al nodo actual
        String nodoId = nodo.getNombreCompleto();
        if (grafo.getNode(nodoId) != null) {
            grafo.getNode(nodoId).setAttribute("xyz", x, -nivel, 0);
        }

        // Obtener la lista de hijos
        ListaPersona hijos = nodo.getHijos();
        if (hijos != null && hijos.getCabeza() != null) {
            ListaPersona.Nodo nodoHijo = hijos.getCabeza();
            int numHijos = contarHijos(hijos);

            // Posición inicial de los hijos (centrada respecto al nodo actual)
            double posX = x - (numHijos - 1) / 2.0;

            while (nodoHijo != null) {
                NodoPersona hijo = nodoHijo.persona;
                asignarPosiciones(hijo, grafo, nivel + 1, posX);
                posX++; // Incrementar la posición horizontal para el siguiente hijo
                nodoHijo = nodoHijo.siguiente;
            }
        }
    }

    /**
    * Cuenta el número de hijos de un nodo en la lista de hijos.
    * 
    * Esta función recorre la lista de hijos del nodo para contar cuántos hijos tiene.
    * 
    * @param hijos La lista de hijos de un nodo.
    * @return El número de hijos del nodo.
    */
    private int contarHijos(ListaPersona hijos) {
        int contador = 0;
        ListaPersona.Nodo nodoActual = hijos.getCabeza();
        while (nodoActual != null) {
            contador++;
            nodoActual = nodoActual.siguiente;
        }
        return contador;
    }


    /**
    * Busca una persona en la tabla hash utilizando su nombre completo.
    * 
    * Esta función busca a la persona cuyo nombre coincida con el nombre dado en la
    * tabla hash de personas. Si no se encuentra la persona, se retorna null.
    * 
    * @param nombre El nombre de la persona a buscar en la tabla hash.
    * @return El nodo de la persona encontrado en la tabla hash, o null si no existe.
    * @throws IllegalStateException Si la tabla hash no está inicializada.
    */
    public NodoPersona buscarEnTablaHash(String nombre) {
        if (tablaHashPersonas == null) {
            throw new IllegalStateException("La tabla hash no está inicializada.");
        }
        // Buscar en la tabla hash
        return tablaHashPersonas.buscar(nombre); // Método 'obtener' implementado en tu clase de tabla hash
    }

    /**
     * Muestra gráficamente el árbol genealógico completo.
     * Esta función limpia el grafo, configura los estilos visuales, agrega los nodos
     * y enlaces, asigna las posiciones y luego muestra el árbol usando un layout automático.
     * @param graph El grafo en el que se mostrará el árbol genealógico.
     */
    
    public void mostrarArbolGraficamente(Graph graph) {
        try {
            // Limpiar el grafo
            graph.clear();

            // Obtener la raíz del árbol genealógico
            NodoPersona raiz = obtenerRaiz();
            if (raiz == null) {
                System.out.println("El árbol está vacío.");
                return;
            }

            // Configurar estilos y calidad del grafo
            graph.setAttribute("ui.stylesheet",
                "node { text-size: 15px; fill-color: lightblue; size: 25px; text-color: black; stroke-mode: plain; stroke-color: black; } " +
                "edge { fill-color: gray; arrow-size: 10px, 10px; }");
            graph.setAttribute("ui.quality");
            graph.setAttribute("ui.antialias");

            // Agregar nodos y enlaces al grafo
            agregarNodosYEnlacesGraficamente(raiz, graph);

            // Asignar posiciones iniciales
            asignarPosiciones(raiz, graph, 0, 0.0);

            // Mostrar el grafo con layout automático mejorado
            Viewer viewer = graph.display();
            viewer.enableAutoLayout();

            System.out.println("Árbol genealógico mostrado correctamente.");
        } catch (Exception e) {
            System.err.println("Error al mostrar el árbol gráficamente: " + e.getMessage());
        }
    }

    /**
     * Agrega los nodos y enlaces de un árbol genealógico al grafo.
     * Esta función recursiva agrega nodos para cada persona y enlaces entre padres e hijos.
     * @param nodo El nodo actual que se está procesando.
     * @param grafo El grafo en el que se agregarán los nodos y enlaces.
     */
    
    public void agregarNodosYEnlacesGraficamente(NodoPersona nodo, Graph grafo) {
        if (nodo == null) return;

        String nodoId = nodo.getNombreCompleto();

        // Si el nodo no existe, agregarlo
        if (grafo.getNode(nodoId) == null) {
            grafo.addNode(nodoId).setAttribute("ui.label", nodo.getNombreCompleto());
        }

        // Agregar los hijos
        ListaPersona hijos = nodo.getHijos();
        if (hijos != null) {
            ListaPersona.Nodo nodoHijo = hijos.getCabeza();
            while (nodoHijo != null) {
                NodoPersona hijo = nodoHijo.persona;
                String hijoId = hijo.getNombreCompleto();

                // Si el nodo hijo no existe, agregarlo
                if (grafo.getNode(hijoId) == null) {
                    grafo.addNode(hijoId).setAttribute("ui.label", hijo.getNombreCompleto());
                }

                // Si el enlace entre el nodo y su hijo no existe, agregarlo
                if (grafo.getEdge(nodoId + "-" + hijoId) == null) {
                    grafo.addEdge(nodoId + "-" + hijoId, nodoId, hijoId);
                }

                // Llamada recursiva para agregar los descendientes
                agregarNodosYEnlacesGraficamente(hijo, grafo);
                nodoHijo = nodoHijo.siguiente;
            }
        }
    }

    
    /**
     * Obtiene una lista de los antepasados de un nodo específico en el árbol genealógico.
     * Esta función recursiva sube por el árbol desde el nodo dado hasta la raíz,
     * recolectando todos los antepasados en orden jerárquico.
     * @param nodo El nodo cuyo linaje ascendente se desea obtener.
     * @return ListaPersona con los antepasados del nodo dado, en orden jerárquico.
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
     * Muestra gráficamente los antepasados de un integrante específico en el árbol genealógico.
     * Utiliza el nombre del integrante para buscarlo en la tabla hash y luego muestra su linaje ascendente.
     * Los antepasados se muestran en un grafo visual donde cada nodo es un antepasado.
     * @param nombreIntegrante El nombre del integrante cuyos antepasados se desean mostrar.
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

        // Crear el grafo para mostrar los antepasados
        Graph grafo = new SingleGraph("Antepasados de " + nombreIntegrante);
        grafo.setStrict(false);
        grafo.setAutoCreate(true);

        // Agregar el nodo del integrante principal
        String nodoId = nodo.getNombreCompleto();
        grafo.addNode(nodoId).setAttribute("ui.label", nodo.getNombreCompleto());

        // Agregar los antepasados
        ListaPersona.Nodo nodoAntepasado = antepasados.getCabeza();
        NodoPersona anterior = nodo;
        while (nodoAntepasado != null) {
            NodoPersona antepasado = nodoAntepasado.getPersona();
            String antepasadoId = antepasado.getNombreCompleto();

            // Si el nodo no existe, agregarlo
            if (grafo.getNode(antepasadoId) == null) {
                grafo.addNode(antepasadoId).setAttribute("ui.label", antepasado.getNombreCompleto());
            }

            // Si el enlace no existe, agregarlo
            if (grafo.getEdge(anterior.getNombreCompleto() + "-" + antepasadoId) == null) {
                grafo.addEdge(anterior.getNombreCompleto() + "-" + antepasadoId, anterior.getNombreCompleto(), antepasadoId);
            }

            anterior = antepasado;
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

