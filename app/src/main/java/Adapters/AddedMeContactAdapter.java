package Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.breakbuddy.R;

import java.util.ArrayList;
import java.util.List;

import Model.Contact;
import Model.Friend;
import QuickScroll.SectionTitleProvider;
import Utils.FontManager;
import Utils.Utilz;


public class AddedMeContactAdapter extends RecyclerView.Adapter<AddedMeContactAdapter.MyViewHolder> implements SectionTitleProvider {

    @Override
    public String getSectionTitle(int position) {
        return friends.get(position).getFirstname().substring(0, 1);
    }

    public interface AddMeListener {
        public void onCancel(int pos);

        public void onAdd(int pos);
    }

    private List<Friend> friends;
    Context context;
    AddMeListener listener;
    Typeface iconFont, medium;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        TextView tvCancel, tvAdd;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvName.setTypeface(medium);
            tvCancel = (TextView) view.findViewById(R.id.tvCancel);
            tvAdd = (TextView) view.findViewById(R.id.tvAdd);
            tvCancel.setTypeface(iconFont);
            tvAdd.setTypeface(iconFont);
        }
    }

    public void refreshList(List<Friend> friends){
        this.friends = friends;
        notifyDataSetChanged();
    }

    public AddedMeContactAdapter(Context context, AddMeListener listener) {
        this.context = context;
        this.listener = listener;
        friends = new ArrayList<>();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        medium = Utilz.getTypefaceMedium(context);
    }


    @Override
    public AddedMeContactAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.added_me_item_layout, parent, false);

        return new AddedMeContactAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AddedMeContactAdapter.MyViewHolder holder, final int position) {
        Friend currFriend = friends.get(position);
        holder.tvName.setText(currFriend.getFirstname() + " " + currFriend.getLastname());
        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel(position);
            }
        });

        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAdd(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
