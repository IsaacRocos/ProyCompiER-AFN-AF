package afn;

import java.util.ArrayList;

/**
 *
 * @author isaac
 */
public class PruebaAFN {
    
    public static void main(String[] args) {
        
        ArrayList <String> alfabeto1 = new ArrayList<>();
        alfabeto1.add("a1");
        alfabeto1.add("b");
        alfabeto1.add("agfgf");
        alfabeto1.add("b2");
        alfabeto1.add("b133");
        
        System.out.println("Prueba 1:");
        AFN pruebaAFN1 = new AFN("agfgfb|*a1-b133-b2-",alfabeto1);
        pruebaAFN1.iniciarCreaAFN();
      
        /*  
        System.out.println("\nPrueba 2:");
        AFN pruebaAFN2 = new AFN("ab*c-|ab-c+||");
        pruebaAFN2.iniciarCreaAFN();
        
        System.out.println("\nPrueba 3:");
        AFN pruebaAFN3 = new AFN("ab*-cd|+a*-|");
        pruebaAFN3.iniciarCreaAFN();
        
        System.out.println("\nPrueba 4:");
        AFN pruebaAFN4 = new AFN("aab|c-*d+-|");
        pruebaAFN3.iniciarCreaAFN();*/
    }
    
}
