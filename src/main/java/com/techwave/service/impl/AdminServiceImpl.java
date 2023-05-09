package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techwave.entity.AdminAndReport;
import com.techwave.entity.ChatBanUser;
import com.techwave.entity.Section;
import com.techwave.entity.SectionAndRequest;
import com.techwave.entity.dto.BanUserDTO;
import com.techwave.entity.dto.SolveDTO;
import com.techwave.entity.vo.AdminReportDataVO;
import com.techwave.entity.vo.AdminReportListVO;
import com.techwave.entity.vo.SectionRequestsTotalVO;
import com.techwave.entity.vo.SectionRequestsVO;
import com.techwave.mapper.*;
import com.techwave.service.AdminService;
import com.techwave.utils.Result;
import com.techwave.utils.TCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.techwave.service.UserService;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AdminAndReportMapper adminAndReportMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private SectionAndRequestMapper sectionAndRequestMapper;
    @Autowired
    private SectionMapper sectionMapper;
    @Autowired
    private ChatBanUserMapper chatBanUserMapper;
    @Override
    public Result getReportList(int page, int perPage) {
        if(page<0||perPage<0){
            return Result.fail(TCode.PARAMS_ERROR.getCode(),TCode.PARAMS_ERROR.getMsg());
        }
        AdminReportDataVO adminReportDataVO= new AdminReportDataVO();
        List<AdminReportListVO> reportData = getReportListWithPage(page,perPage);
        adminReportDataVO.setAdminReportListVO(reportData);
        adminReportDataVO.setTotal(adminAndReportMapper.selectCount(new LambdaQueryWrapper<AdminAndReport>().gt(AdminAndReport::getReportId,0)));
        return Result.success(20000,"获取举报列表成功",adminReportDataVO);
    }

    @Override
    public List<AdminReportListVO> getReportListWithPage(int page, int perPage){
        Page<AdminAndReport> adminAndReportPage = new Page<>(page,perPage);
        LambdaQueryWrapper<AdminAndReport> lambdaQueryWrapper= new LambdaQueryWrapper<>();
        lambdaQueryWrapper.gt(AdminAndReport::getReportId,0);
        adminAndReportMapper.selectPage(adminAndReportPage,lambdaQueryWrapper);
        return getAdminReportListVO(adminAndReportPage.getRecords());
    }
    @Override
    public List<AdminReportListVO> getAdminReportListVO(List<AdminAndReport> adminAndReportList){
        List<AdminReportListVO> adminReportListVO = new ArrayList<>();
        for (AdminAndReport adminAndReport : adminAndReportList) {
            adminReportListVO.add(adminReport2VO(adminAndReport));
        }

        return adminReportListVO;
    }
    @Override
    public AdminReportListVO adminReport2VO(AdminAndReport adminAndReport) {
        AdminReportListVO adminReportListVO = new AdminReportListVO();
        BeanUtils.copyProperties(adminAndReport,adminReportListVO);

        adminReportListVO.setUserName(userService.findUserById(adminAndReport.getReportedUserId()).getUsername());
        adminReportListVO.setAvatar(userService.findUserById(adminAndReport.getReportedUserId()).getAvatar());
        adminReportListVO.setSummary(userService.findUserById(adminAndReport.getReportedUserId()).getSummary());

        //查询用户是否已封禁
        LambdaQueryWrapper<ChatBanUser> lambdaQueryWrapper= new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatBanUser::getUserId,adminAndReport.getReportedUserId());

        adminReportListVO.setIsBanned(chatBanUserMapper.selectOne(lambdaQueryWrapper)!=null);

        return adminReportListVO;
    }

    @Override
    public Result getSectionRequest(int page,int perPage) {
        if(page<0||perPage<0){
            return Result.fail(TCode.PARAMS_ERROR.getCode(),TCode.PARAMS_ERROR.getMsg());
        }
        SectionRequestsTotalVO sectionRequestsTotalVO = new SectionRequestsTotalVO();
        List<SectionRequestsVO> sectionRequestsVOList = getSectionRequestListWithPage(page, perPage);
        sectionRequestsTotalVO.setSectionRequestsVOList(sectionRequestsVOList);
        sectionRequestsTotalVO.setTotal(Math.toIntExact(sectionAndRequestMapper.selectCount(new LambdaQueryWrapper<SectionAndRequest>().eq(SectionAndRequest::getIsApproved, false))));
        return Result.success(20000,"获取版块申请列表成功",sectionRequestsTotalVO);
    }

    @Override
    public List<SectionRequestsVO> getSectionRequestListWithPage(int page, int perPage) {
        Page<SectionAndRequest> sectionAndRequestPage = new Page<>(page,perPage);
        LambdaQueryWrapper<SectionAndRequest> lambdaQueryWrapper= new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SectionAndRequest::getIsApproved,false);
        Page<SectionAndRequest> sectionAndRequestPage1 = sectionAndRequestMapper.selectPage(sectionAndRequestPage, lambdaQueryWrapper);
        return getSectionRequestsVOList(sectionAndRequestPage1.getRecords());
    }

    @Override
    public List<SectionRequestsVO> getSectionRequestsVOList(List<SectionAndRequest> sectionAndRequests){
        List<SectionRequestsVO> sectionRequestsVOList = new ArrayList<>();
        for (SectionAndRequest sectionAndRequest : sectionAndRequests) {
            sectionRequestsVOList.add(sectionRequests2VO(sectionAndRequest));
        }
        return sectionRequestsVOList;
    }
    @Override
    public SectionRequestsVO sectionRequests2VO(SectionAndRequest sectionAndRequest) {
        SectionRequestsVO sectionRequestsVO = new SectionRequestsVO();
        BeanUtils.copyProperties(sectionAndRequest,sectionRequestsVO);
        sectionRequestsVO.setUserName(userService.findUserById(sectionAndRequest.getUserId()).getUsername());
        return sectionRequestsVO;
    }
    @Override
    public Result dealWithSectionRequests(SolveDTO solveDTO){
        int sectionRequestId = solveDTO.getId();
        Boolean isPassed = solveDTO.getIsPassed();

        if(sectionRequestId<0||isPassed==null){
            return Result.fail(TCode.PARAMS_ERROR.getCode(),TCode.PARAMS_ERROR.getMsg());
        }
        if(isPassed)
            return approveSectionRequest(sectionRequestId);
        else
            return rejectSectionRequest(sectionRequestId);
    }
    @Override
    public Result approveSectionRequest(int sectionRequestId) {
        SectionAndRequest sectionAndRequest = sectionAndRequestMapper.selectById(sectionRequestId);
        if(sectionAndRequest==null){
            return Result.fail(-1,"该版块申请不存在",null);
        }
        sectionAndRequest.setIsApproved(true);
        sectionAndRequestMapper.updateById(sectionAndRequest);
        LambdaQueryWrapper<Section> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Section::getName, sectionAndRequest.getName());
        queryWrapper.last("limit 1");
        Section section = sectionMapper.selectOne(queryWrapper);
        if (section != null) {
            return Result.fail(-1, "论坛里已有该版块", null);
        }
        //将该版块插入到section表中
        Section section1 = new Section();
        section1.setName(sectionAndRequest.getName());
        section1.setAvatar(sectionAndRequest.getAvatar());
        section1.setDescription(sectionAndRequest.getDescription());
        section1.setModeratorId(sectionAndRequest.getUserId());
        sectionMapper.insert(section1);

        //插入完成后从申请表中删除
        rejectSectionRequest(sectionRequestId);

        return Result.success(20000,"通过版块申请成功",null);
    }

    @Override
    public Result rejectSectionRequest(int sectionRequestId) {
        SectionAndRequest sectionAndRequest = sectionAndRequestMapper.selectById(sectionRequestId);
        if(sectionAndRequest==null){
            return Result.fail(-1,"该版块申请不存在",null);
        }
        //删除版块申请
        sectionAndRequestMapper.deleteById(sectionRequestId);

        return Result.success(20000,"已拒绝该版块申请",null);
    }
    @Override
    public Result banUser(BanUserDTO banUserDTO){
        Long userId = Long.valueOf(banUserDTO.getTargetId());
        Long reportId = Long.valueOf(banUserDTO.getReportId());
        String createdAt=banUserDTO.getCreatedAt();
        String duration= banUserDTO.getBanUntil();
        int banUntil=0;

        if(userId<0||reportId<0||createdAt==null||duration==null){
            return Result.fail(TCode.PARAMS_ERROR.getCode(),TCode.PARAMS_ERROR.getMsg());
        }
        switch (duration){
            case "一个月":
                banUntil = 30;
                break;
            case "三个月":
                banUntil = 90;
                break;
            case "半年":
                banUntil = 180;
                break;
            case "一年":
                banUntil = 365;
                break;
            default:break;
        }

        Timestamp banUntilTime = new Timestamp(System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(banUntil));
        return banChatUser(userId,reportId,createdAt,banUntilTime);
    }
    @Override
    public  Result banChatUser(Long userId, Long reportId, String createdAt, Timestamp banUntilTime){
        //转换时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String banUntil = simpleDateFormat.format(banUntilTime);

        //将该用户加入到chat_ban表中

        //查询用户是否已封禁
        LambdaQueryWrapper<ChatBanUser> lambdaQueryWrapper= new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatBanUser::getUserId,userId);
        if(chatBanUserMapper.selectOne(lambdaQueryWrapper)!=null){
            return Result.fail(-1,"该用户已被封禁",null);
        }
        ChatBanUser chatBanUser = ChatBanUser.builder()
                .userId(userId)
                .banUntil(banUntil)
                .createdAt(createdAt)
                .build();
        chatBanUserMapper.insert(chatBanUser);
        return Result.success(20000,"封禁成功",null);
    }
    @Override
    public Result deleteReportData(Integer reportId){
        if(reportId<0){
            return Result.fail(TCode.PARAMS_ERROR.getCode(),TCode.PARAMS_ERROR.getMsg());
        }
        if(adminAndReportMapper.selectById(reportId)==null){
            return Result.fail(-1,"该举报信息不存在",null);
        }
        adminAndReportMapper.deleteById(reportId);
        return Result.success(20000,"删除举报信息成功",null);
    }
}
