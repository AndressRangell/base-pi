package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn15 {

    private byte[] nombre = new byte[50];
    private byte[] cargo = new byte[30];
    private byte[] cedula = new byte[10];

    public void clean(){
        nombre = new byte[50];
        cargo = new byte[30];
        cedula = new byte[10];
    }

    public String getNombre() {
        String nom = ISOUtil.bcd2str(nombre,0,nombre.length);
        nom = ISOUtil.hex2AsciiStr(nom);
        return nom;
    }

    public void setNombre(byte[] nombre) {
        this.nombre = nombre;
    }

    /**
     *
     * @return
     */
    public String getCargo() {
        String car = ISOUtil.bcd2str(cargo,0,cargo.length);
        car = ISOUtil.hex2AsciiStr(car);
        return car;
    }

    public void setCargo(byte[] cargo) {
        this.cargo = cargo;
    }

    /**
     *
     * @return
     */
    public String getCedula() {
        String ced = ISOUtil.bcd2str(cedula,0,cedula.length,true);
        //ced = ISOUtil.hex2AsciiStr(ced);
        return ced;
    }

    public void setCedula(byte[] cedula) {
        this.cedula = cedula;
    }

    /**
     *
     * @return
     */
    public String packTkn15() {
        String tkn15 = "";
        tkn15 = "150139"; //id,longitud
        tkn15 += ISOUtil.bcd2str(nombre,0,nombre.length);
        tkn15 += ISOUtil.bcd2str(cargo,0,cargo.length);
        tkn15 += ISOUtil.bcd2str(cedula,0,cedula.length,true);
        return tkn15;
    }
}
