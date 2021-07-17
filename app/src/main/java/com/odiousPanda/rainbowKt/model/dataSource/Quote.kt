package com.odiousPanda.rainbowKt.model.dataSource

class Quote(var main: String = "", var sub: String = "", var att: String = "") {
    init {
        setDefaultQuote()
    }

    fun setDefaultQuote() {
        main = "Hmmmmm..."
        sub = ""
        att = "*"
    }
}