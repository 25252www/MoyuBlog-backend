package com.moyu.common.dto;

import com.moyu.DO.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO extends Comment {
    private String username;
    private String avatar;
    private List<CommentDTO> children;
}
