package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import cn.desert.newpos.payui.master.MasterControl;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.device.printer.PrintRes;

public class ToolsAppCompact extends AppCompatActivity {

    /**
     *
     * @param context
     * @return
     */
    public Typeface tipoFuente1(Context context){
        Typeface tipoFuente1 = Typeface.createFromAsset(context.getAssets(), "font/PreloSlab-Book.otf");
        return tipoFuente1;
    }

    /**
     *
     * @param context
     * @return
     */
    public static Typeface tipoFuente3(Context context){
        Typeface tipoFuente3 = Typeface.createFromAsset(context.getAssets(), "font/Prelo-Medium.otf");
        return tipoFuente3;
    }


    /**
     *
     */
    public void theToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     *
     * @param activity
     * @param idTrans
     * @param tipoTrans
     */
    protected void iniciarTransaccion(AppCompatActivity activity, int idTrans, String tipoTrans){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(activity, MasterControl.class);
        intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[idTrans]);
        intent.putExtra(MasterControl.tipoTrans, tipoTrans);
        activity.startActivity(intent);
    }

    /**
     *
     * @param activity
     * @param idTrans
     * @param
     */
    protected void iniciarTransaccion(AppCompatActivity activity, int idTrans){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(activity, MasterControl.class);
        intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[idTrans]);
        activity.startActivity(intent);
    }

    public void vistaImprimiendo(Context context){
        setContentView(R.layout.vista_imprimiendo);
        Typeface tipoFuente3 = Typeface.createFromAsset(context.getAssets(), "font/Prelo-Medium.otf");
        Typeface tipoFuente1 = Typeface.createFromAsset(context.getAssets(), "font/PreloSlab-Book.otf");
        TextView tv = findViewById(R.id.handing_msginfo);
        tv.setText("Imprimiendo");
        tv.setTypeface(tipoFuente3);
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1);
    }

    public void apagarWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.disconnect();
        wifiManager.setWifiEnabled(false);
    }

    private void setMobileDataEnabled(Context context, boolean enabled) {

    }





}
