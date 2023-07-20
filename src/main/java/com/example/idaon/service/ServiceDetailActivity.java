package com.example.idaon.service;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idaon.BitmapConverter;
import com.example.idaon.R;
import com.example.idaon.SaveSharedPreference;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Target;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceDetailActivity extends AppCompatActivity {
    ActionBar actionBar;
    AlertDialog.Builder builder;

    private String requestUrl;
    private ArrayList<ServiceDetail> itemList = null;

    ServiceDetail serviceDetail = null;
    private String way = "";

    TextView ContentTextView, TargetTextView, PhoneTextView, WayTextView;

    String service_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_detail_activity);

        Toolbar toolbar = findViewById(R.id.toolbar22);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("  ");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);

        String name = getIntent().getStringExtra("SERVICE_NAME");
        String detail = getIntent().getStringExtra("SERVICE_SERVDGST");
        service_id = getIntent().getStringExtra("SERVICE_ID");
        String uri = getIntent().getStringExtra("SERVICE_LINK");

        TextView NameTextView = findViewById(R.id.sername);
        TextView DetailTextView = findViewById(R.id.serdetail);
        TargetTextView = findViewById(R.id.sersurport2);
        ContentTextView = findViewById(R.id.serplus2);
        PhoneTextView = findViewById(R.id.serphone2);
        WayTextView = findViewById(R.id.serway2);

        NameTextView.setText(name);
        DetailTextView.setText(detail);

        Button Button = findViewById(R.id.sitebutton);
        Button.setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            @Override
            public void onClick(View view) {
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    public class MyAsyncTask extends AsyncTask<String, Void, String> {

        private List<String> place;

        @Override
        protected String doInBackground(String... strings) {
            requestUrl = "https://www.bokjiro.go.kr/ssis-tbu/NationalWelfareInformations/NationalWelfaredetailed.do?serviceKey=서비스키&callTp=D&servId=" + service_id + "&SG_APIM=2ug8Dm9qNBfD32JLZGPN64f3EoTlkpD8kSOHWfXpyrY";
            Log.d("service_id", service_id);

            try {
                boolean b_servId = false;
                boolean b_servNm = false;
                boolean b_jurMnofNm = false;
                boolean b_tgtrDtlCn = false;
                boolean b_slctCritCn = false;
                boolean b_alwServCn = false;
                boolean b_lifeArray = false;
                boolean b_trgterIndvdlArray = false;
                boolean b_applmetList = false;
                boolean b_servSeCode = false;
                boolean b_servSeDetailLink = false;
                boolean b_servSeDetailNm = false;

                URL url = new URL(requestUrl);
                InputStream is = url.openStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(is));

                String tag;
                int eventType = parser.getEventType();

                List<String> place = new ArrayList<>();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            itemList = new ArrayList<ServiceDetail>();
                            break;

                        case XmlPullParser.END_TAG:
                            tag = parser.getName();

                            if (tag.equals("wantedDtl")) {
                                itemList.add(serviceDetail);
                            }

                            tag = "";
                            break;

                        case XmlPullParser.START_TAG:
                            tag = parser.getName();

                            if (tag.equals("wantedDtl")) {
                                serviceDetail = new ServiceDetail();
                            }

                            if (tag.equals("servId")) b_servId = true;
                            if (tag.equals("servNm")) b_servNm = true;
                            if (tag.equals("jurMnofNm")) b_jurMnofNm = true;
                            if (tag.equals("tgtrDtlCn")) b_tgtrDtlCn = true;
                            if (tag.equals("slctCritCn")) b_slctCritCn = true;
                            if (tag.equals("alwServCn")) b_alwServCn = true;
                            if (tag.equals("lifeArray")) b_lifeArray = true;
                            if (tag.equals("trgterIndvdlArray")) b_trgterIndvdlArray = true;
                            if (tag.equals("applmetList")) b_applmetList = true;
                            if (tag.equals("servSeCode")) b_servSeCode =true;
                            if (tag.equals("servSeDetailLink")) b_servSeDetailLink = true;
                            if (tag.equals("servSeDetailNm")) b_servSeDetailNm = true;

                            break;

                        case XmlPullParser.TEXT:

                            if (b_servId) {
                                if (parser.getText() != null) {
                                    serviceDetail.setServId(parser.getText());
                                    b_servId = false;
                                    Log.d("servId", parser.getText().trim());
                                } else {
                                    serviceDetail.setServId(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_servNm) {
                                if (parser.getText() != null) {
                                    serviceDetail.setServNm(parser.getText());
                                    b_servNm = false;
                                    Log.d("servNm", parser.getText().trim());
                                } else {
                                    serviceDetail.setServNm(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_jurMnofNm) {
                                if (parser.getText() != null) {
                                    serviceDetail.setJurMnofNm(parser.getText());
                                    b_jurMnofNm = false;
                                    Log.d("jurmnofnm", parser.getText().trim());
                                } else {
                                    serviceDetail.setJurMnofNm(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_tgtrDtlCn) {
                                if (parser.getText() != null) {
                                    serviceDetail.setTgtrDtlCn(parser.getText());
                                    TargetTextView.setText(parser.getText().trim());
                                    b_tgtrDtlCn = false;
                                    Log.d("tgtrDtlCn", parser.getText().trim());
                                } else {
                                    serviceDetail.setTgtrDtlCn(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_slctCritCn) {
                                if (parser.getText() != null) {
                                    serviceDetail.setSlctCritCn(parser.getText());
                                    b_slctCritCn = false;
                                    Log.d("slctCritCn", parser.getText().trim());
                                } else {
                                    serviceDetail.setSlctCritCn(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_alwServCn) {
                                if (parser.getText() != null) {
                                    serviceDetail.setAlwServCn(parser.getText());
                                    ContentTextView.setText(parser.getText().trim());
                                    b_alwServCn = false;
                                    Log.d("alwServCn", parser.getText().trim());
                                } else {
                                    serviceDetail.setAlwServCn(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_lifeArray) {
                                if (parser.getText() != null) {
                                    serviceDetail.setLifeArray(parser.getText());
                                    b_lifeArray = false;
                                    Log.d("lifeArray", parser.getText().trim());
                                } else {
                                    serviceDetail.setLifeArray(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_trgterIndvdlArray) {
                                if (parser.getText() != null) {
                                    serviceDetail.setTrgterIndvdlArray(parser.getText());
                                    b_trgterIndvdlArray = false;
                                    Log.d("trgterIndvdlArray", parser.getText().trim());
                                } else {
                                    serviceDetail.setTrgterIndvdlArray(BitmapConverter.nullToSpace(parser.getText()));
                                }
                            }

                            if (b_applmetList) {
                                b_applmetList = false;
                            }

                            if (b_servSeCode) {
                                b_servSeCode = false;
                            }

                            if (b_servSeDetailLink) {
                                if (parser.getText() != null) {
                                    WayTextView.setText(parser.getText());
                                    way = parser.getText();

                                    place.add(way);

                                    b_servSeDetailLink = false;
                                }
                            }

                            if (b_servSeDetailNm) {
                                b_servSeDetailNm = false;
                            }

                            break;
                    }
                    eventType = parser.next();
                }

                Log.d("place_list", String.valueOf(place));

                WayTextView.setText(place.get(0));
                PhoneTextView.setText(place.get(4));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;

            case R.id.alarm_menu:
                builder = new AlertDialog.Builder(this);
                builder.setMessage("해당 서비스로 알림설정을 하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", (dialogInterface, which) ->
                                Toast.makeText(getApplicationContext(), "서비스 알림 신청 완료", Toast.LENGTH_SHORT).show())
                        .setNegativeButton("No", (dialogInterface, which) -> dialogInterface.cancel());
                AlertDialog alert = builder.create();
                alert.setTitle("알림설정");
                alert.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alarm_menu, menu);
        return true;
    }
}

class ServiceDetail {
    private String servId;
    private String servNm;
    private String jurMnofNm;
    private String tgtrDtlCn;
    private String slctCritCn;
    private String alwServCn;
    private String lifeArray;
    private String trgterIndvdlArray;
    private String applmetList;

    public ServiceDetail() {

    }

    public String getServId() {
        return servId;
    }

    public void setServId(String servId) {
        this.servId = servId;
    }

    public String getServNm() {
        return servNm;
    }

    public void setServNm(String servNm) {
        this.servNm = servNm;
    }

    public String getJurMnofNm() {
        return jurMnofNm;
    }

    public void setJurMnofNm(String jurMnofNm) {
        this.jurMnofNm = jurMnofNm;
    }

    public String getTgtrDtlCn() {
        return tgtrDtlCn;
    }

    public void setTgtrDtlCn(String tgtrDtlCn) {
        this.tgtrDtlCn = tgtrDtlCn;
    }

    public String getSlctCritCn() {
        return slctCritCn;
    }

    public void setSlctCritCn(String slctCritCn) {
        this.slctCritCn = slctCritCn;
    }

    public String getAlwServCn() {
        return alwServCn;
    }

    public void setAlwServCn(String alwServCn) {
        this.alwServCn = alwServCn;
    }

    public String getLifeArray() {
        return lifeArray;
    }

    public void setLifeArray(String lifeArray) {
        this.lifeArray = lifeArray;
    }

    public String getTrgterIndvdlArray() {
        return trgterIndvdlArray;
    }

    public void setTrgterIndvdlArray(String trgterIndvdlArray) {
        this.trgterIndvdlArray = trgterIndvdlArray;
    }
}