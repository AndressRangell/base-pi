package newpos.libpay.trans.pichincha.Recaudaciones.CasasComerciales;

import android.content.Context;
import android.text.InputType;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Recaudaciones.Recaudaciones;
import newpos.libpay.utils.ISOUtil;

import static newpos.libpay.utils.ISOUtil.formatMiles;

public class CasasComerciales extends Recaudaciones {

    public CasasComerciales(Context ctx, String transEname, String tipoTrans, TransInputPara p) {
        super(ctx, transEname, tipoTrans, p);
    }

    public  void casasComerciaes() {
        int select;
        String[] items = {"DePrati","Tarjeta CLUBPYCCA"};
        String[] codItems = {"01","02"};
        select =  seleccionarMenus(items,codItems,"Casas comerciales");
        if (select >= 0) {
            switch (select) {
                case 0:
                    dePrati(items,codItems,select);
                    break;

                case 1:
                    clubPycca(codItems[select],items[select]);
                    break;
            }
        }
    }

    private void dePrati(String[] items, String[] codItems, int select) {
        String contrapartida;
        InitTrans.tkn03.setCodigoEmpresa(ISOUtil.padleft(codItems[select], 10, '0').getBytes());
        contrapartida = ingresoContrapartida(items[select], "Número contrapartida", 20, InputType.TYPE_CLASS_NUMBER, 0);
        if (!contrapartida.equals("")) {
            InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida, 34, ' ').getBytes());
            if (mensajeInicial("490101")) {
                int codEmpresa = Integer.parseInt(codItems[select]);
                codEmpresa = codEmpresa + 20;
                if (confirmarPago(items, select, "4901" + (codEmpresa - 10))) {
                    String[] item = {"Efectivo", "Débito cuenta"};
                    String[] cod = {"01", "02"};
                    String montoTotal = ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length);
                    if (efectivacion(montoTotal, item, cod, "4901" + codEmpresa)) {//cambiar proceCode dependiendo del aumento
                        if (confirmacionRecaudo("4901" + codEmpresa, "Casas comerciales")) {
                            transUI.showSuccess(timeout, Tcode.Mensajes.recaudacionExitosa, "");
                        }
                    } else {
                        transUI.showError(timeout, Tcode.T_user_cancel_operation);
                    }
                } else {
                    transUI.showError(timeout, Tcode.T_user_cancel_operation);
                }
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
    }

    private void clubPycca(String codEmpre, String titulo) {
        int select;
        String[] itemsContrapartida = {context.getResources().getString(R.string.cedula), context.getResources().getString(R.string.ruc), context.getResources().getString(R.string.pasaporte)};
        String[] codDoc = {"01", "02", "03"};
        int[] maxLen = {10, 13, 20};
        int[] minLen = {10, 13, 0};
        int[] inputTypes = {1010, 1010, InputType.TYPE_CLASS_TEXT};

        select = seleccionarMenus(itemsContrapartida, codDoc, "Tipo identificación");
        if (select >= 0) {
            String contrapartida = ingresoContrapartida(titulo, "Ingrese " + itemsContrapartida[select], maxLen[select], inputTypes[select], minLen[select]);

            if(!contrapartida.equals("")){
                setFieldsTokens(codEmpre, contrapartida, codDoc[select]);
                if (enviarConsulta(proCodes.recauPYCCAConsulta)) {
                    mostrarRealizarEfectivacion(titulo, proCodes.recauPYCCAEfectivacion, setValores(),true,true);
                } else {
                    transUI.showMsgError(timeout, "Error en consulta");
                }
            }
        }
    }

    private void setFieldsTokens(String codEmpre, String contrapartida,  String tipoPago){
        InitTrans.tkn03.setCodigoEmpresa(ISOUtil.padleft(codEmpre.substring(1),10,'0').getBytes());
        InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida,34,' ').getBytes());
        InitTrans.tkn13.setTipoPago( ISOUtil.str2bcd(tipoPago,false));

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


    private boolean confirmarPago(String [] items, int select2, String code){
        String tipoPago = ISOUtil.bcd2str(InitTrans.tkn39.getFlagVarFijo(),0, InitTrans.tkn39.getFlagVarFijo().length);
        int i;
        boolean ret=false;
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
        //Comision = Long.parseLong(ISOUtil.bcd2str(InitTrans.tkn06.getCosto_servicio(), 0, InitTrans.tkn06.getCosto_servicio().length));
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
        Amount = Long.parseLong(codItem);
        TotalAmount = Amount;
        para.setAmount(Amount);
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
            para.setNeedPrint(true);
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

}
