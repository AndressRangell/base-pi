package cnb.pichincha.wposs.mivecino_pichincha.tokns;

/**
 * Created by Jhon Morantes on 12/4/2018.
 */

public class Tkn81 {

    private byte[] updateDateTimePos = new byte[7];
    private byte[] flagAltServer = new byte[1];
    private byte[] lenMsgInit = new byte[1];
    private byte[] MsgInit = new byte[100];

    public byte[] getUpdateDateTimePos() {
        return updateDateTimePos;
    }

    public void setUpdateDateTimePos(byte[] updateDateTimePos) {
        this.updateDateTimePos = updateDateTimePos;
    }

    public byte[] getFlagAltServer() {
        return flagAltServer;
    }

    public void setFlagAltServer(byte[] flagAltServer) {
        this.flagAltServer = flagAltServer;
    }

    public byte[] getLenMsgInit() {
        return lenMsgInit;
    }

    public void setLenMsgInit(byte[] lenMsgInit) {
        this.lenMsgInit = lenMsgInit;
    }

    public byte[] getMsgInit() {
        return MsgInit;
    }

    public void setMsgInit(byte[] msgInit) {
        MsgInit = msgInit;
    }
}
