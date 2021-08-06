package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu5_1_AntTransito extends ToolsAppCompact implements View.OnClickListener {

    private String typeTrans;
    TextView tv1;
    Button btnAnt,  btnGYE;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        theToolbar();
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1);
        tv1.setText("Ant/Tránsito");
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, AntTransito.");
        temporizador();

        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");
        LinearLayout linearLayout = findViewById(R.id.linearBotones);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120);
        lp.setMargins(0, 10, 0, 0);

        btnAnt = new Button(this);
        btnAnt.setLayoutParams(lp);
        btnAnt.setText("Ant/Tránsito");
        btnAnt.setTextColor(Color.parseColor("#0F265C"));
        btnAnt.setTypeface(tipoFuente3);
        btnAnt.setBackgroundResource(R.drawable.pichi_btn_menu);
        btnAnt.setTextSize(22);
        btnAnt.setOnClickListener(this);
        btnAnt.setAllCaps(false);
        linearLayout.addView(btnAnt);

        btnGYE = new Button(this);
        btnGYE.setLayoutParams(lp);
        btnGYE.setText("Atm/GYE");
        btnGYE.setTextColor(Color.parseColor("#0F265C"));
        btnGYE.setTypeface(tipoFuente3);
        btnGYE.setBackgroundResource(R.drawable.pichi_btn_menu);
        btnGYE.setTextSize(22);
        btnGYE.setOnClickListener(this);
        btnGYE.setAllCaps(false);
        linearLayout.addView(btnGYE);

    }



    @Override
    public void onClick(View view) {

        if (view.equals(btnAnt)) {
            InitTrans.wrlg.wrDataTxt("Fin timer AntTransito, inicia transacción ant desde mastercontrol.");
            countDownTimer.cancel();
            iniciarTrans(17, "ANT");
        }


        if (view.equals(btnGYE)) {
            InitTrans.wrlg.wrDataTxt("Fin timer AntTransito, inicia transacción ant desde mastercontrol.");
            countDownTimer.cancel();
            iniciarTrans(17, "ATM");
        }
    }

    /**
     *
     * @param idTrans
     * @param tipoTrans
     */
    private void iniciarTrans(int idTrans, String tipoTrans){
        typeTrans = PrintRes.TRANSEN[idTrans];

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(MainMenu5_1_AntTransito.this, MasterControl.class);
        intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[idTrans]);
        intent.putExtra(MasterControl.tipoTrans, tipoTrans);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer AntTransito, desde botón back, ingresa a MainMenu5_Recaudaciones.java.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu5_1_AntTransito.this, MainMenu5_Recaudaciones.class);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            countDownTimer.cancel();
            countDownTimer.start();
        }
        return super.onTouchEvent(event);
    }

    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer AntTransito");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer AntTransito, desde método temporizador, ingresa al menú principal.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu5_1_AntTransito.this, MainMenuPrincipal.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer AntTransito, desde método onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume(){
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer AntTransito, desde onResume");
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
