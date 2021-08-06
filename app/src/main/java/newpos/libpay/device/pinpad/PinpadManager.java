package newpos.libpay.device.pinpad;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.Tcode;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;
import com.pos.device.icc.SlotType;
import com.pos.device.ped.IccOfflinePinApdu;
import com.pos.device.ped.KeySystem;
import com.pos.device.ped.MACMode;
import com.pos.device.ped.Ped;
import com.pos.device.ped.PedRetCode;
import com.pos.device.ped.PinBlockCallback;
import com.pos.device.ped.PinBlockFormat;
import com.pos.device.ped.RsaPinKey;
import com.secure.api.PadView;

import java.util.Locale;

/**
 * Created by zhouqiang on 2017/3/14.
 * @author zhouqiang
 * 密码键盘管理者
 */

public class PinpadManager {
    private static PinpadManager instance ;

    private PinpadManager(){}

    public static PinpadManager getInstance(){
        if(instance == null){
            instance = new PinpadManager();
        }
        return instance ;
    }

    /**
     * 注入主密钥
     * @param info
     * @return
     */
    public static int loadMKey(MasterKeyinfo info){
        return Ped.getInstance().injectKey(
                PinpadKeytem.getKS(info.getKeySystem()),
                PinpadKeytype.getKT(info.getKeyType()),
                info.getMasterIndex(),
                info.getPlainKeyData());
    }

    /**
     * 注入工作密钥
     * @param info
     * @return
     */
    public static int loadWKey(WorkKeyinfo info){
        return Ped.getInstance().writeKey(
                PinpadKeytem.getKS(info.getKeySystem()),
                PinpadKeytype.getKT(info.getKeyType()),
                info.getMasterKeyIndex(),
                info.getWorkKeyIndex(),
                info.getMode(),
                info.getPrivacyKeyData());
    }

    private PinpadListener listener ;
    private String pinCardNo ;
    private int timeout ;

