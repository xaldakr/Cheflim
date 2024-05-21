package sv.edu.catolica.cheflim;

import java.util.List;

import sv.edu.catolica.cheflim.Ingrediente;
import sv.edu.catolica.cheflim.Usuario;
import sv.edu.catolica.cheflim.Resena;

public class Recetas {
    private int id_receta;
    private int id_usuario;
    private String descripcion;
    private int porciones;
    private int tiempo;
    private String video;
    private String img;
    private List<Ingrediente> Ingredientes;
    private Usuario Usuarios;
    private List<Resena> Resena;
    private double promedioResenas;
    private int cantidadResenas;

    // Getters y setters generados
    public int getId_receta() {
        return id_receta;
    }

    public void setId_receta(int id_receta) {
        this.id_receta = id_receta;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPorciones() {
        return porciones;
    }

    public void setPorciones(int porciones) {
        this.porciones = porciones;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<Ingrediente> getIngredientes() {
        return Ingredientes;
    }

    public void setIngredientes(List<Ingrediente> ingredientes) {
        Ingredientes = ingredientes;
    }

    public Usuario getUsuarios() {
        return Usuarios;
    }

    public void setUsuarios(Usuario usuarios) {
        Usuarios = usuarios;
    }

    public List<Resena> getResena() {
        return Resena;
    }

    public void setResena(List<Resena> resena) {
        Resena = resena;
    }

    public double getPromedioResenas() {
        return promedioResenas;
    }

    public void setPromedioResenas(double promedioResenas) {
        this.promedioResenas = promedioResenas;
    }

    public int getCantidadResenas() {
        return cantidadResenas;
    }

    public void setCantidadResenas(int cantidadResenas) {
        this.cantidadResenas = cantidadResenas;
    }
}
