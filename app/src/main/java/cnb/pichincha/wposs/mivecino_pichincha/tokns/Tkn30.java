package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import newpos.libpay.utils.ISOUtil;

public class Tkn30 {

    private byte[] gNewUser = new byte[15];

    public void clean(){
        gNewUser = new byte[15];
    }

    public byte[] getgNewUser() {
        return gNewUser;
    }

    public void setgNewUser(byte[] gNewUser) {
        this.gNewUser = gNewUser;
    }

    /**
     *
     * @return
     */
    public String packTkn30() {
        String tkn30 = "";
        tkn30 = "300090"; //30-->idToken 0090-->longitud del token(180 caracteres)
        tkn30 += user();
        tkn30 += "00000000000000002020202020202020202020202020202020202020202020202020202020202020" +
                "2020202020202020202020202020202020200000000000000000";
        tkn30 += fecha();
        tkn30 += "0304";
        return tkn30;
    }

    /**
     *
     * @return
     */
    public String fecha() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    /**
     *
     * @return
     */
    public String user() {
        String user = ISOUtil.bcd2str(getgNewUser(),0,getgNewUser().length);
        user = ISOUtil.padright(user,15,' ');
        user = ISOUtil.convertStringToHex(user);
        return  user;
    }

}
