package authdemo.relyingparty

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestOperations
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client
import kotlin.collections.listOf

@Configuration
@EnableOAuth2Client
open class OAuth2ClientConfiguration {

    val tokenUrl = "http://localhost:8180/oauth/token"

    @Bean
    open fun resource(): OAuth2ProtectedResourceDetails {
        return ClientCredentialsResourceDetails().apply {
            clientId = "demo-rp"
            clientSecret = "rp-secret"
            grantType = "client_credentials"
            scope = listOf("quotes")
            accessTokenUri = tokenUrl
        }
    }

    @Bean
    open fun restTemplate(): OAuth2RestOperations {
        return OAuth2RestTemplate(
                resource(),
                DefaultOAuth2ClientContext(DefaultAccessTokenRequest()))
    }

}