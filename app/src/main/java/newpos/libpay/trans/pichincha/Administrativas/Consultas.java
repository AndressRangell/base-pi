package newpos.libpay.trans.pichincha.Administrativas;

import android.content.Context;
import android.text.InputType;

import com.android.desert.keyboard.InputInfo;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.utils.ISOUtil;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn09;

import static newpos.libpay.utils.ISOUtil.formatMiles;

/*****************************************************************************
 * Clase:      Consultas
 * Descripcion:  Clase encargada de trx relacionadas con consulta como
 *               consulta de saldo y 10 ultimos movimientos
 * Autor: John Osorio C.
 * **************************************************************************/
public class Consultas extends FinanceTrans implements TransPresenter {

    private byte[] respData;
    private int timeOutScreensInit = 5 * 1000;
    public static Boolean error = false;
    private String TipoConsulta;
    private enum typeTrx {
        SALDOENCUENTA, TENLASTMOVES,SALDOENCUENTA_CNB,TENLASTMOVES_CNB, DIARIOTRANS, VENTAPROD, CONSULTA_FACTURAS;
    }

    public Consultas(Context ctx, String transEname, TransInputPara p, String consulta) {
        super(ctx, transEname);
        TipoConsulta = consulta;
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
        String codItem;
        String tipoTran;
        typeTrx tipoShow = typeTrx.valueOf(TipoConsulta);
        switch (tipoShow) {
            case SALDOENCUENTA:
            case TENLASTMOVES:
                ProcCode=(tipoShow.ordinal()==0)?"390100":"390200";
                Amount = -1;
                TotalAmount = Amount;
                para.setAmount(Amount);
                codItem=(tipoShow.ordinal()==0)?"01":"02";
                tipoTran=(tipoShow.ordinal()==0)?"SALDOENCUENTA":"TENLASTMOVES";
                if(consSaldoImpreso(tipoTran,codItem)){
                    transUI.showSuccess(timeOutScreensInit, Tcode.Mensajes.consultaExitosa, "");
                }
                break;

            case SALDOENCUENTA_CNB:
                saldoEnCuentaCNB();

                break;
            case TENLASTMOVES_CNB:
              tenLastMovesCNB();
                break;
            case DIARIOTRANS:
                if (consultaDiarioTrans()){
                    transUI.showSuccess(timeOutScreensInit, Tcode.Mensajes.consultaExitosa, "");
                }
                break;
            case VENTAPROD:
                if (consultaVentaProductos()){
                    transUI.showSuccess(timeOutScreensInit, Tcode.Mensajes.consultaExitosa, "");
                }
                break;
            case CONSULTA_FACTURAS:
                consultaFacturas();
                break;
        }
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }
    /*************************************************************
     *  Descripcion:Muestra un menu con los tipos de cuentas que
     *  se especifican por parametros a
     *  Argumento: N/A
     *  Retorno:  Codigo de Cuenta seleccionado o -1 (CANCEL)
     * ***********************************************************/
    private boolean consSaldoImpreso(String type, String codItem) {
        boolean ret = false;
        para.setNeedPrint(false);
        if(!consultaInicial(type)){
            return false;
         }
         para.setNeedPrint(true);
             if(tipoCuenta(type)>=0) {
                    if (costoServicio()) {
                        if (Amount > 0) {
                             para.setNeedAmount(true);
                             para.setAmount(Amount);
                             isReversal = false;
                             isSaveLog=true;
                        }
                    }else{
                        return false;
                    }
                 ProcCode = "39" + codItem + "01";
                 if (trxConsulta(type)) {
                    ret = true;
                 }else{
                    return false;
                 }
                 if(ProcCode=="390300"){
                    para.setNeedPrint(false);
                 }
             }
        return ret;
     }



