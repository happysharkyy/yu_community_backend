package com.douyuehan.doubao.utils.test;

import com.douyuehan.doubao.model.entity.Behavior;
import com.douyuehan.doubao.model.entity.BehaviorSimilarity;
import com.douyuehan.doubao.model.entity.BmsPost;
import com.douyuehan.doubao.service.IBmsPostService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类描述：用于商品推荐的工具类
 * 类名称：com.lyu.shopping.recommendate.util.RecommendUtils
 * @author 曲健磊
 * 2018年3月25日.下午8:10:41
 * @version V1.0
 */
public class RecommendUtils {

    /**
     * 更新用户的购买行为，最终插入数据库中
     * @param userId 用户id
     * @param itemId 商品id
     * @return true则更新成功，false则更新失败
     */
    public static boolean updateBuyingBehavior(Long userId, Long itemId) {
        boolean flag = false;
        // TODO
        return flag;
    }
    
    /**
     * 将用户的购买行为组装成一个map,key为userId，value也是一个map，这个map记录的是二级类目以及它对应的点击量
     * @param userActiveList 用户的购买行为列表
     * @return 组装好的用户的购买行为的map集合
     */
    public static ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> assembleUserBehavior(List<Behavior> userActiveList) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> activeMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, Double>>();
        // 遍历查询到的用户点击行为数据
        for (Behavior userActive : userActiveList) {
            // 1.获取用户id
            String userId = userActive.getUserId();
            // 2.获取二级类目id
            String postId = userActive.getPostId();
            // 3.获取该二级类目的点击量
            Double hits = userActive.getCount()*userActive.getBehaviorType();
            
            // 判断activeMap中是否已经存在了该userId的信息，如果存在则进行更新
            if (activeMap.containsKey(userId)) {
                ConcurrentHashMap<String, Double> tempMap = activeMap.get(userId);
                tempMap.put(postId, hits);
                activeMap.put(userId, tempMap);
            } else {
                // 不存在则直接put进
                ConcurrentHashMap<String, Double> category2Map = new ConcurrentHashMap<String, Double>();
                category2Map.put(postId, hits);
                activeMap.put(userId, category2Map);
            }
        }
        
