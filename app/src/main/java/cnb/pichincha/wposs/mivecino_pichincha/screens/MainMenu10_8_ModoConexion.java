package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu10_8_ModoConexion extends ToolsAppCompact implements View.OnClickListener{

    Button btn1, btn2;
    Button[] buttons = {btn1, btn2};
    TextView tv1;
    CountDownTimer countDownTimer;
    protected TMConfig cfg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        theToolbar();
        botones();
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1(this));
        tv1.setText("Modo de conexión");
        temporizador();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(buttons[0])) {
            reiniciarTimer();
            cfg = TMConfig.getInstance();
            cfg.setPubCommun(true);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(getApplicationContext(), MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[0]);
            startActivity(intent);
        } else if (view.equals(buttons[1])) {
            reiniciarTimer();
            cfg = TMConfig.getInstance();
            cfg.setPubCommun(false);
            Intent intent2 = new Intent();
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.setClass(getApplicationContext(), MasterControl.class);
            intent2.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[0]);
            startActivity(intent2);
        }
    }

    /**
     *
     */
    private void botones() {
        LinearLayout linearLayout = findViewById(R.id.linearBotones);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , 120);
        lp.setMargins(0, 10, 0, 0);

        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");

        int i;
        String[] btnTitulo = {"Conexión primaria", "Conexión secundaria"};
        for(i=0; i<buttons.length;i++){
            buttons[i] = new Button(this);
            buttons[i].setBackgroundResource(R.drawable.pichi_btn_menu);
            buttons[i].setLayoutParams(lp);
            buttons[i].setText(btnTitulo[i]);
            buttons[i].setTextColor(Color.parseColor("#0F265C"));
            buttons[i].setTypeface(tipoFuente3);
            buttons[i].setTextSize(22);
            buttons[i].setOnClickListener(MainMenu10_8_ModoConexion.this);
            buttons[i].setAllCaps(false);
            linearLayout.addView(buttons[i]);
        }
    }

    @Override
    public void onBackPressed() {
        countDownTimer.cancel();
        UIUtils.startView(this, MainMenu10_Administrativo.class);
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
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_8_ModoConexion.this, MainMenu10_Administrativo.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reiniciarTimer();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
