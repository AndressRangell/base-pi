package newpos.libpay.trans.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler.ClsConexion;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.screens.Login;
import newpos.libpay.global.TMConfig;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.translog.TransLog;
import newpos.libpay.trans.translog.TransLogData;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

import com.android.desert.keyboard.InputInfo;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenuPrincipal.checkSettle;
import static newpos.libpay.trans.Tcode.T_user_cancel_operation;

/**
 * Created by zhouqiang on 2017/3/31.
 * 结算交易处理类
 *
 * @author zhouqiang
 */

public class SettleTrans extends FinanceTrans implements TransPresenter {

    private long totalAmountTrans = 0;
    private String datatransType;
    private String dataDescuadre = "";
    private String typeSettle;
    private String usuarioCierreParcial = "";
    private int totalUsers = 0;
    private InputInfo inputInfo;
    private String rol_user;
    private final String CIERRE_PARCIAL = "01";
    private final String CIERRE_TOTAL = "00";
    private final String CIERRE_AUTOMATICO = "02";
    private Usuario personaLogin = Login.persona;
    ClsConexion conexion;
    private String role;
    private String userAdmin;
    private ArrayList<String> arrayUsuarios;
    private ArrayList<Usuario> listaUsuario;

    public SettleTrans(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        iso8583.setHasMac(false);
        para = p;
        transUI = para.getTransUI();
        TransEName = transEname;
        conexion = new ClsConexion(ctx);
        if(TMConfig.getInstance().getmodoOperacion().equals("C")){
            role = "CENTRALIZADO";
        }else   {
            if(checkSettle.isSettleAutomatic() || checkSettle.isCierreCambioOperacion()){
                role = "ADMINISTRADOR";
            }else {
                role = personaLogin.getRole();
            }
        }
        para.setNeedPrint(false);
        isReversal = false;
        isSaveLog = false;
        isDebit = false;
        para.setNeedAmount(false);
        Amount = -1;
        isNeed93 = true;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
        if(checkSettle.isCierreCambioOperacion()){
            transUI.newHandling(context.getResources().getString(R.string.procesando) ,timeout , Tcode.Mensajes.cierePorCambioDeOperacion);
            typeSettle = CIERRE_TOTAL;
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            actualizarBatch();
            checkSettle.setCierreCambioOperacion(false);
            transUI.trannSuccess(timeout, Tcode.Status.settling_succ, "");
            return;
        }

        if (TMConfig.getInstance().getmodoOperacion().equals("C") && TMConfig.getInstance().getsesionIniciada().equals("N")) {
            String cedula = InitTrans.user.getCedula();
            MerchID = cedula.trim();
        }

        transUI.newHandling(context.getResources().getString(R.string.procesando) ,timeout , Tcode.Mensajes.conectandoConBanco);
        if (checkSettle != null) {
            if (!checkSettle.isSettleAutomatic()) {
                if (checkBatch(CIERRE_TOTAL_LOG)) {
                    menuCierre();
                } else {
                    if(TMConfig.getInstance().getmodoOperacion().equals("C")) {
                        menuCierre();
                    }else {
                        transUI.showError(timeout, Tcode.T_batch_empy);
                        return;
                    }
                }
            } else {
                inputInfo = new InputInfo();
                inputInfo.setResultFlag(true);
                inputInfo.setResult("4");
            }
            if (inputInfo.isResultFlag()) {
                switch (inputInfo.getResult()) {
                    case "0": //Cierre Parcial
                        if (role.equals("ADMINISTRADOR")) {
                            InputInfo inputInfo2 = transUI.showListCierre(timeout, CIERRE_PARCIAL);
                            if (inputInfo2.isResultFlag()) {
                                typeSettle = CIERRE_PARCIAL;
                                usuarioCierreParcial = inputInfo2.getResult();
                                if (!checkBatch(usuarioCierreParcial)){
                                    transUI.showError(timeout, Tcode.T_batch_empy);
                                    return;
                                }
                            } else {
                                transUI.showMsgError(timeout, "Operación cancelada por usuario");
                            }
                        } else {
                            typeSettle = CIERRE_PARCIAL;
                            usuarioCierreParcial = MerchID.trim();
                            if (!checkBatch(usuarioCierreParcial)){
                                transUI.showError(timeout, Tcode.T_batch_empy);
                                return;
                            }
                        }
                        break;
                    case "1": //Cierre total o cerrar sesion
                        if (TMConfig.getInstance().getmodoOperacion().equals("C")) {
                            String nameCentra = InitTrans.user.getNombre();
                            String[] mensajes = {"Nombre usuario: ",nameCentra.trim(),"","Desea cerrar sesión?",""};
                            InputInfo opc = transUI.showMsgConfirmacion(timeout,mensajes, false);
                            if(opc.getResult().equals("aceptar")){
                                typeSettle = CIERRE_AUTOMATICO;
                                if (!checkBatch(CIERRE_TOTAL_LOG)){
                                    TMConfig.getInstance().setsesionIniciada("L");
                                    InitTrans.loginCentral=false;
                                    TMConfig.getInstance().setCedula("");
                                    InitTrans.initialization = false;
                                    InitTrans.initEMV = false;
                                    transUI.showSuccess(timeout, Tcode.Mensajes.cierreDeSesionExitoso,"");
                                    return;
                                }
                            }else {
                                transUI.showMsgError(timeout, "Operación cancelada por usuario");
                                return;
                            }
                        } else if(role.equals("ADMINISTRADOR")){
                            typeSettle = CIERRE_TOTAL;
                            para.setNeedConfirmPrint(true);
                        } else {
                            transUI.showMsgError(timeout, "No tiene permisos para realizar cierre total");
                            return;
                        }
                        break;
                    default:
                        typeSettle = CIERRE_AUTOMATICO;
                        break;
                }
                int ret = 0;

                if (!checkSettle.isSettleAutomatic()) {
                    ret = transUI.showMsgConfirm(timeout, "Cierre", "Monto total:    " +
                            PAYUtils.getStrAmount(totalAmountTrans));
                }

                if (ret == 0) {
                    settle();
                } else {
                    transUI.showError(timeout, T_user_cancel_operation);
                }
            } else {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }

        }

    }

