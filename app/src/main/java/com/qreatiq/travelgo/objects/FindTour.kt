package com.qreatiq.travelgo.objects

class FindTour{
    var id : Int? = null
    var title : String? = null
    var content : String? = null
    var imageURL : String? = null

    constructor(id: Int, title: String, content : String, imageURL : String){
        this.id = id
        this.title = title
        this.content = content
        this.imageURL = imageURL
    }

    override fun toString(): String {
        return "FindTour(id=$id, title=$title, content=$content, imageURL=$imageURL)"
    }
}