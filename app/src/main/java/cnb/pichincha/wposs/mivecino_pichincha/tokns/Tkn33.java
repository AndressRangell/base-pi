package cnb.pichincha.wposs.mivecino_pichincha.tokns;

public class Tkn33 {

    private byte[] cantTrans = new byte[]{0x00};
    private byte[] transBatch = new byte[999];

    public void clean(){
        cantTrans = new byte[]{0x00};
        transBatch = new byte[999];
    }

    public byte[] getCantTrans() {
        return cantTrans;
    }

    public void setCantTrans(byte[] cantTrans) {
        this.cantTrans = cantTrans;
    }

    public byte[] getTransBatch() {
        return transBatch;
    }

    public void setTransBatch(byte[] transBatch) {
        this.transBatch = transBatch;
    }
}
