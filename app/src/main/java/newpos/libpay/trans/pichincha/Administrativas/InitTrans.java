package newpos.libpay.trans.pichincha.Administrativas;

import android.content.Context;
import android.util.Log;


import java.util.ArrayList;
import java.util.Arrays;

import cn.desert.newpos.payui.base.PayApplication;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler.ClsConexion;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Actividad;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Sector;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.UserCentralizado;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn01;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn03;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn06;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn07;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn08;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn09;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn11;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn12;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn13;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn14;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn15;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn16;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn17;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn19;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn20;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn22;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn27;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn25;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn26;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn28;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn30;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn33;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn38;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn39;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn40;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn43;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn47;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn48;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn49;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn60;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn80;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn81;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn82;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn90;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn91;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn93;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkn98;
import cnb.pichincha.wposs.mivecino_pichincha.tokns.Tkns;
import cnb.pichincha.wposs.mivecino_pichincha.tools.Wrlg;
import newpos.libpay.global.TMConfig;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.manager.DparaTrans;
import newpos.libpay.trans.pichincha.parsinginitemvtables.ParsingInitEMV;
import newpos.libpay.trans.pichincha.parsinginitemvtables.TableEMVConf;
import newpos.libpay.trans.pichincha.parsinginitemvtables.TableEMVKey;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

/**
 *
 */

public class InitTrans extends FinanceTrans implements TransPresenter {

    public static boolean initEMV = false;
    public static boolean initialization = false;
    public static boolean loginCentral = false;
    public static boolean cierreRealizado = false;
    public static boolean isNeedConfirmTime = false;
    public static boolean isReversalTrans = false;
    public static Tkns tkns;
    public static Tkn01 tkn01;
    public static Tkn03 tkn03;
    public static Tkn06 tkn06;
    public static Tkn07 tkn07;
    public static Tkn08 tkn08;
    public static Tkn09 tkn09;
    public static Tkn11 tkn11;
    public static Tkn12 tkn12;
    public static Tkn13 tkn13;
    public static Tkn14 tkn14;
    public static Tkn15 tkn15;
    public static Tkn16 tkn16;
    public static Tkn17 tkn17;
    public static Tkn19 tkn19;
    public static Tkn20 tkn20;
    public static Tkn22 tkn22;
    public static Tkn27 tkn27;
    public static Tkn25 tkn25;
    public static Tkn26 tkn26;
    public static Tkn28 tkn28;
    public static Tkn30 tkn30;
    public static Tkn33 tkn33;
    public static Tkn38 tkn38;
    public static Tkn39 tkn39;
    public static Tkn40 tkn40;
    public static Tkn43 tkn43;
    public static Tkn47 tkn47;
    public static Tkn48 tkn48;
    public static Tkn49 tkn49;
    public static Tkn60 tkn60;
    public static Tkn80 tkn80;
    public static Tkn81 tkn81;
    public static Tkn82 tkn82;
    public static Tkn90 tkn90;
    public static Tkn91 tkn91;
    public static Tkn93 tkn93;
    public static Tkn98 tkn98;
    public static Usuario person;
    public static UserCentralizado user;
    public static ArrayList<TableEMVConf> arrayListBuffer_TableEMVConf;
    public static ArrayList<TableEMVKey>  arrayListBuffer_TableEMVKey;
    public static String ultimoRecibo = "NO";
    public static String ultimoReciboSecuencial = "";
    public static Boolean isMensaje60 = false;
    public static String mensaje60 = " ";

    public static byte[] workingKey;
    public static byte[] tk_Key;
    public static long time = 45000;
    private int timeOutScreensInit = 5 * 1000;
    private byte[] respData;
    private String rspCode;
    private String Field59 = "";
    private ParsingInitEMV parsingInitEMV;
    private String catalogo_Sectores = "00";
    private String catalogo_Laboral = "01";
    private String catalogo_Provincias_INEN = "02";
    private String catalogo_Provincias_ATM = "03";
    private int indice = 0;
    ArrayList<String> codItems = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();

    ClsConexion bd;

    public static Wrlg wrlg;

    public InitTrans(Context ctx, String transEname , TransInputPara p) {
        super(ctx, transEname);
        para = p ;
        setTraceNoInc(true);
        TransEName = transEname ;
        if(para != null){
            transUI = para.getTransUI();
        }
        isReversal = false;
        isSaveLog = false;
        isDebit = false;
        isProcPreTrans = true;
        isProcSuffix = false;
        p.setNeedPrint(false);
        p.setNeedAmount(false);
        bd = new ClsConexion(ctx);
    }

