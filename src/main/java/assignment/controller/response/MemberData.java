package assignment.controller.response;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record MemberData(
      String memberUniqueId,
      String firstName,
      String lastName,
      String dateOfBirth,
      String eligibilityStartDate,
      String eligibilityEndDate,
      String employeeStatus,
      String employeeGroup
) { }
