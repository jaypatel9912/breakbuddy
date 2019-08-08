package Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.breakbuddy.R;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import Model.Friend;
import QuickScroll.SectionTitleProvider;
import Utils.FontManager;
import Utils.Utilz;


public class MyFriendsAdapter extends RecyclerSwipeAdapter<MyFriendsAdapter.MyViewHolder>
        implements StickyRecyclerHeadersAdapter<MyFriendsAdapter.HeaderHolder>, SectionTitleProvider {

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public interface OnMyFriendOperate {
        public void onDelete(Friend friend);

        public void onShowScheduleOfUser(Friend friend);
    }

    OnMyFriendOperate listener;
    Context context;
    Typeface iconFont, medium;
    List<Friend> friends;

    public MyFriendsAdapter(Context context, OnMyFriendOperate listener) {
        this.context = context;
        this.listener = listener;
        friends = new ArrayList<>();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        medium = Utilz.getTypefaceMedium(context);
    }

    public void refreshList(List<Friend> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    @Override
    public MyFriendsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_friend_item_layout, parent, false);
        return new MyFriendsAdapter.MyViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(final MyFriendsAdapter.MyViewHolder holder, final int position) {
        final Friend friend = friends.get(position);
        holder.tvUsername.setText(friend.getFirstname() + " " + friend.getLastname());
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.tvTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swipeLayout.toggle();
                listener.onDelete(friend);
            }
        });

        holder.tvSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swipeLayout.toggle();
                listener.onShowScheduleOfUser(friend);
            }
        });

        holder.tvArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(holder.swipeLayout.getOpenStatus() == SwipeLayout.Status.Close){
//                    holder.tvArrow.setText(context.getString(R.string.fa_angle_left));
//                }else{
//                    holder.tvArrow.setText(context.getString(R.string.fa_angle_right));
//                }
                holder.swipeLayout.toggle();
            }
        });

        holder.tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swipeLayout.toggle();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + friend.getPhone_no()));
                context.startActivity(intent);
            }
        });

        holder.tvMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swipeLayout.toggle();
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("smsto:" + friend.getPhone_no()));
                sendIntent.putExtra("sms_body", "");
                context.startActivity(sendIntent);
            }
        });

        mItemManger.bindView(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public long getHeaderId(int position) {
//        if (position == 0) {
//            return -1;
//        } else {
        return friends.get(position).getFirstname().toUpperCase().charAt(0);
//        }
    }

    @Override
    public MyFriendsAdapter.HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.break_user_header, parent, false);
        return new MyFriendsAdapter.HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(MyFriendsAdapter.HeaderHolder holder, int position) {
        holder.header.setText(String.valueOf(friends.get(position).getFirstname().charAt(0)));
    }

    @Override
    public String getSectionTitle(int position) {
        return friends.get(position).getFirstname().substring(0, 1);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername, tvSchedule, tvTrash, tvArrow, tvCall, tvMsg;
        SwipeLayout swipeLayout;

        public MyViewHolder(View view) {
            super(view);
            swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe);
            tvUsername = (TextView) view.findViewById(R.id.tvUsername);
            tvUsername.setTypeface(medium);
            tvTrash = (TextView) view.findViewById(R.id.tvTrash);
            tvSchedule = (TextView) view.findViewById(R.id.tvSchedule);

            tvArrow = (TextView) view.findViewById(R.id.tvArrow);
            tvCall = (TextView) view.findViewById(R.id.tvCall);
            tvMsg = (TextView) view.findViewById(R.id.tvMsg);

            tvTrash.setTypeface(iconFont);
            tvSchedule.setTypeface(iconFont);
            tvArrow.setTypeface(iconFont);
            tvCall.setTypeface(iconFont);
            tvMsg.setTypeface(iconFont);
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView header;

        public HeaderHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);

        }
    }

}