package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo.MainMenu10_4_UsuariosLocales;
import cnb.pichincha.wposs.mivecino_pichincha.screens.configuracion.MainMenu10_7_Configuracion;
import newpos.libpay.device.pinpad.InjectMasterKey;
import newpos.libpay.device.printer.PrintManager;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.translog.TransLog;

public class MainMenu10_Administrativo extends ToolsAppCompact implements View.OnClickListener {


    Button btn1, btn2, btn3, btn4, btn5;
    Button btn6, btn7, btn8, btn9, btn10;
    Button btn11, btnClose;
    TMConfig config ;
    private Usuario personaLogin = Login.persona;
    private String role;
    PrintManager manager;
    private String typeTrans;

    Button[] buttons = {btn1, btn2, btn3, btn4, btn5, btn6, btn7,
            btn8, btn9, btn10, btn11, btnClose};

    String[] titulos = {"Visita funcionario", "Comprobante", "Informes", "Usuarios locales", "Mensaje inicial emv", "Turnos", "Configuración",
            "Modo de conexión", "Cargar mk","Versiones","Parámetros","Configuración del sistema"};

    CountDownTimer countDownTimer;

    int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        theToolbar();
        temporizador();
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Administrativo.");
        Botones();
        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setText("Administrativo");
        tv1.setTypeface(tipoFuente1);
        if(TMConfig.getInstance().getmodoOperacion().equals("C")){
            role = "CENTRALIZADO";
        }else   {
            role = personaLogin.getRole();
        }

    }

    @Override
    public void onClick(View view) {
        if(view.equals(buttons[0])){
            if(InitTrans.initialization && InitTrans.initEMV){
                InitTrans.wrlg.wrDataTxt("Fin timer Administrativo, inicia transacción visita de funcionario desde master control.");
                countDownTimer.cancel();
                typeTrans = PrintRes.TRANSEN[11];

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(MainMenu10_Administrativo.this, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[11]);
                startActivity(intent);
            }else {
                Toast.makeText(this, "Debe inicializar para poder usar esta opción", Toast.LENGTH_SHORT).show();
            }
        } else if(view.equals(buttons[1])){
            InitTrans.wrlg.wrDataTxt("Fin timer Administrativo, inicia transacción comprobante desde master control.");
            countDownTimer.cancel();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(this, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[14]);
            intent.putExtra(MasterControl.tipoTrans, "CUPON_HISTORICO");
            startActivity(intent);
        } else if(view.equals(buttons[2])){
            if(InitTrans.initialization && InitTrans.initEMV){
                InitTrans.wrlg.wrDataTxt("Fin timer Administrativo, ingresa a informes.");
                countDownTimer.cancel();
                UIUtils.startView(this, MainMenu10_3_Informes.class);
            }else{
                Toast.makeText(this, "Debe inicializar para poder usar esta opción", Toast.LENGTH_SHORT).show();
            }
        } else if(view.equals(buttons[3])){
            InitTrans.wrlg.wrDataTxt("Fin timer Administrativo, ingresa a ususarios locales.");
            countDownTimer.cancel();
            UIUtils.startView(this, MainMenu10_4_UsuariosLocales.class);
        } else if(view.equals(buttons[4])){
            InitTrans.wrlg.wrDataTxt("Reinicio timer Administrativo, inicia transacción de mensaje inicial emv.");
            reiniciarTimer();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(this, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[0]);
            startActivity(intent);
        } else if(view.equals(buttons[5])){
            if (role.equals("TECNICO")) {
                Toast.makeText(this, "¡Permisos denegados!.", Toast.LENGTH_SHORT).show();
            } else {
                InitTrans.wrlg.wrDataTxt("Fin timer Administrativo, inicia transacción de turnos.");
                countDownTimer.cancel();
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(this, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[13]);
                startActivity(intent);
            }
        } else if(view.equals(buttons[6])){
            InitTrans.wrlg.wrDataTxt("Fin timer Administrativo, ingresa a menú configuración.");
            countDownTimer.cancel();
            UIUtils.startView(MainMenu10_Administrativo.this, MainMenu10_7_Configuracion.class);
        } else if(view.equals(buttons[7])){
            InitTrans.wrlg.wrDataTxt("Fin timer Administrativo, ingresa a menú modo conexión.");
            countDownTimer.cancel();
            UIUtils.startView(this, MainMenu10_8_ModoConexion.class);
        } else if(view.equals(buttons[8])){
            InitTrans.wrlg.wrDataTxt("Fin timer Administrativo, ingresa a InjectMasterKey.");
            countDownTimer.cancel();
            UIUtils.startView(MainMenu10_Administrativo.this, InjectMasterKey.class);
        } else if(view.equals(buttons[9])){
            Toast.makeText(this, "Menú no disponible", Toast.LENGTH_SHORT).show();
        } else if(view.equals(buttons[10])){
            String[] stockArr = {};
            manager = PrintManager.getmInstance(getApplicationContext());
            manager.imprimirParametros(stockArr);
        } else if (view.equals(btnClose)){
            InitTrans.wrlg.wrDataTxt("Fin timer Administrativo, ingresa a startactivity, btnClose.");
            countDownTimer.cancel();
            UIUtils.startView(this, StartActivity.class);
        } else if(view.equals(buttons[11])){
            cont ++;
            if (cont >7 && InitTrans.isReversalTrans){
                final Dialog dialog = new Dialog(MainMenu10_Administrativo.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.custom_dialog);
                TextView tvTitulo = dialog.findViewById(R.id.tvTitulo);
                TextView tvMensaje = dialog.findViewById(R.id.tvMensaje);
                final int ss = Integer.parseInt(DateToStr(new Date(), "ss"));
                final int mm = Integer.parseInt(DateToStr(new Date(), "mm"));
                final int hh = Integer.parseInt(DateToStr(new Date(), "HH"));
                tvTitulo.setText("Código de seguridad \n " + ss + "-" + hh + "-" + mm);
                tvMensaje.setText("Suministra el código de seguridad al ejecutivo del banco");

                final EditText txt = dialog.findViewById(R.id.txt_customDialog);
                Button btn_aceptar = dialog.findViewById(R.id.btn_aceptar);
                Button btn_cancelar = dialog.findViewById(R.id.btn_cancelar);

                btn_aceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int result = ss * hh *mm;
                        int data = Integer.parseInt(txt.getText().toString());
                        if (data == result){
                            InitTrans.wrlg.wrDataTxt("Fin timer Administrativo, se realiza borrado de reverso pendiente, ingresa a startactivity.");
                            countDownTimer.cancel();
                            TransLog.clearReveral();
                            InitTrans.isReversalTrans = false;
                            UIUtils.startView(MainMenu10_Administrativo.this, StartActivity.class);
                        } else {
                            InitTrans.wrlg.wrDataTxt("Reinicio timer Administrativo, desde alertdialog para borrar reverso pendiente.");
                            reiniciarTimer();
                            Toast.makeText(MainMenu10_Administrativo.this, "Código no valido", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                        cont = 0;
                    }
                });
                btn_cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer Administrativo, desde alertdialog botón cancelar, borrar reverso pendiente.");
                        reiniciarTimer();
                        dialog.dismiss();
                        cont = 0;
                    }
                });
                dialog.show();
            }
            //UIUtils.startView(this, MainMenu10_8_PruebasImpresion.class);
            //stopLockTask();
            //StartActivity.MODE_KIOSK = false;
            //startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS ));
        }else if(view.equals(buttons[12])){
            InitTrans.wrlg.wrDataTxt("Fin timer Administrativo, desde botón 12.");
            countDownTimer.cancel();
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

        if(!InitTrans.initialization && !InitTrans.initEMV){
            btnClose = new Button(this);
            btnClose.setLayoutParams(lp);
            btnClose.setText("Cerrar sesión");
            btnClose.setTextColor(Color.parseColor("#0F265C"));
            btnClose.setTypeface(tipoFuente3);
            btnClose.setTextSize(22);
            btnClose.setOnClickListener(this);
            btnClose.setAllCaps(false);
            linearLayout.addView(btnClose);
        }
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        if(InitTrans.initialization && InitTrans.initEMV && !InitTrans.isReversalTrans ){
            countDownTimer.cancel();
            UIUtils.startView(MainMenu10_Administrativo.this, MainMenuPrincipal.class);
        }else{
            countDownTimer.cancel();
            UIUtils.startView(MainMenu10_Administrativo.this, StartActivity.class);
        }
    }

    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer Administrativo");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                countDownTimer.cancel();
                if(InitTrans.initialization && InitTrans.initEMV && !InitTrans.isReversalTrans ){
                    countDownTimer.cancel();
                    UIUtils.startView(MainMenu10_Administrativo.this, MainMenuPrincipal.class);
                }else{
                    countDownTimer.cancel();
                    UIUtils.startView(MainMenu10_Administrativo.this, StartActivity.class);
                }
                InitTrans.wrlg.wrDataTxt("Fin timer Administrativo");
            }
        }.start();
    }

    public static String DateToStr(Date date, String formatString) {
        String str = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString);// formatString
            str = format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        countDownTimer.cancel();
        countDownTimer.start();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Administrativo");
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}