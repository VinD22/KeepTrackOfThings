package things.track.keep.chain.key.app.v.keeptrackofthings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import adapter.ThingsAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import model.Thing;

public class MainActivity extends AppCompatActivity {

    private static final String MY_PREFS_NAME = "Preferences";
    private static final String HAS_WATCHED_TUTORIAL = "HasWatchedTutorial";

    private FirebaseAnalytics mFirebaseAnalytics;

    Realm realm;

    ArrayList<Thing> listOfThings = new ArrayList<>();

    private Toolbar mToolbar;
    FloatingActionButton mAddThing;

    private RecyclerView recList;
    private ThingsAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        boolean hasWatchedTutorial = prefs.getBoolean(HAS_WATCHED_TUTORIAL, false);
        if (!hasWatchedTutorial) {
            goToOnboardingActivity();
        }

        realm = Realm.getDefaultInstance();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        recList = (RecyclerView) findViewById(R.id.product_list_recyclerview);

        mAddThing = (FloatingActionButton) findViewById(R.id.fab_add_thing);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(R.string.app_name);
            mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
            // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Home screen - 1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home screen - 2");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Main Activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        mAddThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddThing.class);
                startActivity(intent);
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(layoutManager);
        recList.setHasFixedSize(true);

        recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    mAddThing.hide();
                else if (dy < 0)
                    mAddThing.show();
            }
        });


    }

    public void getThingsList() {

        listOfThings.clear();
        RealmResults<Thing> thingResults =
                realm.where(Thing.class).findAll();

        thingResults = thingResults.sort("name"); // Default Alphabetically Sorting!

        for (Thing t : thingResults) {
            final Thing tempThing = new Thing();
            tempThing.setId(t.getId());
            tempThing.setName(t.getName());
            tempThing.setWhere(t.getWhere());
            tempThing.setAddtionalData(t.getAddtionalData());
            listOfThings.add(tempThing);
        }

        mAdapter = new ThingsAdapter(MainActivity.this, listOfThings);
        recList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        Log.i("totalThings", " " + listOfThings.size());
        Toast.makeText(this, "Things Size : " + listOfThings.size(), Toast.LENGTH_SHORT).show();

    }

    private void goToOnboardingActivity() {

        Intent intent = new Intent(MainActivity.this, OnBoardingActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
//            case R.id.search:
//
//                final Dialog dialog = new Dialog(MainActivity.this);
//                dialog.setContentView(R.layout.custom_product_search);
//
//                // set the custom dialog components - text, image and button
//                EditText mItemName = (EditText) dialog.findViewById(R.id.product_name);
//
//                mItemName.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                        mAdapter.filter(charSequence.toString() + "");
//
//
////                        private List<Thing> data;
////                        private List<Thing> dataCopy;
////
////                        public void filter(String text) {
////                            // Toast.makeText(mContext, "" + text + " /// " + dataCopy.size()  , Toast.LENGTH_SHORT).show();
////                            data.clear();
////                            if(text.isEmpty()){
////                                data.addAll(dataCopy);
////                            } else {
////                                data.clear();
////                                text = text.toLowerCase();
////                                for(Thing item: dataCopy){
////                                    if(item.getName().toLowerCase().contains(text)){
////                                        data.add(item);
////                                    }
////                                }
////                            }
////                            notifyDataSetChanged();
////                        }
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//
//
//
//                    }
//                });
//
//                dialog.setCancelable(true);
//                dialog.show();

//                return true;
            case R.id.settings:
                // Toast.makeText(this, "Show Settings!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getThingsList();
    }

}
