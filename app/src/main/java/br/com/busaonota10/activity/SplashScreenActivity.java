package br.com.busaonota10.activity;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;

import br.com.busaonota10.fragment.intro.FirstIntroFragment;
import br.com.busaonota10.fragment.intro.FourIntroFragment;
import br.com.busaonota10.fragment.intro.SecondIntroFragment;
import br.com.busaonota10.fragment.intro.ThirdIntroFragment;

public class SplashScreenActivity extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new FirstIntroFragment());
        addSlide(new SecondIntroFragment());
        addSlide(new ThirdIntroFragment());
        addSlide(new FourIntroFragment());

        setSkipText("Pular");
        setDoneText("Começar");
    }

    @Override
    public void onSkipPressed() {
        finish();
        redirectToFeedbackActivity();
    }

    @Override
    public void onDonePressed() {
        finish();
        redirectToFeedbackActivity();
    }

    private void redirectToFeedbackActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, FeedbackActivity.class);
        startActivity(intent);
    }
}
