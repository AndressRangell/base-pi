package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn38 {
    private byte[] formaDePago = new byte[1];

    public void clean(){
        formaDePago = new byte[1];
    }

    public byte[] getFormaDePago() {
        return formaDePago;
    }

    public void setFormaDePago(byte[] formaDePago) {
        this.formaDePago = formaDePago;
    }

    public String packTkn38(){
        String tkn38 = "380001";
        tkn38 += ISOUtil.byte2hex(formaDePago);
        return tkn38;
    }

}
