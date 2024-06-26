package site.timecapsulearchive.core.global.security.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import site.timecapsulearchive.core.domain.auth.service.TokenManager;
import site.timecapsulearchive.core.global.error.ErrorCode;
import site.timecapsulearchive.core.global.error.ErrorResponse;
import site.timecapsulearchive.core.global.security.oauth.dto.CustomOAuth2User;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenManager tokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Authentication authentication
    ) throws IOException {

        try {
            final CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            final PrintWriter writer = response.getWriter();

            if (oAuth2User.isNotVerified()) {
                writer.write(objectMapper.writeValueAsString(
                    tokenService.createTemporaryToken(oAuth2User.getId())
                ));
                return;
            }

            writer.write(objectMapper.writeValueAsString(
                tokenService.createNewToken(oAuth2User.getId())
            ));

        } catch (final Exception exception) {
            log.info("oauth2 인증 실패", exception);

            final ErrorResponse errorResponse = ErrorResponse.fromErrorCode(
                ErrorCode.INTERNAL_SERVER_ERROR
            );

            response.getWriter()
                .write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
