package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;

public class Tkn98 {

    private int nmros = 0;
    private String aux;
    private String[] LasCuentas = new String[nmros];
    private byte[] nmrDCuentas = new byte[1];
    private byte[] ctas = new byte[nmros];
    private String cuentaSeleccionada = "";

    public void cleanTkn98() {
        nmros = 0;
        LasCuentas = new String[nmros];
        nmrDCuentas = new byte[1];
        ctas = new byte[nmros];
    }

    public byte[] getNmrDCuentas() {
        return nmrDCuentas;
    }

    /**
     *
     * @param nmrDCuentas
     */
    public void setNmrDCuentas(byte[] nmrDCuentas) {
        this.nmrDCuentas = nmrDCuentas;
        aux = ISOUtil.bcd2str(nmrDCuentas, 0, 1);
        nmros = Integer.parseInt(aux);
    }

    public byte[] getCtas() {
        return ctas;
    }

    /**
     *
     * @param ctas
     */
    public void setCtas(byte[] ctas) {
        this.ctas = ctas;
        aux = ISOUtil.bcd2str(ctas,0,ctas.length);
        aux = ISOUtil.hex2AsciiStr(aux);
        LasCuentas = new String[nmros];
        int x=0, y=18;
        for (int i=0; i<nmros; i++){
            LasCuentas[i] = aux.substring(x,y);
            x+=18;
            y+=18;
        }
    }

    public String[] getLasCuentas() {
        return LasCuentas;
    }

    public void setLasCuentas(String[] lasCuentas) {
        LasCuentas = lasCuentas;
    }

    public String getCuentaSeleccionada() {
        return cuentaSeleccionada;
    }

    public void setCuentaSeleccionada(String cuentaSeleccionada) {
        this.cuentaSeleccionada = cuentaSeleccionada;
    }

    /**
     *
     * @return
     */
    public String packTkn98(){
        String Tkn98 = "980019";
        if (cuentaSeleccionada.equals("")){
            Tkn98 += "00000000000000000000000000000000000000";
        } else {
            Tkn98 += "01";
        }
        Tkn98 += ISOUtil.convertStringToHex(cuentaSeleccionada);
        return Tkn98;
    }

    public String packTkn98DesCred() {
        String idTkn98 = "98";

        String cad = ISOUtil.bcd2str(nmrDCuentas,0,nmrDCuentas.length);
        cad += ISOUtil.bcd2str(ctas,0,ctas.length);

        int lenBuff = (cad.length()/2);
        String len =  ISOUtil.padleft(lenBuff + "", 4, '0');

        return idTkn98 + len + cad;
    }

    /**
     * Realizamos validación sobre el número de cuentas, para saber si es cliente registrado del banco
     * o simplemente es beneficiario para remesas.
     * Sí número de cuentas es diferente de 0, es beneficiario y cliente del banco.
    **/
    public boolean clienteInscrito() {
        int cuentas = Integer.parseInt(ISOUtil.bcd2str(getNmrDCuentas(),0, getNmrDCuentas().length));
        return cuentas != 0 ? true : false;
    }
}
