package cnb.pichincha.wposs.mivecino_pichincha.screens;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.desert.newpos.payui.UIUtils;
import cnb.pichincha.wposs.mivecino_pichincha.R;

public class BannerInferior extends LinearLayout {

    TextView txtVersion;

    public BannerInferior(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Typeface tipoFuente1 = Typeface.createFromAsset(context.getAssets(), "font/Prelo-Medium.otf");
        LayoutInflater.from(context).inflate(R.layout.layout_banner_inferior, this);
        txtVersion = findViewById(R.id.tvVersion);
        txtVersion.setText(UIUtils.versionApk(context));
        txtVersion.setTypeface(tipoFuente1);
    }

}
