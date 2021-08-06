package newpos.libpay.trans.pichincha.financieras.bimo;

import android.content.Context;
import android.text.InputType;

import com.android.desert.keyboard.InputInfo;

import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn92;
import newpos.libpay.global.TMConfig;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;

import static newpos.libpay.utils.ISOUtil.formatMiles;

public class Bimo extends FinanceTrans implements TransPresenter {

    InputInfo inputInfo;
    String inputTitle;
    Context context;
    private int seleccion = 0;
    private String msgErrorUserCancel = "Operación cancelada por usuario";

    public Bimo(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p;
        transUI = para.getTransUI();
        isReversal = false;
        TransEName = transEname;
        context = ctx;
        tkn92 = new Tkn92();
        isProcPreTrans = true;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
        int select = -1;
        String[] opciones = {"Enrolamiento billetera móvil", "Retiro billetera móvil", "Depósito billetera móvil"};
        String[] codOpciones = {"00", "01", "02"};
        select = seleccionarMenus(opciones, codOpciones, "Billetera móvil");
        if (select >= 0) {
            opciones = new String[]{"Enrolamiento billetera móvil - BIMO", "Retiro billetera móvil - BIMO", "Depósito billetera móvil - BIMO"};
            switch (select) {
                case 0:
                    enrolamientoBimo(opciones, codOpciones, select);
                    break;
                case 1:
                    retiroBimo(opciones, codOpciones, select);
                    break;
                case 2:
                    depositoBimo(opciones, codOpciones, select);
                    break;
            }
        } else {
            transUI.showMsgError(timeout, msgErrorUserCancel);
        }
    }

    private void enrolamientoBimo(String[] opciones, String[] codOpciones, int select) {
        String Aumento = "00";
        String celular;
        para.setNeedPass(true);
        para.setEmvAll(true);
        if (leerTarjeta(TARJETA_CLIENTE)) {
            if (consultaInicial("41" + codOpciones[select] + Aumento)) {
                if (multipleCuenta_Tkn17()) {
                    celular = ingresoCelular(opciones, select);
                    if (!celular.equals("")) {
                        if (mostrarConfirmacion(opciones, select, "41" + codOpciones[select] + Aumento, "")) {
                            Amount = Integer.parseInt(ISOUtil.padleft( "0", 12, '0'));
                            TotalAmount = Amount;
                            isReversal = true;
                            if (efectivacion("41" + codOpciones[select] + "01")) {
                                if (confirmacion("41" + codOpciones[select] + "01", opciones[select])) {
                                    transUI.showSuccess(timeout, Tcode.Mensajes.operacionRealizadaConExito, "");
                                }
                            }
                        }
                    }
                }
            } else {
                transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
            }
        }  else {
            transUI.showMsgError(timeout, msgErrorUserCancel);
        }
    }

    private void retiroBimo(String[] opciones, String[] codOpciones, int select) {
        String monto;
        String otp;
        String celular;
        if (consultaInicial("41" + codOpciones[select] + "00")) {
            celular = ingresoContrapartida(opciones[select], "Celular beneficiario", 10, InputType.TYPE_CLASS_NUMBER, 10);
            if (!celular.equals("")) {
                monto = ingresoContrapartida(opciones[select], "Monto a retirar", 8, 10, 4);
                monto = monto.replace(".", "");
                if (!monto.equals("")) {
                    otp = ingresoContrapartida(opciones[select], "Ingrese el código de seguridad (OTP) recibido por SMS", 10, InputType.TYPE_CLASS_NUMBER, 4);
                    if (!otp.equals("")) {
                        InitTrans.tkn48.setNumero_kit(ISOUtil.str2bcd(ISOUtil.padright(otp, 10, 'F'), false));
                        InitTrans.tkn48.setNumero_cel(ISOUtil.str2bcd(ISOUtil.padright(celular, 12, 'F'), false));
                        if (mostrarConfirmacion(opciones, select, "41" + codOpciones[select] + "00", monto)) {
                            Field06 = Amount;
                            Amount = Integer.parseInt(monto);
                            TotalAmount = Amount;
                            isReversal = true;
                            if (efectivacion("410101")) {
                                if (confirmacion("410101", opciones[select])) {
                                    transUI.showSuccess(timeout, Tcode.Mensajes.operacionRealizadaConExito, "");
                                }
                            }
                        }
                    }
                }
            } else {
                transUI.showMsgError(timeout, "El número de celular no coincide");
            }
            /*if (mostrarCosto()) {
            }*/
        }
    }

