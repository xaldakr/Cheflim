package sv.edu.catolica.cheflim;

public class FavoritoRequest {
    private int idUsuario;
    private int idReceta;

    public FavoritoRequest(int idUsuario, int idReceta) {
        this.idUsuario = idUsuario;
        this.idReceta = idReceta;
    }


    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }
}

