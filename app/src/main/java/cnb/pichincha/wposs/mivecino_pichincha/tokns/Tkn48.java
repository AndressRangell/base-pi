package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn48 {

    private byte[] numero_kit = new byte[]{0x20,0x20,0x20,0x20,0x20};
    private byte[] numero_cel = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00};
    private byte[] nombre = new byte[30];

    public void clean(){
        numero_kit = new byte[]{0x20,0x20,0x20,0x20,0x20};
        numero_cel = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00};
        nombre = new byte[30];
    }

    public byte[] getNumero_kit() {
        return numero_kit;
    }

    public void setNumero_kit(byte[] numero_kit) {
        this.numero_kit = numero_kit;
    }

    public byte[] getNumero_cel() {
        return numero_cel;
    }

    public void setNumero_cel(byte[] numero_cel) {
        this.numero_cel = numero_cel;
    }

    public byte[] getNombre() {
        return nombre;
    }

    public void setNombre(byte[] nombre) {
        this.nombre = nombre;
    }

    public String packTkn48() {
        String Tkn48 = "480011";
        Tkn48 += ISOUtil.bcd2str(numero_kit,0,numero_kit.length);
        Tkn48 += ISOUtil.bcd2str(numero_cel,0,numero_cel.length);
        return Tkn48;
    }
}
