package cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pos.device.SDKException;
import com.pos.device.emv.CAPublicKey;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;
import com.pos.device.emv.TerminalAidInfo;
import com.pos.device.printer.Printer;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.ResultControl;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenu10_Administrativo;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenu5_Recaudaciones;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenuPrincipal;
import newpos.libpay.device.printer.PrintManager;
import newpos.libpay.trans.manager.DparaTrans;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.SaveData;

public class ImpresionPantalla extends AppCompatActivity {

    TextView ms0;
    Button btnCancelar, btnAceptar;
    private int rsp;
    CountDownTimer countDownTimer;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impresion_pantalla);
        temporizador();

        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/monofonto.ttf");
        ms0 = findViewById(R.id.ms0);
        ms0.setTypeface(tipoFuente1);
        btnAceptar = findViewById(R.id.btn_aceptar);
        btnCancelar = findViewById(R.id.btn_cancelar);

        bundle = getIntent().getExtras();

        if(bundle != null && bundle.getString("secuencial").equals("secuencial"))
            ms0.setText(InitTrans.ultimoReciboSecuencial);
        else if(bundle != null && bundle.getString("secuencial").equals("conAuto"))
            ms0.setText(SaveData.getInstance().getReciboConsultaAuto());
        else
            ms0.setText(InitTrans.ultimoRecibo);
    }

    public void imprimir(View view) throws SDKException {
        do{
            if(bundle != null && bundle.getString("secuencial").equals("secuencial"))
                validarImpresion(InitTrans.ultimoReciboSecuencial);
            else if(bundle != null && bundle.getString("secuencial").equals("conAuto"))
                validarImpresion(SaveData.getInstance().getReciboConsultaAuto());
            else
                validarImpresion(InitTrans.ultimoRecibo);
        }while (rsp == Printer.PRINTER_STATUS_PAPER_LACK);
        if (rsp == Printer.PRINTER_OK || rsp == Printer.PRINTER_STATUS_BUSY) {
            Toast.makeText(this, "Impresión realizada con exito.", Toast.LENGTH_SHORT).show();
            UIUtils.startView(this, MainMenu10_Administrativo.class);
        }else {
            Toast.makeText(this, "ERROR - Comuniquese con el Call-Center.", Toast.LENGTH_SHORT).show();
            UIUtils.startView(this, MainMenu10_Administrativo.class);

        }
        countDownTimer.cancel();
    }
    public void cancelar(View view){
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        UIUtils.startView(this, MainMenu10_Administrativo.class);
    }


    public static void startResult(Activity activity , boolean flag , String info){
        Intent intent = new Intent();
        intent.setClass(activity , ResultControl.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag" , flag);
        bundle.putString("info" , info);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

    public void temporizador(){
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                countDownTimer.cancel();
                UIUtils.startView(ImpresionPantalla.this, MainMenu10_Administrativo.class);
            }
        }.start();
    }

    private int validarImpresion(String trans) {
        PrintManager printManager = PrintManager.getmInstance(this);
        int limite = trans.length();
        int offset = 0;
        int valor = 4500;
        if (limite > 5500) {
            while (limite > 0) {
                if (limite >= valor) {
                    rsp = printManager.printReimpresion(trans.substring(offset, offset + valor), true);
                    limite-=valor;
                    offset+=valor;
                } else {
                    rsp = printManager.printReimpresion(trans.substring(offset), true);
                    break;
                }
            }
        } else {
            rsp = printManager.printReimpresion(trans,true);
        }
        return rsp;
    }

    @Override
    public void onBackPressed() {
        UIUtils.startView(this, MainMenu10_Administrativo.class);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
