package com.example.sweater.domain.input;

import lombok.Data;

@Data//сущность для input в graphql
public class MessageInput {

    private Long id;

    private String filename;

    private String tag;

    private String text;

    private Long authorId;
}