    /**
     *
     */
    public static void initVarInitialization(){
        tkns = new Tkns();
        tkn01 = new Tkn01();
        tkn03 = new Tkn03();
        tkn06 = new Tkn06();
        tkn07 = new Tkn07();
        tkn08 = new Tkn08();
        tkn09 = new Tkn09();
        tkn11 = new Tkn11();
        tkn12 = new Tkn12();
        tkn13 = new Tkn13();
        tkn14 = new Tkn14();
        tkn15 = new Tkn15();
        tkn16 = new Tkn16();
        tkn17 = new Tkn17();
        tkn19 = new Tkn19();
        tkn20 = new Tkn20();
        tkn22 = new Tkn22();
        tkn25 = new Tkn25();
        tkn26 = new Tkn26();
        tkn27 = new Tkn27();
        tkn28 = new Tkn28();
        tkn30 = new Tkn30();
        tkn33 = new Tkn33();
        tkn38 = new Tkn38();
        tkn39 = new Tkn39();
        tkn40 = new Tkn40();
        tkn43 = new Tkn43();
        tkn47 = new Tkn47();
        tkn48 = new Tkn48();
        tkn49 = new Tkn49();
        tkn60 = new Tkn60();
        tkn80 = new Tkn80();
        tkn81 = new Tkn81();
        tkn82 = new Tkn82();
        tkn90 = new Tkn90();
        tkn91 = new Tkn91();
        tkn93 = new Tkn93();
        tkn98 = new Tkn98();
        person = new Usuario();
        user = new UserCentralizado();
        arrayListBuffer_TableEMVConf = new ArrayList<TableEMVConf>();
        arrayListBuffer_TableEMVKey = new ArrayList<TableEMVKey>();
        wrlg = new Wrlg();
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583 ;
    }

    @Override
    public void start() {
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
        transUI.newHandling(context.getResources().getString(R.string.procesando), timeout, Tcode.Mensajes.inicalizacion);
        //retVal = init();
        verificarCambioDeDia();
        if(inicializacion()){
            transUI.newHandling(context.getResources().getString(R.string.procesando),timeout , Tcode.Mensajes.inicializandoEmv);
            InitTrans.tkn07.setMensaje(null);
            if(initEmv()){
                InitTrans.initialization = true;
                InitTrans.initEMV = true;
                InitTrans.tkn07.setMensaje(null);
                transUI.newHandling("PARAMETROS",timeout , Tcode.Mensajes.conectandoConBanco);
                if (procesarCatalogos()){
                    transUI.showSuccess(timeOutScreensInit, Tcode.Mensajes.inicializacionExitosa, "");
                } else {
                    transUI.showSuccess(timeOutScreensInit, Tcode.Mensajes.inicializacionExitosa, "Catalogos no descargados");
                }
            }else{
                transUI.showMsgError(timeout, "Inicializacion EMV fallida");
            }
        } else {
            transUI.showMsgError(timeout, "Inicializacion fallida");
        }
    }

    /**
     * Todos los dias se debe eliminar las Tablas de catalogos
     * Para recargar si llegaran a tener nuevos datos
     */
    private void verificarCambioDeDia() {
        bd.dropTableCatalogosCambioDia();
    }

    private boolean inicializacion(){
        boolean ret = false;
        iso8583.clearData();
        setFixedDatas();
        MsgID = "0800";
        ProcCode = "930000";
        Field63 = packField63();
        Amount = -1;
        MerchID = "               ";
        int retPre = newPrepareOnline(0);
        if(retPre == 0 || retPre == 98){
            if(iso8583.getfield(48) != null) {
                String bussinesType = String.valueOf(ISOUtil.stringToHex(ISOUtil.bcd2str(InitTrans.tkn80.getTipoNegocio(), 0, InitTrans.tkn80.getTipoNegocio().length)));
                TMConfig.getInstance().setmodoOperacion(bussinesType).save();
                PayApplication.initKeysPichincha();
                ret = true;
            }else {
                transUI.showMsgError(timeout, "Validación del campo 48 nula");
            }
        }
        return ret;
    }

    public boolean initEmv(){
        boolean ret;
        iso8583.clearData();
        setFixedDatas();
        MsgID = "0800";
        ProcCode = "940000";
        int retPre = sendEMV();
        if(retPre == 0){
            parsingInitEMV = new ParsingInitEMV(Field59);
            parsingInitEMV.parsingFld59();
            DparaTrans.loadAIDCAPK2EMVKernelPichincha();
            isNeedConfirmTime = validaHora();
            ret = true;
        } else {
            ret = false;
        }
        return ret;
    }

    private boolean procesarCatalogos(){
        boolean isOk = false;
        String[] catalogos = new String[]{catalogo_Sectores, catalogo_Laboral, catalogo_Provincias_INEN, catalogo_Provincias_ATM};
        for (int x = 0; x < catalogos.length; x++){
            codItems.clear();
            items.clear();
            isOk = descargaParametros(catalogos[x]);
            InitTrans.tkn93.clean();
            if (!isOk)
                break;
        }
        return isOk;
    }

