package com.techwave.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForumUserVO {
     private Long id;

     private String username;

     private String email;

     private String avatar;

     private String gender;

     private boolean is_moderator;

}
