package com.example.greenlight.Notifications

class Data {

    private  var user: String=""
    private  var icon=0
    private  var body: String=""
    private  var tile: String=""
    private  var sented: String=""

    constructor(){}
    constructor(user: String, icon: Int, body: String, tile: String, sented: String) {
        this.user = user
        this.icon = icon
        this.body = body
        this.tile = tile
        this.sented = sented
    }

    fun getUser(): String?{
        return  user
    }

    fun setUser(user: String){
        this.user=user
    }

    fun getIcon(): Int?{
        return  icon
    }

    fun setIcon(icon: Int){
        this.icon=icon
    }

    fun getBody(): String?{
        return  body
    }

    fun setBody(body: String){
        this.body=body
    }

    fun getTitle(): String?{
        return  tile
    }

    fun setTitle(tile: String){
        this.tile=tile
    }

    fun getSented(): String?{
        return  sented
    }

    fun setSented(sented: String){
        this.sented=sented
    }
}