    /**
     * settle
     */
    private void settle() {
        transUI.newHandling(context.getResources().getString(R.string.procesando),timeout , Tcode.Mensajes.conectandoConBanco);
        if (enviarCierre()){
            InitTrans.cierreRealizado = true;
            if(checkSettle.isCierreCambioOperacion()){
                checkSettle.setCierreCambioOperacion(false);
            }
            transUI.trannSuccess(timeout, Tcode.Status.settling_succ, "");
        }else {

            transUI.showMsgError(timeout, "Cierre inconcluso, comuniquese con el Call Center del CNB.");
        }
    }

    private boolean enviarCierre() {
        boolean ret = false;
        int rta;
        camposCierre();
        rta = newPrepareOnline(ENTRY_MODE_HAND);
        switch (rta){
            case 0:
                actualizarBatch();
                ret = true;
                break;
            case 93:
                totalUsers = 0;
                ret = enviarCierre();
                break;
            case 94:
                transUI.newHandling(context.getResources().getString(R.string.procesando), timeout, Tcode.Mensajes.cierreDescuadrado);
                while (true) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                transUI.newHandling(context.getResources().getString(R.string.procesando),timeout , Tcode.Mensajes.conectandoConBanco);
                String cantidadUsuarios = sendBatchUpload();
                camposCierreDescuadrado(cantidadUsuarios);
                rta = newPrepareOnline(ENTRY_MODE_HAND);
                switch (rta){
                    case 0:
                        ret = enviarCierre();
                        break;
                    default:
                        transUI.showMsgError(timeout, "Cierre inconcluso, comuniquese con el Call Center del CNB.");
                        break;
                }
                break;
            case 1100:
            case 101:
            case 102:
            case 103:
                transUI.showMsgError(timeout, "No fue posible realizar cierre. Intente mas tarde.");
                break;
            default:
                actualizarBatch();
                transUI.showMsgError(timeout, "Cierre inconcluso, comuniquese con el Call Center del CNB.");
                break;
        }
        return ret;
    }

