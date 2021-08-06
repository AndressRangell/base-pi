package cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model;

public class SituacionLaboral {

    private String id_laboral;
    private String name_laboral;

    public SituacionLaboral() {
    }

    public SituacionLaboral(String id_laboral, String name_laboral) {
        this.id_laboral = id_laboral;
        this.name_laboral = name_laboral;
    }

    public String getId_laboral() {
        return id_laboral;
    }

    public void setId_laboral(String id_laboral) {
        this.id_laboral = id_laboral;
    }

    public String getName_laboral() {
        return name_laboral;
    }

    public void setName_laboral(String name_laboral) {
        this.name_laboral = name_laboral;
    }

    @Override
    public String toString() {
        return name_laboral;
    }
}
