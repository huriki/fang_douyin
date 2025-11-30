package com.example.fangdouyin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class MineFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private GridLayoutManager layoutManager;
    private RecyclerGridAdapter adapter;

    //接收本地记录的卡片数据
    private List<RecyclerGridBean> datas = new ArrayList<>();

    //接收本地数据库的记录
    private List<item_card> itemCards = new ArrayList<>();
    private item_cardDatabase db;
    private item_cardDao itemCardDao;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

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
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        //构造测试数据
        for (int i=0;i<48;i++){
            RecyclerGridBean bean = new RecyclerGridBean();

            bean.setLikeNum(1234);
            bean.setImageId(res[i% res.length]);

            datas.add(bean);
        }\

         */

        //初始化网格内容
        View rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        mRecyclerView = rootView.findViewById(R.id.mine_recyclerview);
        layoutManager = new GridLayoutManager(getContext(),3);
        mRecyclerView.setLayoutManager(layoutManager);
        //绑定数据，暂时性的为空列表
        adapter = new RecyclerGridAdapter(getActivity(),datas);
        mRecyclerView.setAdapter(adapter);

        //获取单例数据库对象
        db = item_cardDatabase.getDataBase(requireContext());
        //获取表接口
        itemCardDao = db.getItemCardDao();

        //将数据库列表信息加载到网格卡片中，并使用LiveData同步数据库和网格数据
        observeDatabaseData();

        return rootView;
    }

    private void observeDatabaseData() {
        // 观察 LiveData 数据源
        itemCardDao.getAllItem().observe(getViewLifecycleOwner(), new Observer<List<item_card>>(){
            @Override
            public void onChanged(List<item_card> newItemCards) {
                // 新数据回调（主线程执行，安全操作 UI）
                if (newItemCards == null) {
                    newItemCards = new ArrayList<>(); // 避免空指针
                }
                // 1. 清空原有 datas（避免数据重复）
                datas.clear();

                // 2. 同步新数据到 datas（转换 item_card → RecyclerGridBean）
                for (item_card item : newItemCards) {
                    RecyclerGridBean bean = new RecyclerGridBean();
                    bean.setLikeNum(item.getLikeNum());
                    bean.setImageId(item.getImageLocalPath()); // 本地图片路径
                    datas.add(bean);
                }

                // 3. 刷新 adapter（主线程安全）
                //卡片数量较少，直接刷新全部数据
                adapter.notifyDataSetChanged();
            }
        });
    }
}