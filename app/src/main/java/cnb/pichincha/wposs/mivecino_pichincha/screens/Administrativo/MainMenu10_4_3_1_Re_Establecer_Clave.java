package cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler.ClsConexion;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenu10_Administrativo;
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu10_4_3_1_Re_Establecer_Clave extends ToolsAppCompact {

    TextView tv1;
    ListView listUsers;
    ArrayList<String> listaInfo;
    ArrayList<Usuario> litaUsuario;
    ClsConexion conexion = new ClsConexion(this);
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_listview_banner2);
        theToolbar();

        tv1 = findViewById(R.id.tv1);

        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1.setTypeface(tipoFuente1);
        tv1.setText("Selecciona un usuario");
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Re-establecer clave.");
        temporizador();
        listUsers = findViewById(R.id.lv1);

        showUsers();

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaInfo);
        listUsers.setAdapter(adapter);

        listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InitTrans.wrlg.wrDataTxt("Reinicio timer Re-establecer clave, se selecciona item de la lista.");
                reiniciarTimer();
                final String usuario = litaUsuario.get(position).getUser();
                String role = litaUsuario.get(position).getRole();

                if (role.equals("ADMINISTRADOR")){
                    Toast.makeText(getApplicationContext(),"Usuario administrador, no permitido.",Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                            MainMenu10_4_3_1_Re_Establecer_Clave.this, R.style.Theme_AppCompat_Light_Dialog));

                    builder.setTitle("Advertencia");
                    builder.setMessage("¿Desea re-establecer el usuario: " + usuario + "?");
                    builder.setCancelable(true);

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            conexion.updateFields(usuario, "user_password", "","user");
                            int intento=0;
                            String valor=Integer.toString(intento);
                            conexion.updateFields(usuario,"user_intento", valor,"user");
                            conexion.updateFields(usuario,"user_estado","RESETEADO","user");
                            Toast.makeText(getApplicationContext(),"El usuario deberá iniciar sesión para re-establecer la clave.",Toast.LENGTH_SHORT).show();
                            InitTrans.wrlg.wrDataTxt("Fin timer Re-establecer clave, se reestablece la contraseña del usuario, ingresa a administrativo.");
                            countDownTimer.cancel();
                            UIUtils.startView(MainMenu10_4_3_1_Re_Establecer_Clave.this,MainMenu10_Administrativo.class);
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            InitTrans.wrlg.wrDataTxt("Reinicio timer Re-establecer clave, desde botón cancelar.");
                            reiniciarTimer();
                            dialogInterface.cancel();
                        }
                    });
                    Dialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }

    /**
     *
     */
    public void showUsers() {
        ClsConexion conexion = new ClsConexion(this);
        litaUsuario = conexion.readUserList();

        if (litaUsuario != null) {
            listaInfo = new ArrayList<String>();

            for (int i=0; i<litaUsuario.size(); i++) {
                listaInfo.add("\n"+"Usuario: " + litaUsuario.get(i).getUser()+ "\n" +
                        "Rol: " + litaUsuario.get(i).getRole() + "\n" +
                        "Estado: " + litaUsuario.get(i).getEstado() + "\n" +
                        "Intentos: " + litaUsuario.get(i).getIntento() + "\n" +
                        "Fecha creación: " + litaUsuario.get(i).getFecha() +"\n");
            }
        } else {
            Toast.makeText(this,"No fue posible realizar consultas",Toast.LENGTH_SHORT).show();
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
        InitTrans.wrlg.wrDataTxt("Inicio timer re-establecer clave");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer re-establecer clave, desde método temporizador, ingresa a usuarios locales.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_4_3_1_Re_Establecer_Clave.this, MainMenu10_4_UsuariosLocales.class);
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer re-establecer clave, desde botón back, ingresa a usuarios locales.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_4_3_1_Re_Establecer_Clave.this, MainMenu10_4_UsuariosLocales.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer re-establecer clave, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer re-establecer clave, desde onResume.");
        reiniciarTimer();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}