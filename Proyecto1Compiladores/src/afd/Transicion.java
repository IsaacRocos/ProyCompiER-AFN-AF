/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package afd;
import java.util.ArrayList;
import java.util.Iterator;
/**
 *
 * @author JonnyTest
 */
public class Transicion {
    
    private final int edoActual;
    private final ArrayList<Integer> edoSiguiente;
    private final String simbolo;
    
    Transicion(int EdoActual,int EdoSiguiente,String Simbolo){
        edoActual = EdoActual;
        edoSiguiente = new ArrayList<Integer>();
        edoSiguiente.add(EdoSiguiente);
        simbolo = Simbolo;
    }
    Transicion(int EdoActual, String Simbolo){
        edoActual = EdoActual;
        edoSiguiente = new ArrayList<Integer>();
        simbolo = Simbolo;
    }
    
    public int getEdoActual(){
        return edoActual;
    }
    
    public ArrayList getEdoSigiente(){
        return edoSiguiente;
    }
    
    public void setEdoSigiente(int x){
        edoSiguiente.add(x);
    }
    
    public String getSimbolo(){
        return simbolo;
    }
    
    public boolean equals(Transicion x){
        boolean res = false;
        if((x.getEdoActual()==edoActual)&&(x.getEdoSigiente()==edoSiguiente)&&(x.getSimbolo().equalsIgnoreCase(simbolo))){
            res = true;
        }
        return res;
    }
    public static boolean equals(Transicion x, Transicion y){
        boolean res = false;
        if((x.getEdoActual()==y.getEdoActual())&&(x.getEdoSigiente()==y.getEdoSigiente())&&(x.getSimbolo().equalsIgnoreCase(y.getSimbolo()))){
            res = true;
        }
        return res;
    }
    public void imprimir(){
        System.out.println("Mov("+this.simbolo+")="+this.edoSiguiente);
    }
    
    @Override
    public String toString(){
        String res;
        if(this.edoSiguiente.size()>0){
            res = "|"+this.simbolo+"|"+this.edoSiguiente.get(0);
        }else{
            res = "|"+this.simbolo+"|-";
        }
        return res;
    }

    public String imprimir(int x){
        String res;
        res = ":"+this.simbolo+";";
        if(this.edoSiguiente.size()<=0){
            res += "-";
        }else{
            Iterator<Integer> it = this.edoSiguiente.iterator();
            while(it.hasNext()){
                res += it.next()+",";
            }
            res = res.substring(0, res.lastIndexOf(","));
        }
        return res;
    }
}
