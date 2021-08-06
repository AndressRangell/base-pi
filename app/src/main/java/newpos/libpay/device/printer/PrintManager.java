package newpos.libpay.device.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.Utils;
import newpos.libpay.global.TMConfig;
import newpos.libpay.presenter.TransUI;
import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.trans.translog.TransLog;
import newpos.libpay.trans.translog.TransLogData;
import newpos.libpay.utils.ISOUtil;
import newpos.libpay.utils.PAYUtils;
import com.pos.device.config.DevConfig;
import com.pos.device.printer.PrintCanvas;
import com.pos.device.printer.PrintTask;
import com.pos.device.printer.Printer;
import com.pos.device.printer.PrinterCallback;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhouqiang on 2017/3/14.
 * @author zhouqiang
 * 打印管理类
 */
public class PrintManager {

	private static PrintManager mInstance ;
	private static TMConfig cfg ;

	public PrintManager(){}

	private static Context mContext ;
	private static TransUI transUI ;

	public static PrintManager getmInstance(Context c , TransUI tui){
		mContext = c ;
		transUI = tui ;
		if(null == mInstance){
			mInstance = new PrintManager();
		}
		cfg = TMConfig.getInstance();
		return mInstance ;
	}

	public static PrintManager getmInstance(Context c){
		mContext = c ;
		if(null == mInstance){
			mInstance = new PrintManager();
		}
		cfg = TMConfig.getInstance();
		return mInstance ;
	}

	private Printer printer = null ;
	private PrintTask printTask = null ;

	/**
	 * 打印签购单
	 * @param data
	 * @param isRePrint
	 * @return
	 */
	public int print(final TransLogData data, final boolean isRePrint){
		this.printTask = new PrintTask();
		this.printTask.setGray(130);
		int ret = -1;
		if (TransLog.getInstance().getSize() == 0) {
			ret = Tcode.T_print_no_log_err;
		}else {
			printer = Printer.getInstance() ;
			if (printer == null) {
				ret = Tcode.T_sdk_err ;
			}else{
				int num = cfg.getPrinterTickNumber() ;

				Bitmap image = PAYUtils.getLogoByBankId(mContext, cfg.getBankid());
				for (int i = 0; i < num; i++) {

					PrintCanvas canvas = new PrintCanvas() ;
					Paint paint = new Paint() ;
					setFontStyle(paint , 2 , false, mContext);
					canvas.drawText(PrintRes.CH.WANNING , paint);
					printLine(paint , canvas);
					setFontStyle(paint , 1 , true, mContext);
					canvas.drawBitmap(image , paint);
					printLine(paint , canvas);
					setFontStyle(paint , 2 , false, mContext);
					if (i == 0) {
						canvas.drawText(PrintRes.CH.MERCHANT_COPY, paint);
					}else if (i == 1){
						canvas.drawText(PrintRes.CH.CARDHOLDER_COPY , paint);
					}else{
						canvas.drawText(PrintRes.CH.BANK_COPY , paint);
					}
					printLine(paint , canvas);
					setFontStyle(paint , 2 , false, mContext);
					canvas.drawText(PrintRes.CH.MERCHANT_NAME+"\n"+cfg.getMerchName() , paint);
					canvas.drawText(PrintRes.CH.MERCHANT_ID+"\n"+cfg.getMerchID() , paint);
					canvas.drawText(PrintRes.CH.TERNIMAL_ID+"\n"+cfg.getTermID() , paint);
					String operNo = data.getOprNo() < 10 ? "0" + data.getOprNo() : data.getOprNo()+"";
					canvas.drawText(PrintRes.CH.OPERATOR_NO+"    "+operNo, paint);
					printLine(paint , canvas);
					setFontStyle(paint , 2 , false, mContext);
					canvas.drawText(PrintRes.CH.ISSUER, paint);
					canvas.drawText(PrintRes.CH.ACQUIRER, paint);

					setFontStyle(paint , 2 , false, mContext);
					canvas.drawText(PrintRes.CH.TRANS_TYPE , paint);
					setFontStyle(paint , 3 , true, mContext);
					canvas.drawText(formatTranstype(data.getEName()) , paint);
					setFontStyle(paint , 2 , false, mContext);
					if (!PAYUtils.isNullWithTrim(data.getExpDate())){
						canvas.drawText(PrintRes.CH.CARD_EXPDATE+"       " + data.getExpDate() , paint);
					}
					printLine(paint , canvas);
					setFontStyle(paint , 2 , false, mContext);
					if(!PAYUtils.isNullWithTrim(data.getBatchNo())){
						canvas.drawText(PrintRes.CH.BATCH_NO + data.getBatchNo(), paint);
					}
					if(!PAYUtils.isNullWithTrim(data.getTraceNo())){
						canvas.drawText(PrintRes.CH.VOUCHER_NO+data.getTraceNo(), paint);
					}

					setFontStyle(paint , 2 , false, mContext);
					if(!PAYUtils.isNullWithTrim(data.getLocalDate()) && !PAYUtils.isNullWithTrim(data.getLocalTime())){
						String timeStr = PAYUtils.StringPattern(data.getLocalDate() + data.getLocalTime(), "yyyyMMddHHmmss", "yyyy/MM/dd  HH:mm:ss");
						canvas.drawText(PrintRes.CH.DATE_TIME+"\n          " + timeStr, paint);
					}
					if(!PAYUtils.isNullWithTrim(data.getRRN())){
						canvas.drawText(PrintRes.CH.REF_NO+ data.getRRN(), paint);
					}
					canvas.drawText(PrintRes.CH.AMOUNT, paint);
					setFontStyle(paint , 3 , true, mContext);
					canvas.drawText("           "+ PrintRes.CH.RMB+"     "+ PAYUtils.getStrAmount(data.getAmount()) + "" , paint);
					printLine(paint , canvas);
					setFontStyle(paint , 1 , false, mContext);

					if (isRePrint) {
						setFontStyle(paint , 3 , true, mContext);
						canvas.drawText(PrintRes.CH.REPRINT , paint);
					}

					ret = printData(canvas);
				}
				if (printer != null) {
					printer = null;
				}
				if (image != null){
					image.recycle();
				}
			}
		}
		return ret;
	}

