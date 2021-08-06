package newpos.libpay.trans.pichincha.parsinginitemvtables;

/**
 * Created by Jhon Morantes on 11/4/2018.
 */

public class TableEMVConf {

    byte[] idTable = new byte[1];
    byte[] lenTable = new byte[1];

    byte[] idRegister = new byte[1];
    byte[] threshold = new byte[2];
    byte[] randoSelect = new byte[1];
    byte[] maxRandSelect = new byte[1];
    byte[] tacDenial = new byte[5];
    byte[] tacOnline = new byte[5];
    byte[] tacDefault = new byte[5];
    byte[] lenAid = new byte[1];
    byte[] aid = new byte[16];
    // Terminal Capabilities
    byte[] t_9F33 = new byte[3];
    // Additional Terminal Capabilities
    byte[] t_9F40 = new byte[5];
    // Terminal Type
    byte[] t_9F35 = new byte[1];
    // Application Version Number
    byte[] t_9F09 = new byte[2];
    // Terminal Country Code
    byte[] t_9F1A = new byte[2];
    // Transaction Currency Code
    byte[] t_5F2A = new byte[2];
    // Transaction Currency Exponent
    byte[] t_5F36 = new byte[1];
    // Terminal Floor Limit
    byte[] t_9F1B = new byte[4];
    // Acquirer Identifier
    byte[] t_9F01 = new byte[6];
    // Merchant Category Code
    byte[] t_9F15 = new byte[2];
    // Merchant Identifier
    byte[] t_9F16 = new byte[15];
    // Terminal Identification
    byte[] t_9F1C = new byte[8];

    byte[] lenDdol = new byte[1];
    // Tag 9F49
    byte[] ddol = new byte[25];
    byte[] lenTdol = new byte[1];
    // Tag 97
    byte[] tdol = new byte[25];

    public byte[] getIdTable() {
        return idTable;
    }

    public void setIdTable(byte[] idTable) {
        this.idTable = idTable;
    }

    public byte[] getLenTable() {
        return lenTable;
    }

    public void setLenTable(byte[] lenTable) {
        this.lenTable = lenTable;
    }

    public byte[] getIdRegister() {
        return idRegister;
    }

    public void setIdRegister(byte[] idRegister) {
        this.idRegister = idRegister;
    }

    public byte[] getThreshold() {
        return threshold;
    }

    public void setThreshold(byte[] threshold) {
        this.threshold = threshold;
    }

    public byte[] getRandoSelect() {
        return randoSelect;
    }

    public void setRandoSelect(byte[] randoSelect) {
        this.randoSelect = randoSelect;
    }

    public byte[] getMaxRandSelect() {
        return maxRandSelect;
    }

    public void setMaxRandSelect(byte[] maxRandSelect) {
        this.maxRandSelect = maxRandSelect;
    }

    public byte[] getTacDenial() {
        return tacDenial;
    }

    public void setTacDenial(byte[] tacDenial) {
        this.tacDenial = tacDenial;
    }

    public byte[] getTacOnline() {
        return tacOnline;
    }

    public void setTacOnline(byte[] tacOnline) {
        this.tacOnline = tacOnline;
    }

    public byte[] getTacDefault() {
        return tacDefault;
    }

    public void setTacDefault(byte[] tacDefault) {
        this.tacDefault = tacDefault;
    }

    public byte[] getLenAid() {
        return lenAid;
    }

    public void setLenAid(byte[] lenAid) {
        this.lenAid = lenAid;
    }

    public byte[] getAid() {
        return aid;
    }

    public void setAid(byte[] aid) {
        this.aid = aid;
    }

    public byte[] getT_9F33() {
        return t_9F33;
    }

    public void setT_9F33(byte[] t_9F33) {
        this.t_9F33 = t_9F33;
    }

    public byte[] getT_9F40() {
        return t_9F40;
    }

    public void setT_9F40(byte[] t_9F40) {
        this.t_9F40 = t_9F40;
    }

    public byte[] getT_9F35() {
        return t_9F35;
    }

    public void setT_9F35(byte[] t_9F35) {
        this.t_9F35 = t_9F35;
    }

    public byte[] getT_9F09() {
        return t_9F09;
    }

    public void setT_9F09(byte[] t_9F09) {
        this.t_9F09 = t_9F09;
    }

    public byte[] getT_9F1A() {
        return t_9F1A;
    }

    public void setT_9F1A(byte[] t_9F1A) {
        this.t_9F1A = t_9F1A;
    }

    public byte[] getT_5F2A() {
        return t_5F2A;
    }

    public void setT_5F2A(byte[] t_5F2A) {
        this.t_5F2A = t_5F2A;
    }

    public byte[] getT_5F36() {
        return t_5F36;
    }

    public void setT_5F36(byte[] t_5F36) {
        this.t_5F36 = t_5F36;
    }

    public byte[] getT_9F1B() {
        return t_9F1B;
    }

    public void setT_9F1B(byte[] t_9F1B) {
        this.t_9F1B = t_9F1B;
    }

    public byte[] getT_9F01() {
        return t_9F01;
    }

    public void setT_9F01(byte[] t_9F01) {
        this.t_9F01 = t_9F01;
    }

    public byte[] getT_9F15() {
        return t_9F15;
    }

    public void setT_9F15(byte[] t_9F15) {
        this.t_9F15 = t_9F15;
    }

    public byte[] getT_9F16() {
        return t_9F16;
    }

    public void setT_9F16(byte[] t_9F16) {
        this.t_9F16 = t_9F16;
    }

    public byte[] getT_9F1C() {
        return t_9F1C;
    }

    public void setT_9F1C(byte[] t_9F1C) {
        this.t_9F1C = t_9F1C;
    }

    public byte[] getLenDdol() {
        return lenDdol;
    }

    public void setLenDdol(byte[] lenDdol) {
        this.lenDdol = lenDdol;
    }

    public byte[] getDdol() {
        return ddol;
    }

    public void setDdol(byte[] ddol) {
        this.ddol = ddol;
    }

    public byte[] getLenTdol() {
        return lenTdol;
    }

    public void setLenTdol(byte[] lenTdol) {
        this.lenTdol = lenTdol;
    }

    public byte[] getTdol() {
        return tdol;
    }

    public void setTdol(byte[] tdol) {
        this.tdol = tdol;
    }
}
