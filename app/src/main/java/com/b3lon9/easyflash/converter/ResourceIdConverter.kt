package com.b3lon9.easyflash.converter

import android.content.Context
import android.graphics.drawable.Drawable

class ResourceIdConverter {
    companion object {
        private lateinit var context:Context
        private val instance:ResourceIdConverter = ResourceIdConverter()

        fun init(context:Context) {
            this.context = context
        }


        fun convert(id:Int) : Drawable? {

            return null
        }


        fun convert(drawable:Drawable) : Int {

            return 0
        }
    }
}