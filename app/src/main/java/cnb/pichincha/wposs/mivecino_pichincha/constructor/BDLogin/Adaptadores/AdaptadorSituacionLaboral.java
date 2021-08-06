package cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.SituacionLaboral;

public class AdaptadorSituacionLaboral extends BaseAdapter {

    private Context context;
    private List<SituacionLaboral> lista;

    public AdaptadorSituacionLaboral(Context context, List<SituacionLaboral> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        long valor = 1;
        try {
             valor = Long.parseLong(lista.get(position).getId_laboral());
        }catch (Exception ex){
            ex.printStackTrace();
            valor = 1;
        }
        return valor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.inform, parent, false);
        }
        SituacionLaboral currentItem = (SituacionLaboral) getItem(position);

        TextView textViewItemName = convertView.findViewById(R.id.mensajeria);

        textViewItemName.setText(currentItem.getName_laboral());
        
        return convertView;
    }
}
