/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_8_quiz;

import java.io.Serializable;

/**
 *
 * @author DELL
 */
public class Nodo implements Serializable {
    private Cancion dato;
    private Nodo siguiente;
    
    public Nodo(Cancion dato) {
        this.dato = dato;
        this.siguiente = null;
    }
    
    public Cancion getDato() { return dato; }
    public void setDato(Cancion dato) { this.dato = dato; }
    
    public Nodo getSiguiente() { return siguiente; }
    public void setSiguiente(Nodo siguiente) { this.siguiente = siguiente; }
}
