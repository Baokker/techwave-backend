package com.techwave.service.impl;
/**
 * @author baokker
 * @date 2023/4/28
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techwave.entity.*;
import com.techwave.entity.dto.DeleteCommentDTO;
import com.techwave.entity.dto.ModeratorReportDTO;
import com.techwave.entity.dto.SectionDataDTO;
import com.techwave.entity.vo.PostReportDataVO;
import com.techwave.entity.vo.ReportDataVO;
import com.techwave.entity.vo.SectionReportVO;
import com.techwave.mapper.*;
import com.techwave.service.*;
import com.techwave.utils.Result;
import com.techwave.utils.TCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @descriptions: 举报
 * @author: baokker
 * @date: 2023/4/28 09:04
 * @version: 1.0
 */
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ModeratorReportMapper moderatorReportMapper;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostAndBodyMapper postAndBodyMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ReplyMapper replyMapper;
    @Autowired
    private CommentAndBodyMapper commentAndBodyMapper;

    @Autowired
    private PostAndCommentMapper postAndCommentMapper;

    @Autowired
    private CommentAndReplyMapper commentAndReplyMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public Result createModeratorReport(ModeratorReportDTO moderatorReportDTO, Long userId) {
        ModeratorReport moderatorReport = new ModeratorReport();
        moderatorReport.setReportReason(moderatorReportDTO.getReportReason());
        moderatorReport.setReportSubtype(moderatorReportDTO.getReportSubtype());
        moderatorReport.setReportType(moderatorReportDTO.getReportType());
        moderatorReport.setSectionId(moderatorReportDTO.getSectionId());
        moderatorReport.setReportedId(moderatorReportDTO.getReportedId());
        moderatorReport.setUserId(userId);
        Integer result = moderatorReportMapper.insert(moderatorReport);
        if (result == 1) {
            return Result.success(TCode.SUCCESS.getCode(), "举报成功", null);
        }
        return Result.fail(TCode.FAIL.getCode(), "举报失败", null);
    }

    @Override
    public Result getCommentReportWithPage(SectionDataDTO sectionDataDTO) {
        Long sectionId = sectionDataDTO.getSectionId();
        Integer page = sectionDataDTO.getPage();
        Integer perPage = sectionDataDTO.getPerPage();

        if (sectionId == null || page == null || perPage == null)
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);
        Section section = sectionService.findSectionById(sectionId);
        if (section == null) {
            return Result.fail(-1, "参数有误", null);
        }

        List<ReportDataVO> reportList = this.findReportBySectionIdWithPage(sectionId, page, perPage);

        SectionReportVO sectionReportVO = new SectionReportVO();
        sectionReportVO.setReportDataVOList(reportList);

        QueryWrapper<ModeratorReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("section_id", sectionId);
        queryWrapper.in("report_type", Arrays.asList("comment", "reply"));
        sectionReportVO.setTotal(Math.toIntExact(moderatorReportMapper.selectCount(queryWrapper)));

        return Result.success(20000, sectionReportVO);
    }

    @Override
    public List<ReportDataVO> findReportBySectionIdWithPage(Long sectionId, Integer page, Integer perPage) {
        Page<ModeratorReport> moderatorReportPage = new Page<>(page, perPage);
        LambdaQueryWrapper<ModeratorReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ModeratorReport::getSectionId, sectionId);
        queryWrapper.in(ModeratorReport::getReportType, Arrays.asList("comment", "reply"));

        Page<ModeratorReport> moderatorReportPage1 = moderatorReportMapper.selectPage(moderatorReportPage, queryWrapper);
        return copyList(moderatorReportPage1.getRecords());
    }

    @Override
    public List<PostReportDataVO> findPostReportBySectionIdWithPage(Long sectionId, Integer page, Integer perPage) {
        Page<ModeratorReport> moderatorReportPage = new Page<>(page, perPage);
        LambdaQueryWrapper<ModeratorReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ModeratorReport::getSectionId, sectionId);
        queryWrapper.eq(ModeratorReport::getReportType, "post");

        Page<ModeratorReport> moderatorReportPage1 = moderatorReportMapper.selectPage(moderatorReportPage, queryWrapper);
        return copyList2(moderatorReportPage1.getRecords());
    }

    @Override
    public Result getPostReportWithPage(SectionDataDTO sectionDataDTO) {
        Long sectionId = sectionDataDTO.getSectionId();
        Integer page = sectionDataDTO.getPage();
        Integer perPage = sectionDataDTO.getPerPage();

        if (sectionId == null || page == null || perPage == null)
            return Result.fail(TCode.PARAMS_ERROR.getCode(), TCode.PARAMS_ERROR.getMsg(), null);
        Section section = sectionService.findSectionById(sectionId);
        if (section == null) {
            return Result.fail(-1, "参数有误", null);
        }

        List<PostReportDataVO> reportList = this.findPostReportBySectionIdWithPage(sectionId, page, perPage);

        SectionReportVO sectionPostReportVO = new SectionReportVO();
        sectionPostReportVO.setPostReportDataVOList(reportList);

        QueryWrapper<ModeratorReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("section_id", sectionId);
        queryWrapper.eq("report_type",  "post");
        sectionPostReportVO.setTotal(Math.toIntExact(moderatorReportMapper.selectCount(queryWrapper)));

        return Result.success(20000, sectionPostReportVO);
    }

    private List<ReportDataVO> copyList(List<ModeratorReport> moderatorReportList) {
        List<ReportDataVO> voList = new ArrayList<>();
        for (ModeratorReport moderatorReport : moderatorReportList) {
            voList.add(copy(moderatorReport));
        }
        return voList;
    }

    private List<PostReportDataVO> copyList2(List<ModeratorReport> moderatorReportList) {
        List<PostReportDataVO> voList = new ArrayList<>();
        for (ModeratorReport moderatorReport : moderatorReportList) {
            voList.add(copy2(moderatorReport));
        }
        return voList;
    }

    private PostReportDataVO copy2(ModeratorReport moderatorReport) {
        PostReportDataVO reportDataVO = new PostReportDataVO();
        reportDataVO.setUserId(moderatorReport.getUserId());
        reportDataVO.setId(moderatorReport.getReportId());
        reportDataVO.setReportType(moderatorReport.getReportType());
        reportDataVO.setReportSubtype(moderatorReport.getReportSubtype());
        reportDataVO.setReportReason(moderatorReport.getReportReason());
        reportDataVO.setReportedId(moderatorReport.getReportedId());
        String str = moderatorReport.getCreatedAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        reportDataVO.setCreatedAt(dateTime);
        return reportDataVO;
    }
    private ReportDataVO copy(ModeratorReport moderatorReport) {
        ReportDataVO reportDataVO = new ReportDataVO();
        if (moderatorReport.getReportType().equals("comment")) {
            //如果reportType为comment，去comment表中找到content
        reportDataVO.setCommentContent(commentService.findContentById(moderatorReport.getReportedId()).getContent());}
        else if (moderatorReport.getReportType().equals("reply")) {
            //如果reportType为reply，去reply表中找到content
            reportDataVO.setCommentContent(replyService.findContentById(moderatorReport.getReportedId()).getContent());
        }
        reportDataVO.setUserId(moderatorReport.getUserId());
        reportDataVO.setId(moderatorReport.getReportId());
        reportDataVO.setReportType(moderatorReport.getReportType());
        reportDataVO.setReportSubtype(moderatorReport.getReportSubtype());
        reportDataVO.setReportReason(moderatorReport.getReportReason());
        reportDataVO.setReportedId(moderatorReport.getReportedId());
        String str = moderatorReport.getCreatedAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        reportDataVO.setCreatedAt(dateTime);
        return reportDataVO;
    }

