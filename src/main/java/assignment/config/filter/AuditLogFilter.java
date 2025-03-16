package assignment.config.filter;

import assignment.repository.AuditLogRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Component
public class AuditLogFilter extends OncePerRequestFilter {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();

    private final AuditLogRepository auditLogRepository;

    public AuditLogFilter(final AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            CompletableFuture.runAsync(() -> saveAuditLog(request, response), EXECUTOR_SERVICE);
        }
    }

    private void saveAuditLog(final HttpServletRequest request, final HttpServletResponse response) {
        String ipAddress = request.getRemoteAddr();
        String parameters = convertParamsToString(request.getParameterMap());

        auditLogRepository.save(ipAddress, response.getStatus(), parameters);
    }

    private String convertParamsToString(Map<String, String[]> params) {
        return params.entrySet().stream()
                .map(e -> e.getKey() + "=" + Arrays.toString(e.getValue()))
                .collect(Collectors.joining(", "));
    }
}
