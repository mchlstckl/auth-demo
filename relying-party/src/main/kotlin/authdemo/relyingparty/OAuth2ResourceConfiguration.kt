package authdemo.relyingparty

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter

@Configuration
@EnableResourceServer
@EnableWebSecurity
open class OAuth2ResourceConfiguration : ResourceServerConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        // @formatter:off
        http!!
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/**").permitAll()
        // @formatter:on
    }
}