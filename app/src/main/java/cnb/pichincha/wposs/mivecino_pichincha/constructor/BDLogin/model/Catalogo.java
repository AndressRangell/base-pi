package cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model;

public class Catalogo {

    String id_item;
    String name_item;

    public Catalogo() {
    }

    public Catalogo(String id_item, String name_item) {
        this.id_item = id_item;
        this.name_item = name_item;
    }

    public String getId_item() {
        return id_item;
    }

    public void setId_item(String id_item) {
        this.id_item = id_item;
    }

    public String getName_item() {
        return name_item;
    }

    public void setName_item(String name_item) {
        this.name_item = name_item;
    }

    @Override
    public String toString() {
        return name_item ;
    }
}
