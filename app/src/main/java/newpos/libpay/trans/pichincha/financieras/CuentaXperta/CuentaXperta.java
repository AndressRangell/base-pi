package newpos.libpay.trans.pichincha.financieras.CuentaXperta;

import android.content.Context;
import android.text.InputType;
import android.util.Log;

import com.android.desert.keyboard.InputInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.Utils;
import newpos.libpay.global.TMConfig;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

public class CuentaXperta extends FinanceTrans implements TransPresenter {

    private static final String CANCELAR = "cancelar";
    private static final String MENSAJE_TITULO = "Entrega de CNB para aprobación de cliente";

    private static final String proCodeValidacion = "360010";
    private static final String proCodeCreacion = "360000";
    private static final String proCodeOTPCreacion = "360011";
    private static final String proCodeOTPRepo = "370011";
    private static final String proCodeValidacionFaceId = "370010";
    private String [] listaKit;
    private boolean enviarConsulta = false;
    private  String[] listaContrato, listaApertura, listaCapturas;
    private String email, celular;
    int count = 1;
    private int contadorOtp = 0;

    /**
     * 金融交易类构造
     *
     * @param ctx
     * @param transEname
     */
    public CuentaXperta(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p;
        setTraceNoInc(true);
        TransEName = transEname;
        if (para != null) {
            transUI = para.getTransUI();
        }
        isReversal = false;
        isProcPreTrans = true;
    }

    @Override
    public void start() {
        int select;
        InitTrans.wrlg.wrDataTxt("Inicio transaccion " + TransEName);

        String[] items = {"Apertura cuenta básica", "Reposición kit", "Cambio de clave tarjeta débito"};
        select = seleccionarMenus(items, "Cuenta básica");

        String titulo = items[select];
        switch (select) {

            case 0:
                aperturaCuentaBasica(titulo);
                break;

            case 1:
                reposicionKit(titulo);
                break;

            case 2:
                cambioClaveBasica();
                break;

        }
    }