        return activeMap;
    }
    
    /**
     * 计算用户与用户之间的相似性，返回计算出的用户与用户之间的相似度对象
     * @param activeMap 用户对各个二级类目的购买行为的一个map集合
     * @return 计算出的用户与用户之间的相似度的对象存储形式
     */
    public static List<BehaviorSimilarity>  calcSimilarityBetweenUsers(ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> activeMap) {
        // 用户之间的相似度对集合
        List<BehaviorSimilarity> similarityList = new ArrayList<>();
        
        // 获取所有的键的集合
        Set<String> userSet = activeMap.keySet();
        
        // 把这些集合放入ArrayList中
        List<String> userIdList = new ArrayList<>(userSet);
        
        // 小于两个说明当前map集合中只有一个map集合的购买行为，或者一个都没有，直接返回
        if (userIdList.size() < 2) {
            return similarityList;
        }
        
        // 计算所有的用户之间的相似度对
        for (int i = 0; i < userIdList.size() - 1; i++) {
            for (int j = i + 1; j < userIdList.size(); j++) {
                // 分别获取两个用户对每个二级类目的点击量
                ConcurrentHashMap<String, Double> userCategory2Map = activeMap.get(userIdList.get(i));
                ConcurrentHashMap<String, Double> userRefCategory2Map = activeMap.get(userIdList.get(j));

                // 获取两个map中二级类目id的集合
                Set<String> key1Set = userCategory2Map.keySet();
                Set<String> key2Set = userRefCategory2Map.keySet();
                Iterator<String> it1 = key1Set.iterator();
                Iterator<String> it2 = key2Set.iterator();

                // 两用户之间的相似度
                double similarity = 0.0;
                // 余弦相似度公式中的分子
                double molecule = 0.0;
                // 余弦相似度公式中的分母
                double denominator = 1.0;
                // 余弦相似度公式中分母根号下的两个向量的模的值
                double vector1 = 0.0;
                double vector2 = 0.0;

                while (it1.hasNext() && it2.hasNext()) {
                    String it1Id = it1.next();
                    String it2Id = it2.next();
                    // 获取二级类目对应的点击次数
                    Double hits1 = userCategory2Map.get(it1Id);
                    Double hits2 = userRefCategory2Map.get(it2Id);
                    // 累加分子
                    molecule += hits1 * hits2;
                    // 累加分母中的两个向量的模
                    vector1 += Math.pow(hits1, 2);
                    vector2 += Math.pow(hits2, 2);
                }
                // 计算分母
                denominator = Math.sqrt(vector1) * Math.sqrt(vector2);
                // 计算整体相似度
                similarity =( molecule / denominator);

                // 创建用户相似度对对象
                BehaviorSimilarity behaviorSimilarity = new BehaviorSimilarity();
                behaviorSimilarity.setUserId(userIdList.get(i));
                behaviorSimilarity.setUserRefId(userIdList.get(j));
                behaviorSimilarity.setSimilarity(similarity);
                // 将计算出的用户以及用户之间的相似度对象存入list集合
                similarityList.add(behaviorSimilarity);
            }
        }
        
        return similarityList;
    }
    
    /**
     * 找出与userId购买行为最相似的topN个用户
     * @param userId 需要参考的用户id
     * @param behaviorSimilarityList 用户相似度列表
     * @param topN 与userId相似用户的数量
     * @return 与usereId最相似的topN个用户
     */
    public static List<String> getSimilarityBetweenUsers(String userId, List<BehaviorSimilarity> behaviorSimilarityList, Integer topN) {
        // 用来记录与userId相似度最高的前N个用户的id
        List<String> similarityList = new ArrayList<String>(topN);
        
        // 堆排序找出最高的前N个用户，建立小根堆，遍历的时候当前的这个相似度比堆顶元素大就剔掉堆顶的值，把这个数入堆(把小的都删除干净,所以要建立小根堆)
        PriorityQueue<BehaviorSimilarity> minHeap = new PriorityQueue<BehaviorSimilarity>(new Comparator<BehaviorSimilarity>(){
            @Override
            public int compare(BehaviorSimilarity o1, BehaviorSimilarity o2) {
                if (o1.getSimilarity() - o2.getSimilarity() > 0) {
                    return 1;
                } else if (o1.getSimilarity() - o2.getSimilarity() == 0) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        
        for (BehaviorSimilarity BehaviorSimilarity : behaviorSimilarityList) {
            if (minHeap.size() < topN) {
                minHeap.offer(BehaviorSimilarity);

            } else if (minHeap.peek().getSimilarity() < BehaviorSimilarity.getSimilarity()) {
                minHeap.poll();
                minHeap.offer(BehaviorSimilarity);
            }
        }
        // 把得到的最大的相似度的用户的id取出来(不要取它自己)
        for (BehaviorSimilarity BehaviorSimilarity : minHeap) {
            System.out.println(BehaviorSimilarity.getSimilarity());
            similarityList.add(BehaviorSimilarity.getUserId().equals(userId)  ? BehaviorSimilarity.getUserRefId() : BehaviorSimilarity.getUserId());
        }
        
        return similarityList;
    }

    /**
     * 到similarUserList中的用户访问的二级类目中查找userId不经常点击的二级类目中获得被推荐的类目id
     * @param userId 被推荐商品的用户id
     * @param similarUserList 用userId相似的用户集合
     * @param userActiveList 所有用户的浏览行为
     * @return 可以推荐给userId的二级类目id列表
     */
    public static List<String> getRecommendateCategory2(String userId, List<String> similarUserList, List<Behavior> userActiveList) {
        List<String> recommeddateProductList = new ArrayList<String>();
        
        // userId的浏览行为列表
        List<Behavior> userIdActiveList = findUsersBrowsBehavior(userId, userActiveList);
        
        // 对userId的浏览行为按照二级类目id排个序，方便后续与推荐的用户的浏览行为中的二级类目的点击次数直接相减，避免时间复杂度为O(n2)
        Collections.sort(userIdActiveList, new Comparator<Behavior>(){
            @Override
            public int compare(Behavior o1, Behavior o2) {
                return o1.getPostId().compareTo(o2.getPostId());
            }
        });

        // 1.从与useId浏览行为相似的每个用户中找出一个推荐的二级类目
        for (String refId : similarUserList) {
            // 计算当前用户所点击的二级类目次数与被推荐的用户所点击的二级类目的次数的差值
            // 找到当前这个用户的浏览行为
            List<Behavior> currActiveList = findUsersBrowsBehavior(refId, userActiveList);
            
            // 排序，同上述理由
            Collections.sort(currActiveList, new Comparator<Behavior>(){
                @Override
                public int compare(Behavior o1, Behavior o2) {
                    return o1.getPostId().compareTo(o2.getPostId());
                }
            });
            
            // 记录差值最大的二级类目的id
            String maxCategory2 = "";
            
            // 记录最大的差值
            double maxDifference = 0.0;
            for (int i = 0; i < currActiveList.size(); i++) {
                // 求出点击量差值最大的二级类目，即为要推荐的类目
                double difference = Math.abs((currActiveList.get(i).getCount()*currActiveList.get(i).getBehaviorType())
                        - (userIdActiveList.get(i).getCount()*userActiveList.get(i).getBehaviorType()));
                if (difference > maxDifference) {
                    maxDifference = difference;
                    maxCategory2 = currActiveList.get(i).getPostId();
                }
            }
            recommeddateProductList.add(maxCategory2);
        }
        return recommeddateProductList;
    }

    /**
     * 找到当前用户的浏览行为列表
     * @param userId 当前用户id
     * @param userActiveList 所有用户的浏览行为列表
     * @return 当前用户的浏览行为列表
     */
    public static List<Behavior> findUsersBrowsBehavior(String userId, List<Behavior> userActiveList) {
        List<Behavior> currActiveList = new ArrayList<Behavior>();
        for (Behavior Behavior : userActiveList) {
            if (Behavior.getUserId().equals(userId)) {
                currActiveList.add(Behavior);
            }
        }
        return currActiveList;
    }

    /**
     * 找到当前商品列表中点击量最高的商品
     * @param productList 商品列表
     * @return 点击量最高的商品
     */
    public static BmsPost findMaxHitsProduct(List<? extends BmsPost> productList) {
        if (productList == null || productList.size() == 0) {
            return null;
        }
        // 记录当前最大的点击量
        Integer maxHits = null;
        
        // 记录当前点击量最大的商品
        BmsPost product = null;
        for (BmsPost temp : productList) {
            if (temp.getView() >= maxHits) {
                maxHits = temp.getView(); 
                product = temp;
            }
        }
        return product;
    }
    
}
