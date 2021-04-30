package com.douyuehan.doubao.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyuehan.doubao.common.api.ColumnFilter;
import com.douyuehan.doubao.common.api.PageRequest;
import com.douyuehan.doubao.common.api.PageResult;
import com.douyuehan.doubao.mapper.BmsTagMapper;
import com.douyuehan.doubao.mapper.BmsTopicMapper;
import com.douyuehan.doubao.mapper.SysUserMapper;
import com.douyuehan.doubao.model.dto.CreateTopicDTO;
import com.douyuehan.doubao.model.dto.RankDTO;
import com.douyuehan.doubao.model.dto.RankViewDTO;
import com.douyuehan.doubao.model.dto.ResultDTO;
import com.douyuehan.doubao.model.entity.*;
import com.douyuehan.doubao.model.vo.BmsPostVO;
import com.douyuehan.doubao.model.vo.PostVO;
import com.douyuehan.doubao.model.vo.ProfileVO;
import com.douyuehan.doubao.service.*;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class IBmsPostServiceImpl extends ServiceImpl<BmsTopicMapper, BmsPost> implements IBmsPostService {
    @Resource
    private BmsTagMapper bmsTagMapper;
    @Resource
    private SysUserMapper umsUserMapper;

    @Autowired
    @Lazy
    private IBmsTagService iBmsTagService;

    @Autowired
    private IUmsUserService iUmsUserService;

    @Autowired
    private IBehaviorService iBehaviorService;

    @Autowired
    private IBehaviorUserLogService iBehaviorUserLogService;
    @Autowired
    private com.douyuehan.doubao.service.IBmsTopicTagService IBmsTopicTagService;
    @Override
    public Page<PostVO> getList(Page<PostVO> page, String tab) {
        // 查询话题
        Page<PostVO> iPage = this.baseMapper.selectListAndPage(page, tab);
        // 查询话题的标签
        setTopicTags(iPage);
        return iPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BmsPost create(CreateTopicDTO dto, SysUser user) {
        BmsPost topic1 = this.baseMapper.selectOne(new LambdaQueryWrapper<BmsPost>().eq(BmsPost::getTitle, dto.getTitle()));
        Assert.isNull(topic1, "话题已存在，请修改");
        // 封装
        BmsPost topic = BmsPost.builder()
                .userId(user.getId())
                .title(dto.getTitle())
                .content(EmojiParser.parseToAliases(dto.getContent()))
                .createTime(new Date())
                .build();
        this.baseMapper.insert(topic);
        //用户创建动作 设置权重
        Behavior behavior = new Behavior(user.getId(),topic.getId(),new Date(),iBehaviorUserLogService.getWeightByType("post"), (long) 1);
        iBehaviorService.insert(behavior);
        // 用户积分增加
        int newScore = user.getScore() + 1;
        umsUserMapper.updateById(user.setScore(newScore));

        // 标签
        if (!ObjectUtils.isEmpty(dto.getTags())) {
            // 保存标签
            List<BmsTag> tags = iBmsTagService.insertTags(dto.getTags());
            // 处理标签与话题的关联
            IBmsTopicTagService.createTopicTag(topic.getId(), tags);
        }

        return topic;
    }

    @Override
    public Map<String, Object> viewTopic(Principal principal,String id) {
        Map<String, Object> map = new HashMap<>(16);
        BmsPost topic = this.baseMapper.selectById(id);
        Assert.notNull(topic, "当前话题不存在,或已被作者删除");
        // 查询话题详情
        topic.setView(topic.getView() + 1);
        this.baseMapper.updateById(topic);
        // emoji转码
        topic.setContent(EmojiParser.parseToUnicode(topic.getContent()));
        map.put("topic", topic);
        // 标签
        QueryWrapper<BmsTopicTag> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(BmsTopicTag::getTopicId, topic.getId());
        Set<String> set = new HashSet<>();
        for (BmsTopicTag articleTag : IBmsTopicTagService.list(wrapper)) {
            set.add(articleTag.getTagId());
        }
        List<BmsTag> tags = iBmsTagService.listByIds(set);
        map.put("tags", tags);

        // 作者

        ProfileVO user = iUmsUserService.getUserProfile(topic.getUserId());
        map.put("user", user);

        //用户查看帖子 更新权重
        String nowId = iUmsUserService.getUserByUsername(principal.getName()).getId();
        if(!ObjectUtil.isEmpty(iBehaviorService.getByBehaviorType(nowId,topic.getId()))){
            Behavior behavior = new Behavior(nowId,topic.getId(),new Date(),
                    iBehaviorUserLogService.getWeightByType("view")+iBehaviorService.getByBehaviorType(nowId,topic.getId())
                            .getBehaviorType (),iBehaviorService.getByBehaviorType(nowId,topic.getId())
                    .getCount()+1);
            LambdaQueryWrapper<Behavior> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Behavior::getUserId,nowId);
            queryWrapper.eq(Behavior::getPostId,topic.getId());
            iBehaviorService.update(behavior,queryWrapper);
        }

        return map;
    }

    @Override
    public List<BmsPost> getRecommend(String id) {
        return this.baseMapper.selectRecommend(id);
    }
    @Override
    public Page<PostVO> searchByKey(Principal principal,String keyword, Page<PostVO> page) {
        // 查询话题
        Page<PostVO> iPage = this.baseMapper.searchByKey(page, keyword);
        // 查询话题的标签
        setTopicTags(iPage);
        List<PostVO> list = iPage.getRecords();int i=0;
        String nowId = iUmsUserService.getUserByUsername(principal.getName()).getId();
        for (PostVO postVO:
        list) {
            i++;

            if(!ObjectUtil.isEmpty(iBehaviorService.getByBehaviorType(nowId,postVO.getId()))) {
                if(i<=5){//分段
                    Behavior behavior = new Behavior(nowId,postVO.getId(),new Date(),
                            iBehaviorUserLogService.getWeightByType("search")+iBehaviorService.getByBehaviorType(nowId,postVO.getId())
                                    .getBehaviorType (),iBehaviorService.getByBehaviorType(nowId,postVO.getId()).getCount()+1);
                    LambdaQueryWrapper<Behavior> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(Behavior::getUserId,nowId);
                    queryWrapper.eq(Behavior::getPostId,postVO.getId());
                    iBehaviorService.update(behavior,queryWrapper);
                }else if(5<i&&i<=10){
                    Behavior behavior = new Behavior(nowId,postVO.getId(),new Date(),
                            iBehaviorUserLogService.getWeightByType("search")-0.2+iBehaviorService.getByBehaviorType(nowId,postVO.getId())
                                    .getBehaviorType (),iBehaviorService.getByBehaviorType(nowId,postVO.getId()).getCount()+1);
                    LambdaQueryWrapper<Behavior> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(Behavior::getUserId,nowId);
                    queryWrapper.eq(Behavior::getPostId,postVO.getId());
                    iBehaviorService.update(behavior,queryWrapper);
                }else if(i>10){
                    Behavior behavior = new Behavior(nowId,postVO.getId(),new Date(),
                            iBehaviorUserLogService.getWeightByType("search")-0.4+iBehaviorService.getByBehaviorType(nowId,postVO.getId())
                                    .getBehaviorType (),iBehaviorService.getByBehaviorType(nowId,postVO.getId()).getCount()+1);
                    LambdaQueryWrapper<Behavior> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(Behavior::getUserId,nowId);
                    queryWrapper.eq(Behavior::getPostId,postVO.getId());
                    iBehaviorService.update(behavior,queryWrapper);
                }

            }
        }

        return iPage;
    }

    private void setTopicTags(Page<PostVO> iPage) {
        iPage.getRecords().forEach(topic -> {
            List<BmsTopicTag> topicTags = IBmsTopicTagService.selectByTopicId(topic.getId());

            if (!topicTags.isEmpty()) {
                List<String> tagIds = topicTags.stream().map(BmsTopicTag::getTagId).collect(Collectors.toList());
                List<BmsTag> tags = bmsTagMapper.selectBatchIds(tagIds);
                topic.setTags(tags);


            }
        });
    }
    public String getColumnFilterValue(PageRequest pageRequest, String filterName) {
        String value = null;
        ColumnFilter columnFilter = pageRequest.getColumnFilters().get(filterName);
        if(columnFilter != null) {
            value = columnFilter.getValue();
        }
        return value;
    }
    @Override
    public PageResult findPage(PageRequest pageRequest) {
        PageResult pageResult = null;
        String title = getColumnFilterValue(pageRequest, "title");
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        Page<BmsPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BmsPost> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(title)) {
            queryWrapper.eq(BmsPost::getTitle, title);
        }
        IPage<BmsPost> result = this.baseMapper.selectPage(page, queryWrapper);;
        pageResult = new PageResult(result);
        return pageResult;
    }

    @Override
    public List<BmsPostVO> getByListId(Set<String> recommendate) {
        List<BmsPostVO> list = new ArrayList<>();
        for (String s:
             recommendate) {
            BmsPost bmsPost = this.baseMapper.selectById(s);
            BmsPostVO bmsPostVO = new BmsPostVO();
            BeanUtil.copyProperties(bmsPost,bmsPostVO);
            bmsPostVO.setStatus("1");
            list.add(bmsPostVO);
        }
        int val = 15 - list.size();//满不满足15条数据
        List<BmsPost> listAll = this.baseMapper.selectList(null);
        if(val>0) {
            Iterator<BmsPost> it = listAll.iterator();
            while (it.hasNext()) {
                String str = it.next().getId();
                list.forEach(item -> {
                    if (str.equals(item.getId())) {
                        it.remove();
                    }
                });
            }
            System.out.println("还差" + val + "个元素，listAll size:" + listAll.size());

            HashSet<Integer> hashSet = new HashSet<>();
            Random random = new Random();
            while (hashSet.size() < val) {
                hashSet.add(random.nextInt(listAll.size()));
            }
            Iterator<Integer> iterable = hashSet.iterator();
            for (int i = 0; i < hashSet.size(); i++) {
                if(iterable.hasNext()) {
                    BmsPost bmsPost = listAll.get(iterable.next());
                    BmsPostVO bmsPostVO = new BmsPostVO();
                    BeanUtil.copyProperties(bmsPost, bmsPostVO);
                    bmsPostVO.setStatus("0");
                    list.add(bmsPostVO);
                }
            }
        }
        return list;
    }
    @Override
    public int getTodayAddPost() {
        LambdaQueryWrapper<BmsPost> queryWrapper = new LambdaQueryWrapper<>();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar calendar=java.util.Calendar.getInstance();
        calendar.roll(java.util.Calendar.DAY_OF_YEAR,-1);
        String last = df.format(calendar.getTime());
        calendar.roll(java.util.Calendar.DAY_OF_YEAR,1);
        String next = df.format(calendar.getTime());
        queryWrapper.ge(BmsPost::getCreateTime, last).apply("DATE_FORMAT(create_time,'%Y-%m-%d') <= DATE_FORMAT({0},'%Y-%m-%d')", next);
        return this.baseMapper.selectCount(queryWrapper);
    }

    @Override
    public ResultDTO getMonthAddPost() {
        List<String> key = new ArrayList<>();
        List<String> val = new ArrayList<>();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        for (int i = 30; i >0; i--) {
            LambdaQueryWrapper<BmsPost> queryWrapper = new LambdaQueryWrapper<>();
            java.util.Calendar calendar=java.util.Calendar.getInstance();
            calendar.roll(java.util.Calendar.DAY_OF_YEAR,-i);
            String last = df.format(calendar.getTime());
            calendar.roll(java.util.Calendar.DAY_OF_YEAR,1);
            String next = df.format(calendar.getTime());
            queryWrapper.ge(BmsPost::getCreateTime, last).apply("DATE_FORMAT(create_time,'%Y-%m-%d') <= DATE_FORMAT({0},'%Y-%m-%d')", next);
            Date date = new Date();
            try{
                date = sdf.parse(calendar.getTime().toString());
            }catch (Exception e){
                e.printStackTrace();
            }

            key.add(new SimpleDateFormat("MM-dd").format(date));
            val.add(this.baseMapper.selectCount(queryWrapper).toString());
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setKeyList(key);
        resultDTO.setResultList(val);

        return resultDTO;
    }

    @Override
    public RankDTO getRank() {
        List<String> key = new ArrayList<>();
        List<String> val = new ArrayList<>();
        List<RankDTO> list =  this.baseMapper.getRank();
        for (RankDTO rankDTO:
             list) {
            key.add(rankDTO.getAlias());
            val.add(String.valueOf(rankDTO.getCount()));
        }
        RankDTO rankDTO = new RankDTO();
        rankDTO.setKey(key);
        rankDTO.setResult(val);
        return rankDTO;
    }

    @Override
    public List<RankViewDTO> getViewRank() {
        return this.baseMapper.getViewRank();
    }


}
