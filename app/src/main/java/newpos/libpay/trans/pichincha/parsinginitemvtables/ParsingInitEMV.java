package newpos.libpay.trans.pichincha.parsinginitemvtables;

import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;

/**
 * Created by Jhon Morantes on 11/4/2018.
 */

public class ParsingInitEMV {

    private boolean ret = false;
    private byte[] Fld59;
    private int lenFld59;

    private TableEMVConf tableEMVConf;
    private TableEMVKey tableEMVKey;
    private int indice;
    private byte[] auxTagEmv;


    public ParsingInitEMV(String fld59) {
        this.indice = 0;
        this.Fld59 = new byte[999];
        this.Fld59 = ISOUtil.str2bcd(fld59, false);
        this.lenFld59 = Fld59.length;
    }

    public boolean parsingFld59(){

        for (int i = 0; i < lenFld59; i = indice) {

            tableEMVConf = new TableEMVConf();
            tableEMVKey = new TableEMVKey();
            auxTagEmv = new byte[1];

            System.arraycopy(Fld59, indice, auxTagEmv, 0, 1);
            tableEMVConf.setIdTable(auxTagEmv);

            indice += 1;
            String auxIdTable = ISOUtil.bcd2str(auxTagEmv, 0, 1);

            if (auxIdTable.equals("01")) {

                auxTagEmv = new byte[]{0x00, (byte) 0x8C};
                tableEMVConf.setLenTable(auxTagEmv);
                indice += 2;

                auxTagEmv = new byte[1];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 1);

                tableEMVConf.setIdRegister(auxTagEmv);
                indice += 1;

                auxTagEmv = new byte[2];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 2);
                tableEMVConf.setThreshold(auxTagEmv);
                indice += 2;

                auxTagEmv = new byte[1];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 1);
                tableEMVConf.setRandoSelect(auxTagEmv);
                indice += 1;

                auxTagEmv = new byte[1];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 1);
                tableEMVConf.setMaxRandSelect(auxTagEmv);
                indice += 1;

                auxTagEmv = new byte[5];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 5);
                tableEMVConf.setTacDenial(auxTagEmv);
                indice += 5;

                auxTagEmv = new byte[5];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 5);
                tableEMVConf.setTacOnline(auxTagEmv);
                indice += 5;

                auxTagEmv = new byte[5];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 5);
                tableEMVConf.setTacDefault(auxTagEmv);
                indice += 5;

                auxTagEmv = new byte[1];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 1);
                tableEMVConf.setLenAid(auxTagEmv);
                indice += 1;

                auxTagEmv = new byte[16];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 16);
                tableEMVConf.setAid(auxTagEmv);
                indice += 16;

                auxTagEmv = new byte[3];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 3);
                tableEMVConf.setT_9F33(auxTagEmv);
                indice += 3;

                auxTagEmv = new byte[5];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 5);
                tableEMVConf.setT_9F40(auxTagEmv);
                indice += 5;

                auxTagEmv = new byte[1];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 1);
                tableEMVConf.setT_9F35(auxTagEmv);
                indice += 1;

                auxTagEmv = new byte[2];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 2);
                tableEMVConf.setT_9F09(auxTagEmv);
                indice += 2;

                auxTagEmv = new byte[2];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 2);
                tableEMVConf.setT_9F1A(auxTagEmv);
                indice += 2;

                auxTagEmv = new byte[2];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 2);
                tableEMVConf.setT_5F2A(auxTagEmv);
                indice += 2;

                auxTagEmv = new byte[1];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 1);
                tableEMVConf.setT_5F36(auxTagEmv);
                indice += 1;

                auxTagEmv = new byte[4];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 4);
                tableEMVConf.setT_9F1B(auxTagEmv);
                indice += 4;

                auxTagEmv = new byte[6];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 6);
                tableEMVConf.setT_9F01(auxTagEmv);
                indice += 6;

                auxTagEmv = new byte[2];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 2);
                tableEMVConf.setT_9F15(auxTagEmv);
                indice += 2;

                auxTagEmv = new byte[15];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 15);
                tableEMVConf.setT_9F16(auxTagEmv);
                indice += 15;

                auxTagEmv = new byte[8];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 8);
                tableEMVConf.setT_9F1C(auxTagEmv);
                indice += 8;

                auxTagEmv = new byte[1];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 1);
                tableEMVConf.setLenDdol(auxTagEmv);
                indice += 1;

                auxTagEmv = new byte[25];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 25);
                tableEMVConf.setDdol(auxTagEmv);
                indice += 25;

                auxTagEmv = new byte[1];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 1);
                tableEMVConf.setLenTdol(auxTagEmv);
                indice += 1;

                auxTagEmv = new byte[25];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 25);
                tableEMVConf.setTdol(auxTagEmv);
                indice += 25;

                InitTrans.arrayListBuffer_TableEMVConf.add(tableEMVConf);

            }//END ID TABLE
            else if (auxIdTable.equals("02")) {
                auxTagEmv = new byte[2];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 2);
                String auxtag = ISOUtil.bcd2str(auxTagEmv, 0, 1);
                tableEMVKey.setLenTable(auxTagEmv);
                indice += 2;

                auxTagEmv = new byte[1];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 1);
                auxtag = ISOUtil.bcd2str(auxTagEmv, 0, 1);
                tableEMVKey.setIdRegister(auxTagEmv);
                indice += 1;

                auxTagEmv = new byte[1];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 1);
                auxtag = ISOUtil.bcd2str(auxTagEmv, 0, 1);
                tableEMVKey.setPubKeyId(auxTagEmv);
                indice += 1;

                auxTagEmv = new byte[2];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 2);
                auxtag = ISOUtil.bcd2str(auxTagEmv, 0, 2);
                tableEMVKey.setKeyLen(auxTagEmv);
                indice += 2;

                auxTagEmv = new byte[Integer.parseInt(auxtag)];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, auxTagEmv.length);
                tableEMVKey.setPublicKey(auxTagEmv);
                indice += 256;

                auxTagEmv = new byte[5];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 5);
                auxtag = ISOUtil.bcd2str(auxTagEmv, 0, 1);
                tableEMVKey.setPubKeyRid(auxTagEmv);
                indice += 5;

                auxTagEmv = new byte[1];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 1);
                auxtag = ISOUtil.bcd2str(auxTagEmv, 0, 1);
                tableEMVKey.setPubKeyExp(auxTagEmv);
                indice += 1;

                auxTagEmv = new byte[20];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 20);
                auxtag = ISOUtil.bcd2str(auxTagEmv, 0, 20);
                tableEMVKey.setPubKeyHas(auxTagEmv);
                indice += 20;

                auxTagEmv = new byte[2];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 2);
                auxtag = ISOUtil.bcd2str(auxTagEmv, 0, 1);
                tableEMVKey.setEffectDate(auxTagEmv);
                indice += 2;

                auxTagEmv = new byte[3];
                System.arraycopy(Fld59, indice, auxTagEmv, 0, 2);
                auxtag = ISOUtil.bcd2str(auxTagEmv, 0, 1);
                auxTagEmv[2]=0x31;
                tableEMVKey.setExpiryDate(auxTagEmv);
                indice += 2;

                InitTrans.arrayListBuffer_TableEMVKey.add(tableEMVKey);
            }
        }
        return ret;
    }
}
