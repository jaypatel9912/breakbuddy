package Adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.support.transition.Visibility;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.breakbuddy.R;

import java.util.ArrayList;
import java.util.List;

import Model.Friend;
import Utils.Constants;
import Utils.FontManager;
import Utils.Utilz;

public class BBContactAdapter extends RecyclerView.Adapter<BBContactAdapter.MyViewHolder> {


    private List<Friend> friend;
    Context context;
    ContactAdapter.InviteListener listener;
    String currUserId;
    Typeface iconFont,medium, bold;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvAccept;
        TextView tvCancel;
        Button tvAdd;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvName.setTypeface(medium);
            tvCancel = (TextView) view.findViewById(R.id.tvCancel);
            tvCancel.setTypeface(iconFont);
            tvAccept = (TextView) view.findViewById(R.id.tvAdd);
            tvAccept.setTypeface(iconFont);
            tvAdd = (Button) view.findViewById(R.id.tvAccept);
            tvAdd.setVisibility(View.VISIBLE);
            tvAdd.setTypeface(bold);
        }
    }

    public BBContactAdapter(Context context, ContactAdapter.InviteListener listener) {
        this.context = context;
        this.listener = listener;
        friend = new ArrayList<>();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        currUserId = Utilz.getUserDetails(context).get(Constants.MM_user_id);
        medium = Utilz.getTypefaceMedium(context);
        bold = Utilz.getTypefaceBold(context);
    }

    public void refreshList(List<Friend> Friend) {
        this.friend = Friend;
        notifyDataSetChanged();
    }

    @Override
    public BBContactAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.added_me_item_layout, parent, false);

        return new BBContactAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BBContactAdapter.MyViewHolder holder, final int position) {
        final Friend currFriend = friend.get(position);
        if (currFriend.getName() != null && !currFriend.getName().isEmpty())
            holder.tvName.setText(currFriend.getName());
        else
            holder.tvName.setText(currFriend.getEmail().equalsIgnoreCase("") ? currFriend.getPhone_no() : currFriend.getEmail());

        if (currFriend.getStatus().equalsIgnoreCase("0")) {
            holder.tvAdd.setText(context.getString(R.string.add));
            holder.tvAdd.setClickable(true);

            holder.tvAdd.setVisibility(View.VISIBLE);
            holder.tvCancel.setVisibility(View.GONE);
            holder.tvAccept.setVisibility(View.GONE);
        } else if (currFriend.getStatus().equalsIgnoreCase("1")) {
            if (currFriend.getUser_id().equalsIgnoreCase(currUserId)) {
                holder.tvAdd.setText(context.getString(R.string.req_sent));
                holder.tvAdd.setClickable(false);
                holder.tvAdd.setVisibility(View.VISIBLE);
                holder.tvCancel.setVisibility(View.GONE);
                holder.tvAccept.setVisibility(View.GONE);
            } else {
                holder.tvCancel.setVisibility(View.VISIBLE);
                holder.tvAccept.setVisibility(View.VISIBLE);
                holder.tvAdd.setVisibility(View.GONE);
                holder.tvAdd.setClickable(false);
            }

        }

        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currFriend.getUser_id().equalsIgnoreCase(currUserId)) {
                    listener.inviteClicked(true, position, 0, false, currFriend);
                }
            }
        });
//
        holder.tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    listener.inviteClicked(true ,position, 1, true, currFriend);
            }
        });

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.inviteClicked(true ,position, 1, false, currFriend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friend.size();
    }
}
