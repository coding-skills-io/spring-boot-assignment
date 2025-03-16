package assignment.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberRequest(
        @NotBlank(message = "employeeCode is required")
        @Size(min = 1, max = 10, message = "employeeCode supports min 1 and max 10 characters")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "employeeCode only support alphanumeric characters")
        String employeeCode,

        @NotBlank(message = "membersStatus is required")
        @Pattern(regexp = "^(?i)(employee|dependent)$", message = "memberStatus only supports 'employee' or 'dependent' values")
        String memberStatus,

        @NotBlank(message = "employeeId is required")
        @Size(min = 5, max = 10, message = "employeeId should have from 5 to 10 characters")
        String employeeId,

        @NotBlank(message = "employeeDateOfBirth is required")
        @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$", message = "employeeDateOfBirth should be in ISO 8601 format")
        String employeeDateOfBirth,

        @Size(min = 1, max = 50, message = "employeeFirstName should be from 1 to 50 characters")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "employeeFirstName should be only letters or space")
        String employeeFirstName,

        @Size(min = 1, max = 50, message = "employeeLastName should be from 1 to 50 characters")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "employeeLastName should be only letters or space")
        String employeeLastName
) { }
