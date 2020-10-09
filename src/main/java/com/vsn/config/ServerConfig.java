package com.vsn.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ServerConfig {

    @Value("${eth.directory}")
    private  String ethWalletDirectoryNode;

    @Bean
    public String ethWalletDirectory(){
        return  ethWalletDirectoryNode;
    }

    @Bean
    public String ethWalletPassword(){
        return "hDiGqYfjTVpT8/lceJvC/fzf13NFwKxtR7seVGnA";
    }


    @Bean
    public String clientAddress(
            @Value("${application.client.protocol}") String protocol,
            @Value("${application.client.host}") String host,
            @NotNull @Value("${application.client.port}") String port
    ) {
        if(port.equals(""))
        return protocol + "://" + host;

        return protocol + "://" + host + ":" + port;
    }
}