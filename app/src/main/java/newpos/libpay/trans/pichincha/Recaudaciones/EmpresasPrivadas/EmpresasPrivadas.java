package newpos.libpay.trans.pichincha.Recaudaciones.EmpresasPrivadas;
import android.content.Context;
import android.text.InputType;

import com.android.desert.keyboard.InputInfo;

import java.lang.reflect.Field;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Recaudaciones.Recaudaciones;
import newpos.libpay.utils.ISOUtil;

import static newpos.libpay.utils.ISOUtil.formatMiles;

public class EmpresasPrivadas extends Recaudaciones {

    InputInfo inputInfo;

    public EmpresasPrivadas(Context ctx, String transEname, String tipoTrans, TransInputPara p) {
        super(ctx, transEname, tipoTrans, p);
        Amount = -1;
        TotalAmount = Amount;
        para.setAmount(Amount);
    }

    public void ventaCatalogo() {
        int select, select2;
        String contrapartida = "";
        String[] items = {"Herbalife", "Belcorp", "Avon"};
        String[] codItems = {"01","02","03"};
        select =  seleccionarMenus(items,codItems,"Seleccione catálogo");
        if (select >= 0){
            InitTrans.tkn03.setCodigoEmpresa( ISOUtil.padleft(codItems[select],10,'0').getBytes());
            switch (select){
                case 0:
                    contrapartida = ingresoContrapartida(items[select],  "Ingrese contrapartida",20, InputType.TYPE_CLASS_TEXT, 0);
                    break;
                case 1:
                    String[] itemsDoc = {"Identificación", "Cod consultora"};
                    String[] codItemsDoc = {"01","02"};
                    select2 =  seleccionarMenus(itemsDoc,codItemsDoc,"Seleccione catálogo");
                    if (select2 >= 0) {
                        contrapartida = ingresoContrapartida(items[select],  itemsDoc[select2],20, InputType.TYPE_CLASS_NUMBER, 0);
                        InitTrans.tkn13.setTipoPago(ISOUtil.str2bcd(codItems[select2], false));
                    }
                    break;
                case 2:
                    contrapartida = ingresoContrapartida(items[select],  "Ingrese contrapartida",11, InputType.TYPE_CLASS_NUMBER, 11);
                    break;
            }
            if(!contrapartida.equals("")){
                InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida,34,' ').getBytes());
                if (mensajeInicial("4930" + codItems[select])){
                    int codEmpresa = Integer.parseInt(codItems[select]);
                    codEmpresa = codEmpresa+20;
                    if(confirmarPagoCat(items,select,"4930" + (codEmpresa - 10))) {
                        String[] item = {"Efectivo", "Débito cuenta"};
                        String[] cod = {"01", "02"};
                        para.setNeedPrint(false);
                        String montoTotal = ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length);
                        if (efectivacion(montoTotal, item, cod, "4930" + String.valueOf(codEmpresa))){
                            if(confirmacionRecaudo("4930" + String.valueOf(codEmpresa), "Venta catálogo")){
                                transUI.showSuccess(timeout , Tcode.Mensajes.recaudacionExitosa, "" );
                            }
                        }
                    }
                }
            }else {
                transUI.showError(timeout , Tcode.T_user_cancel_operation);
            }
        }
    }

    public void recaudacionEmpresas() {
        String[] opciones = {"Código empresa privada","Municipios"};
        inputInfo = transUI.showBotones(2, opciones, "Empresas");
        if (inputInfo.isResultFlag()) {
            String itemSelect = inputInfo.getResult();
            switch (itemSelect) {
                case "0":
                    empresasPrivadas();
                    break;
                case "1":
                    empresasPubliMuniGuaya();
                    break;
            }
        }
    }

    public void empresasPrivadas() {
        int select = 0;
        String codEmpresaServicio, contrapartida;
        String[] items = {"Empresas privadas"};
        String[] codItems = {"00"};
        codEmpresaServicio = ingresoContrapartida(items[0],  "Código empresa / Servicio",10, InputType.TYPE_CLASS_NUMBER, 0);
        InitTrans.tkn03.setCodigoEmpresa( ISOUtil.padleft(codEmpresaServicio,10,'0').getBytes());
        if (!codEmpresaServicio.equals("")) {
            contrapartida = ingresoContrapartida(items[0],  "Número contrapartida", 20, InputType.TYPE_CLASS_TEXT, 0);
            InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida,34,' ').getBytes());
            if (!contrapartida.equals("")) {
                if (mensajeInicial("320000")){
                    if(confirmarPagoCat(items,select, "320100")) {
                        String[] item = {"Efectivo", "Débito cuenta"};
                        String[] cod = {"01", "02"};
                        para.setNeedPrint(true);
                        String montoTotal = ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length);
                        if (efectivacion(montoTotal, item, cod, "320200")){
                            if(confirmacionRecaudo("320200", "Empresas privadas")){
                                transUI.showSuccess(timeout , Tcode.Mensajes.recaudacionExitosa, "" );
                            }
                        }
                    }
                }
            }
        }else {
            transUI.showError(timeout , Tcode.T_user_cancel_operation);
        }
    }

    public void empresasPubliMuniGuaya() {
        boolean validar = false;
        int select, selectMer;
        String[] itemsMuni = {"Guayaquil"};
        String[] codItemsMuni = {"02"};
        int select2 = seleccionarMenus(itemsMuni, codItemsMuni, "Municipios");
        String codEmpre;
        if (select2 <= 0){
            String contrapartida = "";
            String[] items = {"Cep", "Mi lote", "Predios", "Mercados", "T.R.B."};
            int[] longitudes = {10, 13, 0, 13, 13};
            int[] type = {InputType.TYPE_CLASS_NUMBER, InputType.TYPE_CLASS_NUMBER, InputType.TYPE_CLASS_NUMBER,
                    InputType.TYPE_CLASS_NUMBER, InputType.TYPE_CLASS_NUMBER};
            String[] codItems = {"01", "02", "03", "04", "05"};
            String[] mensajes = {"Número cep", "Número mi lote", "Sector", "", "Número T.R.B."};
            String[] itemsMercados ={"Credencial", "Puesto"};
            String[] codItemsMercados = {"01", "02"};
            String[] mensajesPredios = {"Sector", "Manzana", "Lote", "División", "Ub. prop. vertical",
                    "Ub. prop. horizontal", "No. emisión predio"};
            int[] longPredios = {4, 5, 5, 5, 4, 4, 3};
            select = seleccionarMenus(items, codItems, "Guayaquil");
            if (select >= 0) {
                InitTrans.tkn03.setCodigoEmpresa( ISOUtil.padleft(codItems[select],10,'0').getBytes());
                if (select == 2) {
                    contrapartida = ingresoEmprePubli(mensajesPredios, longPredios, InputType.TYPE_CLASS_NUMBER, 0);
                }
                if (select != 3 && select!= 2) {
                    contrapartida = ingresoContrapartida(items[select],  mensajes[select],longitudes[select], type[select], 0);
                } else if (select == 3) {
                    selectMer = seleccionarMenus(itemsMercados, codItemsMercados, "Mercados - Guayaquil");
                    if (selectMer >= 0) {
                        contrapartida = ingresoContrapartida(itemsMercados[selectMer],  "Número " + " "+
                                 Character.toLowerCase(itemsMercados[selectMer].charAt(0)) + itemsMercados[selectMer].substring(1,itemsMercados[selectMer].length()) //--> Para capturar la primera en minúscula <--//
                                , 13, InputType.TYPE_CLASS_NUMBER, 0);
                        codEmpre = codItems[select] + codItemsMercados[selectMer].substring(1);
                        InitTrans.tkn03.setCodigoEmpresa( ISOUtil.padleft(codEmpre,10,'0').getBytes());
                    }
                }
                if (!contrapartida.equals("")) {
                    InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida,34,' ').getBytes());
                    if (mensajeInicial("4851" + codItems[select])) {
                        int codEmpresa = Integer.parseInt(codItems[select]);
                        codEmpresa = codEmpresa+20;
                        if (confirmarPagoCat(items, select, "4851" + (codEmpresa - 10))) {
                            String[] item = {"Efectivo", "Débito cuenta"};
                            String[] cod = {"01", "02"};
                            para.setNeedPrint(true);
                            String montoTotal = ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length);
                            if (efectivacion(montoTotal, item, cod, "4851" + codEmpresa)) {
                                if(confirmacionRecaudo("4851" + codEmpresa, "Empresas públicas")){
                                    transUI.showSuccess(timeout , Tcode.Mensajes.recaudacionExitosa, "" );
                                }
                            }else{
                                transUI.showMsgError(timeout, "Error en recaudo");
                            }
                        }
                    }
                }else {
                    transUI.showError(timeout , Tcode.T_user_cancel_operation);
                }
            }
        }
    }
    /**
     * Funcion mensajeInicial()
     * Crea los campos necesarios para una consulta inicial de MsgID 0100
     * @param code recibe el ProcCode del mensaje
     * @return retorna boleano true si el envio de la transaccion funciono correctamente.
     */
    @Override
    protected boolean mensajeInicial(String code) {
        boolean ret;
        setFixedDatas();
        MsgID = "0100";
        ProcCode = code;
        EntryMode = "0021";
        RspCode = null;
        Field48 = InitTrans.tkn03.packTkn03(true);
        if (code.substring(4,5).equals("1") || code.equals("320100")){
            Field48 += InitTrans.tkn06.packTkn06();
        }
        if (code.equals("493002") || code.equals("493012")) {
            Field48 += InitTrans.tkn13.packTkn13();
        }
        String newCode = code.substring(0, 5);
        if (newCode.equals("49301")) {
            RRN = null;
            AuthCode = null;
        }
        packField63();
        ret = enviarTransaccion();
        return ret;
    }


    private boolean efectivacion(String codItem,String[] itemsT,String[] codItemsT, String proceCode){
        int select;
        boolean ret=false;
        boolean leeTarjeta = false;
        boolean tipoPago = false;
        select = seleccionarMenus(itemsT,codItemsT,"Modalidad pago");
        Amount = Long.parseLong(codItem);
        TotalAmount = Amount;
        para.setAmount(Amount);
        para.setNeedPass(true);
        para.setEmvAll(true);
        isReversal = true;
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
            select = seleccionarMenus(item,cod,context.getResources().getString(R.string.tipoDeCuenta));
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
            if(msgEfectivacion(proceCode, tipoPago)){
                ret=true;
            }
        }
        return ret;
    }

    private boolean confirmarPagoCat(String [] items, int select2, String code){
        String tipoPago = ISOUtil.bcd2str(InitTrans.tkn39.getFlagVarFijo(),0, InitTrans.tkn39.getFlagVarFijo().length);
        int i;
        boolean ret=false;
        if(tipoPago.equals("4E")){
            if (montoVariable(items, select2)){
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
        ret=ventanaConfirmacion(vistaMensaje);
        return(ret);
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
