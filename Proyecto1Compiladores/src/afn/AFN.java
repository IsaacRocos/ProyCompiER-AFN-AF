package afn;
import java.util.Stack;
/**
 *
 * @author Isaac
 */
public class AFN {

    private char[] regExp; // Guarda la expresión en posfijo, en un arreglo de caracteres.
    private Stack<char[]> automata = new Stack<char[]>();  //Almacena la información del automata.
    
    
    public AFN(String regExp){
        System.out.println("Se va a convertir: <<" + regExp + ">> en un AFN");
        setRegExp(regExp);
    }
    
    public void setRegExp(String regExp){
        this.regExp = new char[regExp.length()];
        this.regExp = regExp.toCharArray();
    }
    
    
    
    public void iniciarCreaAFN(){
        char []automataHijo = {'0','a','1'};
        automata.add(automataHijo);
        //System.out.println(automata.firstElement());
        
        
    }
    
    public void creaAutomataUnion(){
    
    }
    
    public void creaAutomataConcatenacion(){
    
    }
    
    public void creaAutomataCerraduraP(){

    }
    
    public void creaAutomataCerraduraEp(){
    
    }
    
}
