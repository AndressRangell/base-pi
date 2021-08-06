package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn49 {

    private byte[] cedula = new byte[16];
    private byte[] apellido1 = new byte[20];
    private byte[] apellido2= new byte[20];
    private byte[] nombre1 = new byte[20];
    private byte[] nombre2 = new byte[20];
    private byte[] nombreCompleto = new byte[30];
    private String cedula1;

    public void clean(){
        cedula = new byte[16];
        apellido1 = new byte[20];
        apellido2= new byte[20];
        nombre1 = new byte[20];
        nombre2 = new byte[20];
        nombreCompleto = new byte[30];
    }

    public String getCedula() {
        return cedula1;
    }

    public void setCedula(byte[] cedula) {
        this.cedula = cedula;
        String cedAux;
        cedAux= ISOUtil.bcd2str(cedula,0,cedula.length);
        cedAux = ISOUtil.cutChar(cedAux, "F");
        this.cedula1 = cedAux;
    }

    public byte[] getApellido1() {
        return apellido1;
    }

    public void setApellido1(byte[] apellido1) {
        this.apellido1 = apellido1;
    }

    public byte[] getApellido2() {
        return apellido2;
    }

    public void setApellido2(byte[] apellido2) {
        this.apellido2 = apellido2;
    }

    public byte[] getNombre1() {
        return nombre1;
    }

    public void setNombre1(byte[] nombre1) {
        this.nombre1 = nombre1;
    }

    public byte[] getNombre2() {
        return nombre2;
    }

    public void setNombre2(byte[] nombre2) {
        this.nombre2 = nombre2;
    }

    public byte[] getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(byte[] nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String packTkn49() {
        String Tkn49 = "490118";

        Tkn49 += ISOUtil.bcd2str(cedula,0,cedula.length);
        Tkn49 += ISOUtil.bcd2str(getApellido1(),0,getApellido1().length);
        Tkn49 += ISOUtil.bcd2str(getApellido2(),0,getApellido2().length);
        Tkn49 += ISOUtil.bcd2str(getNombre1(),0,getNombre1().length);
        Tkn49 += ISOUtil.bcd2str(getNombre2(),0,getNombre2().length);
        Tkn49 += ISOUtil.bcd2str(getNombreCompleto(),0,getNombreCompleto().length);

        return Tkn49;
    }
}
