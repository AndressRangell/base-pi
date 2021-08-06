package newpos.libpay.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import newpos.libpay.global.TMConstants;
import com.pos.device.SDKException;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

/**
 * Created by zhouqiang on 2017/6/30.
 * @author zhouqiang
 * 支付相关工具类
 */

public class PAYUtils {

    /**
     * 发卡脚本结果上送 55域
     */
    public static final int wISR_tags[] = { 0x9F33, // Terminal Capabilities
            0x95,
            0x9F37,
            0x9F1E,
            0x9F10,
            0x9F26,
            0x9F36,
            0x82,
            0xDF31,
            0x9F1A,
            0x9A,
            0 };

    /**
     * 消费 55域数据
     */
    public static final int wOnlineTags[] = {

            0x5F2A,
            0x5F34,
            0x82,
            0x95,
            0x9A,
            0x9C,
            0x9F02,
            0x9F06,
            0x9F09,
            0x9F10,
            0x9F1A,
            0x9F1E,
            0x9F26,
            0x9F27,
            0x9F33,
            0x9F34,
            0x9F35,
            0x9F36,
            0x9F37,
            0 };


    /** 冲正 **/
    public static final int reversal_tag[] = { 0x95,
            0x9F1E,
            0x9F10,
            0x9F36,
            0xDF31,
            0 };