    /**
     * 获取联机PIN
     * @param t
     * @param c
     * @param l
     */
    public void getPin(int t, String title, String amount, String c, PinpadListener l) {
        this.listener = l;
        this.timeout = t;
        this.pinCardNo = c;
        final PinInfo info = new PinInfo();
        if (null == l) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_invoke_para_err);
            listener.callback(info);
        } else if (pinCardNo == null || pinCardNo.equals("")) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_ped_card_err);
            listener.callback(info);
        } else {

            final Ped ped = Ped.getInstance();
            pinCardNo = pinCardNo.substring(pinCardNo.length() - 13, pinCardNo.length() - 1);
            pinCardNo = ISOUtil.padleft(pinCardNo, pinCardNo.length() + 4, '0');
            PadView padView = new PadView();

            padView.setTitleMsg("Sistema de clave segura");
            padView.setAmountTitle("Confirma monto:");
            padView.setAmount(PAYUtils.TwoWei(amount));
            padView.setPinTips(title);

            ped.setPinPadView(padView);
            new Thread() {
                @Override
                public void run() {
                    ped.getPinBlock(KeySystem.MS_DES,
                            TMConfig.getInstance().getMasterKeyIndex(),
                            PinBlockFormat.PIN_BLOCK_FORMAT_0,
                            "4",
                            pinCardNo,
                            new PinBlockCallback() {
                                @Override
                                public void onPinBlock(int i, byte[] bytes) {
                                    switch (i) {
                                        case PedRetCode.NO_PIN:
                                            info.setResultFlag(true);
                                            info.setNoPin(true);
                                            break;
                                        case PedRetCode.TIMEOUT:
                                            info.setResultFlag(false);
                                            info.setErrno(Tcode.T_wait_timeout);
                                            break;
                                        case PedRetCode.ENTER_CANCEL:
                                            info.setResultFlag(false);
                                            info.setErrno(Tcode.T_user_cancel_pin_err);
                                            break;
                                        case 0:
                                            info.setResultFlag(true);
                                            info.setPinblock(bytes);
                                            break;
                                        default:
                                            info.setResultFlag(false);
                                            info.setErrno(i);
                                            break;
                                    }
                                    listener.callback(info);
                                }
                            });
                }
            }.start();
        }
    }

    public void getOfflinePin(int i , OfflineRSA key , int counts , PinpadListener l){
        this.listener = l ;
        Ped ped = Ped.getInstance() ;
        PadView padView = new PadView();
        final String pinTips ;
        if(Locale.getDefault().getLanguage().equals("zh")){
            padView.setTitleMsg("华智融安全键盘");
            pinTips = "请输入脱机PIN\n" +"剩余 "+ counts+" 次";
        }else {
            padView.setTitleMsg("Newpos Secure Keyboard");
            pinTips = "Please enter offline PIN\n" + "Tiempo restante "+counts +" times";
        }
        final PinInfo info = new PinInfo();
        padView.setPinTips(pinTips);
        ped.setPinPadView(padView);
        IccOfflinePinApdu apdu = new IccOfflinePinApdu();
        if(i == 1){
            RsaPinKey rsaPinKey = new RsaPinKey();
            rsaPinKey.setIccrandom(key.getIccRandom());
            rsaPinKey.setModlen(key.getMod().length);
            rsaPinKey.setMod(key.getMod());
            rsaPinKey.setExplen(key.getExp().length);
            rsaPinKey.setExp(key.getExp());
            apdu.setRsakey( rsaPinKey );
        }
        apdu.setCla(0x00);
        apdu.setIns(0x20);
        apdu.setLe(0x00);
        apdu.setLeflg(0x00);
        apdu.setP1(0x00);
        apdu.setP2(i == 1 ? 0x88:0x80);
        ped.getOfflinePin(i == 1 ? KeySystem.ICC_CIPHER:KeySystem.ICC_PLAIN,
                ped.getIccSlot(SlotType.USER_CARD),
                "0,4,5,6,7,8,9,10,11,12",
                apdu,
                new PinBlockCallback() {
                    @Override
                    public void onPinBlock(int i, byte[] bytes) {
                        if(bytes!=null){
                        }
                        info.setPinblock(bytes);
                        if(i == PedRetCode.NO_PIN){
                            info.setResultFlag(true);
                            info.setNoPin(true);
                        }else if(i == 0){
                            info.setResultFlag(true);
                        }else {
                            info.setResultFlag(false);
                        }
                        listener.callback(info);
                    }
                });
    }

    /**
     * 获取加密后的MAC信息
     * @param data 加密源数据
     * @param offset
     * @param len
     * @return 加密后的MAC信息
     */
    public byte[] getMac(byte[] data, int offset, int len) {
        byte[] macIn ;
        macIn = new byte[((len + 7) >> 3) << 3];
        System.arraycopy(data, offset, macIn, 0, len);
        byte[] macBlock = Ped.getInstance().getMac(KeySystem.MS_DES, TMConfig.getInstance().getMasterKeyIndex(), MACMode.MAC_MODE_CUP_8, macIn);
        return macBlock;
    }

    /**
     * 中信银行算MAC采用CBC方式
     * @param data
     * @param offset
     * @param len
     * @return
     */
    public byte[] getCITICMac(byte[] data, int offset, int len) {
        byte[] macIn ;
        macIn = new byte[((len + 7) >> 3) << 3];
        System.arraycopy(data, offset, macIn, 0, len);
        byte[] macBlock = Ped.getInstance().getMac(KeySystem.MS_DES, TMConfig.getInstance().getMasterKeyIndex(), MACMode.MAC_MODE_CUP, macIn);
        return macBlock;
    }

    /**
     * 获取加密后的磁道信息
     * 磁道加密
     * @param track
     * @return
     */
    public String getEac(int index , String track) {
        int ofs, org_len;
        StringBuffer trackEnc = new StringBuffer(120);
        byte[] bufSrc ;
        byte[] bufDest ;
        if (track == null || track.equals("")) {
            return null;
        }
        org_len = track.length();//37
        if (((org_len % 2) != 0)) {
            if (track.length() < 17) {
                return null;
            }
            ofs = org_len - 17;
        } else {
            if (track.length() < 18) {
                return null;
            }
            ofs = org_len - 18;
        }
        trackEnc.append(track.substring(0, ofs));
        bufSrc = ISOUtil.str2bcd(track.substring(ofs, ofs + 16), false);
        bufDest = Ped.getInstance().encryptAccount(KeySystem.MS_DES, index, Ped.TDEA_MODE_ECB, bufSrc);
        if ( bufDest == null ) {
            return null;
        }
        trackEnc.append(ISOUtil.byte2hex(bufDest));
        trackEnc.append(track.substring(ofs + 16, org_len));
        return trackEnc.toString();
    }
}
