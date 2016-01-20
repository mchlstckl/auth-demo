package authdemo.resourceservice.web

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.AsyncRestTemplate
import org.springframework.web.context.request.async.DeferredResult

data class Quote(
        val id: Long = -1,
        val quote: String = ""
)

data class QuoteResource(
        val type: String = "",
        val value: Quote = Quote()
)

@RestController
class QuotesController {

    val rest = AsyncRestTemplate()

    @RequestMapping("/quotes/{id}", method = arrayOf(RequestMethod.GET))
    fun getQuote(@PathVariable id: Long): DeferredResult<Quote> {

        val result = DeferredResult<Quote>()
        val future = rest.getForEntity("http://gturnquist-quoters.cfapps.io/api/$id", QuoteResource::class.java)
        future.addCallback(
                { success -> result.setResult(success.body.value) },
                { failure -> result.setErrorResult(failure) }
        )
        return result
    }

    @RequestMapping("/quotes/random", method = arrayOf(RequestMethod.GET))
    fun getRandomQuote(): DeferredResult<Quote> {

        val result = DeferredResult<Quote>()
        val future = rest.getForEntity("http://gturnquist-quoters.cfapps.io/api/random", QuoteResource::class.java)
        future.addCallback(
                { success -> result.setResult(success.body.value) },
                { failure -> result.setErrorResult(failure) }
        )
        return result
    }

    @RequestMapping("/hello", method = arrayOf(RequestMethod.GET))
    fun getHello() = "Hello, you ;) No secrets here!"

}