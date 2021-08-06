package newpos.libpay.trans.pichincha.Recaudaciones.PagoServicios;

import android.content.Context;
import android.content.res.Resources;
import android.text.InputType;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Recaudaciones.Recaudaciones;
import newpos.libpay.utils.ISOUtil;

import static newpos.libpay.utils.ISOUtil.formatMiles;

public class PagoDeServicios extends Recaudaciones {


    public PagoDeServicios(Context ctx, String transEname, String tipoTrans, TransInputPara p) {
        super(ctx, transEname, tipoTrans, p);
        Amount = -1;
        TotalAmount = Amount;
        para.setAmount(Amount);
    }

    public void servicioAguaPotable(){
        int select;
        String contrapartida;
        String[] items = {"Empresa agua Quito", "Interagua Guayaquil"};
        String[] codItems = {"01","02"};
        String codigoProceso = "470201";
        select =  seleccionarMenus(items,codItems,"Agua potable");
        if (select >= 0){
            InitTrans.tkn03.setCodigoEmpresa( ISOUtil.padleft(codItems[select],10,'0').getBytes());
            if(select==1){
                codigoProceso = selecTipoPagoInteraguaGye();
            }

            contrapartida = ingresoContrapartida(items[select],  "Número de suministro",20, InputType.TYPE_CLASS_NUMBER, 0);
            if(!contrapartida.equals("")){

                InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida,34,' ').getBytes());

                if (mensajeInicial(codigoProceso)){
                    int codEmpresa = Integer.parseInt(codItems[select]);
                    codEmpresa = codEmpresa + 20;
                    if(confirmarPago(contrapartida,items,select)) {
                        String[] item = {"Efectivo", "Débito cuenta"};
                        String[] cod = {"01", "02"};
                        String montoTotal = ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length);

                        String nextCodigoProceso = nuevoCodigoProceso(codigoProceso, 2);
                        if(efectivacion(montoTotal, item, cod, nextCodigoProceso )){
                            if(confirmacionRecaudo(nextCodigoProceso, "Pago de servicios")){
                                transUI.showSuccess(timeout , Tcode.Mensajes.pagoExitoso, "" );
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

    public void servicioLuzElectrica(){
        int select;
        String contrapartida;
        String[] items = {"Cnel", "Servicio eléctrico"};
        String[] codItems = {"02","04"};
        select =  seleccionarMenus(items,codItems,"Luz eléctrica");
        if (select >= 0){
            InitTrans.tkn03.setCodigoEmpresa( ISOUtil.padleft(codItems[select],10,'0').getBytes());
            contrapartida = ingresoContrapartida(items[select],  "Número de suministro",20, InputType.TYPE_CLASS_NUMBER, 0);
            if(!contrapartida.equals("")){
                InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida,34,' ').getBytes());
                if (mensajeInicial("4703" + codItems[select])){
                    int codEmpresa = Integer.parseInt(codItems[select]);
                    codEmpresa = codEmpresa + 20;
                    if(confirmarPago(contrapartida,items,select)) {
                        String[] item = {"Efectivo", "Débito cuenta"};
                        String[] cod = {"01", "02"};
                        String montoTotal = ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length);
                        if(efectivacion(montoTotal, item, cod, "4703" + String.valueOf(codEmpresa))){
                            if(confirmacionRecaudo("4703" + String.valueOf(codEmpresa), "Luz eléctrica")){
                                transUI.showSuccess(timeout , Tcode.Mensajes.pagoExitoso, "" );
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

    String selecTipoPagoInteraguaGye(){
        int select;
        String ret = null;
        String[] items = {"Pago por contrato","Pago por cupón"};
        String[] codItems = {"01","02"};
        select =  seleccionarMenus(items,codItems,"Selecciona el tipo de pago");
        if( select >= 0 ){
            ret = "4752" + codItems[select];
        }

        return ret;
    }
    private boolean confirmarPago(String contrapartida, String [] items, int select2){
        String tipoPago = ISOUtil.bcd2str(InitTrans.tkn39.getFlagVarFijo(),0, InitTrans.tkn39.getFlagVarFijo().length);
        int i;
        boolean ret=false;
        if(tipoPago.equals("4E")){
            if (montoVariable(items, select2)) {
                int code = Integer.parseInt(iso8583.getfield(3)) + 10;
                if (mensajeInicial(String.valueOf(code))) {}
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
        return(ventanaConfirmacion(vistaMensaje));
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
        if (code.equals("470211") || code.equals("470312") || code.equals("475211") ||
                code.equals("470314") || code.equals("475212")) {
            Field48 += InitTrans.tkn06.packTkn06();
        }
        /*if (code.equals("470101")) {
            Field48 += InitTrans.tkn13.packTkn13();
        }*/
        packField63();
        ret = enviarTransaccion();
        return ret;
    }

    private boolean efectivacion(String aMonto,String[] itemsT,String[] codItemsT, String proceCode){
        int select;
        boolean ret=false;
        boolean leeTarjeta = false;
        boolean tipoPago = false;
        select = seleccionarMenus(itemsT,codItemsT,"Modalidad pago");
        para.setNeedPass(true);
        para.setEmvAll(true);
        para.setNeedConfirmCard(false);
        para.setNeedPrint(true);
        isReversal = true;
        isProcPreTrans = true;
        isSaveLog = true;

        Amount = Long.parseLong(aMonto);
        TotalAmount = Amount;
        para.setAmount(Amount);

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
            if(msgEfectivacion(proceCode, tipoPago)){
                ret=true;
            }
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


    /*
    Suma la cantidad aSuma al caracter 5 de aCodigoProceso
    retorna . String con nuevo codigo de proceso.
     */
    String nuevoCodigoProceso(String aCodigoProceso, int aSuma){
        char auxChar = aCodigoProceso.charAt(4);
        int auxIntDigitos = auxChar - '0';

        String auxStringDigito = String.valueOf(auxIntDigitos + aSuma);

        String primeros4 = aCodigoProceso.substring(0,4);
        String ultimodigito = aCodigoProceso.substring(5,6);
        String nextCodigoProceso = primeros4 + auxStringDigito + ultimodigito;

        return nextCodigoProceso;
    }

    public void servicioTelefonico() {
        int select, consulta = 0;
        String codProceso = "4701";
        String[] items = {"Cnt fijo", "Planes cnt", "Claro fijo","Planes claro","Planes movistar"};
        String[] codItems = {"01","05","03","04","02"};
        String[] itemsConsulta = {"Número telefónico","Cuenta financiera"};
        String[] codConsulta = {"01","02"};
        select = seleccionarMenus(items,codItems,"Teléfono / planes");
        if (select >= 0) {
            /*if (select == 0) {
                consulta = seleccionarMenus(itemsConsulta, codConsulta, "Tipo consulta");
            }*/
            if(cntTransacciones(items, codItems, select, codConsulta ,consulta)){
                if (enviarConsulta(codProceso + codItems[select])) {
                    int code = Integer.parseInt(codProceso + codItems[select]) + 20;
                    InitTrans.tkn13.clean();
                    mostrarRealizarEfectivacion("Pago servicio", String.valueOf(code), setValores(),true,false);
                } else {
                    transUI.showError(timeout, Tcode.T_user_cancel_operation);
                }
            }
        }
    }

    private boolean cntTransacciones(String[] items, String[] codItems, int select, String[] codConsulta ,int consulta) {
        boolean ret = false;
        String contrapartida;
        if (select == 0) {
            contrapartida = ingresoContrapartida(items[select],"Ingrese número telefónico",9, InputType.TYPE_CLASS_NUMBER, 0);
        } else {
            contrapartida = ingresoContrapartida(items[select],"Ingrese número telefónico",10, InputType.TYPE_CLASS_NUMBER, 0);
        }
        if (!contrapartida.equals("")) {
            InitTrans.tkn03.setCodigoEmpresa(ISOUtil.padleft(codItems[select],10,'0').getBytes());
            InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida,34,' ').getBytes());
            /*if (consulta >= 0 ) {
                InitTrans.tkn13.setTipoPago(ISOUtil.str2bcd(String.valueOf(codConsulta[consulta]), true));
            } else {
                InitTrans.tkn13.setTipoPago(ISOUtil.str2bcd("1", true));
            }*/
            ret = true;
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
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

    public void internet() {
        int select;
        String codProceso = "4704";
        String[] items = {"Cnt internet"};
        String[] codItems = {"01"};

        select = seleccionarMenus(items,codItems,"Cnt internet");
        if (select >= 0) {
            if (intTransacciones(items, codItems, select)) {
                if (enviarConsulta(codProceso + codItems[select])) {
                    int code = Integer.parseInt(codProceso + codItems[select]) + 20;
                    mostrarRealizarEfectivacion("Pago servicio", String.valueOf(code), setValores(),true,false);
                } else {
                    transUI.showError(timeout, Tcode.T_user_cancel_operation);
                }
            }
        }

    }

    private boolean intTransacciones(String[] items, String[] codItems, int select) {
        boolean ret = false;
        String contrapartida;
        contrapartida = ingresoContrapartida(items[select],"Ingrese número de contrato",20, InputType.TYPE_CLASS_NUMBER, 0);
        if (!contrapartida.equals("")) {
            InitTrans.tkn03.setCodigoEmpresa(ISOUtil.padleft(codItems[select],10,'0').getBytes());
            InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida,34,' ').getBytes());
            ret = true;
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }
}
