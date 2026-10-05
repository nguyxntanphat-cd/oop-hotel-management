package hotel.menu;

import hotel.util.InputHelper;

/**
 * Lớp trừu tượng cho mọi menu console.
 * <p>
 * Lớp con cung cấp danh sách lựa chọn ({@link #getOptions()}) và cách xử lý từng lựa chọn
 * ({@link #handle(int)}). Lựa chọn 0 luôn là "Quay lại". Đa hình: {@link #run()} viết 1 lần,
 * dùng chung cho mọi menu con.
 */
public abstract class Menu {
    protected final String title;

    public Menu(String title) {
        this.title = title;
    }

    /** Các dòng lựa chọn; phần tử thứ i tương ứng với phím (i + 1). */
    protected abstract String[] getOptions();

    /** Xử lý lựa chọn từ 1 đến getOptions().length. */
    protected abstract void handle(int choice);

    /** Nhãn của lựa chọn 0. */
    protected String getExitLabel() {
        return "Quay lại";
    }

    /** Gọi khi người dùng chọn 0 để rời menu (ví dụ: lưu file). */
    protected void onExit() {
    }

    public void run() {
        while (true) {
            String[] options = getOptions();
            System.out.println();
            System.out.println("========== " + title + " ==========");
            for (int i = 0; i < options.length; i++) {
                System.out.println((i + 1) + ". " + options[i]);
            }
            System.out.println("0. " + getExitLabel());
            int choice = InputHelper.readInt("Chọn: ", 0, options.length);
            if (choice == 0) {
                onExit();
                return;
            }
            try {
                handle(choice);
            } catch (RuntimeException e) {
                // Lưới an toàn: lỗi ở 1 chức năng không làm dừng cả chương trình
                System.out.println("Đã xảy ra lỗi: " + e);
            }
        }
    }

    public String getTitle() {
        return title;
    }
}
