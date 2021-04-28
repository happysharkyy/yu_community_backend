package com.douyuehan.doubao;

import com.douyuehan.doubao.model.entity.Behavior;
import com.douyuehan.doubao.model.entity.BehaviorSimilarity;
import com.douyuehan.doubao.service.IBehaviorService;
import com.douyuehan.doubao.service.IBehaviorSimilarityService;
import com.douyuehan.doubao.service.IBmsPostService;
import com.douyuehan.doubao.utils.test.RecommendUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DoubaoApplicationTests {

    @Autowired
    IBehaviorService iBehaviorService;

    @Autowired
    IBehaviorSimilarityService userSimilarityService;

    @Autowired
    IBmsPostService productService;
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

