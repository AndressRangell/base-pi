package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import java.util.ArrayList;
import java.util.List;
import newpos.libpay.utils.ISOUtil;

public class Tkn08 {
    private int cantidadItems;
    private String[] listaItems;
    private int indice;
    List<String> list ;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String[] getListaItems() {
        return listaItems;
    }

    public void setListaItems(String[] listaItems) {
        this.listaItems = listaItems;
        this.cantidadItems = listaItems.length;
    }

    public int getCantidadItems() {
        return cantidadItems;
    }

    public void setCantidadItems(int cantidadItems) {
        this.cantidadItems = cantidadItems;
    }


    /*
    cuerpoToken : bufer con informacion de longitud de token + contenido del token
    Retorna : cantidad de bytes procesados de @cuerpoToken
     */
    public int unpackToken(byte[] cuerpoToken, int aIndice) {
        int indice = aIndice;
        int lenToken;

        try{
            if(cuerpoToken != null){

                lenToken = ISOUtil.bcd2int(cuerpoToken, indice,2);
                indice += 2;

                this.cantidadItems = ISOUtil.bcd2int(cuerpoToken, indice,1);
                indice ++; // Cantidad items
                indice ++; //  | separador

                byte[] byteTkn = new byte[lenToken];
                int len2Copy = lenToken - 2; // 3 : 2 de cant items + 1 de separador

                System.arraycopy(cuerpoToken, indice, byteTkn, 0, len2Copy);
                indice += len2Copy; // Cantidad items

                String hexList = ISOUtil.byte2hex(byteTkn, 0, len2Copy);
                String strItems =  ISOUtil.stringToHex(hexList).toString();
                this.listaItems = strItems.split("@");



            }
        }catch (Exception e){
            e.printStackTrace();
            //InitTrans.wrlg.wrDataTxt("Error en guardarImpresionTxt() - " + nomarchivo + " - error " + e.toString());
        }

        return indice - aIndice;
    }


    public List<String> unpackImpresion(String getfield, boolean reimpresion) {
        String[] datos = getfield.split("\n");
        int i = 0;

        list = new ArrayList<>();
        if (reimpresion){
            list.add(datos[0]);
            i++;
        }

        list.add("         BANCO PICHINCHA C.A         ");
        list.add("          RUC: 1790010937001          ");
        list.add("      COMPOBANTE DE RECAUDACIONES      ");

        try{
            list.add(datos[1 + i]);
            list.add("Empresa.........: "+datos[2 + i]);
            list.add("Cliente.........: "+datos[3 + i]);
            list.add("Contrapartida...: "+datos[4 + i]);
            list.add("Documento(sec)..: "+datos[5 + i]);
            list.add("");
            list.add("Valor...........: "+datos[6 + i]);
            list.add("Tarifa por serv.: "+datos[7 + i]);
            list.add("Descuento.......: "+datos[8 + i]);
            list.add("Débito..........: "+datos[9 + i]);
            list.add("TOTAL...........: "+datos[10 + i]);
            list.add("Moneda..........: "+datos[11 + i]);
            list.add("");
            list.add("Oficina.........: "+datos[12 + i]);
            list.add("CNB.............: "+datos[13 + i]);
            list.add("Fecha...........: "+datos[14 + i]);
            list.add("");
            list.add("Control(journal): "+ datos[15 + i]);
            list.add("Forma de Pago...: "+ datos[16 + i]);
            list.add("Autorización....: "+ datos[17 + i]);
            list.add("Seq unico UPP...: "+ datos[18 + i]);

        }catch (Exception e){
        }
        list.add("");
        list.add("   EL CLIENTE O DEPOSITANTE, SEGÚN   \n"+
                "CORRESPONDA, DECLARA QUE LOS FONDOS DE\n"+
                "   ESTA TRANSACCIÓN SON LÍCITOS, NO   \n"+
                "  PROVIENEN DE/NI SERÁN DESTINADOS A  \n"+
                "NINGUNA ACTIVIDAD ILEGAL O DELICTIVA.\n"+
                "   EL CLIENTE NO CONSENTIRÁ QUE SE   \n"+
                "EFECTÚEN DEPÓSITOS O TRANSFERENCIAS A\n"+
                "   SU CUENTA, PROVENIENTES DE ESTAS   \n"+
                "ACTIVIDADES. EXPRESAMENTE AUTORIZA AL\n"+
                "  BANCO PICHINCHA C.A. REALIZAR LAS  \n"+
                " VERIFICACIONES Y DEBIDAS DILIGENCIAS \n"+
                "CORRESPONDIENTES E INFORMAR DE MANERA\n"+
                "INMEDIATA Y DOCUMENTADA A LA AUTORIDAD\n"+
                "COMPETENTE EN CASOS DE INVESTIGACIÓN O\n"+
                "  CUANDO SE DETECTAREN TRANSACCIONES  \n"+
                "INUSUALES E INJUSTIFICADAS. POR LO QUE\n"+
                "  NO EJERCERÁ NINGÚN RECLAMO O ACCIÓN \n"+
                "               JUDICIAL.              ");
        list.add("");
        list.add("         **GUARDE SU RECIBO**         ");
        list.add("Cuidar la clave es su responsabilidad");
        list.add("               ORIGINAL               ");
        list.add("--------------------------------------");
        //InitTrans.tkn08.setList(list);
        return list;
    }
}
