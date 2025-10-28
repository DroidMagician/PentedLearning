package com.pented.learningapp.homeScreen.leaderboard.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pented.learningapp.authScreens.model.*
import com.pented.learningapp.base.BaseViewModel
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.Event
import com.pented.learningapp.helper.Utils
import com.pented.learningapp.homeScreen.leaderboard.LeaderBoardFragment.Companion.getLeaderboardRequestModel
import com.pented.learningapp.homeScreen.leaderboard.model.GetLeaderboardResponseModel
import com.pented.learningapp.homeScreen.leaderboard.model.MonthListModel
import com.pented.learningapp.retrofit.APITask
import com.pented.learningapp.retrofit.OnResponseListener
import java.text.SimpleDateFormat
import java.util.*


class LeaderboardVM(val context: Application) : BaseViewModel(context), OnResponseListener {
    var leaderboardResponseModel = GetLeaderboardResponseModel()


    var getStudentListResponse = GetSchoolNameResponseModel()
    var getCityListResponse = GetSchoolNameResponseModel()
    var getStandardListResponse = GetDropdownResponseModel()
    var getSchoolNameListResponse = GetSchoolNameResponseModel()


    private var StudentListResponse = MutableLiveData<Event<GetSchoolNameResponseModel>>()
    private fun setStudentListResponseData(attachedAccount: GetSchoolNameResponseModel) {
        this.StudentListResponse.postValue(Event(attachedAccount))
    }

    fun observerStudentListResponseData() = StudentListResponse

    private var CityListResponse = MutableLiveData<Event<GetSchoolNameResponseModel>>()
    private fun setCityListResponseData(attachedAccount: GetSchoolNameResponseModel) {
        this.CityListResponse.postValue(Event(attachedAccount))
    }

    fun observerCityListResponseData() = CityListResponse

    private var StandardListResponse = MutableLiveData<Event<GetDropdownResponseModel>>()
    private fun setStandardListResponseData(attachedAccount: GetDropdownResponseModel) {
        this.StandardListResponse.postValue(Event(attachedAccount))
    }

    fun observerStandardListResponseData() = StandardListResponse

    private var SchoolNameList = MutableLiveData<Event<GetSchoolNameResponseModel>>()
    private fun setSchoolNameListData(attachedAccount: GetSchoolNameResponseModel) {
        this.SchoolNameList.postValue(Event(attachedAccount))
    }

    fun observerSchoolNameListData() = SchoolNameList



    private var liveClass = MutableLiveData<Event<GetLeaderboardResponseModel>>()
    private fun setLiveClassData(attachedAccount: GetLeaderboardResponseModel) {
        this.liveClass.postValue(Event(attachedAccount))
    }

    fun observerLeaderBoardData() = liveClass


    private var messageString = MutableLiveData<Event<String>>()
    private fun setMessage(msg: String) {
        messageString.value = Event(msg)
    }

    fun observedChanges() = messageString
    private var errorMessageString = MutableLiveData<Event<String>>()
    private fun setErrorMessage(msg: String) {
        errorMessageString.value = Event(msg)
    }

    fun observedErrorMessageChanges() = errorMessageString


    fun getStudentList(name:String ? = null){
       // setMessage(Constants.VISIBLE)
        APITask.getInstance().getStudentList(this,name)?.let { mDisposable?.add(it) }

    }
    fun getCityList(){
      //  setMessage(Constants.VISIBLE)
        APITask.getInstance().getCityList(this)?.let { mDisposable?.add(it) }

    }
    fun getStandardList(){
      //  setMessage(Constants.VISIBLE)
        APITask.getInstance().getDropdownList(this)?.let { mDisposable?.add(it) }

    }
    fun getSchoolNameList(){
        //setMessage(Constants.VISIBLE)
        APITask.getInstance().getSchoolName(this)?.let { mDisposable?.add(it) }

    }

     fun callGetLeaderBoard(currentPage:Int,isProgressVisible:Boolean =true) {
         if(isProgressVisible)
         {
             setMessage(Constants.VISIBLE)
         }

         if(getLeaderboardRequestModel.StandardIds.isNullOrEmpty())
         {
             Constants.headerstandardid?.toInt()?.let {
                 getLeaderboardRequestModel.StandardIds.add(
                     it
                 )
             }
         }


         val c: Calendar = Calendar.getInstance()
         c.setTime(Date())
         val sdf = SimpleDateFormat("MMMM yyyy")
         System.out.println(sdf.format(c.getTime())) // NOW
         var model1 = MonthListModel(sdf.format(c.getTime()),false)
         var currentMonth = c.get(Calendar.MONTH)+1
         var currentYear = model1.monthName.split(" ")[1].toInt()

         if(getLeaderboardRequestModel.Month == 0)
         {
             getLeaderboardRequestModel.Month = currentMonth
         }
         if(getLeaderboardRequestModel.Year == 0)
         {
             getLeaderboardRequestModel.Year = currentYear
         }

        // getLeaderboardRequestModel.Student_Id = Constants.selectedUserId
         getLeaderboardRequestModel.PageNumber = currentPage
         APITask.getInstance().getLeaderBoard(this,getLeaderboardRequestModel)?.let { mDisposable?.add(it) }
    }




    override fun <T> onResponseReceived(response: T, requestCode: Int) {

        if (response != null && requestCode == APITask.getLeaderBoard) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            leaderboardResponseModel = response as GetLeaderboardResponseModel
            setMessage(Constants.HIDE)
            if((response as GetLeaderboardResponseModel).status == "200")
            {
                setLiveClassData(leaderboardResponseModel)
            }
            else{
                setMessage(Constants.NO_DATA)
            }
        }
        else if (response != null && requestCode == APITask.getStudentList) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            getStudentListResponse = response as GetSchoolNameResponseModel

            if((response as GetSchoolNameResponseModel).status == "200")
            {
                setStudentListResponseData(getStudentListResponse)
            }
            else{
               // setMessage((response as GetSchoolNameResponseModel).message)
            }
        }

        else if (response != null && requestCode == APITask.getCityList) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            getCityListResponse = response as GetSchoolNameResponseModel

            if((response as GetSchoolNameResponseModel).status == "200")
            {
                setCityListResponseData(getCityListResponse)
            }
            else{
               // setMessage((response as GetSchoolNameResponseModel).message)
            }
        }

        else if (response != null && requestCode == APITask.getDropDownList) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            getStandardListResponse = response as GetDropdownResponseModel

            if((response as GetDropdownResponseModel).status == "200")
            {
                setStandardListResponseData(getStandardListResponse)
            }
            else{
               // setMessage((response as GetDropdownResponseModel).message)
            }
        }

        else if (response != null && requestCode == APITask.getSchoolName) {
            Log.e("Forgot", "Response :  ${Gson().toJson(response)}")
            getSchoolNameListResponse = response as GetSchoolNameResponseModel

            if((response as GetSchoolNameResponseModel).status == "200")
            {
                setSchoolNameListData(getSchoolNameListResponse)
            }
            else{
                //setMessage((response as GetSchoolNameResponseModel).message)
            }
        }


    }

    override fun onResponseError(message: String, requestCode: Int, responseCode: Int) {

        setErrorMessage(message)
        setMessage(Constants.HIDE)
    }
}