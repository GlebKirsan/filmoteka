package com.example.filmoteka

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import data.FilmraryDbHelper
import data.SeriesContract.Series
import kotlinx.android.synthetic.main.activity_info_serias.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SeriasInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_serias)
        appendText(header_text_view, readField(Series.COLUMN_NAME, intent))

        appendText(seriesName, readField(Series.COLUMN_NAME, intent))
        appendText(seriesYear, readField(Series.COLUMN_START_YEAR, intent))
        appendText(seriesGenre, readField(Series.COLUMN_GENRE, intent))
        appendText(seriesCountry, readField(Series.COLUMN_COUNTRY, intent))
        appendText(seriesProducer, readField(Series.COLUMN_PRODUCER, intent))
        appendText(seriesIMDB, readField(Series.COLUMN_IMDB, intent))
        appendText(seriesKinopoisk, readField(Series.COLUMN_KINOPOISK, intent))
        appendText(seriesDescription, readField(Series.COLUMN_DESCRIPTION, intent))
        appendText(series_info_state, readField(Series.COLUMN_STATE, intent))
        appendText(series_info_season_number, readField(Series.COLUMN_SEASONS_NUM, intent))
        appendText(series_info_episodes_a_season, readField(Series.COLUMN_EP_IN_SEASON_NUM, intent))
        appendText(series_info_episode_length, readField(Series.COLUMN_EP_DURATION, intent))

        addPlayer(intent.getStringExtra(Series.COLUMN_LINK).toString())
    }


    private fun appendText(field: TextView, text: String): Unit = field.append(" $text")
    private fun readField(field: String, intent: Intent) = intent.getStringExtra(field).toString()
    private fun getFirstPart(textField: TextView) = textField.text.toString().substringBefore(" ")

    override fun onBackPressed() {
        seriesName.text = getFirstPart(seriesName)
        seriesYear.text = getFirstPart(seriesYear)
        seriesGenre.text = getFirstPart(seriesGenre)
        seriesCountry.text = getFirstPart(seriesCountry)
        seriesProducer.text = getFirstPart(seriesProducer)
        seriesIMDB.text = getFirstPart(seriesIMDB)
        seriesKinopoisk.text = getFirstPart(seriesKinopoisk)
        seriesDescription.text = getFirstPart(seriesDescription)
        series_info_state.text = getFirstPart(series_info_state)
        series_info_season_number.text = getFirstPart(series_info_season_number)
        series_info_episodes_a_season.text = getFirstPart(series_info_episodes_a_season)
        series_info_episode_length.text = getFirstPart(series_info_episode_length)
        super.onBackPressed()
    }

    fun onClick(view: View) {
        val filmId = intent.getStringExtra(Series._ID)
        val db = FilmraryDbHelper.getInstance(this).writableDatabase
        val deleted = db.delete(Series.TABLE_NAME, "${Series._ID} = $filmId", null)
        Log.d("seriasInfoDeleteMovies", "Deleted $deleted rows", null)
        intent.setClass(this, MainActivitySerias::class.java)
        startActivity(intent)
        finish()
    }

    fun onClickEdit(view: View) {
        intent.setClass(this, SeriasInfoEditor::class.java)
        startActivity(intent)
    }

    fun addPlayer(url: String) {
        val pattern = "http(?:s?)://(?:www\\.)?youtu(?:be\\.com/watch\\?v=|\\.be/)([\\w\\-_]*)(&(amp;)?" +
                "\u200C\u200B[\\w?\u200C\u200B=]*)?"

        val compiledPattern: Pattern = Pattern.compile(pattern)
        val matcher: Matcher = compiledPattern.matcher(url)

        if (!matcher.find()) {
            return
        }
        third_party_player_view.visibility = View.VISIBLE
        third_party_player_view.getPlayerUiController().showFullscreenButton(true)
        third_party_player_view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                val videoId = matcher.group(1)
                youTubePlayer.cueVideo(videoId!!, 0f)
            }
        })

        third_party_player_view.getPlayerUiController().setFullScreenButtonClickListener(View.OnClickListener {
            if (third_party_player_view.isFullScreen()) {
                third_party_player_view.exitFullScreen()
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                // Show ActionBar
                if (supportActionBar != null) {
                    supportActionBar!!.show()
                }
            } else {
                third_party_player_view.enterFullScreen()
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                // Hide ActionBar
                if (supportActionBar != null) {
                    supportActionBar!!.hide()
                }
            }
        })
    }
}
