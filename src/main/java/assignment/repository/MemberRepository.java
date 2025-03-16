package assignment.repository;

import assignment.generated.tables.records.EligibilityRecordsRecord;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static assignment.generated.Tables.ELIGIBILITY_RECORDS;

@Repository
public class MemberRepository {

    private final DSLContext dsl;

    public MemberRepository(final DSLContext dsl) {
        this.dsl = dsl;
    }

    public Optional<EligibilityRecordsRecord> findByIdAndDob(final String id, final String dob) {
        Condition condition = ELIGIBILITY_RECORDS.MEMBER_UNIQUE_ID.eq(id)
                .and(ELIGIBILITY_RECORDS.DATE_OF_BIRTH.eq(dob));

        EligibilityRecordsRecord record = dsl.selectFrom(ELIGIBILITY_RECORDS)
                .where(condition)
                .fetchOne();
        return Optional.ofNullable(record);
    }

    public Optional<EligibilityRecordsRecord> findByMemberId(final String firstName, final String lastName, final String dob) {
        var condition = ELIGIBILITY_RECORDS.FIRST_NAME.eq(firstName)
                .and(ELIGIBILITY_RECORDS.LAST_NAME.eq(lastName))
                .and(ELIGIBILITY_RECORDS.DATE_OF_BIRTH.eq(dob));

        EligibilityRecordsRecord record = dsl.selectFrom(ELIGIBILITY_RECORDS)
                .where(condition)
                .fetchOneInto(EligibilityRecordsRecord.class);
        return Optional.ofNullable(record);
    }
}