    private void actualizarBatch() {
        if (typeSettle.equals(CIERRE_TOTAL) || typeSettle.equals(CIERRE_AUTOMATICO)) {
            updateSettle_total();
            actaulizarEstdosEnCerrado();
            if(typeSettle.equals(CIERRE_AUTOMATICO)){
                actaulizarEstdosEnCerrado();
            }
        } else {
            updateSettle_parcial();
        }

        if (typeSettle.equals(CIERRE_AUTOMATICO) && TMConfig.getInstance().getmodoOperacion().equals("C")){
            TMConfig.getInstance().setsesionIniciada("L");
            InitTrans.loginCentral=false;
            TMConfig.getInstance().setCedula("");
            InitTrans.initialization = false;
            InitTrans.initEMV = false;
        }
        actualizarFechaUltimoCierre(usuarioCierreParcial);
    }

    private void actaulizarEstdosEnCerrado() {
        transUI.actualizarEstadoCierre();
    }

    private void camposCierre() {
        iso8583.clearData();
        MsgID = "0500";
        ProcCode = "950200";
        setTraceNoInc(true);
        LocalTime = PAYUtils.getLocalTime();
        LocalDate = PAYUtils.getLocalDate();
        packField48();
        CurrencyCode = null;
        Field60 = null;
        packField63();
        RspCode = null;
        EntryMode = null;
        para.setNeedPrint(true);
    }

    private void camposCierreDescuadrado(String cantiUsuarios) {
        iso8583.clearData();
        MsgID = "0320";
        ProcCode = "950300";
        setTraceNoInc(true);
        LocalTime = PAYUtils.getLocalTime();
        LocalDate = PAYUtils.getLocalDate();
        EntryMode = "0021";
        if (RRN != null) {
            iso8583.setField(37, RRN);
        }else {
            iso8583.setField(37, "000000000000");
        }
        RspCode = null;
        String tok31 = "31";
        String date = PAYUtils.getLocalDateAAAAMMDD();
        String typeSeat = typeSettle;
        String aux48 = date + typeSeat + cantiUsuarios + dataDescuadre;
        int lenBuff = (aux48.length()/2);
        Field48 = tok31 + ISOUtil.padleft(String.valueOf(lenBuff),4,'0') + aux48;
        iso8583.setField(48, Field48);
        CurrencyCode = null;
        Field60 = null;
        packField63();
        para.setNeedPrint(false);
    }

    private void menuCierre() {
        String[] menu;
        if(TMConfig.getInstance().getmodoOperacion().equals("C")){
            menu = new String[]{"Cierre parcial", "Cierre total - Sesión"};
        } else {
            menu = new String[]{"Cierre parcial", "Cierre total"};
        }
        inputInfo = transUI.showBotones(menu.length,menu, "Cierre");
    }

    private boolean getAllUsers() {

        ClsConexion conexion = new ClsConexion(context);
        List<TransLogData> list = TransLog.getInstance(CIERRE_TOTAL_LOG, true).getData();
        List<TransLogData> listAux = null;
        int lenList = list.size();
        if (lenList > 0) {
            ArrayList<Usuario> listUsers = conexion.readUserList();
            //totalUsers = listUsers.size();
            for (int i = 0; i < listUsers.size(); i++) {
                Usuario usuario = listUsers.get(i);
                listAux = new ArrayList<>();
                for (int j = 0; j < lenList; j++) {
                    TransLogData transLogData = list.get(j);
                    try {
                        if (usuario.getUser().trim().equals(transLogData.getMerchID().trim())) {
                            listAux.add(transLogData);
                        }
                    }catch (Exception e){
                        transUI.showMsgError(timeout, "Error al validar usuario en Log Data de transacciones.");
                        return false;
                    }

                }
                try {
                    getDataOrderByProcCode(usuario.getUser(), listAux);
                }catch (Exception e){
                    transUI.showMsgError(timeout,"Error en la validacion de log por usuario");
                    return false;
                }

            }
        }
        return datatransType != null;
    }

