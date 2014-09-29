package dubujaAutom;

import afn.AFN;
import interfaz.Imagen;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author Isaac
 */
/*FORMATO DE ARCHIVO .DOT PARA AF
 -----------------------------------------
 digraph finite_state_machine {
 size="4,4"
 rankdir=LR;
 node [shape = doublecircle]; q3;
 node [shape = point ]; qi
 node [shape = circle];
 qi   -> q0;
 q0 -> q1 [ label = "0" ];
 q0 -> q0 [ label = "a1" ];
 q1 -> q2 [ label = "w" ];
 q2 -> q3 [ label = "otrosimbolo" ];  
 }
 -----------------------------------------
 */
public class DibujaAutomata {

    private ArrayList automata;
    String nombreAutomata;
    Imagen dibujaImagen;
    String nombreImagen;

    public DibujaAutomata(String ruta) {
        this.automata = new ArrayList();
        inicia(ruta);
    }

    //recibe: ruta del archivo txt
    private void inicia(String ruta) {
        cargarArchivoTXT(ruta);
        obtenerEstadosFinales();
        obtenerEstadoInicial();
        generarDOT_DesdeArchivo();
        ejecutarDOT();
        dibujaImagen = new Imagen(); // UN FRAME QUE SÓLO CARGA LA IMAGEN QUE SE GENERÓ
        dibujaImagen.agregarImagen("AutomatasGenerados\\" + nombreImagen); // parametro:  ruta de la imagen generada
        dibujaImagen.setVisible(true); //Mostrar el frame
    }

