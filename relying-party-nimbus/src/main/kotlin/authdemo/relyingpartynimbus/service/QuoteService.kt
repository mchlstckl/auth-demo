package authdemo.relyingpartynimbus.service

import authdemo.relyingpartynimbus.domain.Quote
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant
import com.nimbusds.oauth2.sdk.Scope
import com.nimbusds.oauth2.sdk.TokenRequest
import com.nimbusds.oauth2.sdk.auth.JWTAuthenticationClaimsSet
import com.nimbusds.oauth2.sdk.auth.PrivateKeyJWT
import com.nimbusds.oauth2.sdk.id.Audience
import com.nimbusds.oauth2.sdk.id.ClientID
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

@Service
open class QuoteService {

    val privateKeyBase64 = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANv3dWMgW6qLe3pt89Wt4IRy+MU2NqkSV742+IbaOC7/lzawpNUNqZo6ymJDB79bbhV86surpgULlU0CegIReUDXGbFWT4j0pMhJe4mZWK2gOHjRNSREFLHOF5prHDiYGkaeJjOLKvpAjF+kczZTTZXzB6ZgMCAhtphKvjCcWEHBAgMBAAECgYEAy7VXRA7SdZWpK8+p8iiN9XtYQaMcaeVv1tunu5NaVsQjMOXUMO7vM8LgbLGw+hldUI8YDriPDrsUcHYrscCm7ZrtH9IWFHT5d/aI/f71Q2RAGytMnHtPRVjqOhzJ+786EZNEqRMTUvQKSe7OPFcvz123bZkLsRwqT9tlSVpoXdUCQQDxoYUueYlaY2zYOgCLMNsl1eje7R5K/nndL5rwGfJsTXGEUHdX5PhL80LLfxRXxghybFmpnUEH17jePLk3g1V/AkEA6QwheqW4wwn5zROkT9hFKomBPa2YwPWnNsdDKWH6JESEf6r43kAmFs8RnVuG1yHWulwTU3NgGnnoWJaE7BCIvwJBAKN/UHe/AyKnLgotTRrh5xd144mcVqgWAu2YfbVDSVbhHBOUwvw1UZFKvAKoLWKq/CI4kH8FifA9lrjBy+31Lw8CQAXZfRmkAUA8Bt4j6RLr/ch0jR7sNZJaWCki1Ue7oti4M480zmxdtdcKuYl2m6rYuZgR6ZZFphrkBnX6yTlXrOcCQQC7RZaAgDEgG2akvJD/e0rRyGruZX+Xf0/VzSTKW5n7d2SJY4ZhBLTs9mPjHXRe+c+aV+5LcRSIupTcG0gKqLL/"
    val tokenUrl = "http://localhost:8180/oauth/token"
    val quotesUrl = "http://localhost:8280/quotes/random"

    open fun fetchRandomQuote(): String {

        val privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64)
        val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKey = keyFactory.generatePrivate(keySpec) as RSAPrivateKey

        val jwtAuthenticationClaimsSet = JWTAuthenticationClaimsSet(
                ClientID("demo-rp"),
                Audience("demo-as")
        )

        val privateKeyJWT = PrivateKeyJWT(
                jwtAuthenticationClaimsSet,
                JWSAlgorithm.RS256,
                privateKey,
                "1",
                null
        )
        val tokenEndpoint = URI.create(tokenUrl)
        val clientCredentialsGrant = ClientCredentialsGrant()
        val scopes = Scope.parse("quotes")
        val tokenRequest = TokenRequest(
                tokenEndpoint,
                privateKeyJWT,
                clientCredentialsGrant,
                scopes
        )

        val httpResponse = tokenRequest.toHTTPRequest().send()
        val tokenResponse = AccessTokenResponse.parse(httpResponse)
        val accessToken = tokenResponse.tokens.accessToken

        val headers = HttpHeaders()
        headers.set("Authorization", accessToken.toAuthorizationHeader())
        val entity = HttpEntity<Any>(headers)

        val restTemplate = RestTemplate()
        val quotesResponse = restTemplate.exchange(quotesUrl, HttpMethod.GET, entity, Quote::class.java)

        return quotesResponse.body.quote
    }
}