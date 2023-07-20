package com.example.idaon.item;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.idaon.BitmapConverter;
import com.example.idaon.MainActivity;
import com.example.idaon.R;
import com.example.idaon.SaveSharedPreference;
import com.example.idaon.home.CommunityFragment;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    ActionBar actionBar;

    ScaleAnimation scaleAnimation;
    BounceInterpolator bounceInterpolator; //애니메이션이 일어나는 동안의 회수, 속도를 조절하거나 시작과 종료시의 효과를 추가 할 수 있다
    CompoundButton button_favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);

        String nickname = getIntent().getStringExtra("NICKNAME");
        String name = getIntent().getStringExtra("NAME");
        String price = getIntent().getStringExtra("PRICE");
//        int image = getIntent().getIntExtra("IMAGE", 0);
        String content = getIntent().getStringExtra("CONTENT");
        String time = getIntent().getStringExtra("TIME");
        String img = getIntent().getStringExtra("IMG");

        TextView nicknameTextView = findViewById(R.id.txt_id);
        TextView nameTextView = findViewById(R.id.detailname);
        TextView PriceTextView = findViewById(R.id.detailprice);
        ImageView imageView = findViewById(R.id.detailimage);
        TextView contentView = findViewById(R.id.detailcontent);
        TextView priceTextView = findViewById(R.id.detailprice1);
        TextView timeTextView = findViewById(R.id.txt_date);

        DecimalFormat decFormat = new DecimalFormat("###,###");

        nicknameTextView.setText(nickname);
        nameTextView.setText(name);
        PriceTextView.setText(decFormat.format(Integer.parseInt(price)) + "원");
        imageView.setImageBitmap(BitmapConverter.stringToBitmap(img));
        contentView.setText(content);
        priceTextView.setText(decFormat.format(Integer.parseInt(price)) + "원");
        timeTextView.setText(time);

        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        button_favorite = findViewById(R.id.heart);
        button_favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.startAnimation(scaleAnimation);
            }
        });
        Button buybutton = findViewById(R.id.buybutton);
        buybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a1 = new Intent(getApplicationContext(), ChatroomActivity.class);
                startActivity(a1);
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;

            case R.id.delete:

                String item_user = getIntent().getStringExtra("UID");

                String uid = SaveSharedPreference.getUserID(this);
                String iname = getIntent().getStringExtra("NAME");

                Response.Listener<String> responseListener = response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        if (item_user.equals(uid)) {
                            if (success) {
                                Toast.makeText(getApplicationContext(), "삭제완료", Toast.LENGTH_SHORT).show();

                                onBackPressed();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "내 글이 아닙니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };

                DeleteRequest deleteRequest = new DeleteRequest(uid, iname, responseListener);
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(deleteRequest);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return true;
    }
}

class DeleteRequest extends StringRequest {
    final static private String URL = "https://android-con.run.goorm.io/delete.php";
    private Map<String, String> map;

    public DeleteRequest(String uid,  String iname, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("uid", uid);
        map.put("iname", iname);
    }

    protected Map<String, String> getParams() {
        return map;
    }
}