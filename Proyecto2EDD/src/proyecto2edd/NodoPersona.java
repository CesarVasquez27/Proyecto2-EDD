/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto2edd;

/**
 *
 * @author Christian
 */
public class NodoPersona {
    private String nombreCompleto;
    private String numeral;         // Ejemplo: "II" para William II
    private NodoPersona padre;      // Referencia al padre
    private String mote;            // Apodo único
    private String tituloNobiliario; // Título nobiliario
    private ListaPersona hijos;     // Lista de hijos
    private String antecedentes;    // Información adicional

    // Constructor principal

    /**
     *
     * @param nombreCompleto
     * @param numeral
     * @param padre
     * @param mote
     * @param tituloNobiliario
     * @param antecedentes
     */
    public NodoPersona(String nombreCompleto, String numeral, NodoPersona padre, String mote, String tituloNobiliario, String antecedentes) {
        this.nombreCompleto = nombreCompleto;
        this.numeral = numeral;
        this.padre = padre;
        this.mote = mote;
        this.tituloNobiliario = tituloNobiliario;
        this.antecedentes = antecedentes;
        this.hijos = new ListaPersona(); // Inicializamos la lista de hijos vacía
    }

    // getters y setters
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getNumeral() {
        return numeral;
    }

    public void setNumeral(String numeral) {
        this.numeral = numeral;
    }

    public NodoPersona getPadre() {
        return padre;
    }

    public void setPadre(NodoPersona padre) {
        this.padre = padre;
    }

    public String getMote() {
        return mote;
    }

    public void setMote(String mote) {
        this.mote = mote;
    }

    public String getTituloNobiliario() {
        return tituloNobiliario;
    }

    public void setTituloNobiliario(String tituloNobiliario) {
        this.tituloNobiliario = tituloNobiliario;
    }

    public ListaPersona getHijos() {
        return hijos;
    }

    public void agregarHijo(NodoPersona hijo) {
        hijos.agregar(hijo);
        hijo.setPadre(this); // Establece este nodo como el padre del hijo
    }

    public String getAntecedentes() {
        return antecedentes;
    }

    public void setAntecedentes(String antecedentes) {
        this.antecedentes = antecedentes;
    }

    @Override
    public String toString() {
        return "Nombre: " + nombreCompleto + " " + numeral + ", Mote: " + mote +
               ", Título: " + tituloNobiliario + ", Antecedentes: " + antecedentes;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NodoPersona otraPersona = (NodoPersona) obj;
        return nombreCompleto.equals(otraPersona.nombreCompleto); // Comparar por nombre completo
    }
    @Override
    public int hashCode() {
        return nombreCompleto.hashCode(); // Usar nombreCompleto para el hash
    }
}
