package Adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.breakbuddy.R;

import java.util.ArrayList;
import java.util.List;

import Model.BreakTime;
import Utils.FontManager;
import Utils.Utilz;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {
    Context context;
    ArrayList<BreakTime> schedules;
    Typeface iconFont, bold;


    public ScheduleAdapter(Context context, ArrayList<BreakTime> schedules) {
        this.context = context;
        this.schedules = schedules;
        bold = Utilz.getTypefaceBold(context);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView schduleName, tvCheck;
        public RelativeLayout tlMain;

        public MyViewHolder(View view) {
            super(view);
            tlMain = (RelativeLayout) view.findViewById(R.id.tlMain);
            schduleName = (TextView) view.findViewById(R.id.schduleName);
            schduleName.setTypeface(bold);
            iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            tvCheck = (TextView) view.findViewById(R.id.tvCheck);
        }
    }

    public void refreshAdapter(ArrayList<BreakTime> schedules) {
        this.schedules = schedules;
        notifyDataSetChanged();
    }

    public ArrayList<BreakTime> getScheduleData() {
        return schedules;
    }

    @Override
    public ScheduleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item_layout, parent, false);

        return new ScheduleAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ScheduleAdapter.MyViewHolder holder, final int position) {
        BreakTime currBreakTime = schedules.get(position);
        holder.schduleName.setText(currBreakTime.getStartTime() + " - " + currBreakTime.getEndTime());
        holder.tvCheck.setTypeface(iconFont);

        if (currBreakTime.isEnable()) {
            holder.tvCheck.setVisibility(View.VISIBLE);
            holder.tlMain.setBackgroundColor(ContextCompat.getColor(context, R.color.schedule_select_color));
        } else {
            holder.tlMain.setBackgroundColor(ContextCompat.getColor(context, R.color.new_bg_blank));
            holder.tvCheck.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

}