    private void consultaFacturas(){
        String res;
        int res2 = 0;
        ProcCode="436000";
        InputInfo inputInfo = transUI.showContrapartida(timeout, "Fecha de consulta\n(AAAA)", 4, InputType.TYPE_CLASS_NUMBER, 4, "Consulta de facturas");
        if(inputInfo.isResultFlag()){
            res = inputInfo.getResult();
            inputInfo = transUI.showContrapartida(timeout, "Fecha de consulta\n(MM)", 2, 12, 2, "Consulta de facturas");
            if (inputInfo.isResultFlag()) {
                res2 = Integer.parseInt(inputInfo.getResult());
                if (res2 < 13 && res2 > 0) {
                    if(!res.equals("") && res2!=0) {
                        res += String.valueOf(res2);
                        InitTrans.tkn60.clean();
                        InitTrans.tkn60.setAAAAMM(ISOUtil.str2bcd(res, false));
                        if(consultaInicial("CONSULTA_FACTURA_CNB")) {
                            para.setNeedPrint(true);
                            if (mostrarConsultaFacturas()) {
                                transUI.showSuccess(timeout, Tcode.Mensajes.operacionRealizadaConExito, "");
                            }
                        }
                    }
                } else {
                    transUI.showMsgError(timeout, "Mes ingresado no válido");
                }
            } else {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }
        }
        else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            res="";
        }
    }

     /*************************************************************
     *  Descripcion:Realiza la lectura de tarjeta y muestra en
     *  pantalla los datos de la trx
     *  se especifican por parametros a
     *  Argumento: N/A
     *  Retorno:  N/A
     * ***********************************************************/
    private void saldoEnCuentaCNB(){
        ProcCode="390300";
        if(leerTarjeta(TARJETA_CNB)){
            para.setNeedPrint(false);
            if(consultaInicial("SALDOENCUENTA_CNB")) {
                //mostrar en pantalla datos
                if (mostrarConsultaSaldoCnb()) {
                    transUI.showSuccess(timeOutScreensInit, Tcode.Mensajes.consultaExitosa, "");
                }
                else {
                    transUI.showError(timeout, Tcode.T_user_cancel_operation);
                }
            }
        }else{
            transUI.showMsgError(timeout, "Tarjeta no procesada");
        }
    }

    /*************************************************************
     *  Descripcion:Muestra un menu con los tipos de cuentas que
     *  se especifican por parametros a
     *  Argumento: N/A
     *  Retorno:  Codigo de Cuenta seleccionado o -1 (CANCEL)
     * ***********************************************************/
    private int tipoCuenta(String tipoTrans) {
        int ret=-1;

        String[] cuentas = {"Cuenta corriente", "Cuenta ahorros", "Cuenta básica", "Tarjeta prepago"};
        String[] codCuenta = {"01", "02", "03", "04"};
        int numBotones = (tipoTrans.equals("TENLASTMOVES")) ? 3 : 4;
        InputInfo inputInfo = transUI.showBotones(numBotones, cuentas, "Tipo de cuenta consultas");
        if (inputInfo.isResultFlag()) {
            InitTrans.tkn09 = new Tkn09();
            InitTrans.tkn09.setSb_typeAccount(ISOUtil.str2bcd(codCuenta[inputInfo.getErrno()], true));
            ret = Integer.parseInt(inputInfo.getResult());
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }

        return ret;
    }
    /*************************************************************************
     *  Descripcion:Setea los campos y envia la transaccion Consulta
     *  Argumento:  type: Tipo de Consulta-En Saldo / 10 Ultimos Movimientos
     *  Retorno:    Codigo de Cuenta seleccionado o -1 (CANCEL)
     * ***********************************************************************/
    private boolean trxConsulta(String type) {
        boolean ret = false;
        if(leerTarjeta(TARJETA_CLIENTE)){
            Field07=null;
            MsgID = "0200";
            Field48 = InitTrans.tkn09.packTkn09()+ InitTrans.tkn43.packTkn43(inputMode, Track2);
            RspCode=null;
            armar59();
            if(newPrepareOnline(0)==0){
                ret=true;
            }
        }else{
            transUI.showMsgError(timeout, "Tarjeta no procesada");
        }
        return ret;
    }

    /*******************************************************************
     *  Descripcion:Muestra una pantalla con el costo del Servicio
     *  Argumento:  N/A
     *  Retorno:    Tecla presionada ENTER o CANCEL
     * *****************************************************************/
    private boolean costoServicio(){
        boolean ret=false;
        InputInfo inputInfo = transUI.showConsultas(timeout, "COSTOSERVICIO",Amount );
        if (inputInfo.isResultFlag()) {
            ret=true;
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    /*******************************************************************
     *  Descripcion:Muestra una pantalla con los datos de la consulta
     *  Argumento:  N/A
     *  Retorno:    Array de mansajes
     * *****************************************************************/

    private boolean mostrarConsultaSaldoCnb() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");//dd/MM/yyyy
        Date nowDate = new Date();
        String strDate = dateFormat.format(nowDate);

        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");//HH//mm
        Date nowTime = new Date();
        String strTime = sdfDate.format(nowTime);

        String[] valores = new String[4];
        String[] mensaje = new String[6];

        valores[0] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn47.getNombre()));

        String auxField61 = String.valueOf(iso8583.getfield(61));
        String Field61 = auxField61.substring(8);
        Field61 = String.valueOf(ISOUtil.hex2AsciiStr(Field61));
        String[] datos = Field61.split("@");
        String cuenta = datos[0].substring(9);
        String disponible = datos[1].substring(9);
        String contable = datos[2].substring(9);

        mensaje[0] = "Fecha " + strDate + "  Hora " + strTime;
        mensaje[1] = "Nombre : " + valores[0];
        mensaje[2] = "Cuenta : " + cuenta;
        mensaje[3] = "Disponible : " + disponible;
        mensaje[4] = "Contable : " + contable;
        mensaje[5] = "Saldo en cuenta CNB";

        return (ventanaConfirmacion(mensaje));
    }

    private boolean mostrarConsultaFacturas() {
        boolean ret = false;
        int i;
        String[] valores = new String[15];
        String[] mensaje = new String[7];
        String[] mensajes = {"Num transacciones : ", "Fecha acreditación : ", "Valor a pagar : ", "Estado : ", "Motivo : ", "No. cuenta : ", "No. factura : ",
                             "Fecha emisión : ", "Base imponible : ", "1201% iva : ", "Valor total : ", "808% Retención iva : ", "1000% Ret. servicio : "};
        String valor, base, iva1201, total, retencion, servicio, fechaAcre, fechaEmi;

        valor = ISOUtil.bcd2str(InitTrans.tkn60.getVlrPago(), 0, InitTrans.tkn60.getVlrPago().length);
        fechaAcre = ISOUtil.bcd2str(InitTrans.tkn60.getFechaAcredita(), 0, InitTrans.tkn60.getFechaAcredita().length);
        fechaEmi = ISOUtil.bcd2str(InitTrans.tkn60.getFechaEmision(), 0, InitTrans.tkn60.getFechaEmision().length);
        base = ISOUtil.bcd2str(InitTrans.tkn60.getBaseImponible(), 0, InitTrans.tkn60.getBaseImponible().length);
        iva1201 = ISOUtil.bcd2str(InitTrans.tkn60.getIva(), 0, InitTrans.tkn60.getIva().length);
        total = ISOUtil.bcd2str(InitTrans.tkn60.getValorTotal(), 0, InitTrans.tkn60.getValorTotal().length);
        retencion = ISOUtil.bcd2str(InitTrans.tkn60.getRetencionIva(), 0, InitTrans.tkn60.getRetencionIva().length);
        servicio = ISOUtil.bcd2str(InitTrans.tkn60.getRetencionServicio(), 0, InitTrans.tkn60.getRetencionServicio().length);

        valores[0] = ISOUtil.bcd2str(InitTrans.tkn60.getNumTxs(), 0, InitTrans.tkn60.getNumTxs().length);
        valores[1] = fechaAcre.substring(0, 4) + "-" + fechaAcre.substring(4, 6) + "-" + fechaAcre.substring(6, 8);
        valores[2] = formatMiles(Integer.parseInt(valor));
        valores[3] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn60.getEstado()));
        valores[4] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn60.getMotivo()));
        valores[5] = ISOUtil.bcd2str(InitTrans.tkn60.getCuenta(), 0, InitTrans.tkn60.getCuenta().length);
        valores[6] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn60.getFactura()));
        valores[7] = fechaEmi.substring(0, 4) + "-" + fechaEmi.substring(4, 6) + "-" + fechaEmi.substring(6, 8);
        valores[8] = formatMiles(Integer.parseInt(base));
        valores[9] = formatMiles(Integer.parseInt(iva1201));
        valores[10] = formatMiles(Integer.parseInt(total));
        valores[11] = formatMiles(Integer.parseInt(retencion));
        valores[12] = formatMiles(Integer.parseInt(servicio));

        mensaje[0] = mensajes[0] + valores[0];
        mensaje[1] = mensajes[1] + valores[1];
        mensaje[2] = mensajes[2] + valores[2];
        mensaje[3] = mensajes[3] + valores[3];
        mensaje[4] = "";

        if (ventanaConfirmacion(mensaje)) {
            mensaje[0] = mensajes[4] + valores[4];
            mensaje[1] = mensajes[5] + valores[5];
            mensaje[2] = mensajes[6] + valores[6];
            mensaje[3] = mensajes[7] + valores[7];
            mensaje[4] = "";
            if (ventanaConfirmacion(mensaje)) {
                mensaje[0] = mensajes[8] + valores[8];
                mensaje[1] = mensajes[9] + valores[9];
                mensaje[2] = mensajes[10] + valores[10];
                mensaje[3] = mensajes[11] + valores[11];
                mensaje[4] = "";
                if (ventanaConfirmacion(mensaje)) {
                    Arrays.fill(mensaje, null);
                    mensaje[0] = mensajes[12] + valores[12];
                    if (ventanaConfirmacion(mensaje)) {
                        ret = true;
                    }
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
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }

        return ret;
    }

    /*************************************************************
     *  Descripcion:Lee tarjeta y envia trx de consulta sobre los
     *  10 ultimos movimientos CNB, Imprime recibo.
     *  Argumento: N/A
     *  Retorno:  N/A
     * ***********************************************************/
    private void tenLastMovesCNB(){
        ProcCode="390400";
        if(leerTarjeta(TARJETA_CNB)){
            para.setNeedPrint(true);
            if(consultaInicial("TENLASTMOVES_CNB")) {
                transUI.showSuccess(timeOutScreensInit, Tcode.Mensajes.consultaExitosa, "");
            }
            else {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }
        }else{
            transUI.showMsgError(timeout, "Tarjeta no procesada");
            return;
        }

    }
    /*******************************************************************
     *  Descripcion:Realiza la consulta inicial donde llega el costo del
     *              Servicio
     *  Argumento:  type: Tipo de Consulta- En Saldo / 10 Ultimos Movimientos
     *  Retorno:    Resultado del Proceso TRUE/FALSE
     * *****************************************************************/
    private boolean consultaInicial(String type) {
        boolean ret = false;
        //setFixedDatas();
        TransEName = type;
        MsgID = "0100";
        Field07=null;
        EntryMode = "0021";
        Field63=packField63();
        if (ProcCode=="390300" || ProcCode=="390400") {
            Amount= -1;
            TotalAmount = Amount;
            EntryMode = "0051";
            Field48 = InitTrans.tkn43.packTkn43(inputMode, Track2);
            armar59();
        }
        else if (ProcCode=="436000") {
            Field48 = InitTrans.tkn43.packTkn43(inputMode, Track2);
            Field48 += InitTrans.tkn60.packTkn60();
            inputMode = 0;
            Amount = -1;
            TotalAmount = Amount;
        }
        if(newPrepareOnline(inputMode)==0){
            if(ProcCode=="390100" || ProcCode=="390200"){
                Amount=Long.valueOf(iso8583.getfield(4));
                TotalAmount = Amount;
            }
            ret = true;
        }
        return ret;
    }

    /**
     *
     * @return
     */
    private boolean consultaDiarioTrans() {
        boolean ret = false;
        String fechaHoraInicial = capturaFechaHora(true);
        if(!fechaHoraInicial.equals("CANCEL")){
            InitTrans.tkn91.setUpdateDateTimePos(ISOUtil.hex2byte(ISOUtil.convertStringToHex(fechaHoraInicial)));
            String fechaHoraFinal = capturaFechaHora(false);
            if(!fechaHoraInicial.equals("CANCEL")){
                InitTrans.tkn91.setFlagAltServer(ISOUtil.hex2byte(ISOUtil.convertStringToHex(fechaHoraFinal)));
                if(trxConsultaDiarioTrans("930100",InitTrans.tkn91.packTkn91Diario())){
                    ret = true;
                    transUI.showSuccess(timeout, Tcode.Mensajes.informeCorrecto, "");
                }
            }
        }


        return ret;
    }

    /**
     *
     * @param flag
     * @return
     */
    private String capturaFechaHora(boolean flag) {
        String ret = "CANCEL";
        InputInfo inputInfo = transUI.showFechaHora(timeout,"",flag);
        if(inputInfo.isResultFlag()){
            return inputInfo.getResult();
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    /**
     *
     * @param procCode
     * @param tok91
     * @return
     */
    private boolean trxConsultaDiarioTrans(String procCode, String tok91){
        boolean ret = false;
        int rta;
        transUI.newHandling("Diario de transacción",timeout , Tcode.Mensajes.transaccionEnProceso);
        setFixedDatas();
        Amount = -1;
        TotalAmount = Amount;
        MsgID = "0800";
        ProcCode = procCode;
        Field07=null;
        EntryMode = "0021";
        if (procCode.equals("930100")){
            iso8583.setField(39,null);
            RspCode = null;
        }
        Field48 = tok91;
        Field48 += InitTrans.tkn93.packTkn93();
        Field63=packField63();
        para.setNeedPrint(true);
        rta = newPrepareOnline(0);
        if(rta==0){
            ret=true;
        }else if(rta==93){
            ret = trxConsultaDiarioTrans(procCode,tok91);
        }
        transUI.newHandling("Prueba", timeout, Tcode.Mensajes.impresionExitosa);
        return ret;
    }

    /**
     *
     * @param procCode
     * @param tok91
     * @return
     */
    private boolean trxConsultaVentaTrans(String procCode, String tok91){
        boolean ret = false;
        int rta;
        transUI.newHandling("Diario de transacción",timeout, Tcode.Status.handling);
        setFixedDatas();
        Amount = -1;
        TotalAmount = Amount;
        MsgID = "0800";
        AcquirerID = null;

        CurrencyCode = null;
        ProcCode = procCode;
        Field07=null;
        EntryMode = null;
        Field48 = tok91;
        Field48 += InitTrans.tkn93.packTkn93();
        Field63 = packField63();
        para.setNeedPrint(true);
        rta = newPrepareOnline(0);
        if(rta==0){
            ret=true;
        }else if(rta==93){
            ret = trxConsultaDiarioTrans(procCode,tok91);
        }
        return ret;
    }

    /**
     *
     * @return
     */
    private boolean consultaVentaProductos(){
        boolean ret = false;
        if(ingresoCodProducto()){
            String fechaHoraInicial = capturaFechaHora(true);
            if(!fechaHoraInicial.equals("CANCEL")){
                InitTrans.tkn91.setUpdateDateTimePos(ISOUtil.hex2byte(ISOUtil.convertStringToHex(fechaHoraInicial)));
                String fechaHoraFinal = capturaFechaHora(false);
                if(!fechaHoraInicial.equals("CANCEL")){
                    InitTrans.tkn91.setFlagAltServer(ISOUtil.hex2byte(ISOUtil.convertStringToHex(fechaHoraFinal)));
                    if(trxConsultaVentaTrans("930300",InitTrans.tkn91.packTkn91Venta())){
                        ret = true;
                        transUI.showSuccess(timeout, Tcode.Mensajes.informeCorrecto, "");
                    }
                }
            }
        }
        return ret;
    }

    private boolean ingresoCodProducto() {
        boolean ret = false;
        InputInfo inputInfo = transUI.showContrapartida(timeout,"Ingresa el código de producto",6,InputType.TYPE_CLASS_NUMBER, 0, "Ingreso Contrapartida");
        if(inputInfo.isResultFlag()){
            InitTrans.tkn91.setLenMsgInit(ISOUtil.hex2byte(ISOUtil.convertStringToHex(inputInfo.getResult())));
            ret = true;
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }

        return ret;
    }
}

