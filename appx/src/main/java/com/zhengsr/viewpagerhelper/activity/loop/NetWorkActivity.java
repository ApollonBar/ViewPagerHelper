package com.zhengsr.viewpagerhelper.activity.loop;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.zhengsr.viewpagerhelper.R;
import com.zhengsr.viewpagerhelper.bean.ArticleData;
import com.zhengsr.viewpagerhelper.bean.BannerBean;
import com.zhengsr.viewpagerhelper.bean.BaseResponse;
import com.zhengsr.viewpagerhelper.bean.PageDataInfo;
import com.zhengsr.viewpagerhelper.rx.HttpCreate;
import com.zhengsr.viewpagerhelper.rx.RxUtils;
import com.zhengsr.viewpagerhelper.view.BannerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class NetWorkActivity extends AppCompatActivity {
    private static final String TAG = "NetWorkActivity";
    private static final String WANANDROID_BANNER = "https://www.wanandroid.com/banner/json";

    private RecyclerView mRecyclerView;
    private List<ArticleData> mArticleBeans = new ArrayList<>();
    private ArticleAdapter mAdapter;
    private BannerView mBannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        initView();
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new ArticleAdapter(R.layout.item_article_recy_layout, mArticleBeans);

        mBannerView = new BannerView(this);

        mAdapter.addHeaderView(mBannerView);
        mRecyclerView.setAdapter(mAdapter);

        loadData();

    }

    public void update(View view) {
        loadData();
    }

    private boolean isSecond;
    private void loadData() {
        //banner



        HttpCreate.getServer().getBanner()
                .compose(RxUtils.<BaseResponse<List<BannerBean>>>rxScheduers())
                .compose(RxUtils.<List<BannerBean>>handleResult())
                .subscribe(new Observer<List<BannerBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<BannerBean> bannerBeans) {

                        mBannerView.setData(bannerBeans);
                        mAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "zsr - onError: "+e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });


        HttpCreate.getServer().getArticle(0)
                .compose(RxUtils.<BaseResponse<PageDataInfo<List<ArticleData>>>>rxScheduers())
                .compose(RxUtils.<PageDataInfo<List<ArticleData>>>handleResult())
                .subscribe(new Observer<PageDataInfo<List<ArticleData>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PageDataInfo<List<ArticleData>> listPageDataInfo) {
                        mAdapter.setNewData(listPageDataInfo.getDatas());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }




    public class ArticleAdapter extends BaseQuickAdapter<ArticleData, BaseViewHolder> {


        public ArticleAdapter(int layoutResId, @Nullable List<ArticleData> data) {
            super(layoutResId, data);
        }


        @Override
        protected void convert(BaseViewHolder helper, ArticleData item) {
            String msg;
            if (!TextUtils.isEmpty(item.getSuperChapterName())){
                msg = item.getSuperChapterName()+"/"+item.getChapterName();
            }else{
                msg = item.getChapterName();
            }
            String author = (item.getAuthor() != null && item.getAuthor().length() > 0) ? item.getAuthor():item.getShareUser();
            helper.setText(R.id.item_article_author,author)
                    .setText(R.id.item_article_chapat, msg)
                    .setText(R.id.item_article_title,item.getTitle())
                    .setText(R.id.item_article_time,item.getNiceDate());

        }


    }
}