    private void depositoBimo(String[] opciones, String[] codOpciones, int select) {
        String Aumento = "00";
        String celular, monto, cedula;
        celular = ingresoCelular(opciones, select);
        if (!celular.equals("")) {
            monto = ingresoContrapartida(opciones[select], "Monto a depositar", 8, 10, 4);
            cedula = ingresoContrapartida(opciones[select], "Cédula del depositante", 10, 1010, 10);
            monto = monto.replace(".", "");
            Amount = Integer.parseInt(monto);
            TotalAmount = Amount;
            para.setNeedAmount(true);
            para.setAmount(Amount);
            InitTrans.tkn47.setCedula_ruc(ISOUtil.str2bcd(ISOUtil.padright(cedula, 16, 'F'), false));
            InitTrans.tkn47.setNombre(new byte[30]);
            InitTrans.tkn47.setCuenta(ISOUtil.str2bcd(ISOUtil.padright(celular, 14, 'F'), false));
            if (!monto.equals("") && !cedula.equals("")) {
                if (consultaInicial("41" + codOpciones[select] + Aumento)) {
                    if (mostrarConfirmacion(opciones, select, "41" + codOpciones[select] + Aumento, "")) {
                        para.setNeedPass(true);
                        para.setEmvAll(true);
                        if (leerTarjeta(TARJETA_CNB)) {
                            TotalAmount = Amount;
                            isReversal = true;
                            if (efectivacion("41" + codOpciones[select] + "01")) {
                                if (confirmacion("41" + codOpciones[select] + "01", opciones[select])) {
                                    transUI.showSuccess(timeout, Tcode.Mensajes.operacionRealizadaConExito, "");
                                }
                            }
                        } else {
                            transUI.showMsgError(timeout, msgErrorUserCancel);
                        }
                    }
                }
            }
        } else {
            transUI.showMsgError(timeout, "El número de celular no coincide");
        }
    }

    private String ingresoCelular(String[] items, int select) {
        String ret = "";
        String numCelular, confirmarNum, numTkn47;
        numCelular = ingresoContrapartida(items[select], "Celular beneficiario", 10, InputType.TYPE_CLASS_NUMBER, 10);
        if (!numCelular.equals("")) {
            if (select == 0) {
                numTkn47 = ISOUtil.bcd2str(InitTrans.tkn47.getCuenta(), 0, InitTrans.tkn47.getCuenta().length).substring(0, 10);
                confirmarNum = ingresoContrapartida(items[select], "Ingrese nuevamente", 10, InputType.TYPE_CLASS_NUMBER, 10);
                if (!confirmarNum.equals("")) {
                    if (numCelular.equals(confirmarNum)) {
                        if (numCelular.equals(numTkn47)) {
                            ret = numCelular;
                        } else {
                            transUI.showMsgError(timeout, "El número de celular no coincide con la base de datos");
                            ret = "";
                        }
                    } else {
                        transUI.showMsgError(timeout, "Los números de celular no coinciden");
                        ret = "";
                    }
                }
            } else {
                confirmarNum = ingresoContrapartida(items[select], "Ingrese nuevamente", 10, InputType.TYPE_CLASS_NUMBER, 10);
                if (!confirmarNum.equals("")) {
                    if (numCelular.equals(confirmarNum)) {
                        ret = numCelular;
                    } else {
                        transUI.showMsgError(timeout, "Los números de celular no coinciden");
                        ret = "";
                    }
                }
            }
        }
        return ret;
    }

    private boolean multipleCuenta_Tkn17() {
        boolean ret = false;
        int select;
        if (RspCode.equals("97")){
            String[] itemsV = InitTrans.tkn17.items;
            String[] codItemsV = InitTrans.tkn17.codItem;
            if(itemsV == null){
                transUI.showMsgError(timeout, "Error de token 17");
                return false;
            }else{
                select = seleccionarMenus(itemsV,codItemsV,InitTrans.tkn17.titulo);
                seleccion = select;
                ret = true;
            }
        } else {
            ret = true;
        }
        return ret;
    }

