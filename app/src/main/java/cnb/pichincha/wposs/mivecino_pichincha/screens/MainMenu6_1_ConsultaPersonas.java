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


import cnb.pichincha.wposs.mivecino_pichincha.R;
import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu6_1_ConsultaPersonas extends ToolsAppCompact implements View.OnClickListener {

    private String typeTrans;
    TextView tv1;
    Button btn1, btn2,btn3, btn4, btn5, btn6;
    Button[] buttons = {btn1, btn2,btn3, btn4, btn5, btn6};
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        theToolbar();
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1);
        tv1.setText("Consultas");
        botones();
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, consultaPersonas.");
        temporizador();

    }

    @Override
    public void onClick(View view) {
        if (view.equals(buttons[0])) {
            InitTrans.wrlg.wrDataTxt("Fin timer consultaPersonas, inicia transacción saldo en cuenta desde mastercontrol.");
            countDownTimer.cancel();
            iniciarTrans(7, "SALDOENCUENTA");
        } else if (view.equals(buttons[1])) {
            InitTrans.wrlg.wrDataTxt("Fin timer consultaPersonas, inicia transacción 10 útimos movimientos desde mastercontrol.");
            countDownTimer.cancel();
            iniciarTrans(7, "TENLASTMOVES");
        }else if (view.equals(buttons[2])) {
            InitTrans.wrlg.wrDataTxt("Fin timer consultaPersonas, inicia transacción saldo en cuenta cnb desde mastercontrol.");
            countDownTimer.cancel();
            iniciarTrans(7, "SALDOENCUENTA_CNB");
        }else if (view.equals(buttons[3])) {
            InitTrans.wrlg.wrDataTxt("Fin timer consultaPersonas, inicia transacción 10 últimos movimienots desde mastercontrol.");
            countDownTimer.cancel();
            iniciarTrans(7, "TENLASTMOVES_CNB");
        }else if (view.equals(buttons[4])) {
            InitTrans.wrlg.wrDataTxt("Fin timer consultaPersonas, inicia transacción consulta valor crédito desde mastercontrol.");
            countDownTimer.cancel();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(MainMenu6_1_ConsultaPersonas.this, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[21]);
            intent.putExtra(MasterControl.tipoTrans, "ConsultaValorCredito");
            startActivity(intent);
        }else if (view.equals(buttons[5])) {
            InitTrans.wrlg.wrDataTxt("Fin timer consultaPersonas, inicia transacción consulta de facturas desde mastercontrol.");
            countDownTimer.cancel();
            iniciarTrans(7, "CONSULTA_FACTURAS");
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
        String[] btnTitulo = {"Saldo en cuenta","10 últimos movimientos", "Saldo en cuenta CNB",
                              "10 últimos movimientos CNB", "Consulta valor crédito", "Consulta de facturas"};
        for(i=0; i<buttons.length;i++){
            buttons[i] = new Button(this);
            buttons[i].setBackgroundResource(R.drawable.pichi_btn_menu);
            buttons[i].setLayoutParams(lp);
            buttons[i].setText(btnTitulo[i]);
            buttons[i].setTextColor(Color.parseColor("#0F265C"));
            buttons[i].setTypeface(tipoFuente3);
            buttons[i].setTextSize(22);
            buttons[i].setOnClickListener(MainMenu6_1_ConsultaPersonas.this);
            buttons[i].setAllCaps(false);
            linearLayout.addView(buttons[i]);
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
        intent.setClass(MainMenu6_1_ConsultaPersonas.this, MasterControl.class);
        intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[idTrans]);
        intent.putExtra(MasterControl.tipoTrans, tipoTrans);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer consultaPersonas, desde botón back, ingresa al menú principal.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu6_1_ConsultaPersonas.this, MainMenuPrincipal.class);
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
        InitTrans.wrlg.wrDataTxt("Inicio timer ConsultaPersonas.");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer ConsultaPersonas, ingresa al menú principal.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu6_1_ConsultaPersonas.this, MainMenuPrincipal.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer consultaPersonas, desde método onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer ConsultasPersonas, desde método onResume");
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
