/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package afd;

import java.util.ArrayList;
//import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author JonnyTest
 */
public class Nodo{
    private final ArrayList<Transicion> trans;
    private boolean inicial;
    private boolean aceptacion;
    
    Nodo(){
        this.trans = new ArrayList<Transicion>();
        this.inicial = false;
        this.aceptacion = false;
    }
    Nodo(Transicion tran){
        this.trans = new ArrayList<Transicion>();
        this.trans.add(tran);
        this.inicial = false;
        this.aceptacion = false;
    }
    Nodo(Transicion tran, boolean ini, boolean acp){
        this.trans = new ArrayList<Transicion>();
        this.trans.add(tran);
        this.inicial = ini;
        this.aceptacion = acp;
    }
    Nodo(boolean ini, boolean acp){
        this.trans = new ArrayList<Transicion>();
        this.inicial = ini;
        this.aceptacion = acp;
    }
    public boolean getInicial(){
        return inicial;
    }
    public boolean getAceptacion(){
        return aceptacion;
    }
    public void setInicial(boolean ini){
        inicial = ini;
    }
    public void setAceptacion(boolean acp){
        aceptacion = acp;
    }
    public void addTransicion(Transicion x){
        trans.add(x);
    }
    public Transicion buscarTrans(String x){
        Iterator<Transicion> itera = trans.iterator();
        while(itera.hasNext()){
            Transicion aux = itera.next();
            if(aux.getSimbolo().equalsIgnoreCase(x)){
                return aux;
            }
        }
        return null;
    }
    public Transicion buscarTrans(Transicion x){
        Iterator<Transicion> itera = trans.iterator();
        while(itera.hasNext()){
            Transicion aux = itera.next();
            if(aux.equals(x)){
                return aux;
            }
        }
        return null;
    }
    public ArrayList listaTrans(){
        return trans;
    }
    public void imprimir(){
        Iterator s = trans.iterator();
        System.out.println("Inicio: "+ this.inicial+" Aceptacion: "+this.aceptacion);
        while(s.hasNext()){
            Transicion aux = (Transicion) s.next();
            aux.imprimir();
        }
    }
    public String imprimir(int x){
        String res = "";
        if(this.aceptacion && !this.inicial){
            res = "**";
            Iterator<Transicion> it = this.trans.iterator();
            while(it.hasNext()){
                res += it.next().imprimir(0);
            }
        }else{
            if(this.inicial){
                res = "*";
                Iterator<Transicion> it = this.trans.iterator();
                while(it.hasNext()){
                    res += it.next().imprimir(0);
                }
            }else{
                Iterator<Transicion> it = this.trans.iterator();
                while(it.hasNext()){
                    res += it.next().imprimir(0);
                }
            }
        }
        return res;
    }
    @Override
    public String toString(){
        String res = null;
        if(this.aceptacion && !this.inicial){
            res = "F";
            Iterator<Transicion> it = this.trans.iterator();
            while(it.hasNext()){
                res += it.next().toString();
            }
        }else{
            if(this.inicial){
                res = "I";
                Iterator<Transicion> it = this.trans.iterator();
                while(it.hasNext()){
                    res += it.next().toString();
                }
            }else{
                res = "N";
                Iterator<Transicion> it = this.trans.iterator();
                while(it.hasNext()){
                    res += it.next().toString();
                }
            }
        }
        return res;
    }
    //public static Comparator<Nodo> res = (Nodo t, Nodo t1) -> new Integer(t.getNombre()).compareTo(t1.getNombre());
}
