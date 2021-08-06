package newpos.libpay.trans.pichincha.Administrativas;

import android.content.Context;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.utils.ISOUtil;


/*****************************************************************************
 * Clase:      LoginCentral
 * Descripcion:  Realiza Login Centralizado .
 * Autor: John Osorio C.
 * **************************************************************************/
public class LoginCentral extends FinanceTrans implements TransPresenter {


    public static Boolean error = false;
    String userLocal;

    public LoginCentral(Context ctx, String transEname, String tipoTrans,TransInputPara p) {

        super(ctx, transEname);
        para = p;
        setTraceNoInc(true);
        userLocal=tipoTrans;
        TransEName = transEname;
        if (para != null) {
            transUI = para.getTransUI();
        }

    }

    @Override
    public void start() {
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
        transUI.newHandling(context.getResources().getString(R.string.procesando) ,2000 , Tcode.Mensajes.conectandoConBanco);
        if(login(userLocal)){
            InitTrans.loginCentral = true;
            transUI.trannSuccess(timeout,Tcode.Status.logon_succ);

        }else{
            InitTrans.loginCentral = false;
            transUI.showMsgError(timeout, "Error");
        }
    }


    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }


    /**
     * @param user: usuario Ingresado
     * @return estado de la transaccion
     */
    private boolean login(String user) {
        boolean ret = false;
        MsgID = "0800";
        EntryMode = "0021";
        ProcCode = "951000";
        MerchID = null;
        CurrencyCode=null;
        AcquirerName=ISOUtil.padleft(user,40,' ');
        Field63=packField63();
        Amount = -1;
        TotalAmount = Amount;
        para.setAmount(Amount);
        if(newPrepareOnline(0)==0){
            ret = true;
        }

        return ret;
    }
}

