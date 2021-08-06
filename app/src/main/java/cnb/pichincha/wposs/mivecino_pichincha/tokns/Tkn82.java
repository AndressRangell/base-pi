package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.global.TMConfig;
import newpos.libpay.utils.ISOUtil;

/**
 *
 */

public class Tkn82 {

    private  byte[] ruc;
    private  byte[] nombreEmpresa;
    private  byte[] cedula;
    private  byte[] nombreUsuario;
    private  byte[] razonSocial;
    private  byte[] validarUltDigCed;



    public  byte[] getRuc() {
        return ruc;
    }

    /**
     *
     * @param ruc
     */
    public void setRuc(byte[] ruc) {
        this.ruc = ruc;
        String rucAux;
        rucAux= ISOUtil.bcd2str(ruc,0,ruc.length);
        rucAux = ISOUtil.cutChar(rucAux, "F");

    }

    public  byte[] getNombreEmpresa() {
        return nombreEmpresa;
    }

    /**
     *
     * @param nombreEmpresa
     */
    public void setNombreEmpresa( byte[] nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
        String hexNE=ISOUtil.byte2hex(nombreEmpresa,0,nombreEmpresa.length);
    }

    public  byte[] getCedula() {
        return cedula;
    }

    /**
     *
     * @param cedula
     */
    public void setCedula( byte[] cedula) {
        this.cedula= cedula;
        String cedAux;
        cedAux= ISOUtil.bcd2str(cedula,0,cedula.length);
        cedAux = ISOUtil.cutChar(cedAux, "F");
        TMConfig.getInstance().setCedula(cedAux);


    }

    public  byte[] getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     *
     * @param nombreUsuario
     */
    public void setNombreUsuario( byte[] nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        String hexName = ISOUtil.byte2hex(nombreUsuario,0,nombreUsuario.length);
    }

    public  byte[] getRazonSocial() {
        return razonSocial;
    }

    /**
     *
     * @param razonSocial
     */
    public void setRazonSocial( byte[] razonSocial) {
        this.razonSocial = razonSocial;
    }

    public byte[] getValidarUltDigCed() {
        return validarUltDigCed;
    }

    public void setValidarUltDigCed(byte[] validarUltDigCed) {
        this.validarUltDigCed = validarUltDigCed;

    }

}
