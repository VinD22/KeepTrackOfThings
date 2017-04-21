package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import model.Thing;
import things.track.keep.chain.key.app.v.keeptrackofthings.R;

/*
 *  Adapter class to see all the categories + delete categories which are not needed!
 */

public class ThingsAdapter extends RecyclerView.Adapter<ThingsAdapter.RecyclerViewHolder> {

    private static final String INVENTORY_UPDATE = "inventory_update";
    private List<Thing> data;
    private List<Thing> dataCopy;
    private Context mContext;
    Realm realm;

    public ThingsAdapter(Context context, ArrayList<Thing> data) {
        this.mContext = context;
        this.data = data;
        dataCopy = new ArrayList<Thing>();
        dataCopy.addAll(data);
        realm = Realm.getDefaultInstance();
        // setHasStableIds(true);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.thing_list_item, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder viewHolder, final int position) {

        final Thing tempThing = data.get(viewHolder.getAdapterPosition());
        viewHolder.mThingName.setText(capitalizeFirstLetter(tempThing.getName()));

        viewHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Edit Thing by clicking on the Linear Layout, takes the user to the Edit Thing Page!
//                Intent intent = new Intent(mContext, ViewThing.class);
//                intent.putExtra("id", tempThing.getId());
//                mContext.startActivity(intent);

            }
        });



    }


    public void filter(String text) {
         // Toast.makeText(mContext, "" + text + " /// " + dataCopy.size()  , Toast.LENGTH_SHORT).show();
        data.clear();
        if(text.isEmpty()){
            data.addAll(dataCopy);
        } else {
            data.clear();
            text = text.toLowerCase();
            for(Thing item: dataCopy){
                if(item.getName().toLowerCase().contains(text)){
                    data.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout placeHolder;
        public LinearLayout mLinearLayout;
        protected TextView mThingName;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.lin);
            placeHolder = (LinearLayout) itemView.findViewById(R.id.mainHolder);
            mThingName = (TextView) itemView.findViewById(R.id.name);
        }

    }


}
