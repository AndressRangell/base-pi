package newpos.libpay.device.pinpad;

import android.os.AsyncTask;
import android.util.Log;

import com.pos.device.SDKException;
import com.pos.device.apdu.CommandApdu;
import com.pos.device.apdu.ResponseApdu;
import com.pos.device.icc.ContactCard;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.OperatorMode;
import com.pos.device.icc.SlotType;
import com.pos.device.icc.VCC;

import newpos.libpay.utils.ISOUtil;

/**
 * Created by Acer on 5/02/2018.
 */

public class ProcessMasterKey extends AsyncTask<String, Integer, String> {

    private FileCallback callback;
    private static final String TAG = "IccReader";
    private IccReader iccReader0;
    private String passMasterKEY;

    public ProcessMasterKey(final FileCallback callback, String pass) {
        passMasterKEY = pass;
        this.callback = callback;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        boolean cardPresent;
        String hex1 = null, hex2;
        String rta = "1";

        try {
            while (true) {

                if (isCancelled()) {
                    rta = "2";
                    break;
                }
                iccReader0 = IccReader.getInstance(SlotType.USER_CARD);
                ContactCard contactCardSam = null;
                try {
                    contactCardSam = iccReader0.connectCard(VCC.VOLT_5, OperatorMode.EMV_MODE);
                } catch (SDKException e) {
                    e.printStackTrace();
                }
                if (contactCardSam != null) {

                    byte[] passMK = passMasterKEY.getBytes("US-ASCII");

                    String comando_APDU_Pin = "00200000" + "0" + passMasterKEY.length()
                            + ISOUtil.bcd2str(passMK, 0, passMK.length);

                    byte[] apdu = ISOUtil.str2bcd(comando_APDU_Pin, false);

                    CommandApdu cmdApdu = new CommandApdu(apdu);
                    cardPresent = iccReader0.isCardPresent();

                    if (cardPresent) {

                        try {
                            ResponseApdu rspApdu = iccReader0.transmit(contactCardSam, cmdApdu);
                            String respuestaAPDU = null;
                            if (rspApdu != null) {
                                respuestaAPDU = ISOUtil.bcd2str(rspApdu.getBytes(), 0, rspApdu.getBytes().length * 2, false);
                            }

                            if (!respuestaAPDU.equals("9000")) {
                                rta = "1";
                                break;
                            }
                            byte[] apdu2 = ISOUtil.str2bcd("00B0850010", false);
                            byte[] rspData = iccReader0.transmit(contactCardSam, apdu2);
                            if (rspData != null) {
                                hex1 = ISOUtil.bcd2str(rspData, 0, rspData.length * 2, false);
                            }

                            byte[] apdu3 = ISOUtil.str2bcd("00B0860010", false);
                            byte[] rspData3 = iccReader0.transmit(contactCardSam, apdu3);

                            if (rspData3 != null) {

                                hex2 = ISOUtil.bcd2str(rspData3, 0, rspData3.length * 2, false);

                                String auxMk1 = convertHexToString(hex1);
                                String auxMk2 = convertHexToString(hex2);

                                String finalMk = auxMk1.substring(0, auxMk1.length() - 2) + auxMk2.substring(0, auxMk2.length() - 2);

                                if (finalMk != null) {
                                    rta = finalMk;
                                    break;
                                }
                            }
                            rta = "3";
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    Log.e(TAG, "contactCardSam=null");
                }
            }

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return rta;

    }

    private String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }

        return sb.toString();
    }

    private static byte[] xor(final byte[] input, final byte[] secret) {
        final byte[] output = new byte[input.length];
        if (secret.length == 0) {
            throw new IllegalArgumentException("empty security key");
        }
        int spos = 0;
        for (int pos = 0; pos < input.length; ++pos) {
            output[pos] = (byte) (input[pos] ^ secret[spos]);
            ++spos;
            if (spos >= secret.length) {
                spos = 0;
            }
        }
        return output;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
    }

    @Override
    protected void onPostExecute(String text) {
        super.onPostExecute(text);
        callback.RspUnpack(text);
    }

    public interface FileCallback {
        String RspUnpack(String MK);
    }

}
