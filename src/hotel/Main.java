package hotel;

import hotel.menu.MainMenu;
import hotel.util.AppConfig;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Điểm bắt đầu chương trình Quản lý khách sạn.
 * Chạy từ thư mục gốc của repo để chương trình tìm thấy thư mục data/.
 */
public class Main {
    public static void main(String[] args) {
        enableUtf8Console();
        System.out.println("Chào mừng đến với hệ thống quản lý " + AppConfig.HOTEL_NAME);
        HotelData.loadAll();
        new MainMenu().run();
    }

    /** In tiếng Việt có dấu đúng trên console (Windows cần chạy thêm lệnh chcp 65001). */
    private static void enableUtf8Console() {
        try {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));
        } catch (UnsupportedEncodingException ignored) {
            // UTF-8 luôn được hỗ trợ
        }
    }
}
