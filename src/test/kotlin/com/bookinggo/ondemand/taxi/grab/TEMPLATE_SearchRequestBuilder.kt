package com.bookinggo.ondemand.taxi.grab


object TEMPLATE_SearchRequestBuilder {

    @JvmStatic
    fun searchRequestPayload(customerAccountId: String) = """

        {
       "customerAccountId": "$customerAccountId",
        "from":{
        "latitude":40.058922,
        "longitude":116.312615,
        "name":"西二旗地铁站"

    },
        "to":{
        "latitude":39.998568,
        "longitude":116.344434,
        "name":"五道口购物中心"
    }
    }
    """

    @JvmStatic
    fun searchRequestPayloadInvalid(customerAccountId: String) = """

        {
       "customerAccountId": "$customerAccountId",
        "from":{
        "latitude":40.058922,
        "longitude":116.312615,
        "name":"西二旗地铁站"

    },
        "to":{
        "latitude":99.998568,
        "longitude":999.344434,
        "name":"五道口购物中心"
    }
    }
    """
}


