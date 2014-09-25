package dubujaAutom;

import afn.AFN;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
 q0 -> q0 [ label = "0/1" ];
 q1 -> q2 [ label = "0/1" ];
 q2 -> q3 [ label = "0/1" ];  
 }
 -----------------------------------------
 */
public class DibujaAutomata {

    private ArrayList automata;

    public DibujaAutomata() {
        this.automata = new ArrayList();
        inicia();
    }

    private void inicia() {
        cargarArchivoTXT();
        obtenerEstadosFinales();
        obtenerEstadoInicial();
    }

    public void cargarArchivoTXT() {
        File ArchivoAutomata = null;
        FileReader fr = null;
        BufferedReader br = null;
        JFileChooser selector = null;

        try {
            selector = new JFileChooser();
            int resultado = selector.showOpenDialog(null);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                ArchivoAutomata = selector.getSelectedFile();
            }
            fr = new FileReader(ArchivoAutomata);
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

    public void generarDOT_DesdeArchivo() {
        FileWriter archivoAutomata = null;
        PrintWriter pw = null;
        String nombreAutomata = JOptionPane.showInputDialog("Por favor, indique el nombre del archivo DOT que describe al automata");

        pw.println("digraph finite_state_machine {");
        pw.println("node [shape = point ];" + obtenerEstadoInicial());
        pw.print("node [shape = doublecircle];");
        ArrayList estadosFinales = obtenerEstadosFinales();
        for (int i = 0; i < estadosFinales.size(); i++) {
            pw.print((String) estadosFinales.get(i) + ";");
        }
        pw.println();
        pw.println("node [shape = circle];");
        
        try {
            archivoAutomata = new FileWriter(nombreAutomata + ".txt");
            pw = new PrintWriter(archivoAutomata);
            for (int i = 0; i < automata.size(); i++) {
                String[] transicionesEstado = automata.get(i).toString().split(":");
                
            }
            pw.println("}");

        } catch (IOException ex) {
            Logger.getLogger(AFN.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (null != archivoAutomata) {
                    archivoAutomata.close();
                    JOptionPane.showMessageDialog(null, "El archivo " + nombreAutomata + ".dot se gener\u00f3 satisfactoriamente");
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
                System.out.println("Encontrado estado final: " + efinal);
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
                System.out.println("Encontrado estado inicial: " + eInicial);
                break;
            }
        }
        return eInicial;
    }

}

//http://www.graphviz.org/Documentation.php
//http://irisus90.wordpress.com/2011/06/25/uso-de-graphviz-desde-java/
//http://www.rdebug.com/2010/05/usar-graphviz-desde-java.html
//https://code.google.com/p/generadorpl/source/browse/trunk/src/dominio/Automata.java
