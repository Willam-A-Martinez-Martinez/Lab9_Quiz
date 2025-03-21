package lab_8_quiz;

import java.io.Serializable;

public class ListaEnlazada implements Serializable {
    private Nodo cabeza;
    private int tamaño;
    
    public ListaEnlazada() {
        this.cabeza = null;
        this.tamaño = 0;
    }
    
    public boolean estaVacia() {
        return cabeza == null;
    }
    
    public int getTamaño() {
        return tamaño;
    }
    
    public void agregarCancion(Cancion cancion) {
        Nodo nuevoNodo = new Nodo(cancion);
        if (estaVacia()) {
            cabeza = nuevoNodo;
        } else {
            Nodo actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
        tamaño++;
    }
    
    public boolean eliminarCancion(int indice) {
        if (estaVacia() || indice < 0 || indice >= tamaño) {
            return false;
        }
        if (indice == 0) {
            cabeza = cabeza.getSiguiente();
            tamaño--;
            return true;
        }
        Nodo actual = cabeza;
        for (int i = 0; i < indice - 1; i++) {
            actual = actual.getSiguiente();
        }
        actual.setSiguiente(actual.getSiguiente().getSiguiente());
        tamaño--;
        return true;
    }
    
    public Cancion obtenerCancion(int indice) {
        if (estaVacia() || indice < 0 || indice >= tamaño) {
            return null;
        }
        Nodo actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }
    
    public Cancion[] obtenerTodasLasCanciones() {
        if (estaVacia()) {
            return new Cancion[0];
        }
        Cancion[] canciones = new Cancion[tamaño];
        Nodo actual = cabeza;
        for (int i = 0; i < tamaño; i++) {
            canciones[i] = actual.getDato();
            actual = actual.getSiguiente();
        }
        return canciones;
    }
}