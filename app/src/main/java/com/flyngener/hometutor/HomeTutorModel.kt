package com.flyngener.hometutor

import com.google.gson.annotations.SerializedName


data class HomeTutorModel(val message: String?, val isSuccess: Boolean)

data class RegistrationResponse(val message: String?, val isSuccess: Boolean, val insert_id: Int)

data class CityResponse(
    val result: List<City>,
    val isSuccess: Boolean
)

data class City(
    val id: String,
    val city_name: String,
    val state_name: String
)

data class KycModel(
    val insert_id: String,
    val message: String,
    val isSuccess: Boolean
)


data class RegistrationModel(
    val full_name: String,
    val email_id: String,
    val mobile_number: String,
    val refferal_code: String,
    val city: String,
    val state: String,
    val pincode: String,
    val gender: String
)

data class UserDetails(
    val insert_id: String,
    val qualification: String,
    val experience: String,
    val dob: String,
    val age: String,
    val gender: String,
    val about: String,
    val state: String,
    val city: String,
    val pincode: String,
    val fullAddress: String
)

data class ServiceProfile(
    @SerializedName("id")
    val id: String,
    @SerializedName("service_profile_name")
    val serviceProfileName: String,
    @SerializedName("service_profile_image")
    val serviceProfileImage: String
)

data class ServiceProfileResponse(
    @SerializedName("result")
    val result: List<ServiceProfile>,
    @SerializedName("isSuccess")
    val isSuccess: String
)

data class Unit(
    @SerializedName("id")
    val id: String,
    @SerializedName("unit_name")
    val unitName: String,
    @SerializedName("Sub_unit")
    val subUnit: List<String>
)

data class UnitResponse(
    @SerializedName("result")
    val result: List<Unit>,
    @SerializedName("isSuccess")
    val isSuccess: String
)

data class ServiceRegistrationModel(
    @SerializedName("insert_id")
    val insertId: String,
    @SerializedName("service_profile_ids")
    val serviceProfileIds: String,
    @SerializedName("unit_ids")
    val unitIds: String,
    @SerializedName("service_profile_name")
    val serviceProfileName: String,
    @SerializedName("unit_name")
    val unitName: String,
    @SerializedName("sub_unit")
    val subUnit: String,
    @SerializedName("capture_area")
    val captureArea: String
)

data class BankAccountModel(
    val insert_id: String,
    val bank_name: String,
    val branch_name: String,
    val account_number: String,
    val account_name: String,
    val ifsc_code: String
)

data class LoginMobileResponse(val user_id: String, val otp: String, val message: String?, val isSuccess: Boolean)

data class LoginWithMobileModel(val mobile_number: String)

data class OtpModel(val otp: String)

data class Guardian(val insert_id: Int)

data class OtpResponse(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("is_guardian")
    val isGuardian: String,
    val step_complete: String,
    val message: String,

    @SerializedName("isSuccess")
    val isSuccess: Boolean
)

data class UserTypeSwitch(
    val user_type: String,
    val user_id: String,
    val switch_to: String
)

data class SwitchResponse(
    val user_id: String,
    val step: String,
    val message: String,
    val isSuccess: Boolean
)

data class TopBannerResponse(
    val result: List<TopBannerItem>,
    val isSuccess: String
)

data class TopBannerItem(
    val id: String,
    val external_link: String,
    val banner_image: String
)

data class TeacherProfile(
    val id: String,
    val email_id: String,
    val mobile_number: String,
    val gender: String,
    val city: String,
    val state: String,
    val pincode: String,
    val full_address: String,
    val profile_image: String,
    val qualification: String,
    val experience: String,
    val dob: String,
    val age: String,
    val about: String,
    val service_profile_name: String
)

data class TeacherProfileResponse(
    val result: List<TeacherProfile>
)

data class ServiceProfileGuardianResult(
    val result: List<ServiceProfileGuardian>,
    val isSuccess: String
)

data class ServiceProfileGuardian(
    val id: String,
    val service_profile_name: String,
    val service_profile_image: String,
    val service_profile_type: String,
    val tag_line1: String,
    val tag_line2: String,
    val tag_line3: String,
    val is_show_in_home: String,
    val question: Boolean,
    val random_number: Int,
    val service_profile: Boolean

)


data class PackageResponse(
    val result: List<PackageDetails>,
    val isSuccess: String,
    val count: Int
)

data class PackageDetails(
    val id: String,
    val guardians_package_name: String,
    val package_price: String,
    val package_type: String,
    val package_description: String,
    val package_validity: String,
    val free_demo_booking_limits: String
)

data class TeacherPackageResponse(
    val result: List<TeacherPackageDetails>,
    val isSuccess: String,
    val count: Int
)

data class TeacherPackageDetails(
    val id: String,
    val teacher_package_name: String,
    val package_price: String,
    val package_type: String,
    val package_description: String,
    val package_validity: String,
)

data class Option(
    val value: String
)

