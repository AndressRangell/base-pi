package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn14 {

    private byte[] nombre = new byte[50];
    private byte[] valor = new byte[6];
    private byte[] periodo = new byte[4];
    private byte[] periodoFin = new byte[4];
    private byte[] tipo_sub = new byte[15];
    private byte[] informacion = new byte[60];

    public void clean(){
        nombre = new byte[50];
        valor = new byte[6];
        periodo = new byte[4];
        periodoFin = new byte[4];
        tipo_sub = new byte[15];
        informacion = new byte[60];
    }

    public String getNombre() {
        String nom = ISOUtil.bcd2str(nombre,0,nombre.length);
        nom = ISOUtil.hex2AsciiStr(nom);
        return nom;
    }

    public void setNombre(byte[] nombre) {
        this.nombre = nombre;
    }

    public byte[] getValor() {
        return valor;
    }

    public void setValor(byte[] valor) {
        this.valor = valor;
    }

    /**
     *
     * @return
     */
    public String getPeriodo() {
        String str = ISOUtil.bcd2str(periodo,0,periodo.length);
        //str = ISOUtil.hex2AsciiStr(str);
        return str;
    }

    public void setPeriodo(byte[] periodo) {
        this.periodo = periodo;
    }

    /**
     *
     * @return
     */
    public String getPeriodoFin() {
        String str = ISOUtil.bcd2str(periodoFin,0,periodoFin.length);
        //str = ISOUtil.hex2AsciiStr(str);
        return str;
    }

    public void setPeriodoFin(byte[] periodoFin) {
        this.periodoFin = periodoFin;
    }

    /**
     *
     * @return
     */
    public String getTipo_sub() {
        String str = ISOUtil.bcd2str(tipo_sub,0,tipo_sub.length);
        str = ISOUtil.hex2AsciiStr(str);
        return str;
    }

    public void setTipo_sub(byte[] tipo_sub) {
        this.tipo_sub = tipo_sub;
    }

    /**
     *
     * @return
     */
    public String getInformacion() {
        String str = ISOUtil.bcd2str(informacion,0,informacion.length);
        str = ISOUtil.hex2AsciiStr(str);
        return str;
    }

    public void setInformacion(byte[] informacion) {
        this.informacion = informacion;
    }

    public String packTkn14(){
        String tok14 = "140139";
        tok14 += ISOUtil.bcd2str(nombre, 0, nombre.length);
        tok14 += ISOUtil.bcd2str(valor, 0, valor.length);
        tok14 += ISOUtil.bcd2str(periodo, 0, periodo.length);
        tok14 += ISOUtil.bcd2str(periodoFin, 0, periodoFin.length);
        tok14 += ISOUtil.bcd2str(tipo_sub, 0, tipo_sub.length);
        tok14 += ISOUtil.bcd2str(informacion, 0, informacion.length);

        return tok14;
    }

}
