package com.sql.gaming.backend.pojo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SqlTableResponse {

    private Long id;

    private String tableName;

    private String description;

    private Boolean active;

}
