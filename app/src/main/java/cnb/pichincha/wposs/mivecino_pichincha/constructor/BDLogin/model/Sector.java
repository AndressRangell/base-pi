package cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model;

public class Sector {

    private String idSector;
    private String sector;

    public Sector() {
    }

    public Sector(String idSector, String sector) {
        this.idSector = idSector;
        this.sector = sector;
    }

    public String getIdSector() {
        return idSector;
    }

    public void setIdSector(String idSector) {
        this.idSector = idSector;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    @Override
    public String toString() {
        return  sector;
    }
}
