package com.flyngener.tutorhub

import com.google.gson.annotations.SerializedName


data class HomeTutorModel(val message: String?, val isSuccess: Boolean)

data class TeacherServiceResponse(
    val teacher_id: Int,
    val message: String,
    val isSuccess: Boolean
)

data class RegistrationResponse(val message: String?, val isSuccess: Boolean, val insert_id: Int, val mobile_number: String)

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

data class UnitDetail(
    val unit_id: String,
    val unit_name: String,
    val sub_units: List<String>
)


data class RegistrationModel(
    val full_name: String,
    val email_id: String,
    val mobile_number: String,
    val refferal_code: String,
    val city: String,
    val state: String,
    val pincode: String,
    val gender: String,
    val is_guardian: String,
    val user_id: String,
    val password: String,
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
    @SerializedName("insertId")
    val insertId: String,
    @SerializedName("serviceProfileIds")
    val serviceProfileIds: List<String>,
    @SerializedName("serviceProfileName")
    val serviceProfileName: List<String>,
    val unit_details: List<SelectUnitResult>,
    @SerializedName("captureArea")
    val captureArea: String
)

data class ServiceRegistrationResponse(
    val insert_id: Int,
    val message: String,
    val isSuccess: Boolean
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
    val mobile_number: String,
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

data class ActiveListResponse(
    val result: List<Student>,
    val isSuccess: Boolean
)

data class Student(
    val id: String,
    val gaurdian_id: String?,
    val student_id: String,
    val booking_id: String,
    val appointed_as: String,
    val appointment_id: String,
    val appointment_location: String,
    val week_days: String,
    val phone_number: String?,
    val student_name: String,
    val student_image: String,
    val unit_details: String
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
    val free_demo_booking_limits: String,
    val package_icon: String
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
    val package_icon: String
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
    val gaurdian_id: String,
    val payment_type: String,
    val payment_status: String
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
    val experience: String,
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
    val status: String,
    val rejected_message: String,
)

data class TeacherBookingResponse(
    val result: List<BookingItem>,
    val isSuccess: String
)

data class BookingItem(
    val id: String,
    val service_profile_id: String,
    val service_profile_name: String,
    val service_profile_hirarchy: List<ServiceProfileDetail>,
    val service_profile_details: String,
    val gaurdian_id: String,
    val gaurdian_name: String,
    val gaurdian_address: String,
    val fees_amount: String,
    val is_free_demo_booked: String,
    val only_free_demo_booked: String,
    val submit_date: String,
    val gaurdian_image: String,
    val booking_status: String,
    val service_profile_image: String
)

data class ServiceProfileDetail(
    val value: String
)

data class GuardianPackageData(
    val gaurdian_id: String,
    val package_id: String,
    var transaction_id: String,
)

data class TeacherPackageData(
    val teacher_id: String,
    val package_id: String,
    var transaction_id: String,
    )

data class PackageBookingResponse(
    val user_id: String,
    val is_guardian: String,
    val message: String,
    val isSuccess: Boolean
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
    val isSuccess: Boolean
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
    val booking_time: String,
    val fees_amount: String,
    val is_free_demo_booked: String,
    val only_free_demo_booked: String,
    val unit_type: String,
    val question_ans: List<Any>,
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
    val guardian_id: String,
    val payment_type: String,
    val payment_status: String
)

data class SupportPageResult(
    val subject: String,
    val details: String,
    val status: String
)

data class SupportPageResponse(
    val result: List<SupportPageResult>,
    val isSuccess: String
)

data class DetailsActiveResponse(
    val result: List<StudentDetails>,
    val isSuccess: Boolean
)
data class StudentDetails(
    val guardian_image: String?,
    val id: String,
    val gaurdian_name: String,
    val gaurdian_id: String,
    val gaurdian_booking_date: String?,
    val gaurdian_phone: String,
    val gaurdian_email: String,
    val student_phone_number: String?,
    val student_name: String,
    val student_dob: String,
    val teacher_id: String,
    val student_age: String,
    val student_id: String,
    val student_image: String?,
    val unit_details: List<Map<String, String>>,
    val service_profile_name: String,
    val booking_id: String,
    val appointmet_id: String,
    val appointment_location: String?,
    val fees_date: String,
    val appointment_date: String,
    val fees_amount: String,
    val total_paid: String,
    val due_amount: String?,
    val enable_pay_button_show: Int
)

data class StudentUnitDetail(
    val Board: String?,
    val Class: String?,
    val Medium: String?
)
data class EnablePaymentModel(
    val teacher_id: String,
    val booking_id: String,
    val gaurdian_id: String
)

data class TaskDetails(
    val id: String,
    val task_title: String,
    val task_date: String,
    val task_time: String,
    val schedule_date: String,
    val phone_number: String?,
    val student_name: String?,
    val task_image: String
)

data class TaskResponse(
    val result: List<TaskDetails>,
    val isSuccess: String
)

data class TaskDetailsUpdate(
    val teacher_id: String,
    val task_id: String,
    val task_remark: String?,
    val task_rating: String
)

data class AppointmentResponse(
    val result: List<Appointment>,
    val isSuccess: String
)

data class Appointment(
    val id: String,
    val appointment_id: String
)


data class GuardianAppointedResponse(
    val result: List<AppointedModel>,
    val isSuccess: Boolean
)

data class AppointedModel(
    val id: String,
    val teacher_id: String,
    val teacher_name: String,
    val teacher_code: String,
    val teacher_mobile: String,
    val teacher_experience: String,
    val teacher_qualification: String,
    val teacher_location: String,
    val dob: String,
    val age: String,
    val student_name: String,
    val student_age: String,
    val student_gender: String,
    val student_address: String,
    val student_appointment_location: String,
    val student_image: String,
    val appointment_id: String,
    val appointment_date: String,
    val fees_amount: String,
    val appointment_for: String,
    val unit_type: String,
    val appointment_week_days: String,
    val enable_pay_button_show: Int,
    val due_amount: String,
    val unit_details: String,
    val teacher_image: String,
    val service_profile_image: String,
    val status: String,
    val total_paid: String
)

data class TeacherPaymentResponse(
    val result: List<TeacherPaymentData>,
    val isSuccess: String
)

data class TeacherPaymentData(
    val id: String,
    val payment_amount: String,
    val transaction_id: String,
    val payment_date: String,
    val payment_type: String?,
    val payment_status: String?
)

data class ProfileResponse(
    val teacher_id: String,
    val full_name: String,
    val email_id: String,
    val mobile_number: String,
    val gender: String,
    val qualification: String,
    val experience: String,
    val dob: String,
    val age: String,
    val about: String
)

data class AddressResponse(
    val teacher_id: String,
    val city: String,
    val state: String,
    val pincode: String,
    val full_address: String
)

data class ServiceResponse(
    val teacher_id: String,
    val service_profile_deatils: List<ServiceProfile>,
    val unit_details: List<SelectUnitResult>,
)

data class DocumentDetails(
    val teacher_id: String,
    val voter_id: String?,
    val voter_card: String?,
    val aadhar_id: String?,
    val aadhar_card: String?,
    val pan_no: String?,
    val pan_card: String?,
    val bank_passbook: String?,
    val driving_license_no: String?,
    val driving_license: String?
)

data class BankDetails(
    val teacher_id: String,
    val bank_name: String,
    val branch_name: String,
    val account_number: String,
    val account_name: String,
    val ifsc_code: String,
    val capture_area: String
)


data class SearchKeywordResponse(
    val result: List<SearchKeyword>,
    val isSuccess: String
)

data class SearchKeyword(
    val id: String,
    val service_profile_name: String,
    val service_profile_image: String,
    val service_profile_type: String?,
    val tag_line1: String?,
    val tag_line2: String?,
    val tag_line3: String?,
    val is_show_in_home: String?,
    val question: String,
    val random_number: Int,
    val service_profile: Boolean
)

data class SearchKeyResult(
    val guardian_id: String,
    val search_keyword: String,
    val result_found: Int
)

data class TeacherEarningsResponse(
    val result: TeacherEarning,
    val isSuccess: String
)

data class TeacherEarning(
    val teacher_id: String,
    val total_earned: String,
    val total_pending: Int
)


data class GuardianTaskResponse(
    val result: List<GuardianTask>,
    val isSuccess: String
)

data class GuardianTask(
    val id: String,
    val task_title: String,
    val task_date: String,
    val task_time: String,
    val task_rating: String?,
    val task_remark: String?,
    val schedule_date: String,
    val phone_number: String?,
    val student_name: String?,
    val task_image: String
)

data class ResignRequest(
    val booking_id: String,
    val teacher_id: String,
    val resign_reason: String
)

data class GuardianProfileResponse(
    val result: List<GuardianProfile>,
    val isSuccess: String
)

data class GuardianProfile(
    val id: String,
    val full_name: String,
    val mobile_number: String,
    val email_id: String,
    val gender: String,
    val city: String,
    val State: String,
    val pincode: String?,
    val full_address: String,
    val profile_image: String
)


data class TeacherProfileDetails(
    val result: List<TeacherDetailsModel>,
    val isSuccess: String
)

data class TeacherDetailsModel(
    val id: String,
    val full_name: String,
    val mobile_number: String,
    val email_id: String,
    val gender: String,
    val city: String,
    val State: String,
    val pincode: String?,
    val full_address: String,
    val voter_id: String,
    val aadhar_id: String,
    val pan_no: String,
    val driving_license_no: String,
    val bank_name: String,
    val branch_name: String,
    val account_number: String,
    val account_name: String,
    val ifsc_code: String,
    val qualification: String,
    val experience: String,
    val age: String,
    val dob: String,
    val about: String,
    val service_profile_name: String,
    val unit_subunit_details: String,
    val profile_image: String
)

data class PaymentReminderResponse(
    val result: List<PaymentReminderList>,
    val isSuccess: String
)

data class PaymentReminderList(
    val result: List<PaymentReminder>
)

data class PaymentReminder(
    val teacher_id: String,
    val notification_title: String,
    val notification_date: String,
    val amount: String,
    val notification_details: String
)

data class Username(
    val user_id: String,
    val password: String,
)

data class UsernameResponse(
    val user_id: String,
    val is_guardian: String,
    val step_complete: String,
    val message: String,
    val mobile_number: String,
    val isSuccess: Boolean
)


data class CompleteProfileDetailsResponse(
    val result: List<CompleteTeacherDetails>,
    val isSuccess: String
)

data class CompleteTeacherDetails(
    val teacher_id: String,
    val full_name: String,
    val gender: String,
    val dob: String,
    val age: String,
    val address: String,
    val appointment_date: String,
    val fees_amount: String,
    val fees_c_date: String,
    val upload_image: String,
    val start_date: String,
    val unit_details: String
)

data class BannerResponse(
    val result: List<Banner>,
    val isSuccess: String
)

data class Banner(
    val id: String,
    val external_link: String,
    val banner_image: String
)
