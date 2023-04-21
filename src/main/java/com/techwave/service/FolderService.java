package com.techwave.service;

import com.techwave.entity.dto.RenameFolderDTO;
import com.techwave.utils.Result;
import com.techwave.entity.dto.FolderPostDTO;

public interface FolderService {
    Result createFolder(Long userId, String folderName);

    Result renameFolder(RenameFolderDTO renameFolderDTO);
    Result getFolders(Long userId);

    Result getCollectInfo(Long userId, FolderPostDTO folderPostDTO);

    Result deleteFolder(Long userId, Long folderId);
}
