/*
 * Utils.java
 *
 * Created on 21 de abril de 2008, 06:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cnb.pichincha.wposs.mivecino_pichincha.constructor;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

public class Utils {

    public Utils() {
    }
    
    /** Este metodo convierte un array de Bytes a un objeto tipo String
     * @retorna la cadena tipo String
     */
    public static String uninterpret_ASCII(byte[] rawData, int offset, int length){
        char[] ret = new char[length];
        for (int i = 0; i < length; i++)
        {
            ret[i] = (char)rawData[offset + i];
        }
        return new String(ret);
    }

    /**
     * Asigna al Buffer el caracter especificado en cada una de sus posiciones hasta completar size
     * @param Buffer: buffer de tipo byte que se va a modificar
     * @param caracter: caracter a agregar al buffer
     * @param size: Tama�o que se va copiar
     */
    public static void memSet(byte[] Buffer, byte caracter, int size ) {
        int i;

        for(i=0; i<size; i++){
            Buffer[i] = caracter;
        }
    }

    /*******************************************************************************
     Function        : getDate
     Description     : Recibe la fecha en formato Dia/mes/año (20/01/18)
     Return          : Fecha
     ******************************************************************************/
    public static String getDate(){
        String day, month, year;
        TimeZone tz = TimeZone.getTimeZone("GMT-5");
        Calendar actualDateTime = Calendar.getInstance(tz);

        year= String.valueOf(actualDateTime.get(actualDateTime.YEAR));
        month= String.valueOf(actualDateTime.get(actualDateTime.MONTH) + 1);
        day= String.valueOf(actualDateTime.get(actualDateTime.DAY_OF_MONTH));

        if((actualDateTime.get(actualDateTime.MONTH) + 1) < 10)
            month= "0" + month ;
        if(actualDateTime.get(actualDateTime.DAY_OF_MONTH) < 10)
            day= "0" + day ;
        if(actualDateTime.get(actualDateTime.HOUR_OF_DAY) < 10)

        if(actualDateTime.get(actualDateTime.MINUTE) < 10)

        if(actualDateTime.get(actualDateTime.SECOND) < 10)

        year = year.substring(2,4);

        String dateTime = day + "/" + month + "/" + year;
        return dateTime;
    }

    /*******************************************************************************
     Function        : getDate
     Description     : Recibe la fecha en formato Dia/mes/año (2019/01/18)
     Return          : Fecha
     ******************************************************************************/
    public static String getDateYearMonthDay(){
        String day, month, year;
        TimeZone tz = TimeZone.getTimeZone("GMT-5");
        Calendar actualDateTime = Calendar.getInstance(tz);

        year= String.valueOf(actualDateTime.get(actualDateTime.YEAR));
        month= String.valueOf(actualDateTime.get(actualDateTime.MONTH) + 1);
        day= String.valueOf(actualDateTime.get(actualDateTime.DAY_OF_MONTH));

        if((actualDateTime.get(actualDateTime.MONTH) + 1) < 10)
            month= "0" + month ;
        if(actualDateTime.get(actualDateTime.DAY_OF_MONTH) < 10)
            day= "0" + day ;
        if(actualDateTime.get(actualDateTime.HOUR_OF_DAY) < 10)

            if(actualDateTime.get(actualDateTime.MINUTE) < 10)

                if(actualDateTime.get(actualDateTime.SECOND) < 10)

                    year = year.substring(0,4);

        String dateTime = year + "/" + month + "/" + day;
        return dateTime;
    }

    /*******************************************************************************
     Function        : getTime
     Description     : Recibe la hora en formato HH:MM (Hora:Minuto)
     Return          : Hora
     ******************************************************************************/
    public static String getTime(){

        String time;
        String  hour, minute;
        TimeZone tz = TimeZone.getTimeZone("GMT-5");
        Calendar actualDateTime = Calendar.getInstance(tz);
        hour= String.valueOf(actualDateTime.get(actualDateTime.HOUR_OF_DAY));
        minute= String.valueOf(actualDateTime.get(actualDateTime.MINUTE));

        if(actualDateTime.get(actualDateTime.HOUR_OF_DAY) < 10)
            hour= "0" + hour ;
        if(actualDateTime.get(actualDateTime.MINUTE) < 10)
            minute= "0" + minute ;

        time = hour + ":" + minute;

        return time;
    }

    /*******************************************************************************
     Function        : formatMiles
     Description     : Recibe una cadena y la convierte en formato miles
     Input           : cadena = Cadena a convertir
     Return          : cadena formateada
     ******************************************************************************/
    public static String formatMiles(String cadena){
        int i, k = 0;
        int j = 0;
        int tam;

        tam = cadena.length();

        byte[] cadena_orig = cadena.getBytes();
        byte[] cad_destino = new byte[50];

        i=tam/3;
        if(i*3==tam) i--;
	k=0;

	for(j=tam-1; j>=0; j--){
            cad_destino[j+i]=cadena_orig[j];
            k++;
            if( (k/3)*3==k ){
                i--;

                if( (j+i) > 0 )
                    cad_destino[j+i]='.';
            }
	}

        return uninterpret_ASCII(cad_destino, 0, cad_destino.length ) ;
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':')<0;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%');
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { }
        return "";
    }

    /** Función que elimina acentos y caracteres especiales de
     * una cadena de texto.
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public static String removeSpecial(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùüñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }
        return output;
    }
}
