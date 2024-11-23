/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto2edd;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.swingViewer.SwingViewer;
import org.graphstream.ui.swingViewer.ViewPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

        // Inicializar el grafo
        inicializarGrafo();

        // Crear panel principal y añadir componentes
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        getContentPane().add(panelPrincipal);

        // Añadir el componente del grafo al panel
        Viewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);  // Panel donde se muestra el grafo
        viewer.enableAutoLayout();  // Habilitar el diseño automático para los nodos y aristas

        if (viewPanel != null) {
            // Ajustar la vista para que todo el grafo sea visible dentro del panel
            viewer.getDefaultView().getCamera().setViewPercent(1.0);  
            viewer.getDefaultView().getCamera().resetView();  // Restablecer la vista para centrar el grafo
            panelPrincipal.add(viewPanel, BorderLayout.CENTER);  // Añadir el panel al panel principal
        } else {
            System.out.println("Error: La vista generada no es de tipo ViewPanel");
        }

        // Añadir el panel de botones
        JPanel panelBotones = new JPanel();
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // Botón para cargar un nuevo árbol genealógico
        JButton btnCargarArbol = new JButton("Cargar Árbol Genealógico");
        panelBotones.add(btnCargarArbol);
        btnCargarArbol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarArbolGenealogico();
            }
        });

        // Botón para ver registro
        JButton btnVerRegistro = new JButton("Ver Registro");
        panelBotones.add(btnVerRegistro);
        btnVerRegistro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verRegistro();
            }
        });

        // Botón para buscar por nombre
        JButton btnBuscarPorNombre = new JButton("Buscar por Nombre");
        panelBotones.add(btnBuscarPorNombre);
        btnBuscarPorNombre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarPorNombre();
            }
        });

        // Botón para mostrar antepasados
        JButton btnMostrarAntepasados = new JButton("Mostrar Antepasados");
        panelBotones.add(btnMostrarAntepasados);
        btnMostrarAntepasados.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarAntepasados();
            }
        });
    }

    /**
     * Inicializa el grafo con algunos nodos de ejemplo.
     */
    private void inicializarGrafo() {
        graph = new SingleGraph("Árbol Genealógico");

        // Estilos opcionales para el grafo
        graph.setAttribute("ui.stylesheet", "node { fill-color: red; } node.marked { fill-color: blue; }");

        graph.addNode("Padre").setAttribute("ui.label", "Padre");
        graph.addNode("Hijo1").setAttribute("ui.label", "Hijo1");
        graph.addNode("Hijo2").setAttribute("ui.label", "Hijo2");

        graph.addEdge("Padre-Hijo1", "Padre", "Hijo1");
        graph.addEdge("Padre-Hijo2", "Padre", "Hijo2");
    }

    /**
     * Método para cargar un nuevo árbol genealógico desde un archivo JSON.
     */
    private void cargarArbolGenealogico() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Ruta del archivo seleccionado
            String rutaArchivo = fileChooser.getSelectedFile().getPath();
            try (FileReader reader = new FileReader(rutaArchivo)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                arbolGenealogico = new Arbol();
                arbolGenealogico.cargarArbolDesdeJSON(rutaArchivo);
                System.out.println("Árbol genealógico cargado correctamente.");
            } catch (IOException e) {
                System.out.println("Error al leer el archivo: " + e.getMessage());
            }
        }
    }

    /**
     * Método para ver el registro de un integrante del árbol genealógico.
     */
    private void verRegistro() {
        String nombreIntegrante = JOptionPane.showInputDialog(this, "Ingrese el nombre del integrante:");
        if (nombreIntegrante != null && arbolGenealogico != null) {
            NodoPersona persona = arbolGenealogico.buscarNodo(nombreIntegrante, arbolGenealogico.obtenerRaiz());
            if (persona != null) {
                JOptionPane.showMessageDialog(this, persona.toString(), "Registro de " + nombreIntegrante, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Integrante no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Primero cargue un árbol genealógico.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para buscar un integrante por su nombre en el árbol genealógico.
     */
    private void buscarPorNombre() {
        String nombreBusqueda = JOptionPane.showInputDialog(this, "Ingrese el nombre a buscar:");
        if (nombreBusqueda != null && arbolGenealogico != null) {
            NodoPersona persona = arbolGenealogico.buscarNodo(nombreBusqueda, arbolGenealogico.obtenerRaiz());
            if (persona != null) {
                JOptionPane.showMessageDialog(this, persona.toString(), "Resultado de búsqueda", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Integrante no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Primero cargue un árbol genealógico.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para mostrar los antepasados de un integrante específico del árbol genealógico.
     */
    private void mostrarAntepasados() {
        String nombreIntegrante = JOptionPane.showInputDialog(this, "Ingrese el nombre del integrante:");
        if (nombreIntegrante != null && arbolGenealogico != null) {
            arbolGenealogico.mostrarAntepasadosGraficamente(nombreIntegrante);
        } else {
            JOptionPane.showMessageDialog(this, "Primero cargue un árbol genealógico.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método principal para ejecutar la aplicación.
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}
