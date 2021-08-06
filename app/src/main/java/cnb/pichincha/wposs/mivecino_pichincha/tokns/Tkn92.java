package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn92 {

    private byte[] tipoDocumento = new byte[1];
    private byte[] fechaTrx = new byte[8];
    private byte[] numeroComprobante = new byte[12];

    public byte[] getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(byte[] tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public byte[] getFechaTrx() {
        return fechaTrx;
    }

    public void setFechaTrx(byte[] fechaTrx) {
        this.fechaTrx = fechaTrx;
    }

    public byte[] getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(byte[] numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    private String inputText;
    private String inputDate;
    private String itemSelect;

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getInputDate() {
        return inputDate;
    }

    public void setInputDate(String inputDate) {
        this.inputDate = inputDate;
    }

    public void setItemSelect(String itemSelect) {
        this.itemSelect = itemSelect;
    }

    /**
     *
     * @return
     */
    public String packTkn92 (){
        String tkn92;
        tkn92 = "920021";
        tkn92 += ISOUtil.convertStringToHex(itemSelect);
        tkn92 += ISOUtil.convertStringToHex(inputDate);
        tkn92 += ISOUtil.convertStringToHex(ISOUtil.padright(inputText, 12, 'F'));
        return tkn92;
    }


}