@Override
public Result deleteReport(Long userId, Integer reportId){
    LambdaQueryWrapper<ModeratorReport> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ModeratorReport::getReportId, reportId);
    queryWrapper.last("limit 1");
    ModeratorReport moderatorReport = moderatorReportMapper.selectOne(queryWrapper);
    moderatorReportMapper.delete(queryWrapper);

    Notification notification = new Notification();
    notification.setUserId(userId);
    notification.setTitle("举报处理通知");
    notification.setNotificationType("system");
    notification.setContent("您的举报已处理，处理结果：不通过");
    notification.setIsRead(false);
    notificationMapper.insert(notification);

    return Result.success(20000, "okk", null);
}

    @Override
    public Result deletePost(Long userId, Integer targetId){

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setNotificationType("system");
        notification.setTitle("举报处理通知");
        notification.setContent("您的举报已处理，处理结果：通过，《"+postMapper.selectById(targetId).getTitle()+"》该帖子已被删除");
        notification.setIsRead(false);
        notificationMapper.insert(notification);

        LambdaQueryWrapper<Post> postQuery = new LambdaQueryWrapper<>();
        postQuery.select(Post::getSectionId).eq(Post::getId, targetId);
        Post post = postMapper.selectOne(postQuery);
        Long sectionId = post.getSectionId();
        LambdaUpdateWrapper<Section> sectionUpdate = new LambdaUpdateWrapper<>();
        sectionUpdate.setSql("post_count = post_count - 1").eq(Section::getId, sectionId);

        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getId, targetId);
        postMapper.delete(queryWrapper);

        LambdaQueryWrapper<PostAndBody> postAndBodyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postAndBodyLambdaQueryWrapper.eq(PostAndBody::getId, targetId);
        postAndBodyMapper.delete(postAndBodyLambdaQueryWrapper);

        LambdaQueryWrapper<PostAndComment> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(PostAndComment::getPostId, targetId);
        postAndCommentMapper.delete(queryWrapper2);

        LambdaQueryWrapper<ModeratorReport> queryWrapper3 = new LambdaQueryWrapper<>();
        queryWrapper3.eq(ModeratorReport::getReportedId, targetId);
        queryWrapper3.eq(ModeratorReport::getReportType, "post");
        queryWrapper3.last("limit 1");
        ModeratorReport moderatorReport = moderatorReportMapper.selectOne(queryWrapper3);
        moderatorReportMapper.delete(queryWrapper3);




        return Result.success(20000, "okk", null);
    }

    @Override
    public Result deleteCommentOrReply(DeleteCommentDTO deleteCommentDTO){
        Long userId = deleteCommentDTO.getUserId();
        Integer targetId = deleteCommentDTO.getTargetId();
        String reportType = deleteCommentDTO.getReportType();
        if (reportType.equals("comment")) {
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();

            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle("举报处理通知");
            notification.setNotificationType("system");
            notification.setContent("您的举报已处理，处理结果：通过，相关违规评论已被删除");
            notification.setIsRead(false);
            notificationMapper.insert(notification);


            queryWrapper.eq(Comment::getId, targetId);
            commentMapper.delete(queryWrapper);

            LambdaQueryWrapper<CommentAndBody> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(CommentAndBody::getId, targetId);
            commentAndBodyMapper.delete(queryWrapper2);

            LambdaQueryWrapper<CommentAndReply> queryWrapper3 = new LambdaQueryWrapper<>();
            queryWrapper3.eq(CommentAndReply::getCommentId, targetId);
            commentAndReplyMapper.delete(queryWrapper3);

            LambdaQueryWrapper<PostAndComment> queryWrapper4 = new LambdaQueryWrapper<>();
            queryWrapper4.eq(PostAndComment::getCommentId, targetId);
            postAndCommentMapper.delete(queryWrapper4);

            LambdaQueryWrapper<ModeratorReport> queryWrapper5 = new LambdaQueryWrapper<>();
            queryWrapper5.eq(ModeratorReport::getReportedId, targetId);
            queryWrapper5.eq(ModeratorReport::getReportType, "comment");
            queryWrapper5.last("limit 1");
            ModeratorReport moderatorReport = moderatorReportMapper.selectOne(queryWrapper5);
            moderatorReportMapper.delete(queryWrapper5);
        }
        else {
            LambdaQueryWrapper<CommentAndReply> queryWrapper1 = new LambdaQueryWrapper<>();

            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setNotificationType("system");
            notification.setTitle("举报处理通知");
            notification.setContent("您的举报已处理，处理结果：通过，相关违规回复已被删除");
            notification.setIsRead(false);
            notificationMapper.insert(notification);

            queryWrapper1.eq(CommentAndReply::getReplyId, targetId);
            commentAndReplyMapper.delete(queryWrapper1);

            LambdaQueryWrapper<Reply> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(Reply::getId, targetId);
            replyMapper.delete(queryWrapper2);

            LambdaQueryWrapper<ModeratorReport> queryWrapper3 = new LambdaQueryWrapper<>();
            queryWrapper3.eq(ModeratorReport::getReportedId, targetId);
            queryWrapper3.eq(ModeratorReport::getReportType, "reply");
            queryWrapper3.last("limit 1");
            ModeratorReport moderatorReport = moderatorReportMapper.selectOne(queryWrapper3);
            moderatorReportMapper.delete(queryWrapper3);
        }

        return Result.success(20000, "okk", null);
    }

}
