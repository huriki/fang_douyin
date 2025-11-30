package com.example.fangdouyin;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private FirstPageFragment mFirstPageFragment;
    private FriendFragment mFriendFragment;
    private NewsFragment mNewsFragment;
    private PublishFragment mPublishFragment;
    private MineFragment mMineFragment;

    private BottomNavigationView mboBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        //初始化控件
        mboBottomNavigationView = findViewById(R.id.main_bottom);
        //main_bottom_nv tab点击切换
        mboBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.front_page) {
                    selectedFragment(0);
                }  else if (item.getItemId() == R.id.friend) {
                    selectedFragment(1);
                }   else if (item.getItemId() == R.id.pubish) {
                    selectedFragment(2);
                }   else if (item.getItemId() == R.id.news) {
                    selectedFragment(3);
                }   else if (item.getItemId() == R.id.mine) {
                    selectedFragment(4);
                }

                return true;
            }
        });
        //默认首页选中
        selectedFragment(0);
    }

    public void selectedFragment(int position) {
        //获取fragmentManager管理器
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);

        //若需要的fragment=null，声明初始化
        if (position == 0) {
            if (mFirstPageFragment == null) {
                mFirstPageFragment = new FirstPageFragment();
                transaction.add(R.id.content, mFirstPageFragment);
            } else {
                transaction.show(mFirstPageFragment);
            }
        } else if (position == 1) {
            if (mFriendFragment == null) {
                mFriendFragment = new FriendFragment();
                transaction.add(R.id.content, mFriendFragment);
            } else {
                transaction.show(mFriendFragment);

            }
        } else if (position == 2) {
            if (mPublishFragment == null) {
                mPublishFragment = new PublishFragment();
                transaction.add(R.id.content, mPublishFragment);
            } else {
                transaction.show(mPublishFragment);

            }
        } else if (position == 3) {
            if (mNewsFragment == null) {
                mNewsFragment = new NewsFragment();
                transaction.add(R.id.content, mNewsFragment);
            } else {
                transaction.show(mNewsFragment);

            }
        } else if (position == 4) {
            if (mMineFragment == null) {
                mMineFragment = new MineFragment();
                transaction.add(R.id.content, mMineFragment);
            } else {
                transaction.show(mMineFragment);

            }
        }

        //最后设置提交     注意：这句话一定不能少！！！！！！
        transaction.commit();

    }

    //将所有存在的fragment加载隐藏
    private void hideFragment(FragmentTransaction transaction) {
        if (mFirstPageFragment != null) {
            transaction.hide(mFirstPageFragment);
        }
        if (mFriendFragment != null) {
            transaction.hide(mFriendFragment);
        }
        if (mPublishFragment != null) {
            transaction.hide(mPublishFragment);
        }
        if (mNewsFragment != null) {
            transaction.hide(mNewsFragment);
        }
        if (mMineFragment != null) {
            transaction.hide(mMineFragment);
        }
    }

}