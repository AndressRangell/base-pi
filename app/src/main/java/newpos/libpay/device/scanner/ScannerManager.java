package newpos.libpay.device.scanner;

import android.support.v7.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.desert.keyboard.InputManager;
import com.pos.device.scanner.OnScanListener;
import com.pos.device.scanner.Scanner;


import newpos.libpay.trans.Tcode;

/**
 * Created by zhouqiang on 2017/7/7.
 * @author zhouqiang
 * 扫码管理
 */

public class ScannerManager {

    private static ScannerManager instance ;
    private static InputManager.Style mode ;
    private static InnerScanner scanner ;

    public static boolean isBackC = false;

    private ScannerManager(){}

    public static ScannerManager getInstance(AppCompatActivity activity , InputManager.Style m){
        mode = m ;
        scanner = new InnerScanner(activity);
        if(null == instance){
            instance = new ScannerManager();
        }
        return instance ;
    }

    private QRCListener listener ;

    public void getQRCode(int timeout , QRCListener l){

        scanner.initScanner();
        final QRCInfo info = new QRCInfo();
        if(null == l){
            info.setResultFalg(false);
            info.setErrno(Tcode.T_invoke_para_err);
            listener.callback(info);
        }else {

            this.listener = l ;
            scanner.startScan(timeout, new InnerScannerListener() {
                @Override
                public void onScanResult(int retCode, byte[] data) {
                    if(Tcode.Status.scan_success == retCode){
                        info.setResultFalg(true);
                        info.setQrc(new String(data));
                    }else {
                        info.setResultFalg(false);
                        info.setErrno(retCode);
                    }
                    listener.callback(info);
                }
            });
//            
        }
    }


    private Scanner scannerBar = Scanner.getInstance();

    public static ScannerManager getScannerInstance()
    {
        return instance == null?instance=new ScannerManager():instance;
    }

    public void startScanner( final QRCListener listener){
        final Bundle bundle = new Bundle();
        bundle.putBoolean("play_beep",true);
        bundle.putBoolean("continue_scan",false);
        bundle.putBoolean("is_Back_Camera",isBackC);
        scannerBar.initScanner(bundle);
        scannerBar.startScan(60 * 1000, new OnScanListener() {
            @Override
            public void onScanResult(int result, byte[] data) {
                QRCInfo info = new QRCInfo();
                Log.d("Salida", "call scanner: "+result);
                switch (result)
                {
                    case Scanner.SCANNER_TIMEOUT:
                        info.setErrno(result);
                        info.setResultFalg(false);
                        break;
                    case Scanner.SCANNER_PARAM_INVALID:
                        info.setErrno(result);
                        info.setResultFalg(false);
                        break;
                    case Scanner.SCANNER_SUCCESS:
                        info.setQrc(new String(data));
                        info.setResultFalg(true);
                        break;
                    default:
                        break;
                }
                if (result !=Scanner.SCANNER_EXIT){
                    listener.callback(info);
                    scannerBar.stopScan();
                }
            }
        });
    }

}
