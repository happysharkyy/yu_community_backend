//package com.douyuehan.doubao.utils.test;
//
//import com.douyuehan.doubao.model.entity.Behavior;
//import com.douyuehan.doubao.model.entity.BehaviorSimilarity;
//import com.douyuehan.doubao.model.entity.BmsPost;
//import com.douyuehan.doubao.service.IBehaviorService;
//import com.douyuehan.doubao.service.IBehaviorSimilarityService;
//import com.douyuehan.doubao.service.IBmsPostService;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 类描述：测试推荐模块中的一些功能
// * 类名称：com.lyu.shopping.recommendate.test.RecommendateTest
// * @author 曲健磊
// * 2018年3月26日.下午6:57:01
// * @version V1.0
// */
//public class RecommendateTest {
//
//
//    /**
//     * 测试列出所有用户的购买行为的方法
//     */
//    @Test
//    public void testListAllUserActive() {
//        IBehaviorService iBehaviorService = (IBehaviorService) application.getBean("iBehaviorService");
//        // 1.查询出所有用户对所有二级类目的浏览记录
//        List<Behavior> BehaviorList = iBehaviorService.listAllUserActive();
//
//        // 2.输出浏览记录列表
//        for (Behavior Behavior : BehaviorList) {
//            System.out.println(Behavior.getUserId() + "\t" + Behavior.getPostId() + "\t" + Behavior.getCount());
//        }
//
//    }
//
//    /**
//     * 测试组装用户行为数据的方法
//     */
//    @Test
//    public void testAssembleUserBehavior() {
//		IBehaviorService IBehaviorService = (IBehaviorService) application.getBean("IBehaviorService");
//        // 1.查询所有的用户浏览记录
//        List<Behavior> behaviorList = IBehaviorService.listAllUserActive();
//
//        // 2.调用推荐模块工具类的方法组装成一个ConcurrentHashMap来存储每个用户以及其对应的二级类目的点击量
//        ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> activeMap = RecommendUtils.assembleUserBehavior(behaviorList);
//
//        // 3.输出封装后的map的大小（也就是多少个用户的浏览记录）
//        System.out.println(activeMap.size());
//    }
//
//    /**
//     * 计算用户的相似度
//     */
//    @Test
//    public void testCalcSimilarityBetweenUser() {
//        IBehaviorService IBehaviorService = (IBehaviorService) application.getBean("iBehaviorService");
//        IBehaviorSimilarityService userSimilarityService = (IBehaviorSimilarityService) application.getBean("iBehaviorSimilarityService");
//        // 1.查询所有的用户浏览记录
//        List<Behavior> behaviorList = IBehaviorService.listAllUserActive();
//
//        // 2.调用推荐模块工具类的方法组装成一个ConcurrentHashMap来存储每个用户以及其对应的二级类目的点击量
//        ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> activeMap = RecommendUtils.assembleUserBehavior(behaviorList);
//
//        // 3.调用推荐模块工具类的方法计算用户与用户之间的相似度
//        List<BehaviorSimilarity> similarityList = RecommendUtils.calcSimilarityBetweenUsers(activeMap);
//
//        // 4.输出计算好的用户之间的相似度
//        for (BehaviorSimilarity usim : similarityList) {
//            System.out.println(usim.getUserId() + "\t" + usim.getUserRefId() + "\t" + usim.getSimilarity());
//            // 5.如果用户之间的相似度已经存在与数据库中就修改，不存在就添加
//            if (userSimilarityService.isExistsUserSimilarity(usim)) { // 修改
//            	boolean flag = userSimilarityService.updateUserSimilarity(usim);
//            	if (flag) {
//                	System.out.println("修改数据成功");
//                }
//            } else { // 新增
//            	boolean flag = userSimilarityService.saveUserSimilarity(usim);
//                if (flag) {
//                	System.out.println("插入数据成功");
//                }
//            }
//        }
//
//    }
//
//    /**
//     * 测试查询用户相似度集合列表
//     */
//    @Test
//    public void testListUserSimilarity() {
//    	IBehaviorSimilarityService userSimilarityService = (IBehaviorSimilarityService) application.getBean("userSimilarityService");
//        // 1.查询出某个用户与其他用户的相似度列表
//    	List<BehaviorSimilarity> userSimilarityList = userSimilarityService.listUserSimilarityByUId(2L);
//
//    	// 2.打印输出
//    	for (BehaviorSimilarity behaviorSimilarity : userSimilarityList) {
//    		System.out.println(behaviorSimilarity.getUserId() + "\t" + behaviorSimilarity.getUserRefId() + "\t" + behaviorSimilarity.getSimilarity());
//    	}
//
//    }
//
//    /**
//     * 测试取出与指定用户相似度最高的前N个用户
//     */
//    @Test
//    public void testGetTopNUser() {
//
//    	IBehaviorSimilarityService userSimilarityService = (IBehaviorSimilarityService) application.getBean("userSimilarityService");
//    	// 1.查询出某个用户与其他用户的相似度列表
//    	List<BehaviorSimilarity> userSimilarityList = userSimilarityService.listUserSimilarityByUId(2L);
//
//    	// 2.打印输出
//    	for (BehaviorSimilarity BehaviorSimilarity : userSimilarityList) {
//    		System.out.println(BehaviorSimilarity.getUserId() + "\t" + BehaviorSimilarity.getUserRefId() + "\t" + BehaviorSimilarity.getSimilarity());
//    	}
//
//    	// 3.获取与id为2L的用户的浏览行为最相似的前2个用户
//    	List<String> userIds = RecommendUtils.getSimilarityBetweenUsers("1", userSimilarityList, 3);
//
//    	// 4.打印输出
//    	System.out.println("与" + 2 + "号用户最相似的前3个用户为：");
//    	for (String userRefId : userIds) {
//    		System.out.println(userRefId);
//    	}
//
//    }
//
//    /**
//     * 获取被推荐的类目id列表
//     */
//    @Test
//    public void testGetRecommendateCategoy2() {
//    	IBehaviorSimilarityService userSimilarityService = (IBehaviorSimilarityService) application.getBean("userSimilarityService");
//    	IBehaviorService IBehaviorService = (IBehaviorService) application.getBean("IBehaviorService");
//    	// 1.查询出某个用户与其他用户的相似度列表
//    	List<BehaviorSimilarity> userSimilarityList = userSimilarityService.listUserSimilarityByUId(1L);
//
//    	// 2.获取所有的用户的浏览记录
//    	List<Behavior> userActiveList = IBehaviorService.listAllUserActive();
//    	for (BehaviorSimilarity BehaviorSimilarity : userSimilarityList) {
//    		System.out.println(BehaviorSimilarity.getUserId() + "\t" + BehaviorSimilarity.getUserRefId() + "\t" + BehaviorSimilarity.getSimilarity());
//    	}
//
//    	// 3.找出与id为1L的用户浏览行为最相似的前3个用户
//    	List<String> userIds = RecommendUtils.getSimilarityBetweenUsers("1", userSimilarityList, 3);
//    	System.out.println("与" + 1 + "号用户最相似的前3个用户为：");
//    	for (String userRefId : userIds) {
//    		System.out.println(userRefId);
//    	}
//
//    	// 4.获取应该推荐给1L用户的二级类目
//    	List<String> recommendateCategory2 = RecommendUtils.getRecommendateCategory2("1", userIds, userActiveList);
//    	for (String category2Id : recommendateCategory2) {
//    		System.out.println("被推荐的二级类目：" + category2Id);
//    	}
//
//    }
//
//    /**
//     * 测试获取被推荐的商品列表(从被推荐的二级类目找出点击量最大的商品作为推荐的商品)
//     */
//    @Test
//    public void testGetRecommendateProduct() {
//    	IBehaviorSimilarityService userSimilarityService = (IBehaviorSimilarityService) application.getBean("userSimilarityService");
//    	IBehaviorService IBehaviorService = (IBehaviorService) application.getBean("IBehaviorService");
//    	IBmsPostService productService = (IBmsPostService)application.getBean("productService");
//
//    	// 1.查询出某个用户与其他用户的相似度列表
//    	List<BehaviorSimilarity> userSimilarityList = userSimilarityService.listUserSimilarityByUId(1L);
//
//    	// 2.获取所有的用户的浏览记录
//    	List<Behavior> userActiveList = IBehaviorService.listAllUserActive();
//    	for (BehaviorSimilarity BehaviorSimilarity : userSimilarityList) {
//    		System.out.println(BehaviorSimilarity.getUserId() + "\t" + BehaviorSimilarity.getUserRefId() + "\t" + BehaviorSimilarity.getSimilarity());
//    	}
//
//    	// 3.找出与id为1L的用户浏览行为最相似的前2个用户
//    	List<String> userIds = RecommendUtils.getSimilarityBetweenUsers("1", userSimilarityList, 3);
//    	System.out.println("与" + 1 + "号用户最相似的前3个用户为：");
//    	for (String userRefId : userIds) {
//    		System.out.println(userRefId);
//    	}
//
//    	// 4.获取应该推荐给1L用户的二级类目
//    	List<String> recommendateCategory2 = RecommendUtils.getRecommendateCategory2("1", userIds, userActiveList);
//    	for (String category2Id : recommendateCategory2) {
//    		System.out.println("被推荐的二级类目：" + category2Id);
//    	}
//
//    	// 5.找出二级类目中的所有商品
////    	List<BmsPost> recommendateProducts = new ArrayList<BmsPost>();
////    	for (String category2Id : recommendateCategory2) {
////    		List<ProductDTO> productList = productService.listProductByCategory2Id(category2Id);
////    		// 找出当前二级类目中点击量最大的商品
////    		Product maxHitsProduct = RecommendUtils.findMaxHitsProduct(productList);
////    		recommendateProducts.add(maxHitsProduct);
////    	}
////
////    	// 6.打印输出
////    	for (Product product : recommendateProducts) {
////    		System.out.println("被推荐的商品：" + product.getProductName());
////    	}
//    }
//}
