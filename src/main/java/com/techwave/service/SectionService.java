package com.techwave.service;

import com.techwave.entity.Section;
import com.techwave.entity.SubSection;
import com.techwave.entity.dto.*;
import com.techwave.utils.Result;

import java.util.List;

public interface SectionService {
    Result getSectionData(Long userId, SectionDataDTO sectionDataDTO);

    Section findSectionById(Long sectionId);
    SubSection findSubSectionById(Long subsectionId);

    Result getPostsByTag(PostsWithTagDTO postsWithTagDTO);

    List<SubSection> findSubSectionBySectionId(Long sectionId);

    Result collectSection(Integer userId);

    Result hotSection(Integer i);

    Result searchSection(String content);

    void updateSectionByCollectCount(Long sectionId, boolean b);

    void updateSectionByPostCount(Long sectionId, boolean b);

    Result getUserSections(Long userId);

    Result createSection(Long userId, String sectionName, String s, String sectionIntro, String[] subsection);

    Result deleteSubSection(Long userId, Integer subsectionId);

    Result renameSubSection(RenameSubSectionDTO renameSubSectionDTO);

    Result changeSectionAvatar(Long userId, Long sectionId, String avatar);

    Result addSubSection(AddSubSectionDTO addSubSectionDTO);

    Result changeSectionIntro(Long userId, ChangeIntroDTO changeIntroDTO);

    Result getAllPostsInSection(SectionDataDTO sectionDataDTO);
}