	public int printPichincha(String dataPrint, final boolean isRePrint){
		this.printTask = new PrintTask();
		this.printTask.setGray(130);
		int ret = -1;
		printer = Printer.getInstance() ;
		if (printer == null) {
			ret = Tcode.T_sdk_err ;
		}else{

			PrintCanvas canvas = new PrintCanvas() ;
			Paint paint = new Paint() ;
			setFontStyle(paint , 1 , true, mContext);
			canvas.drawText(dataPrint , paint);
			//printLine(paint , canvas);
			//printLine(paint , canvas);
			ret = printData(canvas);
			if (printer != null) {
				printer = null;
			}
		}
		return ret;
	}

	public int printPichincha(String dataPrint){
		this.printTask = new PrintTask();
		this.printTask.setGray(130);
		int ret = -1;
		printer = Printer.getInstance() ;
		if (printer == null) {
			ret = Tcode.T_sdk_err ;
		}else{
			PrintCanvas canvas = new PrintCanvas() ;
			Paint paint = new Paint() ;
			setFontStyle(paint , 1 , true, mContext);
			canvas.drawText(dataPrint , paint);
			//printLine(paint , canvas);
			//printLine(paint , canvas);
			ret = printData2(canvas);
			if (printer != null) {
				printer = null;
			}
		}
		return ret;
	}

	public int printInformes(String dataPrint, int size, String Font, Context context){
		this.printTask = new PrintTask();
		this.printTask.setGray(130);
		int ret = -1;
		Typeface tipoFuente1 = Typeface.createFromAsset(context.getAssets(), Font);
		printer = Printer.getInstance() ;
		if (printer == null) {
			ret = Tcode.T_sdk_err ;
		}else{
			PrintCanvas canvas = new PrintCanvas() ;
			Paint paint = new Paint() ;
			paint.setTypeface(tipoFuente1);
			paint.setTextSize(Float.parseFloat(size+"F"));
			canvas.drawText(dataPrint , paint);
			//printLine(paint , canvas);
			//printLine(paint , canvas);
			ret = printData2(canvas);
			if (printer != null) {
				printer = null;
			}
		}
		return ret;
	}

	public int printReimpresion(String dataPrint, boolean msgReimpresion){
		this.printTask = new PrintTask();
		this.printTask.setGray(130);
		int ret = -1;
		printer = Printer.getInstance() ;
		if (printer == null) {
			ret = Tcode.T_sdk_err ;
		}else{
			PrintCanvas canvas = new PrintCanvas() ;
			Paint paint = new Paint() ;
			setFontStyle(paint , 1 , true, mContext);
			if(msgReimpresion){
				canvas.drawText("        *** REIMPRESION ***", paint);
			}
			//printLine(paint , canvas);
			canvas.drawText(dataPrint , paint);
			//printLine(paint , canvas);
			//printLine(paint , canvas);
			ret = printData2(canvas);
			if (printer != null) {
				printer = null;
			}
		}
		return ret;
	}

