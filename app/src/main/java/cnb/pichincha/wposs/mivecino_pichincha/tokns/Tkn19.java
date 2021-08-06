package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn19 {

    private byte[] numeroControl = new byte[6];

    public void clean(){
        numeroControl = new byte[6];
    }

    public byte[] getNumeroControl() {
        return numeroControl;
    }

    public void setNumeroControl(byte[] numeroControl) {
        this.numeroControl = numeroControl;
    }

    public String packTkn19(){
        String tkn19 = "190006";
        tkn19 += ISOUtil.byte2hex(numeroControl);
        return tkn19;
    }
}
