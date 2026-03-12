package online.beneaththestars.btsbackend.exceptions;

import online.beneaththestars.btsbackend.models.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        int code = ex.getStatusCode().value();
        String message = ex.getReason() != null ? ex.getReason() : "Some error!";

        return ResponseEntity.status(code)
                .body(new ApiErrorResponse(
                        code,
                        message,
                        Map.of(
                                "exception", ex.getClass().getSimpleName()
                        )
                ));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(
                        404,
                        "API route not found!",
                        Map.of(
                                "exception", ex.getClass().getSimpleName(),
                                "path", ex.getResourcePath()
                        )
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleOther(Exception ex) {
        Throwable rootCause = getRootCause(ex);

        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(
                        500,
                        "Unexpected backend error!",
                        Map.of(
                                "exception", ex.getClass().getSimpleName(),
                                "message", ex.getMessage() != null ? ex.getMessage() : "No exception message",
                                "rootCause", rootCause.getMessage() != null ? rootCause.getMessage() : "No root cause message"
                        )
                ));
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable result = throwable;
        while (result.getCause() != null) {
            result = result.getCause();
        }
        return result;
    }
}