	public int reciboUsuarios(String[] mensaje){
		this.printTask = new PrintTask();
		this.printTask.setGray(130);
		int ret = -1;
		String fecha = "FECHA: " + PAYUtils.getLocalDate2() + " HORA: " + PAYUtils.getLocalTime2();
		String sn = "SERIAL: " + DevConfig.getSN() + "-" + DevConfig.getMachine();

		printer = Printer.getInstance() ;
		if (printer == null) {
			ret = Tcode.T_sdk_err ;
		}else{
			PrintCanvas canvas = new PrintCanvas() ;
			Paint paint = new Paint() ;
			setFontStyle(paint , 2 , true, mContext);
			canvas.drawText("     REPORTE DE USUARIOS" , paint);
			setFontStyle(paint , 1 , true, mContext);
			canvas.drawText(fecha,paint);
			canvas.drawText(sn,paint);
			printLine(paint , canvas);
			for (int i=0; i<(mensaje.length); i++){
				setFontStyle(paint , 1, false, mContext);
				canvas.drawText(mensaje[i], paint);
				printLine(paint , canvas);
			}

			ret = printData2(canvas);

			if (ret == -3) {
				Toast.makeText(mContext, "¡La impresora no tiene papel!, Intenta de nuevo.", Toast.LENGTH_SHORT).show();
			}
			if (printer != null) {
				printer = null;
			}
		}
		//}
		return ret;
	}

	public int reciboLimites(){
		this.printTask = new PrintTask();
		this.printTask.setGray(130);
		int ret = -1;
		String fecha = "Fecha: " + PAYUtils.getLocalDate2();
		String hora = "Hora: " + PAYUtils.getLocalTime2();
		String minimoRetiro = ISOUtil.bcd2str(InitTrans.tkn80.getValMinRetiro(),0,
				InitTrans.tkn80.getValMinRetiro().length);
		int converMinimoRetiro = Integer.parseInt(minimoRetiro);

		String costoRetiro = ISOUtil.bcd2str(InitTrans.tkn80.getCostoServicio(),0,
				InitTrans.tkn80.getCostoServicio().length);
		Double converCostoRetiro = Double.parseDouble(costoRetiro);
		DecimalFormat formatMinimoRetiro = new DecimalFormat("00,00");
		String finalMinimoRetiro = String.valueOf(formatMinimoRetiro.format(converCostoRetiro));

		String minimoDeposito = ISOUtil.bcd2str(InitTrans.tkn80.getValMinDeposito(),0,
				InitTrans.tkn80.getValMinDeposito().length);
		Double converMinimoDeposito = Double.parseDouble(minimoDeposito);
		DecimalFormat formatMinimoDeposito = new DecimalFormat("00,00");
		String finalMinimoDeposito = String.valueOf(formatMinimoDeposito.format(converMinimoDeposito));


		printer = Printer.getInstance() ;
		if (printer == null) {
			ret = Tcode.T_sdk_err ;
		}else{
			PrintCanvas canvas = new PrintCanvas() ;
			Paint paint = new Paint() ;
			setFontStyle(paint , 2 , false, mContext);
			canvas.drawText("Informe de límite de operaciones" , paint);
			setFontStyle(paint , 2 , false, mContext);
			canvas.drawText(fecha,paint);
			canvas.drawText(hora,paint);
			printLine(paint , canvas);
			printLine(paint , canvas);
			setFontStyle(paint , 2, false, mContext);
			canvas.drawText("            Retiro", paint);
			canvas.drawText("Límite mínimo:  US$  " + converMinimoRetiro, paint);
			canvas.drawText("Costo de servicio:  US$  " + finalMinimoRetiro, paint);
			canvas.drawText(" ", paint);
			canvas.drawText("           Depósito", paint);
			canvas.drawText("Límite mínimo  US$  " + finalMinimoDeposito, paint);
			printLine(paint , canvas);
			printLine(paint , canvas);

			ret = printData2(canvas);

			if (ret == -3) {
				Toast.makeText(mContext, "¡La impresora no tiene papel!, Intenta de nuevo.", Toast.LENGTH_SHORT).show();
			}
			if (printer != null) {
				printer = null;
			}
		}

		return ret;
	}

