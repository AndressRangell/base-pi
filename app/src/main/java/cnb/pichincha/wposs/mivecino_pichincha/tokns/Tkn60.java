package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn60 {

    private byte[] AAAAMM = new byte[3];
    private byte[] numTxs = new byte[2];
    private byte[] fechaAcredita = new byte[4];
    private byte[] vlrPago = new byte[6];
    private byte[] estado = new byte[15];
    private byte[] motivo = new byte[20];
    private byte[] cuenta = new byte[5];
    private byte[] factura = new byte[20];
    private byte[] fechaEmision = new byte[4];
    private byte[] baseImponible = new byte[6];
    private byte[] porcentIva = new byte[2];
    private byte[] iva = new byte[4];
    private byte[] valorTotal = new byte[6];
    private byte[] porcentRetencIva = new byte[2];
    private byte[] retencionIva = new byte[4];
    private byte[] porcentRetencServicio = new byte[2];
    private byte[] retencionServicio = new byte[4];

    public void clean(){
        AAAAMM = new byte[3];
        numTxs = new byte[2];
        fechaAcredita = new byte[4];
        vlrPago = new byte[6];
        estado = new byte[15];
        motivo = new byte[20];
        cuenta = new byte[5];
        factura = new byte[20];
        fechaEmision = new byte[4];
        baseImponible = new byte[6];
        porcentIva = new byte[2];
        iva = new byte[4];
        valorTotal = new byte[6];
        porcentRetencIva = new byte[2];
        retencionIva = new byte[4];
        porcentRetencServicio = new byte[2];
        retencionServicio = new byte[4];
    }

    public byte[] getAAAAMM() {
        return AAAAMM;
    }

    public void setAAAAMM(byte[] AAAAMM) {
        this.AAAAMM = AAAAMM;
    }

    public byte[] getNumTxs() {
        return numTxs;
    }

    public void setNumTxs(byte[] numTxs) {
        this.numTxs = numTxs;
    }

    public byte[] getFechaAcredita() {
        return fechaAcredita;
    }

    public void setFechaAcredita(byte[] fechaAcredita) {
        this.fechaAcredita = fechaAcredita;
    }

    public byte[] getVlrPago() {
        return vlrPago;
    }

    public void setVlrPago(byte[] vlrPago) {
        this.vlrPago = vlrPago;
    }

    public byte[] getEstado() {
        return estado;
    }

    public void setEstado(byte[] estado) {
        this.estado = estado;
    }

    public byte[] getMotivo() {
        return motivo;
    }

    public void setMotivo(byte[] motivo) {
        this.motivo = motivo;
    }

    public byte[] getCuenta() {
        return cuenta;
    }

    public void setCuenta(byte[] cuenta) {
        this.cuenta = cuenta;
    }

    public byte[] getFactura() {
        return factura;
    }

    public void setFactura(byte[] factura) {
        this.factura = factura;
    }

    public byte[] getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(byte[] fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public byte[] getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(byte[] baseImponible) {
        this.baseImponible = baseImponible;
    }

    public byte[] getPorcentIva() {
        return porcentIva;
    }

    public void setPorcentIva(byte[] porcentIva) {
        this.porcentIva = porcentIva;
    }

    public byte[] getIva() {
        return iva;
    }

    public void setIva(byte[] iva) {
        this.iva = iva;
    }

    public byte[] getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(byte[] valorTotal) {
        this.valorTotal = valorTotal;
    }

    public byte[] getPorcentRetencIva() {
        return porcentRetencIva;
    }

    public void setPorcentRetencIva(byte[] porcentRetencIva) {
        this.porcentRetencIva = porcentRetencIva;
    }

    public byte[] getRetencionIva() {
        return retencionIva;
    }

    public void setRetencionIva(byte[] retencionIva) {
        this.retencionIva = retencionIva;
    }

    public byte[] getPorcentRetencServicio() {
        return porcentRetencServicio;
    }

    public void setPorcentRetencServicio(byte[] porcentRetencServicio) {
        this.porcentRetencServicio = porcentRetencServicio;
    }

    public byte[] getRetencionServicio() {
        return retencionServicio;
    }

    public void setRetencionServicio(byte[] retencionServicio) {
        this.retencionServicio = retencionServicio;
    }

    /**
     *
     * @return
     */
    public String packTkn60(){
        StringBuilder tkn60 = new StringBuilder();
        tkn60.append("600109");//IdFld,sizeFld
        tkn60.append(ISOUtil.bcd2str(AAAAMM, 0, AAAAMM.length));
        tkn60.append(ISOUtil.bcd2str(numTxs,0,numTxs.length));
        tkn60.append(ISOUtil.bcd2str(fechaAcredita, 0, fechaAcredita.length));
        tkn60.append(ISOUtil.bcd2str(vlrPago,0,vlrPago.length));
        tkn60.append(ISOUtil.bcd2str(estado,0,estado.length));
        tkn60.append(ISOUtil.bcd2str(motivo,0,motivo.length));
        tkn60.append(ISOUtil.bcd2str(cuenta,0,cuenta.length));
        tkn60.append(ISOUtil.bcd2str(factura,0,factura.length));
        tkn60.append(ISOUtil.bcd2str(fechaEmision,0,fechaEmision.length));
        tkn60.append(ISOUtil.bcd2str(baseImponible,0,baseImponible.length));
        tkn60.append(ISOUtil.bcd2str(porcentIva,0,porcentIva.length));
        tkn60.append(ISOUtil.bcd2str(iva,0,iva.length));
        tkn60.append(ISOUtil.bcd2str(valorTotal,0,valorTotal.length));
        tkn60.append(ISOUtil.bcd2str(porcentRetencIva,0,porcentRetencIva.length));
        tkn60.append(ISOUtil.bcd2str(retencionIva,0,retencionIva.length));
        tkn60.append(ISOUtil.bcd2str(porcentRetencServicio,0,porcentRetencServicio.length));
        tkn60.append(ISOUtil.bcd2str(retencionServicio,0,retencionServicio.length));
        return tkn60.toString();
    }

}
