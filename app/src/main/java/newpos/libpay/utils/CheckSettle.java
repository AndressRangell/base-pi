package newpos.libpay.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pos.device.config.DevConfig;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.desert.newpos.payui.master.MasterControl;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

import static cnb.pichincha.wposs.mivecino_pichincha.screens.StartActivity.checkBatch;

public class CheckSettle {

    private boolean runTrans;
    private boolean settleAutomatic;
    private boolean cierreCambioOperacion;

    public CheckSettle(boolean runTrans, boolean settleAutomatic) {
        this.runTrans = runTrans;
        this.settleAutomatic = settleAutomatic;
    }

    public boolean isSettleAutomatic() {
        return settleAutomatic;
    }

    public void setSettleAutomatic(boolean settleAutomatic) {
        this.settleAutomatic = settleAutomatic;
    }

    public boolean isCierreCambioOperacion() {
        return cierreCambioOperacion;
    }

    public void setCierreCambioOperacion(boolean cierreCambioOperacion) {
        this.cierreCambioOperacion = cierreCambioOperacion;
    }

    public void setRunTrans(boolean runTrans) {
        this.runTrans = runTrans;
    }

    /*@Override
    public void onReceive(Context context, Intent intent) {
        Log.i("onReceive", "CheckSettle");
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String hora = dateFormat.format(new Date());
        Date hora_actual = null;
        Date hora_cierre = null;
        try {
            hora_actual = dateFormat.parse(hora);
            hora_cierre = dateFormat.parse(getFrecuencia());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int rta = hora_actual.compareTo(hora_cierre);

        if (rta > 0 && !runTrans && checkBatch()) {
            if (InitTrans.initialization){
                Intent intent2 = new Intent();
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2.setClass(context, MasterControl.class);
                intent2.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[13]);
                context.startActivity(intent2);
                settleAutomatic = true;
            }
        } else {
            settleAutomatic = false;
        }
    }*/

    private String getFrecuencia() {
        String hora = "";
        String SN = DevConfig.getSN();
        String charSN = SN.substring(SN.length() - 1);
        switch (charSN) {
            case "1":
                hora = "21:00";
                break;
            case "2":
                hora = "21:02";
                break;
            case "3":
                hora = "21:04";
                break;
            case "4":
                hora = "21:06";
                break;
            case "5":
                hora = "21:08";
                break;
            case "6":
                hora = "21:10";
                break;
            case "7":
                hora = "21:12";
                break;
            case "8":
                hora = "21:14";
                break;
            case "9":
                hora = "21:16";
                break;
            case "0":
                hora = "21:18";
                break;
            default:
                break;
        }
        return hora;
    }

}
