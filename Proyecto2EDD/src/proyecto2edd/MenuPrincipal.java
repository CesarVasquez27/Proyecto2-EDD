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
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);  // No abrir en una nueva ventana
        panelPrincipal.add(viewPanel, BorderLayout.CENTER);

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
            // Lógica para cargar el árbol genealógico desde el archivo
            System.out.println("Cargando árbol genealógico desde: " + rutaArchivo);
            // TODO: Implementar la lógica para cargar el árbol genealógico
        }
    }

    /**
     * Método para ver el registro de un integrante del árbol genealógico.
     */
    private void verRegistro() {
        // Lógica para ver el registro
        String nombreIntegrante = JOptionPane.showInputDialog(this, "Ingrese el nombre del integrante:");
        if (nombreIntegrante != null) {
            System.out.println("Mostrando registro de: " + nombreIntegrante);
            // TODO: Implementar la lógica para mostrar el registro del integrante
        }
    }

    /**
     * Método para buscar un integrante por su nombre en el árbol genealógico.
     */
    private void buscarPorNombre() {
        // Lógica para buscar por nombre
        String nombreBusqueda = JOptionPane.showInputDialog(this, "Ingrese el nombre a buscar:");
        if (nombreBusqueda != null) {
            System.out.println("Buscando integrante con nombre: " + nombreBusqueda);
            // TODO: Implementar la lógica para buscar el integrante por nombre
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
