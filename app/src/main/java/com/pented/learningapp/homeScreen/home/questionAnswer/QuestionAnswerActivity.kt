package com.pented.learningapp.homeScreen.home.questionAnswer

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.pented.learningapp.BR
import com.pented.learningapp.R
import com.pented.learningapp.base.BaseActivity
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.base.BindingAdapter
import com.pented.learningapp.databinding.ActivityQuestionAnswerBinding
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.homeScreen.home.model.VideoQuestionResponseModel
import com.pented.learningapp.homeScreen.home.questionAnswer.model.OptionModel
import com.pented.learningapp.homeScreen.home.questionAnswer.viewModel.QuestionAnswerVM
import com.pented.learningapp.homeScreen.home.subjectTopic.ChapterWithAnimation2Activity
import com.pented.learningapp.homeScreen.scanQR.activity.WatchQRVideoActivity
import kotlinx.android.synthetic.main.activity_question_answer.*
import kotlinx.android.synthetic.main.activity_question_answer.mainFrame

class QuestionAnswerActivity : BaseActivity<ActivityQuestionAnswerBinding>() {

    override fun layoutID() = R.layout.activity_question_answer
    override fun viewModel(): BaseViewModel =
        ViewModelProvider(this).get(QuestionAnswerVM::class.java)
    var position = 0
    var correctAnswer = 0
    lateinit var questionAnswerVM: QuestionAnswerVM
    var optionList = ArrayList<OptionModel>()
    var selectedPosition = -1
    var questionList = ArrayList<VideoQuestionResponseModel.Question>()
    var questionResponseModel : VideoQuestionResponseModel.Data ? = null
    companion object{
        var questionID:String ? = null
    }
    override fun initActivity() {
        questionAnswerVM = (getViewModel() as QuestionAnswerVM)
        init()
        observer()
        listener()
    }

