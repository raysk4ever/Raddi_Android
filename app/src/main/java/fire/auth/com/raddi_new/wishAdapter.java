package fire.auth.com.raddi_new;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class wishAdapter extends RecyclerView.Adapter<wishAdapter.ViewHolder> {

    List<wishmodel> mList;

    public wishAdapter(List<wishmodel> mList) {
        this.mList = mList;
    }

    public wishAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.wishlist_data,viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.wish_book_name.setText(mList.get(i).getPostid());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView wish_book_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            wish_book_name = mView.findViewById(R.id.wish_book_name);





        }
    }
}