    private void aperturaCuentaBasica(String titulo) {
        String[] mensajes = new String[]{titulo, "¿Tiene residencia fiscal diferente a Ecuador?", "Sí", "No"};
        String auxRet, retDirec, retDatos, retKit, correoConfirmacion;

        boolean isOk;
        int retPrep;
        setFixedDatas();
        MsgID = "0100";
        ProcCode = proCodeCreacion;
        RspCode = null;
        EntryMode = "0021";
        Amount = -1;
        Field63 = packField63();
        valdiarTkn01();
        TermID = TMConfig.getInstance().getTermID();
        MerchID = TMConfig.getInstance().getMerchID();

        try {
            String cedulaScan = ingresoCedulaScan("Crear cuenta básica");
            String[] listaDatosCed = cedulaScan.split("@");
            if (!cedulaScan.equals("")) {
                auxRet = residenciaFiscal(mensajes);
                if (!auxRet.equals("")) {
                    InitTrans.tkn13.clean();
                    InitTrans.tkn40.setListaItems(listaDatosCed); // cedula, codigo dactilar, fecha expedicion
                    Field48 = InitTrans.tkn40.packToken();
                    retPrep = newPrepareOnline(inputMode);
                    if (retPrep == 0) {
                        isOk = true;
                        listaContrato = InitTrans.tkn40.getListaItems();
                        //nombre, fecha nacimiento, estado civil, genero, celular, mail, cupo cajero, tnp, cupo CNB, provincia, ciudad, referencia Ub, RUC CNB, establecimiento CNB, valida FACE ID
                        if(listaContrato.length==15){
                            if (listaContrato[14].equals("S")){
                                isOk = procesarFaceID(proCodeValidacion, listaDatosCed[0]);
                                count = 1;
                                if (isOk){
                                    isOk = sucessFaceID();
                                } else {
                                    transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                                }
                            }
                        }else{
                            isOk = false;
                            transUI.showMsgError(timeout, "Error en obtención de informacion Creacion Xperta");
                        }


                        if (isOk) {
                            retDirec = datos_direccion();
                            System.out.println(retDirec);
                            if (!retDirec.equals("")) {
                                retDatos = datos_complementarios();
                                System.out.println(retDatos);
                                if (!retDatos.equals("")) {


                                    String [] listaDireccion = retDirec.split("~");
                                    String [] retDatosCompleto = retDatos.split("Ø");
                                    String [] listaEconomico = retDatosCompleto[0].split("~");
                                    String [] listaEconomicoNombre = retDatosCompleto[1].split("~");

                                    if (verificacionInfoKit(listaDatosCed,listaContrato,null,listaDireccion,listaEconomico,listaEconomicoNombre,false)){

                                        isOk = otpXperta(proCodeOTPCreacion, "0200", "360100", listaApertura);
                                        System.out.println("isOk " + isOk);
                                        if (isOk) {
                                            correoConfirmacion = listaApertura[3];
                                            String msgFinal = "También puedes solicitar tu contrato en la agencia del Banco de tu preferencia.";
                                            if (mensanjeConfiramcion("Tu cuenta básica con éxito. Tu contrato", correoConfirmacion, msgFinal)) {
                                                if (confirmacion("360100", "Creación cuenta básica")) {
                                                    //code: posición de string a mostrar en pantalla, lee un archivo en res/values/arrays arrays.xml-> mensajes_handling
                                                    transUI.showSuccess(timeout, 15, "");
                                                } else {
                                                    transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                                                }
                                            }
                                        } else {
                                            transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            InitTrans.wrlg.wrDataTxt("Excepción activada - CuentaXperta.java" + e);
        }
    }

    private boolean sucessFaceID() {
        boolean res = false;
        String ret;
        InputInfo inputInfo = transUI.showSuccesView(timeout,"Registro FaceID", "Reconocimiento facial se realizó \n con éxito",true,false);
        if (inputInfo.isResultFlag()) {
            ret = inputInfo.getResult();
            if (ret.equals("Exito")) {
                res = true;
            }
        }
        return res;
    }

    private boolean verificacionInfoKit(String[] listaDatosCed, String[] listaContrato, String[] datos,String[] listaDireccion, String[] listaEconomico, String[] listaEconomicoNombre, boolean iskitReposicion) {
        String retKit = null;
        boolean isOk = false;
        int retPrep;
        int contador = 0;
        String codigoProceso = "00";
        boolean ret = false;
        System.out.println("verificacionInfoKit *******************");
        while(!codigoProceso.equals("03")){
            System.out.println("while *******************");
            if (iskitReposicion){
                if (datos != null){
                    retKit = info_kit_reposicion(datos[0], datos[1]);
                    listaKit = retKit.split("~");
                    listaCapturas = new String[7];
                    listaCapturas[0] = listaDatosCed[0];  // Cedula
                    listaCapturas[1] = datos[0];       // celular
                    listaCapturas[2] = listaContrato[0];  // Nombre Apellido
                    if (!email.equals(listaKit[2])) {
                        listaCapturas[3] = listaKit[2];
                    } else {
                        listaCapturas[3] = datos[1];
                    }
                    //listaCapturas[3] = listaKit[2];       // mail
                    listaCapturas[4] = listaKit[0];      // numero de Kit
                    listaCapturas[5] = listaDatosCed[1];  // Código dactilar
                    listaCapturas[6] = listaDatosCed[2];  // Fecha expedición

                    System.out.println("mostrarContratoReposicion()");
                    isOk = mostrarContrato(proCodeOTPRepo, MENSAJE_TITULO, listaContrato, listaCapturas, null);
                    System.out.println("isOk " + isOk);
                }
            }else {
                retKit = info_kit(listaKit);
                if (!retKit.equals("")){
                    listaKit = retKit.split("~");
                    System.out.println(listaEconomico);
                    System.out.println(listaEconomicoNombre);
                    listaApertura = new String[14];
                    listaApertura[0] = listaDatosCed[0];  // Cedula
                    listaApertura[1] = listaKit[1];       // celular
                    listaApertura[2] = listaContrato[0];  // Nombre Apellido
                    listaApertura[3] = listaKit[2];       // mail
                    listaApertura[4] = listaDireccion[0]; // Calle principal
                    listaApertura[5] = listaDireccion[1]; // Lote
                    listaApertura[6] = listaDireccion[2]; // Calle Secundaria
                    listaApertura[7] = listaEconomico[0]; // Sector Economico
                    listaApertura[8] = listaEconomico[1]; // Actividad Economica
                    listaApertura[9] = listaEconomico[2]; // ingresos
                    listaApertura[10] = listaEconomico[3];// Situacion laboral
                    listaApertura[11] = listaKit[0];      // numero de Kit
                    listaApertura[12] = listaDatosCed[1]; // Código dactilar
                    listaApertura[13] = listaDatosCed[2]; // Fecha expedición
                    System.out.println("mostrarContrato()");
                    isOk = mostrarContrato(proCodeOTPCreacion, MENSAJE_TITULO, listaContrato, listaApertura, listaEconomicoNombre);

                    System.out.println("isOk " + isOk);
                }
            }

            if (isOk){

                String[] info = new String[4];
                info[0] = listaDatosCed[0];
                if (iskitReposicion) {
                    info[1] = datos[0];
                } else {
                    info[1] = listaKit[1];
                }
                info[2] = listaContrato[0];
                info[3] = listaKit[0];

                RspCode = null;
                ProcCode = iskitReposicion ? proCodeOTPRepo : proCodeOTPCreacion;
                //ProcCode = proCodeOTPCreacion;
                Field48 = null;



                InitTrans.tkn13.setTipoPago(ISOUtil.str2bcd(codigoProceso,false));
                InitTrans.tkn40.setListaItems(info);
                Field48 = InitTrans.tkn13.packTkn13()+ InitTrans.tkn40.packToken();

                retPrep = newPrepareOnline(inputMode);

                if(retPrep == 0){
                    ret = true;
                    enviarConsulta = true;
                    break;
                } else if (retPrep == 91) {
                    codigoProceso = ISOUtil.bcd2str(InitTrans.tkn13.getTipoPago(),0, InitTrans.tkn13.getTipoPago().length);
                    contador++;

                    if (codigoProceso.equals("03")){
                        ret = false;
                        transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                        break;
                    } else {
                        transUI.handlingOTP(false,InitTrans.tkn07.obtenerMensaje());
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    ret = false;
                    transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                    break;
                }
            }else {
                break;
            }
        }
        return ret;
    }

    String armarContratoReposicion(String[] aListaContrato, String []aListaCaptura){
        StringBuilder contrato = new StringBuilder();

        String referenciaUbicacion = aListaContrato[11];
        String nombreCNB = aListaContrato[13];
        String lugarFecha = PAYUtils.getLocalDate2();
        String tipoTarjeta = "KIT CUENTA EXPERTA";
        String numeroKit = aListaCaptura[4];
        String cupoMaxAutoPOS = aListaContrato[8];
        String cupoMaxAutoCajeros = aListaContrato[6];
        String cupoMaxAutoTNP = aListaContrato[7];

        contrato.append("SOLICITUD REPOSICIÓN DE TARJETA DE DÉBITO (KIT CUENTA BÁSICA)" + "\n\n");

        contrato.append(referenciaUbicacion + "\n");

        contrato.append("Lugar y Fecha " + nombreCNB + " " + lugarFecha + "\n\n");

        contrato.append("Señor(es):\n" + "BANCO PICHINCHA C.A.\n" + "Ciudad. " + referenciaUbicacion + "\n" + "De mi consideración:\n\n");

        contrato.append("Por medio de la presente solicito la reposición de la tarjeta de débito (Kit cuenta básica) de acuerdo al " +
                "siguiente detalle:\n\n");

        contrato.append("1. TIPO DE TARJETA\n" + tipoTarjeta + "\n\n");
        contrato.append("2. NÚMERO DE KIT EXPERTA\n" + numeroKit + "\n\n");
        contrato.append("3. CUPOS Y COBERTURA ESTABLECIDOS PARA EL USO DE LA TARJETA DE DEBITO\n\n");

        contrato.append("CUPOS PARA CONSUMO/POS\n" + cupoMaxAutoPOS + "\n\n");
        contrato.append("CUPO PARA RETIRO CAJEROS\n" + cupoMaxAutoCajeros + "\n\n");
        contrato.append("CUPO TNP\n" + cupoMaxAutoTNP + "\n\n");

        contrato.append("*Cupos Máximos asignados a la fecha\n\n");

        contrato.append("Los cupos que autorizo(amos) a través del presente documento, se entenderán como los máximos asignados de acuerdo " +
                "al producto emitido por el BANCO, para realizar Compras en establecimientos, trasferencias y/o retiros en los sistemas de " +
                "Cajeros Automáticos (A.T.M.) y transacciones con Medios de Pago Electrónicos y Servicios Electrónicos.\n\n" +
                "La cobertura de las tarjetas dependerá del tipo de producto. Tarjeta de Débito Internacional/Preferente. - cajeros y " +
                "establecimientos afiliados a VISA a nivel Nacional e Internacional.\n\n" +
                "Por su seguridad, la cantidad de transacciones en cajero y/o compras en establecimiento(s) que el (los) cliente(s) puede " +
                "realizar con su tarjeta, serán determinadas por la(s) institución(es).\n\n");


        return contrato.toString();
    }

    /*
 tkn40Contrato : Token 40 de envio (100) en transaccion 360011 - GeneraOTP
 tkn40_OtherInfo : Token 40 de respuesta (110) en transaccion 360011 - GeneraOTP
 */
    private boolean mostrarContrato( String aProcCode, String titulo, String[] aListaContrato , String[] aListaApertura, String[] listaEconomicoNombre) {
        System.out.println("mostrarContrato");
        String ret = "";
        boolean rsp;
        String strinContrato = null;
        String leyenda = "";

        if (aListaApertura != null){
                try {
                    if(aProcCode.equals(proCodeOTPCreacion)){
                        System.out.println("armarContrato creacion");
                        strinContrato = armarContrato(titulo, aListaContrato, aListaApertura, listaEconomicoNombre );
                        leyenda = "Estimado cliente favor leer y aceptar el contrato de Apertura de cuenta Básica";
                    }else{
                        strinContrato = armarContratoReposicion(aListaContrato, aListaApertura);
                        leyenda = "Estimado cliente favor leer y aceptar el contrato de Apertura de cuenta Básica";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            String[] mensajes;
            mensajes = new String[]{leyenda, strinContrato, "Acepto términos y condiciones"};
            InputInfo inputInfo = transUI.showContrato(timeout, MENSAJE_TITULO, mensajes);
            if (inputInfo.isResultFlag()) {
                ret = inputInfo.getResult();
                if (ret.equals(CANCELAR)) {
                    transUI.showError(timeout, Tcode.T_user_cancel_operation);
                    rsp = false;
                } else {
                    rsp = true;
                }
            } else {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
                rsp = false;
            }
            return rsp;
    }

    /*
     tkn40Contrato : Token 40 de envio (100) en transaccion 360011 - GeneraOTP
     tkn40_OtherInfo : Token 40 de respuesta (110) en transaccion 360011 - GeneraOTP
     */
    private String armarContrato(String titulo, String[] aListaContrato, String[] aListaApertura, String[] listaEconomicoNombre) {
        StringBuilder contrato = new StringBuilder();

        String sNumCedula = aListaApertura[0];
        String sCallePrincipal = aListaApertura[4];
        String lote = aListaApertura[5];
        String sCalleSecundaria = aListaApertura[6];
        System.out.println("actividadEconomica");
        String actividadEconomica = listaEconomicoNombre[1];
        String sIngresosMensuales = aListaApertura[9];
        System.out.println("situacionLaboral");
        String situacionLaboral = listaEconomicoNombre[2];
        String sNumeroKit = aListaApertura[11];

        //nombre, fecha nacimiento, estado civil, genero, celular, mail, cupo cajero, tnp, cupo CNB, provincia, ciudad, referencia Ub, RUC CNB, establecimiento CNB, valida FACE ID

        String nombreApellido = aListaContrato[0];
        String fechaNacimiento = aListaContrato[1];
        String estadoCivil = aListaContrato[2];
        String genero = aListaContrato[3];
        String sNumCelular = aListaApertura[1];
        String mail = aListaApertura[3];

        String telDomicilio = "";

        String provincia = aListaContrato[9];
        String ciudadResidencia = aListaContrato[10];
        String referenciaUbicacion = aListaContrato[11];
        String rucCnb = aListaContrato[12];
        String establecimiento = aListaContrato[13];


        String edad = obtenerEdad(fechaNacimiento);

        contrato.append("N°. de kit: " +sNumeroKit+ "\n" + titulo + "\n\n");
        contrato.append("Señores:\n" + "BANCO PICHINCHA C.A.\n" + "Presente\n\n");

        contrato.append("El(La) infrascrito(a) " +nombreApellido+ ", por mis propios derechos, " +
                "manifiesto y solicito a ustedes la apertura de una Cuenta Básica, según las condiciones " +
                "que a continuación se detallan. Para efectos de esta solicitud, se denominará EL CLIENTE o TITULAR " +
                "a quien suscribe este instrumento, y EL BANCO al Banco Pichincha C.A.\n\n" + "EL CLIENTE para " +
                "la apertura de cuenta, detalla los siguientes datos personales:\n\n");

        contrato.append("Tipo de identificación: " + "C.I" + "\n\n" + "N° de indentificación:\n" +sNumCedula+ "\n\n"
                + "Fecha de nacimiento:\n" +fechaNacimiento+ " (dd/mm/aaaa)" + "\n\n" + "Edad: " + edad + "\n\n"
                + "Estado civil:\n" + estadoCivil + "\n\n" + "Género: " + genero + "\n\n" + "Teléfono de domicilio:\n"
                + telDomicilio + "\n\n" + "Teléfono celular:\n" + sNumCelular + "\n\n" + "Correo electrónico:\n"
                + mail + "\n\n" + "Actividad económica:\n" + actividadEconomica + "\n\n" + "Ingresos mensuales:\n"
                + sIngresosMensuales + "\n\n" + "Situación laboral:\n" + situacionLaboral + "\n\n" + "Dirección de domicilio\n"
                + "Provincia de residencia:\n" + provincia + "\n\n" + "Ciudad de residencia:\n" + ciudadResidencia + "\n\n"
                + "Calle principal y número de vivienda:\n" + sCallePrincipal +"  "+lote+"\n\n" + "Calle secundaria:\n" + sCalleSecundaria
                + "\n\n" + "Referencia de ubicación:\n" +referenciaUbicacion + "\n\n");

        contrato.append("1.\tCondiciones generales\n\n" + "PRIMERA: APERTURA.- EL CLIENTE solicita a EL BANCO la apertura de " +
                "una cuenta básica en Dólares de los Estados Unidos de América, sujeta a la normativa que regula el manejo de " +
                "este tipo de cuentas, y particularmente, a las cláusulas de este instrumento.\n" + "EL CLIENTE acepta y autoriza " +
                "que EL BANCO realice la debida verificación de la información proporcionada para la apertura de la cuenta; de manera " +
                "que si el resultado de dicha verificación no es exitoso, la cuenta no se activará." + "\n\n");


        contrato.append("SEGUNDA: OBJETO.- Con estos antecedentes y en atención a la solicitud de EL CLIENTE, EL BANCO apertura a su nombre" +
                " la cuenta básica N° " + sNumeroKit + " en Dólares de los Estados Unidos de América, la cual constituye una cuenta de depósitos" +
                " a la vista que permite a EL CLIENTE acceder a servicios financieros a través de los canales físicos y electrónicos determinados" +
                " para el efecto por medio de una tarjeta electrónica, con sujeción a lo dispuesto en la normativa financiera vigente.\n\n");

        contrato.append("TERCERA: OBLIGACIONES DE LAS PARTES.- El BANCO entrega a EL CLIENTE como efecto de la suscripción del presente " +
                "instrumento, un KIT DE APERTURA DE CUENTA BÁSICA, que contiene una tarjeta de débito con cobertura nacional para efectuar" +
                " todas las transacciones permitidas con la tarjeta para este tipo de cuentas; clave de seguridad inicial de acceso a los " +
                "canales transaccionales, misma que deberá ser sustituida inmediata y privadamente por EL CLIENTE; una guía ilustrativa de " +
                "uso de cuenta y de los canales electrónicos que ofrece EL BANCO; y una copia del presente documento.\n" + "Es responsabilidad " +
                "de EL BANCO la prestación de los servicios financieros solicitados por EL CLIENTE, de acuerdo con las condiciones de este " +
                "instrumento, los montos depositados en la cuenta y la confidencialidad de la información. Esta responsabilidad no incluye las" +
                " pérdidas o daños que sufra EL CLIENTE por causas no imputables a EL BANCO.\n\n");

        contrato.append("CUARTA: DISPONIBILIDAD DE FONDOS.- Independientemente de los medios/canales físicos, electrónicos, telefónicos o de internet " +
                "puestos a disposición de EL CLIENTE por parte de EL BANCO, para que pueda realizar las transacciones permitidas sobre su cuenta, " +
                "los fondos de la cuenta podrán ser retirados únicamente por EL TITULAR en cualquier momento que lo requiera a través de los canales " +
                "habilitados para retiros. EL BANCO pagará los fondos a EL CLIENTE a través de los medios y servicios puestos a su disposición.\n\n");

        contrato.append("QUINTA: DEPÓSITO EN CHEQUE(S).- El valor del(los) cheque(s) depositado(s) en la cuenta será(n) acreditado(s) únicamente " +
                "cuando éste(os) haya(n) sido efectivizado(s). Para el caso que existan valores en cheque(s) no efectivizado(s), EL BANCO queda" +
                " autorizado a efectuar el ajuste sin necesidad autorización adicional y no se responsabiliza de realizar ninguna gestión de " +
                "cobro para aquellos cheques que resulten devueltos y no pagados.\n\n");

        contrato.append("SEXTA: MOVIMIENTOS DE LA CUENTA.- EL BANCO pondrá a disposición de EL CLIENTE el registro de la transacción realizada a " +
                "través de comprobantes generados en cada transacción. Adicionalmente, EL CLIENTE acepta y conoce que debe revisar constantemente" +
                " las transacciones o movimientos realizados en su cuenta, a través de cualquiera de los canales físicos, electrónicos, telefónicos " +
                "o de Internet que EL BANCO pone a su disposición, considerándose efectuada una transacción, desde el momento en que conste la " +
                "información en el canal electrónico. En caso de que EL CLIENTE no objete la transacción o movimientos dentro de los 30 días calendario " +
                "siguientes a la fecha de la transacción, se entenderá su conformidad con la misma. En caso de presentarse alguna inconsistencia en las " +
                "transacciones efectuadas en la cuenta, EL CLIENTE podrá formular mediante comunicación escrita el reclamo correspondiente.\n\n");

        contrato.append("SÉPTIMA: INFORMACIÓN.- EL BANCO podrá enviar a EL CLIENTE las notificaciones o informaciones que le correspondan, " +
                "a la dirección física o correo electrónico detallados en este instrumento. EL CLIENTE se obliga a comunicar de cualquier forma " +
                "y de manera inmediata todo cambio en la dirección de domicilio, trabajo o correo electrónico y demás datos personales; y autoriza " +
                "expresamente a EL BANCO actualizar esta información en sus bases de datos.\n\n");

        contrato.append("OCTAVA: COMPENSACIÓN.- EL CLIENTE autoriza a EL BANCO debitar de su cuenta cualquier saldo deudor que provenga de " +
                "obligaciones crediticias exigibles a favor de EL BANCO, de cualquier tipo o clase que éstas sean, incluyendo obligaciones " +
                "directas o indirectas, o cualquier costo, gasto u honorario en el que hubiere incurrido por el cobro de dichas obligaciones " +
                "en los términos de la normativa vigente, o por tarifas, cargos o servicios financieros recibidos.\n\n");

        contrato.append("NOVENA: VIGENCIA.- La vigencia del presente instrumento será indefinida. No obstante las partes unilateralmente o de" +
                " común acuerdo, podrán darlo por concluido en cualquier momento. La terminación por parte de EL BANCO se efectuará previa " +
                "notificación a EL CLIENTE con 30 días de anticipación a través de cualquier canal de comunicación de EL BANCO, a la dirección" +
                " física o electrónica proporcionada por EL CLIENTE. Adicionalmente, EL BANCO podrá unilateralmente, sin necesidad de " +
                "notificación, cancelar la cuenta y/o inhabilitar el uso de la tarjeta electrónica si llegase a detectar: a) inactividad " +
                "por un período de 6 meses; b) falsedad o inexactitud de los datos proporcionados por EL CLIENTE o falta de entrega de la " +
                "información solicitada por EL BANCO; c) fuerza mayor, d) por existir movimientos inusuales que no correspondan al manejo de" +
                " la cuenta; e) por utilizar la cuenta para efectuar pagos y cobros a terceros y de terceros en forma reiterada sin autorización" +
                " de EL BANCO por no contar con los convenios especiales establecidos para el efecto; f) por falta de actualización de datos; g)" +
                " por cualquier otra causa que a criterio de EL BANCO justifique la cancelación; h) por mal uso de la cuenta o incumplimiento " +
                "de disposiciones legales. En estos casos, EL BANCO no recibirá depósitos en la cuenta aperturada y su saldo podrá ser retirado" +
                " en ventanilla o acreditado a otra cuenta que EL CLIENTE mantenga activa en EL BANCO como TITULAR de cuenta.\n\n");

        contrato.append("DÉCIMA: SERVICIOS ELECTRÓNICOS.- EL CLIENTE solicita a EL BANCO los habilitantes con los cuales pueda realizar transacciones" +
                " relacionadas con su cuenta y otras operaciones bancarias a través de medios/canales electrónicos, electromecánicos, telefónicos " +
                "o de Internet puestos a su disposición por EL BANCO, mediante clave(s) y/o tarjeta(s), sea(n) de débito, inteligente o del tipo " +
                "que EL BANCO le entregue (en adelante “la tarjeta”), considerando que EL CLIENTE declara conocer y aceptar las características " +
                "y condiciones referentes a dichas operaciones bancarias a través de medios/canales electrónicos ofrecidos por EL BANCO, asumiendo" +
                " EL CLIENTE la responsabilidad respecto a las transacciones que efectuare a través de estos medios/canales, así como la obligación" +
                " de custodiar debidamente su(s) tarjeta(s) y mantener en secreto su clave personal o seguridades adicionales.\n\n");

        contrato.append("DÉCIMA PRIMERA: DECLARACIONES.- EL CLIENTE declara que conoce y acepta: Que debe cumplir las condiciones que constan" +
                " en este instrumento. Que la(s) clave(s) de la(s) “tarjeta(s)” y/o códigos de seguridad y/o registros biométricos y/o " +
                "información personal y/o claves asignadas por EL BANCO y/o clave(s) generada(s) por EL CLIENTE (en adelante los Habilitantes)," +
                " constituyen la información necesaria para identificarle y acceder a los servicios brindados a través de medios/canales electrónicos," +
                " electromecánicos o telefónicos. Para acceder a los servicios por medios/canales electrónicos, se somete a los procedimientos" +
                " o mecanismos de seguridad que EL BANCO exija para el efecto. Que conoce plenamente los riegos y contingencias asociados." +
                " Por seguridad, EL CLIENTE deberá realizar de manera periódica el cambio de clave(s) y/o cuando EL BANCO lo solicite." +
                " EL CLIENTE acepta expresamente las responsabilidades que se ocasionen como consecuencia de la inobservancia a esta" +
                " cláusula, además del prolijo cuidado que debe tener de los Habilitantes, constituyéndose en una obligación de su parte," +
                " para evitar actos dolosos en su contra, que puedan causar perjuicios especialmente por transacciones, transferencias," +
                " retiros, solicitudes, operaciones, facilidades bancarias, conocimiento de información y en general otros actos que" +
                " pudieran efectuar terceras personas que lleguen a tener dicha información y/o Habilitantes por falta de precaución" +
                " o descuido de EL CLIENTE. EL BANCO podrá retener, bloquear o cancelar, lo que fuere del caso, los Habilitantes, en" +
                " cualquier tiempo, en los casos que por seguridad sean necesarios o de establecer la existencia de error en el ingreso" +
                " de la información de la clave o código de seguridad en varias ocasiones. Que EL BANCO no prestará los servicios" +
                " electrónicos, en el momento en que sean solicitados, de presentarse situaciones ajenas a su control de tipo técnico," +
                " tecnológico y otras relacionadas con casos fortuitos o de fuerza mayor. Que para la reposición de la tarjeta electrónica," +
                " deberá comprar un nuevo kit de cuenta básica y realizar el proceso de activación de la tarjeta; en este caso el número de cuenta" +
                " se mantiene, sin que la firma de los nuevos documentos implique o pueda considerarse como la apertura de otra cuenta básica a favor" +
                " del Cliente. Que en caso de pérdida, destrucción o robo de la tarjeta o de vulneración de la seguridad de la clave personal, " +
                "debe notificar inmediatamente a EL BANCO por los medios definidos por éste para el efecto; de omitirse este aviso, las " +
                "transacciones que fueren realizadas serán de responsabilidad de EL CLIENTE.\n\n");

        contrato.append("DÉCIMA SEGUNDA: CARGOS POR SERVICIOS.- EL CLIENTE manifiesta que conoce y acepta las tarifas y cargos que cobra " +
                "EL BANCO por los servicios que presta, los cuales le han sido informados y constan en el Tarifario de Servicios Financieros" +
                " publicado en agencias y en la página web de EL BANCO, y que han sido debidamente aprobados por los organismos de control de" +
                " acuerdo a la Ley; así como el hecho de que EL BANCO puede modificar dichas tarifas y cargos, previa fijación de la autoridad" +
                " competente, debiendo hacer conocer a EL CLIENTE de tal modificación, inclusive a través de la divulgación en sus oficinas, a" +
                " través de medios electrónicos y si fuere del caso, a través de una publicación por la prensa. La utilización de los servicios" +
                " por parte de EL CLIENTE, mediante la realización de cualquier transacción desde o con cargo a la cuenta, se entenderá como " +
                "aceptación de los cargos y condiciones relacionados con ellos. EL CLIENTE autoriza expresamente a EL BANCO a debitar el valor" +
                " de dichos cargos de cualquiera de sus cuentas, valores o inversiones.\n\n");

        contrato.append("DÉCIMA TERCERA: INTERESES.- Los valores depositados en la cuenta básica devengarán los intereses que públicamente " +
                "ofrezca EL BANCO, mismos que se pagarán o capitalizarán en los períodos determinados.\n\n");

        contrato.append("DÉCIMA CUARTA: ORIGEN LÍCITO DE FONDOS.- EL CLIENTE declara que los fondos objeto de las transacciones que se " +
                "realicen en la cuenta son lícitos, no provienen de/ni serán destinados a ninguna actividad ilegal o delictiva; ni consentirá" +
                " que se efectúen depósitos o transferencias a su cuenta, provenientes de estas actividades. Expresamente autoriza a EL BANCO" +
                " a realizar las verificaciones y debida diligencia correspondientes e informar de manera inmediata y documentada a la autoridad" +
                " competente en casos de investigación o cuando se detectaren transacciones inusuales e injustificadas, por lo que no ejercerá" +
                " ningún reclamo o acción judicial.\n\n");

        contrato.append("DÉCIMA QUINTA: AUTORIZACIÓN.- EL CLIENTE autoriza expresa e indefinidamente a EL BANCO para que obtenga de cualquier" +
                " fuente de información, incluido Burós de Información Crediticia autorizados para operar en el país, Registro de Datos Crediticios," +
                " su(s) referencia(s) personal(es) y/o patrimonial(es) anterior(es), actual(es) y/o posterior(es) a la suscripción de esta " +
                "autorización, sea como deudor principal, codeudor o garante, sobre su comportamiento crediticio, manejo de su(s) cuenta(s) " +
                "corriente(s), de ahorro, tarjetas de crédito, etc., y en general al cumplimiento de su(s) obligación(es) y demás activos, pasivos," +
                " datos personales y/o patrimoniales, aplicables para uno o más de los servicios y productos que brindan las Instituciones del " +
                "Sistema Financiero, según corresponda." + " EL CLIENTE faculta expresamente a EL BANCO para transferir o entregar dicha información," +
                " referente a operaciones crediticias, contingentes y/o cualquier otro compromiso crediticio que mantenga(n), sea como deudor" +
                " principal, codeudor o garante, con EL BANCO a cualquier Registro Crediticio, Burós de Información Crediticia autorizados" +
                " para el efecto, a autoridades competentes y organismos de control, así como a otras instituciones o personas jurídicas" +
                " legalmente facultadas." + " En caso de cesión, transferencia, titularización o cualquier otra forma de transferencia de " +
                "operaciones crediticias, contingentes y/o cualquier otro compromiso crediticio que mantenga EL CLIENTE, sea como deudor principal," +
                " codeudor o garante, con EL BANCO, la persona natural o jurídica cesionaria o adquirente de dicha obligación queda desde ya" +
                " expresamente facultada para realizar las mismas actividades establecidas en los dos párrafos precedentes." +
                " Adicionalmente, en el caso que EL CLIENTE tenga la obligación de declarar, pagar o remitir información respecto " +
                "al cumplimiento de sus obligaciones tributarias ante jurisdicciones fiscales nacionales o extranjeras, autoriza " +
                "expresa e indefinidamente a EL BANCO para que le reporte a la(s) administración(es) tributaria(s) competente(s), " +
                "la información de su(s) cuenta(s), inversión(es) y sus respectivos movimientos y transacciones, conforme esta sea requerida.\n\n");

        contrato.append("DÉCIMA SEXTA: MONTO MÁXIMO Y FRECUENCIA.- El saldo máximo mensual que podrá mantener EL CLIENTE en su cuenta " +
                "básica, será de dos salarios básicos o el establecido en la normativa vigente. Por motivo de seguridad, EL BANCO determinará" +
                " el número, monto y frecuencia de las transacciones diarias, lo cual es conocido y aceptado por EL CLIENTE, los mismos" +
                " que podrán ser modificados en cualquier momento, previa notificación al CLIENTE a través de los medios electrónicos.\n\n");

        contrato.append("DÉCIMA SÉPTIMA: CANALES TRANSACCIONALES.- Los canales que EL CLIENTE puede utilizar para realizar las transacciones" +
                " relacionadas con su cuenta a través de la tarjeta de débito son, entre otros que establezca EL BANCO: Cajeros Automáticos" +
                " habilitados; Corresponsales No Bancarios; servicio de Call Center; establecimientos comerciales que acepten la tarjeta de" +
                " débito, Pichincha Celular, Banca Móvil, Banca Electrónica y otros canales electrónicos que eventualmente llegare a determinar" +
                " y habilitar EL BANCO. Todos estos canales EL CLIENTE expresamente declara conocer y aceptar, así como los cupos diarios y" +
                " número máximo de transacciones.\n\n");

        contrato.append("JURISIDICCIÓN Y COMPETENCIA.- Para el caso de controversia, EL CLIENTE y EL BANCO, hacen una renuncia general de" +
                " domicilio y aceptan someterse a los jueces o tribunales del lugar donde se les encuentre o a los de la ciudad donde se " +
                "suscribe este documento o a los de la ciudad de Quito a elección de EL BANCO y al procedimiento que por ley corresponda.\n\n");

        contrato.append("DÉCIMA NOVENA: CAPACIDAD LEGAL.- EL CLIENTE declara que acepta las responsabilidades de orden civil y penal " +
                "correspondientes a las declaraciones que ha expresado; y, que tiene capacidad legal para otorgar este instrumento y " +
                "realizar todas las operaciones relacionadas con el manejo de su cuenta, por lo que exime de responsabilidad a EL BANCO " +
                "en caso de que estas declaraciones sean falsas o no se ajusten totalmente a la verdad.\n\n");

        contrato.append("La recepción de este instrumento por EL BANCO y el hecho que brinde el(los) servicio(s) solicitado(s) constituye " +
                "su manifestación de aceptación y un acuerdo de voluntades con EL CLIENTE, siendo este instrumento ley para las partes y " +
                "respaldo suficiente para el acceso y prestación del(los) servicio(s). Acepto y conozco todas y cada una de las cláusulas " +
                "de este instrumento, y que fueron solventadas las dudas sobre el producto, para constancia de lo cual suscribo en:\n\n");

        contrato.append("Ciudad y fecha:\n" + ciudadResidencia + "\n" + PAYUtils.getLocalFecha() + " (dd/ mm / aaaa)" + "\n\n" +
                "Nombre (titular):\n" + nombreApellido + "\n\n" + "C.C./C.I./P.P.N°:\n" + sNumCedula + "\n\n" + "Información " +
                "apertura de Cuenta básica en Corresponsal No Bancario - Mi Vecino" + "\n\n" + "RUC CNB:\n" + rucCnb + "\n\n" +
                "Nombre establecimiento:\n" + establecimiento + "\n\n" + "Proceso activación: " + "POS" + "\n\n" +
                "Información apertura de Cuenta básica por asesor Banco Pichincha" + "\n\n" + "Proceso activación: " + "POS" + "\n");

        return  contrato.toString();
    }


    private void obtenerlaEdad() {
       /* DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        java.time.LocalDate fechaNac = LocalDate.parse("15/08/1993", fmt);
        LocalDate ahora = LocalDate.now();

        Period periodo = Period.between(fechaNac, ahora);
        System.out.printf("Tu edad es: %s años, %s meses y %s días",
                periodo.getYears(), periodo.getMonths(), periodo.getDays());*/
    }


    private void setInfoMsg(String aMsgId, String aProcCode, String aEntryMode){
        setFixedDatas();
        EntryMode = aEntryMode;

        TermID = TMConfig.getInstance().getTermID();
        MerchID = TMConfig.getInstance().getMerchID();

        MsgID = aMsgId;
        ProcCode = aProcCode;
        RspCode = null;
    }

    private boolean otpXperta(String aProcCode1, String aMsgType2, String aProcCode2, String[] aListaCapturas) {
        String otp = "";
        boolean isOk = true;
        boolean rtn = false;
        int retPrep;
        String[] listaValidacion;
        String[] listaGeneracion;

        listaGeneracion = new String[4];
        listaGeneracion[0] = aListaCapturas[0];  // Cedula
        listaGeneracion[1] = aListaCapturas[1];  // celular
        listaGeneracion[2] = aListaCapturas[2];  // Nombre Apellido

        if(aProcCode1.equals(proCodeOTPRepo)){
            listaGeneracion[3] = aListaCapturas[4];  // Número de kit

            listaValidacion = new String[7];
            listaValidacion[0] = aListaCapturas[0];  // Cedula
            listaValidacion[1] = aListaCapturas[1];  // celular
            listaValidacion[2] = aListaCapturas[3];  // mail
            listaValidacion[3] = aListaCapturas[4];  // numero de Kit
            listaValidacion[4] = aListaCapturas[2];  // Nombre apellido
            listaValidacion[5] = aListaCapturas[5];  // Nombre apellido
            listaValidacion[6] = aListaCapturas[6];  // Nombre apellido
        }else{
            listaGeneracion[3] = aListaCapturas[11];  // Número de kit

            listaValidacion = new String[14];
            listaValidacion[0] = aListaCapturas[0]; // Cedula
            listaValidacion[1] = aListaCapturas[1]; // celular
            listaValidacion[2] = aListaCapturas[3]; // mail
            listaValidacion[3] = aListaCapturas[4]; // Calle principal
            listaValidacion[4] = aListaCapturas[5]; // Lote
            listaValidacion[5] = aListaCapturas[6]; // Calle Secundaria
            listaValidacion[6] = aListaCapturas[7]; // Sector Economico
            listaValidacion[7] = aListaCapturas[8]; // Actividad Economica
            listaValidacion[8] = aListaCapturas[9]; // ingresos
            listaValidacion[9] = aListaCapturas[10]; // Situacion laboral
            listaValidacion[10] = aListaCapturas[11]; // numero de Kit
            listaValidacion[11] = aListaCapturas[2]; // Nombre apellido
            listaValidacion[12] = aListaCapturas[12]; // Código dactilar
            listaValidacion[13] = aListaCapturas[13]; // Fecha expedición
        }

        isOk = notificarOtp(aListaCapturas[1]);
        if (isOk) {
            otp =  vistaGenerarOtp(aProcCode1, listaGeneracion);
            if(!otp.equals("")){
                while(contadorOtp < 3){
                    if (!otp.equals("")) {
                        InitTrans.tkn28.setNumeroOtp(ISOUtil.str2bcd(ISOUtil.padright(otp, 12, 'F'), false));
                        setFixedDatas();
                        para.setNeedPrint(true);
                        isReversal = true;

                        // Validar otp - EFECTIVACION
                        Field07 = null;
                        inputMode = 0;
                        RspCode = null;
                        AuthCode = null;
                        Field63 = packField63();
                        valdiarTkn01();

                        InitTrans.tkn40.setListaItems(listaValidacion);
                        Field48 = InitTrans.tkn28.packTkn28() + InitTrans.tkn40.packToken() + InitTrans.tkn93.packTkn93();

                        setInfoMsg(aMsgType2, aProcCode2, "0021");
                        retPrep = newPrepareOnline(inputMode);
                        if(retPrep == 0){
                            rtn = true;
                            break;
                        }else if(retPrep == 77){
                            contadorOtp++;
                            if (contadorOtp == 3) {
                                break;
                            } else {
                                transUI.handlingOTP(false,InitTrans.tkn07.obtenerMensaje());
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                otp = vistaGenerarOtp(aProcCode1, listaGeneracion);
                            }
                        }else{
                            transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return rtn;
    }

    /*
    Si la posicion 3 (0,1,2,3) de mensajes, se activa vista de reposicion de kit "modificar datos"
     */
    private String info_kit(String[] datos) {
        String ret = "";
        String[] mensajes = {"Número de kit","N° celular", "Correo electrónico", "Confimación correo electrónico"};
        InputInfo inputInfo = transUI.showInfoKit(timeout,"Información kit", mensajes, datos);
        if (inputInfo.isResultFlag()) {
            ret = inputInfo.getResult();
            if (ret.equals(CANCELAR)) {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            } else {
                return ret;
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            return ret;
        }
        return ret;
    }



    private String info_kit_reposicion(String snumCelular, String aMail) {
        String ret = "";
        String[] mensajes = {"Número de kit","N° celular", "Correo electrónico", ""};
        if (!aMail.isEmpty() && !snumCelular.isEmpty()){
            email = aMail.substring(0,1)+"*******"+aMail.substring(aMail.indexOf("@"), aMail.length());       // mail
            celular = "*******"+snumCelular.substring(snumCelular.length() - 3,snumCelular.length());
        }
        String[] contenido = {celular, email};

        InputInfo inputInfo = transUI.showReposicionInfoKit(timeout,"Información kit", mensajes, contenido);
        if (inputInfo.isResultFlag()) {
            ret = inputInfo.getResult();
            if (ret.equals(CANCELAR)) {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            } else {
                return ret;
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            return ret;
        }
        return ret;
    }

    private String datos_complementarios() {
        String ret = "";
        String[] mensajes = {"Sector económico","Actividad económica","Ingresos mensuales", "Situación laboral"};
        InputInfo inputInfo = transUI.showDatosComplementarios(timeout,"Datos complementarios", mensajes);
        if (inputInfo.isResultFlag()) {
            ret = inputInfo.getResult();
            if (ret.equals(CANCELAR)) {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            } else {
                return ret;
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            return ret;
        }
        return ret;
    }

    private String datos_direccion() {
        String ret = "";
        String[] mensajes = {"Calle Principal:", "Lote:", "Calle secundario"};
        InputInfo inputInfo = transUI.showDatosDireccion(timeout, "Datos dirección" ,mensajes);
        if (inputInfo.isResultFlag()) {
            ret = inputInfo.getResult();
            if (ret.equals(CANCELAR)) {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            } else {
                InitTrans.tkn40.setListaItems(inputInfo.getResult().split("~"));
                return ret;
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            return ret;
        }
        return ret;
    }


    private boolean procesarFaceID(String aProcCode, String aCedula) {
        boolean rtn = false;
        transUI.newHandling(context.getResources().getString(R.string.procesando),timeout , Tcode.Mensajes.transaccionEnProceso);
        InputInfo inputInfo = transUI.showVerficacionFaceId(timeout);
//        boolean verificar=inputInfo.isResultFlag();
//        System.out.println("------------- "+inputInfo.getResult());
//        System.out.println("Retorno de verificacion face id  " +verificar);

        if (inputInfo != null){
            if(inputInfo.getResult().equals("ApliacionNoInstalada")){
                transUI.showMsgError(timeout, "La aplicación FacePhi no está instalada, por favor contáctese con soporte ");
                return false;
            }
            if(inputInfo.getResult().equals("LecturaFallida")){
                transUI.showMsgError(timeout, "No se pudo leer la informacion de FacePhi, por favor contáctese con soporte");
                return false;
            }
            if(inputInfo.getResult().equals("ArchivoNoExiste")){
                System.out.println("ArchivoNoExiste CuentaXperta");
                transUI.showMsgError(timeout, "Por favor acercarse a una agencia de Banco Pichincha");
                return false;
            }
            if(inputInfo.getResult().equals("ProcesoFallido")){
                transUI.showMsgError(timeout, "Por favor acercarse a una agencia de Banco Pichincha");
                return false;
            }
            System.out.println("INFORMACION CUENTA XPERTA *******  "+inputInfo.getResult());
            if(enviarTransaccionTemplate(aProcCode, aCedula, inputInfo.getResult()) == 0){
                rtn = true;
            } else {
                return rtn;
            }
        }else{
            System.out.println("inputInfo vacio");
            transUI.showMsgError(timeout, "La aplicación Face ID no Funciona, por favor contáctese con soporte " );
            return false;
        }

        return rtn;

        /*String ret = null;
        String[] faceId = {"FaceID"};
        transUI.newHandling(context.getResources().getString(R.string.procesando),timeout , Tcode.Mensajes.transaccionEnProceso);
        //InputInfo inputInfo = transUI.showVerficacionFaceId(timeout,faceId);
        String cedula = "1716304140";
        String template = "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";

        enviarTransaccionTemplate(cedula, template);
        return true;*/
    }

    private int pos = 0;
    private int numIteracion = 0;
    private static final int lenMax = 900;

    private int enviarTransaccionTemplate(String aProcCode, String cedula, String data)
    {
        boolean isOk;
        int dataLength = data.length();
        String[] listaItemTkn40 = new String[3];

        String estadoTnk93 = null;
        byte[] lenMensaje93 = null;

        int ret = -1;

        listaItemTkn40[0] = cedula;
        listaItemTkn40[1] = String.valueOf(numIteracion);
        //InitTrans.tkn40.setCantidadItems(3);
        InitTrans.tkn93.cleanTkn93();

        if(dataLength <= lenMax)
        {
            estadoTnk93 = "00";
            listaItemTkn40[2] = data;
        }else {
            if(pos == 0){
                estadoTnk93 = "00";
                listaItemTkn40[2] = data.substring(pos, pos + lenMax);
            }else if(dataLength > (pos + lenMax))
            {
                estadoTnk93 = "01";
                listaItemTkn40[2] = data.substring(pos, pos + lenMax);
            }else{
                estadoTnk93 = "02";
                listaItemTkn40[2] = data.substring(pos);
            }
        }

        InitTrans.tkn40.setListaItems(listaItemTkn40);
        lenMensaje93 = ISOUtil.str2bcd(estadoTnk93, false);
        InitTrans.tkn93.setEstadoMsg(lenMensaje93);
        setFieldseTransTemplate("0100", aProcCode, "0021");
        transUI.newHandling(context.getResources().getString(R.string.procesando),timeout , Tcode.Mensajes.transaccionEnProceso);
        ret = newPrepareOnline(inputMode);

        if (ret == 0 && !estadoTnk93.equals("02")){
            pos += listaItemTkn40[2].length();
            numIteracion ++;
            ret = enviarTransaccionTemplate(aProcCode, cedula, data);
        } else if (ret == 3 && count < 3) {
            count ++;
            transUI.handlingOTP(false,InitTrans.tkn07.obtenerMensaje());
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isOk = procesarFaceID(proCodeValidacion, cedula);

            if(isOk){
                ret = 0;
            }else {
                ret = -1;
            }
        } else {
            cfg.incTraceNo().save();
        }

        return ret;
    }

    private void setFieldseTransTemplate(String aMsgId, String aProcCode, String aEntryMode){
        setFixedDatas();
        Field48 = InitTrans.tkn40.packToken();
        Field48 += InitTrans.tkn93.packTkn93();
        EntryMode = aEntryMode;

        TermID = TMConfig.getInstance().getTermID();
        MerchID = TMConfig.getInstance().getMerchID();

        MsgID = aMsgId;
        ProcCode = aProcCode;
        RspCode = null;
    }

    private void reposicionKit(String titulo) {
        String[] mensajes = new String[]{titulo, "¿Tiene residencia fiscal diferente a Ecuador?", "Sí", "No"};
        String auxRet, retKit;

        boolean isOk;
        int retPrep;
        setFixedDatas();
        MsgID = "0100";
        ProcCode = "370000";
        RspCode = null;
        EntryMode = "0021";
        Amount = -1;
        Field63 = packField63();
        valdiarTkn01();
        TermID = TMConfig.getInstance().getTermID();
        MerchID = TMConfig.getInstance().getMerchID();

        try {
            String cedulaScan = ingresoCedulaScan("Reposición de kit");
            String[] listaDatosCed = cedulaScan.split("@");
            if (!cedulaScan.equals("")) {
                auxRet = residenciaFiscal(mensajes);
                if (!auxRet.equals("")) {
                    InitTrans.tkn40.setListaItems(listaDatosCed); // cedula, codigo dactilar, fecha expedicion
                    Field48 = InitTrans.tkn40.packToken();
                    retPrep = newPrepareOnline(inputMode);
                    if (retPrep == 0) {
                        isOk = true;
                        listaContrato = InitTrans.tkn40.getListaItems();
                        //nombre, fecha nacimiento, estado civil, genero, celular, mail, cupo cajero, tnp, cupo CNB, provincia, ciudad, referencia Ub, RUC CNB, establecimiento CNB, valida FACE ID
                        if(listaContrato.length==15){
                            if (listaContrato[14].equals("S")){
                                isOk = procesarFaceID(proCodeValidacionFaceId, listaDatosCed[0]);
                                count = 1;
                                if (isOk){
                                    isOk = sucessFaceID();
                                } else {
                                    transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                                }
                            }
                        }else{
                            isOk = false;
                            transUI.showMsgError(timeout, "Error en obtención de informacion Creacion Xperta");
                        }

                        if (isOk) {
                           /* retKit = info_kit_reposicion(listaContrato[4], listaContrato[5]);
                           if (!retKit.equals("") && retKit.equals("Modificar")) {
                                retKit = info_kit();
                            }*/

                           String[] info = new String[2];
                           info[0] = listaContrato[4];
                           info[1] = listaContrato[5];

                           if (verificacionInfoKit(listaDatosCed, listaContrato,info,null,null,null,true)){
                               isOk = otpXperta(proCodeOTPRepo, "0200", "370100", listaCapturas);
                               if (isOk) {
                                   if (confirmacion("370100", titulo)) {
                                       //code: posición de string a mostrar en pantalla, lee un archivo en res/values/arrays arrays.xml-> mensajes_handling
                                       transUI.showSuccess(timeout, 15, "");
                                   } else {
                                       transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                                   }
                                        /*String correoConfirmacion = listaCapturas[2];
                                        String msgFinal = "\nLa solicitud de reposición de Kit esta disponible en el portal de Banco Pichincha o puedes acercarte a cualquier agencia del Banco de Pichincha a solicitarlo";
                                        if (mensanjeConfiramcion("Tu solicitud de reposición", correoConfirmacion, msgFinal)) {
                                        }*/
                               } else {
                                   transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                               }
                           }
                        }
                    }
                }
            }
        } catch (Exception e) {
            InitTrans.wrlg.wrDataTxt("Excepción activada - CuentaXperta.java" + e);
        }
    }

    private void cambioClaveBasica() {
        para.setNeedPass(true);
        para.setChangePin(true);
        if (leerTarjeta(TARJETA_CAMBIO_CLAVE)) {
            if (mensajeInicial("340000")) {
                transUI.trannSuccess(timeout, Tcode.Status.operacionRealizadaConExito);
            } else {
                transUI.showError(timeout, Tcode.T_wait_timeout);
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
    }

    private String ingresoCedulaScan(String titulo) {
        String ret;
        String[] mensajes = {"Ingresa número de cédula", "Ingresa código dactilar", "Ingresa fecha de expedición"};
        int[] longPredios = {10, 10, 8};
        InputInfo inputInfo = transUI.showBonoDesarrollo(timeout, titulo, mensajes, longPredios, InputType.TYPE_CLASS_NUMBER, 0);
        if (inputInfo.isResultFlag()) {
            ret = inputInfo.getResult();
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            ret = "";
        }
        return ret;
    }

    protected boolean confirmacion(String code, String title) {
        boolean ret = false;
        setFixedDatas();
        clearDataFields();
        RRN = iso8583.getfield(37);
        if (RRN == null) {
            RRN = "000000000000";
        }
        iso8583.clearData();
        RspCode = null;
        transUI.newHandling(title, timeout, Tcode.Mensajes.conectandoConBanco);
        MsgID = "0202";
        TermID = TMConfig.getInstance().getTermID();
        MerchID = TMConfig.getInstance().getMerchID();
        ProcCode = code;
        para.setNeedPrint(false);
        setFields();
        retVal = 0;
        retVal = enviarConfirmacion();
        if (retVal != 0) {
            transUI.showError(timeout, retVal);
        } else {
            ret = true;
        }
        return ret;
    }

    private boolean mensajeInicial(String code) {
        boolean ret = false;
        int rta;
        setFixedDatas();
        MsgID = "0100";
        ProcCode = code;
        RspCode = null;

        switch (code) {
            case "340000":
                if (emv != null)  {
                    SecurityInfo = emv.getNewPinBlock();
                }
                Field48 = InitTrans.tkn43.packTkn43(inputMode, Track2);

                if (inputMode == 2){
                    EntryMode = "0021";
                }else {
                    EntryMode = "0051";
                }

                armar59();
                para.setNeedPrint(true);
                break;

            case proCodeValidacion:
                Field48 = InitTrans.tkn47.packTkn47();
                Field48 += InitTrans.tkn48.packTkn48();
                EntryMode = "0021";
                break;

            case "360030":
                Field48 = InitTrans.tkn19.packTkn19();
                EntryMode = "0021";
                break;

            case proCodeCreacion:
            default:
                Field48 = InitTrans.tkn12.packTkn12();
                EntryMode = "0021";
                break;
        }

        Amount = -1;
        Field63 = packField63();
        if (code.equals(proCodeValidacion) || code.equals(proCodeCreacion)) {
            valdiarTkn01();
        }
        rta = enviarTransaccion();
        if (rta == 0 || rta == 95 || rta == 99 || rta == 77) {
            ret = true;
        }
        return ret;
    }

    public int enviarTransaccion() {
        int retPrep;
        TermID = TMConfig.getInstance().getTermID();
        MerchID = TMConfig.getInstance().getMerchID();
        retPrep = newPrepareOnline(inputMode);
        if (retPrep == 1995) {
            transUI.showMsgError(timeout, "No se obtuvo información de impresión");
        }
        return retPrep;
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    protected int seleccionarMenus(String[] items, String Msg) {
        int ret = -1;
        InputInfo inputInfo = transUI.showBotones(items.length, items, Msg);
        if (inputInfo.isResultFlag()) {
            ret = inputInfo.getErrno();
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    protected String ingresoContrapartida(String titulo, String mensaje, int maxLeng, int inputType, int carcRequeridos) {
        String ret;
        InputInfo inputInfo = transUI.showContrapartida(timeout, mensaje, maxLeng, inputType, carcRequeridos, titulo);
        if (inputInfo.isResultFlag()) {
            ret = inputInfo.getResult();
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            ret = "";
        }
        return ret;
    }

    protected String residenciaFiscal(String[] mensaje) {
        String ret;
        InputInfo inputInfo = transUI.showMsgConfirmacionCheck(timeout, mensaje);
        if (inputInfo.isResultFlag()) {
            ret = inputInfo.getResult();
            if (ret.equals("Sí")) {
                transUI.showMsgError(timeout, "Por favor acérquese a una agencia");
                ret = "";
            } else if (ret.equals(CANCELAR)) {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            ret = "";
        }
        return ret;
    }

    private boolean notificarOtp(String aNumCelular) {
        String mensaje2 = "Se enviará un SMS con \n una clave temporal al \n celular indicado " +
                "\n anteriormente \n" + "XXXXXX" + aNumCelular.substring(6, 10);

                //ISOUtil.bcd2str(InitTrans.tkn48.getNumero_cel(), 0, InitTrans.tkn48.getNumero_cel().length).substring(6, 10);
        return (ventanaConfirmacion("Notificación", mensaje2));

    }

    private boolean ventanaConfirmacion(String titulo, String mensaje) {
        String[] vistaMensaje = new String[6];

        vistaMensaje[0] = mensaje;
        vistaMensaje[1] = "";
        vistaMensaje[2] = "";
        vistaMensaje[3] = "";
        vistaMensaje[4] = "";
        vistaMensaje[5] = titulo;

        return (ventanaConfirmacion(vistaMensaje));
    }

    protected boolean ventanaConfirmacion(String[] mensajes) {
        boolean ret = false;

        InputInfo inputInfo = transUI.showMsgConfirmacion(timeout, mensajes, false);
        if (inputInfo.isResultFlag()) {
            if (inputInfo.getResult().equals("aceptar")) {
                ret = true;
            } else {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }

        return ret;
    }

    protected void valdiarTkn01() {
        String pack63 = packField63();
        Field63 = "010017" + pack63.substring(6);
        Field63 += InitTrans.tkn01.packTkn01();
    }

    protected int enviarOTP(String aProcCode, String[] aListTkn40) {
        int rsp;
        Amount = -1;
        Field63 = packField63();
        valdiarTkn01();
        //Enviar generacion de OTP
        InitTrans.tkn40.setListaItems(aListTkn40);

        setInfoMsg("0100", aProcCode, "0021");
        Field48 = InitTrans.tkn13.packTkn13();
        Field48 += InitTrans.tkn40.packToken();
        para.setNeedPrint(false);
        isReversal = false;
        Field07 = null;
        RspCode = null;
        AuthCode = null;
        rsp = newPrepareOnline(inputMode);
        return rsp;
    }

    protected String ingresoOTP() {
        String ret;
        String[] mensaje = {"Ingresa en este dispositivo la clave\n" +
                "temporal enviada al celular",
                "Si no le llega el código intente\n" +
                        "generar uno nuevo:"};
        InputInfo inputInfo = transUI.showIngresoOTP(timeout, "Validación código de seguridad", mensaje);
        if (inputInfo.isResultFlag()) {
            ret = inputInfo.getResult();
            switch (ret) {
                case "generar":
                    return ret;
                case CANCELAR:
                    transUI.showError(timeout, Tcode.T_user_cancel_operation);
                    ret = "";
                    break;
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            ret = "";
        }
        return  ret;
    }

    protected boolean mensanjeConfiramcion(String aHeader, String aMail, String aMsgFinal) {
        boolean rsp = false;
        String ret = "";

        String parte1 = aMail.substring(0,aMail.lastIndexOf('@'));
        String parte2 = aMail.substring(aMail.lastIndexOf('@'));
        String mascara = parte1.substring(0,parte1.length()/2) + "xxxxx";
        String correo = mascara + parte2;

        String[] mensajes = {aHeader + " se enviará a la siguiente dirección: " +
                correo, aMsgFinal};
        InputInfo inputInfo = transUI.showMensajeConfirmacion(timeout,"Creación cuenta básica",mensajes);
        if (inputInfo.isResultFlag()) {
            ret = inputInfo.getResult();
            if (ret.equals("")) {
                transUI.showError(timeout, Tcode.T_user_cancel_operation);
                return rsp;
            } else {
                return true;
            }
        } else {
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
            return rsp;
        }
    }

    protected String vistaGenerarOtp(String aProcCode1, String[] aListTkn40) {
        String otp = "";
        int retPrep = 0;

        /*if (!enviarConsulta) {
            retPrep = enviarOTP(aProcCode1, aListTkn40);
        }*/
        //enviarConsulta = false;
        if (retPrep == 0) {
            otp = ingresoOTP();

            while (otp.equals("generar")) {
                contadorOtp = 0;
                retPrep = enviarOTP(aProcCode1, aListTkn40);
                if (retPrep == 0) {
                    otp = ingresoOTP();
                }else{
                    break;// Codigos de error por Netmanager
                }
            }
        }
        return otp;
    }

    private static String obtenerEdad(String fechaNacimiento) {

        if (fechaNacimiento != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            StringBuilder result = new StringBuilder();
            Calendar c = new GregorianCalendar();
            Date date;

            try {
                date = sdf.parse(fechaNacimiento);
                c.setTime(date);
                result.append(calcularEdad(c));
//                result.append(" años");
//                    result.append(sdf.format(date));
//                    result.append(" ");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result.toString();
        }
        return "";
    }

    private static int calcularEdad(Calendar fechaNac) {
        Calendar today = Calendar.getInstance();
        int diffYear = today.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
        int diffMonth = today.get(Calendar.MONTH) - fechaNac.get(Calendar.MONTH);
        int diffDay = today.get(Calendar.DAY_OF_MONTH) - fechaNac.get(Calendar.DAY_OF_MONTH);

        // Si está en ese año pero todavía no los ha cumplido
        System.out.println(diffYear);
        if (diffMonth < 0 || (diffMonth == 0 && diffDay < 0)) {
            diffYear = diffYear - 1;
        }
        return diffYear;
    }

}
