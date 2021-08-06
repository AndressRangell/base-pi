package newpos.libpay.trans.pichincha.Administrativas;

import android.content.Context;

import com.android.desert.keyboard.InputInfo;

import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.screens.Login;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.pichincha.Tools.TransTools;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

public class CrearUsuario extends FinanceTrans implements TransPresenter {

    private String rspCode;
    private Usuario personaLogin = Login.persona;

    /**
     * 金融交易类构造
     *
     * @param ctx
     * @param transEname
     */
    public CrearUsuario(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p ;
        setTraceNoInc(true);
        TransEName = transEname ;
        if(para != null){
            transUI = para.getTransUI();
        }
        isProcPreTrans = true;
    }

    @Override
    public void start() {
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
        if (vistaCrearUsuario()){
            if (leerTarjeta(TARJETA_CNB)){
                Amount=-1;
                Field48 = InitTrans.tkn30.packTkn30();
                Field48 = Field48 + InitTrans.tkn43.packTkn43(inputMode, Track2);
                armar59();
                packField63();
                AcquirerID = TransTools.ACQUIRER_ID;
                CurrencyCode = TransTools.CURRENCY_CODE;
                LocalTime = PAYUtils.getLocalTime();
                LocalDate = PAYUtils.getLocalDate();

                if (prepareOnline()) {
                    rspCode = iso8583.getfield(39);
                    if(rspCode.equals(ISO8583.RSPCODE.RSP_00)){
                        transUI.showSuccess(timeout , Tcode.Mensajes.usuarioCreadoConExito, "");
                    }else {
                        transUI.showError(timeout , retVal);
                    }
                }else {
                    transUI.showError(timeout , retVal);
                }
            }else {
                transUI.showMsgError(timeout, "Tarjeta no procesada");
            }
        }else {
            transUI.showError(timeout , retVal);
        }
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    private boolean vistaCrearUsuario(){
        boolean ret = false;
        InputInfo inputInfo = transUI.showAdministrativas(5*1000,"CREARUSUARIO");
        if(inputInfo.isResultFlag()){
            InitTrans.tkn30.setgNewUser(ISOUtil.str2bcd(inputInfo.getResult(),false));
            ret = true;
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    private boolean prepareOnline() {
        boolean ret = false;
        transUI.newHandling("Conectando" ,timeout , Tcode.Mensajes.conectandoConBanco);
        int retPrep;
        retPrep=newPrepareOnline(inputMode);
        if(retPrep == 0 || retPrep == 97){
            ret = true;
        }else {
            transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
        }
        clearPan();
        return ret;
    }
}