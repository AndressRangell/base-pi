package cnb.pichincha.wposs.mivecino_pichincha.screens.configuracion;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pos.device.config.DevConfig;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenuPrincipal;
import cnb.pichincha.wposs.mivecino_pichincha.screens.StartActivity;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import cnb.pichincha.wposs.mivecino_pichincha.screens.custom_dialog;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu10_7_3_Pantalla extends ToolsAppCompact implements View.OnClickListener {

    TextView tv1;
    Button btn1;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        theToolbar();
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Pantalla.");
        temporizador();
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1);
        tv1.setText("Pantalla");
        botones();
    }

    public void botones() {
        LinearLayout linearLayout = findViewById(R.id.linearBotones);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , 120);
        lp.setMargins(0, 10, 0, 0);

        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");


        btn1 = new Button(this);
        btn1.setBackgroundResource(R.drawable.pichi_btn_menu);
        btn1.setLayoutParams(lp);
        btn1.setText("Desactivar modo kiosko");
        /*if (validarModoKiosko()){
            btn1.setText("Desactivar modo kiosko");
        }else{
            btn1.setText("Activar modo kiosko");
        }*/
        btn1.setTextColor(Color.parseColor("#0F265C"));
        btn1.setTypeface(tipoFuente3);
        btn1.setTextSize(22);
        btn1.setOnClickListener(this);
        btn1.setAllCaps(false);
        linearLayout.addView(btn1);
    }

    /**
     *
     */
    public void theToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer Configuración, desde botón back, ingresa a configuración.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_7_3_Pantalla.this, MainMenu10_7_Configuracion.class);
    }

    /**
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        if (view.equals(btn1)){
            Intent intentPack = getPackageManager().getLaunchIntentForPackage("com.downloadmanager");
            startActivity(intentPack);
        }

        /*if(this.btn1.getText().equals("Desactivar modo kiosko")){
            InitTrans.wrlg.wrDataTxt("Reinicio timer pantalla, ingresa alertdialog para 4 últimos dígitos del S/N.");
            reiniciarTimer();
            new custom_dialog(this,MainMenu10_7_3_Pantalla.this);
        }else if(this.btn1.getText().equals("Activar modo kiosko")){
            InitTrans.wrlg.wrDataTxt("Fin timer pantalla, ingresa a Modo kiosko.");
            countDownTimer.cancel();
            UIUtils.startView(this, ModoKiosko.class);
        }*/



    }

    /*@Override
    public void resultado(String val) {
        String noKiosk = val;
        String SN = DevConfig.getSN().substring(6,10);
        String SNinvertido = "";
        for (int x=SN.length()-1;x>=0;x--)
            SNinvertido = SNinvertido + SN.charAt(x);
        if(SNinvertido.equals(noKiosk)){
            InitTrans.wrlg.wrDataTxt("Fin timer pantalla, ingresa a configuración, modo kiosko desactivado.");
            countDownTimer.cancel();
            stopLockTask();
            StartActivity.MODE_KIOSK = false;
            this.btn1.setText("Activar modo kiosko");
            Toast.makeText(MainMenu10_7_3_Pantalla.this, "Modo kiosko desactivado", Toast.LENGTH_SHORT).show();
            UIUtils.startView(this, MainMenu10_7_Configuracion.class);
        }else{
            InitTrans.wrlg.wrDataTxt("Reinicio timer pantalla, código no valido, modo kiosko no desactivado.");
            reiniciarTimer();
            Toast.makeText(MainMenu10_7_3_Pantalla.this, "Codigo no valido", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validarModoKiosko(){
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(am.isInLockTaskMode()){
            return true;
        }else{
            return false;
        }
    }*/

    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer pantalla.");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                countDownTimer.cancel();
                if(!InitTrans.initialization && !InitTrans.initEMV){
                    InitTrans.wrlg.wrDataTxt("Fin timer pantalla, desde método temporizador, ingresa a startactivity, POS no inicializado.");
                    countDownTimer.cancel();
                    UIUtils.startView(MainMenu10_7_3_Pantalla.this, StartActivity.class);
                }else{
                    InitTrans.wrlg.wrDataTxt("Fin timer pantalla, desde método temporizador, ingresa a menú principal, POS inicializado.");
                    countDownTimer.cancel();
                    UIUtils.startView(MainMenu10_7_3_Pantalla.this, MainMenuPrincipal.class);
                }
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer pantalla, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer pantalla, desde onResume.");
        reiniciarTimer();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
