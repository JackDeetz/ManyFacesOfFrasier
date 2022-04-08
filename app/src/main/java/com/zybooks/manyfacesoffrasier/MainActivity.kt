package com.zybooks.manyfacesoffrasier

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import menu.FrasierOrNilesDialogFragment

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), FrasierOrNilesDialogFragment.FrasierOrNilesListener {
    lateinit var textViewTitle : TextView       //frasier activity Label
    lateinit var textViewTitle2 : TextView      //niles activity label
    lateinit var frasierGridLayout : GridLayout //the whole gridlayout with frasiers pics
    lateinit var nilesGridLayout : GridLayout   //the whole gridlayout with Niles pics
    lateinit var skylineBGImage :ImageView      //background1 for mainActivity
    lateinit var apartmentBGImage :ImageView    //background2 for mainActivity
    private var settingsToggleButtonActivated = false   // a local variable that holds the value of Settings activity's Toggle Button

    private lateinit var gestureDetector: GestureDetectorCompat //local variable that holds a Gesture Detector Object (pinch, expand, fling...)

// onCreate() runs when Activity is first created/activated //Bundle? holds either the activity's saved state, or is null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  //sets the XML layout to the screen

        Log.d(TAG, "OnCreate was called")

        linkLocalVariablesToActivityMainViews()

        setupGestureDetector()

        setupTouchEventHandler()

        callDialogBox()
    }

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