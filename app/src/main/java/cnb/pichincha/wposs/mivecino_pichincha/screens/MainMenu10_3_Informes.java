package cnb.pichincha.wposs.mivecino_pichincha.screens;


import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pos.device.config.DevConfig;

import java.util.List;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.device.printer.PrintManager;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.translog.TransLog;
import newpos.libpay.trans.translog.TransLogData;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;

import static newpos.libpay.trans.Trans.CIERRE_TOTAL_LOG;

public class MainMenu10_3_Informes extends ToolsAppCompact implements View.OnClickListener {

    Button trans, diario, limite, venta;
    PrintManager manager;
    TextView tv1;
    CountDownTimer countDownTimer;
    protected TMConfig cfg;

    Button[] buttons = {trans, diario, limite, venta};

    String[] titulos = {"Transacciones", "Diario de transacciones","Límite de operación","Venta de productos"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        theToolbar();
        botones();
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1(this));
        tv1.setText("Informes");
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Informes.");
        temporizador();
    }

    @Override
    public void onClick(View view) {
        if(view.equals(buttons[0])){
            InitTrans.wrlg.wrDataTxt("Fin timer Informes, inicia impresión desde método crearInforme().");
            countDownTimer.cancel();
            crearInforme();
        } else if(view.equals(buttons[1])){
            InitTrans.wrlg.wrDataTxt("Fin timer Informes, inicia diario transacciones desde mastercontrol .");
            countDownTimer.cancel();
            iniciarTransaccion(this, 7, "DIARIOTRANS");
        } else if(view.equals(buttons[2])){
            manager = PrintManager.getmInstance(getApplication());
            manager.reciboLimites();
        } else if(view.equals(buttons[3])){
            InitTrans.wrlg.wrDataTxt("Fin timer Informes, inicia venta de productos desde mastercontrol .");
            countDownTimer.cancel();
            iniciarTransaccion(this, 7, "VENTAPROD");
        }

    }

    private void crearInforme() {
        cfg = TMConfig.getInstance();
        List<TransLogData> list = TransLog.getInstance(CIERRE_TOTAL_LOG, true).getData();
        if(list.size() != 0){
            String print;
            print = "************************************************";
            print += "                                                ";
            print += "                  INFORME                       ";
            print += "                                                ";
            print += PAYUtils.getLocalFechaHora()+ "                           ";
            print += "Serial: "+ DevConfig.getSN() +"                              ";
            print += "Operador: "+ cfg.getMerchID().trim() +"                            ";
            print += "************************************************";
            int i;
            for (i = 0; i < list.size();i++){
                String hora = list.get(i).getLocalTime();
                String trace = list.get(i).getTraceNo();
                String rnn = list.get(i).getRRN();
                String msgID = list.get(i).getMsgID();
                String procCode = list.get(i).getProcCode();
                long monto = list.get(i).getAmount();
                print += "  " + PAYUtils.StringPattern(hora, "HHmmss", "HH:mm:ss") + "  " +
                        trace + "   " + rnn + "  " + msgID + " \n" +
                        procCode + "               $" + ISOUtil.formatMiles((int) monto) + "\n\n";

            }

            print += "************************************************";

            manager = PrintManager.getmInstance(getApplicationContext());
            int rta = manager.printInformes(print, 16, "font/mplus-1m-bold.ttf", this);
            if (rta == 0){
                UIUtils.startView(this, MainMenu10_Administrativo.class);
            }
        }else {
            Toast.makeText(this, "No existen transacciones. ", Toast.LENGTH_SHORT).show();
            InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde crearInforme, Informes.");
            temporizador();
        }

    }

    /**
     *
     */
    public void botones() {
        LinearLayout linearLayout = findViewById(R.id.linearBotones);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , 120);
        lp.setMargins(0, 10, 0, 0);

        //Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button(this);
            buttons[i].setBackgroundResource(R.drawable.pichi_btn_menu);
            buttons[i].setLayoutParams(lp);
            buttons[i].setText(titulos[i]);
            buttons[i].setTextColor(Color.parseColor("#0F265C"));
            buttons[i].setTypeface(tipoFuente3(this));
            buttons[i].setTextSize(22);
            buttons[i].setOnClickListener(this);
            buttons[i].setAllCaps(false);
            linearLayout.addView(buttons[i]);
        }
    }

    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer Informes, desde botón back, ingresa al menú administrativo.");
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
        InitTrans.wrlg.wrDataTxt("Inicio timer Informes.");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Informes, desde método temporizador, ingresa al menú administrativo.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_3_Informes.this, MainMenu10_Administrativo.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Informes, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Informes, desde onResume.");
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
