package cnb.pichincha.wposs.mivecino_pichincha.constructor;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cnb.pichincha.wposs.mivecino_pichincha.R;

public class ListViewAdapter extends BaseAdapter {

    Context context;
    String[] titulos;
    int[] imagenes;
    LayoutInflater inflater;

    public ListViewAdapter(Context context, String[] titulos, int[] imagenes) {
        this.context = context;
        this.titulos = titulos;
        this.imagenes = imagenes;
    }

    @Override
    public int getCount() {
        return titulos.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Typeface tipoFuente3 = Typeface.createFromAsset(context.getAssets(), "font/Prelo-Medium.otf");
        TextView txtTitle;
        ImageView imgImg;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_adapter, parent, false);
        txtTitle = itemView.findViewById(R.id.list_row_title);
        imgImg = itemView.findViewById(R.id.list_row_image);
        txtTitle.setText(titulos[position]);
        txtTitle.setTypeface(tipoFuente3);
        imgImg.setImageResource(imagenes[position]);

        return itemView;
    }
}
