package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class MainMenu5_Recaudaciones extends AppCompatActivity implements View.OnClickListener {

    Button btnRecauEmpr,btnCrecitCard, btnPagoCredito, btnBonoDH, btnCasaComer, btnVentaCat, btnTvPag, btnANT, btnCentrEdu, btnRecauFreq;

    Button[] buttonsMenuRecau =  {btnRecauEmpr,btnCrecitCard, btnPagoCredito, btnBonoDH, btnCasaComer, btnVentaCat, btnTvPag, btnANT, btnCentrEdu, btnRecauFreq};
    String[] titulos = {"Recaudación empresa", "Tarjeta de crédito",
            "Pago crédito", "Bono", "Casas comerciales",
            "Venta por catálogo", "Televisión pagada", "Automotores/Peatones",
            "Centros educativos", "Recaudaciones frecuentes"};


    TextView tv1;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        Botones();
        TheToolbar();
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);
        tv1.setText("Recaudaciones");
        tv1.setTypeface(tipoFuente1);
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, recaudaciones.");
        temporizador();
    }

    /**
     *
     */
    private void Botones() {
        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");
        LinearLayout linearLayout = findViewById(R.id.linearBotones);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120);
        lp.setMargins(0, 10, 0, 0);

        for (int i = 0; i < buttonsMenuRecau.length; i++) {
            buttonsMenuRecau[i] = new Button(this);
            buttonsMenuRecau[i].setBackgroundResource(R.drawable.pichi_btn_menu);
            buttonsMenuRecau[i].setLayoutParams(lp);
            buttonsMenuRecau[i].setText(titulos[i]);
            buttonsMenuRecau[i].setTextColor(Color.parseColor("#0F265C"));
            buttonsMenuRecau[i].setTypeface(tipoFuente3);
            buttonsMenuRecau[i].setTextSize(22);
            buttonsMenuRecau[i].setOnClickListener(this);
            buttonsMenuRecau[i].setAllCaps(false);
            linearLayout.addView(buttonsMenuRecau[i]);
        }

    }

    @Override
    public void onClick(View view) {

        if(view.equals(buttonsMenuRecau[5])) {
            InitTrans.wrlg.wrDataTxt("Fin timer recaudaciones, inicia transacción venta por catálogo en mastercontrol.");
            countDownTimer.cancel();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(MainMenu5_Recaudaciones.this, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[17]);
            intent.putExtra(MasterControl.tipoTrans, "VentaCatalogo");
            startActivity(intent);
        }

        if(view.equals(buttonsMenuRecau[3])){
            InitTrans.wrlg.wrDataTxt("Fin timer recaudaciones, inicia transacción bono desarrollo humano en mastercontrol.");
            countDownTimer.cancel();
            Intent intent2 = new Intent();
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.setClass(MainMenu5_Recaudaciones.this, MasterControl.class);
            intent2.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[17]);
            intent2.putExtra(MasterControl.tipoTrans, "BonoDesarrolloHumano");
            startActivity(intent2);
        }

        if(view.equals(buttonsMenuRecau[0])){
            InitTrans.wrlg.wrDataTxt("Fin timer recaudaciones, inicia transacción recaudación empresa en mastercontrol.");
            countDownTimer.cancel();
            Intent intent2 = new Intent();
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.setClass(MainMenu5_Recaudaciones.this, MasterControl.class);
            intent2.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[17]);
            intent2.putExtra(MasterControl.tipoTrans, "RecaudacionEmpresa");
            startActivity(intent2);
        }

        if(view.equals(buttonsMenuRecau[1])){
            InitTrans.wrlg.wrDataTxt("Fin timer recaudaciones, inicia transacción tarjeta de crédito en mastercontrol.");
            countDownTimer.cancel();
            Intent intent2 = new Intent();
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.setClass(MainMenu5_Recaudaciones.this, MasterControl.class);
            intent2.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[17]);
            intent2.putExtra(MasterControl.tipoTrans, "TarjetaCredito");
            startActivity(intent2);
        }

        if(view.equals(buttonsMenuRecau[6])){
            InitTrans.wrlg.wrDataTxt("Fin timer recaudaciones, inicia transacción televisión pagada en mastercontrol.");
            countDownTimer.cancel();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(MainMenu5_Recaudaciones.this, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[17]);
            intent.putExtra(MasterControl.tipoTrans, "TelevisionPagada");
            startActivity(intent);
        }

        if(view.equals(buttonsMenuRecau[4])) {
            InitTrans.wrlg.wrDataTxt("Fin timer recaudaciones, inicia transacción casas comerciales en mastercontrol.");
            countDownTimer.cancel();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(MainMenu5_Recaudaciones.this, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[17]);
            intent.putExtra(MasterControl.tipoTrans, "CasasComerciales");
            startActivity(intent);
        }

        if(view.equals(buttonsMenuRecau[2])){
            InitTrans.wrlg.wrDataTxt("Fin timer recaudaciones, inicia transacción pago crédito en mastercontrol.");
            countDownTimer.cancel();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(MainMenu5_Recaudaciones.this, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[17]);
            intent.putExtra(MasterControl.tipoTrans, "PagoCredito");
            startActivity(intent);
        }

        if(view.equals(buttonsMenuRecau[8])){
            intentMetodo("CentrosEducativos");
        }

        if(view.equals(buttonsMenuRecau[7])){
            InitTrans.wrlg.wrDataTxt("Fin timer recaudaciones, inicia transacción automotores/peatones en MainMenu5_1_AntTransito.java.");
            countDownTimer.cancel();
            UIUtils.startView(MainMenu5_Recaudaciones.this, MainMenu5_1_AntTransito.class);
        }

    }

    private void intentMetodo(String tipoTrans) {
        InitTrans.wrlg.wrDataTxt("Fin timer recaudaciones, inicia transacción pago crédito en mastercontrol.");
        countDownTimer.cancel();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(MainMenu5_Recaudaciones.this, MasterControl.class);
        intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[17]);
        intent.putExtra(MasterControl.tipoTrans, tipoTrans);
        startActivity(intent);
    }


    /**
     *
     */
    public void TheToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer recaudaciones, desde botón back, ingresa a menú principal.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu5_Recaudaciones.this, MainMenuPrincipal.class);
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
     * @param
     */
    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer Recaudaciones");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Recaudaciones, desde método temporizador, ingresa al menú principal.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu5_Recaudaciones.this, MainMenuPrincipal.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Recaudaciones, desde método onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume(){
        super.onResume();
        try{
            MasterControl.masterCDT.cancel();
        }catch (Exception e){
            e.printStackTrace();
        }
        InitTrans.wrlg.wrDataTxt("Reinicio timer Recaudaciones, desde método onResume");
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
