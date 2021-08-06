package newpos.libpay.trans.pichincha.Administrativas;

import android.content.Context;
import android.content.Intent;

import com.android.desert.keyboard.InputInfo;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo.ImpresionPantalla;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn92;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.translog.TransLog;
import newpos.libpay.trans.translog.TransLogData;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.SaveData;

public class CuponHistorico extends FinanceTrans implements TransPresenter {

    private InputInfo inputInfo;
    private String itemSelect;
    private String inputTitle;
    String[] menu;
    private Context context;
    private String type;
    TransLogData revesalData;

    public CuponHistorico(Context ctx, String transEname, TransInputPara p, String tipoTrans) {
        super(ctx, transEname);
        para = p;
        transUI = para.getTransUI();
        isReversal = false;
        iso8583.setHasMac(false);
        TransEName = transEname;
        context = ctx;
        tkn92 = new Tkn92();
        type=tipoTrans;
        isProcPreTrans = true;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
        if(type.equals("CUPON_HISTORICO")){
            revesalData = TransLog.getReversal();
            switch(tipoCupon()){
                case 0:
                    ultimoComprobanteLocal();
                    break;
                case 1:
                    if (revesalData != null){
                        int rta = verificaReverso();
                        if( rta != Tcode.T_success ){
                            transUI.showError(timeout,  Tcode.T_reversal_fail);
                        }
                    }
                    tipoCuponHistorico();
                    if (ingresoFecha()){
                        prepareOnline();
                    }
                    break;
                case 2:
                    secuencialUltimaTRX();
                    break;
                case 3:
                    consultaAutomatica();
                    break;
            }
        } else if(type.equals("ULTIMO COMPROBANTE")){
            if(emisionUltimoComprobante()){
                transUI.showSuccess(timeout, Tcode.Mensajes.impresionExitosa,"");
            }
        }
    }

    private boolean secuencialUltimaTRX() {
        boolean ret = false;
        if (!InitTrans.ultimoReciboSecuencial.equals("")){
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("secuencial", "secuencial");
            intent.setClass(context, ImpresionPantalla.class);
            context.startActivity(intent);
        }else{
            transUI.showMsgError(timeout, "No existen recibos almacenados");
        }
        return ret;
    }

    private boolean consultaAutomatica() {
        boolean ret = false;
        if (!SaveData.getInstance().getReciboConsultaAuto().equals("")){
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("secuencial", "conAuto");
            intent.setClass(context, ImpresionPantalla.class);
            context.startActivity(intent);
        }else{
            transUI.showMsgError(timeout, "No existen recibos almacenados");
        }
        return ret;
    }

    private boolean ultimoComprobanteLocal(){
        boolean ret = false;
        if (revesalData == null) {
            if (!InitTrans.ultimoRecibo.equals("NO")){
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, ImpresionPantalla.class);
                context.startActivity(intent);
            }else{
                transUI.showMsgError(timeout, "No existen recibos almacenados");
            }
        }else {
            transUI.showMsgError(timeout, "Funcionalidad deshabilitada por reverso pendiente\nIntente mas tarde");
        }
        return ret;
    }

    private boolean emisionUltimoComprobante(){
        boolean ret = false;
        if(type.equals("ULTIMO COMPROBANTE")){
            if(ingresoFechaAuto()){
                Amount = -1;
                trxEmision();
                ret=true;
            }
        }
        return ret;
    }

    /**
     *
     * @return
     */
    private boolean trxEmision( ) {
        boolean ret = false;
        ProcCode ="930200";
        Field07=null;
        MsgID = "0800";
        EntryMode = "0021";
        Field48 = tkn92.packTkn92();
        RspCode=null;
        Field60=null;
        Field63 = packField63();
        para.setNeedPrint(true);
        if(newPrepareOnline(0)==0){
            ret=true;
        }else{
            transUI.showError(timeout, 149);
        }


        return ret;
    }
    /**
     *
     * @return
     */
    private boolean ingresoFechaAuto(){
        boolean ret = false;
        InputInfo inputInfo = transUI.showFecha(timeout, "Ingrese la fecha de la transacción");
        if (inputInfo.isResultFlag()) {
            String date = inputInfo.getResult();
            if (!date.equals("")) {
                ret = true;
                tkn92.setItemSelect("3");
                tkn92.setInputDate(date);
                tkn92.setInputText("0");

            }
            else {
                transUI.showMsgError(timeout, "Fecha vacía, inicie de nuevo");
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    /**
     *
     * @return
     */
    private int tipoCupon(){

        int ret =-1;
        String[] opciones = {"Emisión último comprobante", "Cupón histórico", "Secuencial última trx"};
        inputInfo = transUI.showBotones(opciones.length, opciones, "Comprobante");
        if (inputInfo.isResultFlag()) {
            itemSelect = inputInfo.getResult();
            if (itemSelect.equals("0")){
                ret = 0;
            }else if (itemSelect.equals("1")){
                ret = 1;
            }
            else if (itemSelect.equals("2")){
                ret = 2;
            }
            else if (itemSelect.equals("3")){
                ret = 3;
            }
        }
        else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;

    }

    /**
     *
     * @return
     */

    private boolean tipoCuponHistorico (){
        boolean ret = false;
        String[] opcionesCupon = {"Documento de transacción", "Número control de transacción"};
        inputInfo = transUI.showBotones(2, opcionesCupon, "Filtro de búsqueda");
        if (inputInfo.isResultFlag()) {
            itemSelect = inputInfo.getResult();
            if (itemSelect.equals("0")){
                itemSelect = "1";
                inputTitle ="Ingresa el documento de transacción";
            }else if (itemSelect.equals("1")){
                itemSelect = "2";
                inputTitle = "Ingresa número de control de transacción";
            }else if (itemSelect.equals("2")){
                itemSelect = "3";
                inputTitle = "Ingresa número de seguimiento";
            }else if (itemSelect.equals("3")){
                itemSelect = "1";
                inputTitle = "Ingresa número de seguimiento";
            }

            ret=true;
        }
        else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return  ret;
    }

    private boolean ingresoFecha(){
        boolean ret = false;
        inputInfo = transUI.show_input_text_date(timeout, "Consulta comprobante histórico", inputTitle);
        if (inputInfo.isResultFlag()) {
            ret = true;
            String[] parts = inputInfo.getResult().split(";");
            tkn92.setInputText(parts[0]);
            tkn92.setInputDate(parts[1]);
            tkn92.setItemSelect(itemSelect);
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    /**
     * prepareOnline
     */
    private void prepareOnline() {
        transUI.newHandling(context.getResources().getString(R.string.procesando) ,timeout , Tcode.Mensajes.conectandoConBanco);
        Amount = -1;
        EntryMode = ISOUtil.padleft(inputMode + "", 2, '0');
        EntryMode = "0021";
        Field48 = tkn92.packTkn92();
        Field48 += InitTrans.tkn93.packTkn93();
        Field63 = packField63();
        Field60 = null;
        setFields();
        para.setNeedAmount(false);
        retVal = newPrepareOnline(inputMode);
        clearPan();
        if (retVal == 0) {
            transUI.showSuccess(timeout, Tcode.Mensajes.operacionRealizadaConExito, "");
        } else {
            transUI.showError(timeout, retVal);
        }
    }
}

