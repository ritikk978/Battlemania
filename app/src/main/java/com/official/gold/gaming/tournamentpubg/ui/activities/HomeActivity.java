//For show tab in main screen
package com.official.gold.gaming.tournamentpubg.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import com.official.gold.R;
import com.official.gold.gaming.tournamentpubg.ui.adapters.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    Boolean doubleBackToExitPressedOnce = false;
    int n = 1;
    private int[] tabIcons = {
            R.drawable.earn,
            R.drawable.battlegame,
            R.drawable.accounticon,
            R.drawable.earndollar
    };
    TabLayout tabs;
    SharedPreferences sp;
    View view1;
    View view2;
    View view3;
    ImageView tabImageView1;
    TextView tabtextview1;
    ImageView tabImageView2;
    TextView tabtextview2;
    ImageView tabImageView3;
    TextView tabtextview3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);

        SharedPreferences preferences1 = getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = preferences1.edit();
        editor1.clear();
        editor1.commit();

        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setSelectedTabIndicatorHeight(0);

        setupTabIcons();

        try {
            Intent intent = getIntent();
            String N = intent.getStringExtra("N");
            n = Integer.parseInt(N);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        viewPager.setCurrentItem(n);
        if (tabs.getTabAt(0).isSelected()) {
            TextView tv = (TextView) tabs.getTabAt(0).getCustomView().findViewById(R.id.tabtextview);
            tv.setTextColor(getResources().getColor(R.color.newblack));
            ImageView iv = (ImageView) tabs.getTabAt(0).getCustomView().findViewById(R.id.tabimageview);
            iv.getDrawable().setColorFilter(getResources().getColor(R.color.newblack), PorterDuff.Mode.SRC_IN);
        }
    }
    private void setupTabIcons() {

        view1 = getLayoutInflater().inflate(R.layout.custom_tab_layout, null);
        tabImageView1 = (ImageView) view1.findViewById(R.id.tabimageview);
        tabtextview1 = (TextView) view1.findViewById(R.id.tabtextview);
        tabtextview1.setText("Earn");
        SharedPreferences spc = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        String selectedcurrency = spc.getString("currency", "₹");
        if (TextUtils.equals(selectedcurrency, "₹")) {
            tabImageView1.setImageDrawable(getDrawable(tabIcons[0]));
            tabImageView1.setTag(getDrawable(tabIcons[0]));
        } else if (TextUtils.equals(selectedcurrency, "$")) {
            tabImageView1.setImageDrawable(getDrawable(tabIcons[3]));
            tabImageView1.setTag(getDrawable(tabIcons[3]));
        }

        view2 = getLayoutInflater().inflate(R.layout.custom_tab_layout, null);
        tabImageView2 = (ImageView) view2.findViewById(R.id.tabimageview);
        tabtextview2 = (TextView) view2.findViewById(R.id.tabtextview);
        tabtextview2.setText("Play");
        tabImageView2.setImageDrawable(getDrawable(tabIcons[1]));
        tabImageView2.setTag(getDrawable(tabIcons[1]));

        view3 = getLayoutInflater().inflate(R.layout.custom_tab_layout, null);
        tabImageView3 = (ImageView) view3.findViewById(R.id.tabimageview);
        tabtextview3 = (TextView) view3.findViewById(R.id.tabtextview);
        tabtextview3.setText("Account");
        tabImageView3.setImageDrawable(getDrawable(tabIcons[2]));
        tabImageView3.setTag(getDrawable(tabIcons[2]));


        tabs.getTabAt(0).setCustomView(view1);
        tabs.getTabAt(1).setCustomView(view2);
        tabs.getTabAt(2).setCustomView(view3);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                TextView tv = (TextView) tab.getCustomView().findViewById(R.id.tabtextview);
                tv.setTextColor(getResources().getColor(R.color.newblack));
                ImageView iv = (ImageView) tab.getCustomView().findViewById(R.id.tabimageview);
                iv.getDrawable().setColorFilter(getResources().getColor(R.color.newblack), PorterDuff.Mode.SRC_IN);

                sp = getSharedPreferences("tabinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("selectedtab", String.valueOf(tab.getPosition()));
                editor.apply();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tv = (TextView) tab.getCustomView().findViewById(R.id.tabtextview);
                tv.setTextColor(Color.WHITE);
                ImageView iv = (ImageView) tab.getCustomView().findViewById(R.id.tabimageview);
                iv.getDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                TextView tv = (TextView) tab.getCustomView().findViewById(R.id.tabtextview);
                tv.setTextColor(getResources().getColor(R.color.newblack));
                ImageView iv = (ImageView) tab.getCustomView().findViewById(R.id.tabimageview);
                iv.getDrawable().setColorFilter(getResources().getColor(R.color.newblack), PorterDuff.Mode.SRC_IN);

            }
        });

    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1500);
    }
}