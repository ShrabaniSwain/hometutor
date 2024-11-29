package com.flyngener.tutorhub

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface HomeTutorApi {

    @GET("state_city_list.php")
    suspend fun getCityList(): Response<CityResponse>

    @GET("service_profile_list.php")
    suspend fun getServiceList(): Response<ServiceProfileResponse>


    @GET("gaurdian_package_list.php")
    fun getGaurdianPackageList(): Call<PackageResponse>

    @GET("teacher_package_list.php")
    fun getTeacherPackageList(): Call<TeacherPackageResponse>

    @GET("service_profile_list_gaurdian.php")
    fun getServiceProfileList(): Call<ServiceProfileGuardianResult>

    @GET("popular_teacher.php")
    fun getPopularTeacherList(): Call<PopularTeacherResponse>

    @GET("teacher_support_content.php")
    fun getTeacherSupport(@Query("teacher_id") teacher_id: Int): Call<SupportPageResponse>

    @GET("gaurdian_support_content.php")
    fun getGuardianSupport(@Query("gaurdian_id") gaurdian_id: Int): Call<SupportPageResponse>

    @GET("terms_for_teacher.php")
    fun getTermsList(): Call<SupportResponse>

    @GET("terms_for_guardian.php")
    fun getTermsGuardianList(): Call<SupportResponse>

    @GET("privacy_for_teacher.php")
    fun getPrivacyList(): Call<SupportResponse>

    @GET("privacy_for_guardian.php")
    fun getPrivacyGuardianList(): Call<SupportResponse>

    @GET("top_banner_list.php")
    fun getTopBanner(): Call<TopBannerResponse>

    @GET("bottom_banner.php")
    fun getBottomBanner(): Call<TopBannerResponse>

    @GET("guardian_banner.php")
    fun getBanner(): Call<BannerResponse>

    @GET("unit_list.php")
    fun getSelectUnitList(): Call<SelectUnitResponse>

    @GET("service_profile_list.php")
    fun getServiceListProfile(): Call<ServiceProfileResponse>

    @GET("service_profile_payment_option.php")
    fun getPaymentAmount(@Query("id") id: Int): Call<FeesResponse>

    @GET("question_list.php")
    fun getCustomerDetails(@Query("id") id: Int): Call<QuestionResponse>

    @GET("service_profile_list_gaurdian.php")
    fun getSubcategoryProfile(@Query("id") id: Int): Call<ServiceProfileGuardianResult>

    @GET("accepted_booking_list.php")
    fun getAcceptedBookingList(@Query("gaurdian_id") gaurdian_id: Int): Call<RequestGuardianBooking>
    @GET("appointed_booking_list.php")
    fun getAppointedBookingList(@Query("gaurdian_id") gaurdian_id: Int): Call<GuardianAppointedResponse>
    @GET("appointment_list.php")
    fun getAppointmentList(@Query("gaurdian_id") gaurdian_id: Int): Call<AppointmentResponse>
    @GET("payment_list_teacher.php")
    fun getPaymentList(@Query("teacher_id") gaurdian_id: Int): Call<TeacherPaymentResponse>

    @GET("gaurdian_booking_list.php")
    fun getBookingList(@Query("teacher_id") teacher_id: Int): Call<TeacherBookingResponse>
    @GET("gaurdian_booking_list2.php")
    fun getBookingList2(@Query("teacher_id") teacher_id: Int): Call<TeacherBookingResponse>
    @GET("teacher_earned_pending.php")
    fun getTeacherEarnedList(@Query("teacher_id") teacher_id: Int): Call<TeacherEarningsResponse>
    @GET("teacher_notofication.php")
    fun getTeacherNotification(@Query("teacher_id") teacher_id: Int): Call<PaymentReminderResponse>
    @GET("guardian_notofication.php")
    fun getGuardianNotification(@Query("gaurdian_id") gaurdian_id: Int): Call<PaymentReminderResponse>

    @GET("teacher_profile_complete_get.php")
    fun getCompleteTeacherProfile(@Query("teacher_id") teacher_id: Int): Call<CompleteProfileDetailsResponse>

    @GET("assigned_booking_list.php")
    fun getAssignedBookingList(@Query("teacher_id") teacher_id: Int): Call<TeacherRequestTopResponse>

    @GET("service_profile_list_gaurdian.php")
    fun getSubPopularProfile(@Query("teacher_id") teacher_id: Int): Call<ServiceProfileGuardianResult>
    @GET("active_list_for_teacher.php")
    fun getActiveListForTeacher(@Query("teacher_id") teacher_id: Int): Call<ActiveListResponse>
    @GET("teacher_profile.php")
    fun getTeacherProfile(@Query("teacher_id") teacher_id: Int): Call<TeacherProfileDetails>
    @GET("gaurdian_profile.php")
    fun getGuardianProfile(@Query("gaurdian_id") gaurdian_id: Int): Call<GuardianProfileResponse>
    @GET("details_active_list_for_teacher.php")
    fun getDetailsActiveListForTeacher(@Query("booking_id") booking_id: Int): Call<DetailsActiveResponse>
    @GET("search_keyword_list.php")
    fun getSearchKeyword(): Call<SearchKeywordResponse>

    @GET("task_list_in_teacher.php")
    fun getTaskListInTeacher(@Query("teacher_id") teacher_id: Int, @Query("booking_id") booking_id: Int): Call<TaskResponse>

    @GET("task_list_in_gaurdian.php")
    fun getTaskListInGuardian(@Query("gaurdian_id") gaurdian_id: Int, @Query("booking_id") booking_id: Int): Call<GuardianTaskResponse>
    @POST("gaurdian_teacher_registration.php")
    fun addRegistrationsDetails(
        @Body registrationModel: RegistrationModel,
    ): Call<RegistrationResponse>

    @POST("teacher_package_booking.php")
    fun teacherPackageBooking(
        @Body registrationModel: TeacherPackageData,
    ): Call<PackageBookingResponse>

    @POST("is_gaurdian_package_booked.php")
    fun checkGuardianPackage(
        @Query("gaurdian_id") gaurdian_id: Int
    ): Call<HomeTutorModel>

    @POST("is_teacher_package_booked.php")
    fun checkTeacherPackage(
        @Query("teacher_id") teacher_id: Int
    ): Call<HomeTutorModel>

    @POST("forget_password.php")
    fun forgetPassword(
        @Query("email_id") email_id: String
    ): Call<HomeTutorModel>

    @POST("gaurdian_package_booking.php")
    fun guardianPackageBooking(
        @Body registrationModel: GuardianPackageData,
    ): Call<PackageBookingResponse>

    @POST("enable_booking_payment_by_teacher.php")
    fun enableBookingPayment(
        @Body enablePaymentModel: EnablePaymentModel,
    ): Call<HomeTutorModel>

    @POST("disable_booking_payment_by_teacher.php")
    fun disableBookingPayment(
        @Body enablePaymentModel: EnablePaymentModel,
    ): Call<HomeTutorModel>
    @POST("teacher_update_task.php")
    fun teacherUpdateTask(
        @Body taskDetailsUpdate: TaskDetailsUpdate,
    ): Call<HomeTutorModel>
    @POST("edit_teacher_profile_pd.php")
    fun editTeacherProfile(
        @Body profileResponse: ProfileResponse,
    ): Call<HomeTutorModel>
    @POST("edit_teacher_profile_address.php")
    fun editTeacherProfileAddress(
        @Body addressResponse: AddressResponse,
    ): Call<HomeTutorModel>
    @POST("edit_teacher_profile_service.php")
    fun editTeacherProfileService(
        @Body serviceResponse: ServiceResponse,
    ): Call<TeacherServiceResponse>
    @POST("edit_teacher_profile_kyc.php")
    fun editTeacherProfileKyc(
        @Body documentDetails: DocumentDetails,
    ): Call<HomeTutorModel>
    @POST("edit_teacher_profile_bank.php")
    fun editTeacherProfileBank(
        @Body bankDetails: BankDetails,
    ): Call<HomeTutorModel>
    @POST("search_key_by_gaurdian.php")
    fun searchKeyByGuardian(
        @Body searchKeyResult: SearchKeyResult,
    ): Call<HomeTutorModel>

    @POST("teacher_registration_step2.php")
    fun teacherRegistrationStep2(
        @Body userDetails: UserDetails,
    ): Call<HomeTutorModel>

    @POST("teacher_registration_step3.php")
    fun teacherServiceRegistration(
        @Body serviceRegistrationModel: ServiceRegistrationModel,
    ): Call<ServiceRegistrationResponse>

    @POST("teacher_registration_step5.php")
    fun bankDetailsRegistration(
        @Body bankAccount: BankAccountModel,
    ): Call<HomeTutorModel>

    @POST("login_check_mobile_send_otp.php")
    fun loginCheckMobileNumber(
        @Body loginWithMobileModel: LoginWithMobileModel,
    ): Call<LoginMobileResponse>
    @POST("username_password_login.php")
    fun loginWithUsername(
        @Body username: Username,
    ): Call<UsernameResponse>

    @POST("check_otp_and_login.php")
    fun checkOtpForLogin(
        @Body otpModel: OtpModel,
    ): Call<OtpResponse>

    @POST("gaurdian_question_submit.php")
    fun guardianQuestionSubmit(
        @Body bookingDataSubmit: BookingDataSubmit,
    ): Call<HomeTutorModel>

    @POST("click_on_gaurdian.php")
    fun clickOnGuardian(
        @Body otpModel: Guardian,
    ): Call<HomeTutorModel>

    @POST("switch_account.php")
    fun switchAccount(
        @Body userTypeSwitch: UserTypeSwitch,
    ): Call<SwitchResponse>

    @Multipart
    @POST("teacher_registration_step4.php")
    fun kycRegistration(
        @Part("insert_id") insert_id: RequestBody,
        @Part("voter_id") voter_id: RequestBody,
        @Part("aadhar_id") aadhar_id: RequestBody,
        @Part("pan_no") pan_no: RequestBody,
        @Part("driving_license_no") driving_license_no: RequestBody,
        @Part voter_card: MultipartBody.Part?,
        @Part aadhar_card: MultipartBody.Part?,
        @Part pan_card: MultipartBody.Part?,
        @Part bank_passbook: MultipartBody.Part?,
        @Part driving_license: MultipartBody.Part?,
    ): Call<KycModel>

    @POST("teacher_support_form.php")
    fun teacherSupportPackageForm(
        @Body teacherSubject: TeacherSubject,
    ): Call<SupportRequestResponse>

    @POST("gaurdian_support_form.php")
    fun guardianSupportPackageForm(
        @Body teacherSubject: GuardianSubject,
    ): Call<SupportRequestResponse>

    @POST("accept_booking_by_teacher.php")
    fun acceptBookingByTeacher(
        @Body acceptBooking: AcceptBooking,
    ): Call<HomeTutorModel>

    @POST("reject_teacher_request_by_gaurdian.php")
    fun rejectBookingByTeacher(
        @Body rejectBookingByGuardian: RejectBookingByGuardian,
    ): Call<HomeTutorModel>

    @POST("assign_booking_by_gaurdian.php")
    fun assignBookingByTeacher(
        @Body assignBookingByGuardian: AssignBookingByGuardian,
    ): Call<AssignBookingResponse>

    @POST("pay_after_booking.php")
    fun payAfterAssignBooking(
        @Body payAfterAssign: PayAfterAssign,
    ): Call<HomeTutorModel>

    @POST("pay_in_details.php")
    fun payInDetails(
        @Body payAfterAssign: PayAfterAssign,
    ): Call<HomeTutorModel>
    @POST("resign_by_teacher.php")
    fun resignByTeacher(
        @Body resignRequest: ResignRequest,
    ): Call<HomeTutorModel>

    @Multipart
    @POST("teacher_add_student.php")
    fun teacherAddStudent(
        @Part("teacher_id") teacher_id: RequestBody,
        @Part("booking_id") booking_id: RequestBody,
        @Part("guardian_id") guardian_id: RequestBody,
        @Part("student_name") student_name: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("age") age: RequestBody,
        @Part("address") address: RequestBody,
        @Part("appointment_location") appointment_location: RequestBody,
        @Part("fees_amount") fees_amount: RequestBody,
        @Part("fees_c_date") fees_c_date: RequestBody,
        @Part student_image: MultipartBody.Part?,
        @Part("start_date") start_date: RequestBody,
        @Part("unitDetails") unit_details: RequestBody,
        ): Call<HomeTutorModel>

    @Multipart
    @POST("teacher_complete_profile.php")
    fun teacherCompleteProfile(
        @Part("teacher_id") teacher_id: RequestBody,
        @Part("full_name") full_name: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("age") age: RequestBody,
        @Part("address") address: RequestBody,
        @Part("appointment_date") appointment_date: RequestBody,
        @Part("fees_amount") fees_amount: RequestBody,
        @Part("fees_c_date") fees_c_date: RequestBody,
        @Part upload_image: MultipartBody.Part?,
        @Part("start_date") start_date: RequestBody,
        @Part("unit_details") unit_details: RequestBody,
    ): Call<HomeTutorModel>
    @Multipart
    @POST("teacher_add_task.php")
    fun teacherAddTask(
        @Part("teacher_id") teacher_id: RequestBody,
        @Part("booking_id") booking_id: RequestBody,
        @Part("guardian_id") guardian_id: RequestBody,
        @Part("student_id") student_id: RequestBody,
        @Part("student_name") student_name: RequestBody,
        @Part("task_title") task_title: RequestBody,
        @Part("task_date") task_date: RequestBody,
        @Part("task_time") task_time: RequestBody,
        @Part("schedule_date") schedule_date: RequestBody,
        @Part task_image: MultipartBody.Part,
        ): Call<HomeTutorModel>
    @Multipart
    @POST("edit_gaurdian_profile.php")
    fun editGuardianProfile(
        @Part("full_name") full_name: RequestBody,
        @Part("email_id") email_id: RequestBody,
        @Part("mobile_number") mobile_number: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("city") city: RequestBody,
        @Part("State") State: RequestBody,
        @Part("pincode") pincode: RequestBody,
        @Part("full_address") full_address: RequestBody,
        @Part profile_image: MultipartBody.Part?,
        @Part("gaurdian_id") gaurdian_id: RequestBody,
        ): Call<HomeTutorModel>

    @Multipart
    @POST("change_teacher_profile_image.php")
    fun changeTeacherProfileImage(
        @Part("teacher_id") teacher_id: RequestBody,
        @Part profile_image: MultipartBody.Part,
        ): Call<HomeTutorModel>

}
