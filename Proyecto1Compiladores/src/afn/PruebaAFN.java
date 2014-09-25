package afn;

import java.util.ArrayList;

/**
 *
 * @author isaac
 */
public class PruebaAFN {
    
    public static void main(String[] args) {
        
        ArrayList <String> alfabeto1 = new ArrayList<>();
        alfabeto1.add("a");
        alfabeto1.add("b");
        alfabeto1.add("c");
        alfabeto1.add("@");
        alfabeto1.add("d");
        /*
        System.out.println("<-> PRUEBA 1 <->");
        AFN pruebaAFN1 = new AFN("ab|*a-b-b-",alfabeto1);
        pruebaAFN1.iniciarCreaAFN();
      
        
        System.out.println("<-> PRUEBA 2 <->");
        AFN pruebaAFN2 = new AFN("ab*c-|ab-c+||",alfabeto1);
        pruebaAFN2.iniciarCreaAFN();
         */
        System.out.println("<-> PRUEBA 3 <->");
        AFN pruebaAFN3 = new AFN("ab*-cd|+a*-|",alfabeto1);
        pruebaAFN3.iniciarCreaAFN();
        
        System.out.println("<-> PRUEBA 4 <->");
        AFN pruebaAFN4 = new AFN("aab|c-*d+-|",alfabeto1);
        pruebaAFN4.iniciarCreaAFN();
                
    }
}
