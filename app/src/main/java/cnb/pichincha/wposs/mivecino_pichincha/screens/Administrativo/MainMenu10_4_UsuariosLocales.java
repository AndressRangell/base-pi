package cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo;

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
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler.ClsConexion;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenu10_Administrativo;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import newpos.libpay.device.printer.PrintRes;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu10_4_UsuariosLocales extends ToolsAppCompact implements View.OnClickListener {

    TextView tv1;
    Usuario person = ClsConexion.persona;
    String role;
    private String typeTrans;
    CountDownTimer countDownTimer;
    Button btn[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_buttons_banner2);
        theToolbar();
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, UsuariosLocales.");
        temporizador();

        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1);
        tv1.setText("Usuarios locales");

        opciones();

    }

    /**
     *
     */
    public void opciones(){

        if(TMConfig.getInstance().getmodoOperacion().equals("C")){
            role="CENTRALIZADO";
        }else {
            role = person.getRole();
        }
        switch(role){
            case "ADMINISTRADOR":
                String[] cuentas = {"Modificar administrador", "Crear usuario", "Re-establecer claves"
                        , "Eliminar usuario", "Reporte de usuarios", "Modificar clave"};
                botones(cuentas);

                break;
            case"USUARIO":
                String[] cuentaUser = {"Modificar clave"};
                botones(cuentaUser);
                break;
            case "CENTRALIZADO":
                String[] cuentasCentralizado = {"Cerrar sesión"};
                botones(cuentasCentralizado);
                break;
            default:
                break;
        }

    }

    /**
     *
     * @param btnTitulo
     */
    public void botones(final String[] btnTitulo){
        LinearLayout linearLayout = findViewById(R.id.linearBotones);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , 120);
        lp.setMargins(0, 10, 0, 0);

        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");

        int i;
        btn = new Button[btnTitulo.length+1];
        for(i=0; i<btnTitulo.length;i++){
            btn[i] = new Button(this);
            btn[i].setBackgroundResource(R.drawable.pichi_btn_menu);
            btn[i].setLayoutParams(lp);
            btn[i].setText(btnTitulo[i]);
            btn[i].setTextColor(Color.parseColor("#0F265C"));
            btn[i].setTypeface(tipoFuente3);
            btn[i].setTextSize(22);
            btn[i].setOnClickListener(this);
            btn[i].setAllCaps(false);
            linearLayout.addView(btn[i]);
        }
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer UsuariosLocales, desde botón back, ingresa a administrativo.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_4_UsuariosLocales.this, MainMenu10_Administrativo.class);
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
        InitTrans.wrlg.wrDataTxt("Inicio timer Usuarios Locales");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Usuarios Locales, desde método temporizador, ingresa a administrativo.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_4_UsuariosLocales.this, MainMenu10_Administrativo.class);
            }
        }.start();
    }

    /**
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch(role) {
            case "ADMINISTRADOR":
                if (view.equals(btn[0])) {
                    InitTrans.wrlg.wrDataTxt("Fin timer Usuarios Locales, ingresa a login modificar admin.");
                    countDownTimer.cancel();
                    UIUtils.startView(MainMenu10_4_UsuariosLocales.this, MainMenu10_4_1_Login_ModificarAdmin.class);
                } else if (view.equals(btn[1])) {
                    InitTrans.wrlg.wrDataTxt("Fin timer Usuarios Locales, ingresa a crear usuario desde mastercontrol.");
                    countDownTimer.cancel();
                    typeTrans = PrintRes.TRANSEN[4];
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(MainMenu10_4_UsuariosLocales.this, MasterControl.class);
                    intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[4]);
                    startActivity(intent);
                } else if (view.equals(btn[2])) {
                    InitTrans.wrlg.wrDataTxt("Fin timer Usuarios Locales, ingresa a login reestablecer clave");
                    countDownTimer.cancel();
                    UIUtils.startView(MainMenu10_4_UsuariosLocales.this, MainMenu10_4_3_Login_Re_Establecer_Clave.class);
                } else if (view.equals(btn[3])) {
                    InitTrans.wrlg.wrDataTxt("Fin timer Usuarios Locales, ingresa a login reestablecer clave");
                    countDownTimer.cancel();
                    UIUtils.startView(MainMenu10_4_UsuariosLocales.this,MainMenu10_4_4_Login_EliminarUsuario.class);
                } else if (view.equals(btn[4])) {
                    InitTrans.wrlg.wrDataTxt("Fin timer Usuarios Locales, ingresa a reporte usuario.");
                    countDownTimer.cancel();
                    UIUtils.startView(MainMenu10_4_UsuariosLocales.this, MainMenu10_4_5_ReporteUsuario.class);
                } else if (view.equals(btn[5])) {
                    InitTrans.wrlg.wrDataTxt("Fin timer Usuarios Locales, ingresa a login modificar clave");
                    countDownTimer.cancel();
                    UIUtils.startView(MainMenu10_4_UsuariosLocales.this, MainMenu10_4_6_Login_ModificarClave.class);
                }
                break;
            case"USUARIO":
                InitTrans.wrlg.wrDataTxt("Fin timer Usuarios Locales, ingresa a login modificar clave");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_4_UsuariosLocales.this,MainMenu10_4_6_Login_ModificarClave.class);
                break;
            case "CENTRALIZADO":
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(this, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[13]);
                startActivity(intent);
                this.finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Usuarios Locales, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Usuarios Locales, desde onResume.");
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
