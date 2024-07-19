package aisaac.entities;

import java.util.Date;

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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Table(name = "user")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class User {

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
    
    @Column(name = "role_id")
    private Long roleId;
    
    @Column(name = "display_role", length = 255)
    private String displayRole;

    @Column(name = "country_name", length = 255)
    private String countryName;

    @Column(name = "country_isd_code", length = 20)
    private String countryIsdCode;

    @Column(name = "mobile_no", length = 32)
    private String mobileNumber;

    @Column(name = "user_salt", length = 255)
    private String userSalt;

    @Column(name = "get_sms", nullable = false)
    private boolean getSms;

    @Column(name = "get_email", nullable = false)
    private boolean getEmail;

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
    private boolean twoFactorAuth;

    @Column(name = "is_azure_ad_user", nullable = false)
    private boolean azureAdUserAuth;

    @Column(name = "azure_ad_user_name", length = 50)
    private String azureAdUsername;

    @Column(name = "user_status", length = 11)
    private Integer userStatus;


    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;
    
    @Column(name = "timezone_display_preference",nullable = false)
    private String timezoneDisplayPreference;
    
    @Column(name = "datetime_display_preference",nullable = false)
    private String timeStampDisplayPreference;
}
