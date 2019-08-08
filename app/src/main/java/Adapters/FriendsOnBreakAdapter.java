package Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.breakbuddy.R;
import com.breakbuddy.UI.MyFriendsActivity;
import com.breakbuddy.UI.ScheduleActivity;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

import Model.Friends;
import QuickScroll.SectionTitleProvider;
import Utils.Constants;
import Utils.FontManager;
import Utils.Utilz;

public class FriendsOnBreakAdapter extends RecyclerView.Adapter<FriendsOnBreakAdapter.MyViewHolder>
        implements StickyRecyclerHeadersAdapter<FriendsOnBreakAdapter.HeaderHolder>, SectionTitleProvider {

    private List<Friends> friends;
    Context context;
    Typeface iconFont, bold, medium;
    boolean ifShowTime;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvFriendsName;
        TextView tvBreakTimeOnline, tvCall, tvMsg, tvSchedule;

        public MyViewHolder(View view) {
            super(view);
            tvFriendsName = (TextView) view.findViewById(R.id.tvFriendsName);
            tvFriendsName.setTypeface(medium);
            tvBreakTimeOnline = (TextView) view.findViewById(R.id.tvBreakTimeOnline);
            tvBreakTimeOnline.setTypeface(bold);
            tvCall = (TextView) view.findViewById(R.id.tvCall);
            tvCall.setTypeface(iconFont);
            tvMsg = (TextView) view.findViewById(R.id.tvMsg);
            tvMsg.setTypeface(iconFont);
            tvSchedule = (TextView) view.findViewById(R.id.tvSchedule);
            tvSchedule.setTypeface(iconFont);
        }
    }

    public void refreshList(List<Friends> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    public FriendsOnBreakAdapter(Context context, boolean ifShowTime) {
        this.context = context;
        this.ifShowTime = ifShowTime;
        friends = new ArrayList<>();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        bold = Utilz.getTypefaceBold(context);
        medium = Utilz.getTypefaceMedium(context);
    }


    @Override
    public FriendsOnBreakAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_on_break_item_layout, parent, false);

        return new FriendsOnBreakAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FriendsOnBreakAdapter.MyViewHolder holder, final int position) {
        final Friends currFriend = friends.get(position);
        holder.tvFriendsName.setText(currFriend.getFirstname() + " " + currFriend.getLastname());

        if (ifShowTime) {
            holder.tvBreakTimeOnline.setVisibility(View.VISIBLE);
//            holder.tvCall.setVisibility(View.VISIBLE);
//            holder.tvMsg.setVisibility(View.VISIBLE);
        } else {
            holder.tvBreakTimeOnline.setVisibility(View.GONE);
//            holder.tvCall.setVisibility(View.INVISIBLE);
//            holder.tvMsg.setVisibility(View.INVISIBLE);
        }

        if (currFriend.getIs_online_offline() != null && currFriend.getIs_online_offline().equalsIgnoreCase("1")) {
            holder.tvBreakTimeOnline.setText(context.getString(R.string.online));
        } else {
            holder.tvBreakTimeOnline.setText(currFriend.getFrom_time() + " - " + currFriend.getTo_time());
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
    public long getHeaderId(int position) {
        return friends.get(position).getFirstname().toUpperCase().charAt(0);
    }

    @Override
    public FriendsOnBreakAdapter.HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.break_user_header, parent, false);
        return new FriendsOnBreakAdapter.HeaderHolder(view) ;
    }

    @Override
    public void onBindHeaderViewHolder(FriendsOnBreakAdapter.HeaderHolder holder, int position) {
        holder.header.setText(String.valueOf(friends.get(position).getFirstname().charAt(0)));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


    @Override
    public String getSectionTitle(int position) {
        return friends.get(position).getFirstname().substring(0, 1);
    }


    public class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView header;
        public HeaderHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);

        }
    }
}
