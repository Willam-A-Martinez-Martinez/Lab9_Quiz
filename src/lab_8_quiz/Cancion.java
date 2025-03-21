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
public class Cancion implements Serializable {
    private String nombre;
    private String artista;
    private String duracion;
    private String rutaImagen;
    private String genero;
    private String rutaArchivo;

    public Cancion(String nombre, String artista, String duracion, String rutaImagen, String genero, String rutaArchivo) {
        this.nombre = nombre;
        this.artista = artista;
        this.duracion = duracion;
        this.rutaImagen = rutaImagen;
        this.genero = genero;
        this.rutaArchivo = rutaArchivo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }
    
    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    
    public String getRutaImagen() { return rutaImagen; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }
    
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    
    @Override
    public String toString() {
        return nombre + " - " + artista;
    }
}