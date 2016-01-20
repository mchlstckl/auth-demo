package authdemo.authorizationservice

import authdemo.authorizationservice.jwt.JwtAuthenticationProvider
import authdemo.authorizationservice.jwt.JwtTokenEndpointAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.ClientDetailsService

@Configuration
@EnableWebSecurity
@EnableAuthorizationServer
open class OAuth2Configuration : WebSecurityConfigurerAdapter(), AuthorizationServerConfigurer {

    @Autowired
    val authenticationManager: AuthenticationManager? = null

    @Autowired
    val clientDetailsService: ClientDetailsService? = null

    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        // @formatter:off
        security
        .tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')")
        .checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')")
        .addTokenEndpointAuthenticationFilter(
                JwtTokenEndpointAuthenticationFilter(authenticationManager!!))
        // @formatter:on
    }

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        // @formatter:off
        clients.inMemory()
        .withClient("demo-rp")
            .authorizedGrantTypes("client_credentials")
            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
            .scopes("quotes")
            .additionalInformation("public_key:MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDb93VjIFuqi3t6bfPVreCEcvjFNjapEle+NviG2jgu/5c2sKTVDamaOspiQwe/W24VfOrLq6YFC5VNAnoCEXlA1xmxVk+I9KTISXuJmVitoDh40TUkRBSxzheaaxw4mBpGniYziyr6QIxfpHM2U02V8wemYDAgIbaYSr4wnFhBwQIDAQAB")
            .accessTokenValiditySeconds(60)
        .and()
        .withClient("demo-rs")
            .authorizedGrantTypes("client_credentials")
            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
            .secret("rs-secret")
            .accessTokenValiditySeconds(60)
        // @formatter:on
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.authenticationManager(authenticationManager)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(JwtAuthenticationProvider(clientDetailsService!!))
    }

    // UNCOMMENT TO ENABLE JWT ACCESS TOKENS
    //    @Bean
    //    open fun accessTokenConverter() = JwtAccessTokenConverter()

    //    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
    // @formatter:off
        // endpoints
        // .authenticationManager(authenticationManager)
        // UNCOMMENT TO ENABLE JWT ACCESS TOKENS
        // Also, look into using a tokenEnhancer if we use JWT access tokens
        //        .accessTokenConverter(accessTokenConverter())
        // @formatter:on
    //}
}