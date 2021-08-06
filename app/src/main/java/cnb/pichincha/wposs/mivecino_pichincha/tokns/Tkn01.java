package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.utils.ISOUtil;

public class Tkn01 {

    private byte[] numApp = new byte[10];

    public byte[] getNumApp() {
        return numApp;
    }

    public void setNumApp(byte[] numApp) {
        this.numApp = numApp;
    }

    public String packTkn01(){
        return FinanceTrans.asciiToHex("TO5");
    }

    public void cleanTkn06(){
        numApp = new byte[999];
    }
}
