package newpos.libpay.presenter;

import android.support.v7.app.AppCompatActivity;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import cn.desert.newpos.payui.master.MasterControl;
import newpos.libpay.PaySdk;
import newpos.libpay.PaySdkException;
import newpos.libpay.device.card.CardInfo;
import newpos.libpay.device.card.CardListener;
import newpos.libpay.device.card.CardManager;
import newpos.libpay.device.pinpad.OfflineRSA;
import newpos.libpay.device.pinpad.PinInfo;
import newpos.libpay.device.pinpad.PinpadListener;
import newpos.libpay.device.pinpad.PinpadManager;
import newpos.libpay.device.user.OnUserResultListener;
import newpos.libpay.global.TMConstants;
import newpos.libpay.trans.Tcode;
import newpos.libpay.utils.PAYUtils;

/**
 * Created by zhouqiang on 2017/4/25.
 * @author zhouqiang
 * 交易UI接口实现类
 * MVP架构中的P层 ，处理复杂的逻辑及数据
 */

public class TransUIImpl implements TransUI {

    private TransView transView = null ;
    private AppCompatActivity mActivity = null ;

    public TransUIImpl(AppCompatActivity activity , TransView tv){
        this.transView = tv ;
        this.mActivity = activity ;
    }

    private CountDownLatch mLatch ;
    private int mRet = 0 ;
    private InputManager.Style payStyle ;

    final OnUserResultListener listener = new OnUserResultListener() {
        @Override
        public void confirm(InputManager.Style style) {
            mRet = 0 ;
            payStyle = style ;
            mLatch.countDown();
        }

        @Override
        public void cancel() {
            mRet = 1 ;
            mLatch.countDown();
        }
    };

    @Override
    public PinInfo getPinpadOfflinePin(int timeout , int i , OfflineRSA key , int counts) {
        this.mLatch = new CountDownLatch(1);
        final PinInfo pinInfo = new PinInfo();
        PinpadManager pinpadManager = PinpadManager.getInstance();
        pinpadManager.getOfflinePin(i,key,counts,new PinpadListener() {
            @Override
            public void callback(PinInfo info) {
                pinInfo.setResultFlag(info.isResultFlag());
                pinInfo.setErrno(info.getErrno());
                pinInfo.setNoPin(info.isNoPin());
                pinInfo.setPinblock(info.getPinblock());
                mLatch.countDown();
            }
        });
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        transView.showMsgInfo("",timeout , getStatusInfo(String.valueOf(Tcode.Status.handling)));
        return pinInfo ;
    }

    @Override
    public CardInfo getCardUse(int timeout, int mode, int tipo) {
        MasterControl.onBackCard = true;
        transView.showCardView(timeout , mode, tipo, listener);
        this.mLatch = new CountDownLatch(1);
        final CardInfo cInfo = new CardInfo() ;
        CardManager cardManager = CardManager.getInstance(mode);
        cardManager.getCard(timeout, new CardListener() {
            @Override
            public void callback(CardInfo cardInfo) {
                cInfo.setResultFalg(cardInfo.isResultFalg());
                cInfo.setNfcType(cardInfo.getNfcType());
                cInfo.setCardType(cardInfo.getCardType());
                cInfo.setTrackNo(cardInfo.getTrackNo());
                cInfo.setCardAtr(cardInfo.getCardAtr());
                cInfo.setErrno(cardInfo.getErrno());
                mLatch.countDown();
            }
        });
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MasterControl.onBackCard = false;
        return cInfo;
    }

    @Override
    public CardInfo getCedulaNfc(int timeout, int mode) {
        MasterControl.onBackCard = true;
        transView.showCedulaNfcView(timeout , mode, listener);
        this.mLatch = new CountDownLatch(1);
        final CardInfo cInfo = new CardInfo() ;
        CardManager cardManager = CardManager.getInstance(mode);
        cardManager.getCard(timeout, new CardListener() {
            @Override
            public void callback(CardInfo cardInfo) {
                cInfo.setResultFalg(cardInfo.isResultFalg());
                cInfo.setNfcType(cardInfo.getNfcType());
                cInfo.setNfcUid(cardInfo.getNfcUid());
                cInfo.setNfcCardInfo(cardInfo.getNfcCardInfo());
                cInfo.setCardType(cardInfo.getCardType());
                cInfo.setTrackNo(cardInfo.getTrackNo());
                cInfo.setCardAtr(cardInfo.getCardAtr());
                cInfo.setErrno(cardInfo.getErrno());
                mLatch.countDown();
            }
        });
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MasterControl.onBackCard = false;
        return cInfo;
    }

