package org.ugonna.springboot.keycloak.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.ugonna.springboot.keycloak.exception.CustomKeycloakAuthenticationHandler;
import org.ugonna.springboot.keycloak.exception.RestAccessDeniedHandler;

// Defines all annotations that are needed to integrate Keycloak in Spring Security
@KeycloakConfiguration
class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Autowired
    RestAccessDeniedHandler restAccessDeniedHandler;

    @Autowired
    CustomKeycloakAuthenticationHandler customKeycloakAuthenticationHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.csrf().disable().cors().disable()
                .authorizeRequests()
                .antMatchers("/login", "/random").permitAll()
                .antMatchers("/visitor").hasRole("visitor")
                .antMatchers("/admin").hasRole("admin")
                .anyRequest()
                .authenticated();

        //Custom error handler
        http.exceptionHandling().accessDeniedHandler(restAccessDeniedHandler);
    }

    // Disable default role prefix ROLE_
    @Autowired
    public void configureGlobal( AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    // Use Spring Boot property files instead of default keycloak.json
    @Bean
    public KeycloakSpringBootConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    // Register authentication strategy for public or confidential applications
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    //Keycloak auth exception handler
    @Bean
    @Override
    protected KeycloakAuthenticationProcessingFilter keycloakAuthenticationProcessingFilter() throws Exception {
        KeycloakAuthenticationProcessingFilter filter = new KeycloakAuthenticationProcessingFilter(this.authenticationManagerBean());
        filter.setSessionAuthenticationStrategy(this.sessionAuthenticationStrategy());
        filter.setAuthenticationFailureHandler(customKeycloakAuthenticationHandler);
        return filter;
    }


}