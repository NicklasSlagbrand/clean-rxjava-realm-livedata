package com.nicklasslagbrand.placeholder.feature.basket

import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.feature.purchase.order.SuccessfulPaymentData
import com.nicklasslagbrand.placeholder.feature.purchase.payment.PaymentJsonParser
import com.nicklasslagbrand.placeholder.testutils.failIfError
import com.nicklasslagbrand.placeholder.testutils.failIfSuccess
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test

class PaymentJsonParserTest {
    @Test
    fun `test parser parsers payment json correctly`() {
        val string =
            """{"acceptUrl":"https://dev.copenhagencard.com/bambora/accept?ui=inline&txnid=156870077183641600&orderid=ABC1239&reference=373831854912&amount=15400&currency=DKK&date=20190226&time=1222&feeid=146247&txnfee=0&paymenttype=1&cardno=457174XXXXXX0002&eci=7&issuercountry=DNK&hash=64a373cd1d30c3a4f8eba0d9f85d44b6","data":{"ui":"inline","txnid":"156870077183641600","orderid":"ABC1239","reference":"373831854912","amount":"15400","currency":"DKK","date":"20190226","time":"1222","feeid":"146247","txnfee":"0","paymenttype":"1","cardno":"457174XXXXXX0002","eci":"7","issuercountry":"DNK","hash":"64a373cd1d30c3a4f8eba0d9f85d44b6"}}"""

        val response = PaymentJsonParser.parseJson(string)

        response.fold({
            SuccessfulPaymentData(
                orderId = "ABC1239",
                orderReference = "373831854912",
                amount = "15400",
                currency = "DKK",
                date = "20190226"
            )
        }, ::failIfError)
    }

    @Test
    fun `test parser returns failure for invalid json`() {
        val string =
            """{"acceptUrl":"","data": null}"""

        val response = PaymentJsonParser.parseJson(string)

        response.fold(::failIfSuccess) {
            it.shouldBeInstanceOf(Error.GeneralError::class)
            (it as Error.GeneralError).exception.shouldBeInstanceOf(IllegalStateException::class)
        }
    }
}
