package cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
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
import newpos.libpay.presenter.TransUI;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;

public class MainMenu10_4_5_1_ReporteUsuario_Pantalla extends ToolsAppCompact {

    TextView tv1;
    ListView listUsers;
    ArrayList<String> listaInfo;
    ArrayList<Usuario> litaUsuario;
    protected TransUI transUI;
    String usuario = null;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_listview_banner2);
        theToolbar();

        Typeface tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        tv1 = findViewById(R.id.tv1);

        tv1.setTypeface(tipoFuente1);
        tv1.setText("Reporte de usuarios");

        listUsers = findViewById(R.id.lv1);
        InitTrans.wrlg.wrDataTxt("Llamado al método temporizador desde onCreate, Reporte pantalla.");
        temporizador();
        showUsers();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaInfo);
        listUsers.setAdapter(adapter);
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
                        "Fecha creación: " + litaUsuario.get(i).getFecha() +"\n" +
                        "Estado cierre: " + litaUsuario.get(i).getEstadoCierre() +"\n" +
                        "Fecha último cierre: " + litaUsuario.get(i).fechaCierrePantalla());
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
        InitTrans.wrlg.wrDataTxt("Fin timer Reporte pantalla, desde botón back, ingresa a usuarios locales.");
        countDownTimer.cancel();
        UIUtils.startView(MainMenu10_4_5_1_ReporteUsuario_Pantalla.this, MainMenu10_4_UsuariosLocales.class);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            countDownTimer.cancel();
            countDownTimer.start();
        }
        return super.onTouchEvent(event);
    }

    public void temporizador(){
        InitTrans.wrlg.wrDataTxt("Inicio timer Reporte usuario pantalla");
        countDownTimer = new CountDownTimer(InitTrans.time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InitTrans.wrlg.wrDataTxt("Fin timer Reporte usuario pantalla, desde método temporizador, ingresa a usuarios locales");
                countDownTimer.cancel();
                UIUtils.startView(MainMenu10_4_5_1_ReporteUsuario_Pantalla.this, MainMenu10_4_UsuariosLocales.class);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitTrans.wrlg.wrDataTxt("Fin timer Reporte usuario pantalla, desde onPause.");
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitTrans.wrlg.wrDataTxt("Reinicio timer Reporte usuario pantalla, desde onResume.");
        reiniciarTimer();
    }

    public void reiniciarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