    public void cargarArchivoTXT(String ruta) {
        File archivoAutomata = null;
        FileReader fr = null;
        BufferedReader br = null;
        JFileChooser selector = null;

        try {
            if (ruta == null) {  // si no se pasa una ruta, hay que selecionar el archivo
                JOptionPane.showMessageDialog(null, "Por favor, seleccione el archivo TXT que contiene la descripci\u00f3n del automata");
                selector = new JFileChooser();
                int resultado = selector.showOpenDialog(null);
                if (resultado == JFileChooser.APPROVE_OPTION) {
                    archivoAutomata = selector.getSelectedFile();
                }
            } else {  // si se pasa una ruta, se carga el archivo inmediatamente
                archivoAutomata = new File(ruta);
            }
            fr = new FileReader(archivoAutomata);
            br = new BufferedReader(fr);
            // Lectura del fichero
            String linea;
            System.out.println("Cargando automata... ");
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
                automata.add(linea);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    //Este metodo genera un archivo .dot con el formato que requiere dot.exe para generar los dibujos de los automatas.
    //Puede traducir cualquier atchivo TXT que siga el formato mostrado por el profesor.
    public void generarDOT_DesdeArchivo() {
        FileWriter archivoAutomata = null;
        PrintWriter pw = null;
        nombreAutomata = JOptionPane.showInputDialog("Por favor, indique el nombre del archivo DOT que describe al automata");
        this.nombreAutomata += ".dot";
        try {
            //archivoAutomata = new FileWriter("C:\\GraphViz\\bin\\generadosyF\\" + nombreAutomata); // una ruta alternativa
            archivoAutomata = new FileWriter("ArchivosDOT\\" + nombreAutomata);

            pw = new PrintWriter(archivoAutomata);

            String estadoInicial = obtenerEstadoInicial();
            pw.println("digraph finite_state_machine {");
            pw.println("rankdir=LR;");
            pw.println("node [shape = point ]; qi;");
            pw.print("node [shape = doublecircle];");

            System.out.println("digraph finite_state_machine {");
            System.out.println("rankdir=LR;");
            System.out.println("node [shape = point ]; qi;");
            System.out.println("node [shape = doublecircle];");

            ArrayList estadosFinales = obtenerEstadosFinales();
            for (int i = 0; i < estadosFinales.size(); i++) {
                pw.print((String) estadosFinales.get(i) + ";");
                System.out.println((String) estadosFinales.get(i) + ";");
            }
            pw.println();
            pw.println("node [shape = circle];");
            pw.println("qi -> " + estadoInicial + ";");

            System.out.println("node [shape = circle];");
            System.out.println("qi   ->" + estadoInicial + ";");

            for (int l = 0; l < automata.size(); l++) { //ciclo principal, recorre automata
                String[] transicionesEstado = automata.get(l).toString().split(":"); // toma la linea completa:   0:a;1:b;-:c;-:@;-  y la parte por :
                for (int j = 1; j < transicionesEstado.length; j++) {  // resulta un arreglo del siguiente modo: transicionesEstado = {"0" ,  "a;1"  ,  "b;-"  ,  "c;-" ,  "@;-"}
                    String fragmentoTransic = transicionesEstado[j]; // toma un elemento del arreglo que se geeró despues de split
                    if (!fragmentoTransic.contains("-")) {
                        // se quita ; y ahora cada elemento es del tipo:   a,1,5,6,9 ...  (se sabe que el primer elemento es el simbolo de transición y los siguientes son estados)
                        String[] simboloyEstados = fragmentoTransic.replace(";", ",").split(",");
                        for (int k = 1; k < simboloyEstados.length; k++) {
                            //q2 -> q3 [ label = "0" ]; 
                            pw.println(transicionesEstado[0].replace("*", "") + " -> " + simboloyEstados[k] + "[ label = \"" + simboloyEstados[0] + "\" ];");
                            System.out.println(transicionesEstado[0].replace("*", "") + " -> " + simboloyEstados[k] + "[ label = \"" + simboloyEstados[0] + "\" ];");
                        }
                    }
                }
            }
            pw.println("}");
            System.out.println("}");

        } catch (IOException ex) {
            Logger.getLogger(AFN.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (null != archivoAutomata) {
                    archivoAutomata.close();
                    JOptionPane.showMessageDialog(null, "El archivo " + nombreAutomata + " se gener\u00f3 satisfactoriamente");
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public ArrayList obtenerEstadosFinales() {
        ArrayList estadosFinales = new ArrayList();
        for (int i = 0; i < automata.size(); i++) {
            String transicEstado = (String) automata.get(i);
            if (transicEstado.contains("**")) {
                String[] partirTransicEstado = transicEstado.split(":");
                String efinal = partirTransicEstado[0].replace("*", "");
                //System.out.println("Encontrado estado final: " + efinal);
                estadosFinales.add(efinal);
            }
        }
        return estadosFinales;
    }

    public String obtenerEstadoInicial() {
        String eInicial = "";
        for (int i = 0; i < automata.size(); i++) {
            String transicEstado = (String) automata.get(i);

            if (transicEstado.contains("*") && !transicEstado.contains("**")) {
                String[] partirTransicEstado = transicEstado.split(":");
                eInicial = partirTransicEstado[0].replace("*", "");
                //System.out.println("Encontrado estado inicial: " + eInicial);
                break;
            }
        }
        return eInicial;
    }


    // Este metodo usa  el método exec()  para ejecutar dot.exe como proceso. 
    // En consola (cmd) el formato de la instrucción sería el siguiente: 
    // dot.exe  -Tjpg RutaDelArchivo\ArchivoAutomata.dot  >  rutaDeImagenDeAutomataGenerado\nombreImagen.jpg
    
    public void ejecutarDOT() {
        nombreImagen = JOptionPane.showInputDialog("Para generar la imagen, por favor, indique el nombre que desea asignarle");
        nombreImagen += ".jpg";
        try {
            System.out.println("Ejecutando: -Tjpg C:\\GraphViz\\bin\\generadosyF\\" + nombreAutomata + " > C:\\GraphViz\\bin\\AutomatasGenerados\\" + nombreImagen);
            // String argumentos = "-Tjpg C:\\GraphViz\\bin\\generadosyF\\" + nombreAutomata + " > C:\\GraphViz\\bin\\AutomatasGenerados\\" + nombreImagen + ".jpg";
            String argumentos = "-Tjpg ArchivosDOT\\" + nombreAutomata + " > AutomatasGenerados\\" + nombreImagen;
            Process p = Runtime.getRuntime().exec("cmd /c C:\\GraphViz\\bin\\dot.exe " + argumentos);
            JOptionPane.showMessageDialog(null, "El archivo " + nombreImagen + " se gener\u00f3 satisfactoriamente");
        } catch (IOException ex) {
            Logger.getLogger(DibujaAutomata.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Ocurrieron problemas al intentar generar " + nombreImagen + " NO se gener\u00f3 correctamente.");
        }
    }
}

//Fuentes graphviz
//http://www.graphviz.org/Documentation.php
//http://irisus90.wordpress.com/2011/06/25/uso-de-graphviz-desde-java/
//http://www.rdebug.com/2010/05/usar-graphviz-desde-java.html
//https://code.google.com/p/generadorpl/source/browse/trunk/src/dominio/Automata.java
