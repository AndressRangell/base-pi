package newpos.libpay.trans.pichincha.Recaudaciones.TarjetaCredito;

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

public class TarjetaCredito extends Recaudaciones {

    public TarjetaCredito(Context ctx, String transEname, String tipoTrans, TransInputPara p) {
        super(ctx, transEname, tipoTrans, p);
        isPinExist=true;
    }

    public void tarjetaCredito() {
        Amount = -1;
        TotalAmount = Amount;
        para.setAmount(Amount);
        int select;
        String contrapartida;
        String[] items = {"Diners","Visa","Mastercard"};
        String[] codItems = {"01","02","03"};
        select =  seleccionarMenus(items,codItems,"Pago de tarjeta");
        if (select >= 0) {
            InitTrans.tkn03.setTipoTarjeta(codItems[select].getBytes());
            if (select == 0) {
                contrapartida = ingresoContrapartida(items[select],  "Número tarjeta",14, InputType.TYPE_CLASS_NUMBER, 4);
            } else {
                contrapartida = ingresoContrapartida(items[select],  "Número tarjeta",16, InputType.TYPE_CLASS_NUMBER, 4);
            }
            if (!contrapartida.equals("")) {
                InitTrans.tkn03.setNumeroTarjeta(ISOUtil.padright(contrapartida, 42, ' ').getBytes());
                if (mensajeInicialTarjeta("350000")) {
                    if (confirmarPago(items, select)) {
                        String[] item = {"Efectivo", "Débito cuenta"};
                        String[] cod = {"01", "02"};
                        if (efectivacion(codItems[select], item, cod, "350200")) {
                            if (confirmacion("350200")) {
                                transUI.showSuccess(timeout , Tcode.Mensajes.recaudacionExitosa, "" );
                            } else {
                                transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                            }
                        } else {
                            transUI.showError(timeout , Tcode.T_user_cancel_operation);
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

    private boolean confirmarPago(String [] items, int select2){
        String tipoPago = ISOUtil.bcd2str(InitTrans.tkn39.getFlagVarFijo(),0, InitTrans.tkn39.getFlagVarFijo().length);
        int i;
        if(tipoPago.equals("4E")){
            if (montoVariable(items, select2)){
                if (!mensajeInicialTarjeta("350100")){
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
            if(mensajeTransaccion(proceCode, tipoPago)){
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

    private boolean mensajeTransaccion(String code, boolean tipoPago) {
        boolean ret;
        setFixedDatas();
        MsgID = "0200";
        ProcCode = code;
        iso8583.clearData();
        RRN = null;
        RspCode = null;
        AuthCode = null;
        Field48 = InitTrans.tkn03.packTkn03PagTar(tipoPago);
        Field48 += InitTrans.tkn06.packTkn06();
        if(tipoPago) {
            Field48 += InitTrans.tkn09.packTkn09();
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

    private boolean confirmacion(String code) {
        boolean ret = false;
        setFixedDatas();
        clearDataFields();
        RRN=iso8583.getfield(37);
        iso8583.clearData();
        transUI.newHandling("Tarjeta crédito" ,timeout , Tcode.Mensajes.conectandoConBanco);
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

    protected boolean mensajeInicialTarjeta(String code) {
        boolean ret;
        setFixedDatas();
        para.setNeedAmount(true);
        if (code.equals("350000")){
            Amount = -1;
            TotalAmount = Amount;
            para.setAmount(Amount);
        }
        MsgID = "0100";
        ProcCode = code;
        EntryMode = "0021";
        Field48 = InitTrans.tkn03.packTkn03PagTar(true);
        if (code.equals("350100")){
            Field48 += InitTrans.tkn06.packTkn06();
            iso8583.setField(39,null);
            RspCode = null;
        }
        packField63();
        ret = enviarTransaccion();
        return ret;
    }
}
