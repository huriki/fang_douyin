# fang_douyin
TikTok研发-国际化生活服务业务- 客户端作业：高仿抖音“经验”频道

实现包含：
  - 瀑布流布局：实现抖音经验页的双列瀑布流界面，支持动态高度适配
  - 经验卡片 UI 组件：包含图片、标题、用户头像、用户名、点赞数等核心元素
  - 点赞交互功能：支持点击点赞图标切换点赞状态，实时更新点赞计数
  - 数据刷新机制：实现上拉加载更多数据和下拉刷新最新内容的功能
  - 数据配置管理：支持 Mock 数据模拟，配置网络图片资源，确保数据可动态更新
  - 预加载优化：支持卡片预加载和图片预加载，提升滑动流畅度
  - 图片缓存：实现图片缓存策略

# 目录/文件说明

## image_mock

  #### get_image.ipynb
    根据百度图片的搜索结果，模拟卡片数据
    使用selenium，分别抓取“拍摄”、“头像”的搜索结果
        “拍摄”的搜索结果中，抓取图片链接、图片标题；“头像”的搜索结果中，抓取图片链接
        利用正则匹配，提取“拍摄”结果链接中的图片高宽
    从字库中随机生成用户名，并随机生成点赞数

    上述数据组成单条卡片内容
      {
      "ItemId": item_id,  # 记录卡片编号（自增）
      "Title": title,  # 标题
      "UserName": generate_username(),  # 用户名
      "LikeNum": like_num,  # 点赞数
      "ImageUrl": item_image_url,  # 内容图片链接
      "ImageWidth": width,  # 内容图片宽度
      "ImageHeight": height,  # 内容图片高度
      "HeadUrl": head_image_url  # 头像图片链接
      }

  #### server.py
    使用Flask搭建简易数据服务器

    http://localhost:20000/ #服务器端口，同一网段的客户端访问时根据服务器IP替换localhost
    
    访问接口：
        "/api/itemcard"  # 接口添加 count 参数（可选，默认单次返回10条数据，客户端请求时传入，如 /api/itemcard?count=5） 
        "/api/find"  # 接口添加 id 参数（必选，客户端请求时传入，如 /api/find?id=5）
        "/api/update"  # post类型，传入json数据，{ "item_id": item_card ID（必传）,"operation": 操作类型：add/sub（必传）}

    接口功能：
        "/api/itemcard"：返回特定数量的卡片数据（默认10条，且每次返回的数据不重复，且假设返回的都是客户端没有点赞浏览过的数据
        "/api/find"：根据指定的卡片id，返回该条卡片数据
        "/api/update"：指定的id卡片的数据，根据操作类型"add"或者"sub"，对该条卡片的点赞数+1或者-1

## fangdouyin
  客户端代码，使用java编写

  ### 布局与功能：
  
  #### 首页：
  <img width="410" height="911" alt="截屏2025-11-30 13 19 46" src="https://github.com/user-attachments/assets/b509987a-2f0f-4dfe-a252-0808bb2cfbcb" />
  
    瀑布流布局：抖音经验页的双列瀑布流界面，支持动态高度适配
    数据刷新机制：上拉加载更多数据和下拉刷新最新内容的功能
    点赞交互功能：支持点击点赞图标切换点赞状态，
                实时更新点赞计数，并能与将点赞数的增减同步到服务器端
                同时点赞后的卡片图片，能实时传递显示到“我”的页面
                
  #### 我：
  <img width="411" height="911" alt="截屏2025-11-30 13 23 31" src="https://github.com/user-attachments/assets/73cacda6-60a7-41e9-906c-614509779709" />

    网格布局：固定大小显示点赞的图片内容，左下角显示点赞数
    实时同步：首页中点赞后的图片内容会同步刷新到页面中

# 框架结构
<img width="4464" height="1378" alt="exported_image (1)" src="https://github.com/user-attachments/assets/eba3d0c1-6caf-488f-b177-54b6e3a5e36b" />

