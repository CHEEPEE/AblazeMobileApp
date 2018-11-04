package icandoallthingsthroughchrist10.blazeownerapp.tabbedActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;


import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import icandoallthingsthroughchrist10.blazeownerapp.R;

import icandoallthingsthroughchrist10.blazeownerapp.fragmentAdapter.ViewPagerAdapter;
import icandoallthingsthroughchrist10.blazeownerapp.fragments.FragManageReservationAccounts;
import icandoallthingsthroughchrist10.blazeownerapp.fragments.FragManageCandiddatesTenantsTickets;

public class ManageTenantsAccountsBotNav extends AppCompatActivity {

    private TextView mTextMessage;
    private ViewPagerAdapter adapter;
    private Context context;
    ViewPager viewPager;
    MenuItem prevMenuItem;
    BottomNavigationView navigation;
    Dialog dialog;
    String classKey;
    FragManageCandiddatesTenantsTickets fragManageCandiddatesTenantsTickets;
    FragManageReservationAccounts fragManageReservationAccounts;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tenants_accounts_bot_nav);

        context = ManageTenantsAccountsBotNav.this;
        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
//        classKey = getIntent().getExtras().getString("classKey");
        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }


            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    navigation.getMenu().getItem(0).setChecked(false);

                }

                Log.d("page", "onPageSelected: "+position);
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        setupViewPager(viewPager);
    }
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragManageCandiddatesTenantsTickets = new FragManageCandiddatesTenantsTickets();
        fragManageReservationAccounts = new FragManageReservationAccounts();
        adapter.addFragment(fragManageCandiddatesTenantsTickets);
        adapter.addFragment(fragManageReservationAccounts);

        viewPager.setAdapter(adapter);
    }
}
