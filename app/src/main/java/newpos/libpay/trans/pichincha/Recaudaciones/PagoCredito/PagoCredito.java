package newpos.libpay.trans.pichincha.Recaudaciones.PagoCredito;

import android.content.Context;
import android.text.InputType;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Recaudaciones.Recaudaciones;
import newpos.libpay.utils.ISOUtil;

import static newpos.libpay.utils.ISOUtil.formatMiles;

public class PagoCredito extends Recaudaciones {

    int seleccion = 0;

    public PagoCredito(Context ctx, String transEname, String tipoTrans, TransInputPara p) {
        super(ctx, transEname, tipoTrans, p);
        isPinExist=true;
    }

    public void pagoCredito () {
        int select;
        String contrapartida = "";
        String[] items = {"Cédula de identidad","Número de crédito"};
        String[] codItems = {"01","02"};
        select =  seleccionarMenus(items,codItems,"Pago de crédito");
        if (select >= 0) {
            switch (select){
                case 0:
                    contrapartida = ingresoContrapartida("Pago crédito",  "Cédula",10, 1010, 10);
                    InitTrans.tkn12.setCedula(ISOUtil.padright(contrapartida, 16, 'F').getBytes());
                    InitTrans.tkn09.setSb_typeAccount(ISOUtil.str2bcd(ISOUtil.padleft("8",2,'0'),false));
                    break;
                case 1:
                    contrapartida = ingresoContrapartida("Pago crédito",  "Número de crédito",16, InputType.TYPE_CLASS_NUMBER, 4);
                    InitTrans.tkn12.setCedula(ISOUtil.padright(contrapartida, 16, 'F').getBytes());
                    InitTrans.tkn09.setSb_typeAccount(ISOUtil.str2bcd(ISOUtil.padleft("9",2,'0'),false));
                    break;
            }
            if (!contrapartida.equals("")) {
                if (mensajeInicialPagC("420700")) {
                    if (confirmarDatos(items, select)) {
                        if (confirmaCuenta()) {
                            if (confirmarPagoMonto(items, select)) {
                                String[] item = {"Efectivo", "Débito cuenta"};
                                String[] cod = {"01", "02"};
                                if (efectivacion(codItems[select], item, cod, "420701")) {  //  CONFIRMAR
                                    if (confirmacion("420701")) {
                                        transUI.showSuccess(timeout , Tcode.Mensajes.recaudacionExitosa, "" );
                                    } else {
                                        transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                                    }
                                } else {
                                    transUI.showError(timeout, Tcode.T_user_cancel_operation);
                                }
                            } else {
                                transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                            }
                        }else {
                            transUI.showError(timeout, Tcode.T_user_cancel_operation);
                        }
                    } else {
                        transUI.showError(timeout , Tcode.T_user_cancel_operation);
                    }
                }else {
                    transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                }
            } else {
                transUI.showError(timeout , Tcode.T_user_cancel_operation);
            }
        }
    }

