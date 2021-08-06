package cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo;

import android.graphics.Typeface;
import android.os.Bundle;
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
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenuPrincipal;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;

public class MainMenu10_4_3_2_Re_EstablecerClave extends ToolsAppCompact implements View.OnClickListener {

    TextView tv1, tvMsg1, tvMsg2, tvMsg3;
    Button btnAceptar, btnCancelar;
    EditText et_user, et_pw, et_pwc;
    private Usuario person101 = Login.persona;
    private String usuario, estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_crearusuario_bd);

        tv1 = findViewById(R.id.tv1);
        btnAceptar = findViewById(R.id.btAceptar);
        btnCancelar = findViewById(R.id.btCancel);
        et_user = findViewById(R.id.et_user);
        et_pw = findViewById(R.id.et_pw);
        et_pwc = findViewById(R.id.et_pwc);
        tvMsg1 = findViewById(R.id.tvMsg1);
        tvMsg2 = findViewById(R.id.tvMsg2);
        tvMsg3 = findViewById(R.id.tvMsg3);

        Typeface tipoFuente2 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Bold.otf");
        Typeface tipoFuente3 = Typeface.createFromAsset(this.getAssets(), "font/Prelo-Medium.otf");

        tv1.setText("Modificar clave");
        tvMsg1.setTypeface(tipoFuente3);
        tvMsg2.setTypeface(tipoFuente3);
        tvMsg3.setTypeface(tipoFuente3);
        btnAceptar.setTypeface(tipoFuente2);
        btnCancelar.setTypeface(tipoFuente2);

        btnAceptar.setOnClickListener(this);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.startView(MainMenu10_4_3_2_Re_EstablecerClave.this,Login.class);
            }
        });
        usuario = person101.getUser().toString();
        estado = person101.getEstado().toString();
        et_user.setText(usuario);
        et_user.setEnabled(false);


    }

    @Override
    public void onClick(View view) {
        String pass = et_pw.getText().toString();
        String passConf = et_pwc.getText().toString();

        if (pass.length() < 4 || passConf.length()<4) {
            Toast.makeText(getApplicationContext(),"La clave debe ser de 4 dígitos.",Toast.LENGTH_SHORT).show();
            et_pw.setText("");
            et_pwc.setText("");
        } else {
            if (pass.equals(passConf)) {
                if (estado.equals("RESETEADO")) {
                    ClsConexion conexion = new ClsConexion(this);
                    conexion.updatePass(usuario,pass, "HABILITADO");
                    Toast.makeText(this,"Clave actualizada",Toast.LENGTH_SHORT).show();
                    UIUtils.startView(MainMenu10_4_3_2_Re_EstablecerClave.this, MainMenuPrincipal.class);
                } else {
                    ClsConexion conexion = new ClsConexion(this);
                    conexion.updatePass(usuario,pass,estado);
                    Toast.makeText(this,"Clave actualizada",Toast.LENGTH_SHORT).show();
                    UIUtils.startView(MainMenu10_4_3_2_Re_EstablecerClave.this, MainMenuPrincipal.class);
                }
            } else {
                Toast.makeText(this,"Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                et_pw.setText("");
                et_pwc.setText("");
            }
        }
    }

    @Override
    public void onBackPressed() {

    }
}
