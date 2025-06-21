package com.root7325.voicy.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author root7325 on 21.06.2025
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MiscConfig {
    private String openRouterKey;
    private String voskModelPath;
}