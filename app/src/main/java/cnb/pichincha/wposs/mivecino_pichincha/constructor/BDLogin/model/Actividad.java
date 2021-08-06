package cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model;

public class Actividad {

    private String idActividad;
    private String actividad;
    private String idSector;

    public Actividad() {
    }

    public Actividad(String idActividad, String actividad) {
        this.idActividad = idActividad;
        this.actividad = actividad;
    }

    public String getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(String idActividad) {
        this.idActividad = idActividad;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getIdSector() {
        return idSector;
    }

    public void setIdSector(String idSector) {
        this.idSector = idSector;
    }

    @Override
    public String toString() {
        return  actividad;
    }
}
