package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

/**
 * Created by Jhon Morantes on 16/4/2018.
 */

public class Tkn11 {

    public static String id_banco;
    public static String agenci;
    public static String no_cuenta;
    public static String digito;
    public static String nombre;

    private byte[] idbanco = new byte[]{0x00,0x10};
    private byte[] agencia = new byte[]{0x30,0x30,0x30};
    private byte[] ncuenta = new byte[7];
    private byte[] dgt = new byte[]{0x20,0x20};
    private byte[] nmbr = new byte[]{0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20};

    public void clean(){
        idbanco = new byte[]{0x00,0x10};
        agencia = new byte[]{0x30,0x30,0x30};
        ncuenta = new byte[7];
        dgt = new byte[]{0x20,0x20};
        nmbr = new byte[]{0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20};
    }

    public byte[] getIdbanco() {
        return idbanco;
    }

    public void setIdbanco(byte[] idbanco) {
        this.idbanco = idbanco;
    }

    public byte[] getAgencia() {
        return agencia;
    }

    public void setAgencia(byte[] agencia) {
        this.agencia = agencia;
    }

    public byte[] getNcuenta() {
        return ncuenta;
    }

    public void setNcuenta(byte[] ncuenta) {
        this.ncuenta = ncuenta;
    }

    public byte[] getDgt() {
        return dgt;
    }

    public void setDgt(byte[] dgt) {
        this.dgt = dgt;
    }

    public byte[] getNmbr() {
        return nmbr;
    }

    /**
     *
     * @param nmbr
     */
    public void setNmbr(byte[] nmbr) {
        nombre = ISOUtil.bcd2str(nmbr,0,nmbr.length);
        nombre = ISOUtil.padleft(nombre,64,' ');
        nmbr = ISOUtil.str2bcd(nombre,false);
        this.nmbr = nmbr;
        nombre = ISOUtil.hex2AsciiStr(nombre);
    }

    /**
     *
     * @return
     */
    public String packTkn11(){
        String tkn11 = "";
        tkn11 = "110049";//IdFld,sizeFld
        tkn11 += ISOUtil.bcd2str(idbanco, 0, idbanco.length);
        tkn11 += ISOUtil.bcd2str(agencia,0,agencia.length);
        tkn11 += ISOUtil.bcd2str(ncuenta, 0, ncuenta.length);
        tkn11 += ISOUtil.bcd2str(dgt,0,dgt.length);
        tkn11 += ISOUtil.bcd2str(nmbr,0,nmbr.length);

        return tkn11;
    }
}