    private boolean mostrarCosto(){
        Amount = Integer.parseInt(iso8583.getfield(4));
        String[] vistaMensaje = new String[6];
        vistaMensaje[0] = "Esta transacción tiene un costo de servicio para el ordenante de $" +
                formatMiles(Integer.parseInt(iso8583.getfield(4)));
        vistaMensaje[1] = "";
        vistaMensaje[2] = "";
        vistaMensaje[3] = "";
        vistaMensaje[4] = "";
        vistaMensaje[5] = "Consulta retiro";

        return(ventanaConfirmacion(vistaMensaje));
    }

    private boolean mostrarConfirmacion(String [] items, int select2, String code, String servicio) {
        boolean ret;
        int i;
        String[] valores = new String[5];
        String[] vistaMensaje = new String[6];
        String[] itemsV = InitTrans.tkn17.items;
        String[] codItemsV = InitTrans.tkn17.codItem;
        String[] mensajes = {"Número celular : ", "Número cédula : ", "Cuenta : ", "Propietario TD : ", ""};
        String[] mensajesRetiro = {"Confirmar datos", "Celular beneficiario : ", "Monto a retirar : ", "", ""};
        String[] mensajesDeposito = {"Celular beneficiario : ", "Nombre beneficiario : ", "", "Monto : ", ""};
        if (code.equals("410000")) {
            if (RspCode.equals("97")) {
                String celular = ISOUtil.bcd2str(InitTrans.tkn47.getCuenta(), 0, InitTrans.tkn47.getCuenta().length).replace("F", "").substring(0, 10);
                valores[0] = celular.substring(0,3) + "XXXX" + celular.substring(celular.length() - 3);
                valores[1] = ISOUtil.bcd2str(InitTrans.tkn47.getCedula_ruc(), 0, InitTrans.tkn47.getCedula_ruc().length).replace("F", "");
            } else {
                String celular = ISOUtil.bcd2str(InitTrans.tkn47.getCuenta(), 0, InitTrans.tkn47.getCuenta().length).replace("F", "").substring(0, 10);
                valores[0] = celular.substring(0,3) + "XXXX" + celular.substring(celular.length() - 3);
                valores[1] = ISOUtil.bcd2str(InitTrans.tkn47.getCedula_ruc(), 0, InitTrans.tkn47.getCedula_ruc().length).replace("F", "");
            }
            valores[2] = itemsV[seleccion].substring(0, 3) + "XXXX" + itemsV[seleccion].substring(itemsV[seleccion].indexOf(' ') - 3, itemsV[seleccion].indexOf(' '));
            valores[3] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn47.getNombre()));
            valores[4] = "";
            for (i = 0; i < mensajes.length; i++) {
                vistaMensaje[i] = mensajes[i] + valores[i];
            }
        } else if (code.equals("410200")) {
            valores[0] = ISOUtil.bcd2str(InitTrans.tkn48.getNumero_cel(), 0, InitTrans.tkn48.getNumero_cel().length).substring(0, 10);
            valores[1] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn47.getNombre()));
            valores[2] = "";
            valores[3] = formatMiles(Integer.parseInt(String.valueOf(Amount)));
            valores[4] = "";
            for (i = 0; i < mensajesDeposito.length; i++) {
                vistaMensaje[i] = mensajesDeposito[i] + valores[i];
            }
        } else if (code.equals("410100")) {
            valores[0] = "";
            valores[1] = ISOUtil.bcd2str(InitTrans.tkn48.getNumero_cel(), 0, InitTrans.tkn48.getNumero_cel().length).substring(0, 10);
            valores[2] = formatMiles(Integer.parseInt(servicio));
            valores[3] = "";
            valores[4] = "";
            for (i = 0; i < mensajesRetiro.length; i++) {
                vistaMensaje[i] = mensajesRetiro[i] + valores[i];
            }
        }
        vistaMensaje[5] = items[select2];
        ret=ventanaConfirmacion(vistaMensaje);
        return(ret);
    }

    private boolean consultaInicial(String code) {
        boolean ret;
        setFixedDatas();
        para.setNeedPrint(false);
        InitTrans.tkn98.cleanTkn98();
        MsgID = "0100";
        if (!code.equals("410200")) {
            Amount = -1;
        }
        ProcCode = code;
        SvrCode = null;
        Field60 = null;
        if (ProcCode.equals("410100")) {
            EntryMode = "0021";
        } else if (ProcCode.equals("410200")) {
            EntryMode = "0021";
            Field48 = InitTrans.tkn47.packTkn47();
        } else {
            EntryMode = "0051";
            Field48 = InitTrans.tkn43.packTkn43(inputMode, Track2);
        }
        packField63();
        ret = enviarTransaccion(code);
        return ret;
    }

    private boolean efectivacion(String code){
        boolean ret;
        String pin;
        if (isPinExist) {
            pin = emv.getPinBlock();
        }
        if (!code.equals("410201")) {
            iso8583.clearData();
        }
        isSaveLog = true;
        Field07=null;
        MsgID = "0200";
        ProcCode = code;
        EntryMode = "0051";
        if (code.equals("410201")) {
            Field48 = InitTrans.tkn43.packTkn43(inputMode, Track2);
            Field48 += InitTrans.tkn47.packTkn47();
            Field48 += InitTrans.tkn48.packTkn48();
            Field48 += InitTrans.tkn93.packTkn93();
        } else if (code.equals("410101")) {
            EntryMode = "0021";
            Field48 = InitTrans.tkn48.packTkn48();
            Field48 += InitTrans.tkn93.packTkn93();
        } else if (code.equals("410001")){
            Field48 = InitTrans.tkn17.packTkn17(seleccion);
            Field48 += InitTrans.tkn47.packTkn47();
            Field48 += InitTrans.tkn93.packTkn93();
        }
        RspCode = null;
        if (!code.equals("410101")) {
            armar59();
        }
        Field63 = packField63();
        para.setNeedPrint(true);
        inputMode = 0;
        InitTrans.tkn47.cleanConsultaCredito();
        ret = enviarTransaccion(code);
        return ret;
    }

    private boolean confirmacion(String code, String title) {
        boolean ret = false;
        setFixedDatas();
        clearDataFields();
        RRN=iso8583.getfield(37);
        iso8583.clearData();
        transUI.newHandling(title ,timeout , Tcode.Mensajes.conectandoConBanco);
        MsgID = "0202";
        TermID= TMConfig.getInstance().getTermID();
        MerchID=TMConfig.getInstance().getMerchID();
        ProcCode = code;
        para.setNeedPrint(false);
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

    private boolean enviarTransaccion(String code) {
        boolean ret = false;
        int retPrep;
        TermID=TMConfig.getInstance().getTermID();
        MerchID=TMConfig.getInstance().getMerchID();
        retPrep=newPrepareOnline(inputMode);
        if(retPrep == 0){
            ret = true;
        }else {
            if (retPrep == 1995) {
                transUI.showMsgError(timeout, "No se obtuvo información de impresión");
            }else if(retPrep==93){
                efectivacion(code);
            }
        }
        return ret;
    }

    protected int seleccionarMenus(String[] items, String[] codItems, String Msg){
        int ret = -1;
        InputInfo inputInfo = transUI.showBotones(items.length, items, Msg);
        if(inputInfo.isResultFlag()){
            ret = inputInfo.getErrno();
        }else{
            transUI.showMsgError(timeout, msgErrorUserCancel);
        }
        return ret;
    }

    protected String ingresoContrapartida(String titulo, String mensaje, int maxLeng, int inputType, int carcRequeridos){
        String ret;
        InputInfo inputInfo = transUI.showContrapartida(timeout,mensaje,maxLeng,inputType,carcRequeridos, titulo);
        if(inputInfo.isResultFlag()){
            ret = inputInfo.getResult();
        }else{
            transUI.showMsgError(timeout, msgErrorUserCancel);
            ret="";
        }
        return ret;
    }

    protected boolean ventanaConfirmacion(String[] mensajes){
        boolean ret = false;

        InputInfo inputInfo = transUI.showMsgConfirmacion(timeout, mensajes, false);
        if(inputInfo.isResultFlag()){
            if (inputInfo.getResult().equals("aceptar")){
                ret = true;
            } else{
                transUI.showMsgError(timeout, msgErrorUserCancel);
            }
        }else{
            transUI.showMsgError(timeout, msgErrorUserCancel);
        }

        return ret;
    }

}