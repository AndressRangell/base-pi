package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.ResultControl;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.Utils;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;

/**
 * Created by Jhon Morantes on 9/4/2018.
 */

public class Tkns {
    private byte[] tkn01;
    private byte[] tkn03;
    private byte[] tkn06;
    private byte[] tkn07;
    private byte[] tkn09;
    private byte[] tkn11;
    private byte[] tkn12;
    private byte[] tkn13;
    private byte[] tkn14;
    private byte[] tkn15;
    private byte[] tkn16;
    private byte[] tkn17;
    private byte[] tkn19;
    private byte[] tkn20;
    private byte[] tkn22;
    private byte[] tkn25;
    private byte[] tkn26;
    private byte[] tkn27;
    private byte[] tkn28;
    private byte[] tkn33;
    private byte[] tkn38;
    private byte[] tkn39;
    private byte[] tkn40;
    private byte[] tkn47;
    private byte[] tkn48;
    private byte[] tkn49;
    private byte[] tkn60;
    private byte[] tkn80;
    private byte[] tkn81;
    private byte[] tkn82;
    private byte[] tkn93;
    private byte[] tkn96;
    private byte[] tkn97;
    private byte[] tkn98;

    private int indice;
    private byte[] idTkn;
    private byte[] lenTkn = new byte[2];
    private int auxLenTkn;
    String auxIdTkn;

    public byte[] getTkn01() {
        return tkn01;
    }

    public void setTkn01(byte[] tkn_01) {
        int indice = 0;

        System.arraycopy(tkn_01, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn01 = new byte[auxLenTkn];
            System.arraycopy(tkn_01, this.indice, tkn01, 0 ,auxLenTkn);

            indice += 14;
            byte[] auxTkn01 = new  byte[auxLenTkn];
            System.arraycopy(tkn01, indice, auxTkn01, 0, auxLenTkn);
            InitTrans.tkn01.setNumApp(auxTkn01);
            this.indice += indice;
        }
    }

    public byte[] getTkn06() {
        return tkn06;
    }

    /**
     *
     * @param tkn_06
     */
    public void setTkn06(byte[] tkn_06) {
        int indice;

        System.arraycopy(tkn_06, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            if (auxLenTkn == 86) {
                tkn06 = new byte[auxLenTkn];
                System.arraycopy(tkn_06, this.indice, tkn06, 0, auxLenTkn);

                indice = 0;
                byte[] auxTkn06 = new byte[20];
                System.arraycopy(tkn06, indice, auxTkn06, 0, 20);
                indice += 20;
                InitTrans.tkn06.setNomEmpresa(auxTkn06);

                auxTkn06 = new byte[20];
                System.arraycopy(tkn06, indice, auxTkn06, 0, 20);
                indice += 20;
                InitTrans.tkn06.setNomPersona(auxTkn06);

                auxTkn06 = new byte[4];
                System.arraycopy(tkn06, indice, auxTkn06, 0, 4);
                indice += 4;
                InitTrans.tkn06.setFecha_contable(auxTkn06);

                auxTkn06 = new byte[6];
                System.arraycopy(tkn06, indice, auxTkn06, 0, 6);
                indice += 6;
                InitTrans.tkn06.setValor_documento(auxTkn06);

                auxTkn06 = new byte[6];
                System.arraycopy(tkn06, indice, auxTkn06, 0, 6);
                indice += 6;
                InitTrans.tkn06.setValor_descuento(auxTkn06);

                auxTkn06 = new byte[6];
                System.arraycopy(tkn06, indice, auxTkn06, 0, 6);
                indice += 6;
                InitTrans.tkn06.setValor_cuenta(auxTkn06);

                auxTkn06 = new byte[6];
                System.arraycopy(tkn06, indice, auxTkn06, 0, 6);
                indice += 6;
                InitTrans.tkn06.setCosto_servicio(auxTkn06);

                auxTkn06 = new byte[6];
                System.arraycopy(tkn06, indice, auxTkn06, 0, 6);
                indice += 6;
                InitTrans.tkn06.setValor_multa(auxTkn06);

                auxTkn06 = new byte[6];
                System.arraycopy(tkn06, indice, auxTkn06, 0, 6);
                indice += 6;
                InitTrans.tkn06.setValor_intereses(auxTkn06);

                auxTkn06 = new byte[6];
                System.arraycopy(tkn06, indice, auxTkn06, 0, 6);
                indice += 6;
                InitTrans.tkn06.setValor_adeudado(auxTkn06);
                this.indice += indice;
            }
        }
    }

    public byte[] getTkn07() {
        return tkn07;
    }

