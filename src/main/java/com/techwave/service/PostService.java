package com.techwave.service;

import com.techwave.entity.dto.PostPublishDTO;
import com.techwave.entity.vo.FolderPostVO;
import com.techwave.entity.vo.PostDataVO;
import com.techwave.utils.Result;
import com.techwave.entity.dto.PostDataDTO;

import java.text.ParseException;
import java.util.List;

public interface PostService {
    List<PostDataVO> findPostBySectionIdWithPage(Long sectionId, int curPage, int limit);

    List<PostDataVO> findPostBySectionIdAndSubSectionId(Long subsectionId, int curPage, int limit);


    Result getPostData(Long userId, PostDataDTO postDataDTO) throws ParseException;

    Result pinOrUnpinPost(Long userId, Long postId);

    Result highlightOrUnhighlightPost(Long userId, Long postId);

    Result hotPost();

    Result getNews();

    Result publishPost(Long userId, PostPublishDTO postPublishDTO);

    void updatePostByCommentCount(Long postId, boolean b);

    List<FolderPostVO> findPostsByFolderIdWithPage(Long folderId, Integer curPage, Integer limit);

    Result findPostsByUserIdWithPage(Long userId, String type, Integer curPage, Integer limit);

    Result deleteMyPost(Long postId, Long userId);

    List<Long> findPostIdsByUserId(Long userId);

    List<PostDataVO> findPostBySectionIdWithPageAndContent(Long sectionId, Integer page, Integer perPage, String content);

    List<PostDataVO> findPinnedPostsBySectionId(Long sectionId);

    List<PostDataVO> findHighlightedPostBySectionIdWithPage(Long sectionId, Integer page, Integer perPage);

    Result likeOrUnlikePost(Long userId, Long postId);

    Result deletePostByModerator(Long userId, Long postId);
}
