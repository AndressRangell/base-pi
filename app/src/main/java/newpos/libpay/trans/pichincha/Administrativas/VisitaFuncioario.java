package newpos.libpay.trans.pichincha.Administrativas;

import android.content.Context;
import com.android.desert.keyboard.InputInfo;

import newpos.libpay.device.printer.PrintManager;
import newpos.libpay.helper.iso8583.ISO8583;
import newpos.libpay.presenter.TransPresenter;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.TransInputPara;
import newpos.libpay.trans.finace.FinanceTrans;
import newpos.libpay.trans.pichincha.Tools.TransTools;
import newpos.libpay.utils.PAYUtils;

public class VisitaFuncioario extends FinanceTrans implements TransPresenter {

    private String rspCode;

    public VisitaFuncioario(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p ;
        setTraceNoInc(true);
        TransEName = transEname ;
        if(para != null){
            transUI = para.getTransUI();
        }
        isProcPreTrans = true;
    }

    @Override
    public void start() {
        InitTrans.wrlg.wrDataTxt("Inicio transaccion "+ TransEName);
        if (leerTarjeta(TARJETA_FUNCIONARIO)){
            ExpDate = null;
            Pan = null;
            PanSeqNo = null;
            Track2 = null;
            Amount=-1;
            AcquirerID = TransTools.ACQUIRER_ID;
            CurrencyCode = TransTools.CURRENCY_CODE;
            LocalTime = PAYUtils.getLocalTime();
            LocalDate = PAYUtils.getLocalDate();
            armar59();
            Field48 = InitTrans.tkn43.packTkn43(inputMode, Track2);
            packField63();

            if (prepareOnline()) {
                rspCode = iso8583.getfield(39);
                if(rspCode.equals(ISO8583.RSPCODE.RSP_00)){
                    if(vistaInformacionVisita()){
                        transUI.showSuccess(timeout , Tcode.Mensajes.operacionRealizadaConExito, "");
                    }else{
                        transUI.showError(timeout , retVal);
                    }
                }else {
                    transUI.showError(timeout , retVal);
                }
            }else{
                transUI.showError(timeout , retVal);
            }
        }else {
            transUI.showMsgError(timeout, "Tarjeta no procesada");
        }

    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }



    private boolean vistaInformacionVisita(){
        boolean ret = false;
        InputInfo inputInfo = transUI.showInformacion(5*1000,"VISITAFUNCIONARIO_C");
        if(inputInfo.isResultFlag()){
            if (inputInfo.getResult().equals("si")) {

                imprimir();
                return true;
            }
            if (inputInfo.getResult().equals("no")) {
                transUI.showSuccess(timeout , Tcode.Mensajes.operacionRealizadaConExito, "");
            }else {

                System.out.print(inputInfo.getResult());
            }
            ret = true;
        }else{
            transUI.showError(timeout, Tcode.T_user_cancel_operation);
        }
        return ret;
    }

    private void imprimir() {
        transUI.imprimiendo(timeout , Tcode.Mensajes.imprimiendoRecibo);
        PrintManager printManager = PrintManager.getmInstance(context , transUI);
        if (iso8583.getfield(61) != null){
            String auxField61 = iso8583.getfield(61);
            getTkn(auxField61);
        }else if(iso8583.getfield(62) != null){
            String auxField62 = iso8583.getfield(62);
            getTkn(auxField62);
        }
        retVal = printManager.printPichincha(Tkn08, false);
    }

    private boolean prepareOnline() {
        boolean ret = false;
        int retPrep;
        transUI.newHandling("Conectando" ,timeout , Tcode.Mensajes.conectandoConBanco);
        retPrep=newPrepareOnline(inputMode);
        if(retPrep == 0 || retPrep == 97){
            ret = true;
        }
        clearPan();
        return ret;
    }

}
