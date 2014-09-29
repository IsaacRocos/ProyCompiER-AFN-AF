
package afd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author JonnyTest
 */
public class AFN {
    private final String ruta;
    private Integer EdoIni;
    private Integer EdoFin;
    private final ArrayList<String> alfabeto;
    private final Map<Integer,Nodo> nodos;
    
    public AFN(String arc ){
        this.ruta = arc;
        this.alfabeto = new ArrayList<String>();
        this.nodos = new HashMap<Integer,Nodo>();
        this.EdoIni = null;
        EdoFin = null;
    }
    
    @SuppressWarnings("null")
    public void lecturaArc(){
        try(FileReader fr=new FileReader(ruta)){
            BufferedReader br = new BufferedReader(fr);
            Iterator<String> itr;
            String aux = br.readLine();
            String strNodo;
            boolean trans;
            boolean equalSim = true;
            Integer indAux = null;
            Nodo auxNodo = null;
            String simbolo = null;
            do{
                trans= true;
                do{
                    if(aux.indexOf(':')>=0){
                        strNodo = aux.substring(0, aux.indexOf(':'));
                        aux = aux.substring(aux.indexOf(':')+1);
                    }else{
                        strNodo = aux;
                        aux = null;
                    }
                    if(trans){
                        if(!(Character.isDigit(strNodo.charAt(strNodo.length()-1)))){
                            if("**".equalsIgnoreCase(strNodo.substring(strNodo.length()-2, strNodo.length()))){
                                indAux = Integer.parseInt(strNodo.substring(0, strNodo.length()-2));
                                EdoFin = indAux;
                                auxNodo = new Nodo(false, true);
                            }else{
                                indAux = Integer.parseInt(strNodo.substring(0, strNodo.length()-1));
                                EdoIni = indAux;
                                auxNodo = new Nodo(true, false);
                            }
                        }else{
                            indAux = Integer.parseInt(strNodo);
                            auxNodo = new Nodo(false,false);
                        }
                        trans = false;
                    }else{
                        simbolo = strNodo.substring(0,strNodo.indexOf(';'));
                        itr = alfabeto.iterator();
                        while(itr.hasNext()){
                            String listSim = itr.next();
                            if(listSim.equalsIgnoreCase(simbolo)){
                                equalSim=false;
                            }
                        }
                        if(equalSim){
                            alfabeto.add(simbolo);
                        }
                        equalSim=true;
                        //simbolo = strNodo.substring(0,strNodo.indexOf(','));
                        strNodo = strNodo.substring(strNodo.indexOf(';')+1);
                        Transicion Taux = new Transicion(indAux,simbolo);
                        do{
                            if(strNodo.charAt(0)=='-'){
                                strNodo = null;
                            }else{
                                if(strNodo.indexOf(',')>0){
                                    Taux.setEdoSigiente(Integer.parseInt(strNodo.substring(0,strNodo.indexOf(','))));
                                    strNodo = strNodo.substring(strNodo.indexOf(',')+1);
                                }else{
                                    Taux.setEdoSigiente(Integer.parseInt(strNodo));
                                    strNodo= null;
                                }
                            }
                        }while(strNodo!=null);
                        auxNodo.addTransicion(Taux);
                    }
                }while(aux!=null);
                nodos.put(indAux,auxNodo);
                aux = br.readLine();
            }while(aux!=null);
        }catch(IOException e){
            System.out.println("Error E/S: "+e);
        }
    }
    
    public void imprimir(){
        Iterator s = alfabeto.iterator();
        Iterator d = nodos.keySet().iterator();
        while(s.hasNext()){
            System.out.println(s.next());
        }
        System.out.println("Fin de alfabeto");
        while(d.hasNext()){
            Integer aux = (Integer) d.next();
            System.out.print("Nombre: "+aux);
            this.nodos.get(aux).imprimir();
        }
        
    }
    
    public Map getMap(){
        return nodos;
    }
    public int getIni(){
        return EdoIni;
    }
    public Integer getFin(){
        return EdoFin;
    }
    public ArrayList getAlfab(){
        return alfabeto;
    }
    public Nodo getNodo(int x){
        return nodos.get(x);
    }
    
    public ArrayList getTransicion(int nodo, String simbolo){
        return nodos.get(nodo).buscarTrans(simbolo).getEdoSigiente();
    }
    /*public void ordenar(){
        Collections.sort(nodos, Nodo.res);
    }*/
}
