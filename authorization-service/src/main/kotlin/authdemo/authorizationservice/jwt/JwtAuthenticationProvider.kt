package authdemo.authorizationservice.jwt

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException
import org.springframework.security.jwt.crypto.sign.RsaVerifier
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.NoSuchClientException
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

class JwtAuthenticationProvider(

        private val clientDetailsService: ClientDetailsService

) : AuthenticationProvider {

    override fun supports(authentication: Class<*>?): Boolean {
        return JwtToken::class.java.isAssignableFrom(authentication)
    }

    override fun authenticate(authentication: Authentication): Authentication {
        try {
            val token = authentication as JwtToken
            val clientId = token.principal
            val client = clientDetailsService.loadClientByClientId(clientId)

            val publicKey64 = client.additionalInformation["public_key"] as String
            val publicKey = toRsaPublicKey(publicKey64)
            val verifier = RsaVerifier(publicKey)

            token.verifySignature(verifier)
            token.isAuthenticated = true

            return token
        } catch (e: NoSuchClientException) {
            throw UnapprovedClientAuthenticationException("Client not found", e)
        } catch (e: InvalidSignatureException) {
            throw BadCredentialsException("JWT signature invalid", e)
        }
    }

    private fun toRsaPublicKey(base64PublicKey: String): RSAPublicKey {
        val publicKeyBytes = Base64.getDecoder().decode(base64PublicKey)
        val keySpec = X509EncodedKeySpec(publicKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec) as RSAPublicKey
    }
}