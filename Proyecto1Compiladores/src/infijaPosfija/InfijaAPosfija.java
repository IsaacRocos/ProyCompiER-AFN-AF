package infijaPosfija;

/**
 *
 * @author ALMA
 */
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class InfijaAPosfija {

    private String notacionPosfija;
    private String notacionInfija;
    public ArrayList<String> alfabeto = new ArrayList<>();

    public String getNotacionInfija() {
        return notacionInfija;
    }

    public ArrayList<String> getAlfabeto() {
        return this.alfabeto;
    }

    public void setNotacionInfija(String notacionInfija) {
        this.notacionInfija = notacionInfija;
    }

    public String getNotacionPosfija() {
        return notacionPosfija;
    }

    public void setNotacionPosfija(String notacionPosfija) {
        this.notacionPosfija = notacionPosfija;
    }

    public void crearAlfabeto() {
        alfabeto.add("@");
        String caracterAlfabeto = "";
        int cadenaIniciada = 0;
        for (int i = 0; i < notacionInfija.length(); i++) {
            if (((((notacionInfija.charAt(i) == '|' || notacionInfija.charAt(i) == '-') || notacionInfija.charAt(i) == '*') || notacionInfija.charAt(i) == '+') || notacionInfija.charAt(i) == '(') || notacionInfija.charAt(i) == ')') {
                if (cadenaIniciada == 1) {
                    cadenaIniciada = 0;
                    
                    if (!alfabeto.contains(caracterAlfabeto)) {
                        alfabeto.add(caracterAlfabeto);
                        System.out.println("Agregado: " + caracterAlfabeto);
                    }
                    caracterAlfabeto="";
                }
            } else {
                caracterAlfabeto += notacionInfija.charAt(i);
                cadenaIniciada = 1;
                if(i==notacionInfija.length() && !alfabeto.contains(caracterAlfabeto)){
                        alfabeto.add(caracterAlfabeto);
                        System.out.println("Agregado: " + caracterAlfabeto);
                    }
            }
        }
    }

    public boolean inicioInfijaAPosfija() {

        //ENTRADA DE DATOS
        //System.out.println("*Escribe una expresión regular: ");
        //Scanner leer = new Scanner(System.in);
        crearAlfabeto();
        String entrada = getNotacionInfija();

        //Depurar la expresion algebraica
    /*Un depurador permite ir paso a paso a través de cada línea de código en un programa,con lo
         cual puedes rastrear la ejecución y descubrir los errores. También puede mostrar el contenido
         de la memoria, los valores de las variables y las direcciones, así como registrar el contenido.*/
        String expr = depurar(entrada);

        String[] arrayInfix = expr.split(" ");//Split:divide la cadena en sub cadenas

        System.out.println("Array infix:" + arrayInfix);

        //DECLARACIÓN DE LAS PILAS
    /*STACK
         La clase Stack es una clase de las llamadas de tipo
         LIFO (Last In - First Out, o último en entrar - primero en salir). 
         Esta clase hereda de la clase que ya hemos estudiado anteriormente 
         en el curso Vector y con 5 operaciones permite tratar un vector a modo de pila o stack.

         Las operaciones básicas son push (que introduce un elemento en la pila), 
         pop (que saca un elemento de la pila), peek (consulta el primer elemento de la cima de la pila),
         empty (que comprueba si la pila está vacía) y search (que busca un determinado elemento dentro 
         de la pila y devuelve su posición dentro de ella).*/
        Stack< String> E = new Stack< String>(); //Pila ENTRADA
        Stack< String> P = new Stack< String>(); //Pila TEMPORAL PARA OPERACIONES
        Stack< String> S = new Stack< String>(); //Pila SALIDA

        //Añadir la array a la Pila de entrada (E)
        for (int i = arrayInfix.length - 1; i >= 0; i--) {
            E.push(arrayInfix[i]);
        }

        try {//algoritmo q puede geneerar errores
            //Algoritmo Infijo a Postfijo
            while (!E.isEmpty()) {
                switch (pref(E.peek())) {
                    case 1:
                        P.push(E.pop());
                        break;
                    case 3:
                    case 4:
                        while (pref(P.peek()) >= pref(E.peek())) {
                            S.push(P.pop());
                        }
                        P.push(E.pop());
                        break;
                    case 2:
                        while (!P.peek().equals("(")) {
                            S.push(P.pop());
                        }
                        P.pop();
                        E.pop();
                        break;
                    default:
                        S.push(E.pop());
                }
            }

            //Eliminacion de `impurezas´ en la expresiones algebraicas
            String infix = expr.replace(" ", "");
            String postfix = S.toString().replaceAll("[\\]\\[,]", "");

            //MOSTRAR RESULTADOS
            System.out.println("Expresion Infija: " + infix);
            System.out.println("Expresion Postfija: " + postfix);
            postfix=postfix.replace(" ","");
            setNotacionPosfija(postfix);
            return true;

        } catch (Exception ex) {
            System.out.println("Error en la expresión regular");
            System.err.println(ex);
            return false;
        }
    }// inicio infija a posfija

    //DEPURAR ESPRESIÓN REGULAR
    private static String depurar(String s) {
        s = s.replaceAll("\\s+", ""); //Elimina espacios en blanco
        s = "(" + s + ")";
        String simbols = "|-*+()";
        String str = "";

        //Deja espacios entre operadores
        for (int i = 0; i < s.length(); i++) {
            if (simbols.contains("" + s.charAt(i))) {
                str += " " + s.charAt(i) + " ";
            } else {
                str += s.charAt(i);
                System.out.println("String concat: " + str);
            }
        }
        return str.replaceAll("\\s+", " ").trim();
    }//depurador

    //Jerarquia de los operadores
    private static int pref(String op) {
        int prf = 99;
        //if (op.equals("")) prf = 5;
        if (op.equals("*") || op.equals("+")) {
            prf = 5;
        }
        if (op.equals("-")) {
            prf = 4;
        }
        if (op.equals("|")) {
            prf = 3;
        }
        if (op.equals(")")) {
            prf = 2;
        }
        if (op.equals("(")) {
            prf = 1;
        }
        return prf;
    }
}
