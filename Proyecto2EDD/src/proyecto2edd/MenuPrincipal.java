/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto2edd;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;

/**
 *
 * @author tomas
 */

/**
 * Clase principal para la interfaz gráfica del Visor de Árbol Genealógico.
 * Extiende JFrame y configura los componentes gráficos y las funcionalidades básicas.
 */
public class MenuPrincipal extends JFrame {

    private Graph graph;
    private Arbol arbolGenealogico;

    /**
     * Constructor de MenuPrincipal.
     * Configura la ventana principal y añade los componentes gráficos.
     */
    public MenuPrincipal() {
        // Configuración inicial de la ventana
        setTitle("Visor de Árbol Genealógico");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializar el grafo visual
        inicializarGrafo();

        // Crear panel principal y añadir componentes
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        getContentPane().add(panelPrincipal);

        // Añadir el componente del grafo al panel
        Viewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);
        viewer.enableAutoLayout();

        panelPrincipal.add(viewPanel, BorderLayout.CENTER);

        // Añadir el panel de botones
        JPanel panelBotones = new JPanel();
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // Botones
        agregarBotones(panelBotones);
    }

    /**
     * Inicializa el grafo visual con un diseño básico.
     */
    private void inicializarGrafo() {
        graph = new SingleGraph("Árbol Genealógico");

        // Estilo del grafo
        String stylesheet = """
            node {
                fill-color: red;
                size: 20px;
                text-size: 15px;
                text-alignment: center;
            }
            edge {
                fill-color: gray;
                size: 2px;
            }
        """;
        graph.setAttribute("ui.stylesheet", stylesheet);
    }

    /**
     * Añade los botones al panel inferior.
     * @param panelBotones Panel donde se añaden los botones.
     */
    private void agregarBotones(JPanel panelBotones) {
        // Botón para cargar un árbol genealógico
        JButton btnCargarArbol = new JButton("Cargar Árbol Genealógico");
        panelBotones.add(btnCargarArbol);
        btnCargarArbol.addActionListener(e -> cargarArbolGenealogico());

        // Botón para ver registro
        JButton btnVerRegistro = new JButton("Ver Registro");
        panelBotones.add(btnVerRegistro);
        btnVerRegistro.addActionListener(e -> verRegistro());

        // Botón para buscar por nombre
        JButton btnBuscarPorNombre = new JButton("Buscar por Nombre");
        panelBotones.add(btnBuscarPorNombre);
        btnBuscarPorNombre.addActionListener(e -> buscarPorNombre());

        // Botón para mostrar antepasados
        JButton btnMostrarAntepasados = new JButton("Mostrar Antepasados");
        panelBotones.add(btnMostrarAntepasados);
        btnMostrarAntepasados.addActionListener(e -> mostrarAntepasados());
    }

    /**
     * Método para cargar un nuevo árbol genealógico desde un archivo JSON.
     */
    private void cargarArbolGenealogico() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String rutaArchivo = fileChooser.getSelectedFile().getPath();
            try (FileReader reader = new FileReader(rutaArchivo)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                arbolGenealogico = new Arbol();
                arbolGenealogico.cargarArbolDesdeJSON(rutaArchivo);
                actualizarGrafo();
                JOptionPane.showMessageDialog(this, "Árbol genealógico cargado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar el archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Actualiza el grafo visual basado en el árbol genealógico cargado.
     */
    private void actualizarGrafo() {
        try {
            arbolGenealogico.mostrarArbolGraficamente(graph);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al mostrar el árbol: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para ver el registro de un integrante del árbol genealógico.
     */
    private void verRegistro() {
        if (arbolGenealogico == null) {
            JOptionPane.showMessageDialog(this, "Primero cargue un árbol genealógico.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombreIntegrante = JOptionPane.showInputDialog(this, "Ingrese el nombre del integrante:");
        if (nombreIntegrante != null) {
            NodoPersona persona = arbolGenealogico.buscarNodo(nombreIntegrante, arbolGenealogico.obtenerRaiz());
            if (persona != null) {
                JOptionPane.showMessageDialog(this, persona.toString(), "Registro de " + nombreIntegrante, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Integrante no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Método para buscar un integrante por su nombre.
     */
    private void buscarPorNombre() {
        // Similar a verRegistro, pero adaptado para búsquedas generales
        verRegistro();
    }

    /**
     * Método para mostrar los antepasados de un integrante.
     */
    private void mostrarAntepasados() {
        if (arbolGenealogico == null) {
            JOptionPane.showMessageDialog(this, "Primero cargue un árbol genealógico.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombreIntegrante = JOptionPane.showInputDialog(this, "Ingrese el nombre del integrante:");
        if (nombreIntegrante != null) {
            arbolGenealogico.mostrarAntepasadosGraficamente(nombreIntegrante);
        }
    }

    /**
     * Método principal para ejecutar la aplicación.
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}
