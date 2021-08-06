package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import cn.desert.newpos.payui.base.PayApplication;
import newpos.libpay.trans.Trans;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

/**
 * Created by Jhon Morantes on 18/4/2018.
 */

public class Tkn43 {

    /**
     *
     * @param inputMode
     * @param trk2
     * @return
     */
    public String packTkn43(int inputMode, String trk2){

        if (inputMode == Trans.ENTRY_MODE_MAG){
            byte[] auxByte = new byte[40];
            Arrays.fill(auxByte, (byte) 0x20);
            byte[] bytes = new byte[20];
            int len = PAYUtils.get_tlv_data_kernal(0x57, bytes);
            String str_trak2_final = trk2.replace("F", " ").trim();
            try {
                bytes = str_trak2_final.getBytes("US-ASCII");
                System.arraycopy(bytes, 0, auxByte, 0, bytes.length);
                auxByte = PayApplication.encryp3DES(auxByte, 40);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String x = ISOUtil.hexString(auxByte);
            String y = ISOUtil.convertStringToHex(x);
            auxByte = ISOUtil.str2bcd(y, false);

            byte[] auxBuff = new byte[]{0x43, 0x00, (byte) 0x80};
            byte[] buff_final = new byte[auxByte.length + auxBuff.length];

            System.arraycopy(auxBuff, 0, buff_final, 0, auxBuff.length);
            int indice = auxBuff.length;

            System.arraycopy(auxByte, 0, buff_final, indice, auxByte.length);

            String auxStr = ISOUtil.byte2hex(buff_final, 0, buff_final.length);
            String Tok43 = ISOUtil.strpad(auxStr, 80).replace(" ", "20");

            return Tok43;

        }else {

            byte[] auxByte = new byte[40];
            Arrays.fill(auxByte, (byte) 0x20);
            byte[] bytes = new byte[20];
            int len = PAYUtils.get_tlv_data_kernal(0x57, bytes);
            String track2_str = ISOUtil.bcd2str(bytes, 0, len);
            String str_trak2_final = track2_str.replace("F", " ").trim();
            try {
                bytes = str_trak2_final.getBytes("US-ASCII");
                System.arraycopy(bytes, 0, auxByte, 0, bytes.length);
                auxByte = PayApplication.encryp3DES(auxByte, 40);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String x = ISOUtil.hexString(auxByte);
            String y = ISOUtil.convertStringToHex(x);
            auxByte = ISOUtil.str2bcd(y, false);

            byte[] auxBuff = new byte[]{0x43, 0x00, (byte) 0x80};
            byte[] buff_final = new byte[auxByte.length + auxBuff.length];

            System.arraycopy(auxBuff, 0, buff_final, 0, auxBuff.length);
            int indice = auxBuff.length;

            System.arraycopy(auxByte, 0, buff_final, indice, auxByte.length);

            String auxStr = ISOUtil.byte2hex(buff_final, 0, buff_final.length);
            String Tok43 = ISOUtil.strpad(auxStr, 80).replace(" ", "20");

            return Tok43;
        }

    }
}
