package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;

import java.io.File;
import java.io.FileFilter;
import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;


public class EliminarLogs extends AsyncTask<String, Void, String> {

    private Context context;
    private TcpCallback callback;
    private ProgressDialog pd;
    private int timeOut, timeMessages=0;
    CountDownTimer cDt;

    public EliminarLogs(Context context, int timeOut, TcpCallback callback) {
        this.context = context;
        this.callback = callback;
        this.timeOut = timeOut*1000;
    }

    @Override
    protected String doInBackground(String... strings) {
        int logElim =0;
        try{
            SharedPreferences logs =context.getSharedPreferences("logs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=logs.edit();
            editor.putString("version", UIUtils.versionApk(context));
            editor.commit();

            String versionAppEnShared =  logs.getString("versionAnterior","noDATA");
            String versionActual =  UIUtils.versionApk(context);

            if(!versionAppEnShared.equals("noDATA")){
                if(!versionAppEnShared.equals(versionActual)){

                    int elm = eliminarPorExtension("/storage/sdcard0/logs/ISO/",".txt");
                    if (elm==1){
                        logElim = 1;
                    }

                    editor.putString("versionAnterior", versionActual);
                    editor.apply();


                }
            }else{
                int elm = eliminarPorExtension("/storage/sdcard0/logs/ISO/",".txt");
                if (elm==1){
                    logElim = 1;
                }

                editor.putString("versionAnterior", versionActual);
                editor.apply();
            }


        }catch (Exception e){
            logElim=0;

        }

        return String.valueOf(logElim);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        try{

            pd = new ProgressDialog(context);
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
            pd.setTitle("Actualizando");
            Mensajes1();
        }
        catch(Exception e){
            System.out.println(""+e);
        }
    }

    private void IniciarMensaje(){
        cDt = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                timeMessages++;
                if (timeMessages > 3){
                    timeMessages = 0;
                }
                Mensajes1();
            }
        }.start();
    }

    private void Mensajes1(){
        switch (timeMessages)
        {
            case 0:
                pd.setMessage("Aplicando actualización... \nPor favor no apagues el dispositivo");
                break;
            case 1:
                pd.setMessage("Realizando mejoras al aplicativo...\nPor favor no apagues el dispositivo");
                break;
            case 2:
                pd.setMessage("Aplicando configuraciones...\nPor favor no apagues el dispositivo");
                break;
            case 3:
                pd.setMessage("Gracias por su espera...\nPor favor no apagues el dispositivo");
                break;
        }
        pd.show();
        IniciarMensaje();
    }


    @Override
    protected void onPostExecute(String s) {
        try {

            if (pd != null && pd.isShowing())
                pd.dismiss();
            if (cDt != null){
                cDt.cancel();
            }
            callback.RspLog(Integer.parseInt(s));

        } catch (Exception e) {

            System.out.println("Error: \n"+e.getMessage());
            e.printStackTrace();
        }

    }


    int elm;
    public int eliminarPorExtension(String path, final String extension){
        elm=0;
        try{
            File[] archivos = new File(path).listFiles(new FileFilter() {
                public boolean accept(File archivo) {
                    if (archivo.isFile())
                        return archivo.getName().endsWith(extension);

                    return false;
                }
            });

            for (File archivo : archivos) {
                archivo.delete();
            }

            elm=1;
        }catch (Exception ex){
            ex.printStackTrace();
            elm=0;
        }
        return elm;
    }

    public static void eliminarCarpeta(File path){
        try{
            if (path.isDirectory())
            {
                for (File hijo : path.listFiles())
                    eliminarCarpeta(hijo);
            }
            else{
                path.delete();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public interface TcpCallback {
        void RspLog(int RspLog);
    }
}