    /**
     *
     * @param tkn_07
     */
    public void setTkn07(byte[] tkn_07) {

        System.arraycopy(tkn_07, indice, lenTkn, 0, 2);
        indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn07 = new byte[auxLenTkn];

            System.arraycopy(tkn_07, indice, tkn07, 0, auxLenTkn);
            InitTrans.tkn07.setMensaje(tkn07);
            indice += auxLenTkn;
        }

    }

    public byte[] getTkn11() {
        return tkn11;
    }

    /**
     *
     * @param tkn_11
     */
    public void setTkn11(byte[] tkn_11) {
        //this.tkn11 = tkn_11;

        int indice;
        String auxTok;

        System.arraycopy(tkn_11, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn11 = new byte[auxLenTkn];
            System.arraycopy(tkn_11, this.indice, tkn11, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn11 = new byte[2];
            System.arraycopy(tkn11, indice, auxTkn11, 0, 2);
            auxTok = ISOUtil.bcd2str(auxTkn11, 0, auxTkn11.length);
            auxTok = ISOUtil.padleft(auxTok, 4, ' ');
            auxTkn11 = ISOUtil.str2bcd(auxTok, false);
            indice += 2;
            InitTrans.tkn11.setIdbanco(auxTkn11);

            auxTkn11 = new byte[3];
            System.arraycopy(tkn11, indice, auxTkn11, 0, 3);
            indice += 3;
            InitTrans.tkn11.setAgencia(auxTkn11);

            auxTkn11 = new byte[7];
            System.arraycopy(tkn11, indice, auxTkn11, 0, 7);
            indice += 7;
            InitTrans.tkn11.setNcuenta(auxTkn11);

            auxTkn11 = new byte[2];
            System.arraycopy(tkn11, indice, auxTkn11, 0, 2);
            indice += 2;
            InitTrans.tkn11.setDgt(auxTkn11);

            auxTkn11 = new byte[32];
            System.arraycopy(tkn11, indice, auxTkn11, 0, 32);
            indice += 32;
            InitTrans.tkn11.setNmbr(auxTkn11);
            this.indice += indice;
        }
    }

    public byte[] getTkn12() {
        return tkn12;
    }

    public void setTkn12(byte[] tkn_12) {
        //this.tkn12 = tkn12;

        int indice;
        String auxTok;

        System.arraycopy(tkn_12, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn12 = new byte[auxLenTkn];
            System.arraycopy(tkn_12, this.indice, tkn12, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn12 = new byte[8];
            System.arraycopy(tkn12, indice, auxTkn12, 0, 8);
            indice += 8;
            InitTrans.tkn12.setCedula2(auxTkn12);

            this.indice += indice;
        }

    }

    public void setTkn13(byte[] tkn_13) {
        //this.tkn13 = tkn13;
        int indice;

        System.arraycopy(tkn_13, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn13 = new byte[auxLenTkn];
            System.arraycopy(tkn_13, this.indice, tkn13, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn13 = new byte[1];
            System.arraycopy(tkn13, indice, auxTkn13, 0, 1);
            indice += 1;
            InitTrans.tkn13.setTipoPago(auxTkn13);

            this.indice += indice;
        }
    }

    public byte[] getTkn14() {
        return tkn14;
    }

    /**
     *
     * @param tkn_14
     */
    public void setTkn14(byte[] tkn_14) {
        //this.tkn15 = tkn15;

        int indice;

        System.arraycopy(tkn_14, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn14 = new byte[auxLenTkn];
            System.arraycopy(tkn_14, this.indice, tkn14, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn14 = new byte[50];
            System.arraycopy(tkn14, indice, auxTkn14, 0, 50);
            indice += 50;
            InitTrans.tkn14.setNombre(auxTkn14);

            auxTkn14 = new byte[6];
            System.arraycopy(tkn14, indice, auxTkn14, 0, 6);
            indice += 6;
            InitTrans.tkn14.setValor(auxTkn14);

            auxTkn14 = new byte[4];
            System.arraycopy(tkn14, indice, auxTkn14, 0, 4);
            indice += 4;
            InitTrans.tkn14.setPeriodo(auxTkn14);

            auxTkn14 = new byte[4];
            System.arraycopy(tkn14, indice, auxTkn14, 0, 4);
            indice += 4;
            InitTrans.tkn14.setPeriodoFin(auxTkn14);

            auxTkn14 = new byte[15];
            System.arraycopy(tkn14, indice, auxTkn14, 0, 15);
            indice += 15;
            InitTrans.tkn14.setTipo_sub(auxTkn14);

            auxTkn14 = new byte[60];
            System.arraycopy(tkn14, indice, auxTkn14, 0, 60);
            indice += 60;
            InitTrans.tkn14.setInformacion(auxTkn14);
            this.indice += indice;
        }
    }

    public byte[] getTkn15() {
        return tkn15;
    }

    /**
     *
     * @param tkn_15
     */
    public void setTkn15(byte[] tkn_15) {
        //this.tkn15 = tkn15;

        int indice;

        System.arraycopy(tkn_15, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn15 = new byte[auxLenTkn];
            System.arraycopy(tkn_15, this.indice, tkn15, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn15 = new byte[50];
            System.arraycopy(tkn15, indice, auxTkn15, 0, 50);
            indice += 50;
            InitTrans.tkn15.setNombre(auxTkn15);

            auxTkn15 = new byte[30];
            System.arraycopy(tkn15, indice, auxTkn15, 0, 30);
            //String hexpr = ISOUtil.bcd2str(auxTkn15,0,30);
            //String prueba = ISOUtil.hex2AsciiStr(hexpr);
            indice += 30;
            InitTrans.tkn15.setCargo(auxTkn15);

            auxTkn15 = new byte[10];
            System.arraycopy(tkn15, indice, auxTkn15, 0, 10);
            indice += 10;
            InitTrans.tkn15.setCedula(auxTkn15);
            this.indice += indice;
        }
    }

    public byte[] getTkn16() {
        return tkn16;
    }

    public void setTkn16(byte[] tkn_16) {
        int indice;

        System.arraycopy(tkn_16, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn16 = new byte[auxLenTkn];
            System.arraycopy(tkn_16, this.indice, tkn16, 0, auxLenTkn);


            indice = 0;
            byte[] auxTkn16 = new byte[1];
            System.arraycopy(tkn16, indice, auxTkn16, 0, 1);
            indice += 1;
            InitTrans.tkn16.setMsg_tipo(auxTkn16);

            auxTkn16 = new byte[auxLenTkn - 1];
            System.arraycopy(tkn16, indice, auxTkn16, 0, auxLenTkn - 1);
            indice += auxLenTkn - 1;
            InitTrans.tkn16.setMsg_largo(auxTkn16);
            this.indice += indice;
        }
    }

    public byte[] getTkn17() {
        return tkn17;
    }

    /**
     *
     * @param tkn_17
     */
    public void setTkn17(byte[] tkn_17) {
        int indice;

        System.arraycopy(tkn_17, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0){
            tkn17 = new byte[auxLenTkn];
            System.arraycopy(tkn_17, this.indice, tkn17, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn17 = new byte[1];
            System.arraycopy(tkn17, indice, auxTkn17, 0, 1);
            indice += 1;
            InitTrans.tkn17.setNumItems(auxTkn17);

            auxTkn17 = new byte[auxLenTkn -1];
            System.arraycopy(tkn17, indice, auxTkn17, 0, auxLenTkn -1);
            indice += auxLenTkn -1;
            InitTrans.tkn17.setTkn17(auxTkn17);
            this.indice += indice;
        }
    }

    public byte[] getTkn19() {
        return tkn19;
    }

    public void setTkn19(byte[] tkn_19) {
        //this.tkn19 = tkn19;
        int indice;

        System.arraycopy(tkn_19, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn19 = new byte[auxLenTkn];
            System.arraycopy(tkn_19, this.indice, tkn19, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn19 = new byte[6];
            System.arraycopy(tkn19, indice, auxTkn19, 0, 6);
            indice += 6;
            InitTrans.tkn19.setNumeroControl(auxTkn19);

            this.indice += indice;
        }
    }

    public byte[] getTkn20() {
        return tkn20;
    }

    public void setTkn20(byte[] tkn_20) {
        int indice;

        System.arraycopy(tkn_20, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn20 = new byte[auxLenTkn];
            System.arraycopy(tkn_20, this.indice, tkn20, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn20 = new byte[6];
            System.arraycopy(tkn20, indice, auxTkn20, 0, 6);
            indice += 6;
            InitTrans.tkn20.setMonto1(auxTkn20);

            auxTkn20 = new byte[6];
            System.arraycopy(tkn20, indice, auxTkn20, 0, 6);
            indice += 6;
            InitTrans.tkn20.setMonto2(auxTkn20);

            auxTkn20 = new byte[6];
            System.arraycopy(tkn20, indice, auxTkn20, 0, 6);
            indice += 6;
            InitTrans.tkn20.setMonto3(auxTkn20);

            auxTkn20 = new byte[6];
            System.arraycopy(tkn20, indice, auxTkn20, 0, 6);
            indice += 6;
            InitTrans.tkn20.setMonto4(auxTkn20);

            auxTkn20 = new byte[1];
            System.arraycopy(tkn20, indice, auxTkn20, 0, 1);
            indice += 1;
            InitTrans.tkn20.setIvaPorcentaje(auxTkn20);
            this.indice += indice;
        }
    }

    public byte[] getTkn22() {
        return tkn22;
    }

    public void setTkn22(byte[] tkn_22) {
        int indice;

        System.arraycopy(tkn_22, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn22 = new byte[auxLenTkn];
            System.arraycopy(tkn_22, this.indice, tkn22, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn22 = new byte[20];
            System.arraycopy(tkn22, indice, auxTkn22, 0, 20);
            indice += 20;
            InitTrans.tkn22.setDiaPago(auxTkn22);


            auxTkn22 = new byte[1];
            System.arraycopy(tkn22, indice, auxTkn22, 0, 1);
            indice += 1;
            InitTrans.tkn22.setPlazoPago(auxTkn22);
            this.indice += indice;
        }
    }

    public byte[] getTkn25() {
        return tkn25;
    }

    public void setTkn25(byte[] tkn_25) {
        int indice;

        System.arraycopy(tkn_25, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn25 = new byte[auxLenTkn];
            System.arraycopy(tkn_25, this.indice, tkn25, 0, auxLenTkn);


            indice = 0;
            byte[] auxTkn25 = new byte[1];
            System.arraycopy(tkn25, indice, auxTkn25, 0, 1);
            indice += 1;
            InitTrans.tkn25.setLenTok25(auxTkn25);

            auxTkn25 = new byte[auxLenTkn - 1];
            System.arraycopy(tkn25, indice, auxTkn25, 0, auxLenTkn - 1);
            indice += auxLenTkn - 1;
            InitTrans.tkn25.setTkn25(auxTkn25);
            this.indice += indice;
        }
    }

    public byte[] getTkn26() {
        return tkn26;
    }

    public void setTkn26(byte[] tkn_26) {
        int indice;

        System.arraycopy(tkn_26, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn26 = new byte[auxLenTkn];
            System.arraycopy(tkn_26, this.indice, tkn26, 0, auxLenTkn);


            indice = 0;
            byte[] auxTkn26 = new byte[1];
            System.arraycopy(tkn26, indice, auxTkn26, 0, 1);
            indice += 1;
            InitTrans.tkn26.setLenTok26(auxTkn26);

            auxTkn26 = new byte[auxLenTkn - 1];
            System.arraycopy(tkn26, indice, auxTkn26, 0, auxLenTkn - 1);
            indice += auxLenTkn - 1;
            InitTrans.tkn26.setTkn26(auxTkn26);
            this.indice += indice;
        }
    }

    public byte[] getTkn27() {
        return tkn27;
    }

    /**
     *
     * @param tkn_27
     */
    public void setTkn27(byte[] tkn_27) {
        int indice;

        System.arraycopy(tkn_27, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn27 = new byte[auxLenTkn];
            System.arraycopy(tkn_27, this.indice, tkn27, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn27 = new byte[8];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 8);
            indice += 8;
            InitTrans.tkn27.setCedula(auxTkn27);

            auxTkn27 = new byte[40];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 40);
            indice += 40;
            InitTrans.tkn27.setBeneficiario(auxTkn27);

            auxTkn27 = new byte[10];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 10);
            indice += 10;
            InitTrans.tkn27.setDactilar(auxTkn27);

            auxTkn27 = new byte[10];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 10);
            indice += 10;
            InitTrans.tkn27.setFechaCedula(auxTkn27);

            auxTkn27 = new byte[20];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 20);
            indice += 20;
            InitTrans.tkn27.setRemesa(auxTkn27);

            auxTkn27 = new byte[6];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 6);
            indice += 6;
            InitTrans.tkn27.setMontoRemesa(auxTkn27);

            auxTkn27 = new byte[1];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 1);
            indice += 1;
            InitTrans.tkn27.setFormaPago(auxTkn27);

            auxTkn27 = new byte[6];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 6);
            indice += 6;
            InitTrans.tkn27.setCelular(auxTkn27);

            auxTkn27 = new byte[6];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 6);
            indice += 6;
            InitTrans.tkn27.setOtp(auxTkn27);

            auxTkn27 = new byte[9];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 9);
            indice += 9;
            InitTrans.tkn27.setIdTransaccion(auxTkn27);

            auxTkn27 = new byte[40];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 40);
            indice += 40;
            InitTrans.tkn27.setOrdenante(auxTkn27);

            auxTkn27 = new byte[40];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 40);
            indice += 40;
            InitTrans.tkn27.setDireccion(auxTkn27);

            auxTkn27 = new byte[40];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 40);
            indice += 40;
            InitTrans.tkn27.setEstadoRemesa(auxTkn27);

            auxTkn27 = new byte[10];
            System.arraycopy(tkn27, indice, auxTkn27, 0, 10);
            indice += 10;
            InitTrans.tkn27.setFechaEnvio(auxTkn27);
            this.indice += indice;
        }
    }

    public byte[] getTkn28() {
        return tkn28;
    }


    /*
    *   int indice;

        System.arraycopy(tkn_19, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn19 = new byte[auxLenTkn];
            System.arraycopy(tkn_19, this.indice, tkn19, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn19 = new byte[6];
            System.arraycopy(tkn19, indice, auxTkn19, 0, 6);
            indice += 6;
            InitTrans.tkn19.setNumeroControl(auxTkn19);

            this.indice += indice;
        }*/

    public void setTkn28(byte[] tkn_28) {
        //this.tkn12 = tkn12;

        int indice;
        String auxTok;

        System.arraycopy(tkn_28, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn28 = new byte[auxLenTkn];
            System.arraycopy(tkn_28, this.indice, tkn28, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn28 = new byte[6];
            System.arraycopy(tkn28, indice, auxTkn28, 0, auxTkn28.length);
            indice += 6;
            InitTrans.tkn28.setNumeroOtp(auxTkn28);

            byte[] idTransOtp = new byte[9];
            System.arraycopy(tkn28, indice, idTransOtp, 0, idTransOtp.length);
            indice += 9;
            InitTrans.tkn28.setIdTransaccionOtp(idTransOtp);

            this.indice += indice;
        }

    }

    public byte[] getTkn33() {
        return tkn33;
    }

    public void setTkn33(byte[] tkn_33) {
        int indice;

        System.arraycopy(tkn_33, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn33 = new byte[auxLenTkn];
            System.arraycopy(tkn_33, this.indice, tkn33, 0, auxLenTkn);


            indice = 0;
            byte[] auxTkn33 = new byte[1];
            System.arraycopy(tkn33, indice, auxTkn33, 0, 1);
            indice += 1;
            InitTrans.tkn33.setCantTrans(auxTkn33);

            auxTkn33 = new byte[auxLenTkn - 1];
            System.arraycopy(tkn33, indice, auxTkn33, 0, auxLenTkn - 1);
            indice += auxLenTkn - 1;
            InitTrans.tkn33.setTransBatch(auxTkn33);
            this.indice += indice;
        }
    }

    public byte[] getTkn38() {
        return tkn38;
    }

    public void setTkn38(byte[] tkn_38) {
        //this.tkn38 = tkn38;

        int indice;

        System.arraycopy(tkn_38, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn38 = new byte[auxLenTkn];
            System.arraycopy(tkn_38, this.indice, tkn38, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn38 = new byte[1];
            System.arraycopy(tkn38, indice, auxTkn38, 0, 1);
            indice += 1;
            InitTrans.tkn38.setFormaDePago(auxTkn38);

            this.indice += indice;
        }
    }

    public byte[] getTkn39() {
        return tkn39;
    }

    /**
     *
     * @param tkn_39
     */
    public void setTkn39(byte[] tkn_39) {

        int indice;

        System.arraycopy(tkn_39, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn39 = new byte[auxLenTkn];
            System.arraycopy(tkn_39, this.indice, tkn39, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn39 = new byte[1];
            System.arraycopy(tkn39, indice, auxTkn39, 0, 1);
            indice += 1;
            InitTrans.tkn39.setFlagVarFijo(auxTkn39);

            auxTkn39 = new byte[1];
            System.arraycopy(tkn39, indice, auxTkn39, 0, 1);
            indice += 1;
            InitTrans.tkn39.setFlagUsoFuturo1(auxTkn39);

            auxTkn39 = new byte[1];
            System.arraycopy(tkn39, indice, auxTkn39, 0, 1);
            indice += 1;
            InitTrans.tkn39.setFlagUsoFuturo2(auxTkn39);
            this.indice += indice;
        }
    }

    public byte[] getTkn47() {
        return tkn47;
    }

    /**
     *
     * @param tkn_47
     */
    public void setTkn47(byte[] tkn_47) {
        //this.tkn47 = tkn_47;

        int indice;

        System.arraycopy(tkn_47, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn47 = new byte[auxLenTkn];
            System.arraycopy(tkn_47, this.indice, tkn47, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn47 = new byte[8];
            System.arraycopy(tkn47, indice, auxTkn47, 0, 8);
            indice += 8;
            InitTrans.tkn47.setCedula_ruc(auxTkn47);

            auxTkn47 = new byte[30];
            System.arraycopy(tkn47, indice, auxTkn47, 0, 30);
            String hexpr = ISOUtil.bcd2str(auxTkn47, 0, 30);
            String prueba = ISOUtil.hex2AsciiStr(hexpr);
            indice += 30;
            InitTrans.tkn47.setNombre(auxTkn47);

            auxTkn47 = new byte[7];
            System.arraycopy(tkn47, indice, auxTkn47, 0, 7);
            indice += 7;
            InitTrans.tkn47.setCuenta(auxTkn47);
            this.indice += indice;
        }
    }

    public byte[] getTkn48() {
        return tkn48;
    }

    public void setTkn48(byte[] tkn_48) {
        //this.tkn48 = tkn48;

        int indice;

        System.arraycopy(tkn_48, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn48 = new byte[auxLenTkn];
            System.arraycopy(tkn_48, this.indice, tkn48, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn48 = new byte[5];
            System.arraycopy(tkn48, indice, auxTkn48, 0, 5);
            indice += 5;
            InitTrans.tkn48.setNumero_kit(auxTkn48);

            auxTkn48 = new byte[6];
            System.arraycopy(tkn48, indice, auxTkn48, 0, 6);
            indice += 6;
            InitTrans.tkn48.setNumero_cel(auxTkn48);

            if (auxLenTkn > 11) {
                auxTkn48 = new byte[30];
                System.arraycopy(tkn48, indice, auxTkn48, 0, 30);
                indice += 30;
                InitTrans.tkn48.setNombre(auxTkn48);
            }

            this.indice += indice;
        }
    }

    public byte[] getTkn49() {
        return tkn49;
    }

    public void setTkn49(byte[] tkn_49) {
        int indice;

        System.arraycopy(tkn_49, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn49 = new byte[auxLenTkn];
            System.arraycopy(tkn_49, this.indice, tkn49, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn49 = new byte[8];
            System.arraycopy(tkn49, indice, auxTkn49, 0, 8);
            indice += 8;
            InitTrans.tkn49.setCedula(auxTkn49);

            auxTkn49 = new byte[20];
            System.arraycopy(tkn49, indice, auxTkn49, 0, 20);
            indice += 20;
            InitTrans.tkn49.setApellido1(auxTkn49);

            auxTkn49 = new byte[20];
            System.arraycopy(tkn49, indice, auxTkn49, 0, 20);
            indice += 20;
            InitTrans.tkn49.setApellido2(auxTkn49);

            auxTkn49 = new byte[20];
            System.arraycopy(tkn49, indice, auxTkn49, 0, 20);
            indice += 20;
            InitTrans.tkn49.setNombre1(auxTkn49);

            auxTkn49 = new byte[20];
            System.arraycopy(tkn49, indice, auxTkn49, 0, 20);
            indice += 20;
            InitTrans.tkn49.setNombre2(auxTkn49);

            auxTkn49 = new byte[30];
            System.arraycopy(tkn49, indice, auxTkn49, 0, 30);
            indice += 30;
            InitTrans.tkn49.setNombreCompleto(auxTkn49);
            this.indice += indice;
        }
    }

    public byte[] getTkn60() {
        return tkn60;
    }

    public void setTkn60(byte[] tkn_60) {
        int indice;

        System.arraycopy(tkn_60, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn60 = new byte[auxLenTkn];
            System.arraycopy(tkn_60, this.indice, tkn60, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn60 = new byte[3];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 3);
            indice += 3;
            InitTrans.tkn60.setAAAAMM(auxTkn60);

            auxTkn60 = new byte[2];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 2);
            indice += 2;
            InitTrans.tkn60.setNumTxs(auxTkn60);

            auxTkn60 = new byte[4];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 4);
            indice += 4;
            InitTrans.tkn60.setFechaAcredita(auxTkn60);

            auxTkn60 = new byte[6];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 6);
            indice += 6;
            InitTrans.tkn60.setVlrPago(auxTkn60);

            auxTkn60 = new byte[15];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 15);
            indice += 15;
            InitTrans.tkn60.setEstado(auxTkn60);

            auxTkn60 = new byte[20];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 20);
            indice += 20;
            InitTrans.tkn60.setMotivo(auxTkn60);

            auxTkn60 = new byte[5];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 5);
            indice += 5;
            InitTrans.tkn60.setCuenta(auxTkn60);

            auxTkn60 = new byte[20];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 20);
            indice += 20;
            InitTrans.tkn60.setFactura(auxTkn60);

            auxTkn60 = new byte[4];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 4);
            indice += 4;
            InitTrans.tkn60.setFechaEmision(auxTkn60);

            auxTkn60 = new byte[6];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 6);
            indice += 6;
            InitTrans.tkn60.setBaseImponible(auxTkn60);

            auxTkn60 = new byte[2];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 2);
            indice += 2;
            InitTrans.tkn60.setPorcentIva(auxTkn60);

            auxTkn60 = new byte[4];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 4);
            indice += 4;
            InitTrans.tkn60.setIva(auxTkn60);

            auxTkn60 = new byte[6];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 6);
            indice += 6;
            InitTrans.tkn60.setValorTotal(auxTkn60);

            auxTkn60 = new byte[2];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 2);
            indice += 2;
            InitTrans.tkn60.setPorcentRetencIva(auxTkn60);

            auxTkn60 = new byte[4];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 4);
            indice += 4;
            InitTrans.tkn60.setRetencionIva(auxTkn60);

            auxTkn60 = new byte[2];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 2);
            indice += 2;
            InitTrans.tkn60.setPorcentRetencServicio(auxTkn60);

            auxTkn60 = new byte[4];
            System.arraycopy(tkn60, indice, auxTkn60, 0, 4);
            indice += 4;
            InitTrans.tkn60.setRetencionServicio(auxTkn60);

            this.indice += indice;
        }
    }

    public byte[] getTkn80() {
        return tkn80;
    }

    /**
     *
     * @param tkn_80
     */
    public void setTkn80(byte[] tkn_80) {
        //this.tkn80 = tkn_80;

        int indice;

        System.arraycopy(tkn_80, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn80 = new byte[auxLenTkn];
            System.arraycopy(tkn_80, this.indice, tkn80, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn80 = new byte[2];
            System.arraycopy(tkn80, indice, auxTkn80, 0, 2);
            indice += 2;
            InitTrans.tkn80.setTimeMaxConnect(auxTkn80);

            auxTkn80 = new byte[2];
            System.arraycopy(tkn80, indice, auxTkn80, 0, 2);
            indice += 2;
            InitTrans.tkn80.setTimeMaxWait(auxTkn80);

            auxTkn80 = new byte[1];
            System.arraycopy(tkn80, indice, auxTkn80, 0, 1);
            indice += 1;
            InitTrans.tkn80.setMaxRetiros(auxTkn80);

            auxTkn80 = new byte[2];
            System.arraycopy(tkn80, indice, auxTkn80, 0, 2);
            indice += 2;
            InitTrans.tkn80.setValMinRetiro(auxTkn80);

            auxTkn80 = new byte[2];
            System.arraycopy(tkn80, indice, auxTkn80, 0, 2);
            indice += 2;
            InitTrans.tkn80.setValMaxRetiro(auxTkn80);

            auxTkn80 = new byte[1];
            System.arraycopy(tkn80, indice, auxTkn80, 0, 1);
            indice += 1;
            InitTrans.tkn80.setMaxDepositos(auxTkn80);

            auxTkn80 = new byte[2];
            System.arraycopy(tkn80, indice, auxTkn80, 0, 2);
            indice += 2;
            InitTrans.tkn80.setValMinDeposito(auxTkn80);

            auxTkn80 = new byte[2];
            System.arraycopy(tkn80, indice, auxTkn80, 0, 2);
            indice += 2;
            InitTrans.tkn80.setValMaxDeposito(auxTkn80);

            auxTkn80 = new byte[2];
            System.arraycopy(tkn80, indice, auxTkn80, 0, 2);
            indice += 2;
            InitTrans.tkn80.setCostoServicio(auxTkn80);

            if (auxLenTkn == 17) {
                auxTkn80 = new byte[1];
                System.arraycopy(tkn80, indice, auxTkn80, 0, 1);
                indice += 1;
                InitTrans.tkn80.setTipoNegocio(auxTkn80);
            }
            this.indice += indice;
        }
    }

    public byte[] getTkn81() {
        return tkn81;
    }

    /**
     *
     * @param tkn_81
     */
    public void setTkn81(byte[] tkn_81) {
        //this.tkn81 = tkn_81;

        int indice = 0;

        System.arraycopy(tkn_81, this.indice, lenTkn, 0, 2);
        indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn) + 2;

        if (auxLenTkn != 0) {
            tkn81 = new byte[auxLenTkn];
            System.arraycopy(tkn_81, this.indice, tkn81, 0, auxLenTkn);

            //DateTime
            byte[] auxTkn81 = new byte[7];
            System.arraycopy(tkn81, indice, auxTkn81, 0, 7);
            indice += 7;
            InitTrans.tkn81.setUpdateDateTimePos(auxTkn81);

            //Flag Alt server
            auxTkn81 = new byte[1];
            System.arraycopy(tkn81, indice, auxTkn81, 0, 1);
            indice += 1;
            InitTrans.tkn81.setFlagAltServer(auxTkn81);

            //Len message init
            auxTkn81 = new byte[1];
            System.arraycopy(tkn81, indice, auxTkn81, 0, 1);
            indice += 1;
            InitTrans.tkn81.setLenMsgInit(auxTkn81);
            int lenMsgIni = ISOUtil.bcd2int(InitTrans.tkn81.getLenMsgInit(), 0, 1);

            //Message Init
            auxTkn81 = new byte[lenMsgIni];
            System.arraycopy(tkn81, indice, auxTkn81, 0, lenMsgIni);
            indice += lenMsgIni;

            //omit character
            byte[] tmpTkn81 = new byte[lenMsgIni];
            for (int i = 0; i < lenMsgIni; i++) {
                if (auxTkn81[i] == 64) {//change @ for space
                    tmpTkn81[i] = 27;
                } else {
                    tmpTkn81[i] = auxTkn81[i];
                }
            }

            InitTrans.tkn81.setMsgInit(tmpTkn81);

            this.indice += indice;
        }
    }

    public byte[] getTkn93() {
        return tkn93;
    }

    /**
     *
     * @param tkn_93
     */
    public void setTkn93(byte[] tkn_93) {
        int indice;

        System.arraycopy(tkn_93, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn93 = new byte[auxLenTkn];
            System.arraycopy(tkn_93, this.indice, tkn93, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn93 = new byte[1];
            System.arraycopy(tkn93, indice, auxTkn93, 0, 1);
            indice += 1;
            InitTrans.tkn93.setEstadoMsg(auxTkn93);

            auxTkn93 = new byte[2];
            System.arraycopy(tkn93, indice, auxTkn93, 0, 2);
            indice += 2;
            InitTrans.tkn93.setLenMensaje(auxTkn93);
            this.indice += indice;
        }


    }


    public byte[] getTkn82() {
        return tkn82;
    }
    /**
     *
     * @param tkn_82
     */
    public void setTkn82(byte[] tkn_82, String aRspCode) {

        byte[] auxTkn82;
        int indice = 0;
        String var;
        System.arraycopy(tkn_82, this.indice, lenTkn, 0, 2);
        indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn) + 2;

        if (auxLenTkn != 0) {
            tkn82 = new byte[auxLenTkn];
            System.arraycopy(tkn_82, this.indice, tkn82, 0, auxLenTkn);

            if (aRspCode.equals("00")) {
                auxTkn82 = new byte[8];
                System.arraycopy(tkn82, indice, auxTkn82, 0, 8);
                indice += 8;
                InitTrans.tkn82.setRuc(auxTkn82);

                auxTkn82 = new byte[30];
                System.arraycopy(tkn82, indice, auxTkn82, 0, 30);
                indice += 30;
                InitTrans.tkn82.setNombreEmpresa(auxTkn82);

                auxTkn82 = new byte[8];
                System.arraycopy(tkn82, indice, auxTkn82, 0, 8);
                indice += 8;
                InitTrans.tkn82.setCedula(auxTkn82);

                auxTkn82 = new byte[40];
                System.arraycopy(tkn82, indice, auxTkn82, 0, 40);
                indice += 40;
                InitTrans.tkn82.setNombreUsuario(auxTkn82);

                auxTkn82 = new byte[40];
                System.arraycopy(tkn82, indice, auxTkn82, 0, 40);
                indice += 40;
                InitTrans.tkn82.setRazonSocial(auxTkn82);

                auxTkn82 = new byte[1];
                System.arraycopy(tkn82, indice, auxTkn82, 0, 1);
                indice += 1;
                InitTrans.tkn82.setValidarUltDigCed(auxTkn82);
                var = String.valueOf(ISOUtil.stringToHex(ISOUtil.bcd2str(InitTrans.tkn82.getValidarUltDigCed(), 0, InitTrans.tkn82.getValidarUltDigCed().length)));
                TMConfig.getInstance().setsesionIniciada(var);
            }

            this.indice += indice;
        }
    }

    public byte[] getTkn96() {
        return tkn96;
    }

    /**
     *
     * @param tkn_96
     */
    public void setTkn96(byte[] tkn_96) {
        //this.tkn96 = tkn_96;

        System.arraycopy(tkn_96, indice, lenTkn, 0, 2);
        indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn96 = new byte[auxLenTkn];
            System.arraycopy(tkn_96, indice, tkn96, 0, auxLenTkn);
            this.indice += auxLenTkn;
        }
    }

    public byte[] getTkn97() {
        return tkn97;
    }

    /**
     *
     * @param tkn_97
     */
    public void setTkn97(byte[] tkn_97) {
        System.arraycopy(tkn_97, indice, lenTkn, 0, 2);
        indice += 2;
        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn97 = new byte[auxLenTkn];
            System.arraycopy(tkn_97, indice, tkn97, 0, auxLenTkn);
            this.indice += auxLenTkn;
            InitTrans.workingKey = new byte[16];
            InitTrans.tk_Key = new byte[16];
            System.arraycopy(tkn97, 0, InitTrans.workingKey, 0, 16);
            System.arraycopy(tkn97, 16, InitTrans.tk_Key, 0, 16);
        }

    }

    public byte[] getTkn98() {
        return tkn98;
    }

    /**
     *
     * @param tkn_98
     */
    public void setTkn98(byte[] tkn_98) {
        //this.tkn98 = tkn98;
        int indice;
        byte[] cuentas = new byte[1];

        System.arraycopy(tkn_98, this.indice, lenTkn, 0, 2);
        this.indice += 2;

        auxIdTkn = ISOUtil.bcd2str(lenTkn, 0, 2);
        auxLenTkn = Integer.parseInt(auxIdTkn);

        if (auxLenTkn != 0) {
            tkn98 = new byte[auxLenTkn];
            System.arraycopy(tkn_98, this.indice, tkn98, 0, auxLenTkn);

            indice = 0;
            byte[] auxTkn98 = new byte[1];
            System.arraycopy(tkn98, indice, auxTkn98, 0, 1);
            indice += 1;
            InitTrans.tkn98.setNmrDCuentas(auxTkn98);

            int lenTok = tkn98.length - 1;
            auxTkn98 = new byte[lenTok];
            System.arraycopy(tkn98, indice, auxTkn98, 0, lenTok);
            indice += lenTok;
            InitTrans.tkn98.setCtas(auxTkn98);
            this.indice += indice;
        }
    }

    /**
     *
     * @param StrFld48
     */
    public String getTkn(String StrFld48, String aRspCode){
        String ret = "OK";
        idTkn = new byte[1];
        indice = 0;

        byte[] Fld48 = ISOUtil.str2bcd(StrFld48, false);

        for (int i = 0; i < Fld48.length; i = indice) {
            System.arraycopy(Fld48, indice, idTkn, 0, 1);
            indice += 1;
            auxIdTkn = ISOUtil.bcd2str(idTkn, 0, 1);

            try {
                switch (auxIdTkn) {
                    case "06":
                        setTkn06(Fld48);
                        break;
                    case "07":
                        setTkn07(Fld48);
                        break;
                    case "11":
                        setTkn11(Fld48);
                        break;
                    case "12":
                        setTkn12(Fld48);
                        break;
                    case "13":
                        setTkn13(Fld48);
                        break;
                    case "14":
                        setTkn14(Fld48);
                        break;
                    case "15":
                        setTkn15(Fld48);
                        break;
                    case "16":
                        setTkn16(Fld48);
                        break;
                    case "17":
                        setTkn17(Fld48);
                        break;
                    case "19":
                        setTkn19(Fld48);
                        break;
                    case "20":
                        setTkn20(Fld48);
                        break;
                    case "22":
                        setTkn22(Fld48);
                        break;
                    case "25":
                        setTkn25(Fld48);
                        break;
                    case "26":
                        setTkn26(Fld48);
                        break;
                    case "27":
                        setTkn27(Fld48);
                        break;
                    case "28":
                        setTkn28(Fld48);
                        break;
                    case "33":
                        setTkn33(Fld48);
                        break;
                    case "38":
                        setTkn38(Fld48);
                        break;
                    case "39":
                        setTkn39(Fld48);
                        break;
                    case "40":
                        indice += InitTrans.tkn40.unpackToken(Fld48, indice);
                        break;
                    case "47":
                        setTkn47(Fld48);
                        break;
                    case "48":
                        setTkn48(Fld48);
                        break;
                    case "49":
                        setTkn49(Fld48);
                        break;
                    case "60":
                        setTkn60(Fld48);
                        break;
                    case "80":
                        setTkn80(Fld48);
                        break;
                    case "81":
                        setTkn81(Fld48);
                        break;
                    case "82":
                        setTkn82(Fld48, aRspCode);
                        break;
                    case "93":
                        setTkn93(Fld48);
                        break;
                    case "96":
                        setTkn96(Fld48);
                        break;
                    case "97":
                        setTkn97(Fld48);
                        break;
                    case "98":
                        setTkn98(Fld48);
                        break;
                }

            } catch (Exception e){
                InitTrans.wrlg.wrDataTxt("Error en tokens " +e);
                return "Error en tokens";
            }
        }
        return ret;
    }
}