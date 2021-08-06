package newpos.libpay.trans.pichincha.financieras.GirosyRemesas;

import android.content.Context;
import android.text.InputType;

import com.android.desert.keyboard.InputInfo;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.Utils;
import newpos.libpay.global.TMConfig;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

import static newpos.libpay.utils.ISOUtil.formatMiles;

public class GirosyRemesas extends FinanceTrans implements TransPresenter {

    String tipoRecau;

    /**
     * Contructor de la calse GirosyRemesas
     *
     * @param ctx
     * @param transEname
     */
    public GirosyRemesas(Context ctx, String transEname, String tipoTrans, TransInputPara p) {
        super(ctx, transEname);
        para = p ;
        setTraceNoInc(true);
        TransEName = transEname ;
        if(para != null){
            transUI = para.getTransUI();
        }
        isReversal = false;
        isProcPreTrans=true;
        tipoRecau = tipoTrans;
    }

    @Override
    public void start() {
        switch (tipoRecau){
            case "Remesas":
                if(!remesas()){
                    transUI.showMsgError(timeout, "Error");
                }
                break;
            case "GirosNacionales":
                if(!girosNacionales()){
                    transUI.showMsgError(timeout, "Error");
                }
                break;
        }
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    private boolean remesas() {
        boolean ret = false;
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
        int select;
        String[] items = {"Remesas internacionales"};
        String[] codItems = {"01"};
        select =  seleccionarMenus(items,codItems,"Remesas");
        if (select >= 0){
            if (mensajeInicial("420900")){
                if(ingresoDatosRemesas()){
                    if (mensajeInicial("420901")){
                        if (confirmar(items,select)){
                            /*if (cuentas(true)){*/
                            /**Se crea método para separar la lógica de remesas de giros nacionales**/
                            if (cuentasRemesas()) {
                                if (confirmacion("420903","CONFIRMACION REMESAS")) {
                                    transUI.showSuccess(timeout, Tcode.Mensajes.operacionRealizadaConExito,"");
                                    ret = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    private boolean girosNacionales() {
        boolean ret = false;
        int select;
        String[] items = {"Orden de pago","Cobro orden de pago", "Devolución orden de pago"};
        String[] codItems = {"01","02","03"};
        select =  seleccionarMenus(items,codItems,"Giros nacionales");
        if (select >= 0){
            switch (select){
                case 0:
                    ret = ordenPago();
                    break;
                case 1:
                    ret  = cobroOdenPago();
                    break;
                case 2:
                    ret = devolucionOrdenPago();
                    break;
            }
        }
        return ret;
    }

    private boolean ordenPago() {
        boolean ret = false;
        String titulo = "Orden de pago";
        String mensaje = "Número de identificación ordenante";
        int maxLeng = 10;
        int inputType = 1010;
        int caracReq = 10;
        String documento = ingresoContrapartida(titulo,mensaje,maxLeng,inputType,caracReq);
        InitTrans.tkn12.setCedula(ISOUtil.padright(documento, 16, 'F').getBytes());
        if (!documento.equals("")){
            if (mensajeInicial("450200")){
                if (cuentas(false)) {
                    if (confirmarCelular()) {
                        if (validaCliente("450202",3)) {
                            if (confirmarMonto()) {
                                if (ingresaDatos()) {
                                    if (efectivacionOrdenPago(false)) {
                                        if (confirmarDatos()) {
                                            if (mensajeTransaccion("450205")) {
                                                if (confirmacion("450205", "CONFIRMACION ORDEN DE PAGO")){
                                                    transUI.showSuccess(timeout, Tcode.Mensajes.ordenPagoExitosa, "");
                                                    ret = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    private boolean cobroOdenPago() {
        boolean ret = false;
        String titulo = "Cobro orden de pago";
        String[] mensajes = {"Número de identificación del beneficiario", "Ingresa código de seguridad"};
        int[] maxLengs = {10,10};
        int[] inputTypes = {1010,(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)};
        int[] caracRequeridos = {10,0};
        String datos = ingreso2EditText(titulo,mensajes,maxLengs,inputTypes,caracRequeridos);
        if(!datos.equals("")) {
            String[] infoDatos = datos.split("@");
            InitTrans.tkn12.setCedula(ISOUtil.padright(infoDatos[0], 16, 'F').getBytes());
            InitTrans.tkn48.setNumero_kit(ISOUtil.str2bcd(ISOUtil.padright(infoDatos[1],10,'F'), false));
            InitTrans.tkn48.setNumero_cel(ISOUtil.str2bcd(ISOUtil.padright("",12,'0'), false));
            if (mensajeInicial("450206")) {
                if (validaCliente("450206",1)) {
                    if (confirmarPago(infoDatos[0], "Cobro orden de pago")) {
                        if (mensajeTransaccion("450207")) {
                            if (confirmacion("450207", "Cobro orden de pago")) {
                                transUI.showSuccess(timeout, Tcode.Mensajes.ordenPagoExitosa, "");
                            }
                        }
                    } else {
                        transUI.showMsgError(timeout, "Operación cancelada por usuario");
                    }
                }
            }
        }


        return ret;
    }

    private boolean devolucionOrdenPago() {
        boolean ret = false;
        String titulo = "Devolución orden de pago";
        String[] mensajes = {"Número de identificación del ordenante", "Ingresa código de seguridad"};
        int[] maxLengs = {10,10};
        int[] inputTypes = {1010,(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)};
        int[] caracRequeridos = {10,0};
        String datos = ingreso2EditText(titulo,mensajes,maxLengs,inputTypes,caracRequeridos);
        if(!datos.equals("")){
            String[] infoDatos = datos.split("@");
            InitTrans.tkn12.setCedula(ISOUtil.padright(infoDatos[0], 16, 'F').getBytes());
            InitTrans.tkn48.setNumero_kit(ISOUtil.str2bcd(ISOUtil.padright(infoDatos[1],10,'F'), false));
            InitTrans.tkn48.setNumero_cel(ISOUtil.str2bcd(ISOUtil.padright("",12,'0'), false));
            if (mensajeInicial("450208")){
                if (validaCliente("450208",1)) {
                    if (confirmarPago(infoDatos[0],"Devolución orden de pago")) {
                        if (mensajeTransaccion("450209")) {
                            if (confirmacion("450209", "Devolución orden de pago")) {
                                transUI.showSuccess(timeout, Tcode.Mensajes.ordenPagoExitosa, "");
                            }
                        }
                    } else {
                        transUI.showMsgError(timeout, "Operación cancelada por usuario");
                    }
                }
            }
        }



        return ret;
    }

    /***
     * metodos para transaccion
     */
    /**
     * Funcion mensajeInicial()
     * Crea los campos necesarios para una consulta inicial de MsgID 0100
     * @param code recibe el ProcCode del mensaje
     * @return retorna boleano true si el envio de la transaccion funciono correctamente.
     */
    private boolean mensajeInicial(String code) {
        boolean ret;
        setFixedDatas();
        MsgID = "0100";
        ProcCode = code;
        if (code.equals("450204")){
            EntryMode = "0051";
        }else {
            EntryMode = "0021";
        }
        long monto = -1;
        RspCode = null;
        RRN = null;
        if (code.equals("450200")){
            Field48 = InitTrans.tkn12.packTkn12();
        } else if (code.equals("420901")){
            Field48 = InitTrans.tkn27.packTkn27();
        } else if (code.equals("450202") || code.equals("450201") || code.equals("450203")){
            Field48 = InitTrans.tkn38.packTkn38();
            Field48 += InitTrans.tkn47.packTkn47();
            Field48 += InitTrans.tkn48.packTkn48();
        } else if (code.equals("450204")){
            Field48 = InitTrans.tkn09.packTkn09();
            Field48 += InitTrans.tkn12.packTkn12();
            Field48 += InitTrans.tkn19.packTkn19();
            Field48 += InitTrans.tkn38.packTkn38();
            Field48 += InitTrans.tkn43.packTkn43(inputMode, Track2);
            Field48 += InitTrans.tkn47.packTkn47();
            Field48 += InitTrans.tkn48.packTkn48();
            monto = Amount;
        } else if (code.equals("450208") || code.equals("450206")){
            Field48 = InitTrans.tkn12.packTkn12();
            Field48 += InitTrans.tkn48.packTkn48();
        }
        para.setNeedAmount(true);
        Amount = monto;
        TotalAmount = Amount;
        para.setAmount(Amount);
        packField63();
        inputMode = INMODE_HAND;
        InitTrans.tkn27.clean();
        ret = enviarTransaccion(code);
        return ret;
    }

    private boolean efectivacionOrdenPago(boolean cliente) {
        boolean ret=false;
        String code = "";
        int select;
        para.setNeedPass(true);
        para.setEmvAll(true);
        boolean leeTarjeta = false;
        isSaveLog = false;
        isReversal = false;
        int pago = Integer.parseInt(ISOUtil.bcd2str(InitTrans.tkn38.getFormaDePago(),0,InitTrans.tkn38.getFormaDePago().length));
        if (pago == 3) {
            cliente = true;
        }
        if (cliente){
            String[] item = {"Cuenta corriente", "Cuenta ahorros", "Cuenta básica"};
            String[] cod = {"01", "02", "03"};
            select = seleccionarMenus(item,cod,context.getResources().getString(R.string.tipoDeCuenta));
            if (select >= 0) {
                InitTrans.tkn09.setSb_typeAccount(ISOUtil.str2bcd(cod[select], true));
                if (leerTarjeta(TARJETA_CLIENTE)) {
                    if (iso8583.getfield(39).equals("77")){
                        code = "450203";
                    } else {
                        code = "450204";
                    }
                    leeTarjeta = true;
                } else {
                    transUI.showMsgError(timeout, "Tarjeta no procesada");
                }
            }
        } else {
            InitTrans.tkn09.setSb_typeAccount(ISOUtil.str2bcd("00", true));
            if (leerTarjeta(TARJETA_CNB)) {
                code = "450204";
                leeTarjeta = true;
            } else {
                transUI.showMsgError(timeout, "Tarjeta no procesada");
            }
        }

        if (leeTarjeta){
            if (mensajeInicial(code)){
                if (code.equals("450203") && !iso8583.getfield(39).equals(00)){
                    transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                } else {
                    ret = true;
                }
            }
        }
        return ret;
    }

    public boolean enviarTransaccion(String code){
        boolean ret = false;
        int retPrep;
        TermID=TMConfig.getInstance().getTermID();
        MerchID=TMConfig.getInstance().getMerchID();
        retPrep=newPrepareOnline(inputMode);
        if(retPrep == 0 || retPrep == 99 || retPrep == 66){
            ret = true;
        }else {
            if (retPrep == 1995) {
                if (!code.equals("450209") && !code.equals("450205")){
                    transUI.showMsgError(timeout, "No se obtuvo información de impresión");
                } else {
                    ret = true;
                }

            }else if(retPrep==93){
                mensajeTransaccion(code);
            }
        }
        return ret;
    }

    private boolean mensajeTransaccion(String code) {
        boolean ret;
        boolean imprimir = false;
        setFixedDatas();
        MsgID = "0200";
        ProcCode = code;
        if (code.equals("450205")){
            EntryMode = "0051";
            iso8583.setField(52,null);
            PIN = null;
            imprimir = true;
        }
        RRN = null;
        RspCode = null;
        isSaveLog = true;
        InitTrans.tkn93.clean();
        if (code.equals("420903")){
            Field48 = InitTrans.tkn27.packTkn27();
            Field48 += InitTrans.tkn43.packTkn43(inputMode, Track2);
            Field48 += InitTrans.tkn93.packTkn93();
            Field48 += InitTrans.tkn98.packTkn98();
            imprimir = true;
        } else if (code.equals("450209") || code.equals("450207")){
            Field48 = InitTrans.tkn12.pack2Tkn12();
            Field48 += InitTrans.tkn19.packTkn19();
            Field48 += InitTrans.tkn47.packTkn47();
            Field48 += InitTrans.tkn48.packTkn48();
            Field48 += InitTrans.tkn93.packTkn93();
            imprimir = true;
            isSaveLog = false;
        } else {
            Field48 = InitTrans.tkn12.packTkn12();
            Field48 += InitTrans.tkn19.packTkn19();
            Field48 += InitTrans.tkn26.packTkn26();
            Field48 += InitTrans.tkn38.packTkn38();
            Field48 += InitTrans.tkn47.packTkn47();
            Field48 += InitTrans.tkn48.packTkn48();
            Field48 += InitTrans.tkn93.packTkn93();
        }
        if (!code.equals("450205")){
            if (isPinExist) {
                PIN = emv.getPinBlock();
            }
        }
        Field63 = packField63();
        para.setNeedPrint(imprimir);
        ret = enviarTransaccion(code);
        return ret;
    }

    private boolean confirmacion(String code, String title) {
        boolean ret = false;
        setFixedDatas();
        clearDataFields();
        if (code.equals("450205") || code.equals("420903") || code.equals("450207") || code.equals("450209")){
            RRN = iso8583.getfield(37);
        }
        iso8583.clearData();
        transUI.newHandling(title ,timeout , Tcode.Mensajes.conectandoConBanco);
        MsgID = "0202";
        RspCode = null;
        TermID= TMConfig.getInstance().getTermID();
        MerchID=TMConfig.getInstance().getMerchID();
        ProcCode = code;
        Field63 = packField63();
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


    /***
     * fin metodos para transaccion
     */

    /**
     * ventanas de confirmacion -*VISTAS*-
     */
    private boolean confirmar(String [] items, int select){
        if (RspCode.equals("00")){
            String[] vistaMensaje = new String[6];
            boolean campoDireccion = false;
            if (!InitTrans.tkn27.direccion()) {
                vistaMensaje[0] = "Beneficiario : \n" + ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn27.getBeneficiario())).trim();
                vistaMensaje[1] = "Ordenante : \n" + ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn27.getOrdenante())).trim();
                vistaMensaje[2] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn27.getDireccion())).trim();
                vistaMensaje[3] = "Estado de la remesa : " + ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn27.getEstadoRemesa())).trim();
                vistaMensaje[4] = "Monto remesa: $ " + formatMiles(Integer.parseInt(ISOUtil.bcd2str(InitTrans.tkn27.getMontoRemesa(),0,
                        InitTrans.tkn27.getMontoRemesa().length)));
                vistaMensaje[5] = items[select];
            } else {
                vistaMensaje[0] = "Beneficiario : \n" + ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn27.getBeneficiario())).trim();
                vistaMensaje[1] = "Dirección : ";
                vistaMensaje[2] = "";
                vistaMensaje[3] = "Estado de la remesa : " + ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn27.getEstadoRemesa())).trim();
                vistaMensaje[4] = "Monto remesa: $ " + formatMiles(Integer.parseInt(ISOUtil.bcd2str(InitTrans.tkn27.getMontoRemesa(),0,
                        InitTrans.tkn27.getMontoRemesa().length)));
                vistaMensaje[5] = items[select];
                campoDireccion = true;
            }
            para.setNeedAmount(true);
            Amount = Integer.parseInt(ISOUtil.bcd2str(InitTrans.tkn27.getMontoRemesa(),0,
                    InitTrans.tkn27.getMontoRemesa().length));
            TotalAmount = Amount;
            para.setAmount(Amount);
            return(ventanaConfirmacionRemesa(vistaMensaje,campoDireccion));
        }else {
            transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
            return false;
        }
    }

    private boolean confirmarMonto() {
        boolean ret = false;
        String[] vistaMensaje = new String[5];
        vistaMensaje[0] = "";
        vistaMensaje[1] = "";
        vistaMensaje[2] = "Monto a enviar";
        vistaMensaje[3] = "";
        vistaMensaje[4] = "Orden pago - matric";
        ret=ingresarMontoVariable(vistaMensaje);
        return ret;
    }
    private boolean confirmarDatos(){
        String cuenta;
        if (iso8583.getfield(39).equals("97")){
            cuenta = "Cuenta No :  XXXXXX" + ISOUtil.bcd2str(InitTrans.tkn47.getCuenta(),0,
                    InitTrans.tkn47.getCuenta().length).substring(6,10) + "\n";
        } else {
            cuenta = "";
        }
        String[] vistaMensaje = new String[6];
        String cedula = new String(InitTrans.tkn12.getCedula(), StandardCharsets.UTF_8);
        int montoTotal = Integer.parseInt(InitTrans.tkn26.montos[0]) +
                Integer.parseInt(InitTrans.tkn26.montos[1]) +
                Integer.parseInt(InitTrans.tkn26.montos[2]);
        //vistaMensaje[0]="";
        vistaMensaje[0] = cuenta + "Cédula : " + cedula.substring(0,10) + "\n" +
                "No. celular : " + ISOUtil.bcd2str(InitTrans.tkn19.getNumeroControl(),
                0,InitTrans.tkn19.getNumeroControl().length).substring(0,10);

        vistaMensaje[1] = "\nValor giro :" + formatMiles(Integer.parseInt(InitTrans.tkn26.montos[0])) + "\n" +
                "Comisión :" + formatMiles(Integer.parseInt(InitTrans.tkn26.montos[1])+ Integer.parseInt(InitTrans.tkn26.montos[2])) +
                "\nMonto total :" + formatMiles(montoTotal);
        //vistaMensaje[3] = "";
        vistaMensaje[2] = "¿Deseas pagar?";
        vistaMensaje[3] = "Orden de pago";
        para.setNeedAmount(true);
        Amount = montoTotal;
        TotalAmount = Amount;
        para.setAmount(Amount);
        return(ventanaConfirmacion(vistaMensaje));
    }

    private boolean confirmarCelular(){
        byte[] celularNull = new byte[]{ 0x0,0x0,0x0,0x0,0x0,0x0};
        if (Arrays.equals(InitTrans.tkn48.getNumero_cel(), celularNull)){
            if (confirmacionOTP()){
                if (mensajeInicial("450201")){
                    if (iso8583.getfield(39).equals("00")){
                        if (OTPverificacion("450202", false,3)){
                            return true;
                        } else {
                            transUI.showMsgError(timeout, "Número de intentos fallidos");
                        }
                    } else {
                        transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                    }
                }
            } else {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }
        } else {
            String mensaje = "Celular ordenante : \n" + ISOUtil.bcd2str(InitTrans.tkn48.getNumero_cel(),
                    0,InitTrans.tkn48.getNumero_cel().length).substring(0,10);
            if (ventanaConfirmacion("Orden pago",mensaje)){
                if (mensajeInicial("450202")){
                    return true;
                } else{
                    return true;
                }
            }
        }
        return false;
    }

    private boolean confirmarPago(String cedula,String titulo){
        int monto = 0;
        if (RspCode.equals("00")){
            if (iso8583.getfield(04)!= null){
                monto = Integer.parseInt(iso8583.getfield(04));
            }
            String[] vistaMensaje = new String[6];
            vistaMensaje[0] = "";
            vistaMensaje[1] = "Servicio de pago a terceros";
            vistaMensaje[2] = "No. identificación beneficiario";
            vistaMensaje[3] = cedula;
            vistaMensaje[4] = "Monto: $" + formatMiles(monto);
            vistaMensaje[5] = titulo;
            para.setNeedAmount(true);
            Amount = monto;
            TotalAmount = Amount;
            para.setAmount(Amount);
            return(ventanaConfirmacion(vistaMensaje));
        }else {
            transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
            return false;
        }
    }

    private boolean ventanaConfirmacion(String titulo, String mensaje){
        String[] vistaMensaje = new String[6];

        vistaMensaje[0] = mensaje;
        vistaMensaje[1] = "";
        vistaMensaje[2] = "";
        vistaMensaje[3] = "";
        vistaMensaje[4] = "";
        vistaMensaje[5] = titulo;

        return(ventanaConfirmacion(vistaMensaje));
    }

    private boolean confirmacionOTP() {
        String titulo = "OTP en proceso";
        String mensaje = "No. celular";
        String mensaje2 = "";
        int maxLeng = 10;
        int inputType = InputType.TYPE_CLASS_NUMBER;
        int caracReq = 10;
        String celular = ingresoContrapartida(titulo,mensaje,maxLeng,inputType,caracReq);
        InitTrans.tkn48.setNumero_cel(ISOUtil.str2bcd(ISOUtil.padright(celular,12,'F'),false));
        InitTrans.tkn48.setNumero_kit(ISOUtil.str2bcd(ISOUtil.padright("",10,'0'),false));

        if (celular != "") {
            mensaje2 = "Se enviará un mensaje a\n su número de celular\n" + "XXXXXX" +
                    celular.substring(6,10);
        }
        return (ventanaConfirmacion("OTP en proceso",mensaje2));
    }

    private boolean OTPverificacion(String code, boolean celCompleto, int intentos) {
        boolean ret = false;
        boolean confirma = false;
        int numIntentos = 0;
        if (celCompleto == false){
            String mensaje1 = "Otp en proceso favor\nrevise su celular";
            confirma = (ventanaConfirmacion("OTP en proceso",mensaje1));
        }else {
            confirma = true;
            numIntentos = 1;
        }
        if (confirma){
            do{
                String titulo = "OTP en proceso";
                String mensaje = "Ingresa código";
                int maxLeng = 10;
                int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;
                int caracReq = 0;
                String codigoOTP = ingresoContrapartida(titulo,mensaje,maxLeng,inputType,caracReq);
                if (!codigoOTP.equals("")){
                    InitTrans.tkn48.setNumero_kit(ISOUtil.str2bcd(ISOUtil.padright(codigoOTP,10,'F'),false));
                    mensajeInicial(code);
                    if (iso8583.getfield(39).equals("66")){
                        String mensaje2 = "El código enviado no fue\n validado correctamente";
                        confirma = (ventanaConfirmacion("OTP en proceso",mensaje2));
                        if (confirma == false){
                            return false;
                        }
                    } else {
                        ret = true;
                    }
                    numIntentos++;
                } else {
                    transUI.showMsgError(timeout, "Operación cancelada por el usuario");
                    ret = false;
                }
            } while (iso8583.getfield(39).equals("66") && numIntentos   < intentos);
        }
        return ret;
    }

    private boolean validaCliente(String code, int numIntentos) {
        boolean ret = false;
        boolean rtaConfirma = false;
        int pago = Integer.parseInt(ISOUtil.bcd2str(InitTrans.tkn38.getFormaDePago(),0,InitTrans.tkn38.getFormaDePago().length));
        if (iso8583.getfield(39).equals("77")){
            if (InitTrans.tkn16.getMsg_largo() == null){
                transUI.showMsgError(timeout, "Error de token 16");
                return false;
            }

            if (pago == 2){
                String mensaje2 = ISOUtil.hex2AsciiStr(ISOUtil.bcd2str(InitTrans.tkn16.getMsg_largo(),0,InitTrans.tkn16.getMsg_largo().length)).trim();
                rtaConfirma =ventanaConfirmacion("Cliente no matriculado",mensaje2);
                if (rtaConfirma){
                    rtaConfirma = ventanaConfirmacion("Código seguridad","Matrícula en proceso");
                    if (rtaConfirma){
                        if (OTPverificacion("450203", false,numIntentos)){
                            ret = true;
                        }
                    }
                }
            } else if (pago == 3){
                if (efectivacionOrdenPago(true)){
                    ret = true;
                }
            }
        } else if (iso8583.getfield(39).equals("66")){
            String mensaje2 = "El código enviado no fue\n validado correctamente";
            boolean confirma = ventanaConfirmacion("OTP en proceso",mensaje2);
            if (confirma){
                if (OTPverificacion(code, true, numIntentos)){
                    ret = true;
                } else {
                    transUI.showMsgError(timeout, "Número de intentos fallidos");
                }
            }
        } else if (iso8583.getfield(39).equals("00")){
            ret = true;
        }
        return ret;
    }


    private boolean ingresoDatosRemesas() {
        boolean ret = false;
        String[] mensajes = {"Número cédula", "Código dactilar (10 caracteres)"};
        int[] maxLengs = {10,10};
        int[] inputTypes = {1010, InputType.TYPE_CLASS_TEXT};
        int[] caracRequeridos = {10,10};
        InputInfo inputInfo = transUI.showIngreso2EditText(mensajes, maxLengs,inputTypes, caracRequeridos, "Remesas internacionales");
        if(inputInfo.isResultFlag()){
            ret = true;
            String msg = inputInfo.getResult();
            String[] msg2 ;
            msg2= msg.split("@");
            InitTrans.tkn27.setCedula(ISOUtil.str2bcd(ISOUtil.padright(msg2[0],16,'F'),false));
            InitTrans.tkn27.setDactilar(msg2[1].getBytes());
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        mensajes = new String[] {"Fecha expedición cédula", "Código remesa"};
        int maxLeng = 20;
        int inputType = InputType.TYPE_CLASS_TEXT;
        int caracRequerido = 0;
        InputInfo inputInfo2 = transUI.show1Fecha1EditText(mensajes, maxLeng,inputType, caracRequerido, "Remesas internacionales");
        if(inputInfo2.isResultFlag()){
            ret = true;
            String msg = inputInfo2.getResult();
            String[] msg2 ;
            msg2= msg.split("@");
            msg2[0] = PAYUtils.StrToDate(msg2[0],"yyyyMMdd","yyyy-MM-dd");
            InitTrans.tkn27.setFechaCedula(msg2[0].getBytes());
            InitTrans.tkn27.setRemesa(ISOUtil.padright(msg2[1],20,' ').getBytes());
        }else{
            ret = false;
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        InitTrans.tkn27.setCelular(ISOUtil.str2bcd(ISOUtil.padright("",12,'F'),false));
        InitTrans.tkn27.setOtp(ISOUtil.str2bcd(ISOUtil.padright("",12,'F'),false));
        return ret;
    }

    private boolean ingresaDatos() {
        boolean ret = false;
        String[] mensajes = {"Número de identificación del beneficiario", "Celular"};
        int[] maxLengs = {10,10};
        int[] inputTypes = {1010, InputType.TYPE_CLASS_NUMBER};
        int[] caracRequeridos = {10,0};
        InputInfo inputInfo = transUI.showIngreso2EditText(mensajes, maxLengs,inputTypes, caracRequeridos, "Ingreso datos beneficiario");
        if(inputInfo.isResultFlag()){
            ret = true;
            String msg = inputInfo.getResult();
            String[] msg2 ;
            msg2= msg.split("@");
            InitTrans.tkn12.setCedula(ISOUtil.padright(msg2[0], 16, 'F').getBytes());
            InitTrans.tkn19.setNumeroControl(ISOUtil.str2bcd(ISOUtil.padright(msg2[1],12,'F'),false));
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    /***
     * fin ventanas de confirmacion en ingreso de datos
     */

    /**
     * metodo para mostrar forma de pago dinamica
     */
    private boolean cuentas(boolean tkn){
        boolean ret = true;
        String[] item = null;
        String[] cod = null;
        int tipoCuenta = -1;
        if (tkn){
            tipoCuenta = Integer.parseInt(ISOUtil.bcd2str(InitTrans.tkn27.getFormaPago(),0,
                    InitTrans.tkn27.getFormaPago().length));
        }else {
            tipoCuenta = Integer.parseInt(ISOUtil.bcd2str(InitTrans.tkn38.getFormaDePago(),0,
                    InitTrans.tkn38.getFormaDePago().length));
        }

        if (tipoCuenta == 1){
            if (tkn){
                item = new String[]{"Efectivo","Transferencia a cuenta"};
            } else {
                item = new String[]{"Efectivo","Débito a cuenta"};
            }
            cod = new String[] {"02","03"};
        } else if (tipoCuenta == 2){
            if (tkn){
                ret = false;
                item = new String[]{"Efectivo"};
                cod = new String[] {"02"};
            } else {
                transUI.showMsgError(timeout, "Transaccion solo clientes \nbanco pichincha");
                return false;
            }
        } else if (tipoCuenta == 3) {
            if (tkn) {
                item = new String[]{"Transferencia a cuenta"};
            } else {
                item = new String[]{"Débito a cuenta"};
            }
            cod = new String[] {"03"};
        } else {
            if (tkn){
                transUI.showMsgError(timeout, "La remesa no puede ser pagada por este canal,\n" +
                        "direccionar al cliente a una agencia");
            } else {
                transUI.showMsgError(timeout, "No se identificaron formas de pago");
            }
            return false;
        }
        if (tkn){
            if (efectivacion(item, cod, "420903", true)){
                ret = true;
            }
        } else {
            int select;
            select = seleccionarMenus(item,cod,"Forma de pago");
            if (select >= 0){
                InitTrans.tkn38.setFormaDePago(ISOUtil.str2bcd(ISOUtil.padleft(cod[select],2,'0'),false));
                ret = true;
            }
        }
        return ret;
    }

    /***
     * show -MASTER CONTROL-
     */
    protected String ingresoContrapartida(String titulo, String mensaje, int maxLeng, int inputType, int carcRequeridos){
        String ret;
        InputInfo inputInfo = transUI.showContrapartida(timeout,mensaje,maxLeng,inputType,carcRequeridos, titulo);
        if(inputInfo.isResultFlag()){
            ret = inputInfo.getResult();
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            ret="";
        }
        return ret;
    }

    protected String ingreso2EditText(String titulo, String[] mensajes, int[] maxLengs, int[] inputType, int[] carcRequeridos){
        String ret;
        InputInfo inputInfo = transUI.showIngreso2EditText(mensajes, maxLengs,inputType, carcRequeridos, titulo);
        if(inputInfo.isResultFlag()){
            ret = inputInfo.getResult();
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            ret="";
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

    protected boolean ingresarMontoVariable(String[] mensajes){
        boolean ret = false;

        InputInfo inputInfo = transUI.showMontoVariable(timeout, mensajes);
        if(inputInfo.isResultFlag()){
            Amount = Integer.parseInt(inputInfo.getResult());
            TotalAmount = Amount;
            para.setAmount(Amount);
            ret = true;
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
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
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }

        return ret;
    }

    protected boolean ventanaConfirmacionRemesa(String[] mensajes, boolean valDireccion){
        boolean ret = false;
        String direccion;
        InputInfo inputInfo = transUI.showMsgConfirmacionRemesa(timeout, mensajes, valDireccion);
        if(inputInfo.isResultFlag()){
            direccion = inputInfo.getResult();
            direccion = Utils.removeSpecial(direccion);
            direccion = ISOUtil.padright(direccion, 40, ' ');
            InitTrans.tkn27.setDireccion(direccion.getBytes());
            ret = true;
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    //********************************************************************************************//

    private boolean efectivacion(String[] itemsT,String[] codItemsT, String proceCode, boolean aIsReversal){
        int select;
        boolean ret=false;
        boolean leeTarjeta = false;
        select = seleccionarMenus(itemsT,codItemsT,"Modalidad de pago");
        InitTrans.tkn27.setFormaPago(ISOUtil.str2bcd(ISOUtil.padleft(codItemsT[select],2,'0'),false));
        para.setNeedPass(true);
        para.setEmvAll(true);
        isSaveLog = true;
        isReversal = aIsReversal;
        if(codItemsT[select].equals("02")) {
            if (leerTarjeta(TARJETA_CNB)) {
                leeTarjeta = true;
            } else {
                transUI.showMsgError(timeout, "Tarjeta no procesada");
            }
        } else if (codItemsT[select].equals("03") ) {
            String[] item = InitTrans.tkn98.getLasCuentas();
            String[] cod = {"01", "02", "03"};
            select = seleccionarMenus(item,cod,"Cuentas beneficiario");
            if (select >= 0) {
                InitTrans.tkn98.setCuentaSeleccionada(item[select]);
                if (leerTarjeta(TARJETA_CNB)) {
                    leeTarjeta = true;
                }else{
                    transUI.showMsgError(timeout, "Tarjeta no procesada");
                }
            }
        }
        if (leeTarjeta) {
            if(mensajeTransaccion(proceCode)){
                if (RspCode.equals("00")){
                    ret=true;
                }else {
                    transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                }
            }
        }
        return ret;
    }

    /**
     * Se crea méotodo cuentasRemesas() para separar la logica de remesas, anteriormente se usaba el
     * método cuentas(boolean tkn) el cual también maneja la logica de la transacción giros nacionales.
     * montoRemesa = obtenemos el valor del monto, quitamos "," para evitar errores en casteo.
     * InitTrans.tkn98.clienteInscrito() = nos permite saber si las cuentas del cliente llegan
     * en 0(beneficiario no cliente del banco), de lo contrario el beneficiario es cliente del banco.
     * efectivacion = realizaremos la transacción de tipo 0200
     *
     * Sí el beneficiario es cliente del banco (cuentas diferente a 0) y el monto es menor o igual a 500,
     * mostraremos las opciones de pago "Efectivo","Tranferencia" con código "02","03", estos datos
     * los sacamos de la documentación; sí el monto es mayor a 500 se mostrara la opción de pago "Tranferencia"
     * con código "03".
     *
     * Sí el beneficiario no es cliente del banco (cuentas en 0) y el monto es menor o igual a 500,
     * mostraremos las opciones de pago "Efectivo" con código "02", si el monto es mayor a 500 se
     * mostrara el mensaje: "La remesa no puede ser pagada por este canal, direccionar al cliente a una agencia"
     * y se termina la transacción.
     * **/

    private boolean cuentasRemesas() {
        String[] item;
        String[] cod;
        double montoRemesa = Double.parseDouble(InitTrans.tkn27.montoRemesa().replace(",",""));
        boolean ret = false;

        if (InitTrans.tkn98.clienteInscrito()) {
            if (montoRemesa <= 500) {
                item = new String[]{"Efectivo","Tranferencia cuenta"};
                cod = new String[]{"02","03"};
            } else {
                item = new String[]{"Tranferencia"};
                cod = new String[]{"03"};
            }
        } else {
            if (montoRemesa <= 500) {
                item = new String[]{"Efectivo"};
                cod = new String[]{"02"};
            } else {
                transUI.showMsgError(timeout, "Remesa no puede ser pagada por este canal,\n" +
                        "direccionar al cliente a la agencia");
                return  false;
            }
        }
        if (item.length > 0 && cod.length > 0) {
            if (efectivacion(item, cod, "420903", true)){
                ret = true;
            }
        } else {
            transUI.showMsgError(timeout, "No se identificaron formas de pago");
            ret = false;
        }
        return ret;
    }
}