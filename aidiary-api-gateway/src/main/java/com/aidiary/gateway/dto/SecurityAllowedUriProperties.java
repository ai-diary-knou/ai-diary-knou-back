package com.aidiary.gateway.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SecurityAllowedUriProperties {

    private PermittedAllUri permittedAll;
    private String[] authenticated;

    @Getter
    @Setter
    public static class PermittedAllUri {
        private String[] any;
        private String[] post;
    }

}