data class Question(
    val id: String,
    val question_type: String,
    val service_profile_id: String,
    val question: String,
    val options: List<Option>,
    val random_number: Int?
)


data class QuestionResponse(
    val result: List<Question>,
    val isSuccess: String
)

data class Answer(
    val question_id: String,
    val answer: String
)

data class BookingDataSubmit(
    val service_profile_id: String,
    val random_number: String,
    val fees_amount: String,
    var transaction_id: String,
    val unit_type: String,
    val unit_id: String,
    val unit_name: String,
    val sub_unit_name: String,
    val is_free_demo_booked: String,
    val only_free_demo_booked: String,
    val answer_set: List<Answer>,
    val unit: List<UnitSelectList>,
    val gaurdian_id: String
)

data class FeesResponse(
    val fees_array: List<Fee>,
    val isSuccess: String
)

data class Fee(
    val id: String,
    val fees_amount: String,
    val unit_type: String
)

data class PopularTeacherResponse(
    val result: List<PopularTeacher>,
    val isSuccess: String
)

data class PopularTeacher(
    val id: String,
    val full_name: String,
    val unit_name: String,
    val sub_unit_name: String,
    val profile_image: String
)


data class SelectUnitResponse(
    val result: List<SelectUnitResult>,
    val isSuccess: String
)

data class SelectUnitResult(
    val id: String,
    val unit_name: String,
    val sub_unit_name: List<SubUnit>
)

data class SubUnit(
    val value: String
)

data class UnitSelectList(
    val unit_id: String,
    val unit_name: String,
    val sub_unit_name: String,
)

data class TeacherRequestTopResponse(
    val result: List<TeacherRequestTop>,
    val isSuccess: String
)

data class TeacherRequestTop(
    val id: String,
    val service_profile_name: String,
    val service_profile_details: String,
    val gaurdian_id: String,
    val gaurdian_name: String,
    val gaurdian_address: String,
    val gaurdian_mobile_number: String?,
    val fees_amount: String,
    val submit_date: String,
    val gaurdian_image: String,
    val service_profile_image: String,
    val status: String
)

data class TeacherBookingResponse(
    val result: List<BookingItem>,
    val isSuccess: String
)

data class BookingItem(
    val id: String,
    val service_profile_name: String,
    val service_profile_details: List<ServiceProfileDetail>,
    val gaurdian_id: String,
    val gaurdian_name: String,
    val gaurdian_address: String,
    val fees_amount: String,
    val is_free_demo_booked: String,
    val only_free_demo_booked: String,
    val submit_date: String,
    val gaurdian_image: String,
    val service_profile_image: String,
)

data class ServiceProfileDetail(
    val value: String
)

data class GuardianPackageData(
    val gaurdian_id: String,
    val package_id: String,
)

data class TeacherPackageData(
    val teacher_id: String,
    val package_id: String,
)

data class SupportResponse(
    val result: List<SupportResult>,
    val isSuccess: String
)

data class SupportResult(
    val id: String,
    val page_name: String,
    val page_details: String
)

data class SupportRequestResponse(
    val message: String,
    val support_number: Int,
    val isSuccess: Boolean
)

data class TeacherSubject(
    val teacher_id: String,
    val subject: String,
    val details: String
)

data class GuardianSubject(
    val gaurdian_id: String,
    val subject: String,
    val details: String,
    val appointment_id: String
)

data class AcceptBooking(
    val teacher_id: String,
    val booking_id: String,
    val guardian_id: String
)

data class RejectBookingByGuardian(
    val booking_id: String,
    val reject_reason: String
)

data class AssignBookingByGuardian(
    val booking_id: String,
    val teacher_id: String,
    val gaurdian_id: String
)

data class AssignBookingResponse(
    val booking_id: String,
    val message: String,
    val isSuccess: String
)

data class GuardianRequestResult(
    val teacher_id: String,
    val teacher_name: String,
    val teacher_mobile: String,
    val teacher_code: String,
    val teacher_image: String,
    val accepted_by_teacher: String,
    val assigned_or_reject_by_gaurdian: String,
    val service_profile_id: String,
    val service_profile_name: String,
    val service_profile_details: List<ServiceProfileDetail>,
    val service_profile_image: String,
    val booking_id: String,
    val booking_date: String,
    val fees_amount: String,
    val is_free_demo_booked: String,
    val only_free_demo_booked: String,
    val unit_type: String,
)

data class RequestGuardianBooking(
    val result: List<GuardianRequestResult>,
    val isSuccess: String
)

data class PayAfterAssign(
    val booking_id: String,
    val fees_amount: String,
    var transaction_id: String,
    val unit_type: String,
    val guardian_id: String
)

data class SupportPageResult(
    val id: String,
    val page_name: String,
    val page_details: String,
    val subject: String,
    val details: String,
    val status: String
)

data class SupportPageResponse(
    val result: List<SupportPageResult>,
    val isSuccess: String
)
