package newpos.libpay.trans.pichincha.financieras.Deposito;

import android.content.Context;

import com.android.desert.keyboard.InputInfo;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.global.TMConfig;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

public class Deposito extends FinanceTrans implements TransPresenter {

    InputInfo inputInfo;
    private int timeOutScreensInit = 5 * 1000;

    public Deposito(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p;
        transUI = para.getTransUI();
        isReversal = false;
        isSaveLog = false;
        isDebit = true;
        isProcPreTrans = true;
        isProcSuffix = true;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
        setFixedDatas();
        if (CapturaDatos()){
            if (ConsultaInicial()){
                if (prepareOnline_Consul()){
                    if(MensajeConfirmacion()){
                        isSaveLog = true;
                        isReversal=true;
                        if(prepareOnline()){
                            if(Confirmacion()){
                                InitTrans.wrlg.wrDataTxt("Fin transaccion " + TransEName);
                                transUI.showSuccess(timeout, Tcode.Mensajes.depositoCorrecto, PAYUtils.getStrAmount(Amount));
                            }else{
                                InitTrans.wrlg.wrDataTxt("Error - "+ TransEName +" - "+retVal);
                                transUI.showError(timeout, retVal);
                            }
                        }else{
                            InitTrans.wrlg.wrDataTxt("Error - "+ TransEName +" - "+retVal);
                            transUI.showError(timeout, retVal);
                        }
                    }else{
                        InitTrans.wrlg.wrDataTxt("Error - "+ TransEName +" - "+retVal);
                        transUI.showError(timeout, retVal);
                    }
                }else{
                    InitTrans.wrlg.wrDataTxt("Error - "+ TransEName +" - "+retVal);
                    transUI.showError(timeOutScreensInit , retVal);
                }
            }else{
                InitTrans.wrlg.wrDataTxt("Error - "+ TransEName +" - "+retVal);
                transUI.showError(timeOutScreensInit , retVal);
            }
        }else{
            InitTrans.wrlg.wrDataTxt("Error - "+ TransEName +" - "+retVal);
            transUI.showError(timeOutScreensInit , retVal);
        }

        return;
    }



    private boolean CapturaDatos(){
        boolean ret = false;
        if(tipoCuenta()){
            if(tipoDocumento()){
                if(numCuentas()){
                        ret = true;
                }
            }
        }
        return ret;
    }
    private boolean ConsultaInicial() {
        boolean ret = false;
        if(leerTarjeta(TARJETA_CNB)){
            ret = true;
        }else{
            transUI.showMsgError(timeout, "Tarjeta no procesada");
        }

        return ret;
    }

    private boolean tipoCuenta(){
        boolean ret = false;
        String[] cuentas = {"Cuenta corriente", "Cuenta ahorros", "Cuenta básica"};
        String[] codCuenta = {"01", "02", "03"};
        InputInfo inputInfo = transUI.showBotones(3, cuentas, "Tipo de cuenta depósito");

        if(inputInfo.isResultFlag()){
            InitTrans.tkn09.setSb_typeAccount(ISOUtil.str2bcd(codCuenta[inputInfo.getErrno()], true));
            TypeAccount = inputInfo.getResult();
            ret=true;
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    private boolean tipoDocumento(){
        boolean ret = false;
        InputInfo inputInfo = transUI.showPrincipales(timeout, "DOCUMENTO");
        if(inputInfo.isResultFlag()){
            Cedula_or_Ruc = ISOUtil.padright(inputInfo.getResult(),16,'F');
            InitTrans.tkn47.setCedula_ruc(ISOUtil.str2bcd(Cedula_or_Ruc,false));
            String cuenta = "";
            cuenta = ISOUtil.padright(cuenta,14,'F');
            InitTrans.tkn47.setCuenta(ISOUtil.str2bcd(cuenta,false));
            ret=true;
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    private boolean numCuentas() {
        boolean ret = false;
        InitTrans.tkn11.clean();
        InputInfo inputInfo = transUI.showCuentaYMontoDeposito(timeout);

        if(inputInfo.isResultFlag()){
            String msg = inputInfo.getResult();
            String[] msg2 ;
            msg2= msg.split("@");
            Account = ISOUtil.padright(msg2[0],14,'F');
            InitTrans.tkn11.setNcuenta(ISOUtil.str2bcd(Account,false));
            Amount = Long.parseLong(msg2[1]);
            TotalAmount = Amount;
            para.setAmount(Amount);
            para.setOtherAmount(0);
            ret = true;
        }else {
            InitTrans.wrlg.wrDataTxt("Error - "+ TransEName +" - " + inputInfo.getErrno());
            transUI.showError(timeout , inputInfo.getErrno());
            retVal = 2077;
        }
        return ret;
    }

    private boolean prepareOnline_Consul() {
        boolean ret = false;
        transUI.newHandling("Depósito" ,timeout , Tcode.Mensajes.conectandoConBanco);
        int retPrep;
        InitTrans.tkn47.cleanDeposito();
        MsgID = MsgID_Consulta;
        ProcCode = ProcCode_consulta;
        SvrCode = null;
        Field60 = null;
        Field48 = InitTrans.tkn09.packTkn09();
        Field48 += InitTrans.tkn11.packTkn11();
        Field48 += InitTrans.tkn43.packTkn43(inputMode, Track2);
        Field48 += InitTrans.tkn47.packTkn47();

        packField63();
        para.setNeedPrint(false);
        retPrep=newPrepareOnline(inputMode);
        if(retPrep == 0 || retPrep == 97){
            ret = true;
        }
        return ret;
    }

    private boolean MensajeConfirmacion(){
        boolean ret = false;
        String[] mensajes = {"Cuenta perteneciente a:", InitTrans.tkn11.nombre.trim(), "Monto", PAYUtils.getStrAmount(Amount),"", "Depósito"};
        InputInfo inputInfo = transUI.showMsgConfirmacion(timeout, mensajes, false);
        if(inputInfo.isResultFlag()){
            if (inputInfo.getResult().equals("aceptar")){
                ret = true;
            } else{
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    /**
     * prepareOnline
     */
    private boolean prepareOnline() {
        boolean ret = false;
        int retPrep;
        InitTrans.tkn47.cleanDeposito();
        transUI.newHandling(context.getResources().getString(R.string.procesando) ,timeout , Tcode.Mensajes.conectandoConBanco);
        iso8583.clearData();
        MsgID = "0200";
        ProcCode = "300100";
        RspCode = null;
        PIN = null;
        Field48 = InitTrans.tkn09.packTkn09();
        Field48 += InitTrans.tkn11.packTkn11();
        Field48 += InitTrans.tkn47.packTkn47();
        armar59();
        packField63();
        para.setNeedPrint(true);
        retPrep=newPrepareOnline(0);
        if(retPrep == 0 || retPrep == 97){
            ret = true;
        }
        clearPan();

        return ret;
    }

    private boolean Confirmacion(){
        boolean ret = false;
        int ret2 ;
        setFixedDatas();
        clearDataFields();
        RRN=iso8583.getfield(37);
        iso8583.clearData();
        transUI.newHandling(context.getResources().getString(R.string.procesando) ,timeout , Tcode.Mensajes.conectandoConBanco);
        MsgID = "0202";
        ProcCode = "300100";
        TermID=TMConfig.getInstance().getTermID();
        MerchID=TMConfig.getInstance().getMerchID();
        setFields();
        ret2=enviarConfirmacion();
        if(ret2!=0){
            transUI.showError(timeOutScreensInit , ret2);
        }else{
            ret = true;
        }

        return ret;
    }
}