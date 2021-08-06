package newpos.libpay.trans.pichincha.Recaudaciones;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.desert.keyboard.InputInfo;

import java.lang.reflect.Field;
import java.sql.SQLOutput;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.global.TMConfig;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Recaudaciones.Ant.Ant;
import newpos.libpay.trans.pichincha.Recaudaciones.Atm.AtmGye;
import newpos.libpay.trans.pichincha.Recaudaciones.BonoDesarrolloHumano.PagoBonoDesarrolloHumano;
import newpos.libpay.trans.pichincha.Recaudaciones.CasasComerciales.CasasComerciales;
import newpos.libpay.trans.pichincha.Recaudaciones.CentrosEducativos.CentrosEducativos;
import newpos.libpay.trans.pichincha.Recaudaciones.EmpresasPrivadas.EmpresasPrivadas;
import newpos.libpay.trans.pichincha.Recaudaciones.PagoCredito.PagoCredito;
import newpos.libpay.trans.pichincha.Recaudaciones.PagoServicios.PagoDeServicios;
import newpos.libpay.trans.pichincha.Recaudaciones.Recargas.Recargas;
import newpos.libpay.trans.pichincha.Recaudaciones.TarjetaCredito.TarjetaCredito;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

import static newpos.libpay.utils.ISOUtil.formatMiles;

public class Recaudaciones extends FinanceTrans implements TransPresenter {

    public Context context;
    private String tipoRecau = "";

    private Recargas recargas;
    private EmpresasPrivadas empresasPrivadas;
    private PagoDeServicios pagoDeServicios;
    private PagoBonoDesarrolloHumano pagoBonoDesarrolloHumano;
    private TarjetaCredito tarjetaCredito;
    private CasasComerciales casasComerciales;
    private PagoCredito pagoCredito;
    private Ant ant;

    public static final String NO_APLICA = "NA";

    /**
     * 金融交易类构造
     *
     * @param ctx
     * @param transEname
     */
    public Recaudaciones(Context ctx, String transEname, String tipoTrans, TransInputPara p) {
        super(ctx, transEname);
        Pan = "0000000000000000";
        context = ctx;
        tipoRecau = tipoTrans;
        para = p;
        setTraceNoInc(true);
        TransEName = transEname ;
        isProcPreTrans = true; // Valida si existe reverso para evacuar antes de enviar cualquier transaccion
        isReversal = false;
        isSaveLog = false;
        if(para != null){
            transUI = para.getTransUI();
        }
        isReversal = false;
        isSaveLog = false;
        isProcSuffix = true;

    }

    @Override
    public void start() {
        iniciarTransacciones();
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName + " " + tipoRecau);
        try {
            switch (tipoRecau) {
                case "RecargaOperadores":
                    recargas.recargaOperadores();
                    break;
                case "TelevisionPrepago":
                    recargas.televisionPrepago();
                    break;
                case "TelevisionPagada":
                    recargas.televisionPagada();
                    break;
                case "ServicioAguaPotable":
                    pagoDeServicios.servicioAguaPotable();
                    break;
                case "ServicioLuzElectrica":
                    pagoDeServicios.servicioLuzElectrica();
                    break;
                case "VentaCatalogo":
                    empresasPrivadas.ventaCatalogo();
                    break;
                case "BonoDesarrolloHumano":
                    pagoBonoDesarrolloHumano.bono();
                    break;
                case "RecaudacionEmpresa":
                    empresasPrivadas.recaudacionEmpresas();
                    break;
                case "TarjetaCredito":
                    tarjetaCredito.tarjetaCredito();
                    break;
                case "CasasComerciales":
                    casasComerciales.casasComerciaes();
                    break;
                case "PagoCredito":
                    pagoCredito.pagoCredito();
                    break;
                case "ANT":
                    ant.ant();
                    break;
                case "CentrosEducativos":
                    CentrosEducativos centrosEducativos = new CentrosEducativos(context,Type.RECAU,tipoRecau,para);
                    centrosEducativos.centrosEducativos();
                    break;
                case "ATM":
                    AtmGye atmGye = new AtmGye(context,Type.RECAU,tipoRecau,para);
                    atmGye.pagoAtm();
                    break;
                case "Teléfono/planes":
                    pagoDeServicios.servicioTelefonico();
                    break;
                case "Internet":
                    pagoDeServicios.internet();
                    break;
            }
        }catch (NullPointerException e){
            Log.e("Error", "Null "+e);
        }


    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    private void iniciarTransacciones() {
        recargas = new Recargas(context,Type.RECAU,tipoRecau,para);
        empresasPrivadas = new EmpresasPrivadas(context,Type.RECAU,tipoRecau,para);
        pagoDeServicios = new PagoDeServicios(context,Type.RECAU,tipoRecau,para);
        pagoBonoDesarrolloHumano = new PagoBonoDesarrolloHumano(context,Type.RECAU,tipoRecau,para);
        tarjetaCredito = new TarjetaCredito(context,Type.RECAU,tipoRecau,para);
        casasComerciales = new CasasComerciales(context,Type.RECAU,tipoRecau,para);
        pagoCredito = new PagoCredito(context,Type.RECAU,tipoRecau,para);
        ant = new Ant(context,Type.RECAU,tipoRecau,para);

    }

