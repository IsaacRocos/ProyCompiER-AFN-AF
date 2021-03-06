package afn;

import dubujaAutom.DibujaAutomata;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * 
 *
 * @author Isaac
 */
public class AFN {

    private char[] regExp; // Guarda la expresión en posfijo, en un arreglo de caracteres.
    private Stack<String[]> automata;  //Almacena la información del automata.
    private Stack<int[]> inicio_fin; // Pila que almacena los pares de estados [Inicio-Fin] que se generan.
    private int ultimoEstadoInicial = 0;
    private int ultimoEstadoFinal = 0;
    private ArrayList<String> alfabeto;
    private boolean ocurrioError;
    public ArrayList<String> conjuntoDeEstados;
    public ArrayList<String> automataFormateado;  //Almacena la información del automata.
    String nombreAutomata=null;

    public AFN(String regExp, ArrayList<String> alfabeto) {
        this.automata = new Stack<>();
        this.inicio_fin = new Stack<>();
        this.alfabeto = new ArrayList<>();
        this.conjuntoDeEstados = new ArrayList<>();
        this.automataFormateado = new ArrayList<>();

        System.out.println("Se va a convertir: << " + regExp + " >> en un AFN");
        setRegExp(regExp);
        setAlfabeto(alfabeto);
        System.out.println("Alfabeto recibido:");
        imprimirAlfabeto();
    }

    public void setAlfabeto(ArrayList<String> alfabeto) {
        this.alfabeto = alfabeto;
    }
    
    public ArrayList getAlfabeto(){
        return this.alfabeto;
    }

    public void setRegExp(String regExp) {
        this.regExp = new char[regExp.length()];
        this.regExp = regExp.toCharArray();
    }

    public void iniciarCreaAFN() {
        char elementoExpAnalizar; // alamacena el caracter que se analiza en la expresión regular (sólo un caracter)
        String elementoTansicion = "";  // Se usa para poder identificar si el elemento del alfabeto está compuesto por más de un caracter como  "a1" o "bn" , etc.
        for (int i = 0; i < regExp.length; i++) {
            System.out.println("Analizando elemento " + i + " de la expresion regular...");
            elementoExpAnalizar = regExp[i];
            switch (elementoExpAnalizar) {
                case '|':
                    creaAutomataUnion();
                    break;
                case '-':
                    creaAutomataConcatenacion();
                    break;
                case '*':
                    creaAutomataCerradura(0);
                    break;
                case '+':
                    creaAutomataCerradura(1);
                    break;
                default://CARACTERES DE TRANSICION
                    try {
                        elementoTansicion = "" + elementoExpAnalizar;
                        while (!alfabeto.contains(elementoTansicion) || !alfabeto.contains(regExp[i + 1] + "")) {
                            if (regExp[i + 1] != '|' && regExp[i + 1] != '*' && regExp[i + 1] != '-' && regExp[i + 1] != '+') {
                                elementoTansicion += regExp[i + 1];
                            } else {
                                break;
                            }
                            //System.out.println("Buscando en alfabeto: " + elementoAlfAnalizar);
                            i++;
                        }
                        System.out.println("Encontrado: " + elementoTansicion);
                        creaAutomataSimple(elementoTansicion);
                    } catch (ArrayIndexOutOfBoundsException aiobe) {
                        aiobe.getStackTrace();
                    }
                    break;
            }//switch
        }//for
        imprimirEstados();

        if (ocurrioError) {
            int n = JOptionPane.showConfirmDialog(null,
                    "Se presentaron problemas al generar el automata.\n¿Desea continuar con la generación del archivo?",
                    "Ups, ¡hay problemas!",
                    JOptionPane.YES_NO_OPTION);
            // si= 0,  no = 1
            if (n == 0) {
                 generarArchivoAutomata();
            } else {
                JOptionPane.showMessageDialog(null, "Lamentamos los inconvenientes.\nPor favor, intente ingresar una nueva espresion regular valida\nPrometemos no fallar esta vez :)");
            }
        } else {
            generarFormatoAutomata();
            generarArchivoAutomata();
            int n = JOptionPane.showConfirmDialog(null,
                    "Se puede proceder con el proceso para dibujar el automata.\n¿Desea continuar?",
                    "¿Dibujar?",
                    JOptionPane.YES_NO_OPTION);
            // si= 0,  no = 1
            if (n == 0) {
                DibujaAutomata dibujar = new DibujaAutomata(nombreAutomata);
            } else {  
                JOptionPane.showMessageDialog(null, "Puede dibujar el automata cuando lo desee,\nseleccioando la opcion \"Dibujar automata\" de la ventana principal.");
            }
            
        }

    }//inicia crear automata

