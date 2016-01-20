package authdemo.authorizationservice.jwt

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtTokenEndpointAuthenticationFilter(

        private val authenticationManager: AuthenticationManager,
        private val entryPoint: OAuth2AuthenticationEntryPoint = OAuth2AuthenticationEntryPoint()

) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {

        val req = request as HttpServletRequest
        val res = response as HttpServletResponse

        try {
            apply(req)
            chain.doFilter(request, response)
        } catch (e: AuthenticationException) {
            SecurityContextHolder.clearContext()
            entryPoint.commence(req, res, e)
        }
    }

    private fun apply(request: HttpServletRequest) {

        val grantType = request.getParameter("grant_type")
        if (!"client_credentials".equals(grantType)) {
            return
        }

        val clientAssertionType = request.getParameter("client_assertion_type")
        if (!"urn:ietf:params:oauth:client-assertion-type:jwt-bearer".equals(clientAssertionType)) {
            return
        }

        val clientAssertion = request.getParameter("client_assertion")
        val jwt = JwtHelper.decode(clientAssertion)
        val token = JwtToken(jwt)

        val auth = authenticationManager.authenticate(token)

        SecurityContextHolder.getContext().authentication = auth
    }
}