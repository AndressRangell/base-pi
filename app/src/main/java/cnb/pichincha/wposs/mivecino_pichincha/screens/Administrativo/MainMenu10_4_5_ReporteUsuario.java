package cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler.ClsConexion;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import newpos.libpay.device.printer.PrintManager;
import newpos.libpay.presenter.TransUI;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu10_4_5_ReporteUsuario extends ToolsAppCompact implements View.OnClickListener {

    Button btn1, btn2;
    TextView tv1;
    protected TransUI transUI;
    private String typeTrans;
    PrintManager manager;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        theToolbar();

        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);

        tv1.setTypeface(tipoFuente1);
        tv1.setText("Reporte de usuarios");
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Reporte usuarios.");
        temporizador();

        botones();

    }

    /**
     *
     */
    public void botones() {

        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");

        LinearLayout linearLayout = findViewById(R.id.linearBotones);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , 120);
        lp.setMargins(0, 20, 0, 0);

        btn1 = new Button(this);
        btn1.setBackgroundResource(R.drawable.pichi_btn_menu);
        btn1.setLayoutParams(lp);
        btn1.setText("Reporte en pantalla");
        btn1.setTextColor(Color.parseColor("#0F265C"));
        btn1.setTypeface(tipoFuente3);
        btn1.setTextSize(22);
        btn1.setOnClickListener(this);
        btn1.setAllCaps(false);
        linearLayout.addView(btn1);

        btn2 = new Button(this);
        btn2.setBackgroundResource(R.drawable.pichi_btn_menu);
        btn2.setLayoutParams(lp);
        btn2.setText("Reporte impreso");
        btn2.setTextColor(Color.parseColor("#0F265C"));
        btn2.setTypeface(tipoFuente3);
        btn2.setTextSize(22);
        btn2.setOnClickListener(this);
        btn2.setAllCaps(false);
        linearLayout.addView(btn2);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btn1)) {
            InitTrans.wrlg.wrDataTxt("Fin timer Reporte usuarios, ingresa a reporte en pantalla.");
            countDownTimer.cancel();
            UIUtils.startView(this, MainMenu10_4_5_1_ReporteUsuario_Pantalla.class);
        } else if (v.equals(btn2)) {
            ClsConexion conexion = new ClsConexion(getApplicationContext());
            ArrayList<Usuario> litaUsuario = conexion.readUserList();
            ArrayList<String> listaInfo;

            if (litaUsuario != null) {
                listaInfo = new ArrayList<String>();
                String[] stockArr = {};

                for (int i = 0; i < litaUsuario.size(); i++) {
                    listaInfo.add("       USUARIO : " + litaUsuario.get(i).getUser() + "\n" +
                            "           ROL : " + litaUsuario.get(i).getRole() + "\n" +
                            "        ESTADO : " + litaUsuario.get(i).getEstado() + "\n" +
                            "FECHA CREACION : " + litaUsuario.get(i).getFecha() + " - "
                            + litaUsuario.get(i).getHora());

                    stockArr = listaInfo.toArray(new String[listaInfo.size()]);
                }
                manager = PrintManager.getmInstance(getApplicationContext());
                manager.reciboUsuarios(stockArr);
            }
        }
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer Reporte usuarios, desde botón back, ingresa a usuarios locales.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_4_5_ReporteUsuario.this, MainMenu10_4_UsuariosLocales.class);
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
        InitTrans.wrlg.wrDataTxt("Inicio timer Reporte usuario");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Reporte usuario, desde método temporizador, ingresa a usuarios locales.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_4_5_ReporteUsuario.this, MainMenu10_4_UsuariosLocales.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Reporte usuario, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Reporte usuario, desde onResume.");
        reiniciarTimer();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
