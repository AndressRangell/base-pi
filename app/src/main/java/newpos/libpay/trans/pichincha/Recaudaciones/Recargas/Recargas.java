package newpos.libpay.trans.pichincha.Recaudaciones.Recargas;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;

import cn.desert.newpos.payui.master.MasterControl;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn92;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Recaudaciones.Recaudaciones;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

import static newpos.libpay.utils.ISOUtil.formatMiles;

public class Recargas extends Recaudaciones {

    private static Context mcontext;
    Context context;
    private String contrato = "Contrato";

    public Recargas(Context ctx, String transEname, String tipoTrans, TransInputPara p) {
        super(ctx, transEname, tipoTrans, p);
        isPinExist=true;
        context = ctx;
        tkn92 = new Tkn92();
        TransEName = transEname;
        iso8583.setHasMac(false);
        para = p;
        transUI = para.getTransUI();
        isReversal = false;
    }

    public void recargaOperadores() {
        transUI.newHandling("Recarga celular" ,timeout , Tcode.Mensajes.conectandoConBanco);
        int select;
        String contrapartida;
        String[] items = {"Movistar","Claro","Cnt","Tuenti","Emisión último comprobante"};
        String[] codItems = {"01","02","03","04","05"};
        select =  seleccionarMenus(items,codItems, context.getResources().getString(R.string.selecionaLaOperador));
        if (select == 4){
            //countDownTimer.cancel();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[14]);
            intent.putExtra(MasterControl.tipoTrans, "ULTIMO COMPROBANTE");
            context.startActivity(intent);
        }
        else if (select <= 3){
            InitTrans.tkn03.setCodigoEmpresa( ISOUtil.padleft(codItems[select],10,'0').getBytes());
            contrapartida = ingresoContrapartida(items[select],  "Número telefónico",10, InputType.TYPE_CLASS_NUMBER, 10);
            if(contrapartida.length() == 10) {
                if (confirmarNumeroTelefonico(contrapartida, items[select])) {
                    InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida, 34, ' ').getBytes());
                    if (select == 2) {
                        //InitTrans.tkn03.setCodigoEmpresa(ISOUtil.padleft(codItems[select],10,'0').getBytes());
                        Amount = -1;
                        TotalAmount = Amount;
                        para.setAmount(Amount);
                        InitTrans.tkn17.clean();
                        if (enviarConsulta(proCodes.recauCNTMOVILConsulta)) {
                            InitTrans.tkn13.setTipoPago(ISOUtil.str2bcd("01",false));

                            if (ISOUtil.byte2int(InitTrans.tkn17.getNumItems()) > 1) {
                                multiFacturas();
                            }
                            mostrarRealizarEfectivacion(items[select],proCodes.recauCNTMOVILEfectivacion, setValoresTkn17(),false,false);
                        }
                        return;
                    }
                    if (mensajeInicial("460101")) {

                        if (confirmarPago(contrapartida, items[select])) {
                            String[] item = {"Efectivo", "Débito cuenta"};
                            String[] cod = {"01", "02"};
                            if (efectivacion(codItems[select], item, cod, "460121")) {
                                if (confirmacion("460121", "Recarga Operadores")) {
                                    transUI.showSuccess(timeout, Tcode.Mensajes.recargaExitosa, "");
                                }
                            }
                        } else {
                            transUI.showError(timeout, Tcode.T_user_cancel_operation);
                        }
                    }
                }
            }else {
                transUI.showError(timeout , Tcode.T_user_cancel_operation);
            }
        }
    }

    private void multiFacturas(){
        String[] itemsV = InitTrans.tkn17.items;
        String[] codItemsV = InitTrans.tkn17.codItem;
        byte[][] valoresItems = InitTrans.tkn17.valorItem;

        if(itemsV == null){
            transUI.showMsgError(timeout, "Error de token 17");
        }else{
            int seleccionarMenu = seleccionarMenus(itemsV, codItemsV, InitTrans.tkn17.titulo);
            Amount = ISOUtil.bcd2int(valoresItems[seleccionarMenu], 0, valoresItems[seleccionarMenu].length);
            ISOUtil.bcd2str(InitTrans.tkn06.getCosto_servicio(), 0, InitTrans.tkn06.getCosto_servicio().length);
            Comision = Long.parseLong(ISOUtil.bcd2str(InitTrans.tkn06.getCosto_servicio(), 0, InitTrans.tkn06.getCosto_servicio().length));
            TotalAmount = Amount + Comision;
            para.setAmount(Amount);

            InitTrans.tkn17.setTkn17(codItemsV[seleccionarMenu], itemsV[seleccionarMenu], InitTrans.tkn17.titulo, valoresItems[seleccionarMenu]);
        }
    }


    public void televisionPrepago(){
        int select;
        String contrapartida;
        String[] items = {"Direct tv","Tv cable","Univisa","Claro","Cnt tv","Emisión último comprobante"};
        String[] codItems = {"01","02","03","04","05","05"};
        select =  seleccionarMenus(items,codItems, context.getResources().getString(R.string.selecionaLaOperador));
        if (select == 4) {
            recargaCntTv(items[select], codItems[select]);
        }
        else if (select == 5){
            //countDownTimer.cancel();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[14]);
            intent.putExtra(MasterControl.tipoTrans, "ULTIMO COMPROBANTE");
            context.startActivity(intent);
        }
        else if (select <= 3){
            InitTrans.tkn03.setCodigoEmpresa( ISOUtil.padleft(codItems[select],10,'0').getBytes());
            if (select == 1){
                contrapartida = ingresoContrapartida(items[select],  "Ruc/Cédula",13, InputType.TYPE_CLASS_NUMBER, 0);
            }else {
                contrapartida = ingresoContrapartida(items[select],  "Número smart card",20, InputType.TYPE_CLASS_NUMBER, 0);
            }
            if(!contrapartida.equals("")){
                InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida,34,' ').getBytes());
                if (mensajeInicial("4602"+codItems[select])){
                    int codEmpresa = Integer.parseInt(codItems[select]);
                    codEmpresa = codEmpresa+20;
                    if(confirmarPago(contrapartida,items[select])) {
                        String[] item = {"Efectivo", "Débito cuenta"};
                        String[] cod = {"01", "02"};
                        if(efectivacion(codItems[select], item, cod, "4602"+String.valueOf(codEmpresa))){
                            if (confirmacion("4602" + String.valueOf(codEmpresa), "Televisión Prepago")) {
                                transUI.showSuccess(timeout, Tcode.Mensajes.recargaExitosa, "");
                            }
                        }
                    }else {
                        transUI.showError(timeout , Tcode.T_user_cancel_operation);
                    }
                }
            }else {
                transUI.showError(timeout , Tcode.T_user_cancel_operation);
            }
        }
    }

    public void televisionPagada(){
        int select;

        String ulitmocomprobante = "ULTIMO COMPROBANTE";
        String proceCode = "";
        String contrapartida = "";
        String[] items = {"Tv cable","Direc tv","Univisa","Claro tv satelital", "Claro fijo","Cnt tv pagada","Emisión último comprobante"};
        String[] codItems = {"01","03","02","04","05","06","07"};
        String[] codEmresa = {"01","02","03","04","05","06","07"};
        select =  seleccionarMenus(items,codItems, context.getResources().getString(R.string.selecionaLaOperador));

        if (select <= 5) {
            InitTrans.tkn03.setCodigoEmpresa(ISOUtil.padleft(codEmresa[select], 10, '0').getBytes());
        }
            switch (select){
                case 0:
                case 2:
                case 3:
                case 5:
                    contrapartida = ingresoContrapartida(items[select],  contrato,20, InputType.TYPE_CLASS_NUMBER, 0);
                    if (select == 5){
                        proceCode = "460306";
                    }
                    break;
                case 1:
                    contrapartida = tvpagadaDirectv(items[select]);
                    break;
                case 4:
                    contrapartida = tvpagadaClaroFijo(items[select]);
                    break;

                case 6:
                    emisionUltimoComprobante(ulitmocomprobante);
                    break;
                    default:
                        break;

            }
        if (select <= 4) {
            if (!contrapartida.equals("")) {
                InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida, 34, ' ').getBytes());
                if (mensajeInicial("4603" + codItems[select])) {
                    int codEmpresa = Integer.parseInt(codItems[select]);
                    codEmpresa = codEmpresa + 20;
                    String codeVariable = "4603" + (codItems[select]);
                    codeVariable = String.valueOf(Integer.parseInt(codeVariable) + 10);
                    if (confirmarPagoCat(items, select, codeVariable)) {
                        String[] item = {"Efectivo", "Débito cuenta"};
                        String[] cod = {"01", "02"};
                        if (efectivacion(codItems[select], item, cod, "4603" + String.valueOf(codEmpresa))) {
                            if (confirmacion("4603" + String.valueOf(codEmpresa), "Televisión pagada")) {
                                transUI.showSuccess(timeout, Tcode.Mensajes.recaudacionExitosa, "");
                            }
                        }
                    } else {
                        transUI.showError(timeout, Tcode.T_user_cancel_operation);
                    }
                }
            } else {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }
        }else {
            if (!contrapartida.equals("")){
                InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida, 34, ' ').getBytes());
                transConfirmacionPago(items[select],proceCode);
            }

        }

    }


    private void transConfirmacionPago(String titulo, String proceCode) {
        Amount = -1;
        if (enviarConsulta(proceCode)){

            mostrarRealizarEfectivacion(titulo,proCodes.recauCNTtvpagadaEfectivacion,setValores(),true,false);

        } else {
            transUI.showMsgError(timeout, "Error en consulta");
        }

    }

    private void emisionUltimoComprobante(String titulo) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, MasterControl.class);
        intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[14]);
        intent.putExtra(MasterControl.tipoTrans, titulo);
        context.startActivity(intent);
    }

    private String tvpagadaClaroFijo(String item) {
        String info = null;
        String[] opcion = {context.getResources().getString(R.string.cedula),context.getResources().getString(R.string.ruc),context.getResources().getString(R.string.pasaporte)};
        String[] codOpcion = {"01","02","06"};
        int opcClaro =  seleccionarMenus(opcion,codOpcion,"Claro fijo");
        switch (opcClaro){
            case 0:
                info = ingresoContrapartida(item,  context.getResources().getString(R.string.cedula),10, 1010, 10);
                break;
            case 1:
                info = ingresoContrapartida(item,  context.getResources().getString(R.string.ruc),13, 1010, 13);
                break;
            case 2:
                info = ingresoContrapartida(item,  context.getResources().getString(R.string.pasaporte),9,
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 9);
                break;
                default:
                    break;
        }
        InitTrans.tkn13.setTipoPago(ISOUtil.str2bcd(codOpcion[opcClaro], false));
        return info;
    }

    private String tvpagadaDirectv(String item) {
        String identificacion = "Identificación";
        String info = null;
        String[] opcion = {"Identificación","Cod. servicio"};
        String[] codOpcion = {"01","02"};
        int opc =  seleccionarMenus(opcion,codOpcion,"Direc tv");
        switch (opc){
            case 0:
                info = ingresoContrapartida(item,  identificacion,20, InputType.TYPE_CLASS_NUMBER, 0);
                break;
            case 1:
                info = ingresoContrapartida(item,  contrato,20, InputType.TYPE_CLASS_NUMBER, 0);
                break;
                default:
                    break;
        }
        InitTrans.tkn13.setTipoPago(ISOUtil.str2bcd(codOpcion[opc], false));
        return info;
    }

    private void recargaCntTv(String titulo, String codEmpre) {
        String contrapartida = ingresoContrapartida(titulo, "Contrato",10, InputType.TYPE_CLASS_NUMBER, 0);
        setFieldsTokens(codEmpre, contrapartida);
        InitTrans.tkn17.clean();
        if (!contrapartida.equals("") && enviarConsulta(proCodes.recauCNTTVConsulta)) {
            InitTrans.tkn13.setTipoPago(ISOUtil.str2bcd("01",false));

            if (ISOUtil.byte2int(InitTrans.tkn17.getNumItems()) > 1) {
                multiFacturas();
            }

            mostrarRealizarEfectivacion(titulo, proCodes.recauCNTTVEfectivacion, setValoresTkn17(),false,false);
        }
    }

    private void setFieldsTokens(String codEmpre, String contrapartida){
        InitTrans.tkn03.setCodigoEmpresa(ISOUtil.padleft(codEmpre.substring(1),10,'0').getBytes());
        InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida,34,' ').getBytes());
        Amount = -1;
        TotalAmount = Amount;
        para.setAmount(Amount);
    }

    private String[] setValores(){
        String[] valores = new String[5];
        valores[0] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn06.getNomEmpresa()));
        valores[1] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn06.getNomPersona()));
        valores[2] = ISOUtil.bcd2str(InitTrans.tkn06.getValor_documento(), 0, InitTrans.tkn06.getValor_documento().length);
        valores[3] = ISOUtil.bcd2str(InitTrans.tkn06.getCosto_servicio(), 0, InitTrans.tkn06.getCosto_servicio().length);
        valores[4] = ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length);

        return valores;
    }

    private String[] setValoresTkn17(){
        String[] valores = new String[5];
        valores[0] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn06.getNomEmpresa()));
        valores[1] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn06.getNomPersona()));

        if (ISOUtil.byte2int(InitTrans.tkn17.getNumItems()) > 1) {
            InitTrans.tkn06.setValor_documento(ISOUtil.str2bcd(ISOUtil.padleft(String.valueOf(Amount), 12, '0'), false));
            InitTrans.tkn06.setCosto_servicio(ISOUtil.str2bcd(ISOUtil.padleft(String.valueOf(Comision), 12, '0'), false));
            InitTrans.tkn06.setValor_adeudado(ISOUtil.str2bcd(ISOUtil.padleft(String.valueOf(TotalAmount), 12, '0'), false));
        }

        valores[2] = ISOUtil.bcd2str(InitTrans.tkn06.getValor_documento(), 0, InitTrans.tkn06.getValor_documento().length);
        valores[3] = ISOUtil.bcd2str(InitTrans.tkn06.getCosto_servicio(), 0, InitTrans.tkn06.getCosto_servicio().length);
        valores[4] = ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length);

        return valores;
    }

    private boolean efectivacion(String codItem,String[] itemsT,String[] codItemsT, String proceCode){
        int select;
        boolean ret=false;
        boolean tipoPago = false;
        boolean leeTarjeta = false;
        select = seleccionarMenus(itemsT,codItemsT,"Modalidad pago");
        para.setNeedPass(true);
        para.setEmvAll(true);
        isSaveLog = true;
        if(select == 0) {
            if (leerTarjeta(TARJETA_CNB)) {
                leeTarjeta = true;
                tipoPago = false;
            } else {
                transUI.showMsgError(timeout, "Tarjeta no procesada");
            }
        } else if (select == 1 ) {
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
            //Amount = Long.parseLong(codItem);
            if(mensajeTransaccion(proceCode, tipoPago)){
                ret=true;
            }
        }
        return ret;
    }

    private boolean confirmarPagoCat(String [] items, int select2, String code){
        String tipoPago = ISOUtil.bcd2str(InitTrans.tkn39.getFlagVarFijo(),0, InitTrans.tkn39.getFlagVarFijo().length);
        int i;
        if(tipoPago.equals("4E")){
            if (montoVariable(items, select2)) {
                if (!mensajeInicial(code)){
                    transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                }
            } else {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }
        }
        String[] finales = new  String[5];
        String[] valores = new String[5];
        String[] vistaMensaje = new String[6];
        String[] mensajes = {"Empresa : ", "Nombre : ", "Monto : ", "Cargo : ", "Total : "};
        valores[0] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn06.getNomEmpresa()));
        valores[1] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn06.getNomPersona()));
        valores[2] = ISOUtil.bcd2str(InitTrans.tkn06.getValor_documento(), 0, InitTrans.tkn06.getValor_documento().length);
        valores[3] = ISOUtil.bcd2str(InitTrans.tkn06.getCosto_servicio(), 0, InitTrans.tkn06.getCosto_servicio().length);
        valores[4] = ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length);
        finales[0] = valores[0];
        vistaMensaje[0] = mensajes[0] + finales[0];
        finales[1] = valores[1];
        vistaMensaje[1] = mensajes[1] + finales[1];
        for (i = 2; i < finales.length; i ++) {
            finales[i] = formatMiles(Integer.parseInt(valores[i]));
            vistaMensaje[i] = mensajes[i] + finales[i];
        }
        vistaMensaje[5] = items[select2];

        Amount = Long.parseLong(ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length));
        TotalAmount = Amount;
        para.setAmount(Amount);

        return(ventanaConfirmacion(vistaMensaje));
    }

    private boolean confirmarPago(String contrapartida, String operador){
        int select = -1;
        String[] vistaMensaje = new String[6];
        String[] itemsV = InitTrans.tkn17.items;
        String[] codItemsV = InitTrans.tkn17.codItem;
        byte[][] valoresItems = InitTrans.tkn17.valorItem;
        if(itemsV == null){
            transUI.showMsgError(timeout, "Error de token 17");
            return false;
        }else{
            select = seleccionarMenus(itemsV,codItemsV,InitTrans.tkn17.titulo);
            Amount = ISOUtil.bcd2int(valoresItems[select], 0, 2);
            Comision = 0;
            TotalAmount = Amount + Comision;
            para.setAmount(Amount);
        }
        if(select >= 0){

            vistaMensaje[0] = "Empresa : " + ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn06.getNomEmpresa()));
            vistaMensaje[1] = "No. " + contrapartida;
            vistaMensaje[2] = "Monto : " + PAYUtils.getStrAmount(Amount);
            vistaMensaje[3] = "¿Deseas pagar?";
            vistaMensaje[4] = "";
            vistaMensaje[5] = operador;
            return(ventanaConfirmacion(vistaMensaje));
        }
        return false;
    }

    private boolean confirmarNumeroTelefonico(String numero, String operador) {
        String[] vistaMensaje = new String[6];
        vistaMensaje[1] = "Confirma el número de teléfono:";
        vistaMensaje[2] = numero;
        vistaMensaje[5] = operador;
        return(ventanaConfirmacion(vistaMensaje));
    }

    private boolean mensajeTransaccion(String code, boolean tipoPago) {
        boolean ret;
        setFixedDatas();
        MsgID = "0200";
        ProcCode = code;
        RspCode = null;
        Field48 = InitTrans.tkn03.packTkn03(tipoPago);
        if (Integer.parseInt(code) > 460300) {
            Field48 += InitTrans.tkn06.packTkn06();
        }
        if(tipoPago) {
            Field48 += InitTrans.tkn09.packTkn09();
        }
        if (code.equals("460323")) {
            Field48 += InitTrans.tkn13.packTkn13();
        }
        Field48 += InitTrans.tkn43.packTkn43(inputMode, Track2);
        if (isPinExist) {
            PIN = emv.getPinBlock();
        }
        armar59();
        Field63 = packField63();
        para.setNeedPrint(true);
        isSaveLog = true;
        ret = enviarTransaccion();
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

    private boolean montoVariable(String [] items, int select2){
        boolean ret = false;
        String [] finales = new  String[4];
        String[] valores = new String[4];
        String[] vistaMensaje = new String[5];
        String[] mensajes = {"Empresa : ", "Nombre : ", "Adeudado : ", "Cargo : "};
        valores[0] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn06.getNomEmpresa()));
        valores[1] = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn06.getNomPersona()));
        valores[2] = ISOUtil.bcd2str(InitTrans.tkn06.getValor_documento(), 0, InitTrans.tkn06.getValor_documento().length);
        valores[3] = ISOUtil.bcd2str(InitTrans.tkn06.getCosto_servicio(), 0, InitTrans.tkn06.getCosto_servicio().length);
        vistaMensaje[0] = mensajes[0] + valores[0];
        vistaMensaje[1] = mensajes[1] + valores[1];
        for (int i = 2; i < finales.length; i ++) {
            finales[i] = formatMiles(Integer.parseInt(valores[i]));
            vistaMensaje[i] = mensajes[i] + finales[i];
        }
        vistaMensaje[4] = items[select2];
        ret=ingresarMontoVariable(vistaMensaje);
        return(ret);
    }

}



