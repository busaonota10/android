package br.com.busaonota10.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.busaonota10.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FeedbackCreatedActivity extends AppCompatActivity {

    private String shareUrl;

    @Bind(R.id.text_view_feedback_content)
    TextView feedbackContent;

    @Bind(R.id.text_view_feedback_number_bus)
    TextView feedbackNumberBus;

    @Bind(R.id.text_view_feedback_number_line)
    TextView feedbackNumberLine;

    @Bind(R.id.text_view_feedback_number_date)
    TextView feedbackNumberDate;

    @Bind(R.id.text_view_feedback_text_date)
    TextView feedbackTextDate;

    @Bind(R.id.image_view_feedback_opinion)
    ImageView feedbackImage;

    @Bind(R.id.image_view_maps)
    ImageView imageMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_created);
        ButterKnife.bind(this);
        String feedback = getIntent().getStringExtra("feedback");

        try {
            JSONObject feedbackJSON = new JSONObject(feedback);
            buildInterface(feedbackJSON);
            shareUrl = feedbackJSON.getString("url");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void buildInterface(JSONObject responseJSON) {
        try {
            feedbackContent.setText(responseJSON.getString("content"));
            JSONObject bus = responseJSON.getJSONObject("bus");
            feedbackNumberBus.setText(bus.getString("identification_number"));
            feedbackNumberLine.setText(bus.getString("line"));
            if (responseJSON.getString("opinion").equals("good")) {
                feedbackImage.setImageResource(R.drawable.smile_happy_label);
            } else {
                feedbackImage.setImageResource(R.drawable.smile_sad_label);
            }

            JSONObject date = responseJSON.getJSONObject("sent_at");
            feedbackNumberDate.setText(date.getString("day"));
            feedbackTextDate.setText(date.getString("day_name"));

            Picasso.with(this)
                    .load(responseJSON.getString("image_maps"))
                    .placeholder(R.drawable.map_fallback)
                    .error(R.drawable.map_fallback)
                    .into(imageMaps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Acabei de enviar um feedback no Busão Nota 10: " + shareUrl;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Busão Nota 10");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Compartilhar via"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.action_share:
                share();
                break;
            case R.id.action_about_us:
                redirectToBrowser();
                break;
            case R.id.action_new_feedback:
                intent = new Intent(FeedbackCreatedActivity.this, FeedbackActivity.class);
                finish();
                startActivity(intent);
                break;
            case R.id.action_review_app:
                callRateThisAppIntent();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void callRateThisAppIntent() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    private void redirectToBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.busaonota10.com.br/sobre"));
        startActivity(intent);
    }
}
