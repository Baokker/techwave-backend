package com.techwave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techwave.entity.Section;
import com.techwave.entity.SectionAndRequest;
import com.techwave.entity.dto.SolveDTO;
import com.techwave.entity.vo.SectionRequestsTotalVO;
import com.techwave.entity.vo.SectionRequestsVO;
import com.techwave.mapper.AdminAndReportMapper;
import com.techwave.mapper.AdminMapper;
import com.techwave.mapper.SectionAndRequestMapper;
import com.techwave.mapper.SectionMapper;
import com.techwave.service.AdminService;
import com.techwave.utils.Result;
import com.techwave.utils.TCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.techwave.service.UserService;

import java.util.ArrayList;
import java.util.List;

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
    @Override
    public Result getReportList() {

        return null;
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
}
