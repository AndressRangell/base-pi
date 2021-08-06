package cnb.pichincha.wposs.mivecino_pichincha.constructor;

public class UserCentralizado {
    String ruc;
    String empresa;
    String cedula;
    String nombre;
    String fechaUltimoCierre;
    String fechaRegistro;

    public UserCentralizado(String ruc, String empresa, String cedula, String nombre, String fechaUltimoCierre, String fechaRegistro) {
        this.ruc = ruc;
        this.empresa = empresa;
        this.cedula = cedula;
        this.nombre = nombre;
        this.fechaUltimoCierre = fechaUltimoCierre;
        this.fechaRegistro = fechaRegistro;
}

    public UserCentralizado() {
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaUltimoCierre() {
        return fechaUltimoCierre;
    }

    public void setFechaUltimoCierre(String fechaUltimoCierre) {
        this.fechaUltimoCierre = fechaUltimoCierre;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
