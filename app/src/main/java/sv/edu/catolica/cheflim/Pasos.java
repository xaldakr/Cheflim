package sv.edu.catolica.cheflim;

public class Pasos {
    private int id_paso;
    private int id_receta;
    private String paso;
    private  int orden;

    public int getId_paso() {
        return id_paso;
    }

    public void setId_paso(int id_paso) {
        this.id_paso = id_paso;
    }

    public int getId_receta() {
        return id_receta;
    }

    public void setId_receta(int id_receta) {
        this.id_receta = id_receta;
    }

    public String getPaso() {
        return paso;
    }

    public void setPaso(String paso) {
        this.paso = paso;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
