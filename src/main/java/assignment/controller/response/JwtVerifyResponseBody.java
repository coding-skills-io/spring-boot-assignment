package assignment.controller.response;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder
public record JwtVerifyResponseBody(boolean eligible, String user, List<?> roles, String tokenType) {
}
