package newpos.libpay.trans.pichincha.financieras.Retiro;

import android.content.Context;
import android.text.InputType;

import com.android.desert.keyboard.InputInfo;

import newpos.libpay.global.TMConfig;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

public class Retiro extends FinanceTrans implements TransPresenter {

    public Retiro(Context ctx, String transEname , TransInputPara p) {
        super(ctx, transEname);
        para = p ;
        setTraceNoInc(true);
        TransEName = transEname ;
        if(para != null){
            transUI = para.getTransUI();
        }
        isReversal = true;
        isSaveLog = true;
        isProcPreTrans=true;
        p.setNeedPrint(true);
    }

    @Override
    public void start() {
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
        setFixedDatas();
        if (tipoCuenta()){
            if (!TypeAccount.equals("4")){
                realizarRetiro();
            } else {
                para.setNeedPass(false);
                ProcCode = String.valueOf(Integer.parseInt(ProcCode) + 1000);
                realizarRetiroSinTarjeta();
            }
        }
    }

    private void realizarRetiro() {
        if (getMonto()){
            if (leerTarjeta(TARJETA_CLIENTE)){
                if (mensajeTransaccion()){
                    if (confirmacion("310000")){
                        InitTrans.wrlg.wrDataTxt("Fin transaccion " + TransEName);
                        transUI.showSuccess(timeout , Tcode.Mensajes.retiroExitoso , PAYUtils.getStrAmount(Amount));
                    }
                } else {
                    InitTrans.wrlg.wrDataTxt("Error - "+ TransEName +" - "+retVal);
                    transUI.showMsgError(timeout, "Error al envio de transacción");
                }
            } else {
                InitTrans.wrlg.wrDataTxt("Error - "+ TransEName +" - "+retVal);
                transUI.showMsgError(timeout,"Tarjeta no procesada");
            }
        }
    }

    private void realizarRetiroSinTarjeta() {
        try {
            String contrapartida;
            String[] datos;

            contrapartida = ingresoDatos();
            if (!contrapartida.equals("")) {
                datos = contrapartida.split("@");
                if (empaquetarMontoRetiroSinTarjeta(datos[1])) {
                    if (empaquetarCampo48RetiroSinTarjeta(datos)) {
                        if (mensajeTransaccionRetiroSinTarjeta()) {
                            if (confirmacion("311000")) {
                                //InitTrans.wrlg.wrDataTxt("Fin transaccion " + TransEName);
                                transUI.showSuccess(timeout, Tcode.Mensajes.retiroExitoso, PAYUtils.getStrAmount(Amount));
                            }
                        } else {
                            //InitTrans.wrlg.wrDataTxt("Error - " + TransEName + " - " + retVal);
                            transUI.showMsgError(timeout, "Transacción cancelada");
                        }
                    } else {
                        transUI.showMsgError(timeout, "Error obteniendo datos");
                    }
                } else {
                    transUI.showMsgError(timeout, "Error obteniendo el monto");
                }
            } else {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }
        }catch (Exception e){
            transUI.showMsgError(timeout, "RRST: Ocurrió un error, Favor contacte a un Administrador");
            //InitTrans.wrlg.wrDataTxt("Error - " + TransEName + " - " + retVal);
        }
    }

    private boolean empaquetarCampo48RetiroSinTarjeta(String[] datos) {
        if (datos[0].equals("") || datos[2].equals("")) {
            return false;
        } else {
            InitTrans.tkn12.setCedula(ISOUtil.padright(datos[0], 16, 'F').getBytes());
            InitTrans.tkn19.setNumeroControl(ISOUtil.str2bcd(ISOUtil.padright(datos[2],12,'F'),false));
        }
        return true;
    }

    private boolean mensajeTransaccionRetiroSinTarjeta() {
        boolean ret = false;
        int retPrep;
        EntryMode = "0012";
        Field48 = InitTrans.tkn12.packTkn12();
        Field48 += InitTrans.tkn19.packTkn19();
        packField63();
        retPrep=newPrepareOnline(inputMode);
        if(retPrep == 0){
            ret = true;
        } else if (retPrep == 77){
            realizarRetiroSinTarjeta();
        }
        return ret;
    }

