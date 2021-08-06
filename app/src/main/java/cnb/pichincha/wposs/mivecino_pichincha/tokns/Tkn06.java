package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn06 {

    private byte[] nomEmpresa = new byte[20];
    private byte[] nomPersona = new byte[20];
    private byte[] fecha_contable = new byte[4];
    private byte[] valor_documento = new byte[6];
    private byte[] valor_descuento = new byte[6];
    private byte[] valor_cuenta = new byte[6];
    private byte[] costo_servicio = new byte[6];
    private byte[] valor_multa = new byte[6];
    private byte[] valor_intereses = new byte[6];
    private byte[] valor_adeudado = new byte[6];

    public void cleanTkn06(){
        nomEmpresa = new byte[20];
        nomPersona = new byte[20];
        fecha_contable = new byte[4];
        valor_documento = new byte[6];
        valor_descuento = new byte[6];
        valor_cuenta = new byte[6];
        costo_servicio = new byte[6];
        valor_multa = new byte[6];
        valor_intereses = new byte[6];
        valor_adeudado = new byte[6];
    }

    public byte[] getNomEmpresa() {
        return nomEmpresa;
    }

    public void setNomEmpresa(byte[] nomEmpresa) {
        this.nomEmpresa = nomEmpresa;
    }

    public byte[] getNomPersona() {
        return nomPersona;
    }

    public void setNomPersona(byte[] nomPersona) {
        this.nomPersona = nomPersona;
    }

    public byte[] getFecha_contable() {
        return fecha_contable;
    }

    public void setFecha_contable(byte[] fecha_contable) {
        this.fecha_contable = fecha_contable;
    }

    public byte[] getValor_documento() {
        return valor_documento;
    }

    public void setValor_documento(byte[] valor_documento) {
        this.valor_documento = valor_documento;
    }

    public byte[] getValor_descuento() {
        return valor_descuento;
    }

    public void setValor_descuento(byte[] valor_descuento) {
        this.valor_descuento = valor_descuento;
    }

    public byte[] getValor_cuenta() {
        return valor_cuenta;
    }

    public void setValor_cuenta(byte[] valor_cuenta) {
        this.valor_cuenta = valor_cuenta;
    }

    public byte[] getCosto_servicio() {
        return costo_servicio;
    }

    public void setCosto_servicio(byte[] costo_servicio) {
        this.costo_servicio = costo_servicio;
    }

    public byte[] getValor_multa() {
        return valor_multa;
    }

    public void setValor_multa(byte[] valor_multa) {
        this.valor_multa = valor_multa;
    }

    public byte[] getValor_intereses() {
        return valor_intereses;
    }

    public void setValor_intereses(byte[] valor_intereses) {
        this.valor_intereses = valor_intereses;
    }

    public byte[] getValor_adeudado() {
        return valor_adeudado;
    }

    public void setValor_adeudado(byte[] valor_adeudado) {
        this.valor_adeudado = valor_adeudado;
    }

    /**
     *
     * @return
     */
    public String packTkn06(){
        StringBuilder tkn06 = new StringBuilder();
        tkn06.append("060086");//IdFld,sizeFld
        tkn06.append(ISOUtil.bcd2str(nomEmpresa, 0, nomEmpresa.length));
        tkn06.append(ISOUtil.bcd2str(nomPersona,0,nomPersona.length));
        tkn06.append(ISOUtil.bcd2str(fecha_contable, 0, fecha_contable.length));
        tkn06.append(ISOUtil.bcd2str(valor_documento,0,valor_documento.length));
        tkn06.append(ISOUtil.bcd2str(valor_descuento,0,valor_descuento.length));
        tkn06.append(ISOUtil.bcd2str(valor_cuenta,0,valor_cuenta.length));
        tkn06.append(ISOUtil.bcd2str(costo_servicio,0,costo_servicio.length));
        tkn06.append(ISOUtil.bcd2str(valor_multa,0,valor_multa.length));
        tkn06.append(ISOUtil.bcd2str(valor_intereses,0,valor_intereses.length));
        tkn06.append(ISOUtil.bcd2str(valor_adeudado,0,valor_adeudado.length));

        return tkn06.toString();
    }
}
