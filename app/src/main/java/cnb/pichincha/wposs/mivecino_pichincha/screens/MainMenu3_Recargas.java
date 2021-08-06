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
import cnb.pichincha.wposs.mivecino_pichincha.tools.Wrlg;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu3_Recargas extends ToolsAppCompact implements View.OnClickListener{

    Button btn1, btn2;
    Button[] buttons = {btn1, btn2};
    TextView tv1;

    CountDownTimer countDownTimer;
    Wrlg wrlg = new Wrlg();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        theToolbar();
        Botones();
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);
        tv1.setText("Recargas");
        tv1.setTypeface(tipoFuente1);
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, recargas.");
        temporizador();
    }

    @Override
    public void onClick (View view){
        if(view.equals(buttons[0])){
            InitTrans.wrlg.wrDataTxt("Fin timer recargas, inicia recarga celular en mastercontrol.");
            countDownTimer.cancel();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(MainMenu3_Recargas.this, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[17]);
            intent.putExtra(MasterControl.tipoTrans, "RecargaOperadores");
            startActivity(intent);
        } else if(view.equals(buttons[1])) {
            InitTrans.wrlg.wrDataTxt("Fin timer recargas, inicia televisión prepago en mastercontrol.");
            countDownTimer.cancel();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(MainMenu3_Recargas.this, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[17]);
            intent.putExtra(MasterControl.tipoTrans, "TelevisionPrepago");
            startActivity(intent);
        }

    }

    /**
     *
     */
    public void Botones() {
        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");
        LinearLayout linearLayout = findViewById(R.id.linearBotones);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , 120);
        lp.setMargins(0, 10, 0, 0);

        int i;
        String[] btnTitulo = {"Recarga celular", "Televisión prepago"};
        for(i=0; i<buttons.length;i++){
            buttons[i] = new Button(this);
            buttons[i].setBackgroundResource(R.drawable.pichi_btn_menu);
            buttons[i].setLayoutParams(lp);
            buttons[i].setText(btnTitulo[i]);
            buttons[i].setTextColor(Color.parseColor("#0F265C"));
            buttons[i].setTypeface(tipoFuente3);
            buttons[i].setTextSize(22);
            buttons[i].setOnClickListener(MainMenu3_Recargas.this);
            buttons[i].setAllCaps(false);
            linearLayout.addView(buttons[i]);
        }
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer recargas, desde botón back, ingrsa a menú principal.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu3_Recargas.this, MainMenuPrincipal.class);
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
        InitTrans.wrlg.wrDataTxt("Inicio timer Recargas");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Recargas, desde método temporizador, ingresa a menú principal");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu3_Recargas.this, MainMenuPrincipal.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer recargas, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer recargas, desde onResume.");
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
