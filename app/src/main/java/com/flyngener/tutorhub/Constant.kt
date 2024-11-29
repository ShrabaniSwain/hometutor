package com.flyngener.tutorhub

object Constant {

    val TEACHER_CARD = "teacher"
    val GUARDIAN_CARD = "guardian"
    var is_guardian = ""
    var INSERT_ID = ""
    var MORECLICK = "moreClick"
    var PROFILECLICK = "profileCLick"
    var POPULARTEACHERCLICK = "popular"
    var PACKAGE_ID = ""
    var CLICKTYPE = "click"
    var ANSWER = ""
    var QuestionId = ""
    var PROFILEID = 0
    var RANDOM_NO = ""
    var BUDGET = ""
    var UNIT_TYPE = ""
    var PROFILE_TYPE = false
    var RAZOR_PAY_KEY = "rzp_live_TSrvlH673EF3Cq"
    var RAZOR_PAY_SECRET_KEY = "yCGv86KfjZrelF0cxs6YwEJl"
    var BOOKING_ID = ""
    var STUDENT_NAME = ""
    var STUDENT_ID = ""
    var SERVICE_NAME = ""
    var GUARDIAN_ID = ""
    var SERVICE_PROFILE_PAYMENT_ID = 0
    val APP_VERSION_NAME: Double = BuildConfig.VERSION_NAME
    var isQuestion = false
    var questionnaireList: List<Answer> = emptyList()
    // Use MutableList instead of List to allow modification
    var selectUnitList: MutableList<UnitSelectList> = mutableListOf()
    var unitDetailsList: List<SelectUnitResult> = emptyList()
    const val BASE_URL = "http://hometutorshub.com/admin/mobile/"
    var MOBILE_NO = ""
    var PAYMNET_TYPE = ""
    var ORDER_ID = ""

    var VISIBILITY = 0
    var TEACHER_SELECT_TAB = 0
    var GUARDIAN_SELECT_TAB = 0
//    yCGv86KfjZrelF0cxs6YwEJl
//
//    rzp_live_TSrvlH673EF3Cq

}