package com.flyngener.hometutor

import com.google.gson.annotations.SerializedName
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

    @GET("support.php")
    fun getSupport(): Call<SupportResponse>

    @GET("teacher_support_content.php")
    fun getTeacherSupport(@Query("teacher_id") teacher_id: Int): Call<SupportPageResponse>

    @GET("gaurdian_support_content.php")
    fun getGuardianSupport(@Query("gaurdian_id") gaurdian_id: Int): Call<SupportPageResponse>

    @GET("terms.php")
    fun getTermsList(): Call<SupportResponse>


    @GET("privacy.php")
    fun getPrivacyList(): Call<SupportResponse>

    @GET("top_banner_list.php")
    fun getTopBanner(): Call<TopBannerResponse>

    @GET("bottom_banner.php")
    fun getBottomBanner(): Call<TopBannerResponse>

    @GET("unit_list.php")
    suspend fun getUnit1List(): Response<SelectUnitResponse>

    @GET("unit_list.php")
    fun getSelectUnitList(): Call<SelectUnitResponse>

    @GET("service_profile_payment_option.php")
    fun getPaymentAmount(@Query("id") id: Int): Call<FeesResponse>

    @GET("question_list.php")
    fun getCustomerDetails(@Query("id") id: Int): Call<QuestionResponse>

    @GET("service_profile_list_gaurdian.php")
    fun getSubcategoryProfile(@Query("id") id: Int): Call<ServiceProfileGuardianResult>

    @GET("accepted_booking_list.php")
    fun getAcceptedBookingList(@Query("gaurdian_id") gaurdian_id: Int): Call<RequestGuardianBooking>

    @GET("gaurdian_booking_list.php")
    fun getBookingList(@Query("teacher_id") teacher_id: Int): Call<TeacherBookingResponse>

    @GET("assigned_booking_list.php")
    fun getAssignedBookingList(@Query("teacher_id") teacher_id: Int): Call<TeacherRequestTopResponse>

    @GET("service_profile_list_gaurdian.php")
    fun getSubPopularProfile(@Query("teacher_id") teacher_id: Int): Call<ServiceProfileGuardianResult>

    @POST("gaurdian_teacher_registration.php")
    fun addRegistrationsDetails(
        @Body registrationModel: RegistrationModel,
    ): Call<RegistrationResponse>

    @POST("teacher_package_booking.php")
    fun teacherPackageBooking(
        @Body registrationModel: TeacherPackageData,
    ): Call<HomeTutorModel>

    @POST("is_gaurdian_package_booked.php")
    fun checkGuardianPackage(
        @Query("gaurdian_id") gaurdian_id: Int
    ): Call<HomeTutorModel>

    @POST("is_teacher_package_booked.php")
    fun checkTeacherPackage(
        @Query("teacher_id") teacher_id: Int
    ): Call<HomeTutorModel>

    @POST("gaurdian_package_booking.php")
    fun guardianPackageBooking(
        @Body registrationModel: GuardianPackageData,
    ): Call<HomeTutorModel>

    @POST("teacher_registration_step2.php")
    fun teacherRegistrationStep2(
        @Body userDetails: UserDetails,
    ): Call<HomeTutorModel>

    @POST("teacher_registration_step3.php")
    fun teacherServiceRegistration(
        @Body serviceRegistrationModel: ServiceRegistrationModel,
    ): Call<HomeTutorModel>

    @POST("teacher_registration_step5.php")
    fun bankDetailsRegistration(
        @Body bankAccount: BankAccountModel,
    ): Call<HomeTutorModel>

    @POST("login_check_mobile_send_otp.php")
    fun loginCheckMobileNumber(
        @Body loginWithMobileModel: LoginWithMobileModel,
    ): Call<LoginMobileResponse>

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
        @Part voter_card: MultipartBody.Part,
        @Part aadhar_card: MultipartBody.Part,
        @Part pan_card: MultipartBody.Part,
        @Part bank_passbook: MultipartBody.Part,
        @Part driving_license: MultipartBody.Part,
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
        @Part student_image: MultipartBody.Part,
        @Part("start_date") start_date: RequestBody,
        @Part("unit_id[]") unitIds: RequestBody,
        @Part("unit_name[]") unitName: RequestBody,
        @Part("sub_unit_name[]") subUnit: RequestBody,
        ): Call<HomeTutorModel>

}
