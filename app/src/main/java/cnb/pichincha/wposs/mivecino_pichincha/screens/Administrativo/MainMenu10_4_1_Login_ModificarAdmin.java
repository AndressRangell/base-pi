package cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler.ClsConexion;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.screens.Login;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu10_4_1_Login_ModificarAdmin extends ToolsAppCompact implements View.OnClickListener {

    TextView tv1, tvMsg1, tvMsg2, txtErrorUser, txtErrorPw;
    EditText et_user, et_pw;
    Button btnLogin;
    String user;
    CountDownTimer countDownTimer;
    public static Usuario PERSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_login);
        theToolbar();

        tv1 = findViewById(R.id.tv1);
        et_user = findViewById(R.id.et_user);
        et_pw = findViewById(R.id.et_pw);
        btnLogin = findViewById(R.id.btLogin);
        tvMsg1 = findViewById(R.id.tvMsg1);
        tvMsg2 = findViewById(R.id.tvMsg2);
        txtErrorUser = findViewById(R.id.txtErrorUsuario);
        txtErrorPw = findViewById(R.id.txtErrorClave);

        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");

        tv1.setText("Modificar administrador");
        tv1.setTypeface(tipoFuente1);
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Login modificar admin.");
        temporizador();
        btnLogin.setTypeface(tipoFuente3);
        tvMsg1.setTypeface(tipoFuente3);
        tvMsg2.setTypeface(tipoFuente3);

        et_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                et_pw.setBackgroundResource(R.drawable.edittext_pichi_normal);
                et_user.setBackgroundResource(R.drawable.edittext_pichi_normal);
                txtErrorUser.setText("");
                txtErrorPw.setText("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(et_user.length() == 10){
                    ClsConexion conexion = new ClsConexion(getApplicationContext());
                    user = et_user.getText().toString();
                    PERSON = conexion.readUser(new Usuario(user));
                    if(PERSON != null){
                        if(PERSON.getRole().equals("USUARIO") && PERSON.getEstado().equals("IN-HABILITADO")){
                            et_user.setBackgroundResource(R.drawable.edittext_pichi_error);
                            txtErrorPw.setText("Usuario deshabilitado." + "\n" + "Comuníquese con el administrador para re-establecer usuario");
                        }else{
                            et_pw.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                            et_pw.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                        }
                    }else{
                        InitTrans.wrlg.wrDataTxt("Reinicio timer Login modificar admin, usuario no existe.");
                        reiniciarTimer();
                        et_user.setText("");
                        et_user.setBackgroundResource(R.drawable.edittext_pichi_error);
                        txtErrorUser.setText("Usuario no existe");
                    }
                }else {
                    et_pw.setText("");
                    et_pw.setFilters(new InputFilter[]{new InputFilter.LengthFilter(0)});
                }

            }
        });

        btnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int intento;
        String valor="";
        ClsConexion conexion10 = new ClsConexion(this);
        String user = et_user.getText().toString();
        String pass = et_pw.getText().toString();

        if (!user.isEmpty() || !pass.isEmpty()) {
            if (Login.persona.getUser().equals(user)) {
                PERSON = conexion10.readUser(new Usuario(user));
                intento = PERSON.getIntento();
                if (PERSON.getRole().equals("ADMINISTRADOR") && PERSON.getPassword().equals(pass)) {
                    intento = 0;
                    valor=Integer.toString(intento);
                    conexion10.updateFields(user,"user_intento",valor,"user");
                    InitTrans.wrlg.wrDataTxt("Fin timer Login modificar admin, ingresa a modificar admin.");
                    countDownTimer.cancel();
                    UIUtils.startView(this,MainMenu10_4_1_1_ModificarAdmin.class);
                } else {
                    if (intento >= 3) {
                        changeToBackgroundError(et_pw,txtErrorPw,"Atención!: recientemente se ha intentado acceder fallidamente como administrador.",R.drawable.edittext_pichi_normal);
                    } else {
                        reiniciarTimer();
                        intento++;
                        InitTrans.wrlg.wrDataTxt("Reinicio timer Login modificar admin, contraseña incorrecta " + intento);
                        valor=Integer.toString(intento);
                        conexion10.updateFields(user,"user_intento",valor,"user");
                        et_pw.setText("");
                        changeToBackgroundError(et_pw,txtErrorPw,"Contraseña incorrecta " + " intento número : " + intento,R.drawable.edittext_pichi_normal);
                    }
                }
            } else {
                InitTrans.wrlg.wrDataTxt("Reinicio timer Login modificar admin, debe ingresar el usuario actual.");
                reiniciarTimer();
                et_user.setText("");
                et_pw.setText("");
                et_user.setBackgroundResource(R.drawable.edittext_pichi_error);
                txtErrorUser.setText("Debe ingresar el usuario actual");
            }
        } else {
            InitTrans.wrlg.wrDataTxt("Reinicio timer Login modificar admin, usuario y clave no pueden estar vacíos.");
            reiniciarTimer();
            et_user.setBackgroundResource(R.drawable.edittext_pichi_error);
            txtErrorUser.setText("Usuario y clave no pueden estar vacíos.");
        }

    }

    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer Login modificar admin, desde botón back, ingresa a Usuarios locales.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_4_1_Login_ModificarAdmin.this, MainMenu10_4_UsuariosLocales.class);
    }

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
        InitTrans.wrlg.wrDataTxt("Inicio timer Login modificar admin");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Login modificar admin, desde método temporizador, ingresa a usuarios locales.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_4_1_Login_ModificarAdmin.this, MainMenu10_4_UsuariosLocales.class);
            }
        }.start();
    }

    private void changeToBackgroundError(EditText edt, TextView txt , String msgError , final int direccion) {

        final EditText editText = (EditText) edt;
        final TextView textView = (TextView) txt;

        textView.setText(msgError);
        editText.setBackgroundResource(R.drawable.edittext_pichi_error);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textView.setText("");
                editText.setBackgroundResource(direccion);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Login modificar admin, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Login modificar admin, desde onResume.");
        reiniciarTimer();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
