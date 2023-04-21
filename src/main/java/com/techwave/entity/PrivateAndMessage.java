package com.techwave.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_private_message")
public class PrivateAndMessage {
    private Long id;

    private  Long sendId;

    private Long recipientId;

    private String messageText;

    private String sendAt;

    private Long senderDeletedBy;

    private String senderDeletedAt;

    private Long recipientDeletedBy;

    private String recipientDeletedAt;



}