    private boolean empaquetarMontoRetiroSinTarjeta(String monto) {
        boolean ret = true;
        try {
            Amount = Long.parseLong(monto);                                                         // Monto
            /*String amountServiceCost = ISOUtil.bcd2str(InitTrans.tkn80.getCostoServicio(), 0,
                    InitTrans.tkn80.getCostoServicio().length);
            Comision = Long.valueOf(amountServiceCost);
            TotalAmount = Amount + Long.valueOf(amountServiceCost);*/
            para.setAmount(Amount);
            para.setOtherAmount(0);
        }catch (Exception e){
            ret = false;
        }

        return ret;
    }

    private String ingresoDatos() {
        String ret;
        String[] mensajes = {"Ingresa Número de Celular","Monto : ","Ingresa código de seguridad"};
        String titulo = "Ingresa monto para retiro";
        int[] maxLengs = {16,12};
        int[] inputType = {InputType.TYPE_CLASS_NUMBER,InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD};
        int[] carcRequeridos = {10,8};
        InputInfo inputInfo = transUI.showIngreso2EditTextMonto(timeout,mensajes, maxLengs,inputType, carcRequeridos, titulo);
        if(inputInfo.isResultFlag()){
            ret = inputInfo.getResult();
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            ret="";
        }
        return ret;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    private boolean tipoCuenta(){
        boolean ret = false;
        String[] cuentas = {"Cuenta corriente", "Cuenta ahorros", "Cuenta básica", "Tarjeta prepago","Retiro sin tarjeta"};
        String[] codCuenta = {"01", "02", "03", "04","05"};
        InputInfo inputInfo = transUI.showBotones(5, cuentas, "Tipo de cuenta retiro");
        if(inputInfo.isResultFlag()){
            InitTrans.tkn09.setSb_typeAccount(ISOUtil.str2bcd(codCuenta[inputInfo.getErrno()], true));
            TypeAccount = inputInfo.getResult();
            ret=true;
        }else{
            transUI.showMsgError(timeout,"Operación cancelada por usuario");
        }
        return ret;
    }

    private boolean getMonto(){
        boolean ret = false;
        InputInfo inputInfo = transUI.showPrincipales(timeout, "MONTO");

        if(inputInfo.isResultFlag()){
            Amount = Long.parseLong(inputInfo.getResult());
            String amountServiceCost = ISOUtil.bcd2str(InitTrans.tkn80.getCostoServicio(), 0,
                    InitTrans.tkn80.getCostoServicio().length);
            Comision = Long.valueOf(amountServiceCost);
            TotalAmount = Amount + Long.valueOf(amountServiceCost);
            para.setAmount(Amount);
            para.setOtherAmount(0);
            ret = true;
        }else {
            transUI.showError(timeout , inputInfo.getErrno());
            retVal = 2077;
        }
        return ret;
    }

    private boolean mensajeTransaccion(){
        boolean ret = false;
        int retPrep;
        Field48 = InitTrans.tkn09.packTkn09();
        Field48 += InitTrans.tkn43.packTkn43(inputMode, Track2);
        packField63();
        armar59();
        //transUI.newHandling(context.getResources().getString(R.string.procesando),timeout , Tcode.Mensajes.transaccionEnProceso);
        retPrep=newPrepareOnline(inputMode);
        if(retPrep == 0 || retPrep == 97){
            ret = true;
        }
        return ret;
    }

    private boolean confirmacion(String codigoProceso){
        boolean ret = false;
        setFixedDatas();
        clearDataFields();
        RRN=iso8583.getfield(37);
        iso8583.clearData();
        transUI.newHandling("Retiro" ,timeout , Tcode.Mensajes.conectandoConBanco);
        MsgID = "0202";
        ProcCode = codigoProceso;
        Field07=PAYUtils.getLocalDate()+PAYUtils.getLocalTime();
        TermID=TMConfig.getInstance().getTermID();
        MerchID=TMConfig.getInstance().getMerchID();
        setFields();
        retVal = 0;
        retVal = enviarConfirmacion();
        if(retVal!=0){
            transUI.showError(timeout , retVal);
        }else{
            ret = true;
        }
        return ret;
    }
}