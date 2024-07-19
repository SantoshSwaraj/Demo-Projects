package aisaac.entities;

import java.util.Date;

import org.hibernate.annotations.DynamicInsert;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.util.CustomDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@DynamicInsert
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "password", length = 255)
    private String password;
    
    @Column(name = "display_name", length = 255)
    private String displayName;
    
    @Column(name = "role_details", length = 255)
    private String roleDetails;

    @Column(name = "country_name", length = 255)
    private String countryName;

    @Column(name = "country_isd_code", length = 20)
    private String countryIsdCode;

    @Column(name = "mobile_number", length = 32)
    private String mobileNumber;

    @Column(name = "user_salt", length = 255)
    private String userSalt;

    @Column(name = "get_sms", nullable = false)
    private Boolean getSms;

    @Column(name = "get_email", nullable = false)
    private Boolean getEmail;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login", length = 19)
    private Date lastLogin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "login_attempt_time", length = 19)
    private Date loginAttemptTime;

    @Column(name = "login_attempt_count", length = 11)
    private Integer loginAttemptCount;

    @Column(name = "mail_token", length = 255)
    private String mailToken;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "mail_sent_time", length = 19)
    private Date mailSentTime;

    @Column(name = "activation_mail_count", length = 11)
    private Integer activationMailCount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "password_last_updated", length = 19)
    private Date passwordLastUpdated;

    @Column(name = "otp_count", length = 11)
    private Integer otpCount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "otp_mail_sent_time", length = 19)
    private Date otpMailSentTime;
    
    @Column(name = "two_factor_auth", nullable = false)
    private Boolean twoFactorAuth;

    @Column(name = "azure_ad_user", nullable = false)
    private Boolean azureAdUserAuth;

    @Column(name = "azure_ad_user_name", length = 50)
    private String azureAdUsername;

    @Column(name = "user_status", length = 11)
    private String userStatus;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", length = 19)
    private Date createdDate;
    
    @Column(name = "created_by_user_id", nullable = true)
    private Long createdByUserId;
    
    @Column(name = "updated_by_user_id", nullable = true)
    private Long updatedByUserId;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date", length = 19)
    private Date updatedDate;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;
    
    @Column(name = "timezone_display_preference",nullable = false)
    private String timezoneDisplayPreference;
    
    @Column(name = "datetime_display_preference",nullable = false)
    private String timeStampDisplayPreference;
}