package cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model;

import newpos.libpay.utils.PAYUtils;

public class Usuario {

    private String user;
    private String password;
    private String role;
    private String estado;
    private int intento;
    private String fecha;
    private String hora;
    private String fechaCierre;
    private String estadoCierre;

    public Usuario() {
    }

    public Usuario(String password) {
        this.user = password;
    }

    public Usuario(String user,String pass) {
        this.user = user;
        this.password=pass;
    }

    public Usuario(String user, String password, String role, String estado, int intento, String estadoCierre) {
        this.user = user;
        this.password = password;
        this.role = role;
        this.estado = estado;
        this.intento = intento;
        this.estadoCierre = estadoCierre;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIntento() {
        return intento;
    }

    public void setIntento(int intento) {
        this.intento = intento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(String fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getEstadoCierre() {
        return estadoCierre;
    }

    public void setEstadoCierre(String estadoCierre) {
        this.estadoCierre = estadoCierre;
    }

    public String fechaCierrePantalla() {
        String date = getFechaCierre();
        return  PAYUtils.StrToDate(date,"yyyyMMddHHmmss","yyyy-MM-dd HH:mm:ss");
    }
}
