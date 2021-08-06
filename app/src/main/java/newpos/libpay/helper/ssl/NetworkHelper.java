package newpos.libpay.helper.ssl;

import android.content.Context;
import android.util.Log;

import newpos.libpay.trans.Tcode;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

/**
 * 网络助手类
 * @author zhouqiang
 */
public class NetworkHelper {

	private Socket socket;//SSLSocket对象
	private InputStream is; // 输入流
	private OutputStream os; // 输出流
	private String ip;//连接IP地址
	private int port;//连接端口号
	private Context tcontext ;//上下文对象
	private int timeout ; //超时时间
	private int protocol; // Protocolo 0: 2 bytes de longitud + datos 1: protocolo stx
	private final String CLIENT_KEY_MANAGER = "X509"; // 密钥管理器
	private final String CLIENT_AGREEMENT = "TLSv1.2"; // 使用协议
	private final String CLIENT_KEY_KEYSTORE = "BKS"; // "JKS";//密库，这里用的是BouncyCastle密库
	private final String CLIENT_KEY_PASSWORD = "123456";// 密码

	/**
	 * @param ip 初始化连接的IP
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public NetworkHelper(String ip, int port, int timeout, Context context) {
		this.ip = ip;
		this.port = port;
		this.timeout = timeout;
		this.tcontext = context;
	}

	/**
	 * 连接socket
	 * @return
	 * @throws IOException
	 */
	public int Connect(int timeout) {
		InitTrans.wrlg.wrDataTxt("Inicio Connect() " + ip + ":" + port + " - NetworkHelper" + "Tiempo..." + timeout);
		try {
			socket = new Socket();
			socket.setSoTimeout(timeout);
			socket.connect(new InetSocketAddress(ip, port), timeout);
			is = socket.getInputStream();
			os = socket.getOutputStream();
//				SSLFactory sslFactory  = new SSLFactory(tcontext);;
//				socket = (SSLSocket) sslFactory.createSocket();
//				// socket = (SSLSocket) sslFactory.createSocket(ip, port);
//				socket.setSoTimeout(timeout);
//				socket.connect(new InetSocketAddress(ip, port), 20000);
//				is = socket.getInputStream();
//				os = socket.getOutputStream();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		InitTrans.wrlg.wrDataTxt("Fin exitoso Connect() - NetworkHelper" + "Tiempo..." + timeout);
		return 0;
	}

	/**
	 * 关闭socket
	 */
	public int close() {
		try {
			socket.close();
		} catch (IOException e) {
			InitTrans.wrlg.wrDataTxt("Error al cerrar Close() - NetworkHelper");
			return -1;
		}
		InitTrans.wrlg.wrDataTxt("Fin exitoso Close() - NetworkHelper");
		return 0;
	}

	/**
	 * 发送数据包
	 * @param data
	 * @return
	 */
	public int Send(byte[] data) {
		InitTrans.wrlg.wrDataTxt("Inicio de Send() - NetworkHelper");
		byte[] newData = null;
		if (protocol == 0) {
			newData = new byte[data.length + 2];
			newData[0] = (byte) (data.length >> 8);
			newData[1] = (byte) data.length;// 丢失高位
			System.arraycopy(data, 0, newData, 2, data.length);
			ISOUtil.grabar(newData);
		}
		try {
			InitTrans.wrlg.wrDataTxt("Enviando data send() - NetworkHelper");
			os.write(newData);
			os.flush();
		} catch (IOException e) {
			InitTrans.wrlg.wrDataTxt("Error IOException en Send() - NetworkHelper");
			return Tcode.T_send_err;
		}
		InitTrans.wrlg.wrDataTxt("Fin exitoso Send() - NetworkHelper");
		return 0;
	}

	/**
	 * 接受数据包
	 * @return
	 * @throws IOException
	 */
	public byte[] Recive(int max, int timeout) throws IOException {
		InitTrans.wrlg.wrDataTxt("Inicio de Recive() - NetworkHelper ");
		ByteArrayOutputStream byteOs ;
		byte[] resP = null ;
		InitTrans.wrlg.wrDataTxt("Valor del protocol Recive() " + protocol);
		if (protocol == 0) {
			byte[] packLen = new byte[2];
			int len ;
			byte[] bb = new byte[2+max];
			int i ;
			byteOs = new ByteArrayOutputStream();
			try {
				if ((i = is.read(packLen)) != -1) {
					len = ISOUtil.byte2int(packLen);
					InitTrans.wrlg.wrDataTxt("Longitud del mensaje Recive() " + len);
					while (len > 0 && (i = is.read(bb)) != -1) {
						byteOs.write(bb, 0, i);
						len -= i;
					}
				}
			} catch (InterruptedIOException e) {
				// 读取超时处理
				InitTrans.wrlg.wrDataTxt("PAY_SDK - Recive：Lectura de excepción de tiempo de espera de transmisión de datos");
				Log.w("PAY_SDK" , "Recive：Lectura de excepción de tiempo de espera de transmisión de datos");
				return null;
			}
			resP = byteOs.toByteArray();
			ISOUtil.grabar(resP);
			InitTrans.wrlg.wrDataTxt("Fin de Recive() - NetworkHelper");
		}
		return resP;
	}

	public SSLSocket getSSLSocket() throws KeyManagementException,NoSuchAlgorithmException,
            KeyStoreException, CertificateException,IOException, UnrecoverableKeyException {
		SSLContext ctx = SSLContext.getInstance(CLIENT_AGREEMENT);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(CLIENT_KEY_MANAGER);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(CLIENT_KEY_MANAGER);
		KeyStore ks = KeyStore.getInstance(CLIENT_KEY_KEYSTORE);
		KeyStore tks = KeyStore.getInstance(CLIENT_KEY_KEYSTORE);
		ks.load(tcontext.getAssets().open("client.bks"), CLIENT_KEY_PASSWORD.toCharArray());
		tks.load(tcontext.getAssets().open("root.bks"), CLIENT_KEY_PASSWORD.toCharArray());
		kmf.init(ks, CLIENT_KEY_PASSWORD.toCharArray());
		tmf.init(tks);
		ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		return (SSLSocket) ctx.getSocketFactory().createSocket(ip, port);
	}
}
