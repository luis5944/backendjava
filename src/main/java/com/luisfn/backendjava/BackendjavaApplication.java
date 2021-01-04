package com.luisfn.backendjava;

import com.luisfn.backendjava.models.responses.UserRest;
import com.luisfn.backendjava.security.AppProperties;
import com.luisfn.backendjava.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing //para crear la fecha automaticamente en postentity
public class BackendjavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendjavaApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringApplicationContext springApplicationContext() {
        return new SpringApplicationContext();
    }

    @Bean(name = "AppProperties")
    public AppProperties appProperties() {
        return new AppProperties();
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        //Con esto skipeas los posts de userRest
        mapper.typeMap(UserDto.class, UserRest.class).addMappings(m -> m.skip(UserRest::setPosts));

        return mapper;
    }
}
