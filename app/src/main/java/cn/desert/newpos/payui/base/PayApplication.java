package cn.desert.newpos.payui.base;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pos.device.ped.KeySystem;
import com.pos.device.ped.KeyType;
import com.pos.device.ped.Ped;
import com.pos.device.printer.Printer;

import java.util.LinkedList;
import java.util.List;

import newpos.libpay.Logger;
import newpos.libpay.PaySdk;
import newpos.libpay.PaySdkException;
import newpos.libpay.PaySdkListener;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;

/**
 * Created by zhouqiang on 2017/7/3.
 */

public class PayApplication extends Application {

    private static PayApplication app ;
    private List<AppCompatActivity> mList = new LinkedList<>();
    public static volatile boolean isInit = false ;
    private static final String APP_RUN = "app_run" ;
    private static final String APP_DEK = "app_des" ;
    private SharedPreferences runPreferences ;
    private SharedPreferences.Editor runEditor ;
    private SharedPreferences dekPreferences ;
    private SharedPreferences.Editor dekEditor ;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this ;
        initPaysdk();
    }

    private void initPaysdk(){
        runPreferences = getSharedPreferences(APP_RUN , MODE_PRIVATE);
        runEditor = runPreferences.edit() ;
        dekPreferences = getSharedPreferences(APP_DEK , MODE_PRIVATE);
        dekEditor = dekPreferences.edit() ;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PaySdk.getInstance().init(app, new PaySdkListener() {
                        @Override
                        public void success() {
                            isInit = true ;

                            int status = Printer.getInstance().getStatus();

                        }
                    });
                }catch (PaySdkException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static PayApplication getInstance(){
        return app ;
    }

    public void addActivity(AppCompatActivity activity) {
        mList.add(activity);
    }

    public void exit() {
        isInit = false ;
        PaySdk.getInstance().exit();
        try {
            for (AppCompatActivity activity : mList) {
                if (activity != null) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
            System.gc();
        }
    }

    public void setRunned(){
        runEditor.clear().commit();
        runEditor.putBoolean(APP_RUN , true).commit();
    }

    public boolean isRunned(){
        return runPreferences.getBoolean(APP_RUN , false) ;
    }

    /**
     * 经典桌面与简约桌面
     * @return
     */
    public boolean isClassical(){
        return dekPreferences.getBoolean(APP_DEK , false);
    }

    public void setClassical(boolean classical){
        dekEditor.clear().commit();
        dekEditor.putBoolean(APP_DEK , classical).commit();
    }


    /**
     *
     */
    public static void initKeysPichincha() {

        int storeKeyIdx = 0;
        int TK_EAK_Index = 0;
        int ret = Ped.getInstance().writeKey(KeySystem.MS_DES, KeyType.KEY_TYPE_PINK, TK_EAK_Index, storeKeyIdx, Ped.KEY_VERIFY_NONE, InitTrans.workingKey);
        Log.d("KEY", "inject working key ret=" + ret);

        int MS_Masterkey_index = 0;
        TK_EAK_Index = 2;
        ret = Ped.getInstance().writeKey(KeySystem.MS_DES, KeyType.KEY_TYPE_EAK, MS_Masterkey_index, TK_EAK_Index, Ped.KEY_VERIFY_NONE, InitTrans.tk_Key);
        Log.d("procesos", "inject TK-key2=" + ret);

    }

    public static byte[] encryp3DES(byte[] data, int len) {
        int TK_EAK_Index = 2;
        byte TDEA_MODE_ECB = 0x01;
        byte TDEA_ENCRYPT = 0x00;

        byte[] IV = ISOUtil.str2bcd("0000000000000000", false);
        byte[] outhex = Ped.getInstance().desDencryptUnified(
                KeySystem.MS_DES, KeyType.KEY_TYPE_EAK, TK_EAK_Index,
                TDEA_ENCRYPT | TDEA_MODE_ECB, IV, data);

        return outhex;
    }
}
