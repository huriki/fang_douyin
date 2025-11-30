package com.example.fangdouyin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirstPageFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerStaggeredGridAdapter adapter;
    private StaggeredGridLayoutManager layoutManager;
    //类内唯一且专门存储卡片数据的列表
    private List<RecyclerStaggeredGridBean> datas = new ArrayList<>();
    private Call<itemCardResponse> call;
    private ItemCardApiService apiService = RetrofitClient.getApiService();
/*
    //本地测试数据
    private int[] res = new int[]{
            R.drawable.staggered1,R.drawable.staggered2,R.drawable.staggered3,
            R.drawable.staggered4,R.drawable.staggered5,R.drawable.staggered6,
            R.drawable.staggered7,R.drawable.staggered8};
    private int[][] imageSizes = new int[][]{
            new int[]{952,597},
            new int[]{607,584},
            new int[]{544,592},
            new int[]{447,670},
            new int[]{612,590},
            new int[]{928,630},
            new int[]{380,570},
            new int[]{558,467}
    };
    private int loadMoreNum = 10;
 */
    // 标记是否正在加载中，防止重复请求
    private boolean isLoading = false;

    //定义下滑瀑布流的缓冲长度，单列计数
    private int itemBuffer = 2;
    //定义单次向服务器获取卡片数量
    private int count = 10;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_first_page, container, false);

        //绑定下拉组件
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);

        //异步加载初始数据
        loadData(count);

        //初始化瀑布流组件
        mRecyclerView = rootView.findViewById(R.id.ac_recyclerview);
        layoutManager = new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //绑定数据
        adapter = new RecyclerStaggeredGridAdapter(getActivity(),datas);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //只需定义dy方向的上下滑动
                if (dy > 0) {
                    StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        // 获取当前屏幕上可见的最后一个 item 的位置，返回每一列的的位置
                        int[] lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null);
                        //直接根据第一列判定
                        int lastVisibleItemPosition = lastVisibleItemPositions[0];

                        // 判断是否滚动到了列表底部
                        // lastVisibleItemPosition >= adapter.getItemCount() - 1 - 2  提前2个item开始加载
                        if (lastVisibleItemPosition >= adapter.getItemCount() - 1 - itemBuffer  && !isLoading) {
                            loadData(count); // 加载更多数据
                        }
                    }
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isLoading) {
                    // 在刷新时执行
                    refreshData();
                }
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);


        return rootView;
    }
    private void refreshData() {
        //int currentlength =datas.size();
        datas.clear();
/*
        for (int i=0;i<currentlength;i++){
            RecyclerStaggeredGridBean bean = new RecyclerStaggeredGridBean();
            bean.setItemId(i);
            bean.setName("下拉用户"+(i+1));
            bean.setLikeNum(1234);
            bean.setTitle("下拉第"+(i+1)+"条标题测试数据");
            bean.setImageId(res[i% res.length]);
            bean.setImageWidth(imageSizes[i% imageSizes.length][0]);
            bean.setImageHeight(imageSizes[i% imageSizes.length][1]);
            datas.add(bean);
        }

 */
        loadData(count);

        //通知 Adapter 数据已更改
        //adapter.notifyDataSetChanged();
        //停止刷新动画
        swipeRefreshLayout.setRefreshing(false);
    }

    //异步加载数据，且在回调中通过adapter刷新新拼接到List<RecyclerStaggeredGridBean> datas的数据
    //int num:指定每次向服务器获取的数据条数
    private void loadData(int num){
        //标记加载的开始
        isLoading = true;
        call = apiService.getAllItem(num);
        call.enqueue(new Callback<itemCardResponse>() {
            @Override
            public void onResponse(Call<itemCardResponse> call, Response<itemCardResponse> response) {
                /*
                // 1. 打印 HTTP 状态码（关键！判断是否真的请求成功）
                Log.d("RetrofitDebug", "状态码：" + response.code());
                // 2. 打印响应头（可选，看是否有异常配置）
                Log.d("RetrofitDebug", "响应头：" + response.headers().toString());
                // 3. 打印原始响应体（不管解析是否成功，先看服务器返回了什么）
                try {
                    String responseBodyStr = response.errorBody() != null ?
                            response.errorBody().string() : response.body().toString();
                    Log.d("RetrofitDebug", "原始响应体：" + responseBodyStr);
                } catch (Exception e) {
                    Log.e("RetrofitDebug", "打印响应体失败", e);
                }
                 */

                if (response.isSuccessful() && response.body() != null) {
                    itemCardResponse itemcardresponse = response.body();
                    if (itemcardresponse.getCode() == 200) {
                        // 添加列表数据
                        int startIndex = datas.size();
                        datas.addAll(itemcardresponse.getData());
                        // 3. 通知 Adapter 数据已更改
                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemRangeInserted(startIndex, num);
                        isLoading = false;
                    } else {
                        isLoading = false;
                        Log.d("RetrofitDebug", "请求失败：1");
                        //Toast.makeText(getContext(), "请求失败：1" , Toast.LENGTH_SHORT).show();
                        //loadData();

                    }
                } else {
                    isLoading = false;
                    Log.d("RetrofitDebug", "请求失败：0");
                    //Toast.makeText(getContext(), "请求失败：0" , Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onFailure(Call<itemCardResponse> call, Throwable t) {
                isLoading = false;
                Log.d("RetrofitDebug", "网络错误：" + t.getMessage());
                //Toast.makeText(getContext(), "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
/*
    private void loadMoreData() {
        isLoading = true;
        int startIndex = datas.size();

        for (int i = startIndex; i < startIndex + loadMoreNum; i++) {
            RecyclerStaggeredGridBean bean = new RecyclerStaggeredGridBean();
            bean.setItemId(i);
            bean.setName("上拉用户"+(i+1));
            bean.setLikeNum(1234);
            bean.setTitle("上拉第"+(i+1)+"条标题测试数据");
            bean.setImageId(res[i% res.length]);
            bean.setImageWidth(imageSizes[i% imageSizes.length][0]);
            bean.setImageHeight(imageSizes[i% imageSizes.length][1]);
            datas.add(bean);

        }
        adapter.notifyItemRangeInserted(startIndex, loadMoreNum);
        isLoading = false;

    }
 */

}