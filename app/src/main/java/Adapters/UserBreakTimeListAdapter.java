package Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.breakbuddy.R;
import com.breakbuddy.UI.AddContactActivity;
import com.breakbuddy.UI.ScheduleActivity;

import java.util.ArrayList;
import java.util.List;

import Model.Friend;
import Model.Friends;
import Model.Schedule;
import QuickScroll.SectionTitleProvider;
import Utils.Constants;
import Utils.FontManager;
import Utils.Utilz;

public class UserBreakTimeListAdapter extends RecyclerView.Adapter<UserBreakTimeListAdapter.MyViewHolder> {


    private List<Friends> friends;
    Context context;
    Typeface iconFont, bold, medium;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername, tvUserSchedule;
        TextView tvCall, tvMsg, tvScheduleTime, tvNoFrinds, tvSchedule;
        ViewSwitcher viewSwitcherNoRecord;

        public MyViewHolder(View view) {
            super(view);
            tvUserSchedule = (TextView) view.findViewById(R.id.tvUserSchedule);
            tvUserSchedule.setTypeface(bold);
            tvUsername = (TextView) view.findViewById(R.id.tvUsername);
            tvUsername.setTypeface(medium);
            tvNoFrinds = (TextView) view.findViewById(R.id.tvNoFrinds);
            tvNoFrinds.setTypeface(medium);
            tvCall = (TextView) view.findViewById(R.id.tvCall);
            tvMsg = (TextView) view.findViewById(R.id.tvMsg);
            tvScheduleTime = (TextView) view.findViewById(R.id.tvScheduleTime);
            tvScheduleTime.setTypeface(bold);
            tvMsg.setTypeface(iconFont);
            tvCall.setTypeface(iconFont);
            viewSwitcherNoRecord = (ViewSwitcher) view.findViewById(R.id.viewSwitcherNoRecord);
            tvSchedule = (TextView) view.findViewById(R.id.tvSchedule);
            tvSchedule.setTypeface(iconFont);
        }
    }

    public void refreshList(List<Friends> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    public UserBreakTimeListAdapter(Context context) {
        this.context = context;
        friends = new ArrayList<>();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        bold = Utilz.getTypefaceBold(context);
        medium = Utilz.getTypefaceMedium(context);
    }

    @Override
    public UserBreakTimeListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.break_user_item_layout, parent, false);

        return new UserBreakTimeListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UserBreakTimeListAdapter.MyViewHolder holder, final int position) {
        final Friends currFriend = friends.get(position);

        if (currFriend.getFriend_user_id() == null) {
            holder.viewSwitcherNoRecord.setDisplayedChild(1);
            holder.tvScheduleTime.setText(currFriend.getParentFromTime() + " - " + currFriend.getParentToTime());
            if (currFriend.isIfShowSection())
                holder.tvScheduleTime.setVisibility(View.VISIBLE);
            else
                holder.tvScheduleTime.setVisibility(View.GONE);

        } else {
            holder.viewSwitcherNoRecord.setDisplayedChild(0);

            holder.tvUsername.setText(currFriend.getFirstname() + " " + currFriend.getLastname());
            holder.tvScheduleTime.setText(currFriend.getParentFromTime() + " - " + currFriend.getParentToTime());
            if (currFriend.getIs_online_offline().equalsIgnoreCase("1")) {
                holder.tvUserSchedule.setText(context.getString(R.string.online));
            } else {
                holder.tvUserSchedule.setText("(" + currFriend.getFrom_time() + " - " + currFriend.getTo_time() + ")");
            }

            if (!currFriend.getFrom_time().equalsIgnoreCase(currFriend.getParentFromTime()) || !currFriend.getTo_time().equalsIgnoreCase(currFriend.getParentToTime()))
                holder.tvUserSchedule.setVisibility(View.VISIBLE);
            else
                holder.tvUserSchedule.setVisibility(View.GONE);

            if (currFriend.isIfShowSection())
                holder.tvScheduleTime.setVisibility(View.VISIBLE);
            else
                holder.tvScheduleTime.setVisibility(View.GONE);
        }

        holder.tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + currFriend.getPhone_no()));
                context.startActivity(intent);
            }
        });

        holder.tvMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("smsto:" + currFriend.getPhone_no()));
                sendIntent.putExtra("sms_body", "");
                context.startActivity(sendIntent);

            }
        });

        holder.tvSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScheduleActivity.class);
                intent.putExtra(Constants.MM_user_id, currFriend.getFriend_user_id());
                intent.putExtra(Constants.MM_friend_name, currFriend.getFirstname() + " " + currFriend.getLastname());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
