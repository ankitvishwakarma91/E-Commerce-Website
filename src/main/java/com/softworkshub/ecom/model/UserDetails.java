package com.softworkshub.ecom.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @NotBlank(message = "Name Should not be blank")
    private String name;
  //  @NotBlank(message = "Phone No Should not be blank")
    private String mobileNumber;
    //@NotBlank(message = "Email Should not be blank")
//    @Email(message = "provide valid email")
    private String email;
//    @NotBlank(message = "Address Should not be blank")
    private String address;
//    @NotBlank(message = "City Should not be blank")
    private String city;
//    @NotBlank(message = "State Should not be blank")
    private String state;
//    @NotBlank(message = "Pin Code Should not be blank")
    private String pincode;
//    @NotBlank(message = "Password Should not be blank")
//    @Size(min = 6,max = 10)
    private String password;
//    @NotBlank(message = "Password Should not be blank")
//    @Size(min = 6,max = 10)
    private String confirmpassword;
//    @NotBlank(message = "Please Provide Image in Jpg Format")
    private String profileImage;
    private String role;
    private Boolean isEnabled;
    private Boolean accountNonLocked;
    private Integer failedLoginAttempts;
    private Date lockTime;
    private String resetToken;
}