    /**
     * Revisa si tiene transacciones y obtiene el monto total
     *
     * @param nameFileLog
     * @return
     */
    private boolean checkBatch(String nameFileLog) {
        totalAmountTrans = 0;
        List<TransLogData> list = TransLog.getInstance(nameFileLog, true).getData();
        int lenList = list.size();
        for (int i = 0; i < list.size(); i++) {
            try {
                totalAmountTrans += list.get(i).getTotalAmount();
            }catch (Exception e){
                transUI.showMsgError(timeout, "Problema en Log Data de transacciones (Suma de montos)");
                return false;
            }
        }
        return lenList > 0;
    }

    private void getDataOrderByProcCode(String merchID, List<TransLogData> list) {

        int totalTypesTrans = 0;
        long totalAmountTrans = 0;
        long totalComisiones = 0;
        int contTrans = 0;
        String headerInfoID = null;
        String infoTrans = null;
        String procCodeActual, procCodeAnterior = null;
        List<TransLogData> listCopy = new ArrayList<>();
        int lenList = 0;

        if (list != null) {
            lenList = list.size();
        }


        if (lenList > 0) {
            totalUsers = totalUsers + 1;
            String aux = ISOUtil.strpad(merchID, 15);
            headerInfoID = ISOUtil.convertStringToHex(aux).replace(" ", "20");
        }

        while (lenList > 0) {
            for (int i = 0; i < list.size(); i++) {
                TransLogData data = list.get(i);
                procCodeActual = data.getProcCode();
                if (procCodeAnterior == null) {
                    procCodeAnterior = procCodeActual;
                    data.setFlagOrderByProcCode(true);
                    list.set(i, data);
                } else if (procCodeActual.equals(procCodeAnterior)) {
                    data.setFlagOrderByProcCode(true);
                    list.set(i, data);
                }
            }
            for (int i = 0; i < list.size(); i++) {
                TransLogData data = list.get(i);
                if (data.isFlagOrderByProcCode()) {
                    contTrans++;
                    try {
                        totalAmountTrans += data.getTotalAmount();
                    }catch (Exception e){
                        transUI.showMsgError(timeout, "Asi es que se canta hijueputa");
                        return;
                    }

                } else {
                    listCopy.add(data);
                }
            }

            list.clear();
            list.addAll(listCopy);
            lenList = lenList - contTrans;
            totalTypesTrans++;

            if (infoTrans == null)
                infoTrans = procCodeAnterior;
            else
                infoTrans += procCodeAnterior;

            infoTrans += ISOUtil.padleft(contTrans + "", 4, '0');
            infoTrans += ISOUtil.padleft(totalAmountTrans + "", 12, '0');
            infoTrans += "000000000000";

            procCodeAnterior = null;
            listCopy.clear();
            contTrans = 0;
            totalAmountTrans = 0;
        }
        if (infoTrans != null && totalTypesTrans > 0) {
            datatransType += headerInfoID + ISOUtil.padleft(totalTypesTrans + "", 4, '0') + infoTrans;
        }
    }

