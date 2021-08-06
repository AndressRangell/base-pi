package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn26 {

    private byte[] lenTok26 = new byte[1];
    private byte[] tkn26 = new byte[999];
    public String[] montos;

    public void clean(){
        lenTok26 = new byte[1];
        tkn26 = new byte[999];
    }

    public byte[] getLenTok26() {
        return lenTok26;
    }

    public void setLenTok26(byte[] lenTok26) {
        this.lenTok26 = lenTok26;
    }

    public byte[] getTkn26() {
        return tkn26;
    }

    public void setTkn26(byte[] tkn26) {
        int i;
        int len = 0;
        this.tkn26 = tkn26;
        String strMsg;

        strMsg = ISOUtil.bcd2str(tkn26,0, tkn26.length);
        len = ISOUtil.bcd2int(lenTok26,0,lenTok26.length);
        montos = new String[len];

        int x = 0;
        int y = 12;

        for (i = 0; i < len; i++) {
            montos[i] = strMsg.substring(x,y);
            x = y;
            y = y+12;
        }

    }

    public String packTkn26(){
        String idTkn26 = "26";

        String strMsg = ISOUtil.bcd2str(lenTok26,0, lenTok26.length);
        strMsg += ISOUtil.bcd2str(tkn26,0, tkn26.length);

        int lenBuff = (strMsg.length()/2);
        String len =  ISOUtil.padleft(lenBuff + "", 4, '0');

        return idTkn26 + len + strMsg;
    }
}
