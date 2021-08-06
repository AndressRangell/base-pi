package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn25 {

    private byte[] lenTok25 = new byte[1];
    private byte[] tkn25 = new byte[999];
    public String[] fechas;

    public void clean(){
        lenTok25 = new byte[1];
        tkn25 = new byte[999];
    }

    public byte[] getLenTok25() {
        return lenTok25;
    }

    public void setLenTok25(byte[] lenTok25) {
        this.lenTok25 = lenTok25;
    }

    public byte[] getTkn25() {
        return tkn25;
    }

    public void setTkn25(byte[] tkn25) {
        int i;
        int len = 0;
        this.tkn25 = tkn25;
        String strMsg;

        strMsg = ISOUtil.bcd2str(tkn25,0, tkn25.length);
        len = ISOUtil.bcd2int(lenTok25,0,lenTok25.length);
        fechas = new String[len];

        int x = 0;
        int y = 14;

        for (i = 0; i < len; i++) {
            fechas[i] = strMsg.substring(x,y);
            x = y;
            y = y+14;
        }
    }

    public String packTkn25(){
        String idTkn25 = "25";

        String strMsg = ISOUtil.bcd2str(lenTok25,0, lenTok25.length);
        strMsg += ISOUtil.bcd2str(tkn25,0, tkn25.length);

        int lenBuff = (strMsg.length()/2);
        String len =  ISOUtil.padleft(lenBuff + "", 4, '0');

        return idTkn25 + len + strMsg;
    }

}
