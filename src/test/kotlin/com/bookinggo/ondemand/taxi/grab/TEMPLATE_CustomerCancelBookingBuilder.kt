package com.bookinggo.ondemand.taxi.grab


object TEMPLATE_CustomerCancelBookingBuilder {

    @JvmStatic
    fun customerCancelBookingPayload() = """
{
    "action": "CANCEL",
    "cancellationRequest": {
        "device":{
            "imei":"986521254170365",
            "suuid":"suuid"
        }
    }
}
"""}


