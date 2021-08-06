package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn28 {
    private byte[] idTransaccionOtp = new byte[9];
    private byte[] numeroOtp = new byte[6];


    public void clean(){
        idTransaccionOtp = new byte[9];
        numeroOtp = new byte[6];
    }

    public byte[] getNumeroOtp() {
        return numeroOtp;
    }

    public void setNumeroOtp(byte[] numeroOtp) {
        this.numeroOtp = numeroOtp;
    }

    public byte[] getIdTransaccionOtp() {
        return idTransaccionOtp;
    }

    public void setIdTransaccionOtp(byte[] idTransaccionOtp) {
        this.idTransaccionOtp = idTransaccionOtp;
    }

    public String packTkn28(){
        String tkn28 = "280015";
        tkn28 += ISOUtil.byte2hex(getNumeroOtp());
        tkn28 += ISOUtil.byte2hex(getIdTransaccionOtp());
        return tkn28;
    }

}
