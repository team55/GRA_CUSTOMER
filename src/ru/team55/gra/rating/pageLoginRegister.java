package ru.team55.gra.rating;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.googlecode.androidannotations.annotations.*;
import ru.team55.gra.api.users.UserAccounts;
import ru.team55.gra.api.users.UserAccountsCallback;


@Fullscreen
@EActivity(R.layout.page_login)
public class pageLoginRegister extends ActionBarActivity implements UserAccountsCallback {

    private static int[] currentColors = {0xFF666666, 0xFF96AA39, 0xFFC74B46};
    private static String[] names = {"Вход", "Регистрация", "Забыли пароль?"};

    MyFragmentPagerAdapter adapter;
    @ViewById ViewPager pager;
    @ViewById PagerSlidingTabStrip tabs;


    //TODO: перенести в основную форму
    //@ViewById(R.id.login_form)           View mLoginFormView;
    @ViewById(R.id.login_status)         View mLoginStatusView;
    @ViewById(R.id.login_status_message)
    TextView mLoginStatusMessageView;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        showProgress(false);
    }


    @AfterViews void InitPages(){

        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);




        //TODO: DymmyListener добавить


        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                Log.w("TEST", "tabs onPageScrolled");
                //changeColor(currentColors[pager.getCurrentItem()]);
            }

            @Override
            public void onPageSelected(int i) {
                Log.w("TEST", "tabs onPageSelected");
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                Log.w("TEST", "tabs onPageScrollStateChanged");
            }
        });

        //changeColor(currentColors[0]);
    }


    public void onColorClicked(View v) {

        int color = Color.parseColor(v.getTag().toString());
        changeColor(color);

    }


    //=================================================================================================================
    //ХАКИ С ПАНЕЛЬЮ И ПРОЧЕЕ (не понятно как будет работать с шерлоком)
    //=================================================================================================================


    private Drawable oldBackground = null;
    private void changeColor(int newColor) {

        tabs.setIndicatorColor(newColor);

        // change ActionBar color just if an ActionBar is available


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
            LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

            if (oldBackground == null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(ld);
                }

            } else {

                TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

                // workaround for broken ActionBarContainer drawable handling on
                // pre-API 17 builds
                // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(td);
                }

                td.startTransition(200);

            }

            oldBackground = ld;

            // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);

        }



        //currentColor = newColor;

    }



    private final Handler handler = new Handler();
    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };



    //=================================================================================================================
    //АДАПТЕР
    //=================================================================================================================

    private static class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {

            //return PageFragment.newInstance("My Message " + index);
            switch (index){

                case 1:
                    return new fragmentRegister_();

                case 2:
                    return new fragmentResetPassword_();

                default:
                    return new fragmentLogin_();

            }

        }

        @Override
        public int getCount() {
            return 3;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return names[position];
        }
    }


    //=================================================================================================================
    //ЛОГИКА АВТОРИЗАЦИИ
    //=================================================================================================================

    @Override
    public void onSuccess() {
        showProgress(false);
        finish();
    }

    @Override
    public void onError(int code, String Message) {
        showProgress(false);

        //callback
        //password.setError(getString(R.string.error_incorrect_password));
        //password.requestFocus();
        showNetworkErrorDialog(Message);
    }

    private boolean attempted = false;
    public void attemptLogin(String name, String password) {
        if (attempted) return;
        showProgress(true);
        UserAccounts.logOut(); //TODO: ??
        UserAccounts.loginUser(name, password, this);
    }


    public void attemptRegister(String name, String password, String email) {
        if (attempted) return;
        showProgress(true);
        UserAccounts.signUpUser(name, password, email, this);
    }

    public void attemptResetPassword(String email) {
        if (attempted) return;
        showProgress(true);
        UserAccounts.resetPassword(email, this);
    }


    @UiThread
    void showNetworkErrorDialog(String message){

        new AlertDialog
                .Builder(this)
                .setCancelable(false)
                .setTitle("Ошибка авторизации")
                .setMessage(message)
                .setPositiveButton("Понятно", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    //=================================================================================================================
    //ПРОГРЕССОР
    //=================================================================================================================
    @UiThread
    public void showProgress(final boolean show){
        setSupportProgressBarIndeterminateVisibility(show);
    }
/*

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            //mLoginFormView.setVisibility(View.VISIBLE);
            */
/*mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });*//*

        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


*/


}