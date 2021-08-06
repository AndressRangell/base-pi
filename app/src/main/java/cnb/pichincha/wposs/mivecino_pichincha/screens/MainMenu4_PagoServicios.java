package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
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

public class MainMenu4_PagoServicios extends ToolsAppCompact implements View.OnClickListener {

    Button btnAgua, btnElectrico, btnTelefonico, btnInternet;

    Button[] buttons = {btnAgua, btnElectrico, btnTelefonico, btnInternet};
    String[] titulos = {"Servicio agua potable", "Servicio luz eléctrica", "Teléfono / planes", "Internet"};
    String[] values = {"ServicioAguaPotable", "ServicioLuzElectrica","Teléfono/planes", "Internet"};
    TextView tv1;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        Botones();
        theToolbar();
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);
        tv1.setText("Pago de servicios");
        tv1.setTypeface(tipoFuente1);
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, pago de servicios.");
        temporizador();
    }

    @Override
    public void onClick(View view) {

        for (int i = 0; i < buttons.length; i++) {
            if (view.equals(buttons[i])) {
                InitTrans.wrlg.wrDataTxt("Fin timer pago de servicios, inicia transacción en mastercontrol.");
                countDownTimer.cancel();
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(MainMenu4_PagoServicios.this, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[17]);
                intent.putExtra(MasterControl.tipoTrans, values[i]);
                startActivity(intent);
            }
        }

    }

    /**
     *
     */
    public void Botones() {
        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");

        LinearLayout linearLayout = findViewById(R.id.linearBotones);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120);
        lp.setMargins(0, 10, 0, 0);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button(this);
            buttons[i].setBackgroundResource(R.drawable.pichi_btn_menu);
            buttons[i].setLayoutParams(lp);
            buttons[i].setText(titulos[i]);
            buttons[i].setTextColor(Color.parseColor("#0F265C"));
            buttons[i].setTypeface(tipoFuente3);
            buttons[i].setTextSize(22);
            buttons[i].setOnClickListener(this);
            buttons[i].setAllCaps(false);
            linearLayout.addView(buttons[i]);
        }

    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer pago de servicios, desde botón back, ingresa a menú principal.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu4_PagoServicios.this, MainMenuPrincipal.class);
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            countDownTimer.cancel();
            countDownTimer.start();
        }
        return super.onTouchEvent(event);
    }

    /**
     *
     */
    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer PagoServicios");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer PagoServicios, desde método temporizador, ingresa a menú principal");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu4_PagoServicios.this, MainMenuPrincipal.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer PagoServicios, desde método onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Fin timer PagoServicios, desde método onResume.");
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
