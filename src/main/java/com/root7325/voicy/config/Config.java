package com.root7325.voicy.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author root7325 on 21.06.2025
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {
    private TgConfig tgConfig;
    private MiscConfig miscConfig;
}
