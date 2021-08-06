package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn20 {

    private byte[] monto1 = new byte[6];
    private byte[] monto2 = new byte[6];
    private byte[] monto3 = new byte[6];
    private byte[] monto4 = new byte[6];
    private byte[] ivaPorcentaje = new byte[1];

    public byte[] getMonto1() {
        return monto1;
    }

    public void setMonto1(byte[] monto1) {
        this.monto1 = monto1;
    }

    public byte[] getMonto2() {
        return monto2;
    }

    public void setMonto2(byte[] monto2) {
        this.monto2 = monto2;
    }

    public byte[] getMonto3() {
        return monto3;
    }

    public void setMonto3(byte[] monto3) {
        this.monto3 = monto3;
    }

    public byte[] getMonto4() {
        return monto4;
    }

    public void setMonto4(byte[] monto4) {
        this.monto4 = monto4;
    }

    public byte[] getIvaPorcentaje() {
        return ivaPorcentaje;
    }

    public void setIvaPorcentaje(byte[] ivaPorcentaje) {
        this.ivaPorcentaje = ivaPorcentaje;
    }

    public String packTkn20() {
        StringBuilder tkn20 = new StringBuilder();
        tkn20.append("200025");
        tkn20.append(ISOUtil.bcd2str(monto1, 0, monto1.length));
        tkn20.append(ISOUtil.bcd2str(monto2, 0, monto2.length));
        tkn20.append(ISOUtil.bcd2str(monto3, 0, monto3.length));
        tkn20.append(ISOUtil.bcd2str(monto4, 0, monto4.length));
        return tkn20.toString();
    }

}
