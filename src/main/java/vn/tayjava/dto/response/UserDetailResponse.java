package vn.tayjava.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import vn.tayjava.util.Gender;
import vn.tayjava.util.UserStatus;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
public class UserDetailResponse implements Serializable {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Date dateOfBirth;

    private Gender gender;

    private String username;

    private String type;

    private UserStatus status;

    public UserDetailResponse(Long id, String firstName, String lastName, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
}
