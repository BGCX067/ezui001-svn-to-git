package cn.com.vapk.vstore.client.util;

import android.content.Intent;

final public class DebugUtils {

	public final static String flags(Intent intent) {
		return flags(intent.getFlags());
	}

	public final static String flags(int flags) {
		StringBuffer sb = new StringBuffer(128);
		if ((Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_BROUGHT_TO_FRONT");
		if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_CLEAR_TOP");
		if ((Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET");
		if ((Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_EXCLUDE_FROM_RECENTS");
		if ((Intent.FLAG_ACTIVITY_FORWARD_RESULT & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_FORWARD_RESULT");
		if ((Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_LAUNCHED_FROM_HISTORY");
		if ((Intent.FLAG_ACTIVITY_MULTIPLE_TASK & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_MULTIPLE_TASK");
		if ((Intent.FLAG_ACTIVITY_NEW_TASK & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_NEW_TASK");
		if ((Intent.FLAG_ACTIVITY_NO_ANIMATION & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_NO_ANIMATION");
		if ((Intent.FLAG_ACTIVITY_NO_HISTORY & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_NO_HISTORY");
		if ((Intent.FLAG_ACTIVITY_NO_USER_ACTION & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_NO_USER_ACTION");
		if ((Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_PREVIOUS_IS_TOP");
		if ((Intent.FLAG_ACTIVITY_REORDER_TO_FRONT & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_REORDER_TO_FRONT");
		if ((Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_RESET_TASK_IF_NEEDED");
		if ((Intent.FLAG_ACTIVITY_SINGLE_TOP & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_SINGLE_TOP");
		if ((Intent.FLAG_DEBUG_LOG_RESOLUTION & flags) != 0)
			sb.append("\nFLAG_DEBUG_LOG_RESOLUTION");
		if ((Intent.FLAG_FROM_BACKGROUND & flags) != 0)
			sb.append("\nFLAG_FROM_BACKGROUND");
		if ((Intent.FLAG_GRANT_READ_URI_PERMISSION & flags) != 0)
			sb.append("\nFLAG_GRANT_READ_URI_PERMISSION");
		if ((Intent.FLAG_GRANT_WRITE_URI_PERMISSION & flags) != 0)
			sb.append("\nFLAG_GRANT_WRITE_URI_PERMISSION");
		if ((Intent.FLAG_RECEIVER_REGISTERED_ONLY & flags) != 0)
			sb.append("\nFLAG_RECEIVER_REGISTERED_ONLY");
		if ((Intent.FLAG_RECEIVER_REPLACE_PENDING & flags) != 0)
			sb.append("\nFLAG_RECEIVER_REPLACE_PENDING");
		if ((Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT & flags) != 0)
			sb.append("\nFLAG_ACTIVITY_BROUGHT_TO_FRONT");
		return sb.toString();
	}
}