    private boolean confirmarDatos(String [] items, int select){
        if (RspCode.equals("99")){
            if (select == 1) {
                transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                return false;
            }
        }
        if (select != 0) {
            return true;
        }
        String[] vistaMensaje = new String[6];

        vistaMensaje[0] = "Nombre : " + ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn49.getNombreCompleto()));
        vistaMensaje[1] = "";
        vistaMensaje[2] = "No cédula. " + InitTrans.tkn49.getCedula();
        vistaMensaje[3] = "";
        vistaMensaje[5] = items[select];
        return(ventanaConfirmacion(vistaMensaje));

    }

    private boolean confirmaCuenta() {
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
                if (select >= 0) {
                    InitTrans.tkn12.setCedula(ISOUtil.padright(InitTrans.tkn49.getCedula(), 16, 'F').getBytes());
                    if (mensajeInicialPagC("420710")){
                        if (RspCode.equals("99")){
                                transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                                return false;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            return true;
        }
        return ret;
    }
    private boolean confirmarPagoMonto(String [] items, int select2) {
        String[] vistaMensaje = new String[7];
        String[] itemsV = InitTrans.tkn17.items;

        String dia = InitTrans.tkn25.fechas[0].substring(6,8);
        String mes = InitTrans.tkn25.fechas[0].substring(4, 6);
        String anio = InitTrans.tkn25.fechas[0].substring(0, 4);

        String fechaTexto = dia + "/" + mes + "/" + anio;

        vistaMensaje[0] = "Cliente : " + ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(InitTrans.tkn49.getNombreCompleto()));
        vistaMensaje[1] = "Préstamo nro: " + itemsV[0];
        vistaMensaje[2] = "Fecha prx pago: " + fechaTexto;
        vistaMensaje[3] = "Mnt vencido: $" + formatMiles(Integer.parseInt(InitTrans.tkn26.montos[2])) + " - Mnt prx pago: $" + formatMiles(Integer.parseInt(InitTrans.tkn26.montos[0]));
        vistaMensaje[4] = "Servicio: $" + formatMiles(Integer.parseInt(InitTrans.tkn26.montos[1]));
        vistaMensaje[6] = items[select2];

        return(ventanaConfirmacionPagCredito(vistaMensaje));
    }

    private boolean efectivacion(String codItem,String[] itemsT,String[] codItemsT, String proceCode){
        int select;
        boolean ret=false;
        boolean tipoPago = false;
        boolean leeTarjeta = false;
        select = seleccionarMenus(itemsT,codItemsT,"Modalidad pago");
        para.setNeedPass(true);
        para.setEmvAll(true);
        isReversal = true;
        isSaveLog = true;
        if(select == 0) {
            InitTrans.tkn09.setSb_typeAccount(ISOUtil.str2bcd(String.valueOf(select), true));
            if (leerTarjeta(TARJETA_CNB)) {
                leeTarjeta = true;
                tipoPago = false;
            } else {
                transUI.showMsgError(timeout, "Tarjeta no procesada");
            }
        } else if (select == 1 ) {
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
            if(mensajeTransaccion(proceCode)){
                ret=true;
            }
        }
        return ret;
    }

    private boolean mensajeTransaccion(String code) {
        boolean ret;
        setFixedDatas();
        isReversal = true;
        isSaveLog = true;
        MsgID = "0200";
        ProcCode = code;
        RspCode = null;
        Field48 =  InitTrans.tkn09.packTkn09();
        Field48 += InitTrans.tkn17.packTkn17(0);
        Field48 += InitTrans.tkn25.packTkn25();
        Field48 += InitTrans.tkn26.packTkn26();
        Field48 += InitTrans.tkn43.packTkn43(inputMode, Track2);
        Field48 += InitTrans.tkn93.packTkn93();
        if (isPinExist) {
            PIN = emv.getPinBlock();
        }
        armar59();
        Field63 = packField63();
        para.setNeedPrint(true);
        ret = enviarTransaccion(code);
        return ret;
    }

    public boolean enviarTransaccion(String code){
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
            }else if(retPrep == 93){
                mensajeTransaccion(code);
            }
        }
        return ret;
    }

    private boolean confirmacion(String code) {
        boolean ret = false;
        setFixedDatas();
        clearDataFields();
        RRN=iso8583.getfield(37);
        iso8583.clearData();
        transUI.newHandling("Pago crédito" ,timeout , Tcode.Mensajes.conectandoConBanco);
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

    protected boolean mensajeInicialPagC(String codigo) {
        boolean ret;
        setFixedDatas();
        MsgID = "0100";
        ProcCode = codigo;
        EntryMode = "0021";
        RspCode = null;
        if (codigo.equals("420700")) {
            Field48 = InitTrans.tkn09.packTkn09();
            Field48 += InitTrans.tkn12.packTkn12();
        } else {
            Field48 = InitTrans.tkn12.packTkn12();
            Field48 += InitTrans.tkn17.packTkn17(seleccion);
        }
        Amount = -1;
        packField63();
        ret = enviarTransaccion();
        return ret;
    }
}
