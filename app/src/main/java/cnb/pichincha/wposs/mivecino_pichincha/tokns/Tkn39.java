package cnb.pichincha.wposs.mivecino_pichincha.tokns;

public class Tkn39 {

    private byte[] flagVarFijo = new byte[1];
    private byte[] flagUsoFuturo1 = new byte[1];
    private byte[] flagUsoFuturo2 = new byte[1];

    public void clean(){
        flagVarFijo = new byte[1];
        flagUsoFuturo1 = new byte[1];
        flagUsoFuturo2 = new byte[1];
    }

    public byte[] getFlagVarFijo() {
        return flagVarFijo;
    }

    public void setFlagVarFijo(byte[] flagVarFijo) {
        this.flagVarFijo = flagVarFijo;
    }

    public byte[] getFlagUsoFuturo1() {
        return flagUsoFuturo1;
    }

    public void setFlagUsoFuturo1(byte[] flagUsoFuturo1) {
        this.flagUsoFuturo1 = flagUsoFuturo1;
    }

    public byte[] getFlagUsoFuturo2() {
        return flagUsoFuturo2;
    }

    public void setFlagUsoFuturo2(byte[] flagUsoFuturo2) {
        this.flagUsoFuturo2 = flagUsoFuturo2;
    }
}
