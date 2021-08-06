package cn.desert.newpos.payui.setting.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import cn.desert.newpos.payui.IItem;
import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.setting.ui.simple.CommunSettings;
import cn.desert.newpos.payui.setting.ui.simple.PrivateSettings;
import cn.desert.newpos.payui.setting.ui.simple.TransSetting;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.screens.MainMenu10_Administrativo;
import newpos.libpay.device.pinpad.InjectMasterKey;
import newpos.libpay.global.TMConfig;



public class SettingsFrags extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private GridView mGrid ;
    private LinearLayout rootLayout ;
    private Dialog mDialog ;
    private ListView listView = null ;
    private TextView tv1;
    public static String JUMP_KEY = "JUMP_KEY" ;
    Typeface tipoFuente1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tipoFuente1 = Typeface.createFromAsset(this.getAssets(), "font/PreloSlab-Book.otf");
        init();
    }

    public void setting_return(View v){
        init();
    }

    private void init(){
        setContentView(R.layout.setting);
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(tipoFuente1);
        tv1.setText("Configuración del fabricante");
        mGrid = (GridView) findViewById(R.id.setting_gridview);
        listView = (ListView) findViewById(R.id.setting_listview);
        listView.setAdapter(formatAdapter2());
        listView.setOnItemClickListener(this);
        rootLayout = (LinearLayout) findViewById(R.id.setting_root);
        mGrid.setAdapter(formatAdapter());
        mGrid.setOnItemClickListener(this);
        findViewById(R.id.setting_maintain_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMaintainPwd();
            }
        });
        findViewById(R.id.setting_errlog_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(JUMP_KEY , getString(R.string.errlog_list));

                startActivity(intent);
            }
        });
        findViewById(R.id.setting_advice_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(JUMP_KEY , getString(R.string.feedback_advice));
                startActivity(intent);
            }
        });
        findViewById(R.id.setting_android_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        String text = ((TextView)view.findViewById(R.id.setting_listitem_tv)).getText().toString();
        intent.putExtra(JUMP_KEY , text);
        switch (i){
            case 0:
                intent.setClass(this , CommunSettings.class);
                break;
            case 1:
                intent.setClass(this , TransSetting.class);
                break;
            case 2:
                intent.setClass(this , InjectMasterKey.class);
                break;
            case 3:
                intent.setClass(this , PrivateSettings.class);
                break;
        }
        startActivity(intent);
    }


    private static final String MAP_TV = "MAP_TV" ;
    private static final String MAP_IV = "MAP_IV" ;
    private ArrayList<HashMap<String,Object>> list ;
    private ListAdapter formatAdapter(){
        list = new ArrayList<>();
        String[] names = getResources().getStringArray(R.array.setting_items);
        for (int i = 0 ; i < names.length ; i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put(MAP_TV , names[i]);
            map.put(MAP_IV , IItem.Settings.IMGS[i]);
            list.add(map);
        }
        return new SimpleAdapter(this , list , R.layout.setting_item_view,
                new String[]{MAP_IV , MAP_TV},new int[]{R.id.setting_item_iv,R.id.setting_item_tv});
    }

    private ListAdapter formatAdapter2(){
        list = new ArrayList<>();
        String[] names = getResources().getStringArray(R.array.setting_items2);
        for (int i = 0 ; i < names.length ; i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put(MAP_TV , names[i]);
            map.put(MAP_IV , IItem.Settings.IMGS2[i]);
            list.add(map);
        }
        return new SimpleAdapter(this , list , R.layout.setting_list_item,
                new String[]{MAP_IV , MAP_TV},new int[]{R.id.setting_listitem_iv,R.id.setting_listitem_tv});
    }

    private void changeMaintainPwd(){
        mDialog = UIUtils.centerDialog(this , R.layout.setting_home_pass, R.id.setting_pass_layout);
        final EditText newEdit = (EditText) mDialog.findViewById(R.id.setting_pass_new);
        final EditText oldEdit = (EditText) mDialog.findViewById(R.id.setting_pass_old);
        Button confirm = (Button) mDialog.findViewById(R.id.setting_pass_confirm);
        mDialog.findViewById(R.id.setting_pass_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String np = newEdit.getText().toString();
                String op = oldEdit.getText().toString();
                if(op.equals(TMConfig.getInstance().getMaintainPass())){
                    if(np.equals("")||np == null){
                        Toast.makeText(SettingsFrags.this , getString(R.string.data_null) , Toast.LENGTH_SHORT).show();
                    }else {
                        mDialog.dismiss();
                        TMConfig.getInstance().setMaintainPass(np).save();
                        Toast.makeText(SettingsFrags.this , getString(R.string.save_success) , Toast.LENGTH_SHORT).show();
                    }
                }else {
                    newEdit.setText("");
                    oldEdit.setText("");
                    Toast.makeText(SettingsFrags.this , getString(R.string.original_pass_err) , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        UIUtils.startView(SettingsFrags.this, MainMenu10_Administrativo.class);
    }
}