# 技术难点及方案
## 1.上拉加载数据的判定

  如何判定滑动到瀑布流的底部，以及如何判定何时加载新的数据

  ###
      //定义下滑瀑布流的缓冲长度，单列计数
      private int itemBuffer = 2;

      
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

  滑动瀑布流时，通过mRecyclerView.addOnScrollListener的方法触发滑动判断：
  在滑动判断中，layoutManager.findLastVisibleItemPositions获得当前屏幕上可见的每一列最后一个 item 的位置，通过与存储瀑布流卡片的数据总长度进行比较
      
        // 判断是否滚动到了列表底部
        // lastVisibleItemPosition >= adapter.getItemCount() - 1 - 2  提前2个item开始加载
        lastVisibleItemPosition >= adapter.getItemCount() - 1 - itemBuffer

    即可判断是否已经滑动到瀑布流底部（包含数据缓冲长度）

## 2.本地图片的地址记录
  点赞图标的触发事件中，需要将瀑布流中相应的图片保存到本地，且获得保存地址，并将图片地址等相关信息全部写入数据库。由于图片保存涉及到异步操作，需协调等待内容图片和头像图片的保存完成，才能执行数据入库的步骤

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


# 使用演示
  ## 服务端
  ### 1.执行image_mock/get_image.ipynb
    
  前提是导入相关包，并布置好Chrome浏览器对应的驱动
  https://developer.chrome.com/docs/chromedriver/downloads/version-selection?hl=zh-cn
  
  <img width="1200" height="1011" alt="截屏2025-11-30 13 47 26" src="https://github.com/user-attachments/assets/6663f126-23cf-40e2-a90e-edf2fb58c007" />
  <img width="911" height="493" alt="截屏2025-11-30 13 50 26" src="https://github.com/user-attachments/assets/26081bd2-db1a-4371-8cd1-d6a1fe040e7f" />
  <img width="1196" height="1007" alt="截屏2025-11-30 14 02 55" src="https://github.com/user-attachments/assets/d2aa76b1-6be5-4674-871b-34bf89811b56" />
  <img width="776" height="499" alt="截屏2025-11-30 14 03 42" src="https://github.com/user-attachments/assets/f65baa06-bd09-4e25-9904-a62b10500bad" />
  <img width="438" height="111" alt="截屏2025-11-30 14 06 23" src="https://github.com/user-attachments/assets/7575b6f2-b366-4847-addc-f68b1eac6c37" />
    
  内容图片的数量需大于等于头像图片的数量，以保证最后数据的完整性

  <img width="809" height="413" alt="截屏2025-11-30 14 06 43" src="https://github.com/user-attachments/assets/e4a101ca-2aef-48a2-b16f-cecc73887b20" />
  
  最后生成的数据存储为json格式供服务器读取

  ### 2.执行image_mock/server.py
  <img width="1018" height="239" alt="截屏2025-11-30 15 40 41" src="https://github.com/user-attachments/assets/7d140964-3bc6-4c6e-a24f-9fad26d7f439" />


  根据Flask返回，可知访问端口为http://192.168.8.218:20000

  ## 客户端
  <img width="795" height="607" alt="截屏2025-11-30 15 12 22" src="https://github.com/user-attachments/assets/a3e96ca9-4ba4-48f5-891f-540a9c346e52" />

  通过android studio打开fangdouyin项目文件

  <img width="1465" height="839" alt="截屏2025-11-30 15 30 16" src="https://github.com/user-attachments/assets/23e837fd-fe68-473f-8ad9-dd6705915eb1" />

  在/fangdouyin/app/src/main/java/com.example.fangdouyin/RetrofitClient.java文件中，修改BASE_URL为上述Flask访问端口
  
      private static final String BASE_URL = "http://192.168.8.218:20000";

<img width="1468" height="838" alt="截屏2025-11-30 15 43 49" src="https://github.com/user-attachments/assets/e76643f6-81d3-4488-9d15-4ae4d7a5a51e" />

  Run'app'构建项目并执行，即可运行客户端

  <img width="1466" height="834" alt="截屏2025-11-30 15 48 51" src="https://github.com/user-attachments/assets/bf9f0e05-0e63-4cae-a920-0a9952eb60f7" />

如需清空数据库中，对于点赞数据的记录，可在/fangdouyin/app/src/main/java/com.example.fangdouyin/item_cardDatabase.java文件中，修改数据库的版本号，数据库的升级或降级会清空数据表，“我”的页面中数据也会相应清除


      @Database(entities = {item_card.class}, version = 3, exportSchema = false)
  
  

  
  
  



    









  

  
  

  
  




    
  
