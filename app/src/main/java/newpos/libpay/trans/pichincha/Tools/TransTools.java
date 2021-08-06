package newpos.libpay.trans.pichincha.Tools;

import cnb.pichincha.wposs.mivecino_pichincha.constructor.Utils;
import newpos.libpay.trans.pichincha.Administrativas.InitTrans;
import newpos.libpay.utils.ISOUtil;

/**
 * Created by Jhon Morantes on 16/4/2018.
 */

public class TransTools {

    public static final String ACQUIRER_ID = "0010";
    public static final String CURRENCY_CODE = "840";

    public static String changeFormatAmnt(String amntServiceCost) {

        String cuttedStr = amntServiceCost;
        for (int i = amntServiceCost.length() - 1; i >= 0; i--) {
            char c = amntServiceCost.charAt(i);
            if ('.' == c) {
                cuttedStr = amntServiceCost.substring(0, i) + amntServiceCost.substring(i + 1);
                break;
            }
        }
        int NUM = cuttedStr.length();
        int zeroIndex = -1;
        for (int i = 0; i < NUM - 2; i++) {
            char c = cuttedStr.charAt(i);
            if (c != '0') {
                zeroIndex = i;
                break;
            } else if (i == NUM - 3) {
                zeroIndex = i;
                break;
            }
        }
        if (zeroIndex != -1) {
            cuttedStr = cuttedStr.substring(zeroIndex);
        }
        if (cuttedStr.length() < 3) {
            cuttedStr = "0" + cuttedStr;
        }
        cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2)
                + "." + cuttedStr.substring(cuttedStr.length() - 2);

        return cuttedStr;
    }

    // Cambiar ValidaCedulaRuc por validadorDeCedula
    public static boolean ValidaCedulaRuc(String x) {
        int suma = 0;
        if (x.length() == 9) {
            return false;
        } else {
            int a[] = new int[x.length() / 2];
            int b[] = new int[(x.length() / 2)];
            int c = 0;
            int d = 1;
            for (int i = 0; i < x.length() / 2; i++) {
                a[i] = Integer.parseInt(String.valueOf(x.charAt(c)));
                c = c + 2;
                if (i < (x.length() / 2) - 1) {
                    b[i] = Integer.parseInt(String.valueOf(x.charAt(d)));
                    d = d + 2;
                }
            }

            for (int i = 0; i < a.length; i++) {
                a[i] = a[i] * 2;
                if (a[i] > 9) {
                    a[i] = a[i] - 9;
                }
                suma = suma + a[i] + b[i];
            }
            int aux = suma / 10;
            int dec = (aux + 1) * 10;
            if ((dec - suma) == Integer.parseInt(String.valueOf(x.charAt(x.length() - 1))))
                return true;
            else if (suma % 10 == 0 && x.charAt(x.length() - 1) == '0') {
                return true;
            } else {
                return false;
            }
        }
    }

    // Cambiar validadorDeCedula por ValidaCedulaRuc
    public static boolean validadorDeCedula( String aCedulaRuc )
    {
        int[] d = new int[10];
        int i, res;
        int suma = 0;
        int imp = 0;
        int par = 0;
        int checkDigit;
        int MODULO;
        int sustraendo;
        int[] coeficientesJE = new int[]{4,3,2,7,6,5,4,3,2}; // Coeficientes Juridicos y Extranjero
        int[] coeficientesP = new int[]{3,2,7,6,5,4,3,2}; // Coeficientes Empresas Publicas


        int prov1 = Integer.parseInt(aCedulaRuc.substring(0,2));

        if ( !((prov1 > 0) && (prov1 <= 24)))  // 24 = Numero de provincias
            return false;

        /* Verifica si el ultimo digito de la cedula es valido */

        // copiamos la cedula a un array int
        for (i = 0; i < 10; i++)
            d[i] = aCedulaRuc.charAt(i) - 48;

        if( d[2] == 7 || d[2] == 8 )
            return false;

        if (d[2] < 6 )  // RUC Persona Natural - (Cedula) - Digito de Chequeo en Pos 10
        {
            MODULO = 10;
            // Se suman los duplos de posicion impar
            for (i = 0; i < 10; i += 2)
            {
                d[i] = ((d[i] * 2) > 9) ? ((d[i] * 2) - 9 ) : (d[i] * 2);
                imp += d[i];
            }

            // Se suman los digitos de la posicion par
            for (i = 1; i < 9; i += 2)
                par += d[i];

            suma = imp + par;

            // Se obtiene la decena superior

            String tmpD101 = String.valueOf(suma+10);
            char[] tempCharArray = tmpD101.toCharArray();
            tempCharArray[1] = '0';
            tmpD101 = String.valueOf(tempCharArray);

            checkDigit = Integer.parseInt(tmpD101) - suma;

            checkDigit = (checkDigit == 10) ? 0 : checkDigit;
            // Si el digito10 calculado es igual al digito 10 de la cedula, es correcta!
            return (checkDigit == d[9]);

        }
        else if (d[2] == 6 ) // RUC Empresas Publicas - Digito de Chequeo en Pos 9
        {
            MODULO = 11;
            for (i = 0; i < 8; i++)
            {
                d[i] = d[i] * coeficientesP[i];
                suma += d[i];
            }
            res = suma % MODULO;
            checkDigit = (res == 0) ? 0 : (MODULO - res);
            // Si el digito 10 calculado es igual al digito 9 de la cedula, es correcta!
            return (checkDigit == d[8]);
        }
        else if (d[2] == 9)
        {
            MODULO = 11;
            for (i = 0; i < 9; i++) // RUC Juridica / Extrajera - Digito de Chequeo en Pos 10
            {
                d[i] = d[i] * coeficientesJE[i];
                suma += d[i];
            }
            res = suma % MODULO;
            checkDigit = (res == 0) ? 0 : (MODULO - res);
            checkDigit = (checkDigit == 10) ? 0 : checkDigit;
            // Si el digito 10 calculado es igual al digito 10 de la cedula, es correcta!
            return(checkDigit == d[9]);
        }

        return false;
    }


    public static int validateAmountRetiro(long Amnt) {
        int ret = 0;
        long minAmnt = Long.parseLong(ISOUtil.bcd2str(InitTrans.tkn80.getValMinRetiro(), 0, InitTrans.tkn80.getValMinRetiro().length));
        long maxAmnt = Long.parseLong(ISOUtil.bcd2str(InitTrans.tkn80.getValMaxRetiro(), 0, InitTrans.tkn80.getValMaxRetiro().length));


        if (Amnt < minAmnt) {
            ret = 1;
        }

        if (Amnt > maxAmnt) {
            ret = 2;
        }

        return ret;
    }

    public static int validateAmountDeposito(long Amnt) {
        int ret = 0;
        long minAmnt = Long.parseLong(ISOUtil.bcd2str(InitTrans.tkn80.getValMinDeposito(), 0, InitTrans.tkn80.getValMinDeposito().length));
        long maxAmnt = Long.parseLong(ISOUtil.bcd2str(InitTrans.tkn80.getValMaxDeposito(), 0, InitTrans.tkn80.getValMaxDeposito().length));

        if (Amnt < minAmnt) {
            ret = 1;
        }

        if (Amnt > maxAmnt) {
            ret = 2;
        }

        return ret;
    }
}