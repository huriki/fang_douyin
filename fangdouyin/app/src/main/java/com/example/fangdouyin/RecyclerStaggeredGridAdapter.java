package com.example.fangdouyin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fangdouyin.RecyclerStaggeredGridBean;


import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclerStaggeredGridAdapter extends RecyclerView.Adapter<RecyclerStaggeredGridAdapter.CustomViewHolder> {
    //adapter类中，存储的卡片数据即FirstPageFragmnet中的内容，指向同一对象
    private List<RecyclerStaggeredGridBean> datas;
    private Context context;
    private int radius;
    private int itemWidth;

    private item_cardDatabase db;
    private item_cardDao itemCardDao;
    int maxRetryCount = 2; // 最大重试次数（根据需求调整，建议 1-2 次）
    private ItemCardApiService apiService = RetrofitClient.getApiService();
    private ImageSaveManager imageSaveManager; // 图片保存工具类


    //private OnItemClickListener itemClickListener;
    //private OnMoreClickListener moreClickListener;

    public RecyclerStaggeredGridAdapter(Context context, List<RecyclerStaggeredGridBean> datas){
        this.context = context;
        this.datas = datas;

        //获取单张卡片的宽度
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        itemWidth = (screenWidth-20-20)/2;

        // 初始化图片存储工具类
        imageSaveManager = ImageSaveManager.getInstance(context);

        db = item_cardDatabase.getDataBase(context);

        // 2. 获取 DAO 对象
        itemCardDao = db.getItemCardDao();

    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.adapter_recyclestaggeredgrid,
                parent,false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerStaggeredGridAdapter.CustomViewHolder holder,
                                 int position) {
        //RecyclerStaggeredGridAdapter.CustomViewHolder holder:所绑定的单个卡片组件
        //int position:确定卡片位置
        //获得对应List<>中的卡片数据
        RecyclerStaggeredGridBean bean = datas.get(position);
        holder.nameTxt.setText(bean.getName());
        holder.likeNumTxt.setText(bean.getLikeNum());
        holder.titleTxt.setText(bean.getTitle());
        //根据是否已经点赞的状态，加载对应心形图片
        if (bean.getIsLiked()) {
            holder.likeImage.setImageResource(R.drawable.heart_filled);
        } else {
            holder.likeImage.setImageResource(R.drawable.heart_empty);
        }
        //设置点赞图标的点击事件
        holder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 获取当前 Item 的数据
                //RecyclerStaggeredGridBean currentItem = datas.get(holder.getAdapterPosition());

                if (!bean.isPostImageLoaded() || !bean.isAvatarLoaded()) {
                    Toast.makeText(context, "图片正在加载中，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2. 反转点赞状态
                boolean newState = !bean.getIsLiked();
                bean.setIsLiked(newState);

                // 3. 更新点赞数及相关消息
                if (newState) {
                    //list<>对应的卡片点赞数+1
                    bean.incrementLikeCount();

                    //服务器端对应记录的点赞数+1
                    updateCount(bean.getItemId(),"add");

                    //本地数据库接口类数据初始化
                    //初始化中不添加记录本地图片存储地址，等后续异步缓存完图片后再添加
                    item_card itemCard = new item_card(
                            bean.getItemId(),
                            bean.getTitle(),
                            bean.getName(),
                            bean.getLongLikeNum(),
                            bean.getImageId(),
                            bean.getImageWidth(),
                            bean.getImageHeight(),
                            bean.getHeadId()
                    );

                    //将瀑布流卡片中内容图片和头像图片异步存储，并最后记录到本地数据库中
                    //执行流程：存储内容图片->存储头像图片->本地数据库中添加该条记录
                    // 步骤1：保存内容图片
                    String postLocalPath = imageSaveManager.getPostImageLocalPath(bean.getItemId());
                    imageSaveManager.saveImageFromImageView(
                            holder.imageView,
                            postLocalPath,
                            new ImageSaveManager.SaveCallback() {
                                @Override
                                public void onSuccess(String savedPostPath) {
                                    bean.setLocalPostImagePath(savedPostPath);//内容图片的存储地址同步到list<>中
                                    itemCard.setImageLocalPath(savedPostPath);//内容图片的存储地址同步到数据库接口类中
                                    Log.d("SaveImage","图片存储地址："+savedPostPath);


                                    // 步骤2：保存头像
                                    String avatarLocalPath = imageSaveManager.getAvatarLocalPath(bean.getItemId());
                                    imageSaveManager.saveImageFromImageView(
                                            holder.headView,
                                            avatarLocalPath,
                                            new ImageSaveManager.SaveCallback(){
                                                @Override
                                                public void onSuccess(String savedAvatarPath) {
                                                    // 头像保存成功，更新本地路径
                                                    bean.setLocalAvatarPath(savedAvatarPath);//头像图片的存储地址同步到list<>中
                                                    itemCard.setAvatarLocalPath(savedAvatarPath);//头像图片的存储地址同步到数据库接口类中
                                                    Log.d("SaveImage","头像存储地址："+savedAvatarPath);

                                                    // 步骤3：数据入库
                                                    insert(itemCard);

                                                }
                                                @Override
                                                public void onFailure(String errorMsg) {
                                                    //Toast.makeText(context, "头像保存失败：" + errorMsg, Toast.LENGTH_SHORT).show();
                                                    Log.e("SaveImage","头像保存失败：" + errorMsg);
                                                }

                                            }
                                    );
                                }
                                @Override
                                public void onFailure(String errorMsg) {
                                    //Toast.makeText(context, "内容图保存失败：" + errorMsg, Toast.LENGTH_SHORT).show();
                                    Log.e("SaveImage","内容图保存失败：" + errorMsg);
                                }

                            }
                    );



                } else {
                    //list<>对应的卡片点赞数-1
                    bean.decrementLikeCount();

                    //服务器端对应记录的点赞数-1
                    updateCount(bean.getItemId(),"sub");

                    //可以考虑再取消点赞的触发事件中，删除已经缓存的图片
                    //若不删除已经缓存的图片，则ImageSaveManager.saveImageFromImageView()中的图片已经存在的判断，需要响应回调，以便能更新数据，不然没有数据库和内存中的图片路径更新
                    //或者，可以考虑统一的垃圾回收机制

                    //本地数据库接口类数据初始化
                    item_card itemCard = new item_card(
                            bean.getItemId(),
                            bean.getTitle(),
                            bean.getName(),
                            bean.getLongLikeNum(),
                            bean.getImageId(),
                            bean.getImageWidth(),
                            bean.getImageHeight(),
                            bean.getHeadId()
                    );

                    itemCard.setImageLocalPath(bean.getLocalPostImagePath());//内容图片的存储地址赋值到数据库接口类中
                    itemCard.setAvatarLocalPath(bean.getLocalAvatarPath());//头像图片的存储地址赋值到数据库接口类中


                    //数据库删除该条记录
                    delete(itemCard);
                }

                // 4. 更新视图
                holder.likeNumTxt.setText(bean.getLikeNum());
                holder.likeImage.setImageResource(newState ? R.drawable.heart_filled : R.drawable.heart_empty);

                // 5. 调用adapter刷新显示卡片数据
                // 注意：如果你的点赞数格式复杂，或者有其他视图需要联动更新，
                // 可以直接调用 notifyItemChanged，它会重新执行 onBindViewHolder
                notifyItemChanged(position);
            }
        });

        //设置卡片所显示内容图片的高度
        if (bean.isFirst()){
            bean.setShowHeight((int) (bean.getImageHeight()*
                    (1.0f*itemWidth/bean.getImageWidth())));
        }
        holder.imageView.getLayoutParams().height = bean.getShowHeight();


        loadImageWithRetry(holder.headView, bean.getHeadId(), position, "avatar", maxRetryCount);
        loadImageWithRetry(holder.imageView, bean.getImageId(), position, "post", maxRetryCount);
        /*
        Glide.with(context)
              .load(bean.getImageId())
              .into(holder.imageView);

         */
        /*
        Glide.with(context)
                .load(bean.getImageId())
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL) // Glide 自动缓存
                .timeout(15000)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //Toast.makeText(context, "图片加载失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("RetrofitDebug","图片加载失败：" + e.getMessage());
                        Log.e("RetrofitDebug","imageurl：" + bean.getImageId());
                        if (isFirstResource) {
                            Glide.with(holder.itemView.getContext())
                                    .load(bean.getImageId())

                                    .into(holder.imageView);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // 图片加载成功，显示到 ImageView（主线程）
                        Log.d("RetrofitDebug","加载图片");
                        Log.d("RetrofitDebug", "请求失败：1");
                        holder.imageView.setImageDrawable(resource);

                        return false;
                    }
                })


                .into(holder.imageView);


         */
        //holder.position = position;

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
    public void insert(item_card itemCard) {
        Log.d("MyTag","添加该条卡片纪录:");
        Log.d("MyTag","Title:"+itemCard.getTitle());
        Log.d("MyTag","Name:"+itemCard.getName());
        Log.d("MyTag","LikeNum:"+String.valueOf(itemCard.getLikeNum()));
        Log.d("MyTag","ImageId:"+itemCard.getImageId());
        Log.d("MyTag","ImageWidth:"+String.valueOf(itemCard.getImageWidth()));
        Log.d("MyTag","ImageHeight:"+String.valueOf(itemCard.getImageHeight()));
        Log.d("MyTag","HeadId:"+itemCard.getHeadId());
        Log.d("MyTag","ItemId:"+String.valueOf(itemCard.getItemId()));
        Log.d("MyTag","ImageUrl:"+itemCard.getImageLocalPath());
        Log.d("MyTag","AvatarUrl:"+itemCard.getAvatarLocalPath());
        item_cardDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                itemCardDao.insertItem(itemCard);
            }
        });
    }
    public void delete(item_card itemCard) {
        Log.d("MyTag","删除该条卡片纪录:");
        Log.d("MyTag","Title:"+itemCard.getTitle());
        Log.d("MyTag","Name:"+itemCard.getName());
        Log.d("MyTag","LikeNum:"+String.valueOf(itemCard.getLikeNum()));
        Log.d("MyTag","ImageId:"+String.valueOf(itemCard.getImageId()));
        Log.d("MyTag","ImageWidth:"+String.valueOf(itemCard.getImageWidth()));
        Log.d("MyTag","ImageHeight:"+String.valueOf(itemCard.getImageHeight()));
        Log.d("MyTag","HeadId:"+String.valueOf(itemCard.getHeadId()));
        Log.d("MyTag","ItemId:"+String.valueOf(itemCard.getItemId()));
        Log.d("MyTag","ImageUrl:"+itemCard.getImageLocalPath());
        Log.d("MyTag","AvatarUrl:"+itemCard.getAvatarLocalPath());
        item_cardDatabase.databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                itemCardDao.delete(itemCard);
            }
        });
    }
    /*
        public void setItemClickListener(OnItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void setMoreClickListener(OnMoreClickListener moreClickListener) {
            this.moreClickListener = moreClickListener;
        }

        public interface OnItemClickListener{
            void onItemClick(int position);
            void OnMoreClick(int position);
        }
        public interface OnMoreClickListener{
            void OnMoreClick(int position);
        }
    */
    /**
     * Glide 加载图片（带失败重试）
     * @param imageView 目标 ImageView
     * @param imageResId 图片资源ID（网络URL）
     * @param position 指示需要写入的列表datas的位置
     * @param type 标记是内容图片/头像完成加载
     * @param retryCount 剩余重试次数
     */
    private void loadImageWithRetry(ImageView imageView, String imageResId, int position,String type, int retryCount) {
        Glide.with(context)
                .load(imageResId)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .timeout(15000)
                // 可选：添加加载中占位图（提升体验）
                //.placeholder(R.drawable.ic_loading) // 需在 drawable 目录下添加该图片
                // 可选：最终加载失败兜底图
                //.error(R.drawable.ic_load_fail) // 需在 drawable 目录下添加该图片
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("GlideDebug", "图片加载失败（剩余重试次数：" + retryCount + "）：" + e.getMessage());
                        Log.e("GlideDebug", "图片资源ID：" + imageResId);

                        // 重试次数 > 0 时，进行重试
                        if (retryCount > 0) {
                            // 延迟 500ms 重试（避免瞬间重复请求，可选）
                            imageView.postDelayed(() -> {
                                // 重试前清除该图片的内存缓存（关键：避免重复加载失败的缓存）
                                Glide.get(context).clearMemory();
                                // 递归调用，重试次数减1
                                loadImageWithRetry(imageView, imageResId, position, type,retryCount - 1);
                            }, 500);
                        } else {
                            // 重试次数耗尽，显示失败兜底图（也可在这里处理其他逻辑，如Toast提示）
                            Log.e("GlideDebug", "图片加载最终失败");
                            //imageView.setImageResource(R.drawable.ic_load_fail);
                        }
                        return false; // 返回 false，让 Glide 继续处理 error 占位图
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("GlideDebug", "图片加载成功（来源：" + dataSource.name() + "）");
                        imageView.setImageDrawable(resource);

                        if (type == "post") {
                            datas.get(position).setPostImageLoaded();
                        }else if (type == "avatar"){
                            datas.get(position).setAvatarLoaded();
                        }else {
                            Log.e("GlideDebug","传入的标记类型错误");
                        }
                        return false;
                    }
                })
                .into(imageView);
    }

    //更新服务端数据
    private void updateCount(long item_id,String type){
        updateRequest request = new updateRequest(item_id, type);
        apiService.updateImageCount(request).enqueue(new Callback<updateRequest>() {

            @Override
            public void onResponse(Call<updateRequest> call, Response<updateRequest> response) {
                //暂不接收该返回信息
            }

            @Override
            public void onFailure(Call<updateRequest> call, Throwable t) {
                Log.e("update_debug","网络错误：" + t.getMessage());
            }
        });
    }

    //单独负责绑定adapter_recycestaggeredgrid页面中的各组件
    //实现访问该类内变量，即可访问修改卡片中的各组件
    class CustomViewHolder extends RecyclerView.ViewHolder{

        TextView titleTxt;
        TextView nameTxt;
        TextView likeNumTxt;
        ImageView imageView;
        ImageView headView;
        ImageView likeImage;
        //int position;

        public CustomViewHolder(View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.ad_recyclestaggeredgrid_title);
            nameTxt =itemView.findViewById(R.id.ad_recyclestaggeredgrid_name);
            likeNumTxt = itemView.findViewById(R.id.ad_recyclestaggeredgrid_likeNum);
            imageView = itemView.findViewById(R.id.ad_recyclestaggeredgrid_image);
            headView = itemView.findViewById(R.id.ad_recyclestaggeredgrid_head);
            likeImage = itemView.findViewById(R.id.ad_recyclestaggeredgrid_like);
        }
    }
}
