package io.github.stanislav.smartparkingsystem.util;

public final class Constants {
	public static final String API_ADMIN_PARKING_PATH = "/api/admin";
	public static final String API_CREATE_PARKING_PATH = "/parking-lots";
	public static final String API_PARKING_PATH = "/parking-lots/{lotId}";
	public static final String API_ADD_LEVEL_PARKING_PATH = "/parking-lots/{lotId}/levels";
	public static final String API_DELETE_LEVEL_PARKING_PATH = "/parking-levels/{levelId}";
	public static final String API_ADD_SLOT_PATH = "/parking-levels/{levelId}/slots";
	public static final String API_SLOT_PATH = "/slots/{slotId}";

	public static final String API_PATH = "/api";
	public static final String API_PARKING_LOTS_PATH = "/parking-lots";
	public static final String API_ACTIVE_SESSION_PATH = "/check/{vehicleId}";

	public static final String API_PARKING_INFO_PATH = "/api/parking";
	public static final String API_CHECK_IN_PATH = "/check-in";
	public static final String API_CHECK_OUT_PATH = "/check-out/{vehicleId}";

	private Constants() {
		throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
	}
}
