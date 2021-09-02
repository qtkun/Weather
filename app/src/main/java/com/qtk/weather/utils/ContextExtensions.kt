package com.qtk.weather.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import org.jetbrains.anko.ctx

fun Context.color(res : Int) : Int = ContextCompat.getColor(this, res)

fun Context.drawable(res: Int): Drawable? = ContextCompat.getDrawable(this, res)

fun Context.string(res: Int): String = this.getString(res)

fun Context.drawable(name: String): Drawable? = drawable(resourceId(name))

fun Context.resourceId(name: String): Int = resources.getIdentifier(name, "mipmap", packageName)