package afn;

/**
 *
 * @author isaac
 */
public class PruebaAFN {
    
    public static void main(String[] args) {
        
        System.out.println("Prueba 1:");
        AFN pruebaAFN1 = new AFN("ab|*a-b-b-");
        pruebaAFN1.iniciarCreaAFN();
        System.out.println("Prueba 2:");
        AFN pruebaAFN2 = new AFN("ab*c-|ab-c+||");
        System.out.println("Prueba 3:");
        AFN pruebaAFN3 = new AFN("ab*-cd|+a*-|");
        System.out.println("Prueba 4:");
        AFN pruebaAFN4 = new AFN("aab|c-*d+-|");
    }
    
}
