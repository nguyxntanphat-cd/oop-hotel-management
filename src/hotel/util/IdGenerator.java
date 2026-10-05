package hotel.util;

import hotel.common.Identifiable;

import java.util.Collection;

/**
 * Sinh mã tự động dạng TIỀN_TỐ + 3 chữ số, ví dụ: KH001, NV002, DP010.
 */
public final class IdGenerator {
    private IdGenerator() {
    }

    /** Tìm số lớn nhất trong các mã có cùng tiền tố rồi cộng thêm 1. */
    public static String next(String prefix, Collection<? extends Identifiable> existing) {
        int max = 0;
        for (Identifiable item : existing) {
            String id = item.getId();
            if (id != null && id.toUpperCase().startsWith(prefix.toUpperCase())) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(prefix.length())));
                } catch (NumberFormatException ignored) {
                    // Mã không theo định dạng chuẩn thì bỏ qua
                }
            }
        }
        return String.format("%s%03d", prefix, max + 1);
    }
}
