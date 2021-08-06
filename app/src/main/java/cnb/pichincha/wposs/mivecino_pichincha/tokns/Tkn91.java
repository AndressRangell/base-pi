package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;

public class Tkn91 {

    private byte[] updateDateTimePos = new byte[14];
    private byte[] flagAltServer = new byte[14];
    private byte[] lenMsgInit = new byte[]{0x00,0x00,0x00,0x00,0x00,0x00};

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

    /**
     *
     * @return
     */
    public String packTkn91Diario(){
        String tkn91 = "910035";
        tkn91 += ISOUtil.byte2hex(updateDateTimePos);
        tkn91 += ISOUtil.byte2hex(flagAltServer);
        tkn91 += ISOUtil.bcd2str(lenMsgInit,0,lenMsgInit.length);
        byte[] tipo = InitTrans.tkn80.getTipoNegocio();
        tkn91 += ISOUtil.bcd2str(tipo,0, tipo.length);
        return tkn91;
    }

    /**
     *
     * @return
     */
    public String packTkn91Venta(){
        String tkn91 = "910034";
        tkn91 += ISOUtil.byte2hex(updateDateTimePos);
        tkn91 += ISOUtil.byte2hex(flagAltServer);
        tkn91 += ISOUtil.bcd2str(lenMsgInit,0,lenMsgInit.length);
        return tkn91;
    }
}
