package io.github.stanislav.smartparkingsystem.exception;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({
			ParkingLotNotFoundException.class,
			ParkingLevelNotFoundException.class,
			ParkingSlotNotFoundException.class,
			VehicleNotFoundException.class,
			NoAvailableSlotException.class
	})
	public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, WebRequest request) {
		log.error("Not found: {}", ex.getMessage());
		return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(VehicleAlreadyParkedException.class)
	public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex, WebRequest request) {
		log.error("Conflict: {}", ex.getMessage());
		return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex, WebRequest request) {
		log.error("Bad request: {}", ex.getMessage());
		return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleValidation(
			MethodArgumentNotValidException ex,
			WebRequest request
	) {
		log.error("Validation failed: {}", ex.getMessage());

		Map<String, String> errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.collect(Collectors.toMap(
						FieldError::getField,
						error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
						(existing, replacement) -> existing
				));

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ValidationErrorResponse(
						HttpStatus.BAD_REQUEST.value(),
						"Validation Failed",
						"Invalid request parameters",
						extractPath(request),
						Instant.now(),
						errors
				));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleMissingBody(
			HttpMessageNotReadableException ex,
			WebRequest request
	) {
		log.error("Invalid request body: {}", ex.getMessage());

		String message = "Invalid request body";

		if(ex.getMessage() != null) {
			if(ex.getMessage().contains("Required request body is missing")) {
				message = "Request body is required but missing. Please provide a valid JSON body.";
			} else if(ex.getMessage().contains("not one of the values accepted for Enum")) {
				message = "Invalid enum value. Please check accepted values.";
			} else if(ex.getMessage().contains("JSON parse error")) {
				message = "Invalid JSON format. Please check your request body syntax.";
			}
		}

		return buildResponse(message, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(
			MethodArgumentTypeMismatchException ex,
			WebRequest request
	) {
		log.error("Type mismatch: {}", ex.getMessage());

		String message = String.format(
				"Parameter '%s' should be of type '%s'",
				ex.getName(),
				ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
		);

		return buildResponse(message, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingParam(
			MissingServletRequestParameterException ex,
			WebRequest request
	) {
		log.error("Missing parameter: {}", ex.getParameterName());

		String message = String.format("Required parameter '%s' is missing", ex.getParameterName());
		return buildResponse(message, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(
			ConstraintViolationException ex,
			WebRequest request
	) {
		log.error("Constraint violation: {}", ex.getMessage());

		String message = ex.getConstraintViolations().stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.joining(", "));

		return buildResponse(message, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, WebRequest request) {
		log.error("Unexpected error: ", ex);
		return buildResponse(
				"An unexpected error occurred. Please contact support if the problem persists.",
				HttpStatus.INTERNAL_SERVER_ERROR,
				request
		);
	}

	private ResponseEntity<ErrorResponse> buildResponse(String message, HttpStatus status, WebRequest request) {
		return ResponseEntity
				.status(status)
				.body(new ErrorResponse(
						status.value(),
						status.getReasonPhrase(),
						message,
						extractPath(request),
						Instant.now()
				));
	}

	private String extractPath(WebRequest request) {
		return request.getDescription(false).replace("uri=", "");
	}

	public record ErrorResponse(
			int status,
			String error,
			String message,
			String path,
			Instant timestamp
	) {
	}

	public record ValidationErrorResponse(
			int status,
			String error,
			String message,
			String path,
			Instant timestamp,
			Map<String, String> fieldErrors
	) {
	}
}