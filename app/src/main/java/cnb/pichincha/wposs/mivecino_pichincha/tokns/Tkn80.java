package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.global.TMConfig;
import newpos.libpay.utils.ISOUtil;

/**
 *
 */

public class Tkn80 {

    private  byte[] timeMaxConnect = new byte[2];
    private  byte[] timeMaxWait = new byte[2];
    private  byte[] maxRetiros = new byte[1];
    private  byte[] valMinRetiro = new byte[2];
    private  byte[] valMaxRetiro = new byte[2];
    private  byte[] maxDepositos = new byte[1];
    private  byte[] valMinDeposito = new byte[2];
    private  byte[] valMaxDeposito = new byte[2];
    private  byte[] costoServicio = new byte[2];
    private  byte[] tipoNegocio = new byte[] {0x4E};// N:Natural - J:Juridico - C: Centralizado

    public byte[] getTimeMaxConnect() {
        return timeMaxConnect;
    }

    public void setTimeMaxConnect(byte[] timeMaxConnect) {
        this.timeMaxConnect = timeMaxConnect;
        TMConfig.getInstance().setTimeConnect(Integer.parseInt(ISOUtil.bcd2str(timeMaxConnect, 0, timeMaxConnect.length))*1000);

    }

    public byte[] getTimeMaxWait() {
        return timeMaxWait;
    }

    public void setTimeMaxWait(byte[] timeMaxWait) {
        this.timeMaxWait = timeMaxWait;
        TMConfig.getInstance().setTimeout(Integer.parseInt(ISOUtil.bcd2str(timeMaxWait, 0, timeMaxWait.length))*1000);

    }

    public byte[] getMaxRetiros() {
        return maxRetiros;
    }

    public void setMaxRetiros(byte[] maxRetiros) {
        this.maxRetiros = maxRetiros;
    }

    public byte[] getValMinRetiro() {
        return valMinRetiro;
    }

    public void setValMinRetiro(byte[] valMinRetiro) {
        this.valMinRetiro = valMinRetiro;
    }

    public byte[] getValMaxRetiro() {
        return valMaxRetiro;
    }

    public void setValMaxRetiro(byte[] valMaxRetiro) {
        this.valMaxRetiro = valMaxRetiro;
    }

    public byte[] getMaxDepositos() {
        return maxDepositos;
    }

    public void setMaxDepositos(byte[] maxDepositos) {
        this.maxDepositos = maxDepositos;
    }

    public byte[] getValMinDeposito() {
        return valMinDeposito;
    }

    public void setValMinDeposito(byte[] valMinDeposito) {
        this.valMinDeposito = valMinDeposito;
    }

    public byte[] getValMaxDeposito() {
        return valMaxDeposito;
    }

    public void setValMaxDeposito(byte[] valMaxDeposito) {
        this.valMaxDeposito = valMaxDeposito;
    }

    public byte[] getCostoServicio() {
        return costoServicio;
    }

    public void setCostoServicio(byte[] costoServicio) {
        this.costoServicio = costoServicio;
    }

    public byte[] getTipoNegocio() {
        return tipoNegocio;
    }

    public void setTipoNegocio(byte[] tipoNegocio) {
        this.tipoNegocio = tipoNegocio;
    }
}
