package cn.desert.newpos.payui.transrecord;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;
import cnb.pichincha.wposs.mivecino_pichincha.R;
import newpos.libpay.global.TMConfig;
import newpos.libpay.trans.translog.TransLogData;
import newpos.libpay.utils.PAYUtils;

public class HistorylogAdapter extends ListAdapter<TransLogData> {

	private TMConfig config;
	private OnItemReprintClick click ;

	public HistorylogAdapter(AppCompatActivity context , OnItemReprintClick l) {
		super(context);
		config = TMConfig.getInstance();
		click = l ;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHold viewHold = null;
		TransLogData item = null;
		if (mList.size() > 0) {
			item = mList.get(position);
		}
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_history_item, null);
			viewHold = new ViewHold();
			viewHold.tv_pan = (TextView) convertView.findViewById(R.id.tv_pan);
			viewHold.tv_voucherno = (TextView) convertView.findViewById(R.id.tv_voucherno);
			viewHold.tv_authno = (TextView) convertView.findViewById(R.id.tv_authno);
			viewHold.tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
			viewHold.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			viewHold.tv_batchno = (TextView) convertView.findViewById(R.id.tv_batchno);
			viewHold.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
			viewHold.tv_right_top = (TextView) convertView.findViewById(R.id.status_flag);
			viewHold.reprint = (Button) convertView.findViewById(R.id.re_print);
			convertView.setTag(viewHold);
		} else {
			viewHold = (ViewHold) convertView.getTag();
		}

		if (item != null) {
			String pan = item.getMsgID() ;
			if (!PAYUtils.isNullWithTrim(pan)) {
				String temp ;
				temp = UIUtils.getStringByInt(mContext, R.string.card_num) + " : " +pan;
				viewHold.tv_pan.setText(temp);
			}

			final String traceno = item.getTraceNo() ;
			if (!PAYUtils.isNullWithTrim(traceno)) {
				viewHold.tv_voucherno.setText(UIUtils.getStringByInt(mContext, R.string.voucher_num) + " : " + traceno);
			}

			String amount = item.getAmount().toString() ;
			if (!PAYUtils.isNullWithTrim(amount)) {
				viewHold.tv_amount.setText(UIUtils.getStringByInt(mContext, R.string.amount) + " : " + PAYUtils.TwoWei(amount));
			}

			String en = item.getEName() ;
			if(!PAYUtils.isNullWithTrim(en)) {
				viewHold.tv_status.setText(UIUtils.getStringByInt(mContext, R.string.trans_type) + ":" + en);
			}
			
			viewHold.tv_right_top.setVisibility(View.GONE);

			viewHold.tv_date.setText(UIUtils.getStringByInt(mContext, R.string.trans_date)+ ":" + PAYUtils.printStr(item.getLocalDate(), item.getLocalTime()));

			String bacth = config.getBatchNo() ;
			if (!PAYUtils.isNullWithTrim(bacth)) {
				viewHold.tv_batchno.setText(UIUtils.getStringByInt(mContext, R.string.batch_num) + " : " + config.getBatchNo());
			}

			viewHold.reprint.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(click!=null){
						click.OnItemClick(traceno);
					}
				}
			});

			convertView.setTag(R.id.tag_item_history_trans, item);
		}
		return convertView;
	}

	final class ViewHold {
		TextView tv_pan;
		TextView tv_voucherno;
		TextView tv_authno;
		TextView tv_amount;
		TextView tv_date;
		TextView tv_batchno;
		TextView tv_status;
		TextView tv_right_top;
		Button reprint ;
	}

	public interface OnItemReprintClick{
		void OnItemClick(String traceNO);
	}
}
