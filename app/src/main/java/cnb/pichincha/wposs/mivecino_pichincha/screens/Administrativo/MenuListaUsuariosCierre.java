package cnb.pichincha.wposs.mivecino_pichincha.screens.Administrativo;

import android.graphics.Typeface;
import android.os.Bundle;
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
import newpos.libpay.presenter.TransUI;

public class MenuListaUsuariosCierre extends ToolsAppCompact {
    TextView tv1;
    ListView listUsers;
    ArrayList<String> listaInfo;
    ArrayList<Usuario> litaUsuario;
    protected TransUI transUI;
    String usuario = null;

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
        UIUtils.startView(MenuListaUsuariosCierre.this, MainMenu10_Administrativo.class);
    }
}
