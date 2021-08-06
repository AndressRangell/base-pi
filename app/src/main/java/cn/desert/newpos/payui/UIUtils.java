package cn.desert.newpos.payui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import cn.desert.newpos.payui.master.ResultControl;
import cnb.pichincha.wposs.mivecino_pichincha.R;

/**
 * @author zhouqiang
 * @email wy1376359644@163.com
 */
public class UIUtils {

    /**
     * 显示交易结果
     * @param activity
     * @param flag
     * @param info
     */
    public static void startResult(AppCompatActivity activity , boolean flag , String info){
        Intent intent = new Intent();
        intent.setClass(activity , ResultControl.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag" , flag);
        bundle.putString("info" , info);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     *
     * @param activity
     * @param cls
     */
    public static void startView(AppCompatActivity activity , Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(activity , cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 自定义提示信息
     * @param activity
     * @param content
     */
    public static void toast(AppCompatActivity activity , boolean flag , int content){
        LayoutInflater inflater_3 = activity.getLayoutInflater();
        View view_3 = inflater_3.inflate(R.layout.app_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout));
        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view_3);
        ((TextView)view_3.findViewById(R.id.toast_tv)).
                setText(activity.getResources().getString(content));
        toast.show();
    }

    /**
     * 自定义提示信息
     * @param activity
     * @param str
     */
    public static void toast(AppCompatActivity activity , boolean flag , String str){
        LayoutInflater inflater_3 = activity.getLayoutInflater();
        View view_3 = inflater_3.inflate(R.layout.app_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout));
        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view_3);
        ((TextView)view_3.findViewById(R.id.toast_tv)).setText(str);
        toast.show();
    }

    /**
     * 自定义提示信息
     * @param activity
     * @param str
     */
    public static void toast(AppCompatActivity activity , int ico , String str, int duration){
        LayoutInflater inflater_3 = activity.getLayoutInflater();
        View view_3 = inflater_3.inflate(R.layout.app_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout));

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.setView(view_3);
        ((TextView)view_3.findViewById(R.id.toast_tv)).setText(str);
        toast.show();
    }

    public static void toast(AppCompatActivity activity , String str, int duration){
        LayoutInflater inflater_3 = activity.getLayoutInflater();
        Timer timer = new Timer();
        View view_3 = inflater_3.inflate(R.layout.app_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout));
        final Toast toast1 = new Toast(activity);
        toast1.setGravity(Gravity.CENTER, 0, 0);
        toast1.setDuration(duration);
        toast1.setView(view_3);
        ((TextView)view_3.findViewById(R.id.toast_tv)).setText(str);
        toast1.show();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast1.cancel();
            }
        } , duration);
    }

    public static Dialog centerDialog(Context mContext , int resID , int root){
        final Dialog pd = new Dialog(mContext, R.style.Translucent_Dialog);
        pd.setContentView(resID);
        LinearLayout layout = (LinearLayout) pd.findViewById(root);
        layout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.up_down));
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(true);
        pd.show();
        return pd ;
    }




    /**
     * 拷贝assets文件夹至某一目录
     * @param context
     * @param assetDir
     * @param dir
     */
    public static void copyToAssets(Context context, String assetDir, String dir) {

        String[] files;
        try {

            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);

        if (!mWorkingPath.exists()) {

            if (!mWorkingPath.mkdirs()) {

            }
        }

        for (int i = 0; i < files.length; i++) {
            try {
                String fileName = files[i];
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        copyToAssets(context, fileName, dir + fileName + "/");
                    } else {
                        copyToAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                    continue;
                }

                InputStream in = null;
                try {
                    if (0 != assetDir.length()) {
                        in = context.getAssets().open(assetDir + "/" + fileName);
                    }else {
                        in = context.getAssets().open(fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (0 == assetDir.length()) {
                        copyToAssets(context, fileName, dir + fileName + "/");
                    } else {
                        copyToAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                    continue;
                }

                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists()) {
                    outFile.delete();
                }
                FileOutputStream out = new FileOutputStream(outFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                out.flush();
                out.getFD().sync();
                out.close();
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @description 从SD卡上加载图片
     *
     * @param pathName
     * @param reqWidth 单位：px
     * @param reqHeight 单位：px
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathName,
                                                     int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
    }

    /**
     * @description 计算图片的压缩比率
     *
     * @param options
     *            参数
     * @param reqWidth
     *            目标的宽度
     * @param reqHeight
     *            目标的高度
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * @description 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     *
     * @param src
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth,
                                            int dstHeight, int inSampleSize) {
        if (inSampleSize == 1) {
            return src;
        }

        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) {
            src.recycle();
        }
        return dst;
    }




    /**
     * 根据资源ID获取资源字符串
     * @param context 上下文对象
     * @param resid 资源ID
     * @return
     */
    public static String getStringByInt(Context context, int resid) {
        String sAgeFormat1 = context.getResources().getString(resid);
        return sAgeFormat1;
    }




    public static String versionApk(Context context) {
        String version=null;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            return version;
        }
    }




    public static String getAppTimeStamp(Context context) {
        String timeStamp = "";
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            String appFile = appInfo.sourceDir;
            long time = new File(appFile).lastModified();

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            timeStamp = formatter.format(time);

        } catch (Exception e) {

        }
        return timeStamp;

    }


    public static void validacionCaracterEspecial(EditText editText) {
//        final String charactersForbiden = ",+-*/.|°!@#$%&/()=?¡'¿´+¨*{}[]-_.:,;^~"; //*Caracter o caracteres no permitidos.

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        editText.setFilters(new InputFilter[]{filter});
    }
}
