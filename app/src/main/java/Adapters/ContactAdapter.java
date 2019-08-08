package Adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.breakbuddy.R;

import java.util.ArrayList;
import java.util.List;

import Model.Contact;
import Model.Friend;
import Utils.Constants;
import Utils.Utilz;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    public interface InviteListener {
        public void inviteClicked(boolean ifBB, int pos, int status, boolean accept, Friend friend);
    }

    private List<Friend> friend;
    Context context;
    InviteListener listener;
    String currUserId;
    Typeface medium, bold;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvReject, tvPhoneNo;
        Button tvInvite;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvName.setTypeface(medium);
            tvInvite = (Button) view.findViewById(R.id.tvInvite);
            tvInvite.setTypeface(bold);
            tvReject = (TextView) view.findViewById(R.id.tvReject);
            tvReject.setTypeface(bold);
            tvPhoneNo =  (TextView) view.findViewById(R.id.tvPhoneNo);
            tvPhoneNo.setTypeface(bold);
        }
    }

    public ContactAdapter(Context context, InviteListener listener) {
        this.context = context;
        this.listener = listener;
        friend = new ArrayList<>();
        currUserId = Utilz.getUserDetails(context).get(Constants.MM_user_id);
        medium = Utilz.getTypefaceMedium(context);
        bold = Utilz.getTypefaceBold(context);
    }

    public void refreshList(List<Friend> Friend) {
        this.friend = Friend;
        notifyDataSetChanged();
    }

    @Override
    public ContactAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item_layout, parent, false);

        return new ContactAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ContactAdapter.MyViewHolder holder, final int position) {
        final Friend currFriend = friend.get(position);
        if (currFriend.getName() != null && !currFriend.getName().isEmpty())
            holder.tvName.setText(currFriend.getName());
        else
            holder.tvName.setText("");

        if(currFriend.getPhone_no() == null || currFriend.getPhone_no().isEmpty())
            holder.tvPhoneNo.setText(currFriend.getEmail());
        else
            holder.tvPhoneNo.setText(currFriend.getPhone_no());

        if (currFriend.getStatus().equalsIgnoreCase("0")) {
            holder.tvInvite.setText(context.getString(R.string.invite));
            holder.tvInvite.setClickable(true);
            holder.tvReject.setVisibility(View.GONE);
        } else if (currFriend.getStatus().equalsIgnoreCase("2")) {
            holder.tvInvite.setText(context.getString(R.string.added));
            holder.tvInvite.setClickable(false);
            holder.tvReject.setVisibility(View.GONE);
        } else if (currFriend.getStatus().equalsIgnoreCase("1")) {
            holder.tvInvite.setClickable(true);
            if (currFriend.getUser_id().equalsIgnoreCase(currUserId)) {
                holder.tvInvite.setText(context.getString(R.string.req_sent));
                holder.tvReject.setVisibility(View.GONE);
            } else {
                holder.tvInvite.setText(context.getString(R.string.req_accept));
                holder.tvReject.setVisibility(View.VISIBLE);
            }
        }

        holder.tvInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currFriend.getStatus().equalsIgnoreCase("0"))
                    listener.inviteClicked(false, position, 0, false, currFriend);
                if (currFriend.getStatus().equalsIgnoreCase("1") && currFriend.getFriend_user_id() != null && currFriend.getFriend_user_id().equalsIgnoreCase(currUserId))
                    listener.inviteClicked(false, position, 1, true, currFriend);
            }
        });

        holder.tvReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.inviteClicked(false, position, 1, false, currFriend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friend.size();
    }
}
