package cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cnb.pichincha.wposs.mivecino_pichincha.R;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Sector;

public class AdaptadorSector  extends BaseAdapter {

    private Context context;
    private List<Sector> lista;

    public AdaptadorSector(Context context, List<Sector> lista) {
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
        return Long.parseLong(lista.get(position).getIdSector());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.inform, parent, false);
        }
        Sector currentItem = (Sector) getItem(position);

        TextView textViewItemName = convertView.findViewById(R.id.mensajeria);

        textViewItemName.setText(currentItem.getSector());
        
        return convertView;
    }
}
