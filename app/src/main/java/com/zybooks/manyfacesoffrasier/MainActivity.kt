package com.zybooks.manyfacesoffrasier

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import menu.FrasierOrNilesDialogFragment
import kotlin.random.Random

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), FrasierOrNilesDialogFragment.FrasierOrNilesListener,
    SensorEventListener {
    lateinit var textViewTitle : TextView       //frasier activity Label
    lateinit var textViewTitle2 : TextView      //niles activity label
    lateinit var frasierGridLayout : GridLayout //the whole gridlayout with frasiers pics
    lateinit var nilesGridLayout : GridLayout   //the whole gridlayout with Niles pics
    lateinit var skylineBGImage :ImageView      //background1 for mainActivity
    lateinit var apartmentBGImage :ImageView    //background2 for mainActivity
    private var settingsToggleButtonActivated = false   // a local variable that holds the value of Settings activity's Toggle Button
    private lateinit var gestureDetector: GestureDetectorCompat //local variable that holds a Gesture Detector Object (pinch, expand, fling...)

    private var mediaPlayer: MediaPlayer? = null    //var that plays audio or video
    private var soundPool: SoundPool? = null
    private var audioList = mutableListOf<Int>()

    private lateinit var frasierImage : ImageView
    private lateinit var frasierAnim : Animation

    private lateinit var sensorManager: SensorManager
    private lateinit var accellSensor: Sensor
    private lateinit var frasierScream : ImageView

    // onCreate() runs when Activity is first created/activated //Bundle? holds either the activity's saved state, or is null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  //sets the XML layout to the screen

        Log.d(TAG, "OnCreate was called")


         //create list of audio resources from res/raw directory
        audioList = mutableListOf(R.raw.sherry_audio, R.raw.sherry_audio_two, R.raw.sherry_audio_three, R.raw.sherry_audio_four, R.raw.sherry_audio_five, R.raw.sherry_audio_six, R.raw.sherry_audio_seven, R.raw.sherry_audio_eight, R.raw.sherry_audio_nine )

        //SoundPool object loads a sherry audio R int value to be played when Sherry SoundPool button clicked
        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool!!.load(baseContext, audioList[Random.nextInt(audioList.size)], 1)


        //set variable for activity_main's frasier head image
        frasierImage = findViewById<ImageView>(R.id.frasier_head)
        //set variable for the frasier animation - loads the xml file with animation directions
        frasierAnim = AnimationUtils.loadAnimation(this, R.anim.frasier_anim)


        //set variable for activity_main's frasier animation (slide-show style animation)
        val frasierAnimationImageView: ImageView = findViewById(R.id.frasier_animation)
        //set the background for the activity_main imageview to slide-show style animation file
        //which loads the images and designates how long each image is shown
        val frameAnimation: AnimationDrawable = frasierAnimationImageView.background as AnimationDrawable
        //begin the slide-show
        frameAnimation.start()


        //load sensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        //set sensorManager to access the accelerometer
        accellSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        //set listener on accelerometer
        //necessary functions for sensor event listener are below and called with every event
        sensorManager.registerListener(this, accellSensor, SensorManager.SENSOR_DELAY_NORMAL)
        //sets the imageview on activity_main to variable
        frasierScream = findViewById(R.id.frasier_screaming)

        linkLocalVariablesToActivityMainViews()

        setupGestureDetector()

        setupTouchEventHandler()

//        callDialogBox()
    }

    //user clicks button to play sherry audio
    fun sherryButtonClick(view: View) {
        //first stops any audio that might be playing
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        //create a media player object with a randomly selected audio file from audioList
        mediaPlayer = MediaPlayer.create(this, audioList[Random.nextInt(audioList.size)])
        //begin playing the media
        mediaPlayer?.start()
        Toast.makeText(this, "Playing sound via MediaPlayer", Toast.LENGTH_SHORT).show()
    }

    //called when Sherry SoundPool button is clicked
    fun sherrySPButtonClick(view: View) {
        //plays the loaded soundpool
        soundPool?.play(1, 1F, 1F, 0, 0, 1F)
        Toast.makeText(this, "Playing sound via SoundPool", Toast.LENGTH_SHORT).show()
    }

    //called when frasier head image is clicked
    fun frasierHeadAnimation(view: View) {
        //begins the animation
        frasierImage.startAnimation(frasierAnim)
    }

    //manditory function for sensory event listener, called with every event
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val accelValue0: Float = event.values[0]
            Log.d(TAG, "accelerometer value [0] = $accelValue0")
            if (accelValue0 > 4) {
//              Toast.makeText(this, "SensorChanged Logged at $accelValue0", Toast.LENGTH_SHORT).show()
                frasierScream.visibility = View.VISIBLE
            }
            else{

                frasierScream.visibility = View.INVISIBLE
            }
        }
    }

    //manditory function for sensory event listener
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }


    private fun setupTouchEventHandler() {
        var xDown = 0
        var yDown = 0
        textViewTitle.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    xDown = event.x.toInt()
                    yDown = event.y.toInt()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if ( (event.x.toInt()) > xDown + 200) {
                        skylineBGImage.visibility = View.INVISIBLE
                        apartmentBGImage.visibility = View.VISIBLE
                    }
                    if ( (event.x.toInt()) + 200 < xDown){
                        skylineBGImage.visibility = View.VISIBLE
                        apartmentBGImage.visibility = View.INVISIBLE
                    }
                    if (event.y.toInt()- 100 > yDown)  {
                        callDialogBox()
                    }
                    true
                }
                else -> {true}
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetectorCompat(this,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent?): Boolean {
                    return true
                }

                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    callDialogBox()
                    return true
                }

                override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float,
                                     velocityY: Float): Boolean {
                    callDialogBox()
                    return true
                }
            }
        )
    }

    private fun linkLocalVariablesToActivityMainViews() {
        //var links to Views in activity_main
        //title textViews
        textViewTitle = findViewById(R.id.title_textview)
        textViewTitle2 = findViewById(R.id.title_textview2)


        //labels and pictures
        frasierGridLayout = findViewById(R.id.gridLayoutFrasier)
        nilesGridLayout = findViewById(R.id.gridLayoutNiles)
        //background images
        skylineBGImage = findViewById(R.id.skylineImage)
        apartmentBGImage = findViewById(R.id.apartmentImage)
        Log.d(TAG, "activity_main's views assigned to MainActivity variables")
    }

    //calls the DialogFragment "FrasierOrNilesDialogFragment"
    fun callDialogBox() {
        val dialog = FrasierOrNilesDialogFragment()
        dialog.show(supportFragmentManager, "warningDialog")
    }


    //user clicks button to move to Settings activity
    fun settingsButtonClick(view: View)   {
        //Create Intent object to hold all information to use in Settings Activity
        val intent = Intent(this, Settings::class.java)
        //adds     a       Key,        Value    to the intent to be passed to next Activity
        intent.putExtra(SETTINGS_BOOL, apartmentBGImage.isVisible)
        //Laun
        settingsResultLauncher.launch(intent)
    }

    private val settingsResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            settingsToggleButtonActivated = result.data!!.getBooleanExtra(SETTINGS_BOOL, false)
            if (settingsToggleButtonActivated) {
                skylineBGImage.visibility = View.INVISIBLE
                apartmentBGImage.visibility = View.VISIBLE
            }
            else {
                skylineBGImage.visibility = View.VISIBLE
                apartmentBGImage.visibility = View.INVISIBLE
            }
        }
    }

    //Context Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Determine which menu option was selected
        return when (item.itemId) {
            R.id.action_frasier -> {
                //context menu user selected frasier, hide niles Views and make frasier Views visible
                textViewTitle.visibility = View.VISIBLE
                textViewTitle2.visibility = View.INVISIBLE
                frasierGridLayout.visibility = View.VISIBLE
                nilesGridLayout.visibility = View.INVISIBLE
                true
            }
            R.id.action_niles -> {
                //vice versa
                textViewTitle.visibility = View.INVISIBLE
                textViewTitle2.visibility = View.VISIBLE
                frasierGridLayout.visibility = View.INVISIBLE
                nilesGridLayout.visibility = View.VISIBLE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    //Dialog Box interface abstract function defined, FrasierOrNilesDialogFragment
    override fun userSelectsFraiser(dialog: DialogFragment) {
        //set frasier to visible
        Log.d(TAG, "userSelectsFraiser: Function evoked")

        textViewTitle.visibility = View.VISIBLE
        textViewTitle2.visibility = View.INVISIBLE
        frasierGridLayout.visibility = View.VISIBLE
        nilesGridLayout.visibility = View.INVISIBLE
    }

    override fun userSelectsNiles(dialog: DialogFragment) {
        Log.d(TAG, "userSelectsNiles: Function evoked")
        textViewTitle.visibility = View.INVISIBLE
        textViewTitle2.visibility = View.VISIBLE
        frasierGridLayout.visibility = View.INVISIBLE
        nilesGridLayout.visibility = View.VISIBLE

    }


}