	public int imprimirParametros(String[] mensaje){
		this.printTask = new PrintTask();
		this.printTask.setGray(130);
		int ret = -1;
		String fecha = "FECHA: " + PAYUtils.getLocalDate2() + "  - " + " HORA: " + PAYUtils.getLocalTime2();
		String sn = "SERIAL: " + DevConfig.getSN() + " - " + DevConfig.getMachine();
		cfg = TMConfig.getInstance();




		String tituloConexion 	= "           |  DIRECCION IP   | PUERTO ";
	  	String IP 				= " IP ACTUAL |  " +ISOUtil.padright(Utils.getIPAddress(true),15,' ') + "|  "+ISOUtil.padright("----",6,' ')//+"\n"
								+ "  PRIMARIA |  " +ISOUtil.padright(cfg.getIp(),15,' ')+ "|  "+ISOUtil.padright(cfg.getPort(),6,' ')//+ " "
								+ "SECUNDARIA |  " +ISOUtil.padright(cfg.getIP2(),15,' ')+ "|  "+ISOUtil.padright(cfg.getPort2(),6,' ');


		String version ="VERSION: "+ UIUtils.versionApk(mContext) + "\n"+"FECHA: "+ UIUtils.getAppTimeStamp(mContext);
		printer = Printer.getInstance() ;
		if (printer == null) {
			ret = Tcode.T_sdk_err ;
		}else{
			PrintCanvas canvas = new PrintCanvas() ;
			Paint paint = new Paint() ;
			setFontStyle(paint , 2 , true, mContext);
			canvas.drawText("           PARAMETROS           " , paint);
			printSpace(paint,canvas);
			setFontStyle(paint , 1 , true, mContext);
			canvas.drawText(fecha,paint);
			canvas.drawText(sn,paint);
			printLine(paint , canvas);
			setFontStyle(paint , 1 , true, mContext);
			canvas.drawText(version,paint);
			printLine(paint , canvas);
			setFontStyle(paint , 1 , true, mContext);
			canvas.drawText(tituloConexion,paint);
			canvas.drawText(IP,paint);
			printLine(paint , canvas);
			for (int i=0; i<(mensaje.length); i++){
				setFontStyle(paint , 1, true, mContext);
				canvas.drawText(mensaje[i], paint);
				printLine(paint , canvas);
			}

			ret = printData2(canvas);
			if (ret == -3) {
				Toast.makeText(mContext, "¡La impresora no tiene papel!, Intenta de nuevo.", Toast.LENGTH_SHORT).show();
			}
			if (printer != null) {
				printer = null;
			}
		}

		return ret;
	}

	/**
	 * 打印交易明细
	 * @return
	 */
	public int printDetails(){
		this.printTask = new PrintTask();
		this.printTask.setGray(130);
		int ret = -1;
		if (TransLog.getInstance().getSize() == 0) {
			ret = Tcode.T_print_no_log_err;
		}else {
			printer = Printer.getInstance() ;
			if (printer == null) {
				ret = Tcode.T_sdk_err ;
			}else{
				PrintCanvas canvas = new PrintCanvas() ;
				Paint paint = new Paint() ;
				setFontStyle(paint , 2 , true, mContext);
				canvas.drawText(PrintRes.CH.WANNING , paint);
				printLine(paint , canvas);
				setFontStyle(paint , 3 , true, mContext);
				canvas.drawText("                     "+ PrintRes.CH.DETAILS , paint);
				setFontStyle(paint , 2 , true, mContext);
				canvas.drawText(PrintRes.CH.MERCHANT_NAME+"\n"+cfg.getMerchName() , paint);
				canvas.drawText(PrintRes.CH.MERCHANT_ID+"\n"+cfg.getMerchID() , paint);
				canvas.drawText(PrintRes.CH.TERNIMAL_ID+"\n"+cfg.getTermID() , paint);
				canvas.drawText(PrintRes.CH.BATCH_NO+"\n"+cfg.getBatchNo() , paint);
				canvas.drawText(PrintRes.CH.DATE_TIME+"\n"+PAYUtils.getSysTime(), paint);
				printLine(paint , canvas);
				int num = TransLog.getInstance().getSize() ;
				for (int i = 0 ; i < num ; i++){
					setFontStyle(paint , 1 , true, mContext);
					TransLogData data = TransLog.getInstance().get(i);

					canvas.drawText(PrintRes.CH.TRANS_TYPE+formatTranstype(data.getEName()) , paint);
					canvas.drawText(PrintRes.CH.AMOUNT+ PrintRes.CH.RMB+PAYUtils.getStrAmount(data.getAmount()), paint);
					canvas.drawText(PrintRes.CH.VOUCHER_NO+data.getTraceNo(), paint);
					printLine(paint , canvas);
				}
				ret = printData(canvas);
				if (printer != null) {
					printer = null;
				}
			}
		}
		return ret;
	}



