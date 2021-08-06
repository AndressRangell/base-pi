package newpos.libpay.trans.pichincha.financieras.ConsultaValorCredito;

import android.content.Context;

import com.android.desert.keyboard.InputInfo;

import newpos.libpay.global.TMConfig;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class ConsultaValorCredito extends FinanceTrans implements TransPresenter {

    private int seleccion = -1;
    /**
     * 金融交易类构造
     *
     * @param ctx
     * @param transEname
     */
    public ConsultaValorCredito(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p;
        setTraceNoInc(true);
        TransEName = transEname;
        if (para != null) {
            transUI = para.getTransUI();
        }
        isReversal = false;
        isProcPreTrans = true;
    }

    @Override
    public void start() {
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
        if(consultaInicial()){
            if (mostrarCosto()) {
                InitTrans.tkn47.setNombre(new byte[30]);
                if (efectivacion()) {
                    if(confirmaCuenta()){
                        transUI.showSuccess(timeout , Tcode.Mensajes.consultaExitosa, "");
                    }
                }else {
                    transUI.showMsgError(timeout, "error en efectivación");
                }
            }
        }
    }

    private boolean confirmaCuenta() {
        boolean ret = false;
        int select;
        if (RspCode.equals("99")){
            String[] itemsV = InitTrans.tkn17.items;
            String[] codItemsV = InitTrans.tkn17.codItem;
            if(itemsV == null){
                transUI.showMsgError(timeout, "Error de token 17");
                return false;
            }else{
                select = seleccionarMenus(itemsV,codItemsV,InitTrans.tkn17.titulo);
                seleccion = select;
                if (select >= 0) {
                    if (mensajeTransaccion()){
                        if (RspCode.equals("99")){
                            transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                            return false;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            return true;
        }
        return ret;
    }

    protected int seleccionarMenus(String[] items, String[] codItems, String Msg){
        int ret = -1;
        InputInfo inputInfo = transUI.showBotones(items.length, items, Msg);
        if(inputInfo.isResultFlag()){
            ret = inputInfo.getErrno();
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    private boolean efectivacion() {
        boolean ret = false;
        para.setNeedPass(true);
        para.setEmvAll(true);
        if (leerTarjeta(TARJETA_CLIENTE)){
            if (mensajeTransaccion()){
                ret = true;
            }
        }else {
            transUI.showMsgError(timeout, "Tarjeta no procesada");
        }
        return ret;
    }

    private boolean mensajeTransaccion() {
        boolean ret;
        isSaveLog =true;
        Field07=null;
        InitTrans.tkn47.cleanConsultaCredito();
        MsgID = "0200";
        ProcCode = "420601";
        if (RspCode.equals("00")){
            Field48 = "170000";
        }if (RspCode.equals("99")){
            Field48 = InitTrans.tkn17.packTkn17(seleccion);
        }
        Field48 += InitTrans.tkn43.packTkn43(inputMode, Track2);
        Field48 += InitTrans.tkn47.packTkn47();
        RspCode=null;
        if (isPinExist) {
            PIN = emv.getPinBlock();
        }
        armar59();
        Field63 = packField63();
        para.setNeedPrint(true);
        ret = enviarTransaccion();
        return ret;
    }

    protected boolean consultaInicial() {
        boolean ret;
        setFixedDatas();
        MsgID = "0100";
        ProcCode = "420600";
        EntryMode = "0021";
        Amount = -1;
        packField63();
        ret = enviarTransaccion();
        return ret;
    }

    private boolean mostrarCosto() {
        boolean ret=false;
        Amount = Integer.parseInt(iso8583.getfield(4));
        TotalAmount = Amount;
        para.setAmount(Amount);
        InputInfo inputInfo = transUI.showConsultas(timeout, "COSTOSERVICIO", Amount);
        if (inputInfo.isResultFlag()) {
            ret=true;
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    public boolean enviarTransaccion(){
        boolean ret = false;
        int retPrep;
        TermID=TMConfig.getInstance().getTermID();
        MerchID=TMConfig.getInstance().getMerchID();
        retPrep=newPrepareOnline(inputMode);
        if(retPrep == 0 || retPrep == 99){
            ret = true;
        }else {
            if (retPrep == 1995) {
                transUI.showMsgError(timeout, "No se obtuvo información de impresión");
            }
        }
        return ret;
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }
}
