package assignment.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.Instant;

import static assignment.generated.Tables.AUDIT_LOGS;

@Repository
public class AuditLogRepository {

    private final DSLContext dslContext;

    public AuditLogRepository(final DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public void save(final String ipAddress, final int responseCode, final String requestParam) {
        dslContext.insertInto(AUDIT_LOGS)
                .set(AUDIT_LOGS.IP_ADDRESS, ipAddress)
                .set(AUDIT_LOGS.RESPONSE_CODE, responseCode)
                .set(AUDIT_LOGS.REQUEST_PARAMS, requestParam)
                .set(AUDIT_LOGS.TIMESTAMP, Instant.now().toString())
                .execute();
    }

}
