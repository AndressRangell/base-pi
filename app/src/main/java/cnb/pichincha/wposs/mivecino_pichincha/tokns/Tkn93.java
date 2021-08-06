package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn93 {
    private byte[] estadoMsg = new byte[]{0x00};
    private byte[] lenMensaje = new byte[]{0x00,0x00};

    public void cleanTkn93(){
        estadoMsg = new byte[]{0x00};
        lenMensaje = new byte[]{0x00,0x00};
    }

    public byte[] getEstadoMsg() {
        return estadoMsg;
    }

    public void setEstadoMsg(byte[] estadoMsg) {
        this.estadoMsg = estadoMsg;
    }

    public byte[] getLenMensaje() {
        return lenMensaje;
    }

    public void setLenMensaje(byte[] lenMensaje) {
        this.lenMensaje = lenMensaje;
    }

    /**
     *
     * @return
     */
    public String packTkn93(){
        String tkn93 = "930003";
        tkn93 += ISOUtil.bcd2str(estadoMsg,0,estadoMsg.length);
        tkn93 += ISOUtil.bcd2str(lenMensaje,0,lenMensaje.length);
        return tkn93;
    }

    public void clean(){
        estadoMsg = new byte[]{0x00};
        lenMensaje = new byte[]{0x00,0x00};
    }
}
