package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn03 {
    private byte[] tipoTarjeta = new byte[2];
    private byte[] numeroTarjeta = new byte[42];
    private byte[] codigoEmpresa = new byte[10];
    private byte[] contrapartida = new byte[34];
    private byte[] modoPago = new byte[1];

    public void cleanTkn03(){
        tipoTarjeta = new byte[2];
        numeroTarjeta = new byte[42];
        codigoEmpresa = new byte[10];
        contrapartida = new byte[34];
        modoPago = new byte[1];
    }

    public byte[] getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(byte[] tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public byte[] getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(byte[] numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public byte[] getCodigoEmpresa() {
        return codigoEmpresa;
    }

    public void setCodigoEmpresa(byte[] codigoEmpresa) {
        this.codigoEmpresa = codigoEmpresa;
    }

    public byte[] getContrapartida() {
        return contrapartida;
    }

    public void setContrapartida(byte[] contrapartida) {
        this.contrapartida = contrapartida;
    }

    public byte[] getModoPago() {
        return modoPago;
    }

    public void setModoPago(byte[] modoPago) {
        this.modoPago = modoPago;
    }

    public String packTkn03(boolean pagTarjeta){
        String tkn03 = " ";
        tkn03 = "030045";
        if (pagTarjeta){
            tkn03 += ISOUtil.byte2hex(codigoEmpresa);
            tkn03 += ISOUtil.byte2hex(contrapartida);
            tkn03 += "04";
        }else{
            tkn03 += ISOUtil.byte2hex(codigoEmpresa);
            tkn03 += ISOUtil.byte2hex(contrapartida);
            tkn03 += "01";
        }
        return tkn03;
    }

    public String packTkn03PagTar(boolean pagTarjeta) {
        String tkn03 = "";
        tkn03 = "030045";
        if (pagTarjeta){
            tkn03 += ISOUtil.byte2hex(tipoTarjeta);
            tkn03 += ISOUtil.byte2hex(numeroTarjeta);
            tkn03 += "04";
        }else{
            tkn03 += ISOUtil.byte2hex(tipoTarjeta);
            tkn03 += ISOUtil.byte2hex(numeroTarjeta);
            tkn03 += "01";
        }
        return tkn03;

    }


}
