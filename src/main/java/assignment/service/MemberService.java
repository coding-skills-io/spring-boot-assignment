package assignment.service;

import assignment.controller.request.MemberRequest;
import assignment.controller.response.MemberDataBuilder;
import assignment.controller.response.MemberResponseBody;
import assignment.exception.InvalidEmployeeCodeException;
import assignment.exception.RecordNotFoundException;
import assignment.exception.ValidationException;
import assignment.generated.tables.records.EligibilityRecordsRecord;
import assignment.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.jooq.tools.StringUtils.isBlank;

@Service
public class MemberService {

    private static final String MEMBER_STATUS_DEPENDENT = "dependent";
    private static final String MEMBER_STATUS_EMPLOYEE = "employee";

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponseBody findMember(final MemberRequest memberRequest) {
        validateMemberRequest(memberRequest);

        Optional<EligibilityRecordsRecord> record;
        if (memberRequest.memberStatus().equalsIgnoreCase(MEMBER_STATUS_EMPLOYEE)) {
            record = memberRepository.findByMemberId(memberRequest.employeeFirstName(), memberRequest.employeeLastName(), memberRequest.employeeDateOfBirth());
        } else {
            record = memberRepository.findByIdAndDob(memberRequest.employeeId(), memberRequest.employeeDateOfBirth());
        }

        EligibilityRecordsRecord eligibilityRecordsRecord = record
                .orElseThrow(() -> new RecordNotFoundException("No record is found with the given data"));

        if (!eligibilityRecordsRecord.getEmployeeGroup().equalsIgnoreCase(memberRequest.employeeCode())) {
            throw new InvalidEmployeeCodeException("Employee code does not match");
        }

        return toMemberResponseBody(record.get());
    }

    private static MemberResponseBody toMemberResponseBody(final EligibilityRecordsRecord record) {
        var data = MemberDataBuilder.builder()
                .memberUniqueId(record.getMemberUniqueId())
                .firstName(record.getFirstName())
                .lastName(record.getLastName())
                .dateOfBirth(record.getDateOfBirth())
                .eligibilityStartDate(record.getEligibilityStartDate())
                .eligibilityEndDate(record.getEligibilityEndDate())
                .employeeStatus(record.getEmployeeStatus())
                .employeeGroup(record.getEmployeeGroup())
                .build();

        return new MemberResponseBody("success", data);
    }

    private static void validateMemberRequest(final MemberRequest memberRequest) {
        String memberStatus = memberRequest.memberStatus();
        if (memberStatus.equalsIgnoreCase(MEMBER_STATUS_EMPLOYEE)) {
            if (isBlank(memberRequest.employeeFirstName()) || isBlank(memberRequest.employeeLastName())) {
                throw new ValidationException("employeeFirstName and employeeLastName are required when memberStatus is employee");
            }
        }
    }

}