    public void creaAutomataSimple(String elementoAnalizar) {
        try {
            System.out.println("Generando automata simple...");
            String[] automataHijo = new String[3];
            int[] estados_inicio_fin = new int[2];

            if (ultimoEstadoFinal == 0) { //en caso de que sea el primer automata generado para la regexp, le corresponde el primer valor, que es 0.
                automataHijo[0] = "0";
            } else {
                automataHijo[0] = (1 + ultimoEstadoFinal) + "";
            }
            automataHijo[1] = elementoAnalizar;
            automataHijo[2] = (Integer.parseInt(automataHijo[0]) + 1) + "";

            setUltimoEstadoInicial(Integer.parseInt(automataHijo[0]));
            setUltimoEstadoFinal(Integer.parseInt(automataHijo[2]));

            estados_inicio_fin[0] = Integer.parseInt(automataHijo[0]);
            estados_inicio_fin[1] = Integer.parseInt(automataHijo[2]);

            setConjuntoDeEstados(estados_inicio_fin[0] + "");
            setConjuntoDeEstados(estados_inicio_fin[1] + "");

            agregarAPilaAFN(automataHijo);
            agregarAPilaInicio_Fin(estados_inicio_fin);
            imprimirPilaInicioFin();
            System.out.println("Generado: (" + Integer.parseInt(automataHijo[0]) + ")--" + automataHijo[1] + "--(" + Integer.parseInt(automataHijo[2]) + ")");
        } catch (EmptyStackException ese) {
            ese.getStackTrace();
            System.out.println("La pila est\u00E1 vac\u00CDa");
            setBanderaError();
        }
    }

    public void creaAutomataUnion() {
        try {
            System.out.println("Generando automata para Union...");
            String[] automataHijo = new String[3];
            int[] inicio_finTope = inicio_fin.pop();
            int[] inicio_finFondo = inicio_fin.pop();

            automataHijo[0] = (1 + ultimoEstadoFinal) + "";
            automataHijo[1] = "@";
            automataHijo[2] = inicio_finFondo[0] + "";

            agregarAPilaAFN(automataHijo);

            automataHijo = new String[3];
            automataHijo[0] = (1 + ultimoEstadoFinal) + "";
            automataHijo[1] = "@";
            automataHijo[2] = inicio_finTope[0] + "";

            agregarAPilaAFN(automataHijo);

            setUltimoEstadoInicial(1 + ultimoEstadoFinal);
            setUltimoEstadoFinal(1 + ultimoEstadoInicial);

            automataHijo = new String[3];
            automataHijo[0] = inicio_finFondo[1] + "";
            automataHijo[1] = "@";
            automataHijo[2] = (ultimoEstadoFinal) + "";

            agregarAPilaAFN(automataHijo);

            automataHijo = new String[3];
            automataHijo[0] = inicio_finTope[1] + "";
            automataHijo[1] = "@";
            automataHijo[2] = (ultimoEstadoFinal) + "";
            agregarAPilaAFN(automataHijo);

            int[] nuevoInicioFin = new int[2];
            nuevoInicioFin[0] = this.ultimoEstadoInicial;
            nuevoInicioFin[1] = this.ultimoEstadoFinal;

            setConjuntoDeEstados(nuevoInicioFin[0] + "");
            setConjuntoDeEstados(nuevoInicioFin[1] + "");

            agregarAPilaInicio_Fin(nuevoInicioFin);
            imprimirPilaAutomata();
            imprimirPilaInicioFin();
        } catch (EmptyStackException ese) {
            ese.getStackTrace();
            System.out.println("La pila est\u00E1 vac\u00CDa");
            setBanderaError();
        }
    }

