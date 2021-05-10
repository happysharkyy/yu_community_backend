package com.douyuehan.doubao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.douyuehan.doubao.mapper.BmsTopicMapper;
import com.douyuehan.doubao.model.entity.*;
import com.douyuehan.doubao.model.vo.BmsPostVO01;
import com.douyuehan.doubao.model.vo.PostVO;
import com.douyuehan.doubao.service.*;
import com.douyuehan.doubao.utils.test.RecommendUtils;


import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DoubaoApplicationTests {

    @Autowired
    IBehaviorService iBehaviorService;

    @Autowired
    IBmsPostService iBmsPostService;

    @Autowired
    BmsTopicMapper bmsTopicMapper;


    @Autowired
    IBehaviorSimilarityService userSimilarityService;

    @Autowired
    IBmsPostService productService;

    @Autowired
    IUmsUserService iUmsUserService;

    @Autowired
    IBmsTopicTagService iBmsTopicTagService;

    @Autowired
    IBmsTagService iBmsTagService;

    @Resource
    private RestHighLevelClient client;;

    @org.junit.Test
    public void insert() throws IOException {
        LambdaQueryWrapper<BmsPost> queryWrapper = new LambdaQueryWrapper<>();
        List<BmsPost> list = bmsTopicMapper.selectList(queryWrapper);
        List<BmsPostVO01> result = new ArrayList<>();


        for (BmsPost b1:
                list) {
            BmsPostVO01 bmsPostVO01 = new BmsPostVO01();
            String tagList = "";
            List<BmsTopicTag> topicTagList =  iBmsTopicTagService.selectByTopicId(b1.getId());
            for (BmsTopicTag bmsTopicTag:
                    topicTagList) {
                tagList += iBmsTagService.getById(bmsTopicTag.getTagId()).getName()+"---";
            }
            BeanUtils.copyProperties(b1,bmsPostVO01);
            bmsPostVO01.setUsername(iUmsUserService.getById(b1.getUserId()).getUsername());
            bmsPostVO01.setTagList(tagList);
            result.add(bmsPostVO01);

        }


//保存到es
        // 1.在es中建立索引，product,建立好映射关系

        // 2、给es中保存这些数据
        BulkRequest bulkRequest = new BulkRequest();
        result.forEach(v -> {
            // 保存到那个索引
            IndexRequest indexRequest = new IndexRequest("result_list");
            // 设置唯一id字段
            indexRequest.id(v.getId().toString());
            // 转成json字符串
            String s = JSON.toJSONString(v);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        });
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        // TODO 处理失败信息
        if( bulk.hasFailures()) {
            List<String> collect = Arrays.stream(bulk.getItems())
                    .map(v -> v.getId())
                    .collect(Collectors.toList());

            System.out.println("错误信息id{}"+collect);
        }


    }
    @org.junit.Test
    // 2.获取这些数据实现搜索功能
    public void searchPage() throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("title", "中暑");
        QueryBuilder matchPhraseQueryBuilder1 = QueryBuilders.matchPhraseQuery("tagList", "mysql");
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .should(matchPhraseQueryBuilder)
                .should(matchPhraseQueryBuilder1));
        searchRequest.source(searchSourceBuilder);

        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        // 关闭多个高亮
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        searchSourceBuilder.highlighter(highlightBuilder);

        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        List<BmsPostVO01> playerList = new LinkedList<>();
        for(SearchHit hit: hits){
            BmsPostVO01 player = JSONObject.parseObject(hit.getSourceAsString(),BmsPostVO01.class);

            //解析高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if(title != null){
                Text[] fragments = title.fragments();
                String n_title = "";
                for (Text text : fragments) {
                    n_title += text;
                }
                // 高亮字段替换掉原来的内容即可
                sourceAsMap.put("title",n_title);
                System.out.println("-------------"+sourceAsMap);
            }

            playerList.add(player);
            System.out.println("===========>"+player);
        }

    }
    /**
     * 测试列出所有用户的购买行为的方法
     */
    @org.junit.Test
    public void testListAllUserActive() {
        // 1.查询出所有用户查询操作记录
        List<Behavior> BehaviorList = iBehaviorService.listAllUserActive();

        // 2.输出浏览记录列表
        for (Behavior Behavior : BehaviorList) {
            System.out.println(Behavior.getUserId() + "\t" + Behavior.getPostId() + "\t" + Behavior.getCount());
        }

    }

    /**
     * 测试组装用户行为数据的方法
     */
    @org.junit.Test
    public void testAssembleUserBehavior() {

        // 1.查询所有的用户浏览记录
        List<Behavior> behaviorList = iBehaviorService.listAllUserActive();

        // 2.调用推荐模块工具类的方法组装成一个ConcurrentHashMap来存储每个用户以及其对应的二级类目的点击量
        ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> activeMap = RecommendUtils.assembleUserBehavior(behaviorList);

        // 3.输出封装后的map的大小（也就是多少个用户的浏览记录）
        System.out.println(activeMap.size());
    }

    /**
     * 计算用户的相似度
     */
    @org.junit.Test
    public void testCalcSimilarityBetweenUser() {

        // 1.查询所有的用户浏览记录
        List<Behavior> behaviorList = iBehaviorService.listAllUserActive();

        // 2.调用推荐模块工具类的方法组装成一个ConcurrentHashMap来存储每个用户以及其对应的二级类目的点击量
        ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> activeMap = RecommendUtils.assembleUserBehavior(behaviorList);


        // 3.调用推荐模块工具类的方法计算用户与用户之间的相似度
        List<BehaviorSimilarity> similarityList = RecommendUtils.calcSimilarityBetweenUsers(activeMap);

        // 4.输出计算好的用户之间的相似度
        for (BehaviorSimilarity usim : similarityList) {
            System.out.println(usim.getUserId() + "\t" + usim.getUserRefId() + "\t" + usim.getSimilarity());
            // 5.如果用户之间的相似度已经存在与数据库中就修改，不存在就添加
            if (userSimilarityService.isExistsUserSimilarity(usim)) { // 修改
                boolean flag = userSimilarityService.updateUserSimilarity(usim);
                if (flag) {
                    System.out.println("修改数据成功");
                }
            } else { // 新增
                boolean flag = userSimilarityService.saveUserSimilarity(usim);
                if (flag) {
                    System.out.println("插入数据成功");
                }
            }
        }

    }

    /**
     * 测试查询用户相似度集合列表
     */
    @org.junit.Test
    public void testListUserSimilarity() {
        // 1.查询出某个用户与其他用户的相似度列表
        List<BehaviorSimilarity> userSimilarityList = userSimilarityService.listUserSimilarityByUId("1349618748226658305");

        // 2.打印输出
        for (BehaviorSimilarity behaviorSimilarity : userSimilarityList) {
            System.out.println(behaviorSimilarity.getUserId() + "\t" + behaviorSimilarity.getUserRefId() + "\t" + behaviorSimilarity.getSimilarity());
        }

    }

    /**
     * 测试取出与指定用户相似度最高的前N个用户
     */
    @org.junit.Test
    public void testGetTopNUser() {


        // 1.查询出某个用户与其他用户的相似度列表
        List<BehaviorSimilarity> userSimilarityList = userSimilarityService.listUserSimilarityByUId("1349618748226658305");

        // 2.打印输出
        for (BehaviorSimilarity BehaviorSimilarity : userSimilarityList) {
            System.out.println(BehaviorSimilarity.getUserId() + "\t" + BehaviorSimilarity.getUserRefId() + "\t" + BehaviorSimilarity.getSimilarity());
        }

        // 3.获取与id为2L的用户的浏览行为最相似的前2个用户
        List<String> userIds = RecommendUtils.getSimilarityBetweenUsers("1349618748226658305", userSimilarityList, 5);

        // 4.打印输出
        System.out.println("与1349618748226658305号用户最相似的前5个用户为：");
        for (String userRefId : userIds) {
            System.out.println(userRefId);
        }

    }

    /**
     * 获取被推荐的类目id列表
     */
    @org.junit.Test
    public void testGetRecommendateCategoy2() {
        // 1.查询出某个用户与其他用户的相似度列表
        List<BehaviorSimilarity> userSimilarityList = userSimilarityService.listUserSimilarityByUId("1349618748226658305");

        // 2.获取所有的用户的浏览记录
        List<Behavior> userActiveList = iBehaviorService.listAllUserActive();
        for (BehaviorSimilarity BehaviorSimilarity : userSimilarityList) {
            System.out.println(BehaviorSimilarity.getUserId() + "\t" + BehaviorSimilarity.getUserRefId() + "\t" + BehaviorSimilarity.getSimilarity());
        }

        // 3.找出与id为1L的用户浏览行为最相似的前5个用户
        List<String> userIds = RecommendUtils.getSimilarityBetweenUsers("1349618748226658305", userSimilarityList, 5);
        System.out.println("与1349618748226658305"  + "号用户最相似的前5个用户为：");
        for (String userRefId : userIds) {
            System.out.println(userRefId);
        }

        // 4.获取应该推荐给1L用户的二级类目
        List<String> recommendateCategory2 = RecommendUtils.getRecommendateCategory2("1349618748226658305", userIds, userActiveList);
        for (String category2Id : recommendateCategory2) {
            System.out.println("被推荐的二级类目：" + category2Id);
        }

    }

    /**
     * 测试获取被推荐的商品列表(从被推荐的二级类目找出点击量最大的商品作为推荐的商品)
     */
    @Test
    public void testGetRecommendateProduct() {


        // 1.查询出某个用户与其他用户的相似度列表
        List<BehaviorSimilarity> userSimilarityList = userSimilarityService.listUserSimilarityByUId("1376153606346301441");

        // 2.获取所有的用户的浏览记录
        List<Behavior> userActiveList = iBehaviorService.listAllUserActive();
        for (BehaviorSimilarity BehaviorSimilarity : userSimilarityList) {
            System.out.println(BehaviorSimilarity.getUserId() + "\t" + BehaviorSimilarity.getUserRefId() + "\t" + BehaviorSimilarity.getSimilarity());
        }

        // 3.找出与id为1L的用户浏览行为最相似的前2个用户
        List<String> userIds = RecommendUtils.getSimilarityBetweenUsers("1376153606346301441", userSimilarityList, 5);
        System.out.println("与" + 1 + "号用户最相似的前5个用户为：");
        for (String userRefId : userIds) {
            System.out.println(userRefId);
        }

        // 4.获取应该推荐给1L用户的二级类目
        List<String> recommendateCategory2 = RecommendUtils.getRecommendateCategory2("1376153606346301441", userIds, userActiveList);
        for (String category2Id : recommendateCategory2) {
            System.out.println("被推荐的二级类目：" + category2Id);
        }

        // 5.找出二级类目中的所有商品
//    	List<BmsPost> recommendateProducts = new ArrayList<BmsPost>();
//    	for (String category2Id : recommendateCategory2) {
//    		List<ProductDTO> productList = productService.listProductByCategory2Id(category2Id);
//    		// 找出当前二级类目中点击量最大的商品
//    		Product maxHitsProduct = RecommendUtils.findMaxHitsProduct(productList);
//    		recommendateProducts.add(maxHitsProduct);
//    	}
//
//    	// 6.打印输出
//    	for (Product product : recommendateProducts) {
//    		System.out.println("被推荐的商品：" + product.getProductName());
//    	}
    }
}