    public boolean descargaParametros(String catalogo){
        boolean ret;
        int retPrep;
        iso8583.clearData();
        setFixedDatas();
        MsgID = "0800";
        ProcCode = "960000";
        RspCode = null;
        EntryMode = "0021";
        TermID = TMConfig.getInstance().getTermID();
        Field48 = InitTrans.tkn17.packTkn17_InitCatalogos(catalogo);
        Field48 += InitTrans.tkn93.packTkn93();
        Field63 = packField63();
        retPrep = newPrepareOnline(0);
        if (retPrep == 0){
            ret = true;
            byte[] estadoTok93_0 = new byte[]{0x00};
            byte[] estadoTok93_1 = new byte[]{0x01};
            byte[] estadoTok93_2 = new byte[]{0x02};

            if (tkn17.titulo != null) {
                if (!catalogo.equals(tkn17.titulo.trim())) {
                    transUI.showMsgError(timeout, "Catalogo no concuerda");
                    return false;
                } else {
                    if (Arrays.equals(InitTrans.tkn93.getEstadoMsg(), estadoTok93_0)) {
                        codItems.addAll(Arrays.asList(tkn17.codItem));
                        items.addAll(Arrays.asList(tkn17.items));
                        if (catalogo.equals(catalogo_Sectores)) {
                            procesarSectores(codItems, items);
                        } else {
                            procesarCatalogos(codItems, items, catalogo);
                        }
                    } else if (Arrays.equals(InitTrans.tkn93.getEstadoMsg(), estadoTok93_1)) {
                        codItems.addAll(Arrays.asList(tkn17.codItem));
                        items.addAll(Arrays.asList(tkn17.items));
                        descargaParametros(catalogo);
                    } else if (Arrays.equals(InitTrans.tkn93.getEstadoMsg(), estadoTok93_2)) {
                        codItems.addAll(Arrays.asList(tkn17.codItem));
                        items.addAll(Arrays.asList(tkn17.items));
                        if (catalogo.equals(catalogo_Sectores)) {
                            procesarSectores(codItems, items);
                        } else {
                            procesarCatalogos(codItems, items, catalogo);
                        }
                    }
                }
            }
        } else {
            ret = false;
        }
        return ret;
    }

    private void procesarSectores(ArrayList<String> codItems, ArrayList<String> items) {
        String[] partesSectores;
        String[] partesActividades;

        for (int x = 0; x < codItems.size(); x++){
            String s = codItems.get(x);
            String a = items.get(x);
            partesSectores = new String[2];
            int comaSectores = s.indexOf("^");
            partesSectores[0] = s.substring(0, comaSectores);
            partesSectores[1] = s.substring(comaSectores + 1);

            partesActividades = new String[2];
            int comaActividades = a.indexOf("^");
            partesActividades[0] = a.substring(0, comaActividades);
            partesActividades[1] = a.substring(comaActividades + 1);

            Log.d("SECTORES ", "id_sector: " + partesSectores[0] + " name_Sector: " + partesSectores[1]);
            Log.d("ACTIVIADES ", "id_actividad: " + partesActividades[0] + " name_actividad: " + partesActividades[1] + " id_sector: " + partesSectores[0]);
            bd.insertSectorEconomico(partesSectores[0], partesSectores[1]);
            bd.insertActividadEconomica(partesActividades[0], partesActividades[1], partesSectores[0]);

        }

    }

    private void procesarCatalogos(ArrayList<String> codItems, ArrayList<String> items, String catalogo) {

        if(codItems.size() == items.size()){
            for(int i = 0; i<codItems.size(); i++ ){
                if (catalogo.equals(catalogo_Laboral)) {
                    bd.insertSituacionLaboral(codItems.get(i), items.get(i));
                } else if (catalogo.equals(catalogo_Provincias_INEN)){
                    bd.insertProvincias(codItems.get(i), items.get(i), false);
                } else if (catalogo.equals(catalogo_Provincias_ATM)) {
                    bd.insertProvincias(codItems.get(i), items.get(i), true);
                }
            }
        }else{
            transUI.showMsgError(timeout, "No concuerda informacion de Catalgos 1");
        }
    }

    public int sendEMV(){
        int ret = 0;
        int retPrep;
        String procCode;
        RspCode = null;
        MerchID = null;
        Field63 = null;
        retPrep = newPrepareOnline(0);
        if(retPrep == 0){
            procCode = iso8583.getfield(3);
            Field59 += iso8583.getfield(59);
            if(!procCode.equals("940000")){
                ProcCode = procCode;
                sendEMV();
            }
        }else{
            ret = retPrep;
        }
        return ret;
    }

    private boolean validaHora(){
        boolean rta = false;
        int horaActual = Integer.parseInt(PAYUtils.getHMS().substring(0,4));
        int horaServidor = Integer.parseInt(iso8583.getfield(12).substring(0,4));
        String fechaActual = PAYUtils.getLocalDate();
        String fechaServidor = iso8583.getfield(13);
        int diferencia = horaServidor - horaActual;
        if (diferencia < 0){
            diferencia = diferencia * (-1);
            rta = true;
        }
        if(diferencia <= 5 && fechaActual.equals(fechaServidor)){
            rta = false;
        }else {
            rta = true;
        }
        return rta;
    }

}
