package com.example.cashlessfuel

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import java.util.*


class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val adsElement = Element()
        adsElement.setTitle("Advertise with us")

        val aboutPage: View = AboutPage(this)
            .isRTL(false)
            .setImage(R.drawable.about)
            .addItem(Element().setTitle("Version alpha-1.0"))
            .addItem(adsElement)
            .addGroup("Connect with us")
            .addEmail("cashlessfuel@gmail.com")
            .addWebsite("https://cashlessfuel.au/")
            .addFacebook("cashlessfuel")
            .addTwitter("cashlessfuel")
            .addPlayStore("com.cashlessfuel")
            .addInstagram("cashlessfuel_au")
            .addItem(getCopyRightsElement())
            .create()

        setContentView(aboutPage)
    }

    fun getCopyRightsElement(): Element? {
        val copyRightsElement = Element()
        val copyrights =
            String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR))
        copyRightsElement.setTitle(copyrights)
        copyRightsElement.setIconDrawable(R.drawable.about)
        copyRightsElement.autoApplyIconTint = true
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color)
        copyRightsElement.iconNightTint = R.color.white
        copyRightsElement.gravity = Gravity.CENTER
        copyRightsElement.onClickListener = View.OnClickListener {
            Toast.makeText(this@AboutUsActivity, copyrights, Toast.LENGTH_SHORT).show()
        }
        return copyRightsElement
    }
}