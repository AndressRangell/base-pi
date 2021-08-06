package newpos.libpay.trans.pichincha.Recaudaciones.BonoDesarrolloHumano;

import android.content.Context;
import android.text.InputType;

import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.pichincha.Recaudaciones.Recaudaciones;
import newpos.libpay.utils.ISOUtil;

import static newpos.libpay.utils.ISOUtil.formatMiles;

public class PagoBonoDesarrolloHumano extends Recaudaciones {

    public PagoBonoDesarrolloHumano(Context ctx, String transEname, String tipoTrans, TransInputPara p) {
        super(ctx, transEname, tipoTrans, p);
    }



    public void bono(){
        int select = 0;
        String contrapartida = "";
        String datosNFC = "";
        String[] datos;
        String[] mensajesPredios = {"Ingresa número de cédula", "Ingresa código dactilar", "Ingresa fecha de expedición"};
        int[] longPredios = {10, 10, 8};
        contrapartida = ingresoBonoDesarrollo("Pago de bono", mensajesPredios, longPredios, InputType.TYPE_CLASS_NUMBER, 0);
        datos = contrapartida.split("@");
        if(!contrapartida.equals("")){
            datosNFC = leerCedualNfc();
            if(!datosNFC.equals("")){
                if (mensajeInicialBono("330000", datos, datosNFC)) {
                    if (confirmarPago()) {
                        String montoTotal = ISOUtil.bcd2str(InitTrans.tkn14.getValor(), 0, InitTrans.tkn14.getValor().length);
                        Amount = Long.parseLong(montoTotal);
                        Comision = 0;
                        TotalAmount = Amount + Comision;
                        para.setAmount(Amount);
                        isReversal = true;
                        if (mensajeTransaccion("330100", datos, datosNFC)) {
                            //para.setNeedPrint(true);
                            if (confirmacionRecaudo("330100", "Bono desarrollo humano")) {
                                transUI.showSuccess(timeout, Tcode.Mensajes.recaudacionExitosa, "");
                            } else {
                                transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                            }
                        } else {
                            transUI.showMsgError(timeout, InitTrans.tkn07.obtenerMensaje());
                        }
                    }
                }
            } else {
                transUI.showMsgError(timeout, "Modo de prueba finalizado");
            }
        }
    }

    /**
     *
     * @param code
     * @param datos
     * @return
     */
    private boolean mensajeInicialBono(String code, String[] datos, String datosNFC) {
        boolean ret;
        setFixedDatas();
        MsgID = "0100";
        ProcCode = code;
        EntryMode = "0021";
        Field48 = InitTrans.tkn17.packTkn17_bono(datos, datosNFC);
        Field63 = packField63();
        Amount = -1;
        ret = enviarTransaccion();
        return ret;
    }

    /**
     *
     * @return
     */
    private boolean confirmarPago(){

        String[] valores = new String[6];
        String[] vistaMensaje = new String[7];
        String[] mensajes = {"Nombre : ", "Valor : ", "Tipo subsidio : ", "Periodo : ", "Periodo fin : "};
        String valor = ISOUtil.bcd2str(InitTrans.tkn14.getValor(), 0, InitTrans.tkn14.getValor().length);
        String finalValor = formatMiles(Integer.parseInt(valor));
        valores[0] = String.valueOf(InitTrans.tkn14.getNombre());
        valores[1] = finalValor;
        valores[2] = String.valueOf(InitTrans.tkn14.getTipo_sub());
        valores[3] = String.valueOf(InitTrans.tkn14.getPeriodo());
        valores[4] = String.valueOf(InitTrans.tkn14.getPeriodoFin());
        vistaMensaje[0] = mensajes[0] + valores[0];
        vistaMensaje[1] = mensajes[1] + valores[1];
        vistaMensaje[2] = mensajes[2] + valores[2];
        vistaMensaje[3] = mensajes[3] + valores[3];
        vistaMensaje[4] = mensajes[4] + valores[4];
        vistaMensaje[5] = "Pago de bono";
        return(ventanaConfirmacion(vistaMensaje));
    }

    /**
     *
     * @param code
     * @param datos
     * @return
     */
    private boolean mensajeTransaccion(String code, String[] datos, String datosNFC) {
        boolean ret;
        setFixedDatas();
        iso8583.clearData();
        MsgID = "0200";
        ProcCode = code;
        RspCode = null;
        inputMode = 0;
        Field48 = InitTrans.tkn17.packTkn17_bono(datos, datosNFC);
        Field59 = null;
        Field63 = packField63();
        para.setNeedPrint(true);
        isSaveLog = true;
        ret = enviarTransaccion();
        return ret;
    }


}
