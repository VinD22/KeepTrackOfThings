package things.track.keep.chain.key.app.v.keeptrackofthings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import model.Thing;

/**
 * View the Item that
 */

public class ViewItem extends AppCompatActivity {

    private Button mEditButton;
    private ImageView mItemImage;
    private TextView mName, mWhere, mAdditionalDetails;

    Realm realm;

    int itemId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_item);

        realm = Realm.getDefaultInstance();

        mEditButton = (Button) findViewById(R.id.edit_item);

        mItemImage = (ImageView) findViewById(R.id.item_image);

        mName = (TextView) findViewById(R.id.name);
        mWhere = (TextView) findViewById(R.id.where);
        mAdditionalDetails = (TextView) findViewById(R.id.additional_info);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Toast.makeText(this, "Error in intents! (Contact Developer)", Toast.LENGTH_SHORT).show();
        } else {
            itemId = extras.getInt("id");

            Thing tempThing = realm.where(Thing.class).equalTo("id", itemId).findFirst();
            realm.beginTransaction();

            mName.setText(tempThing.getName());
            mWhere.setText(tempThing.getWhere() + "");
            mAdditionalDetails.setText(tempThing.getAddtionalData() + "");

            Bitmap bmp = BitmapFactory.decodeByteArray(tempThing.getImage(), 0, tempThing.getImage().length);
            mItemImage.setImageBitmap(bmp);

            realm.commitTransaction();

        }

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(itemId == -1) {
                    Toast.makeText(ViewItem.this, "Error in Item Id", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(ViewItem.this, EditItem.class);
                    intent.putExtra("id", itemId);
                    startActivity(intent);

                }

            }
        });



    }
}
