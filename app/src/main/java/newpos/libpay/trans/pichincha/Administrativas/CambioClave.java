package newpos.libpay.trans.pichincha.Administrativas;

import android.content.Context;

import newpos.libpay.global.TMConfig;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;

public class CambioClave extends FinanceTrans implements TransPresenter {

    /**
     * 金融交易类构造
     *
     * @param ctx
     * @param transEname
     */
    public CambioClave(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p;
        transUI = para.getTransUI();
    }

    @Override
    public void start() {
        transUI.newHandling("CAMBIO DE CLAVE" ,timeout , Tcode.Mensajes.conectandoConBanco);
        if (leerTarjeta(TARJETA_CAMBIO_CLAVE)) {
            if (enviarTransaccion()) {
                transUI.trannSuccess(timeout, Tcode.Status.operacionRealizadaConExito);
            } else {
                transUI.showError(timeout, Tcode.T_wait_timeout);
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
    }

    private boolean enviarTransaccion() {
        boolean ret = false;
        int rta;
        setFixedDatas();
        RspCode = null;
        if (emv != null)  {
            SecurityInfo = emv.getNewPinBlock();
        }
        Field48 = InitTrans.tkn43.packTkn43(inputMode, Track2);
        if (inputMode == 2){
            EntryMode = "0021";
        }else {
            EntryMode = "0051";
        }
        armar59();
        para.setNeedPrint(true);
        Amount = -1;
        Field63 = packField63();
        TermID = TMConfig.getInstance().getTermID();
        MerchID = TMConfig.getInstance().getMerchID();
        rta = newPrepareOnline(inputMode);
        if (rta == 0) {
            ret = true;
        } else {
            if (rta == 1995) {
                transUI.showMsgError(timeout, "No se obtuvo información de impresión");
            }
        }
        return ret;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }
}