    public void creaAutomataConcatenacion() {
        try {
            System.out.println("Generando automata para concatenacion...");
            int[] inicio_finTope = inicio_fin.pop();
            int[] inicio_finFondo = inicio_fin.pop();
            int[] inicio_finNuevo = new int[2];
            String[] transicTope = automata.pop();
            conjuntoDeEstados.remove(transicTope[0]);
            //System.out.println("El pop con que se trabaja es: " + inicio_finTope[0] + ","+ inicio_finTope[1]);

            // 1) Cambiar estado inicial de la ultima transicion en el automata, por el estado final en la pila Inicio-Fin.
            transicTope[0] = inicio_finFondo[1] + "";

            // 2) Cambiar el estado final en la pila Inicio-Fin por estado final de la ultima transicion en el automata.
            inicio_finFondo[1] = inicio_finTope[1];

            //3) Agregar nuevo Inicio-Fin a la pila y nuevo estado al automata
            agregarAPilaInicio_Fin(inicio_finFondo);

            agregarAPilaAFN(transicTope);

            //4) Actualizar variable de clase ultimoEstadoFinal
            setUltimoEstadoFinal(inicio_finFondo[1]);

            imprimirPilaAutomata();
            imprimirPilaInicioFin();

        } catch (EmptyStackException ese) {
            ese.getStackTrace();
            System.out.println("La pila est\u00E1 vac\u00CDa");
            setBanderaError();
        }
    }

    // Recibe 0 para cerradura epsilon y 1 para cerradura positiva
    public void creaAutomataCerradura(int tipoCerradura) {
        try {
            if (tipoCerradura == 0) {
                System.out.println("Generando automata para cerradura epsilon...");
            } else {
                System.out.println("Generando automata para cerradura positiva...");
            }

            int[] inicio_finTope = inicio_fin.pop();
            //System.out.println("-Tomado de pila inicio_fin: " + inicio_finTope[0] + "," +inicio_finTope[1]);
//-----  FIN+1  -->  INICIO
            String[] automataHijo = new String[3];
            automataHijo[0] = (inicio_finTope[1] + 1) + "";
            automataHijo[1] = "@";
            automataHijo[2] = inicio_finTope[0] + "";
            agregarAPilaAFN(automataHijo);
            if (tipoCerradura == 0) {
//----- FIN+1  --> FIN+2
                automataHijo = new String[3];
                automataHijo[0] = (inicio_finTope[1] + 1) + "";
                automataHijo[1] = "@";
                automataHijo[2] = (inicio_finTope[1] + 2) + "";
                agregarAPilaAFN(automataHijo);
            }
//----- FIN -->   FIN+2
            automataHijo = new String[3];
            automataHijo[0] = (inicio_finTope[1]) + "";
            automataHijo[1] = "@";
            automataHijo[2] = (inicio_finTope[1] + 2) + "";
            agregarAPilaAFN(automataHijo);
//----- FIN -->   INICIO
            automataHijo = new String[3];
            automataHijo[0] = (inicio_finTope[1]) + "";
            automataHijo[1] = "@";
            automataHijo[2] = (inicio_finTope[0]) + "";

            System.out.println("Nuevo estado final: " + (ultimoEstadoFinal + 1));
            System.out.println("Nuevo estado final: " + (ultimoEstadoFinal + 2));

            setUltimoEstadoInicial(ultimoEstadoFinal + 1);
            setUltimoEstadoFinal(ultimoEstadoFinal + 2);

            inicio_finTope[0] = this.ultimoEstadoInicial;
            inicio_finTope[1] = this.ultimoEstadoFinal;

            setConjuntoDeEstados(inicio_finTope[0] + "");
            setConjuntoDeEstados(inicio_finTope[1] + "");

            agregarAPilaAFN(automataHijo);
            agregarAPilaInicio_Fin(inicio_finTope);
            imprimirPilaAutomata();
            imprimirPilaInicioFin();
        } catch (EmptyStackException ese) {
            ese.getStackTrace();
            System.out.println("La pila est\u00E1 vac\u00CDa");
            setBanderaError();
        }
    }

    public void setUltimoEstadoInicial(int uei) {
        ultimoEstadoInicial = uei;
    }

    public void setUltimoEstadoFinal(int uef) {
        ultimoEstadoFinal = uef;
    }

    public void agregarAPilaAFN(String[] automata) {
        this.automata.push(automata);
    }

    public void agregarAPilaInicio_Fin(int[] inicio_fin) {
        this.inicio_fin.push(inicio_fin);
    }

    public void setConjuntoDeEstados(String estado) {
        if (!conjuntoDeEstados.contains(estado)) {
            conjuntoDeEstados.add(estado);
        }
    }

