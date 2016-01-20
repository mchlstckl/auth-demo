package authdemo.relyingparty.service

import authdemo.relyingparty.domain.Quote
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.OAuth2RestOperations
import org.springframework.stereotype.Service


@Service
open class QuoteService @Autowired constructor(
        val restTemplate: OAuth2RestOperations
) {

    val quotesUrl = "http://localhost:8280/quotes"

    open fun fetchRandomQuote(): String {
        val response = restTemplate.getForObject("$quotesUrl/random", Quote::class.java)
        return response.quote
    }

    open fun fetchQuoteById(id: Long): String {
        val response = restTemplate.getForObject("$quotesUrl/$id", Quote::class.java)
        return response.quote
    }
}