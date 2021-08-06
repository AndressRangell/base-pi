package cnb.pichincha.wposs.mivecino_pichincha.screens.configuracion;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu10_7_2_Comunicacion extends ToolsAppCompact implements View.OnClickListener {

    TextView tv1;
    Button btn1;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        theToolbar();
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Comunicación.");
        temporizador();
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1);
        tv1.setText("Comunicación");
        botones();
    }

    /**
     *
     */
    public void botones() {
        LinearLayout linearLayout = findViewById(R.id.linearBotones);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , 120);
        lp.setMargins(0, 10, 0, 0);

        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");


        btn1 = new Button(this);
        btn1.setBackgroundResource(R.drawable.pichi_btn_menu);
        btn1.setLayoutParams(lp);
        btn1.setText("Red");
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
        InitTrans.wrlg.wrDataTxt("Fin timer Comunicación, desde botón back, ingresa a configuración.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_7_2_Comunicacion.this, MainMenu10_7_Configuracion.class);
    }

    /**
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view.equals(btn1)) {
            InitTrans.wrlg.wrDataTxt("Fin timer Comunicación, ingresa a red.");
            countDownTimer.cancel();
            UIUtils.startView(MainMenu10_7_2_Comunicacion.this,MainMenu10_7_2_1_Red.class);
        }
    }

    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer Comunicación.");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Comunicación, desde método temporizador, ingresa a configuración.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_7_2_Comunicacion.this, MainMenu10_7_Configuracion.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Comunicación, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Comunicación, desde onResume.");
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
