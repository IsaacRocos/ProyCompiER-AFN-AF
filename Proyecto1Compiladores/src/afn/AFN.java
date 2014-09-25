package afn;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Stack;
import javax.swing.JOptionPane;

/**
 * ffffffffa
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

    public AFN(String regExp, ArrayList<String> alfabeto) {
        this.automata = new Stack<>();
        this.inicio_fin = new Stack<>();
        this.alfabeto = new ArrayList<>();
        this.conjuntoDeEstados = new ArrayList<>();

        System.out.println("Se va a convertir: << " + regExp + " >> en un AFN");
        setRegExp(regExp);
        setAlfabeto(alfabeto);
    }

    public void setAlfabeto(ArrayList<String> alfabeto) {
        this.alfabeto = alfabeto;
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
                // generarArchivoAutomata();
            } else {
                JOptionPane.showMessageDialog(null, "Lamentamos los inconvenientes.\nPor favor, intente ingresar una nueva espresion regular valida\nPrometemos no fallar esta vez :)");
            }
        } else {
            generarArchivoAutomata();
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
        System.out.println("----------AUTOMATA------------");
        for (int i = 0; i < automata.size(); i++) {
            String[] transic = automata.get(i);
            System.out.println("(" + Integer.parseInt(transic[0]) + ")-- " + transic[1] + " --(" + Integer.parseInt(transic[2]) + ")");
        }
        System.out.println("------------------------------");
    }

    public void imprimirPilaInicioFin() {
        System.out.println("----------INICIO-FIN------------");
        for (int i = 0; i < inicio_fin.size(); i++) {
            int[] transic = inicio_fin.get(i);
            System.out.println(transic[0] + "," + transic[1]);
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

    private void generarArchivoAutomata() {
        /*
         6*:a;,-:b;,-:@;,4,7
         4:a;,-:b;,-:@;,0,2
         */
        Stack automataAux = automata;

        Iterator iteraEstados = conjuntoDeEstados.iterator();
        int indice = 0;


        while (iteraEstados.hasNext()) {
            String lineaDescripcionEstado = "";
            String estado = (String) iteraEstados.next();
            if (estado.equals(inicio_fin.peek()[0] + "")) {
                lineaDescripcionEstado = estado + "*:";
            } else if (estado.equals(inicio_fin.peek()[1] + "")) {
                lineaDescripcionEstado = estado + "**:";
            } else {
                lineaDescripcionEstado = estado + ":";
            }

            for (int i = 0; i < alfabeto.size(); i++) {
                // Toma cada elemento del alfabeto y lo busca en las transiciones de "estado"

                String letraAlf = alfabeto.get(i); // Toma la i-esima letra.
                Iterator iteraAutomataAux = automataAux.iterator();
                String[] transicion; // Almacenará la info de una transicion.
                while (iteraAutomataAux.hasNext()) {
                    int encontrado = 0;
                    transicion = (String[]) iteraAutomataAux.next();
                    if (estado.equals(transicion[0])) { // Si encuentra el estado al inicio de la transicion actual
                        if (i != (alfabeto.size() - 1)) {
                            lineaDescripcionEstado += transicion[1] + ","; // 
                        }else{
                            
                        }
                    }
                }
                transicion = (String[]) iteraEstados.next();
                String eInicial = transicion[0];
                indice++;
            }
        }
    }//gen arch
}//clase
//http://www.graphviz.org/Documentation.php
//http://irisus90.wordpress.com/2011/06/25/uso-de-graphviz-desde-java/
//http://www.rdebug.com/2010/05/usar-graphviz-desde-java.html
//https://code.google.com/p/generadorpl/source/browse/trunk/src/dominio/Automata.java
/*
 para iterar alfabeto
 for(int i=0; i<alfabeto.size();i++){
 String simboloAlf = alfabeto.get(i);
                    
 }
 */
/*


 Iterator iteraEstados = conjuntoDeEstados.iterator();
 int indice = 0;
        
 while (iteraEstados.hasNext()) {
 String lineaDescripcionEstado = "";
 ArrayList<String> simbolosEncontrados = new ArrayList<>();
 String[] transicion;
 transicion = (String[]) iteraEstados.next();
 String eInicial = transicion[0];
            
 if (!listaEstadosProcesados.contains(eInicial)) {
 if(eInicial.equals(inicio_fin.peek()[0]+"")){
 lineaDescripcionEstado = eInicial + "*:";
 }
 //            }else if(){
                
 //           }
                
                
                
 String simboloTrans = transicion[1];
 String eFinal = transicion[2];
                
 for(int j=1;j<automataAux.size();j++){
 String[] transicionBusqueda;
 transicionBusqueda = (String[]) automataAux.get(j);
 if(transicionBusqueda[0].equals(eInicial)){
 String simboloTransBusqueda = transicionBusqueda[1];
 String eFinalBusqueda = transicionBusqueda[2];
                        
 }
 }

 }//if
 indice ++;
 }//wle



 */
