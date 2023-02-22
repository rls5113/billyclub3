package com.billyclub.points.config;

import com.billyclub.points.model.Event;
import com.billyclub.points.model.Player;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = {"com.billyclub.points.model"})
@EnableJpaRepositories(basePackages = {"com.billyclub.points.repository"})
@EnableTransactionManagement
public class RepositoryConfig {

    @Autowired
    RepositoryRestConfiguration repositoryRestConfiguration;

    @PostConstruct
    public void init() {
        repositoryRestConfiguration.exposeIdsFor(Event.class, Player.class);
        repositoryRestConfiguration.setReturnBodyForPutAndPost(true);
        repositoryRestConfiguration.setReturnBodyOnCreate(true);
        repositoryRestConfiguration.setReturnBodyOnUpdate(true);
    }
}
