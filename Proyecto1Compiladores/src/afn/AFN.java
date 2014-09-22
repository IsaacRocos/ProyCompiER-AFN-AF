package afn;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

/**
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

    public AFN(String regExp, ArrayList<String> alfabeto) {
        this.automata = new Stack<>();
        this.inicio_fin = new Stack<>();
        this.alfabeto = new ArrayList<>();
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

    }

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

            agregarAPilaAFN(automataHijo);
            agregarAPilaInicio_Fin(estados_inicio_fin);

            System.out.println("Generado: (" + Integer.parseInt(automataHijo[0]) + ")--" + automataHijo[1] + "--(" + Integer.parseInt(automataHijo[2]) + ")");
        } catch (EmptyStackException ese) {
            ese.getStackTrace();
            System.out.println("La pila est\u00E1 vac\u00CDa");
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
            agregarAPilaInicio_Fin(nuevoInicioFin);
            imprimirPilaAutomata();
        } catch (EmptyStackException ese) {
            ese.getStackTrace();
            System.out.println("La pila est\u00E1 vac\u00CDa");
        }
    }

    public void creaAutomataConcatenacion() {
        try {
            System.out.println("Generando automata para concatenacion...");
            String[] automataHijo = new String[3];
            int[] inicio_finTope = inicio_fin.pop();
            int[] inicio_finFondo = inicio_fin.pop();
        } catch (EmptyStackException ese) {
            ese.getStackTrace();
            System.out.println("La pila est\u00E1 vac\u00CDa");
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
            agregarAPilaAFN(automataHijo);
            imprimirPilaAutomata();
        } catch (EmptyStackException ese) {
            ese.getStackTrace();
            System.out.println("La pila est\u00E1 vac\u00CDa");
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

    public void imprimirPilaAutomata() {
        System.out.println("----------AUTOMATA------------");
        for (int i = 0; i < automata.size(); i++) {
            String[] transic = automata.get(i);
            System.out.println("(" + Integer.parseInt(transic[0]) + ")--" + transic[1] + "--(" + Integer.parseInt(transic[2]) + ")");
        }
        System.out.println("------------------------------");
    }

}//clase

/*

 char []automataHijo;
 //automata.add(automataHijo);
 //System.out.println(automata.firstElement());

 */
/*

 try{
 elementoAlfAnalizar = "" + elementoExpAnalizar;
 if (!alfabeto.contains(regExp[i + 1] + "")) { // quiere decir que el valor en elementoExpAnalizar es sólo una parte del elementoAlfAnalizar (como a en "a1" ó "an1")
 // Mientras elementoAlfAnalizar no esté en el alfabeto, sigue concatenando letras.
 elementoAlfAnalizar += regExp[++i];
 while (!alfabeto.contains(elementoAlfAnalizar)) {
 i++;
 elementoAlfAnalizar += regExp[i];
 System.out.println("Buscando en alfabeto: " + elementoAlfAnalizar);
 }
 System.out.println("Encontrado despues de concatenar:" + elementoAlfAnalizar);
 }else{
 System.out.println("Encontrado inmeditamente:" + elementoAlfAnalizar);
 }
 //creaAutomataSimple(elementoExpAnalizar);
 }catch(ArrayIndexOutOfBoundsException aiobe){
 aiobe.printStackTrace();
 }


 */
