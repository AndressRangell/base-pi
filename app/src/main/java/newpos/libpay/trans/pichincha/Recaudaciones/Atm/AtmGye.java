package newpos.libpay.trans.pichincha.Recaudaciones.Atm;

import android.content.Context;
import android.text.InputType;

import java.util.ArrayList;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler.ClsConexion;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Catalogo;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Recaudaciones.Recaudaciones;
import newpos.libpay.utils.ISOUtil;

public class AtmGye extends Recaudaciones {
    /**
     * 金融交易类构造
     *
     * @param ctx
     * @param transEname
     * @param tipoTrans
     * @param p
     */
    public AtmGye(Context ctx, String transEname, String tipoTrans, TransInputPara p) {
        super(ctx, transEname, tipoTrans, p);
    }


    public void pagoAtm() {
        String titulo ="ATM / GYE";
        int select;
        String[] regProv;
        String[] tipoSolicitud = {"Citaciones","Solicitud","Renovación"};
        String[] codSolicitud = {"01","02","03"};

        int[] maxLen = {10, 13, 20, 20, 7};
        int[] minLen = {10, 13, 0, 0, 7};
        int[] inputTypes = {1010, 1010, InputType.TYPE_CLASS_TEXT, InputType.TYPE_CLASS_TEXT, InputType.TYPE_CLASS_TEXT};

        select = seleccionarMenus(tipoSolicitud,codSolicitud,"ATM / GYE");

        if (select >= 0){

            if (codSolicitud[select].equals("01")){

                String[] tipoIdentificacion = {context.getResources().getString(R.string.cedula), context.getResources().getString(R.string.ruc), context.getResources().getString(R.string.pasaporte),"Cedula de Residencia", "Placa"};
                String[] codIdentificacion = {"01","02","06","07","08"};
                int select2 = seleccionarMenus(tipoIdentificacion,codIdentificacion,tipoSolicitud[select]);

                if (select2 >= 0){

                    String contrapartida = ingresoContrapartidaRecaud(titulo, "Ingrese  "+tipoIdentificacion[select2],maxLen[select2],inputTypes[select2],minLen[select2]);

                    if (!contrapartida.equals("")){

                        InitTrans.tkn03.setCodigoEmpresa(ISOUtil.padleft(codSolicitud[select].substring(1),10,'0').getBytes());
                        InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida.toUpperCase(),34,' ').getBytes());
                        InitTrans.tkn13.setTipoPago( ISOUtil.str2bcd(codIdentificacion[select2],false));
                        regProv = seleccionarProvincia();

                        if (regProv != null){
                            transConfirmacionPago(titulo,"4942"+codSolicitud[select],codSolicitud[select]);
                        }
                    }
                }

            } else {

                selecTipoDocGYE(titulo,tipoSolicitud[select],codSolicitud[select],select,codSolicitud[select]);
            }

        }

    }

    private void transConfirmacionPago(String titulo, String proceCode, String cod) {
        Amount = -1;
        if (enviarConsulta(proceCode)){
            String info = null;

            switch (cod){
                case "01":
                    info = proCodes.recauATMEfectivacionCitaciones;
                    break;
                case "02":
                    info = proCodes.recauATMEfectivacionSolicitud;
                    break;
                case "03":
                    info = proCodes.recauATMEfectivacionRenovacion;
                    break;
                case "04":
                    info = proCodes.recauATMEfectivacionRodaje;
                    break;
                default:

                    break;
            }

            if (info != null){
                mostrarRealizarEfectivacion(titulo,info,setValores(),true,false);
            }


        } else {
            transUI.showMsgError(timeout, "Error en consulta");
        }

    }

    private void selecTipoDocGYE(String titulo, String tipoSolicitud, String codSolicitud, int select, String cod) {
        String[] regProv;
        int minLen = 0;
        int maxLen = 0;
        String categoria = null;

        switch (codSolicitud){
            case "02":
                maxLen = 30;
                minLen = 5;
                categoria = tipoSolicitud;
                break;
            case "03":
            case "04":
                maxLen = 7;
                minLen = 7;
                categoria = "Placa";
                break;

        }
        String contrapartida = ingresoContrapartidaRecaud(titulo, "Ingrese  "+categoria,maxLen,InputType.TYPE_CLASS_TEXT,minLen);
        if (!contrapartida.equals("")){
            InitTrans.tkn03.setCodigoEmpresa(ISOUtil.padleft(codSolicitud.substring(1),10,'0').getBytes());
            InitTrans.tkn03.setContrapartida(ISOUtil.padright(contrapartida.toUpperCase(),34,' ').getBytes());
            regProv = seleccionarProvincia();
            if (regProv != null){
                transConfirmacionPago(titulo, "4942"+codSolicitud, cod);
            }
        }
    }

    private String[] seleccionarProvincia() {
        String[] selecRegion = new String[1];
        String[] provincias;
        String[] codigoProvincia;
        int selectregion;

        ClsConexion conexion = new ClsConexion(context);

        final ArrayList<Catalogo> sectores = conexion.selectAllProvincias(true);

        provincias = new String[sectores.size()];
        codigoProvincia = new String[sectores.size()];

        for (int i = 0; i < sectores.size(); i++) {
            provincias[i] = sectores.get(i).toString();
            codigoProvincia[i] = (sectores.get(i)).getId_item();
        }


        selectregion = seleccionarMenus(provincias, codigoProvincia, "ATM / GYE");

        if (selectregion >= 0){
            String[] selecRegionTemp = new String[2];
            System.out.println("PROVINCIAS **** "+codigoProvincia[selectregion]+provincias[selectregion]);
            selecRegionTemp[0] = codigoProvincia[selectregion]+"|";
            selecRegionTemp[1] = provincias[selectregion];

            InitTrans.tkn40.setCantidadItems(2);
            InitTrans.tkn40.setListaItems(selecRegionTemp);

        }

        return selecRegion;
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


}
