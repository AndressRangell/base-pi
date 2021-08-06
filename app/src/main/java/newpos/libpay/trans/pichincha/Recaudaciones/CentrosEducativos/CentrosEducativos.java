package newpos.libpay.trans.pichincha.Recaudaciones.CentrosEducativos;

import android.content.Context;
import android.text.InputType;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Recaudaciones.Recaudaciones;
import newpos.libpay.utils.ISOUtil;

public class CentrosEducativos extends Recaudaciones {

    /**
     * 金融交易类构造
     *
     * @param ctx
     * @param transEname
     * @param tipoTrans
     * @param p
     */
    public CentrosEducativos(Context ctx, String transEname, String tipoTrans, TransInputPara p) {
        super(ctx, transEname, tipoTrans, p);
    }

    public void centrosEducativos() {
        int selectM;
        String[] tipoCentro = {"Recaudación UTPL", "Instituto Cordillera"};
        String[] codRecau = {"01", "02"};
        selectM = seleccionarMenus(tipoCentro, codRecau, "Centros Educativos");
        if (selectM >= 0) {

            switch (selectM) {
                case 0:
                    recauUTPL(codRecau[selectM], tipoCentro[selectM]);
                    break;

                case 1:
                    recauITSCO(codRecau[selectM], tipoCentro[selectM]);
                    break;
            }
        }
    }

    private void recauUTPL(String codEmpre, String titulo) {
        int select;
        String[] itemsContrapartida = {context.getResources().getString(R.string.cedula), context.getResources().getString(R.string.ruc),
                context.getResources().getString(R.string.pasaporte), "Carnet de refugiado"};
        String[] codDoc = {"01", "02", "03", "07"};
        int[] maxLen = {10, 13, 20, 20};
        int[] minLen = {10, 13, 0, 0};
        int[] inputTypes = {1010, 1010, InputType.TYPE_CLASS_TEXT, InputType.TYPE_CLASS_TEXT};

        select = seleccionarMenus(itemsContrapartida, codDoc, "Tipo identificación");
        if (select >= 0) {
            String contrapartida = ingresoContrapartida(titulo, "Ingrese " + itemsContrapartida[select], maxLen[select], inputTypes[select], minLen[select]);

            if(!contrapartida.equals("")){
                setFieldsTokens(codEmpre, contrapartida, codDoc[select]);
                if (enviarConsulta(proCodes.recauUTPLConsulta)) {
                    if (ISOUtil.byte2int(InitTrans.tkn17.getNumItems()) > 1) {
                        multiFacturas();
                    }
                    mostrarRealizarEfectivacion(titulo, proCodes.recauUTPLEfectivacion, setValores(),true,false);
                } else {
                    transUI.showMsgError(timeout, "Error en consulta");
                }
            }
        }
    }

    private void recauITSCO(String codEmpre, String titulo) {
        int select;
        String[] itemsContrapartida = {"Arancel", "Derechos"};
        String[] codDoc = {"01", "02"};
        int[] minLen = {0, 12};
        int[] maxLen = {12, 12};
        int[] inputTypes = {InputType.TYPE_CLASS_TEXT, InputType.TYPE_CLASS_TEXT};

        select = seleccionarMenus(itemsContrapartida, codDoc, titulo);
        if (select >= 0) {
            String contrapartida = ingresoContrapartida(titulo, "Contrapartida", maxLen[select], inputTypes[select], minLen[select]);

            if(!contrapartida.equals("")){
                setFieldsTokens(codEmpre, contrapartida, codDoc[select]);
                if (enviarConsulta(proCodes.recauITSCOConsulta)) {
                    InitTrans.tkn17.clean();
                    mostrarRealizarEfectivacion(titulo, proCodes.recauITSCOEfectivacion, setValores(),true,false);
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

    private void multiFacturas(){
        String[] itemsV = InitTrans.tkn17.items;
        String[] codItemsV = InitTrans.tkn17.codItem;
        byte[][] valoresItems = InitTrans.tkn17.valorItem;

        if(itemsV == null){
            transUI.showMsgError(timeout, "Error de token 17");
        }else{
            int seleccionarMenu = seleccionarMenus(itemsV, codItemsV, InitTrans.tkn17.titulo);
            Amount = Integer.parseInt(ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(valoresItems[seleccionarMenu])));
            //Amount = ISOUtil.bcd2int(valoresItems[seleccionarMenu], 0, valoresItems[seleccionarMenu].length);
            ISOUtil.bcd2str(InitTrans.tkn06.getCosto_servicio(), 0, InitTrans.tkn06.getCosto_servicio().length);
            Comision = Long.parseLong(ISOUtil.bcd2str(InitTrans.tkn06.getCosto_servicio(), 0, InitTrans.tkn06.getCosto_servicio().length));
            TotalAmount = Amount + Comision;
            para.setAmount(Amount);

            InitTrans.tkn17.setTkn17(codItemsV[seleccionarMenu], itemsV[seleccionarMenu], InitTrans.tkn17.titulo, valoresItems[seleccionarMenu]);
        }
    }

    private String[] setValores(){
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
}