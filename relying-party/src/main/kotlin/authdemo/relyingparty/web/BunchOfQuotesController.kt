package authdemo.relyingparty.web

import authdemo.relyingparty.service.QuoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.collections.distinct
import kotlin.collections.filter
import kotlin.text.contains


@RestController
class BunchOfQuotesController @Autowired constructor(
        val quoteService: QuoteService
){

    val quotes = ConcurrentLinkedDeque<String>()

    // @Scheduled even supports cron style syntax OMG!
    @Scheduled(fixedDelay = 500)
    fun harvestQuotes() {
        val quote = quoteService.fetchRandomQuote()
        quotes.add(quote)
    }

    @RequestMapping("/quotes/{term}", method = arrayOf(RequestMethod.GET))
    fun searchBunchOfQuotes(@PathVariable term: String): List<String> {
        return quotes.distinct().filter { it.contains(term, ignoreCase = true) }
    }

}