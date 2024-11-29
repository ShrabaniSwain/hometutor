package com.flyngener.tutorhub

import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat

class Utility {

    companion object{

        fun itemBackGround(view: View){
            val cornerRadius = view.context.resources.getDimensionPixelSize(R.dimen.dp_10)
            val shapeDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(ContextCompat.getColor(view.context, R.color.white))
            }
            shapeDrawable.cornerRadius = cornerRadius.toFloat()
            view.background = shapeDrawable

            view.setOnClickListener {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.red))

                Handler(Looper.getMainLooper()).postDelayed({
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            view.context,
                            android.R.color.white
                        )
                    )
                    shapeDrawable.cornerRadius = cornerRadius.toFloat()
                    val borderWidth = view.context.resources.getDimensionPixelSize(R.dimen.dp_2)
                    shapeDrawable.setStroke(
                        borderWidth,
                        ContextCompat.getColor(view.context, R.color.red)
                    )
                    view.background = shapeDrawable
                }, 50)
            }

            var isItemSelected = false
            val borderWidth = view.context.resources.getDimensionPixelSize(R.dimen.dp_2)
            view.setOnClickListener {

                if (!isItemSelected) {
                    // First click: change background to red
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            view.context,
                            R.color.red
                        )
                    )

                    Handler(Looper.getMainLooper()).postDelayed({
                        view.setBackgroundColor(
                            ContextCompat.getColor(
                                view.context,
                                android.R.color.white
                            )
                        )
                        shapeDrawable.cornerRadius = cornerRadius.toFloat()
                        shapeDrawable.setStroke(
                            borderWidth,
                            ContextCompat.getColor(view.context, R.color.red)
                        )
                        view.background = shapeDrawable
                    }, 50)
                    isItemSelected = true
                } else {
                    // Second click: change background to white, add stroke
                    shapeDrawable.setColor(ContextCompat.getColor(view.context, R.color.white))
                    shapeDrawable.setStroke(
                        borderWidth,
                        ContextCompat.getColor(view.context, R.color.white)
                    )
                    view.background = shapeDrawable
                    isItemSelected = false
                }

            }
        }
    }

}