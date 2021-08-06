package newpos.libpay.trans.pichincha.Recaudaciones.Ant;
import android.content.Context;
import android.text.InputType;

import com.android.desert.keyboard.InputInfo;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Recaudaciones.Recaudaciones;
import newpos.libpay.utils.ISOUtil;

import static newpos.libpay.utils.ISOUtil.formatMiles;

public class Ant extends Recaudaciones {

    InputInfo inputInfo;
    Context cont ;

    public Ant(Context ctx, String transEname, String tipoTrans, TransInputPara p) {
        super(ctx, transEname, tipoTrans, p);
        cont = ctx;


    }

    public void ant() {
        int select;
        String aumento, codeConfirm;
        String montoTotal = "";
        String [] regDoc;
        String [] regProv;
        String[] tipoANT = {"Ant. citaciones, facturas", "Ant. solicitudes y servicios",
                                "Ant. convenio de pago" };
        String[] codRecau = {"01", "02", "03"};
        String newCode;
        select = seleccionarMenus(tipoANT, codRecau, "Ant tránsito");

       if(select>=0){
           regDoc=selecTipoDoc(codRecau[select]);
           if(regDoc!=null){
               InitTrans.tkn03.setCodigoEmpresa(ISOUtil.padleft(codRecau[select].substring(1),10,'0').getBytes());
               InitTrans.tkn03.setContrapartida(ISOUtil.padright(regDoc[1],34,' ').getBytes());
               regProv=selecProvincia();
               if(regProv!=null){
                   if(!codRecau[select].equals("02")) {
                       InitTrans.tkn13.setTipoPago( ISOUtil.str2bcd(regDoc[0],false));
                   } else {
                       InitTrans.tkn13.setTipoPago(new byte[1]);
                   }
                   if (mensajeInicial(String.valueOf("4940"+ codRecau[select]))){
                       aumento = String.valueOf(Integer.parseInt("4940"+ codRecau[select]) + 10);
                       String tipoPago = ISOUtil.bcd2str(InitTrans.tkn39.getFlagVarFijo(),0, InitTrans.tkn39.getFlagVarFijo().length);
                       if (confirmarPagoCat(tipoANT, select, aumento)) {
                           String[] item = {"Efectivo", "Débito cuenta"};
                           String[] cod = {"01", "02"};
                           if(tipoPago.equals("4E")){
                               montoTotal = String.valueOf(Amount);
                               codeConfirm = "49402" +codRecau[select].substring(1);
                           } else {
                               montoTotal = ISOUtil.bcd2str(InitTrans.tkn06.getValor_adeudado(), 0, InitTrans.tkn06.getValor_adeudado().length);
                               codeConfirm = "49402" +codRecau[select].substring(1);
                           }
                           InitTrans.tkn17.setTkn17(regProv[0],regProv[1]);
                           newCode = "4940"+ codRecau[select];
                           newCode += String.valueOf(30);
                           if (efectivacion(montoTotal, item, cod, "49402" +codRecau[select].substring(1) )) {
                               if(confirmacionRecaudo(codeConfirm, "RECAUDO ANT")){
                                   transUI.showSuccess(timeout , Tcode.Mensajes.recaudacionExitosa, "" );
                               }
                           }else{
                               transUI.showMsgError(timeout, "Error en recaudo");
                           }
                       }
                   }

               }
           }
       }
    }


    private  String [] selecTipoDoc(String codigo){
        String contrapartida = null;
        int select=0;

        String [] regSelect = new String[2];
        String[] itemCitaciones = {context.getResources().getString(R.string.cedula), context.getResources().getString(R.string.ruc), context.getResources().getString(R.string.pasaporte), "Placa"};
        String[] itemConvenios = {context.getResources().getString(R.string.cedula), context.getResources().getString(R.string.ruc), context.getResources().getString(R.string.pasaporte)};
        Integer[]limCaract={10,10,1,7,10,13,20,7};   // Minimos y maximos caracteres permitidos

        String[] codDoc = {"01", "02", "06", "08"};
        switch (codigo){
            case "01":
                select = seleccionarMenus(itemCitaciones, codDoc, "Tipo identificación");
                break;
            case "02":
                contrapartida = ingresoContrapartida("Servicio", "Ingrese número de orden",
                        20, InputType.TYPE_CLASS_NUMBER, 6);
                break;
            case "03":
                select = seleccionarMenus(itemConvenios, codDoc, "Tipo identificación");
                break;

        }


        if (codigo.equals("01") || codigo.equals("03") ) {
            int inputType;
            InitTrans.tkn13.setTipoPago(ISOUtil.str2bcd(codDoc[select], false));
            inputType= (select>1)?InputType.TYPE_CLASS_TEXT:1010;
            contrapartida = ingresoContrapartida(itemCitaciones[select], "Ingrese "+ " "+
                    Character.toLowerCase(itemCitaciones[select].charAt(0)) +
                    itemCitaciones[select].substring(1,itemCitaciones[select].length()),
                    limCaract[select +4], inputType, limCaract[select]);
            regSelect[0]=codDoc[select];

        }
        regSelect[1]=contrapartida;

        return  regSelect;

    }

