package afn;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Isaac
 */
public class AFN {

    private char[] regExp; // Guarda la expresión en posfijo, en un arreglo de caracteres.
    private Stack<char[]> automata;  //Almacena la información del automata.
    private Stack<int[]> inicio_fin;
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
        String elementoAlfAnalizar = "";  // Se usa para poder identificar si el elemento del alfabeto está compuesto por más de un caracter como  "a1" o "bn" , etc.
        for (int i = 0; i < regExp.length; i++) {
            System.out.println("Valor de indice: " + i);
            elementoExpAnalizar = regExp[i];
            switch (elementoExpAnalizar) {
                case '|':
                    System.out.println("Generando automata para union");
                    // creaAutomataUnion();
                    break;
                case '-':
                    creaAutomataConcatenacion();
                    break;
                case '*':
                    creaAutomataCerraduraEp();
                    break;
                case '+':
                    creaAutomataCerraduraP();
                    break;
                default://cualquier caracter
                    try {
                        elementoAlfAnalizar = "" + elementoExpAnalizar;
                        while (!alfabeto.contains(elementoAlfAnalizar) || !alfabeto.contains(regExp[i + 1] + "")) {
                            if (regExp[i + 1] != '|' && regExp[i + 1] != '*' && regExp[i + 1] != '-' && regExp[i + 1] != '+') {
                                elementoAlfAnalizar += regExp[i + 1];
                            }
                            System.out.println("Buscando en alfabeto: " + elementoAlfAnalizar);
                            i++;
                        }
                        System.out.println("Encontrado: " + elementoAlfAnalizar);
                        //creaAutomataSimple(elementoExpAnalizar);
                    } catch (ArrayIndexOutOfBoundsException aiobe) {
                        aiobe.getStackTrace();
                    }
                    break;
            }//switch
        }//for

    }

    public void creaAutomataSimple(char elementoAnalizar) {
        System.out.println("Generando automata simple...");
        char[] automataHijo = new char[3];
        int[] estados_inicio_fin = new int[2];

        if (ultimoEstadoFinal == 0) { //en caso de que sea el primer automata generado para la regexp, le corresponde el primer valor, que es 0.
            automataHijo[0] = (char) 0;
        } else {
            automataHijo[0] = (char) (ultimoEstadoFinal + 1);
        }
        automataHijo[1] = elementoAnalizar;
        automataHijo[2] = (char) (automataHijo[0] + 1);

        setUltimoEstadoInicial((int) automataHijo[0]);
        setUltimoEstadoFinal((int) automataHijo[2]);

        estados_inicio_fin[0] = (int) automataHijo[0];
        estados_inicio_fin[1] = (int) automataHijo[2];

        agregarAPilaAFN(automataHijo);
        agregarAPilaInicio_Fin(estados_inicio_fin);

        System.out.println("Generado: (" + (int) automataHijo[0] + ")--" + automataHijo[1] + "--(" + (int) automataHijo[2] + ")");
    }

    public void creaAutomataUnion() {
        System.out.println("Generando automata para Union...");
        char[] automataHijo = new char[3];
        int[] inicio_finTope = inicio_fin.pop();
        int[] inicio_finFondo = inicio_fin.pop();

        automataHijo[0] = (char) (ultimoEstadoFinal + 1);
        automataHijo[1] = '@';
        automataHijo[2] = (char) inicio_finFondo[0];

        agregarAPilaAFN(automataHijo);

        automataHijo = new char[3];
        automataHijo[0] = (char) (ultimoEstadoFinal + 1);
        automataHijo[1] = '@';
        automataHijo[2] = (char) inicio_finTope[0];

        agregarAPilaAFN(automataHijo);

        setUltimoEstadoInicial(ultimoEstadoFinal + 1);
        setUltimoEstadoFinal(ultimoEstadoInicial + 1);

        automataHijo = new char[3];
        automataHijo[0] = (char) inicio_finFondo[1];
        automataHijo[1] = '@';
        automataHijo[2] = (char) (ultimoEstadoFinal);

        agregarAPilaAFN(automataHijo);

        automataHijo = new char[3];
        automataHijo[0] = (char) inicio_finTope[1];
        automataHijo[1] = '@';
        automataHijo[2] = (char) (ultimoEstadoFinal);
        agregarAPilaAFN(automataHijo);

        int[] nuevoInicioFin = new int[2];
        nuevoInicioFin[0] = this.ultimoEstadoInicial;
        nuevoInicioFin[1] = this.ultimoEstadoFinal;
        agregarAPilaInicio_Fin(nuevoInicioFin);

        imprimirPilaAutomata();
    }

    public void creaAutomataConcatenacion() {
        System.out.println("Generando automata para concatenacion...");
    }

    public void creaAutomataCerraduraP() {
        System.out.println("Generando automata para cerradura positiva...");
    }

    public void creaAutomataCerraduraEp() {
        System.out.println("Generando automata para cerradura epsilon...");
    }

    public void setUltimoEstadoInicial(int uei) {
        ultimoEstadoInicial = uei;
    }

    public void setUltimoEstadoFinal(int uef) {
        ultimoEstadoFinal = uef;
    }

    public void agregarAPilaAFN(char[] automata) {
        this.automata.push(automata);
    }

    public void agregarAPilaInicio_Fin(int[] inicio_fin) {
        this.inicio_fin.push(inicio_fin);
    }

    public void imprimirPilaAutomata() {
        for (int i = 0; i < automata.size(); i++) {
            char[] transic = automata.get(i);
            System.out.println("(" + (int) transic[0] + ")--" + transic[1] + "--(" + (int) transic[2] + ")");
        }
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
