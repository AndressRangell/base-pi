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
import android.widget.Toast;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu9_Productos extends ToolsAppCompact implements View.OnClickListener {

    Button btnDesemCred, btnReporVen, btnSeguros;

    Button[] buttons = {btnDesemCred, btnReporVen, btnSeguros};
    String[] titulos = {"Desembolso de crédito", "Reporte venta de productos", "Seguros"};
    TextView tv1;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Productos.");
        temporizador();
        Botones();
        theToolbar();
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);
        tv1.setText("Productos");
        tv1.setTypeface(tipoFuente1);
    }

    private void Botones() {
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

    @Override
    public void onClick(View view) {
        if(view.equals(buttons[0])){
            InitTrans.wrlg.wrDataTxt("Fin timer Productos, inicia transacción desembolso de crédito desde master control.");
            countDownTimer.cancel();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(MainMenu9_Productos.this, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[19]);
            intent.putExtra(MasterControl.tipoTrans, "DesembolsoCredito");
            startActivity(intent);
        } else if(view.equals(buttons[1])){
            Toast.makeText(this, "Módulo reporte venta de productos no disponible", Toast.LENGTH_SHORT).show();
        } else if(view.equals(buttons[2])){
            Toast.makeText(this, "Módulo seguros no disponible", Toast.LENGTH_SHORT).show();
        }
        
    }

    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer Productos, desde botón back, ingresa al menú principal.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu9_Productos.this, MainMenuPrincipal.class);
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
        InitTrans.wrlg.wrDataTxt("Inicio timer Productos.");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Productos, desde método temporizador, ingresa al menú principal.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu9_Productos.this, MainMenuPrincipal.class);
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Productos, desde método onResume");
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
