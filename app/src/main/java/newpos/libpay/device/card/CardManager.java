package newpos.libpay.device.card;

import android.os.CountDownTimer;
import android.util.Log;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenuPrincipal;
import cnb.pichincha.wposs.mivecino_pichincha.screens.VerificaPapelImpresion;
import newpos.libpay.trans.Tcode;
import newpos.libpay.utils.ISOUtil;

import com.pos.device.SDKException;
import com.pos.device.icc.ContactCard;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.IccReaderCallback;
import com.pos.device.icc.OperatorMode;
import com.pos.device.icc.SlotType;
import com.pos.device.icc.VCC;
import com.pos.device.led.Led;
import com.pos.device.magcard.MagCardCallback;
import com.pos.device.magcard.MagCardReader;
import com.pos.device.magcard.MagneticCard;
import com.pos.device.magcard.TrackInfo;
import com.pos.device.picc.EmvContactlessCard;
import com.pos.device.picc.MifareClassic;
import com.pos.device.picc.PiccReader;
import com.pos.device.picc.PiccReaderCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhouqiang on 2017/3/14.
 * @author zhouqiang
 * 寻卡管理者
 */

public class CardManager {

    public static final int TYPE_MAG = 1 ;
    public static final int TYPE_ICC = 2 ;
    public static final int TYPE_NFC = 3 ;

    public static final int INMODE_MAG = 0x02;
    public static final int INMODE_IC = 0x08;
    public static final int INMODE_NFC = 0x10;

    private static CardManager instance ;

    private static int mode ;

    private CardManager(){}

    public static CardManager getInstance(int m){
        mode = m ;
        if(null == instance){
            instance = new CardManager();
        }
        return instance ;
    }

    private MagCardReader magCardReader ;
    private IccReader iccReader ;
    private PiccReader piccReader ;

    private void init(){
        if( (mode & INMODE_MAG ) != 0 ){
            magCardReader = MagCardReader.getInstance();
        }
        if( (mode & INMODE_IC) != 0 ){
            iccReader = IccReader.getInstance(SlotType.USER_CARD);
        }
        if( (mode & INMODE_NFC) != 0 ){
            piccReader = PiccReader.getInstance();
        }
        isEnd = false ;
    }

