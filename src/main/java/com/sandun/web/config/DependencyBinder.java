package com.sandun.web.config;

import com.sandun.web.service.LocationService;
import com.sandun.web.service.UserService;
import com.sandun.web.service.UserTypeService;
import com.sandun.web.util.JWTUtil;
import jakarta.inject.Singleton;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class DependencyBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(UserService.class).to(UserService.class).in(Singleton.class);
        bind(UserTypeService.class).to(UserTypeService.class).in(Singleton.class);
        bind(LocationService.class).to(LocationService.class).in(Singleton.class);
        bind(JWTUtil.class).to(JWTUtil.class).in(Singleton.class);
    }
}
