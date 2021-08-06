package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn17 {


    private byte[] numItems = new byte[1];
    private byte[] tkn17 = new byte[999];
    public String titulo;
    public String[] items ;
    public String[] codItem;
    public byte [][] valorItem;



    public void clean(){
        numItems = new byte[1];
        tkn17 = new byte[999];
    }
    public byte[] getNumItems() {
        return numItems;
    }

    public void setNumItems(byte[] numItems) {
        this.numItems = numItems;
    }

    /**
     *
     * @param tkn17
     */
    public void setTkn17(byte[] tkn17) {
        int i;
        int j=0;
        this.tkn17 = tkn17;
        String[] mensaje;
        String strMsg;

        strMsg = ISOUtil.bcd2str(tkn17,0, tkn17.length);
        mensaje = ISOUtil.hex2AsciiStr(strMsg).split("[|]|~");
        items= new String [mensaje.length/2];
        codItem= new String [mensaje.length/2];
        titulo=mensaje[0];
        valorItem = new byte [mensaje.length/2][];

        for(i=1; i<mensaje.length-1;i+=2){
            codItem[j]=mensaje[i];
            items[j]= mensaje[i+1];
            valorItem[j] = codItem[j].getBytes();
            j++;
        }
    }

    public void setTkn17(String codProv, String provincia) {
        codItem= new String[1];
        items=new String[1];
        codItem[0]=codProv;
        items[0]=provincia;
        titulo="          PROVINCIAS";
    }

    public void setTkn17(String cod, String data, String titulo, byte[] valor) {
        codItem= new String[1];
        items=new String[1];
        codItem[0]=cod;
        items[0]=data;
        valorItem[0]=valor;
        this.titulo=titulo;
    }


    public String packTkn17(int posicion) {
        String idTkn17 = "17";
        String tkn17 = titulo + "|" + codItem[posicion] + "~" + items[posicion];
        tkn17 = ISOUtil.convertStringToHex(tkn17);

        int lenBuff = ((tkn17.length()/2)+1);
        String len =  ISOUtil.padleft(lenBuff + "", 4, '0');

        return idTkn17 + len + "01" + tkn17;
    }

    public String packTkn17Byte(int posicion) {
        String idTkn17 = "17";
        //String value = ISOUtil.bcd2str(valorItem[posicion], 0, valorItem[posicion].length);
        String tkn17 = titulo + "|" + "~" + items[posicion];
        tkn17 = ISOUtil.convertStringToHex(tkn17);

        int lenBuff = ((tkn17.length()/2)+1);
        String len =  ISOUtil.padleft(lenBuff + "", 4, '0');

        return idTkn17 + len + "01" + tkn17;
    }

   public String packTkn17_bono(String[] datos, String datosNFC){
        String idTkn17 = "17";
        StringBuilder constTkn17 = new StringBuilder();

        constTkn17.append("                BONO|cedula~");
        constTkn17.append(datos[0]);
        constTkn17.append("|codigo~");
        constTkn17.append(datos[1]);
        constTkn17.append("|fecha~");
        constTkn17.append(datos[2]);
        constTkn17.append("|nfc~");
        constTkn17.append(datosNFC);

        String tkn17 = ISOUtil.convertStringToHex(constTkn17.toString());

        int lenBuff = ((tkn17.length()/2)+1);
        String len =  ISOUtil.padleft(lenBuff + "", 4, '0');
        constTkn17.setLength(0);
        return idTkn17 + len + "04" + tkn17;
    }

    public String packTkn17_InitCatalogos(String catalogo) {

        String idTkn17 = "17";
        String len = "0040";
        String tkn17p = "";
        StringBuilder constTkn17 = new StringBuilder();
        if (catalogo.length() == 2) {
            constTkn17.append("                  " + catalogo);
            tkn17p = ISOUtil.convertStringToHex(constTkn17.toString());

            int lenBuff = ((tkn17p.length() / 2) + 1);
            len = ISOUtil.padleft(lenBuff + "", 4, '0');
            constTkn17.setLength(0);
            //17 0040 00 2020202020202020202020202020202020203030
        }
        return idTkn17 + len + "00" + tkn17p;
    }
}
