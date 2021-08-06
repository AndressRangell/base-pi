package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

/**
 * Created by Jhon Morantes on 13/4/2018.
 */

public class Tkn09 {

    private byte[]  sb_typeAccount = new byte[1];

    public void cleanTkn09(){
        sb_typeAccount = new byte[1];
    }

    public byte[] getSb_typeAccount() {
        return sb_typeAccount;
    }

    public void setSb_typeAccount(byte[] sb_typeAccount) {
        this.sb_typeAccount = sb_typeAccount;
    }

    /**
     *
     * @return
     */
    public String packTkn09(){
        String tkn09 = "";
        tkn09 = "090001";//IdFld,sizeFld
        tkn09 += ISOUtil.bcd2str(getSb_typeAccount(), 0, getSb_typeAccount().length);
        return tkn09;
    }
}
