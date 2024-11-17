/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto2edd;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author tomas
 */

public class MenuPrincipal extends JFrame {

    private Graph graph;

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
    }

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}






