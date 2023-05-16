package com.techwave.service;

import com.techwave.entity.Section;
import com.techwave.entity.SubSection;
import com.techwave.entity.dto.*;
import com.techwave.utils.Result;

import java.text.ParseException;
import java.util.List;

public interface SectionService {
    Result getSectionData(Long userId, Long sectionId, Integer page, Integer perPage) throws ParseException;

    Section findSectionById(Long sectionId);
    SubSection findSubSectionById(Long subsectionId);

    Result getPostsByTag(PostsWithTagDTO postsWithTagDTO);

    List<SubSection> findSubSectionBySectionId(Long sectionId);

    Result collectSection(Long userId, Integer page, Integer perPage);

    Result hotSection(Integer page, Integer perPage);

    Result searchSection(Integer page, Integer perPage, String content);

    void updateSectionByCollectCount(Long sectionId, boolean b);

    void updateSectionByPostCount(Long sectionId, boolean b);

    Result getUserSections(Long userId);

    Result createSection(Long userId, String sectionName, String image, String sectionIntro);

    Result deleteSubSection(Long userId, Integer subsectionId);

    Result renameSubSection(RenameSubSectionDTO renameSubSectionDTO);

    Result changeSectionAvatar(Long userId, Long sectionId, String avatar);

    Result addSubSection(AddSubSectionDTO addSubSectionDTO);

    Result changeSectionIntro( ChangeIntroDTO changeIntroDTO);

    Result getAllPostsInSection(SectionDataDTO sectionDataDTO);

    Result getPostsInSectionByContent(SectionSearchPostDTO sectionSearchPostDTO);

    Result getPinnedPosts(Long sectionId);

    Result getHighlightedPostsInSectionWithPage(SectionDataDTO sectionDataDTO);

    List<Long> findSectionIdsByModeratorId(Long userId);

    Result getUserSectionsBySectionId(Long userId,Long sectionId);

    Result changeSectionName( SectionNameDTO sectionNameDTO);

    Result getSectionDataById(Long sectionId);

    Result getUserBySearch(Long userId, SearchUserDTO searchUserDTO);

    Result transferSection(Long userId,TransferSectionDTO transferSectionDTO);
}