    /**
      * Convert objects and binary file types to each other
      * File serialization into Object
      * @param fileName file absolute path
      * @return Object
      */
    public static Object file2Object(String fileName) throws IOException,ClassNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis) ;
        Object object = ois.readObject();
        if (fis != null) {
            fis.close();
        }
        if (ois != null) {
            ois.close();
        }
        return object;
    }

    /**
     * 将对象和二进制文件类型相互转换
     * 把Object输出到文件
     * @param obj 可序列化的对象
     * @param outputFile 保存的文件
     */
    public static void object2File(Object obj, String outputFile) throws IOException {
        File dir = new File(outputFile);
        if (!dir.exists()) {
              dir.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(dir) ;
        ObjectOutputStream oos = new ObjectOutputStream(fos) ;

        oos.writeObject(obj);
        oos.flush();
        fos.getFD().sync();
        if (oos != null) {
            oos.close();
        }
        if (fos != null) {
            fos.close();
        }
    }

    /**
     * 获取Assets配置信息
     * @param context 上下问对象
     * @param fildName 配置文件名
     * @return Properties
     */
    public static Properties lodeConfig(Context context, String fildName) {
        Properties prop = new Properties();
        try {
            prop.load(context.getResources().getAssets().open(fildName));
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
        return prop;
    }

    /**
     * Get values based on configuration file name and configuration properties
      * @param context context object
      * @param fildName configuration file name
      * @param name configured attribute name
      * @return The String corresponding to the corresponding attribute of the file
     */
    public static String lodeConfig(Context context, String fildName, String name) {
        Properties pro = new Properties();
        try {
            pro.load(context.getResources().getAssets().open(fildName));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }
        return (String) pro.get(name);
    }




    /**
     * 将assets下文件拷贝至本应用程序data下
     * @param context 上下文对象
     * @param fileName assets文件名称
     * @return 文件拷贝结果
     */
    public static boolean copyAssetsToData(Context context , String fileName) {
        try {
            AssetManager as = context.getAssets();
            InputStream ins = as.open(fileName);
            String dstFilePath = context.getFilesDir().getAbsolutePath() + "/" + fileName;
            OutputStream outs = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            byte[] data = new byte[1 << 20];
            int length = ins.read(data);
            outs.write(data, 0, length);
            ins.close();
            outs.flush();
            outs.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取bundle参数列表
     * @param c 上下文对象
     * @param name Properties文件名
     * @param proName 属性代号
     * @return 属性集合
     */
    public static String[] getProps(Context c , String name, String proName) {
        Properties pro = new Properties();
        try {
            pro.load(c.getResources().getAssets().open(name));
        } catch (IOException e) {

            return null;
        }
        String prop = pro.getProperty(proName);
        if (prop == null) {
            return null;
        }
        String[] results = prop.split(",") ;
        for (int i = 0 ; i < results.length ; i++){
            try {
                results[i] = new String(results[i].trim().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return results;
    }

    /**
     * 读取Assets文件夹中的图片资源
     * @param context 上下文对象
     * @param path 文件路径
     * @return 图片对象
     */
    public static Bitmap getImageFromAssetsFile(Context context, String path) {
        Bitmap image = null ;
        try {
            InputStream is = context.getAssets().open(path);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 根据银行ID获取相应图标
     * @param context
     * @param bankId
     * @return
     */
    public static Bitmap getLogoByBankId(Context context , int bankId){
        return getImageFromAssetsFile(context , TMConstants.BANKID.ASSETS[bankId]);
    }



    /**
     * 获取当前系统时间
     * @return 格式化后的时间
     */
    public static String getSysTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 获取当前系统时分秒
     * @return 时分秒
     */
    public static String getHMS(){
        Calendar calendar = Calendar.getInstance() ;
        return str2int(calendar.get(Calendar.HOUR_OF_DAY))+
                str2int(calendar.get(Calendar.MINUTE))+
                str2int(calendar.get(Calendar.SECOND)) ;
    }



    /**
     * 获取当前系统年月日
     * @return 年月日
     */
    public static String getYMD(){
        Calendar calendar = Calendar.getInstance() ;
        return str2int(calendar.get(Calendar.YEAR))+
                str2int(calendar.get(Calendar.MONTH))+
                str2int(calendar.get(Calendar.SECOND)) ;
    }

    /**
     * 时间日期转换为两位格式
     * @param date 时间日期
     * @return 两位日期或时间
     */
    public static String str2int(int date){
        String temp = String.valueOf(date) ;
        if(temp.length() == 1){
            return "0"+temp ;
        }
        return temp;
    }

    /**
     * 字符串转换成日期
     * @param str //yyyy-MM-dd HH:mm:ss
     * @return date 日期对象
     */
    public static String StrToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = new GregorianCalendar();
        c.setTime(date);

        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        String result = day+"/"+month+"/"+year;

        return result;
    }

    /**
     * 字符串转换成日期
     * @param str //yyyy-MM-dd HH:mm:ss
     * @param formatParse 格式化方式
     * @return
     */
    public static String StrToDate(String str, String formatParse, String formatFormat) {
        String fecha = "";
        SimpleDateFormat parseador = new SimpleDateFormat(formatParse);
        SimpleDateFormat formateador = new SimpleDateFormat(formatFormat);

        Date date = null;
        try {
            date = parseador.parse(str);
            fecha = formateador.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fecha;
    }

    /**
     * 日期转换成字符串
     * @param date 日期对象
     * @return str 格式化方式
     */
    public static String DateToStr(Date date, String formatString) {
        String str = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString);// formatString
            str = format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获取当前年
     * @return 年份的整型
     */
    public static int getYear(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        return year;
    }

    /**
     * 时间格式(交易打单专属打印用)
     * @param date 日期字符串
     * @param time 时间字符串
     * @return  yyyy/dd/MM HH:mm:ss
     */
    public static String printStr(String date, String time){
        String newdate = "";
        if(!isNullWithTrim(date)&&!isNullWithTrim(time)){
            if(time.length() == 5){
                newdate = date.substring(0,4)+"/"+date.substring(4,6)+"/"+date.substring(6,8)+"  "
                        +"0"+time.substring(0,1)+":"+time.substring(1,3) ;
            }else {
                newdate = date.substring(0,4)+"/"+date.substring(4,6)+"/"+date.substring(6,8)+"  "
                        +time.substring(0,2)+":"+time.substring(2,4) ;
            }
            return newdate ;
        }return "    " ;


    }

    /**
     * 字符串非空判断
     * @param str 字符串
     * @return true则为空，false不为空
     */
    public static boolean isNullWithTrim(String str) {
        return str == null || str.trim().equals("")||str.trim().equals("null");
    }

    /**
     * 给字符串加*(安全卡号专用)
     * @param cardNo 卡号
     * @param prefix 保留 前几位
     * @param suffix 保留 后几位
     * @return 加*后的 String
     */
    public static String getSecurityNum(String cardNo, int prefix, int suffix) {
        StringBuffer cardNoBuffer = new StringBuffer();
        int len = prefix + suffix;
        if ( cardNo.length() > len) {
            cardNoBuffer.append(cardNo.substring(0, prefix));
            for (int i = 0; i < cardNo.length() - len; i++) {
                cardNoBuffer.append("*");
            }
            cardNoBuffer.append(cardNo.substring(cardNo.length() - suffix, cardNo.length()));
        }
        return cardNoBuffer.toString();
    }


    /**
     * 金额存的时候*100  所有取的时候要/100   格式转换
     * @param amount
     * @return  0.00格式
     */
    public static String TwoWei(String amount){
        DecimalFormat df = new DecimalFormat("0.00");
        double d = 0;
        if(!isNullWithTrim(amount)) {
            d = Double.parseDouble(amount) / 100;
        }

        String ret = df.format(d);

        if(ret.contains(",")){
           ret = ret.replace(",",".");
        }

        return ret;
    }

    /**
     * 时间日期转为标准格式
     * @param date   20160607152954
     * @param oldPattern  yyyyMMddHHmmss
     * @param newPattern yyyy-MM-dd HH:mm:ss
     * @return 2016-06-07 15:29:54
     */
    public static String StringPattern(String date, String oldPattern,
                                       String newPattern) {
        if (date == null || oldPattern == null || newPattern == null) {
            return "";
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat(oldPattern); // 实例化模板对象
        SimpleDateFormat sdf2 = new SimpleDateFormat(newPattern); // 实例化模板对象
        Date d = null;
        try {
            d = sdf1.parse(date); // 将给定的字符串中的日期提取出来
        } catch (Exception e) { // 如果提供的字符串格式有错误，则进行异常处理
            e.printStackTrace(); // 打印异常信息
        }
        return sdf2.format(d);
    }

    /**
     * 字符串对象转换为整型数据
     * @param obj 对象
     * @return 整型数值
     */
    public static int Object2Int(Object obj) {
        return Integer.parseInt((String) obj);
    }



    /**
     * 将long型数据转成金额字符串
     * @param Amount long 型数据
     * @return 金额字符串
     */
    public static String getStrAmount(long Amount) {
        double f1 = Double.valueOf(Amount + "");
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(f1 / 100);
    }


    public static int get_tlv_data(byte[] src, int totalLen, int tag,
                                   byte[] value, boolean withTL) {
        int i, Tag, Len;
        int T;

        if (totalLen == 0) {
            return 0;
        }

        i = 0;
        while (i < totalLen) {
            T = i;

            if ((src[i] & 0x1f) == 0x1f) {
                Tag = ISOUtil.byte2int(src, i, 2);
                i += 2;
            } else {
                Tag = ISOUtil.byte2int(new byte[] { src[i++] });
            }

            Len = ISOUtil.byte2int(new byte[] { src[i++] });
            if ((Len & (byte) 0x80) != 0) {
                int lenL = Len & 3;
                Len = ISOUtil.byte2int(src, i, lenL);
                i += lenL;
            }
            // 找到
            if (tag == Tag) {
                //包含Tag和Len
                if (withTL) {
                    Len = Len + (i - T);
                    System.arraycopy(src, T, value, 0, Len);
                    return Len;
                //不包含Tag和Len
                } else {
                    System.arraycopy(src, i, value, 0, Len);
                    return Len;
                }
            } else {
                i += Len;
            }
        }
        return 0;
    }

    /**
     * 用标签从内核中获得一个数据
     *
     * @param iTag
     *            TLV数据的标签
     * @param data
     *            返回的数据
     * @return 真实长度
     */
    public static int get_tlv_data_kernal(int iTag, byte[] data) {
        IEMVHandler handler = EMVHandler.getInstance();
        int len = 0;
        byte[] Tag ;
        if (iTag < 0x100) {
            Tag = new byte[1];
            Tag[0] = (byte) iTag;
        } else {
            Tag = new byte[2];
            Tag[0] = (byte) (iTag >> 8);
            Tag[1] = (byte) iTag;
        }
        if (handler.checkDataElement(Tag) == 0) {
            try {
                byte[] result = handler.getDataElement(Tag);
                System.arraycopy(result , 0 , data , 0 , result.length);
                len = result.length ;
            } catch (SDKException e) {
                e.printStackTrace();
            }

        } else if (iTag == 0xDF31) {
            byte[] result = handler.getScriptResult() ;
            if(result!=null){
                System.arraycopy(result , 0 , data , 0 , result.length);
                len = result.length ;
            }
        }
        return len;
    }

    /**
     * 从内核拿一组tag标签的数据，包装成一串tlv
     * @param iTags
     * @param dest
     * @return
     */
    public static int pack_tags(int[] iTags, byte[] dest) {
        int i, iTag_len, len;
        byte[] Tag = new byte[2];
        int offset = 0;
        byte[] ptr = new byte[256];

        i = 0;
        while (iTags[i] != 0) {

            if (iTags[i] < 0x100) {
                iTag_len = 1;
                Tag[0] = (byte) iTags[i];
            } else {
                iTag_len = 2;
                Tag[0] = (byte) (iTags[i] >> 8);
                Tag[1] = (byte) iTags[i];
            }

            len = get_tlv_data_kernal(iTags[i], ptr);
            if (len > 0) {
                System.arraycopy(Tag, 0, dest, offset, iTag_len);// 拷标签
                offset += iTag_len;

                if (len < 128) {
                    dest[offset++] = (byte) len;
                } else if (len < 256) {
                    dest[offset++] = (byte) 0x81;
                    dest[offset++] = (byte) len;
                }

                System.arraycopy(ptr, 0, dest, offset, len);
                offset += len;
            }

            i++;
        }
        return offset;
    }



    /**
     * 取时分秒
     * @return
     */
    public static String getLocalTime() {
        return DateToStr(new Date(), "HHmmss");
    }

    /**
     * 取月天
     * @return
     */
    public static String getLocalDate() {
        return DateToStr(new Date(), "MMdd");
    }

    public static String getLocalDateAAAAMMDD() {
        return DateToStr(new Date(), "yyyyMMdd");
    }

    /**
     * 取年月
     * @return
     */


    public static String getLocalDate2() {
        return DateToStr(new Date(), "yyyy-MM-dd");
    }

    public static String getLocalDateCierre() {
        return DateToStr(new Date(), "yyyyMMddHHmmss");
    }

    public static String getLocalFechaHora() {
        return DateToStr(new Date(), "dd/MM/yyyy - HH:mm:ss");
    }

    public static String getLocalFecha() {
        return DateToStr(new Date(), "dd/MM/yyyy");
    }

    public static String getLocalTime2() {
        return DateToStr(new Date(), "HH:mm:ss");
    }
}