    public void imprimirPilaAutomata() {
        System.out.println("-----------AUTOMATA-------------");
        for (int i = 0; i < automata.size(); i++) {
            String[] transic = automata.get(i);
            System.out.println("(" + Integer.parseInt(transic[0]) + ")-- " + transic[1] + " --(" + Integer.parseInt(transic[2]) + ")");
        }
        System.out.println("--------------------------------");
    }

    public void imprimirPilaInicioFin() {
        System.out.println("----------INICIO-FIN------------");
        for (int i = 0; i < inicio_fin.size(); i++) {
            int[] transic = inicio_fin.get(i);
            System.out.println(transic[0] + "," + transic[1]);
        }
        System.out.println("------------------------------");
    }


    public void imprimirAlfabeto() {
        System.out.println("----------ALFABETO------------");
        for (int i = 0; i < alfabeto.size(); i++) {
            String transic = alfabeto.get(i);
            System.out.println("-"+transic+"-");
        }
        System.out.println("------------------------------");
    }

    
    public void imprimirEstados() {
        System.out.println("----------ESTADOS------------");
        for (int i = 0; i < this.conjuntoDeEstados.size(); i++) {
            String transic = conjuntoDeEstados.get(i);
            System.out.println("(" + transic + ")");
        }
        System.out.println("------------------------------");
    }

    public void setBanderaError() {
        this.ocurrioError = true;
    }

    private void generarFormatoAutomata() {
        System.out.println("---FORMATO FINAL DE AUTOMATA---");
        Iterator iteraEstados = conjuntoDeEstados.iterator();
        int indice = 0;
        while (iteraEstados.hasNext()) {
            String lineaDescripcionEstado = "";
            String estado = (String) iteraEstados.next(); //estado actual
            if (estado.equals(inicio_fin.peek()[0] + "")) {  // es estado inicial?
                lineaDescripcionEstado = estado + "*";
            } else if (estado.equals(inicio_fin.peek()[1] + "")) { // es estado final?
                lineaDescripcionEstado = estado + "**";
            } else {
                lineaDescripcionEstado = estado;  // entonces es estado normal
            }
            // Buscar las transiciones del estado actual.
            // Toma cada elemento del alfabeto y lo busca en las transiciones de "estado".
            for (int i = 0; i < alfabeto.size(); i++) {
                String letraAlfab = alfabeto.get(i); // Toma la i-esima letra.
                //Ahora a buscar en el automata las transiciones para "estado" con "letraAlfab"
                Iterator iteraAutomata = automata.iterator();
                int encontrado = 0;
                while (iteraAutomata.hasNext()) {

                    String[] transicion = (String[]) iteraAutomata.next();  // Almacenará la info de una transicion del automata.
                    if (estado.equals(transicion[0]) && letraAlfab.equals(transicion[1])) { // Si encuentra el estado al inicio de la transicion actual

                        if (i != (alfabeto.size() - 1)) { // si no se analiza el último elemento del alfabeto
                            if (encontrado == 0) { // si es el primer elemento que coincide
                                lineaDescripcionEstado += ":" + transicion[1] + ";" + transicion[2];
                            } else { // si ya se habían encontrado otras transiciones
                                lineaDescripcionEstado += "," + transicion[2];
                            }
                        } else { // si es el último elemento
                            if (encontrado == 0) { // si el último elemento es el único que coincide con una transicion
                                lineaDescripcionEstado += ":" + transicion[1] + ";" + transicion[2];
                            } else {
                                lineaDescripcionEstado += "," + transicion[2];
                            }
                        }
                        encontrado = 1;
                    }
                }//while
                if (encontrado == 0) {
                    lineaDescripcionEstado += ":" + letraAlfab + ";-";
                }
            }// for
            System.out.println(lineaDescripcionEstado);
            automataFormateado.add(lineaDescripcionEstado);
        }// while
        System.out.println("------------------------------");
    }//gen arch

    private void generarArchivoAutomata() {
        FileWriter archivoAutomata = null;
        PrintWriter pw = null;
        this.nombreAutomata = "AFN_TXT\\" + JOptionPane.showInputDialog("Por favor, indique el nombre del archivo que almacena al automata")+".txt";
            
        try {
            archivoAutomata = new FileWriter(nombreAutomata);
            pw = new PrintWriter(archivoAutomata);
            for (int i = 0; i < automataFormateado.size(); i++) {
                pw.println(automataFormateado.get(i));
            }

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
    
    public String getRutaArchivoAFN(){
        return this.nombreAutomata;
    }
    
}//clase