    @Override
    protected boolean mensajeInicial(String code) {
        boolean ret;
        setFixedDatas();
        MsgID = "0100";
        EntryMode = "0021";
        ProcCode=code;
        if (!code.substring(0, 5).equals("49401")) {
            Amount=-1;
        }
        Field48 = InitTrans.tkn03.packTkn03(true);
        if (code.substring(0, 5).equals("49401")) {
            RspCode = null;
            Field48 += InitTrans.tkn06.packTkn06();
        }
        if (!code.equals("494002")) {
            Field48= Field48 + InitTrans.tkn13.packTkn13();
        }
        Field48= Field48 + InitTrans.tkn17.packTkn17(0);
        packField63();
        ret = enviarTransaccion();
        return ret;
    }


    public String [] selecProvincia() {
        String [] ret = new String[2];
        int select;
        String[] region = {"Azuay","Bolivar","Canar","Carchi","Cotopaxi","Chimborazo","El oro",
                "Esmeraldas", "Guayas","Imbabura","Loja","Los Ríos","Manabi", "Morona Santiago",
                "Napo","Pastaza","Pichincha","Tungurahua","Zamora Chinchipe","Galápagos","Sucumbios",
                "Orellana","Santo Domingo","Santa Elena"};
        String[] codRegion = {"01","02","03","04","05","06","07","08","09","10","11","12","13",
                "14","15","16","17","18","19","20","21","22","23","24"};
        select = seleccionarMenus(region, codRegion, "Ant tránsito");
        if (select >= 0) {
            ret[0] = codRegion[select];
            ret[1] = region[select];
            InitTrans.tkn17.setTkn17(ret[0],ret[1]);
        }
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
            String tipoMonto = ISOUtil.bcd2str(InitTrans.tkn39.getFlagVarFijo(),0, InitTrans.tkn39.getFlagVarFijo().length);
            if(tipoMonto.equals("4E") && proceCode.substring(0,5).equals("49400") || proceCode.substring(0,5).equals("49401")){
                proceCode = String.valueOf(Integer.parseInt(proceCode) + 10);
            }
            if(msgEfectivacion(proceCode, tipoPago)){
                ret=true;
            }
        }
        return ret;
    }

    @Override
    protected boolean msgEfectivacion(String code, boolean tipoPago) {
        boolean ret;
        setFixedDatas();
        MsgID = "0200";
        ProcCode = code;
        RspCode = null;
        Field48 = InitTrans.tkn03.packTkn03(tipoPago);
        Field48 += InitTrans.tkn06.packTkn06();
        if(tipoPago) {
            Field48 += InitTrans.tkn09.packTkn09();
        }
        Field48 += InitTrans.tkn13.packTkn13();
        Field48 += InitTrans.tkn17.packTkn17(0);
        Field48 += InitTrans.tkn43.packTkn43(inputMode, Track2);
        armar59();
        Field63 = packField63();
        ret = enviarTransaccion();
        return ret;
    }

    private boolean confirmarPagoCat(String [] items, int select2, String code){
        String tipoPago = ISOUtil.bcd2str(InitTrans.tkn39.getFlagVarFijo(),0, InitTrans.tkn39.getFlagVarFijo().length);
        int i;
        boolean ret=false;
        if(tipoPago.equals("4E")){
            if (montoVariable(items, select2)) {
                if (mensajeInicial(code)) {
                    ret = true;
                    para.setNeedPrint(false);
                } else {
                    transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                }
            } else {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }
        } else {
            para.setNeedPrint(true);

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
        }
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
