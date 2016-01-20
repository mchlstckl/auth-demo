package authdemo.authorizationservice.jwt

import org.codehaus.jackson.map.ObjectMapper
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.jwt.Jwt
import org.springframework.security.jwt.crypto.sign.SignatureVerifier
import java.util.Collections


class JwtToken(private val jwt: Jwt) : Authentication {

    private var _authenticated: Boolean = false

    private val claims = ObjectMapper().readTree(jwt.claims)

    private val _authorities = if (claims["roles"] != null) {
        claims["roles"].toList().map { SimpleGrantedAuthority(it.textValue) }
    } else {
        Collections.emptyList<GrantedAuthority>()
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        _authenticated = isAuthenticated
    }

    override fun getAuthorities() = _authorities

    override fun isAuthenticated() = _authenticated

    override fun getCredentials() = ""

    override fun getDetails() = claims

    override fun getName() = claims["sub"].asText()

    override fun getPrincipal() = name

    fun verifySignature(verifier: SignatureVerifier) {
        jwt.verifySignature(verifier)
    }
}