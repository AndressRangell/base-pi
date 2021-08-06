package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn12 {

    private byte[] cedula = new byte[16];
    private byte[] cedula2 = new byte[8];

    public void clean(){
        cedula = new byte[16];
        cedula2 = new byte[8];
    }

    public byte[] getCedula() {
        return cedula;
    }

    public void setCedula(byte[] cedula) {
        this.cedula = cedula;
    }

    public byte[] getCedula2() {
        return cedula2;
    }

    public void setCedula2(byte[] cedula2) {
        this.cedula2 = cedula2;
    }

    public String packTkn12(){
        String tok12 = "120008";
        String cedula = new String(getCedula());
        setCedula2(ISOUtil.str2bcd(cedula,false));
        tok12 += ISOUtil.bcd2str(getCedula2(),0,getCedula2().length);
        return tok12;

    }

    public String pack2Tkn12(){
        String tok12 = "120008";
        tok12 += ISOUtil.bcd2str(getCedula2(),0,getCedula2().length);
        return tok12;
    }
}
