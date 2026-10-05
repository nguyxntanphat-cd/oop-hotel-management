package hotel.menu;

import hotel.common.BaseList;
import hotel.common.FileSerializable;
import hotel.common.Identifiable;
import hotel.common.Searchable;
import hotel.util.InputHelper;

/**
 * Menu chuẩn cho một module: 1.Xem 2.Thêm 3.Sửa 4.Xóa 5.Tìm kiếm (+ chức năng riêng).
 * <p>
 * Lớp con chỉ cần cài đặt {@link #inputNew()} và {@link #inputEdit(Object)}.
 * Có thể ghi đè {@link #getExtraOptions()} / {@link #handleExtra(int)} để thêm chức năng riêng,
 * {@link #canRemove(Object)} để chặn xóa. Khi rời menu, dữ liệu được tự động lưu xuống file.
 */
public abstract class CrudMenu<T extends Identifiable & FileSerializable & Searchable> extends Menu {
    private static final String[] CRUD_OPTIONS = {
            "Xem danh sách", "Thêm mới", "Sửa", "Xóa", "Tìm kiếm"
    };

    protected final BaseList<T> list;

    public CrudMenu(String title, BaseList<T> list) {
        super(title);
        this.list = list;
    }

    /**
     * Nhập thông tin từ bàn phím để tạo đối tượng mới (nên sinh mã bằng list.nextId(...)).
     * Trả về null nếu người dùng hủy.
     */
    protected abstract T inputNew();

    /**
     * Nhập thông tin sửa. Nên tạo BẢN SAO từ old (copy constructor), sửa trên bản sao rồi trả về.
     * Trả về null nếu người dùng hủy.
     */
    protected abstract T inputEdit(T old);

    /** Các chức năng riêng của module, hiển thị sau 5 chức năng chuẩn. */
    protected String[] getExtraOptions() {
        return new String[0];
    }

    /** Xử lý chức năng riêng; index bắt đầu từ 0 theo thứ tự trong getExtraOptions(). */
    protected void handleExtra(int index) {
    }

    /**
     * Kiểm tra trước khi xóa, ví dụ phòng đang có khách thì không cho xóa.
     * Nếu không cho xóa thì in lý do và trả về false.
     */
    protected boolean canRemove(T item) {
        return true;
    }

    @Override
    protected final String[] getOptions() {
        String[] extra = getExtraOptions();
        String[] all = new String[CRUD_OPTIONS.length + extra.length];
        System.arraycopy(CRUD_OPTIONS, 0, all, 0, CRUD_OPTIONS.length);
        System.arraycopy(extra, 0, all, CRUD_OPTIONS.length, extra.length);
        return all;
    }

    @Override
    protected final void handle(int choice) {
        switch (choice) {
            case 1:
                list.displayAll();
                break;
            case 2:
                doAdd();
                break;
            case 3:
                doEdit();
                break;
            case 4:
                doRemove();
                break;
            case 5:
                doSearch();
                break;
            default:
                handleExtra(choice - CRUD_OPTIONS.length - 1);
        }
    }

    protected void doAdd() {
        T item = inputNew();
        if (item == null) {
            System.out.println("Đã hủy thêm.");
        } else if (list.add(item)) {
            list.saveToFile();
            System.out.println("Thêm thành công: " + item.getId());
        } else {
            System.out.println("Thêm thất bại: mã " + item.getId() + " đã tồn tại.");
        }
    }

    protected void doEdit() {
        T old = askExisting("Nhập mã cần sửa: ");
        if (old == null) {
            return;
        }
        System.out.println(old);
        System.out.println("(Nhấn Enter để giữ nguyên giá trị cũ)");
        T edited = inputEdit(old);
        if (edited == null) {
            System.out.println("Đã hủy sửa.");
        } else if (list.update(old.getId(), edited)) {
            list.saveToFile();
            System.out.println("Cập nhật thành công.");
        } else {
            System.out.println("Cập nhật thất bại.");
        }
    }

    protected void doRemove() {
        T item = askExisting("Nhập mã cần xóa: ");
        if (item == null) {
            return;
        }
        System.out.println(item);
        if (!canRemove(item)) {
            return;
        }
        if (InputHelper.confirm("Bạn chắc chắn muốn xóa?")) {
            list.remove(item.getId());
            list.saveToFile();
            System.out.println("Đã xóa " + item.getId() + ".");
        } else {
            System.out.println("Đã hủy xóa.");
        }
    }

    protected void doSearch() {
        String keyword = InputHelper.readLine("Nhập từ khóa (mã, tên, ...): ");
        list.display(list.search(keyword));
    }

    /** Hỏi mã và trả về đối tượng tương ứng; in thông báo và trả về null nếu không có. */
    protected T askExisting(String prompt) {
        String id = InputHelper.readString(prompt);
        T item = list.findById(id);
        if (item == null) {
            System.out.println("Không tìm thấy mã " + id + ".");
        }
        return item;
    }

    @Override
    protected void onExit() {
        list.saveToFile();
    }
}
