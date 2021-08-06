package cnb.pichincha.wposs.mivecino_pichincha.tokns;

import newpos.libpay.utils.ISOUtil;

public class Tkn40 {
    private int cantidadItems;
    private String[] listaItems;
    private int indice;

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
                this.listaItems = strItems.split("\\|");
            }
        }catch (Exception e){
            //InitTrans.wrlg.wrDataTxt("Error en guardarImpresionTxt() - " + nomarchivo + " - error " + e.toString());
        }

        return indice - aIndice;
    }




    public String packToken(){
        String idTkn = "40";
        StringBuilder tkn = new StringBuilder();

        String sCantItems = ISOUtil.padleft(Integer.toString(this.cantidadItems), 2, '0');

        for(int i = 0; i < this.cantidadItems; i++){
            if (i != 0) {
                tkn.append("|");
            }
            tkn.append(this.listaItems[i]);
        }
        String strLongitud =  ISOUtil.padleft(Integer.toString(tkn.length()+1), 4, '0');
        String tknString = ISOUtil.convertStringToHex(tkn.toString());
        return idTkn +  strLongitud + sCantItems + tknString;
    }

    public void clean(){
        cantidadItems = -1;
        listaItems = new String[10];
    }

    public String packTokenAtm(){
        String idTkn = "40";
        StringBuilder tkn = new StringBuilder();

        String sCantItems = ISOUtil.padleft(Integer.toString(this.cantidadItems), 2, '0');

        for(int i = 0; i < this.cantidadItems; i++){
            //tkn.append("|");
            tkn.append(this.listaItems[i]);
        }
        String strLongitud =  ISOUtil.padleft(Integer.toString(tkn.length()+1), 4, '0');
        String tknString = ISOUtil.convertStringToHex(tkn.toString());
        return idTkn +  strLongitud + sCantItems + tknString;
    }
}