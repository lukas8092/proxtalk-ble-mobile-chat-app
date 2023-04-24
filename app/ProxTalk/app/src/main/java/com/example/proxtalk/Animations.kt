package com.example.proxtalk

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.TransitionDrawable
import android.widget.Button
import androidx.core.content.ContextCompat
import com.lukas.proxtalk.R

object Animations {
    /**
     * Class where are animations defined
     */
    fun scanBlink(){
        val mTransition = TransitionDrawable(arrayOf(
            ContextCompat.getDrawable(User.act.application, R.drawable.green_circle),
            ContextCompat.getDrawable(User.act.application, R.drawable.gray_circle)))
        User.act.scanIndicator.background = mTransition
        mTransition.startTransition(520)
    }

    fun serverBlink(){
        val mTransition = TransitionDrawable(arrayOf(ContextCompat.getDrawable(User.act.application, R.drawable.blue_circle),ContextCompat.getDrawable(User.act.application, R.drawable.gray_circle)))
        User.act.serverIndicator.background = mTransition
        mTransition.startTransition(520)
    }

    @SuppressLint("ResourceAsColor")
    fun btnClickAnimation(element: Button){
        element.setTextColor(Color.parseColor("#000000"))
        var transition = element.background as TransitionDrawable
        transition.startTransition(500)
        transition.reverseTransition(350)
        element.setTextColor(Color.parseColor("#FFFFFF"))
    }
}