    private void actualizarFechaUltimoCierre(String userCierre) {
        if (typeSettle.equals(CIERRE_PARCIAL)){
            if (TMConfig.getInstance().getmodoOperacion().equals("C")) {
                String nuevaFecha = PAYUtils.getLocalDateCierre();
                SharedPreferences sharedPreferences =  context.getSharedPreferences("userCentralizado",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("fechaUltimoCierre", nuevaFecha);
                editor.apply();
            } else {
                String role = conexion.selectFechaCierreUsuario(usuarioCierreParcial, "user_role", "user");
                if(!role.equals("ADMINISTRADOR")) {
                    String nuevaFecha = PAYUtils.getLocalDateCierre();
                    conexion.updateFields(usuarioCierreParcial.trim(), "user_fechacierre", nuevaFecha,"user");
                }
            }
        }else{
            capturarUsuarios();
            for (int i = 0; i<arrayUsuarios.size(); i++) {
                String nuevaFecha = PAYUtils.getLocalDateCierre();
                conexion.updateFields(arrayUsuarios.get(i), "user_fechacierre", nuevaFecha,"user");
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    private void updateSettle_parcial() {
        if(role.equals("ADMINISTRADOR")) {
            TransLog.getInstance(usuarioCierreParcial, true).clearAll(usuarioCierreParcial);
        }else {
            TransLog.getInstance(MerchID, true).clearAll(MerchID);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void updateSettle_total() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(new Date());
        TMConfig.getInstance().setFechaCierre(date).save();
        TransLog.getInstance(CIERRE_TOTAL_LOG, true).clearAll();
    }

    private void packField48() {
        String auxVar;
        String userActual;
        datatransType = "";

        if(typeSettle.equals(CIERRE_AUTOMATICO)){
            ClsConexion conexion = new ClsConexion(context);
            userActual = conexion.selectFechaCierreUsuario("ADMINISTRADOR","user","user_role")
                    .trim();
        }else {
            userActual  = MerchID.trim();
        }

        String fechaAntCierre;
        if (TMConfig.getInstance().getmodoOperacion().equals("C")) {
            if (typeSettle.equals(CIERRE_TOTAL) || typeSettle.equals(CIERRE_AUTOMATICO)) {
                fechaAntCierre = InitTrans.user.getFechaRegistro();
            }else {
                fechaAntCierre = InitTrans.user.getFechaUltimoCierre();
            }
        }else{
            fechaAntCierre = conexion.selectFechaCierreUsuario(userActual,"user_fechacierre","user");
        }


        String nuevaFecha = PAYUtils.getLocalDateCierre();

        Field48 = typeSettle;
        Field48 += fechaAntCierre; //esperar modulo de usuarios con ultimo cierre
        Field48 += nuevaFecha;

        if (typeSettle.equals(CIERRE_TOTAL) || typeSettle.equals(CIERRE_AUTOMATICO)) {
            if (TMConfig.getInstance().getmodoOperacion().equals("C")) {
                List<TransLogData> list = TransLog.getInstance(CIERRE_TOTAL_LOG, true).getData();
                try {
                    //totalUsers = 1;
                    getDataOrderByProcCode(MerchID, list);
                }catch (Exception e){
                    transUI.showMsgError(timeout,"Error en la validacion de log por usuario");
                    return;
                }
            }else {
                try {
                    if (!getAllUsers()) {
                        transUI.showError(timeout, Tcode.T_batch_empy);
                        return;
                    }
                } catch (Exception e) {
                    transUI.showMsgError(timeout, "Error en la obtencion de usuarios \"Cierre\"");
                    return;
                }
            }
        } else {
            List<TransLogData> list = TransLog.getInstance(usuarioCierreParcial.trim(), true).getData();
            try {
                getDataOrderByProcCode(usuarioCierreParcial.trim(), list);
            }catch (Exception e){
                transUI.showMsgError(timeout,"Error en la validacion de log por usuario");
                return;
            }
        }

        Field48 += datatransType;

        //Field48 += InitTrans.tkn93.packTkn93();
        int lenBuff = 0;
        try {
            lenBuff = (Field48.length()/2)+1;
        }catch (NumberFormatException e){
            transUI.showMsgError(timeout, "Se te jodio el reverso? Llama a batman");
        }


        auxVar = "32" + ISOUtil.padleft(lenBuff + "", 4, '0')
                + ISOUtil.padleft(String.valueOf(totalUsers) + "", 2, '0')
                + Field48
                + InitTrans.tkn93.packTkn93();

        Field48 = auxVar;
    }

    private String sendBatchUpload() {
        String ret;
        int cantProc = ISOUtil.bcd2int(InitTrans.tkn33.getCantTrans(), 0, InitTrans.tkn33.getCantTrans().length);
        String transacciones = ISOUtil.bcd2str(InitTrans.tkn33.getTransBatch(), 0, InitTrans.tkn33.getTransBatch().length);
        String[] usuarios = new String[cantProc];
        String[] procCodes = new String[cantProc];
        int j;
        int x = 0;
        int y = 30;
        for (j = 0; j < cantProc; j++){
            usuarios[j] = ISOUtil.hex2AsciiStr(transacciones.substring(x, x+30)).substring(0,10);
            procCodes[j] = transacciones.substring(y,y+6);
            x = x+36;
            y = y+36;
            System.out.println(usuarios[j]);
            System.out.println(procCodes[j]);
        }

        getInfoData(usuarios, procCodes);

        ret = ISOUtil.padleft(String.valueOf(usuarios.length), 2, '0');

        return ret;
    }

    private boolean getInfoData(String[] usuarios, String[] procCodes) {

        ClsConexion conexion = new ClsConexion(context);
        List<TransLogData> list = TransLog.getInstance(CIERRE_TOTAL_LOG, true).getData();
        List<TransLogData> listAux = null;
        int lenList = list.size();
        if (lenList > 0) {
            ArrayList<Usuario> listUsers = conexion.readUserList();
            //totalUsers = listUsers.size();
            for (int i = 0; i < listUsers.size(); i++) {
                Usuario usuario = listUsers.get(i);
                listAux = new ArrayList<>();
                for (int j = 0; j < lenList; j++) {
                    TransLogData transLogData = list.get(j);
                    if (usuario.getUser().trim().equals(transLogData.getMerchID().trim())) {
                        listAux.add(transLogData);
                        totalUsers = totalUsers+1;
                    }
                }
                for (int y = 0; y < usuarios.length; y++) {
                    if(usuario.getUser().equals(usuarios[y])){
                        obtenerDatosDescuadre(usuario.getUser(), listAux, procCodes[y]);
                    }
                }
            }
        }
        return dataDescuadre != null;
    }

    private void obtenerDatosDescuadre(String merchID, List<TransLogData> list, String procCodes) {

        int totalTypesTrans = 0;
        StringBuilder infoTrans48 = new StringBuilder();
        int contTrans = 0;
        String headerInfoID = null;
        String infoTrans = null;
        String procCodeActual, procCodeAnterior = null;
        List<TransLogData> listCopy = new ArrayList<>();
        int lenList;
        int contRegistros = 0;

        lenList = list.size();

        while (lenList > 0) {
            for (int i = 0; i < list.size(); i++) {
                TransLogData data = list.get(i);
                procCodeActual = data.getProcCode();
                if (procCodeAnterior == null) {
                    procCodeAnterior = procCodeActual;
                    data.setFlagOrderByProcCode(true);
                    list.set(i, data);
                } else if (procCodeActual.equals(procCodeAnterior)) {
                    data.setFlagOrderByProcCode(true);
                    list.set(i, data);
                }
            }
            for (int i = 0; i < list.size(); i++) {
                TransLogData data = list.get(i);
                if (data.isFlagOrderByProcCode()) {
                    if(data.getProcCode().equals(procCodes)) {
                        contRegistros ++;
                        infoTrans48.append(data.getMsgID());
                        infoTrans48.append(data.getProcCode());
                        infoTrans48.append(data.getTraceNo());
                        infoTrans48.append(ISOUtil.padleft(String.valueOf(data.getAmount()), 12, '0'));
                        infoTrans48.append(ISOUtil.convertStringToHex(data.getRRN()));
                    }
                    contTrans++;
                } else {
                    listCopy.add(data);
                }
            }

            list.clear();
            list.addAll(listCopy);
            lenList = lenList - contTrans;
            totalTypesTrans++;

            if (infoTrans == null)
                infoTrans = procCodeAnterior;
            else
                infoTrans += procCodeAnterior;

            infoTrans += ISOUtil.padleft(contTrans + "", 4, '0');
            infoTrans += "000000000000";

            String prueba = infoTrans48.toString();
            System.out.println(prueba);

            procCodeAnterior = null;
            listCopy.clear();
            contTrans = 0;
        }
        if (infoTrans != null && totalTypesTrans > 0) {
            dataDescuadre += ISOUtil.convertStringToHex(ISOUtil.padright(merchID,15,'0')) +
                    ISOUtil.padleft(String.valueOf(contRegistros),4,'0') +
                    infoTrans48.toString();
        }

    }



    public void capturarUsuarios() {
        ClsConexion conexion = new ClsConexion(context);
        listaUsuario = conexion.readUserList();

        if (listaUsuario != null) {
            arrayUsuarios = new ArrayList<String>();

            for (int i=0; i<listaUsuario.size(); i++) {
                arrayUsuarios.add(listaUsuario.get(i).getUser());
            }
        }
    }

}