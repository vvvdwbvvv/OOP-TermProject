import java.util.prefs.Preferences;
import java.time.LocalDate;

public class ReminderStatus {
	private static final Preferences prefs = Preferences.userRoot().node("training_reminder");

	public static boolean isTodayDismissed() {
		String today = LocalDate.now().toString();
		return today.equals(prefs.get("dismissedDate", ""));
	}

	public static void setTodayDismissed() {
		prefs.put("dismissedDate", LocalDate.now().toString());
		prefs.remove("snooze"); // 取消稍後提醒
	}

	public static boolean isSnoozed() {
		return prefs.getBoolean("snooze", false);
	}

	public static void setSnoozed() {
		prefs.putBoolean("snooze", true);
	}

	public static void clearSnooze() {
		prefs.remove("snooze");
	}
	
	public static void resetDismissed() {
	    prefs.remove("dismissedDate");
	    prefs.remove("snooze");
	}
}
