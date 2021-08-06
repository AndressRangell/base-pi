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
import cnb.pichincha.wposs.mivecino_pichincha.screens.ToolsAppCompact;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu10_4_4_1_EliminarUsuario extends ToolsAppCompact {

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

        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1);
        tv1.setText("Selecciona un usuario");
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Eliminar usuario.");
        temporizador();

        listUsers = findViewById(R.id.lv1);

        showUsers();

        final ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaInfo);
        listUsers.setAdapter(adapter1);

        listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InitTrans.wrlg.wrDataTxt("Reinicio timer Eliminar usuario, selección item lista de usuarios.");
                reiniciarTimer();
                final String user = litaUsuario.get(i).getUser();
                String role = litaUsuario.get(i).getRole();
                if (role.equals("ADMINISTRADOR")){
                    Toast.makeText(getApplicationContext(),"Usuario administrador no permitido.",Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                            MainMenu10_4_4_1_EliminarUsuario.this, R.style.Theme_AppCompat_Light_Dialog));

                    builder.setTitle("Advertencia");
                    builder.setMessage("¿Desea eliminar el usuario: " + user + "?");
                    builder.setCancelable(true);

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            conexion.deleteData(user);
                            Toast.makeText(getApplicationContext(),"!Operación realizada con éxito!.",Toast.LENGTH_SHORT).show();
                            InitTrans.wrlg.wrDataTxt("Fin timer Eliminar usuario, ingresa a usuarios locales, usuario eliminado con éxito.");
                            countDownTimer.cancel();
                            UIUtils.startView(MainMenu10_4_4_1_EliminarUsuario.this,MainMenu10_4_UsuariosLocales.class);
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            InitTrans.wrlg.wrDataTxt("Reinicio timer Eliminar usuario, desde botón cancelar.");
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

    /**
     *
     */
    @Override
    public void onBackPressed() {
        InitTrans.wrlg.wrDataTxt("Fin timer Eliminar usuario, desde botón back, ingresa a usuarios locales.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_4_4_1_EliminarUsuario.this, MainMenu10_4_UsuariosLocales.class);
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
        InitTrans.wrlg.wrDataTxt("Inicio timer Eliminar usuario");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Eliminar usuario, desde método temporizador, ingresa a usuarios locales.");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_4_4_1_EliminarUsuario.this, MainMenu10_4_UsuariosLocales.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Eliminar usuario, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Eliminar usuario, desde onResume.");
        reiniciarTimer();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
