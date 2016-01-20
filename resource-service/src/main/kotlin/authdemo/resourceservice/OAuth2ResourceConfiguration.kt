package authdemo.resourceservice

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.provider.token.RemoteTokenServices
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices

/**
 * This class contains all endpoint security configuration.
 */
@Configuration
@EnableResourceServer
@EnableWebSecurity
open class OAuth2ResourceConfiguration : ResourceServerConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        // @formatter:off
        http!!
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
            .requestMatchers()
            .antMatchers("/**")
        .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/quotes/**").permitAll()
            .antMatchers(HttpMethod.GET, "/quotes/**").access("#oauth2.hasScope('quotes')")
        // @formatter:on
    }

    /**
     * Spring authorization-service provides an "introspection" endpoint
     * called "/check_token". It predates the "introspection" spec and hasn't
     * been updated to match the spec.
     *
     * See: https://tools.ietf.org/html/draft-ietf-oauth-introspection-11
     */
    @Bean
    open fun tokenService(): ResourceServerTokenServices {
        return RemoteTokenServices().apply {
            // Same client credentials as used in "authorization-service" config.
            setClientId("demo-rs")
            setClientSecret("rs-secret")
            setCheckTokenEndpointUrl("http://localhost:8180/oauth/check_token")
        }
    }
}