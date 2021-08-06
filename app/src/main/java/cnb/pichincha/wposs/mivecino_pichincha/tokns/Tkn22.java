package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn22 {
    private byte[] diaPago = new byte[20];
    private byte[] plazoPago = new byte[1];

    public void clean(){
        diaPago = new byte[20];
        plazoPago = new byte[1];
    }

    public byte[] getDiaPago() {
        return diaPago;
    }

    public void setDiaPago(byte[] diaPago) {
        this.diaPago = diaPago;
    }

    public byte[] getPlazoPago() {
        return plazoPago;
    }

    public void setPlazoPago(byte[] plazoPago) {
        this.plazoPago = plazoPago;
    }

    public String packTkn22() {

        String Tkn22 = "220021";

        Tkn22 += ISOUtil.bcd2str(getDiaPago(), 0, getDiaPago().length);
        Tkn22 += ISOUtil.bcd2str(getPlazoPago(), 0, getPlazoPago().length);

        return Tkn22;
    }
}
