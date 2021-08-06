package cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler.ClsConexion;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.screens.Login;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu10_4_1_1_ModificarAdmin extends ToolsAppCompact implements View.OnClickListener {

    TextView tv1, tvMsg1, tvMsg2, tvMsg3;
    Button btnAceptar, btnCancelar;
    EditText et_user, et_pw, et_pwc;
    private Usuario person101 = Login.persona;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_crearusuario_bd);
        theToolbar();

        tv1 = findViewById(R.id.tv1);
        btnAceptar = findViewById(R.id.btAceptar);
        btnCancelar = findViewById(R.id.btCancel);
        et_user = findViewById(R.id.et_user);
        et_pw = findViewById(R.id.et_pw);
        et_pwc = findViewById(R.id.et_pwc);
        tvMsg1 = findViewById(R.id.tvMsg1);
        tvMsg2 = findViewById(R.id.tvMsg2);
        tvMsg3 = findViewById(R.id.tvMsg3);

        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");

        tv1.setTypeface(tipoFuente1);
        tv1.setText("Modificar administrador");
        tvMsg1.setTypeface(tipoFuente3);
        tvMsg2.setTypeface(tipoFuente3);
        tvMsg3.setTypeface(tipoFuente3);
        btnAceptar.setTypeface(tipoFuente3);
        btnCancelar.setTypeface(tipoFuente3);
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Modificar admin.");
        temporizador();
        btnAceptar.setOnClickListener(this);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitTrans.wrlg.wrDataTxt("Fin timer Modificar admin, ingresa a Usuarios locales, desde botón cancelar.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_4_1_1_ModificarAdmin.this,MainMenu10_4_UsuariosLocales.class);
            }
        });
    }


    /**
     *
     */
    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer Modificar admin, ingresa a Usuarios locales, desde botón back.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_4_1_1_ModificarAdmin.this, MainMenu10_4_UsuariosLocales.class);
    }

    @Override
    public void onClick(View view) {
        String usuarioThis = et_user.getText().toString();
        String pass = et_pw.getText().toString();
        String passConf = et_pwc.getText().toString();

        if (usuarioThis.length()<10) {
            InitTrans.wrlg.wrDataTxt("Reinicio timer Modificar admin, código de usuario debe ser de 10 dígitos.");
            reiniciarTimer();
            Toast.makeText(getApplicationContext(),"Código usuario debe ser de 10 dígitos",Toast.LENGTH_SHORT).show();
        } else {
            if ((pass.length() <4 || passConf.length() <4) ){
                InitTrans.wrlg.wrDataTxt("Reinicio timer Modificar admin, la clave debe ser de 4 dígitos.");
                reiniciarTimer();
                Toast.makeText(getApplicationContext(),"La clave debe ser de 4 dígitos.",Toast.LENGTH_SHORT).show();
                et_pw.setText("");
                et_pwc.setText("");
            } else {
                ClsConexion conexion101 = new ClsConexion(this);
                if (passConf.equals(pass)){
                    if (!conexion101.validarUser(usuarioThis)){
                        conexion101.updateUser(usuarioThis,pass);
                        InitTrans.wrlg.wrDataTxt("Fin timer Modificar admin, ingresa a usuarios locales.");
                        countDownTimer.cancel();
                        Toast.makeText(getApplicationContext(),"Operación realizada con éxito",Toast.LENGTH_SHORT).show();
                        UIUtils.startView(MainMenu10_4_1_1_ModificarAdmin.this,MainMenu10_4_UsuariosLocales.class);
                        person101.setUser(usuarioThis);
                    } else {
                        InitTrans.wrlg.wrDataTxt("Reinicio timer Modificar admin, usuario ya existe.");
                        reiniciarTimer();
                        Toast.makeText(this, "Usuario ya existe", Toast.LENGTH_SHORT).show();
                        et_user.setText("");
                    }
                }else {
                    InitTrans.wrlg.wrDataTxt("Reinicio timer Modificar admin, las contraseñas no coinciden.");
                    reiniciarTimer();
                    Toast.makeText(this,"Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    et_pw.setText("");
                    et_pwc.setText("");
                }
            }
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            countDownTimer.cancel();
            countDownTimer.start();
        }
        return super.onTouchEvent(event);
    }

    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer Modificar admin");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Modificar admin, desde método temporizador, ingresa a usuarios locales.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_4_1_1_ModificarAdmin.this, MainMenu10_4_UsuariosLocales.class);
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Modificar admin, desde onResume.");
        reiniciarTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Modificar admin, desde onPause.");
        countDownTimer.cancel();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