    /**
     * Funcion mensajeInicial()
     * Crea los campos necesarios para una consulta inicial de MsgID 0100
     * @param code recibe el ProcCode del mensaje
     * @return retorna boleano true si el envio de la transaccion funciono correctamente.
     */
    protected boolean mensajeInicial(String code) {
        boolean ret;
        setFixedDatas();
        String newCode = code.substring(0, 5);
        if (!code.equals("490111") && !newCode.equals("46031")) {
            Amount = -1;
            TotalAmount = Amount;
        } else {
            RspCode = null;
        }
        para.setAmount(Amount);
        MsgID = "0100";
        ProcCode = code;
        EntryMode = "0021";
        Field48 = InitTrans.tkn03.packTkn03(true);
        if (code.equals("460303") || code.equals("460305")) {
            Field48 += InitTrans.tkn13.packTkn13();
        }
        if (code.equals("490111") || newCode.equals("46031")) {
            RspCode = null;
            Field48 += InitTrans.tkn06.packTkn06();
        }
        if (code.equals("460313")) {
            Field48 += InitTrans.tkn13.packTkn13();
        }
        packField63();
        ret = enviarTransaccion();
        return ret;
    }

    protected int seleccionarMenus(String[] items, String[] codItems, String Msg){
        int ret = -1;
        //InputInfo inputInfo = transUI.showMenu(items,codItems,Msg);
        InputInfo inputInfo = transUI.showBotones(items.length, items, Msg);
        if(inputInfo.isResultFlag()){
            ret = inputInfo.getErrno();
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    protected String ingresoEmprePubli(String mensaje[], int maxLeng[], int inputType, int carcRequeridos){
        String ret;
        InputInfo inputInfo = transUI.showEmpresasPrediosView(timeout,mensaje,maxLeng,inputType,carcRequeridos);
        if(inputInfo.isResultFlag()){
            ret = inputInfo.getResult();
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            ret="";
        }
        return ret;
    }

    protected String ingresoBonoDesarrollo(String titulo, String mensaje[], int maxLeng[], int inputType, int carcRequeridos){
        String ret;
        InputInfo inputInfo = transUI.showBonoDesarrollo(timeout, titulo, mensaje,maxLeng,inputType,carcRequeridos);
        if(inputInfo.isResultFlag()){
            ret = inputInfo.getResult();
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            ret="";
        }
        return ret;
    }

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

    protected String ingresoContrapartidaRecaud(String titulo, String mensaje, int maxLeng, int inputType, int carcRequeridos){
        String ret;
        InputInfo inputInfo = transUI.showContrapartidaRecaud(timeout,mensaje,maxLeng,inputType,carcRequeridos, titulo);
        if(inputInfo.isResultFlag()){
            ret = inputInfo.getResult();
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            ret="";
        }
        return ret;
    }

    protected String ingresoDocumento(){
        String ret = null;
        InputInfo inputInfo = transUI.showIngresoDocumento(timeout);
        if(inputInfo.isResultFlag()){
            ret = inputInfo.getResult();
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            ret = "";
        }

        return ret;
    }

    /*
    Retorna : true/false
     */
    public boolean enviarTransaccion(){
        boolean ret = false;
        int retPrep;
        if (!ProcCode.substring(0,4).equals("4930")){
            RRN=iso8583.getfield(37);
        }
        TermID=TMConfig.getInstance().getTermID();
        MerchID=TMConfig.getInstance().getMerchID();
        retPrep=newPrepareOnline(inputMode);
        if(retPrep == 0 || retPrep == 99){
            ret = true;
        }else {
            if (retPrep == 1995) {
                if (!ProcCode.substring(0,4).equals("4930")){
                    transUI.showMsgError(timeout, "No se obtuvo información de impresión");
                } else {
                    ret = true;
                }
            }
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
        } else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }

        return ret;
    }

    protected boolean msgEfectivacion(String code, boolean tipoPago) {
        boolean ret;
        setFixedDatas();
        MsgID = "0200";
        ProcCode = code;
        RspCode = null;
        if (code.substring(0,4).equals("4930")){
            iso8583.setField(37, null);
            RRN = null;
            AuthCode = null;
        }
        Field48 = InitTrans.tkn03.packTkn03(tipoPago);
        Field48 += InitTrans.tkn06.packTkn06();
        if(tipoPago) {
            Field48 += InitTrans.tkn09.packTkn09();
        }
        if(code.substring(0,5).equals("49402")){
            Field48 += InitTrans.tkn13.packTkn13();
            Field48 += InitTrans.tkn17.packTkn17(0);
        }
        if (code.equals("493022")) {
            Field48 += InitTrans.tkn13.packTkn13();
        }
        Field48 += InitTrans.tkn43.packTkn43(inputMode, Track2);
        armar59();
        Field63 = packField63();
        para.setNeedPrint(true);
        isSaveLog = true;
        ret = enviarTransaccion();
        return ret;
    }
    protected boolean confirmacionRecaudo(String code, String title) {
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
    protected boolean ingresarMontoVariable(String[] mensajes){
        boolean ret = false;

        InputInfo inputInfo = transUI.showMontoVariable(timeout, mensajes);
        if(inputInfo.isResultFlag()){
            //Comision = Long.parseLong(ISOUtil.bcd2str(InitTrans.tkn06.getCosto_servicio(), 0, InitTrans.tkn06.getCosto_servicio().length));
            Amount = Integer.parseInt(inputInfo.getResult());
            TotalAmount = Amount;
            para.setAmount(Amount);
            //Amount += Comision;
            ret = true;
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }

        return ret;
    }

    protected boolean ventanaConfirmacionPagCredito(String[] mensajes){
        boolean ret = false;

        InputInfo inputInfo = transUI.showConfirmacion(timeout, mensajes);
        if(inputInfo.isResultFlag()){
            Amount = Long.parseLong(inputInfo.getResult());
            TotalAmount = Amount;
            para.setAmount(Amount);
            ret = true;
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }

        return ret;
    }

    /**
     * Funcion enviarConsulta
     * Crea los campos necesarios para una consulta inicial de MsgID 0100
     * @param code Código de proceso
     * @return
     */
    protected boolean enviarConsulta(String code) {
        boolean ret;
        setFixedDatas();

        RspCode = null;
        MsgID = "0100";
        ProcCode = code;
        EntryMode = "0021";

        setTokens48(code, true, false);

        packField63();
        ret = newPrepareOnlineRecaudos(code, false);
        return ret;
    }

    /**
     * Función montoVariable
     * Muestra en pantalla la vista para el ingreso del monto variable
     * @param titulo Título a colocar en la pantalla
     * @param valores Array de información recibida para mostrar en pantalla
     * @param mensajes Array de subtítulos
     * @return
     */
    protected boolean montoVariable(String titulo, String[] valores, String[] mensajes){
        boolean ret;
        String [] finales = new  String[valores.length];
        String[] vistaMensaje = new String[valores.length + 1];
        vistaMensaje[0] = mensajes[0] + valores[0];
        vistaMensaje[1] = mensajes[1] + valores[1];
        for (int i = 2; i < finales.length; i ++) {
            finales[i] = formatMiles(Integer.parseInt(valores[i]));
            vistaMensaje[i] = mensajes[i] + finales[i];
        }
        vistaMensaje[4] = titulo;
        ret=ingresarMontoVariable(vistaMensaje);
        return(ret);
    }

    /**
     * Función validacionesPago
     * Muestra pantalla para la confirmación de los datos del pago
     * @param titulo Título de la transacción
     * @param valores Array de información recibida para mostrar en pantalla
     * @param mensajes Array de subtítulos
     * @return
     */
    protected boolean validacionesPago(String titulo, String[] valores, String[] mensajes){
        String tipoPago = ISOUtil.bcd2str(InitTrans.tkn39.getFlagVarFijo(),0, InitTrans.tkn39.getFlagVarFijo().length);
        int i;
        boolean ret = true;
        if (!iso8583.getfield(3).equals(proCodes.recauCNTTVConsulta) &&
                !iso8583.getfield(3).equals(proCodes.recauMicrocobrosConsulta) &&
                !iso8583.getfield(3).equals(proCodes.recauCNTMOVILConsulta) && tipoPago.equals("4E")) {
            ret = montoVariable(titulo, valores, mensajes);
            if (ret) {
                int code = Integer.parseInt(iso8583.getfield(3)) + 10;
                if (!enviarConsulta(String.valueOf(code))) {
                    transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                }
            } else {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }
        }

        if(!ret){
            return false;
        }

        if (!iso8583.getfield(3).equals(proCodes.recauMicrocobrosConsulta)){
            valores[2] = ISOUtil.bcd2str(InitTrans.tkn06.getValor_documento(), 0, InitTrans.tkn06.getValor_documento().length);
            valores[3] = ISOUtil.bcd2str(InitTrans.tkn06.getCosto_servicio(), 0, InitTrans.tkn06.getCosto_servicio().length);
            valores[4] = ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length);
        }


        String[] finales = new  String[valores.length];
        String[] vistaMensaje = new String[valores.length + 1];

        if(iso8583.getfield(3).equals(proCodes.recauCNTMOVILConsulta) || iso8583.getfield(3).equals(proCodes.recauCNTTVConsulta)){
            vistaMensaje[0] = "Empresa : " + ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn06.getNomEmpresa()));
            vistaMensaje[1] = "No. " + ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn03.getContrapartida())).trim();
            vistaMensaje[2] = "Monto : " + PAYUtils.getStrAmount(Amount);
            vistaMensaje[3] = "";
            vistaMensaje[4] = "¿Deseas pagar?";
            vistaMensaje[5] = titulo;
        }else if (iso8583.getfield(3).equals(proCodes.recauMicrocobrosConsulta)){
            vistaMensaje[0] = "Empresa : " + valores[0];
            vistaMensaje[1] = "Rubro : " + valores[1];
            vistaMensaje[2] = "Valor :  " + PAYUtils.getStrAmount(Long.parseLong(valores[2]));
            vistaMensaje[3] = "";
            vistaMensaje[4] = valores[3];
            vistaMensaje[5] = valores[4];
            if (!valores[2].isEmpty()){

                Amount = Long.parseLong(valores[2]);
            }

        }else{
            finales[0] = valores[0];
            vistaMensaje[0] = mensajes[0] + finales[0];
            finales[1] = valores[1];
            vistaMensaje[1] = mensajes[1] + finales[1];
            try{

                for (i = 2; i < finales.length; i ++) {
                    finales[i] = formatMiles(Integer.parseInt(valores[i]));
                    vistaMensaje[i] = mensajes[i] + finales[i];
                }
                vistaMensaje[4] += " \n\n¿Desea Pagar?";
                vistaMensaje[5] = titulo;

            }catch (Exception e){
                Log.i("validacionesPago", e.getMessage());
            }
        }

        return(ventanaConfirmacion(vistaMensaje));
    }

    /**
     * Función newPrepareOnlineRecaudos
     * Envía la transacción
     * @return
     */
    protected boolean newPrepareOnlineRecaudos(String proCodes, boolean isDebitoCUenta){
        boolean ret = false;
        int retPrep;

        if(RRN != null && !RRN.equals(NO_APLICA)){
            RRN=iso8583.getfield(37);
        }

        TermID=TMConfig.getInstance().getTermID();
        MerchID=TMConfig.getInstance().getMerchID();
        retPrep=newPrepareOnline(inputMode);
        if(retPrep == 0){
            ret = true;
        }else {
            if(retPrep == 93){
                iso8583.setField(39,null);
                RspCode = null;
                setTokens48(proCodes,false, isDebitoCUenta);
               /* ret = newPrepareOnlineRecaudos(proCodes, isDebitoCUenta);*/
            }else if (retPrep == 1995) {
                if (!ProcCode.substring(0,4).equals("4930")){
                    transUI.showMsgError(timeout, "No se obtuvo información de impresión");
                } else {
                    ret = true;
                }
            }
        }
        return ret;
    }

    /**
     * Función enviarEfectivacion
     * Crea los campos necesarios para una Efectivación MsgID 0200 y envía Confirmación MsgID 0202 finalizando la transacción
     * @param codProceso Código de proceso
     * @param titulo Título de la transacción
     * @param isDebitoCuenta Flag para empaquetar el token 09 en el campo 48 dependiendo del modo de pago seleccionado
     * @return
     */
    private void enviarEfectivacion(String codProceso, String titulo, boolean isDebitoCuenta) {
        setFixedDatas();
        MsgID = "0200";
        ProcCode = codProceso;
        RspCode = null;

        setTokens48(codProceso, false, isDebitoCuenta);
        Field63 = packField63();

        para.setNeedPrint(true);
        isSaveLog = true;
        if (newPrepareOnlineRecaudos(codProceso, isDebitoCuenta)) {
            if(confirmacionRecaudo(codProceso, titulo)){
                transUI.showSuccess(timeout , Tcode.Mensajes.recaudacionExitosa, "");
            }
        } else {
            transUI.showMsgError(timeout, "Error en recaudo");
        }
    }

    /**
     * Función setTokens48
     * Arma el campo 48 dependiendo del código de proceso de la transacción
     * Se debe agregar case con código de proceso si se requiere enviar algún token que no esté en el método
     * @param codProceso Código de proceso de la recaudación
     * @param esConsulta Flag para saber si la transacción en de tipo consulta o efectivación, en base a esto se arma en campo 48
     * @param isDebitoCuenta Flag para empaquetar el token 09 en el campo 48 dependiendo del modo de pago seleccionado
     */
    private void setTokens48(String codProceso, boolean esConsulta, boolean isDebitoCuenta){
        if(esConsulta){
            Field48 = InitTrans.tkn03.packTkn03(true);
            switch (codProceso) {

                case proCodes.recauUTPLConsulta:
                case proCodes.recauITSCOConsulta:
                case proCodes.recauPYCCAConsulta:
                //case proCodes.recauCNTFIJOConsulta:
                //case proCodes.recauCNTPLANESConsulta:
                    Field48 += InitTrans.tkn13.packTkn13();
                    break;

                case proCodes.recauUTPLMontoVariable:
                case proCodes.recauITSCOMontoVariable:
                case proCodes.recauPYCCAMontoVariable:
                case proCodes.recauATMontoVariableCitaciones:
                    Field48 += InitTrans.tkn06.packTkn06();
                    Field48 += InitTrans.tkn13.packTkn13();
                    break;

                case proCodes.recauCNTFIJOMontoVariable:
                case proCodes.recauCNTPLANESMontoVariable:
                    /*Field48 += InitTrans.tkn06.packTkn06();
                    break;*/

                case proCodes.recauATMontoVariableSolicitud:
                case proCodes.recauATMontoVariableRenovacion:
                case proCodes.recauATMontoVariableRodaje:
                case proCodes.recauCNTtvpagadaMontoVariable:
                case proCodes.recauCNTINTERNETMontoVariable:
                    Field48 += InitTrans.tkn06.packTkn06();
                    break;

                case proCodes.recauATMConsultaCitaciones:
                    Field48 += InitTrans.tkn13.packTkn13();
                    Field48 += InitTrans.tkn40.packTokenAtm();
                    break;

                case proCodes.recauATMConsultaRenovacion:
                case proCodes.recauATMConsultaSolicitud:
                case proCodes.recauATMConsultaRodaje:
                    Field48 += InitTrans.tkn40.packTokenAtm();
                    break;
            }
        }else {
            Field48 = InitTrans.tkn03.packTkn03(isDebitoCuenta);

            if (!proCodes.recauCNTTVEfectivacion.equals(codProceso) &&
                !proCodes.recauCNTMOVILEfectivacion.equals(codProceso)) {
                Field48 += InitTrans.tkn06.packTkn06();
            }
            if (isDebitoCuenta) {
                Field48 += InitTrans.tkn09.packTkn09();
            }

            RspCode = null;

            if (!proCodes.recauATMEfectivacionSolicitud.equals(codProceso) &&
                !proCodes.recauATMEfectivacionRenovacion.equals(codProceso) &&
                !proCodes.recauATMEfectivacionRodaje.equals(codProceso) &&
                    !proCodes.recauMicrocobrosEfectivacion.equals(codProceso)){

                Field48 += InitTrans.tkn13.packTkn13();
            }

            switch (codProceso)
            {
                case proCodes.recauUTPLEfectivacion:
                    Field48 += InitTrans.tkn17.packTkn17Byte(0);
                    break;

                case proCodes.recauATMEfectivacionCitaciones:
                case proCodes.recauATMEfectivacionSolicitud:
                case proCodes.recauATMEfectivacionRodaje:
                case proCodes.recauATMEfectivacionRenovacion:
                    Field48 += InitTrans.tkn40.packTokenAtm();
                    break;
            }

            Field48 += InitTrans.tkn43.packTkn43(inputMode, Track2);
            //Field48 += InitTrans.tkn93.packTkn93();
        }
    }

    /**
     * Función mostrarRealizarEfectivacion
     * Envía datos a mostrar en pantalla y procesa la transacción solicitando tajeta, PIN y finaliza la transacción
     * @param titulo Título de la transacción
     * @param codProceso Código de proceso de la recaudación
     * @param valores Array con la información a mostrar en pantalla para confirmar
     */
    String[] mensajes;
    protected void mostrarRealizarEfectivacion(String titulo, String codProceso, String[] valores, boolean isReversal, boolean comisionMonto) {
        if (codProceso.equals(proCodes.recauMicrocobrosEfectivacion)){
            mensajes = new String[] {"Empresa : ", "Rubro : ", "Total : "};

        }else {
            mensajes = new String[] {"Empresa : ", "Nombre : ", "Monto : ", "Servicio : ", "Total : "};
        }

        if (validacionesPago(titulo, valores, mensajes)) {

            String[] item = {"Efectivo", "Débito cuenta"};
            String[] cod = {"01", "02"};
            String montoVariable = ISOUtil.bcd2str(InitTrans.tkn39.getFlagVarFijo(),0, InitTrans.tkn39.getFlagVarFijo().length);
            String montoTotal;

            if(montoVariable.equals("4E") && Amount!=-1){
                //montoTotal = String.valueOf(Amount);

                if (comisionMonto) {
                    Comision = Long.parseLong(ISOUtil.bcd2str(InitTrans.tkn06.getCosto_servicio(), 0, InitTrans.tkn06.getCosto_servicio().length));
                    montoTotal = String.valueOf(Amount + Comision);
                } else {
                    montoTotal = String.valueOf(Amount);
                }

            } else {
                montoTotal = ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length);
            }

            try {
                procesarEfectivacionRecaudo(montoTotal, item, cod, isReversal, codProceso, titulo);
            } catch (Exception e) {
                e.printStackTrace();
                transUI.showMsgError(timeout, "Error de Monto");
            }

        } else {
            transUI.showError(timeout , Tcode.T_user_cancel_operation);
        }
    }

    /**
     * Función procesarEfectivacionRecaudo
     * Procesar la tarjeta, seleccionar tipo de cuenta si es requerido y proceder a enviar la efectivación
     * @param monto => Monto a enviar
     * @param itemsT => Array de opciones de pago
     * @param codItemsT => Array de código de opciones de pago
     * @param isReversal => Flag para el reverso
     * @return
     */
    protected void procesarEfectivacionRecaudo(String monto, String[] itemsT ,String[] codItemsT, boolean isReversal, String code, String titulo) throws Exception {
        int select;
        boolean leeTarjeta = false;
        boolean tipoPago = false;
        if (code.equals(proCodes.recauMicrocobrosEfectivacion)){
            select = 0;
        }else {
            select = seleccionarMenus(itemsT,codItemsT,"Modalidad pago");
        }

        Amount = Long.parseLong(monto);
        TotalAmount = Amount;
        para.setAmount(Amount);
        para.setNeedPass(true);
        para.setEmvAll(true);
        this.isReversal = isReversal;
        isSaveLog = true;
        if (select == 0) {
            if (leerTarjeta(TARJETA_CNB)) {
                leeTarjeta = true;
                tipoPago = false;
            }else{
                transUI.showMsgError(timeout, "Tarjeta no procesada");
            }
        } else if (select == 1 ){
            String[] item = {"Cuenta corriente", "Cuenta ahorros", "Cuenta básica"};
            String[] cod = {"01", "02", "03"};
            select = seleccionarMenus(item,cod, context.getResources().getString(R.string.tipoDeCuenta));
            if (select >= 0) {
                InitTrans.tkn09.setSb_typeAccount(ISOUtil.str2bcd(cod[select], true));
                if (leerTarjeta(TARJETA_CLIENTE)) {
                    leeTarjeta = true;
                    tipoPago = true;
                }else{
                    transUI.showMsgError(timeout, "Tarjeta no procesada");
                }
            }
        }

        if (leeTarjeta) {
            enviarEfectivacion(code, titulo, tipoPago);
        }
    }

}