	/**
	 * 调用驱动打印
	 * @param pCanvas
	 * @return
	 */
	private int printData(PrintCanvas pCanvas) {
		final CountDownLatch latch = new CountDownLatch(1);
		printer = Printer.getInstance() ;
		int ret = printer.getStatus();
		if(Printer.PRINTER_STATUS_PAPER_LACK == ret){

			transUI.imprimiendo(60*1000 , Tcode.Mensajes.impresoraSinPapel);
			long start = SystemClock.uptimeMillis() ;
			while (true){
				if(SystemClock.uptimeMillis() - start > 60 * 1000){
					ret = Printer.PRINTER_STATUS_PAPER_LACK ;
					break;
				}
				if(printer.getStatus() == Printer.PRINTER_OK){
					ret = Printer.PRINTER_OK ;
					break;
				}else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			}
		}
		if(ret == Printer.PRINTER_OK){
			transUI.imprimiendo(60*1000 , Tcode.Mensajes.imprimiendoRecibo);
			printTask.setPrintCanvas(pCanvas);
			printer.startPrint( printTask , new PrinterCallback() {
				@Override
				public void onResult(int i, PrintTask printTask) {
					latch.countDown();
				}
			});
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return ret ;
	}

	private int printData2(PrintCanvas pCanvas) {
		printer = Printer.getInstance() ;
		int ret = printer.getStatus();

		if(Printer.PRINTER_STATUS_PAPER_LACK == ret){

			long start = SystemClock.uptimeMillis() ;
			while (true){
				if(SystemClock.uptimeMillis() - start > 1 * 1000){
					ret = Printer.PRINTER_STATUS_PAPER_LACK ;
					break;
				}
				if(printer.getStatus() == Printer.PRINTER_OK){
					ret = Printer.PRINTER_OK ;
					break;
				}else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {

					}
				}
			}
		}

		if(ret == Printer.PRINTER_OK){
			printTask.setPrintCanvas(pCanvas);
			printer.startPrint( printTask , new PrinterCallback() {
				@Override
				public void onResult(int i, PrintTask printTask) {
					Toast.makeText(mContext, "¡Impresión realizada con éxito!.", Toast.LENGTH_SHORT).show();
				}
			});
		}
		return ret ;
	}

	/** =======================私有处理方法=====================**/

	private String formatTranstype(String type){
		int index = 0 ;
		for (int i = 0; i < PrintRes.TRANSEN.length ; i++){
			if(PrintRes.TRANSEN[i].equals(type)){
				index = i ;
			}
		}
		if(Locale.getDefault().getLanguage().equals("zh")){
			return PrintRes.TRANSCH[index]+"("+type+")";
		}else {
			return type ;
		}
	}

	/**
	 * 设置打印字体样式
	 * @param paint 画笔
	 * @param size 字体大小 1---small , 2---middle , 3---large
	 * @param isBold 是否加粗
	 * @author zq
	 */
	private void setFontStyle(Paint paint , int size , boolean isBold, Context context){
		//Typeface tipoFuente1 = Typeface.createFromAsset(context.getAssets(), "font/mplus-1m-bold.ttf");
		Typeface tipoFuente1 = Typeface.createFromAsset(context.getAssets(), "font/monofonto.ttf");
		if(isBold) {
			paint.setTypeface(tipoFuente1);
			paint.setFakeBoldText(true);
		}else {
			paint.setTypeface(tipoFuente1);
		}
		switch (size) {
			case 0 : break;
			case 1 : paint.setTextSize(20F) ;break; // 38 caracteres
			case 2 : paint.setTextSize(23F) ;break; //32 Caracteres
			case 3 : paint.setTextSize(30F) ;break; //
			default:break;
		}
	}

	/**
	 * 在画布上画出一条线
	 * @param paint
	 * @param canvas
	 */
	private void printLine(Paint paint , PrintCanvas canvas){
		String line = "--------------------------------------";
		setFontStyle(paint , 1 , true, mContext);
		canvas.drawText(line , paint);
	}

	private void printSpace(Paint paint , PrintCanvas canvas){
		String line = "                                      ";
		setFontStyle(paint , 1 , true, mContext);
		canvas.drawText(line , paint);
	}
}
