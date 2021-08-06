package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn07 {

    private byte[] mensaje = new byte[100];

    public void cleanTkn07(){
        mensaje = new byte[100];
    }

    public byte[] getMensaje() {
        return mensaje;
    }

    public void setMensaje(byte[] mensaje) {
        this.mensaje = mensaje;
    }


    /**
     *
     * @return
     */
    public String obtenerMensaje(){
        String tkn07 = "";
        if(mensaje!=null) {
            tkn07 += ISOUtil.bcd2str(mensaje, 0, mensaje.length);
            tkn07 = ISOUtil.hex2AsciiStr(tkn07);
        }
        return tkn07;
    }
}
