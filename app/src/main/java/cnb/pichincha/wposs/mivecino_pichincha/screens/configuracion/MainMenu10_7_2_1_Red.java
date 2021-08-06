package cnb.pichincha.wposs.mivecino_pichincha.screens.configuracion;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pos.device.config.DevConfig;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.screens.StartActivity;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu10_7_2_1_Red extends ToolsAppCompact implements View.OnClickListener {

    TextView tv1;
    Button btn1, btn2, btn3, btn4;
    Button[] buttons = {btn1, btn2, btn3, btn4};
    protected TMConfig cfg;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        theToolbar();
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Red.");
        temporizador();
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1);
        tv1.setText("Ajusted de red");
        botones();
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
        String[] btnTitulo = {"Conexión primaria", "Conexión secundaria", "Configuración ip conexión" , "Configuración apn"};
        for(i=0; i<buttons.length;i++){
            buttons[i] = new Button(this);
            buttons[i].setBackgroundResource(R.drawable.pichi_btn_menu);
            buttons[i].setLayoutParams(lp);
            buttons[i].setText(btnTitulo[i]);
            buttons[i].setTextColor(Color.parseColor("#0F265C"));
            buttons[i].setTypeface(tipoFuente3);
            buttons[i].setTextSize(22);
            buttons[i].setOnClickListener(MainMenu10_7_2_1_Red.this);
            buttons[i].setAllCaps(false);
            linearLayout.addView(buttons[i]);
        }
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer red, desde botón back, ingresa a comunicación.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_7_2_1_Red.this, MainMenu10_7_2_Comunicacion.class);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(buttons[0])) {
            InitTrans.wrlg.wrDataTxt("Reinicio timer red, se realiza conexión a IP primaria.");
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
            InitTrans.wrlg.wrDataTxt("Reinicio timer red, se realiza conexión a IP secundaria.");
            reiniciarTimer();
            cfg = TMConfig.getInstance();
            cfg.setPubCommun(false);
            Intent intent2 = new Intent();
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.setClass(getApplicationContext(), MasterControl.class);
            intent2.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[0]);
            startActivity(intent2);
        } else if (view.equals(buttons[2])) {
            countDownTimer.cancel();
            InitTrans.wrlg.wrDataTxt("Fin timer red, ingresa a personalizar IP de conexión.");
            UIUtils.startView(MainMenu10_7_2_1_Red.this,MainMenu10_7_2_1_3_Personalizar.class);
        } else if (view.equals(buttons[3])) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(new ContextThemeWrapper(
                    this, R.style.Theme_AppCompat_Light_Dialog));

            builder.setTitle("Salir del modo kiosko");
            builder.setMessage("Digita el codigo para poder salir del modo kiosko");
            builder.setCancelable(false);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reiniciarTimer();
                }
            });
            builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    countDownTimer.cancel();
                    String noKiosk;
                    String SN = DevConfig.getSN().substring(6,10);
                    String SNinvertido = "";
                    for (int x=SN.length()-1;x>=0;x--)
                        SNinvertido = SNinvertido + SN.charAt(x);
                    noKiosk = input.getText().toString();
                    if(SNinvertido.equals(noKiosk)){
                        InitTrans.wrlg.wrDataTxt("Fin timer red, ingresa a configuración de apn, se desactiva modo kiosko.");
                        countDownTimer.cancel();
                        StartActivity.MODE_KIOSK = false;
                        //stopLockTask();
                        startActivity(new Intent(Settings.ACTION_APN_SETTINGS));
                        Toast.makeText(MainMenu10_7_2_1_Red.this, "Modo kiosko desactivado", Toast.LENGTH_SHORT).show();
                    }else{
                        InitTrans.wrlg.wrDataTxt("Reinicio timer red, código no válido, modo kiosko no desactivado.");
                        reiniciarTimer();
                        Toast.makeText(MainMenu10_7_2_1_Red.this, "Codigo no válido", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            builder.show();

        }
    }

    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer red.");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer red, desde método temporizador, ingresa a comunicación.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_7_2_1_Red.this, MainMenu10_7_2_Comunicacion.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer red, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer red, desde onResume.");
        reiniciarTimer();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