    private void stopMAG(){
        try {
            if(magCardReader!=null){
                magCardReader.stopSearchCard();
            }
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }

    private void stopICC(){
        if(iccReader!=null){
            try {
                iccReader.stopSearchCard();
            } catch (SDKException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopPICC(){
        if(piccReader!=null){
            piccReader.stopSearchCard();
            try {
                piccReader.release();
            } catch (SDKException e) {
                e.printStackTrace();
            }
        }
    }

    public void releaseAll(){
        isEnd = true ;
        try {
            if(magCardReader!=null){
                magCardReader.stopSearchCard();
            }
            if(iccReader!=null){
                iccReader.stopSearchCard();
                iccReader.release();
            }
            if(piccReader!=null){
                piccReader.stopSearchCard();
                piccReader.release();
            }
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }

    private CardListener listener ;

    private boolean isEnd = false ;

    public void getCard(final int timeout , CardListener l){

        init();
        final CardInfo info = new CardInfo() ;
        if(null == l){
            info.setResultFalg(false);
            info.setErrno(Tcode.T_invoke_para_err);
            listener.callback(info);
        }else {
            this.listener = l ;
            new Thread(){
                @Override
                public void run(){
                    try{
                        if( (mode & INMODE_MAG) != 0 ){

                            magCardReader.startSearchCard(timeout, new MagCardCallback() {
                                @Override
                                public void onSearchResult(int i, MagneticCard magneticCard) {
                                    if(!isEnd){
                                        isEnd = true ;
                                        stopICC();
                                        stopPICC();
                                        if( 0 == i ){
                                            listener.callback(handleMAG(magneticCard));
                                        }else {
                                            info.setResultFalg(false);
                                            info.setErrno(Tcode.T_search_card_err);
                                            listener.callback(info);
                                        }
                                    }
                                }
                            });
                        }if( (mode & INMODE_IC) != 0 ){
                            iccReader.startSearchCard(timeout, new IccReaderCallback() {
                                @Override
                                public void onSearchResult(int i) {
                                    if(!isEnd){
                                        isEnd = true ;
                                        stopMAG();
                                        stopPICC();
                                        if( 0 == i ){
                                            try {
                                                listener.callback(handleICC());
                                            } catch (SDKException e) {
                                                info.setResultFalg(false);
                                                info.setErrno(Tcode.T_sdk_err);
                                                listener.callback(info);
                                            }
                                        }else {
                                            info.setResultFalg(false);
                                            info.setErrno(Tcode.T_search_card_err);
                                            listener.callback(info);
                                        }
                                    }
                                }
                            });
                        }if( (mode & INMODE_NFC) != 0 ){

                            piccReader.startSearchCard(timeout, new PiccReaderCallback() {
                                @Override
                                public void onSearchResult(int i, int i1) {
                                    try {
                                        Thread.sleep(400);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if(!isEnd){

                                        isEnd = true ;
                                        stopICC();
                                        stopMAG();
                                        if( 0 == i ){
                                            listener.callback(handlePICC(i1));
                                        }else {
                                            info.setResultFalg(false);
                                            info.setErrno(Tcode.T_search_card_err);
                                            listener.callback(info);
                                        }
                                    }
                                }
                            });
                        }
                    }catch (SDKException sdk){

                        releaseAll();
                        info.setResultFalg(false);
                        info.setErrno(Tcode.T_sdk_err);
                        listener.callback(info);
                    }
                }
            }.start();
        }
    }

    private CardInfo handleMAG(MagneticCard card){
        CardInfo info = new CardInfo() ;
        info.setResultFalg(true);
        info.setCardType(CardManager.TYPE_MAG);
        TrackInfo ti_1 = card.getTrackInfos(MagneticCard.TRACK_1);
        TrackInfo ti_2 = card.getTrackInfos(MagneticCard.TRACK_2);
        TrackInfo ti_3 = card.getTrackInfos(MagneticCard.TRACK_3);
        info.setTrackNo(new String[]{ti_1.getData() , ti_2.getData() , ti_3.getData()});
        return info ;
    }

    private CardInfo handleICC() throws SDKException {
        CardInfo info = new CardInfo();
        info.setCardType(CardManager.TYPE_ICC);
        if (iccReader.isCardPresent()) {
            ContactCard contactCard = iccReader.connectCard(VCC.VOLT_5 , OperatorMode.EMV_MODE);
            byte[] atr = contactCard.getATR() ;
            if (atr.length != 0) {
                info.setResultFalg(true);
                info.setCardAtr(atr);
            } else {
                info.setResultFalg(false);
                info.setErrno(Tcode.T_ic_power_err);
            }
        } else {
            info.setResultFalg(false);
            info.setErrno(Tcode.T_ic_not_exist_err);
        }
        return info;
    }

    private CardInfo handlePICC(int nfcType){
        CardInfo info = new CardInfo();
        info.setResultFalg(true);
        info.setCardType(CardManager.TYPE_NFC);
        info.setNfcType(nfcType);

        if (nfcType ==PiccReader.MIFARE_ONE_S50||nfcType ==PiccReader.MIFARE_ONE_S70
                ||nfcType==PiccReader.UNKNOWN_TYPEA){
            try {
                Log.d("tag","start connect");
                MifareClassic classic = MifareClassic.connect();
                if (classic !=null){
                    byte[] uid = classic.getUID();
                    info.setNfcUid(ISOUtil.byte2hex(uid));
                }
            } catch (SDKException e) {
                e.printStackTrace();
            }
        }else {
            EmvContactlessCard emvContactlessCard = null;
            byte[] uid = null;
            byte[] CardInfo = null;

            try {
                emvContactlessCard = EmvContactlessCard.connect();
                if (emvContactlessCard != null){
                    uid = emvContactlessCard.getUID();
                    CardInfo = emvContactlessCard.getInfo();
                    if (uid!=null) {
                        info.setNfcUid(ISOUtil.byte2hex(uid));
                    }
                    else if (nfcType==PiccReader.TYPEB_TCL ){
                        uid = emvContactlessCard.getATQB();
                        String str = ISOUtil.byte2hex(uid);
                        info.setNfcUid(str+"(ATQB)");
                    }
                    if (CardInfo !=null) {
                        info.setNfcCardInfo(ISOUtil.byte2hex(CardInfo));
                    }
                }
            } catch (SDKException e) {
                e.printStackTrace();
            }
        }
        stopPICC();
        return info ;
    }


}
