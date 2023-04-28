package eaan.ppdm4p.aled.MODELO;

/**
 * Created by X on 12/06/2020.
 */

public class Usuario {
    private String user;
    private String pass;
    private String nivel;
    private boolean registrado;

    public Usuario(){
        this.user = "none";
        this.pass = "none";
        this.nivel = "0";
        this.registrado = false;
    }

    public Usuario(String user, String pass, String nivel, boolean registrado) {
        this.user = user;
        this.pass = pass;
        this.nivel = nivel;
        this.registrado = registrado;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public boolean isRegistrado() {
        return registrado;
    }

    public void setRegistrado(boolean registrado) {
        this.registrado = registrado;
    }
}
