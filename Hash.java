/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2edd;

/**
 * Clase Hash que implementa una tabla de dispersión (hash table) para almacenar objetos de tipo NodoPersona.
 * Utiliza un factor de carga y un mecanismo de redimensionamiento para mantener un rendimiento eficiente.
 * 
 * @author Cesar Augusto
 */
public class Hash {
    /**
     * Factor de carga máximo antes de redimensionar la tabla.
     */
    private final double FACTOR_CARGA = 0.75;

    /**
     * Número actual de elementos almacenados en la tabla.
     */
    private int numElementos = 0;

    /**
     * Tamaño actual de la tabla de dispersión.
     */
    private int tamanoTabla;

    /**
     * Arreglo que representa la tabla hash, donde cada índice contiene una lista de personas.
     */
    private ListaPersona[] tabla;

    /**
     * Constructor que inicializa la tabla de dispersión con un tamaño inicial predeterminado.
     */
    public Hash() {
        this.tamanoTabla = 100; // Tamaño inicial de la tabla
        this.tabla = new ListaPersona[tamanoTabla];
        inicializarCubetas();
    }

    /**
     * Método privado para inicializar cada índice de la tabla como una nueva lista de personas.
     */
    private void inicializarCubetas() {
        for (int i = 0; i < tamanoTabla; i++) {
            tabla[i] = new ListaPersona();
        }
    }

    /**
     * Función hash optimizada que genera un índice único basado en la clave proporcionada.
     * 
     * @param clave La clave para calcular el índice hash.
     * @return El índice hash calculado.
     */
    private int funcionHash(String clave) {
        int hash = 0;
        for (int i = 0; i < clave.length(); i++) {
            hash = (hash * 37 + clave.charAt(i)) & 0x7fffffff; // Evita valores negativos
        }
        return hash % tamanoTabla;
    }

    /**
     * Método privado para redimensionar la tabla de dispersión cuando se supera el factor de carga.
     * Reorganiza todos los elementos en una nueva tabla más grande.
     */
    private void redimensionar() {
        int nuevoTamano = tamanoTabla * 2;
        ListaPersona[] nuevaTabla = new ListaPersona[nuevoTamano];
        for (int i = 0; i < nuevoTamano; i++) {
            nuevaTabla[i] = new ListaPersona();
        }

        for (int i = 0; i < tamanoTabla; i++) {
            ListaPersona lista = tabla[i];
            ListaPersona.Nodo nodo = lista.getCabeza();
            while (nodo != null) {
                int nuevoIndice = funcionHash(nodo.persona.getMote()) % nuevoTamano;
                nuevaTabla[nuevoIndice].agregar(nodo.persona);
                nodo = nodo.siguiente;
            }
        }

        this.tabla = nuevaTabla;
        this.tamanoTabla = nuevoTamano;
    }

    /**
     * Inserta un NodoPersona en la tabla de dispersión. Si el factor de carga es superado,
     * redimensiona la tabla.
     * 
     * @param persona El nodo que contiene los datos de la persona a insertar.
     */
    public void insertar(NodoPersona persona) {
        if (contiene(persona.getMote())) {
            return; // Si ya existe, no se inserta nuevamente
        }

        if (numElementos >= tamanoTabla * FACTOR_CARGA) {
            redimensionar(); // Redimensiona si se supera el factor de carga
        }

        int indice = funcionHash(persona.getMote());
        System.out.println("Insertando: " + persona.getMote() + " en índice: " + indice);
        tabla[indice].agregar(persona);
        numElementos++;
    }

    /**
     * Busca un NodoPersona en la tabla de dispersión por su mote.
     * 
     * @param mote El mote de la persona que se desea buscar.
     * @return El nodo que contiene los datos de la persona, o null si no se encuentra.
     */
    public NodoPersona buscar(String mote) {
        mote = mote.toLowerCase().trim(); // Normalización
        int indice = funcionHash(mote);
        System.out.println("Buscando: " + mote + " en índice: " + indice);
        return tabla[indice].buscar(mote);
    }

    /**
     * Verifica si una persona con un mote específico existe en la tabla de dispersión.
     * 
     * @param mote El mote de la persona a verificar.
     * @return true si la persona existe, false en caso contrario.
     */
    public boolean contiene(String mote) {
        int indice = funcionHash(mote);
        NodoPersona persona = tabla[indice].buscar(mote);
        return persona != null;
    }

    /**
     * Elimina un NodoPersona de la tabla de dispersión por su mote.
     * 
     * @param mote El mote de la persona a eliminar.
     */
    public void eliminar(String mote) {
        int indice = funcionHash(mote);
        NodoPersona persona = tabla[indice].buscar(mote);
        if (persona != null) {
            tabla[indice].eliminar(persona);
            numElementos--;
        }
    }
}
