package afd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;


/**
 *
 * @author Isaac
 */
public class AFD {
    private  ArrayList alfabeto;
    private Map<Integer,Nodo> nodos;
    
    //ESTE ES EL CONSTRUCTOR QUE SE LLAMA DESDE LA INTERFAZ  INICIO
    public AFD(AFN aux, ArrayList alf){  // MODIFCADO A PUBLIC PARA PODER USAR CONSTRUCTOR DESDE INTERFAZ
        this.nodos = new HashMap<Integer,Nodo>();
        this.alfabeto = alf;
        generar(aux,this.alfabeto);
        imprimir();  // AGREGADO PARA PRUEBAS
    }
    
    AFD(){
        this.nodos = new HashMap<Integer,Nodo>();
        this.alfabeto = new ArrayList<>();
    }
    
    public Map getMap(Map x){
        return nodos;
    }
    public void setTabla(int ini,String sim,int edoSig){
        
    }
    public void setMap(Map x){
        nodos = x;
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
    
    //public  AFD generar(AFN x, ArrayList alf){   //MODIFICADO PARA HACER PRUEBAS
    public  void generar(AFN x, ArrayList alf){
        AFD res = new AFD();
        boolean tranVal = true;
        int ini = x.getIni();
        int fin = x.getFin();
        int aux = 0;
        Nodo nodAux = null;
        LinkedList cola = new LinkedList();
        Map<Integer,ArrayList> subCon = new HashMap<Integer,ArrayList>();
        Map<Integer,Nodo> lista = new HashMap<Integer,Nodo>();
        ArrayList<Integer> tran = new ArrayList<Integer>();
        ArrayList<Integer> tranSim = new ArrayList<Integer>();

        System.out.println("Generando automata finito determinista...");
        
        alf.remove(alf.size()-1);
        subCon.put(x.getIni(), profundidad(x.getIni(),x.getMap(),"@"));
        cola.addFirst(x.getIni());
        while(!cola.isEmpty()){
            Iterator<String> it = alf.iterator();
            aux = (int)cola.getLast();
            while(it.hasNext()){
                tran = subCon.get(aux);
                Iterator<Integer> itran = tran.iterator();
                String a = it.next();
                while(itran.hasNext()){
                    Integer b = itran.next();
                    nodAux = (Nodo)x.getMap().get(b);
                    tranSim = nodAux.buscarTrans(a).getEdoSigiente();
                    for (Integer tranSim1 : tranSim) {
                        tranVal = false;
                        if (!subCon.containsKey(tranSim1)) {
                            subCon.put(tranSim1, profundidad(tranSim1, x.getMap(), "@"));
                            cola.addFirst(tranSim1);
                        }
                        if(lista.containsKey(aux)){
                            nodAux = lista.get(aux);
                            nodAux.addTransicion(new Transicion(aux,tranSim1,a));
                            if(subCon.get(aux).indexOf(fin)>=0){
                                nodAux.setAceptacion(true);
                            }
                            lista.put(aux, nodAux);
                        }else{
                            if(ini==aux){
                                nodAux = new Nodo(new Transicion(aux, tranSim1, a),true,false);
                            }else{
                                nodAux = new Nodo(new Transicion(aux, tranSim1, a),false,false);
                            }
                            if(subCon.get(aux).indexOf(fin)>=0){
                                nodAux.setAceptacion(true);
                            }
                            lista.put(aux, nodAux);
                        }
                    }
                }
                if(tranVal){
                    if(lista.containsKey(aux)){
                        nodAux = lista.get(aux);
                        nodAux.addTransicion(new Transicion(aux,a));
                        if(subCon.get(aux).indexOf(fin)>=0){
                            nodAux.setAceptacion(true);
                        }
                        lista.put(aux, nodAux);
                    }else{
                        nodAux = new Nodo(new Transicion(aux, a));
                        if(subCon.get(aux).indexOf(fin)>=0){
                            nodAux.setAceptacion(true);
                        }
                        lista.put(aux, nodAux);
                    }
                }
                tranVal = true;
            }
            cola.removeLast();
        }
        //res.setMap(lista);    //  MODIFICADO PARA PRUEBAS
        setMap(lista);
        //return res;          // MODIFICADO PARA PRUEBAS
    }
    
    public void compr(String ruta){
        try(FileWriter fr=new FileWriter(ruta); 
            BufferedWriter br = new BufferedWriter(fr)){
            for (Iterator<Integer> it = nodos.keySet().iterator(); it.hasNext();) {
                Integer key = it.next();
                br.write("|"+key+"|"+ nodos.get(key));
                br.newLine();
            }
        }catch(IOException e){
            System.out.println("Error E/S: "+e);
        }
    }
    public void dibujo(String ruta){
        try(FileWriter fr=new FileWriter(ruta); 
            BufferedWriter br = new BufferedWriter(fr)){
            for (Iterator<Integer> it = nodos.keySet().iterator(); it.hasNext();) {
                Integer key = it.next();
                br.write(key + nodos.get(key).imprimir(0));
                br.newLine();
            }
            br.close();
        }catch(IOException e){
            System.out.println("Error E/S: "+e);
        }
    }
    
    private static ArrayList profundidad(int x, Map y, String z){
        Stack pila = new Stack();
        ArrayList<Integer> tranAux = new ArrayList<Integer>();
        ArrayList<Integer> res = new ArrayList<Integer>();
        Nodo nodAux;
        int aux;
        pila.push(x);
        do{
        aux = (int)pila.pop();
        nodAux = (Nodo) y.get(aux);
        tranAux = nodAux.buscarTrans(z).getEdoSigiente();
        pila = llenaPila(pila,tranAux);
        res.add(aux);
        }while(!pila.isEmpty());
        return res;
    }
    
    private static Stack llenaPila(Stack pila, ArrayList x){
        if(x.size() >0){
            pila.push(x.get(x.size()-1));
            x.remove(x.size()-1);
            llenaPila(pila,x);
        }
        return pila;
    }
}