    @Override
    public PinInfo getPinpadOnlinePin(int timeout, String amount, String cardNo, String title) {
        this.mLatch = new CountDownLatch(1);
        final PinInfo pinInfo = new PinInfo();
        PinpadManager pinpadManager = PinpadManager.getInstance();
        pinpadManager.getPin(timeout,title,amount,cardNo, new PinpadListener() {
            @Override
            public void callback(PinInfo info) {
                pinInfo.setResultFlag(info.isResultFlag());
                pinInfo.setErrno(info.getErrno());
                pinInfo.setNoPin(info.isNoPin());
                pinInfo.setPinblock(info.getPinblock());
                mLatch.countDown();
            }
        });
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        transView.showMsgInfo("", timeout , getStatusInfo(String.valueOf(Tcode.Status.handling)));
        return pinInfo ;
    }

    @Override
    public int showMsgConfirm(int timeout, final String title, final String text) {
        this.mLatch = new CountDownLatch(1);
        transView.showMsgConfirmView(timeout, title,text, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mRet;
    }

    @Override
    public int showCardApplist(int timeout, String[] list) {
        this.mLatch = new CountDownLatch(1) ;
        transView.showCardAppListView(timeout, list, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mRet;
    }



    @Override
    public void handling(String titulo, int timeout, String status) {
        transView.showMsgInfo(titulo, timeout , status);
    }

    @Override
    public void newHandling(String titulo, int timeout, int status) {
        transView.showMsgInfo(titulo, timeout , status);
    }

    @Override
    public void imprimiendo(int timeout, int status) {
        transView.showImprimiendo(timeout , status);
    }

    @Override
    public void almacenarRecibo(boolean secuencial) {
        transView.showAlmacenarRecibo(secuencial);
    }

    @Override
    public void actualizarEstadoCierre() {
        transView.actualizarEstadoCierre();
    }

    @Override
    public void trannSuccess(int timeout , int code , String... args) {
        String info = getStatusInfo(String.valueOf(code)) ;
        if(args.length != 0){
            info += "\n"+args[0] ;
        }
        transView.showSuccess(timeout , info);
    }

    @Override
    public void showSuccess(int timeout , int code , String args) {
        transView.showSuccessView(timeout , code, args);
    }

    @Override
    public void showError(int timeout , int errcode) {
        transView.showError(timeout , getErrInfo(String.valueOf(errcode)));
    }

    @Override
    public void showMsgError(int timeout , String MsgError) {
        transView.showMsgError(timeout , MsgError);
    }

    @Override
    public void showView(final Class<?> cls) {
        transView.showView(cls);
    }

    @Override
    public InputInfo showAmountCnb(int timeout, final String title) {
        transView.showAmountCnbView(timeout, title, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showPrincipales(int timeout, String title) {
        transView.showPrincipalesView(timeout, title, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showAdministrativas(int timeout, String title) {
        transView.showAdministrativasView(timeout, title, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showVisita(int timeout, String title) {
        transView.showVisitaView(timeout, title, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showRecaudaciones(int timeout, String title) {
        transView.showRecaudacionesView(timeout, title, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showList(int timeout, String title, String[] menu) {
        transView.showListView(timeout, title, menu,listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    /** ============================================= */

    @Override
    public InputInfo showConsultas(int timeout, String title,long costo) {
        transView.showConsultasView(timeout, title,costo, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    public InputInfo show_input_text_date(int timeout, final String title, String mensaje){
        transView.show_input_text_date_view(timeout, title, mensaje, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    public InputInfo showContrapartida(int timeout, String mensaje, int maxLeng, int inputType, int carcRequeridos, String titulo) {
        transView.showContrapartidaView(timeout,mensaje,maxLeng,inputType, carcRequeridos, titulo, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    public InputInfo showContrapartidaRecaud(int timeout, String mensaje, int maxLeng, int inputType, int carcRequeridos, String titulo) {
        transView.showContrapartidaRecaudView(timeout,mensaje,maxLeng,inputType, carcRequeridos, titulo, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    public InputInfo showIngreso2EditText(String[] mensajes, int[] maxLengs, int[] inputType, int[] carcRequeridos, String titulo) {
        transView.showIngreso2EditTextView(mensajes,maxLengs,inputType, carcRequeridos, titulo, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    public InputInfo show1Fecha1EditText(String[] mensaje, int maxLeng, int inputType, int carcRequeridos, String titulo) {
        transView.show1Fecha1EditTextView(mensaje,maxLeng,inputType, carcRequeridos, titulo, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showEmpresasPrediosView(int timeout, String[] mensaje, int[] maxLeng, int inputType, int carcRequeridos) {
        transView.showEmpresasPrediosView(timeout,mensaje,maxLeng,inputType, carcRequeridos, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showBonoDesarrollo(int timeout, String titulo, String[] mensaje, int[] maxLeng, int inputType, int carcRequeridos) {
        transView.showBonoDesarrollo(timeout, titulo, mensaje,maxLeng,inputType, carcRequeridos, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    public InputInfo showIngresoDocumento(int timeout) {
        transView.showIngresoDocumentoView(timeout, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setErrno(Integer.parseInt(info.getResult()));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    public InputInfo showCuentaYMontoDeposito(int timeout) {
        transView.showCuentaYMontoDepositoView(timeout, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    public InputInfo showFechaHora(int timeout, String titulo, boolean flag) {
        transView.showFechaHoraView(timeout, titulo, flag, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showFecha(int timeout, String titulo) {
        transView.showFechaView(timeout, titulo, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    public InputInfo showBotones(int cantBtn, String[] btnTitulo, String title) {
        transView.showBotonesView(cantBtn,btnTitulo,title, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setErrno(Integer.parseInt(info.getResult()));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    public InputInfo showListCierre(int timeout, String title) {
        transView.showListCierreView(timeout, title, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }


    /** ============================================= */

    private String getStatusInfo(String status){
        try {
            String[] infos = Locale.getDefault().getLanguage().equals("zh")?
                    PAYUtils.getProps(PaySdk.getInstance().getContext(), TMConstants.STATUS, status):
                    PAYUtils.getProps(PaySdk.getInstance().getContext(), TMConstants.STATUS_EN, status);
            if(infos!=null){
                return infos[0];
            }
        }catch (PaySdkException pse){
            pse.printStackTrace();
        }
        if(Locale.getDefault().getLanguage().equals("zh")){
            return "未知信息" ;
        }else {
            return "Error Desconocido" ;
        }
    }

    private String getErrInfo(String status){
        try {
            String[] errs = Locale.getDefault().getLanguage().equals("zh")?
                    PAYUtils.getProps(PaySdk.getInstance().getContext(), TMConstants.ERRNO, status):
                    PAYUtils.getProps(PaySdk.getInstance().getContext(), TMConstants.ERRNO_EN, status);
            if(errs!=null){
                return errs[0];
            }
        }catch (PaySdkException pse){
            pse.printStackTrace();
        }
        if(Locale.getDefault().getLanguage().equals("zh")){
            return "未知错误" ;
        }else {
            return "Error desconocido" ;
        }
    }

    @Override
    public InputInfo showInformacion(int timeout, String title) {
        transView.showInformacionVisitaView(timeout, title, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showMsgConfirmacion(int timeout, String[] mensajes, boolean corregir) {
        transView.showMsgConfirmacionView(timeout, mensajes, corregir, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showMsgConfirmacionCheck(int timeout, String[] mensajes) {
        transView.showMsgConfirmacionCheckView(timeout, mensajes, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showMontoVariable(int timeout, String[] mensajes) {
        transView.showMontoVariableView(timeout, mensajes, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showConfirmacion(int timeout, String[] mensajes) {
        transView.showConfirmacionView(timeout, mensajes, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showMsgConfirmacionRemesa(int timeout, String[] mensajes, boolean corregir) {
        transView.showMsgConfirmacionRemesaView(timeout, mensajes, corregir, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showVerficacionFaceId(int timeout) {
        transView.showVerficacionFaceId(timeout,listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showDatosDireccion(int timeout, String titulo, String[] mensajes) {
        transView.showDatosDireccion(timeout, titulo, mensajes,listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showDatosComplementarios(int timeout, String titulo, String[] mensajes) {
        transView.showDatosComplementarios(timeout, titulo, mensajes,listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showInfoKit(int timeout, String titulo, String[] mensajes, final String[] contenido) {
        transView.showInfoKit(timeout, titulo, mensajes, contenido, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showContrato(int timeout, String titulo, String[] mensajes) {
        transView.showContrato(timeout, titulo, mensajes,listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showIngresoOTP(int timeout, String titulo, String[] mensajes) {
        transView.showIngresoOTP(timeout, titulo, mensajes,listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showMensajeConfirmacion(int timeout, String titulo, String[] mensajes) {
        transView.showMensajeConfirmacion(timeout, titulo, mensajes,listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showReposicionInfoKit(int timeout, String titulo, String[] mensajes, final String[] contenido) {
        transView.showReposicionInfoKit(timeout, titulo, mensajes, contenido, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showSuccesView(int timeout, String titulo, String mensaje, boolean isSucces, boolean isButtonCancelar) {
        transView.showSuccesView(timeout, titulo, mensaje, isSucces, isButtonCancelar, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showIngreso2EditTextMonto(int timeout, String[] mensajes, int[] maxLengs, int[] inputType, int[] carcRequeridos, String titulo) {
        transView.showIngreso2EditTextMonto(timeout, mensajes, maxLengs, inputType,carcRequeridos,titulo, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if(mRet == 1){
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        }else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public void handlingOTP(boolean flag, String info) {
        transView.handlingOTP(flag, info);
    }
}
