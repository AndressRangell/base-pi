package newpos.libpay.trans.pichincha.financieras.DesembolsoCredito;

import android.content.Context;

import com.android.desert.keyboard.InputInfo;

import java.text.DecimalFormat;

import newpos.libpay.global.TMConfig;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;

public class DesembolsoCredito extends FinanceTrans implements TransPresenter {
    /**
     * 金融交易类构造
     *
     * @param ctx
     * @param transEname
     */

    private int seleccion = 0;

    public DesembolsoCredito(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p ;
        setTraceNoInc(true);
        TransEName = transEname ;
        if(para != null){
            transUI = para.getTransUI();
        }
        isReversal = true;
        isProcPreTrans=true;
    }

    @Override
    public void start() {
        int select = -1;
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
        String contrapartida;
        setFixedDatas();
        String[] items = {"Crédito"};
        String[] codItems = {"01"};
        select =  seleccionarMenus(items,codItems,"Desembolso crédito");
        if (select == 0) {
            String[] items2 = {"Desembolsar"};
            String[] codItems2 = {"01"};
            select = seleccionarMenus(items2,codItems2,"Desembolso crédito");
            if (select >= 0) {
                contrapartida = ingresoContrapartida(items[select],  "Por favor ingrese su cédula de identidad",10, 1010, 10);
                if (!contrapartida.equals("")) {
                    InitTrans.tkn12.setCedula(ISOUtil.padright(contrapartida, 16, 'F').getBytes());
                    if (mensajeInicial()) {
                        if (confirmarCuentas()) {
                            if (confirmarDatos(items, select)) {
                                if (mensajeTransaccion()) {
                                    if (confirmacion()) {
                                        transUI.showSuccess(timeout, Tcode.Mensajes.recaudacionExitosa, "");
                                    } else {
                                        transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                                    }
                                } else {
                                    transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                                }
                            } else {
                                transUI.showError(timeout, Tcode.T_user_cancel_operation);
                            }
                        } else {
                            transUI.showError(timeout, Tcode.T_user_cancel_operation);
                        }
                    } else {
                        transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                    }
                }
            }
        }

    }

    @Override
    public ISO8583 getISO8583() {
        return null;
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


    protected boolean mensajeInicial() {
        boolean ret;
        setFixedDatas();
        Amount = -1;
        MsgID = "0100";
        ProcCode = "420800";
        EntryMode = "0021";
        Field48 = InitTrans.tkn12.packTkn12();
        packField63();
        ret = enviarTransaccion();
        return ret;
    }

    public boolean enviarTransaccion(){
        boolean ret = false;
        int retPrep;
        RRN=iso8583.getfield(37);
        TermID=TMConfig.getInstance().getTermID();
        MerchID=TMConfig.getInstance().getMerchID();
        retPrep=newPrepareOnline(inputMode);
        if(retPrep == 0 || retPrep == 99){
            ret = true;
        }else {
            if (retPrep == 1995) {
                transUI.showMsgError(timeout, "No se obtuvo información de impresión");
            }else if(retPrep == 93){
                mensajeTransaccion();
            }
        }
        return ret;
    }

    private boolean confirmarCuentas () {
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
                ret = true;
            }
        } else {
            ret = true;
        }
        return ret;
    }

    private boolean confirmarDatos(String [] items, int select){
        String[] vistaMensaje = new String[6];
        DecimalFormat formatMinimoRetiro = new DecimalFormat("##,##");
        vistaMensaje[0] = "Cliente : " + ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn49.getNombreCompleto()));
        vistaMensaje[1] =  "";
        vistaMensaje[2] = "No. cédula. " + InitTrans.tkn49.getCedula();
        vistaMensaje[3] = "Monto financiado: " + String.valueOf(formatMinimoRetiro.format(Long.parseLong(InitTrans.tkn26.montos[0])));
        vistaMensaje[4] = "";
        vistaMensaje[5] = items[select];
        return(ventanaConfirmacion(vistaMensaje));
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

    private boolean mensajeTransaccion() {
        boolean ret;
        para.setNeedAmount(true);
        Amount = 0;
        TotalAmount = Amount;
        para.setAmount(Amount);
        setFixedDatas();
        MsgID = "0200";
        ProcCode = "420801";
        RspCode = null;
        Field48 = InitTrans.tkn17.packTkn17(seleccion);
        Field48 += InitTrans.tkn22.packTkn22();
        Field48 += InitTrans.tkn26.packTkn26();
        Field48 += InitTrans.tkn49.packTkn49();
        Field48 += InitTrans.tkn93.packTkn93();
        Field48 += InitTrans.tkn98.packTkn98DesCred();
        if (isPinExist) {
            PIN = emv.getPinBlock();
        }
        Field63 = packField63();
        para.setNeedPrint(true);
        ret = enviarTransaccion();
        return ret;
    }

    private boolean confirmacion() {
        boolean ret = false;
        setFixedDatas();
        clearDataFields();
        RRN=iso8583.getfield(37);
        iso8583.clearData();
        transUI.newHandling("Pago crédito" ,timeout , Tcode.Mensajes.conectandoConBanco);
        MsgID = "0202";
        TermID= TMConfig.getInstance().getTermID();
        MerchID=TMConfig.getInstance().getMerchID();
        ProcCode = "420801";
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
}
