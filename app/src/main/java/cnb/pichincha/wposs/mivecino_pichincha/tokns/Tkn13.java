package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn13 {

    private byte[] tipoPago = new byte[] {0x00};

    public void clean(){
        tipoPago = new byte[] {0x00};
    }

    public byte[] getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(byte[] tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String packTkn13(){
        String tok13 = "130001";
        tok13 += ISOUtil.bcd2str(tipoPago, 0, tipoPago.length);
        return tok13;
    }


}