    private fun observer() {
        questionAnswerVM.observedErrorMessageChanges().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                showErrorMessage(it, this, mainFrame)
            }
        })

        questionAnswerVM.observedtopicVideoDataData().observe(this, { event ->
            event?.getContentIfNotHandled()?.let {
                questionList.clear()
                questionResponseModel = it.data
                it.data.Questions?.let { it1 -> questionList.addAll(it1) }
                setQuestionAndOption(position)

            }
        })
    }

    fun setQuestionAndOption(position:Int)
    {
        if(position < questionList.size)
        {
            scrollViewSolution.visibility = View.GONE
            btnSubmit.visibility = View.VISIBLE
            optionList.clear()
            titleHeading.text = "${getString(R.string.question_answers)} (${position+1} / ${questionList.size})"
            txtTotalQuestions.text = "${correctAnswer*5} / ${questionList.size * 5} "
            txtQuestion.text = questionList[position].QuestionTitle
            Constants.videoNameForSolution = questionList[position].QuestionTitle
            txtDescription.text = questionList[position].QuestionDetails
            var tempOptionList = questionList[position].MCQ?.split(",") as ArrayList<String>
            for (option in tempOptionList)
            {
                var optionModel = OptionModel(option)
                optionList.add(optionModel)
            }
            selectedPosition = -1
            setOptionAdapter()
        }

    }
    fun setOptionAdapter() {
        rvOptions.visibility = View.VISIBLE
        rvOptions.adapter = BindingAdapter(
            layoutId = R.layout.row_options,
            br = BR.model,
            list = ArrayList(optionList),
            clickListener = { view, position ->
                when (view.id) {
                    R.id.lilOption -> {
                        for (options in optionList)
                        {
                            options.status = ""
                        }
                        optionList[position].status = "Selected"
                        rvOptions.adapter?.notifyDataSetChanged()
                        if(optionList.any { it.status == "Selected" })
                        {
                            btnSubmit.visibility = View.VISIBLE
                        }
                        else{
                            btnSubmit.visibility = View.GONE
                        }
                        selectedPosition  = position
                    }
//                    R.id.txtOption ->{
//                        for (options in optionList)
//                        {
//                            options.status = ""
//                        }
//                        optionList[position].status = "Selected"
//                        rvOptions.adapter?.notifyDataSetChanged()
//                    }

                }
            })
    }

    private fun listener() {
        lilOptionA.setOnClickListener {
            setOptionSelected(lilOptionA, txtOptionA)
        }
        lilOptionB.setOnClickListener {
            setOptionSelected(lilOptionB, txtOptionB)
        }
        lilOptionC.setOnClickListener {
            setOptionSelected(lilOptionC, txtOptionC)
        }
        lilOptionD.setOnClickListener {
            setOptionSelected(lilOptionD, txtOptionD)
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
        btnGoBack.setOnClickListener {
            onBackPressed()
        }
        btnNext.setOnClickListener {
         if(position == questionList.size)
         {
             finish()
             if(Constants.isFromNormalVideoList)
             {
                 sendBroadcast(Intent("PlayNextNormalVideo"))
             }
             else
             {
                 sendBroadcast(Intent("StartNowTopicVideo"))
             }

         }
            else{
             setQuestionAndOption(position)
         }
        }
        lilWatchVideoAgain.setOnClickListener {
            var intent = Intent(this@QuestionAnswerActivity, WatchAnswerVideoActivity::class.java)
            //  intent.putExtra("topicVideoId",topicDataList[current].TopicVideoId)
            questionID = questionList[position-1].Id?.toString()
            intent.putExtra("answerVideo",Gson().toJson(questionList[position-1]))
            Log.e("answerVideo===",Gson().toJson(questionList[position-1]))
            startActivity(intent)
//            startActivityWithDataKey(WatchAnswerVideoActivity::class.java,
//                Gson().toJson(questionList[position-1].S3Bucket),"answerVideo")
//
        }
        btnSubmit.setOnClickListener {
            var isCorrect = false
            if(selectedPosition != -1)
            {
                if( optionList[selectedPosition].title == questionResponseModel?.Questions?.get(position)?.CorrectAns)
                {
                    optionList[selectedPosition].status = "Correct"
                    isCorrect = true
                    rvOptions.adapter?.notifyDataSetChanged()
                }
                else
                {
                    optionList[selectedPosition].status = "Wrong"
                    isCorrect = false
                    rvOptions.adapter?.notifyDataSetChanged()
                }
                scrollViewSolution.visibility = View.VISIBLE
                rvOptions.visibility = View.GONE
                btnSubmit.visibility = View.GONE
                if(isCorrect)
                {
                    correctAnswer = correctAnswer+1
                    lil_correct_answer.visibility = View.VISIBLE
                    lil_incorrect_answer.visibility = View.GONE
                    txtTotalQuestions.text = "${correctAnswer*5} / ${questionList.size * 5} "

                    questionAnswerVM.earnPointsRequestModel.ModuleId = questionList[position].Id.toString()
                    questionAnswerVM.earnPointsRequestModel.PointType = "QuestionAnswer"
                    questionAnswerVM.earnPointsRequestModel.SubjectId = Constants.subjectId
                    questionAnswerVM.earnPointsRequestModel.Point = "5"
                    questionAnswerVM.addPoints()
                }
                else
                {
                    lil_correct_answer.visibility = View.GONE
                    lil_incorrect_answer.visibility = View.VISIBLE
                }
                txtSolutionDescription.text = questionList[position].SolutuionDetails
                if(questionList[position].S3Bucket != null)
                {
                    lilWatchVideoAgain.visibility = View.VISIBLE
                }
                else{
                    lilWatchVideoAgain.visibility = View.GONE
                }

                position++
                if(position == questionList.size)
                {
                    btnNext.text = "Next Video"
                }
            }
            else{
                Toast.makeText(this@QuestionAnswerActivity,"Please select atleast one option",Toast.LENGTH_SHORT).show()
            }

        }
    }


    private fun setOptionSelected(view: View, txtOption: TextView) {
        lilOptionA.setBackgroundResource(R.drawable.option_bg)
        lilOptionB.setBackgroundResource(R.drawable.option_bg)
        lilOptionC.setBackgroundResource(R.drawable.option_bg)
        lilOptionD.setBackgroundResource(R.drawable.option_bg)
        txtOptionA.setTextColor(
            ContextCompat.getColor(
                this@QuestionAnswerActivity,
                R.color.resend_otp_gray
            )
        )
        txtOptionB.setTextColor(
            ContextCompat.getColor(
                this@QuestionAnswerActivity,
                R.color.resend_otp_gray
            )
        )
        txtOptionC.setTextColor(
            ContextCompat.getColor(
                this@QuestionAnswerActivity,
                R.color.resend_otp_gray
            )
        )
        txtOptionD.setTextColor(
            ContextCompat.getColor(
                this@QuestionAnswerActivity,
                R.color.resend_otp_gray
            )
        )

        view.setBackgroundResource(R.drawable.option_blue_bg)
        txtOption.setTextColor(ContextCompat.getColor(this@QuestionAnswerActivity, R.color.white))
    }

    private fun init() {
        if(intent.hasExtra("VideoId"))
        {
            var videoId = intent.getIntExtra("VideoId",0)
            questionAnswerVM.getQuestionAnswer(videoId.toString())
        }

    }
}