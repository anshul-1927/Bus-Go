package dev.datvt.busfun.utils;

import java.util.HashMap;
import java.util.List;

import dev.datvt.busfun.R;
import dev.datvt.busfun.models.RssItem;


public class Variables {
    public static final String[] PAPERS = {"VnExpress", "24h", "Dân trí",
            "ICT News"};
    public static final int[] ICONS = {R.drawable.vnexpress,
            R.drawable.news24h, R.drawable.dantri, R.drawable.ictnews};

    // VnExpress
    public static final String[] VNEXPRESS_CATEGORIES = {"Trang chủ",
            "Thời sự", "Thế giới", "Kinh doanh", "Giải trí", "Thể thao",
            "Pháp luật", "Giáo dục", "Sức khỏe", "Gia đình", "Du lịch",
            "Khoa học", "Số hóa", "Xe", "Cộng đồng", "Tâm sự", "Cười"};
    public static final String[] VNEXPRESS_LINKS = {
            "http://vnexpress.net/rss/tin-moi-nhat.rss",
            "http://vnexpress.net/rss/thoi-su.rss",
            "http://vnexpress.net/rss/the-gioi.rss",
            "http://vnexpress.net/rss/kinh-doanh.rss",
            "http://vnexpress.net/rss/giai-tri.rss",
            "http://vnexpress.net/rss/the-thao.rss",
            "http://vnexpress.net/rss/phap-luat.rss",
            "http://vnexpress.net/rss/giao-duc.rss",
            "http://vnexpress.net/rss/suc-khoe.rss",
            "http://vnexpress.net/rss/gia-dinh.rss",
            "http://vnexpress.net/rss/du-lich.rss",
            "http://vnexpress.net/rss/khoa-hoc.rss",
            "http://vnexpress.net/rss/so-hoa.rss",
            "http://vnexpress.net/rss/oto-xe-may.rss",
            "http://vnexpress.net/rss/cong-dong.rss",
            "http://vnexpress.net/rss/tam-su.rss",
            "http://vnexpress.net/rss/cuoi.rss"};

    // 24h
    public static final String[] NEWS24H_CATEGORIES = {"Tin tức trong ngày",
            "Bóng đá", "An ninh - Hình sự", "Thời trang", "Thời trang Hi-tech",
            "Tài chính – Bất động sản", "Ẩm thực", "Làm đẹp", "Phim",
            "Giáo dục - du học", "Bạn trẻ - Cuộc sống", "Ca nhạc - MTV",
            "Thể thao", "Phi thường - kỳ quặc", "Công nghệ thông tin",
            "Ô tô - Xe máy", "Thị trường - Tiêu dùng", "Du lịch",
            "Sức khỏe đời sống", "Cười 24h"};
    public static final String[] NEWS24H_LINKS = {
            "http://www.24h.com.vn/upload/rss/tintuctrongngay.rss",
            "http://www.24h.com.vn/upload/rss/bongda.rss",
            "http://www.24h.com.vn/upload/rss/anninhhinhsu.rss",
            "http://www.24h.com.vn/upload/rss/thoitrang.rss",
            "http://www.24h.com.vn/upload/rss/thoitranghitech.rss",
            "http://www.24h.com.vn/upload/rss/taichinhbatdongsan.rss",
            "http://www.24h.com.vn/upload/rss/amthuc.rss",
            "http://www.24h.com.vn/upload/rss/lamdep.rss",
            "http://www.24h.com.vn/upload/rss/phim.rss",
            "http://www.24h.com.vn/upload/rss/giaoducduhoc.rss",
            "http://www.24h.com.vn/upload/rss/bantrecuocsong.rss",
            "http://www.24h.com.vn/upload/rss/canhacmtv.rss",
            "http://www.24h.com.vn/upload/rss/thethao.rss",
            "http://www.24h.com.vn/upload/rss/phithuongkyquac.rss",
            "http://www.24h.com.vn/upload/rss/congnghethongtin.rss",
            "http://www.24h.com.vn/upload/rss/otoxemay.rss",
            "http://www.24h.com.vn/upload/rss/thitruongtieudung.rss",
            "http://www.24h.com.vn/upload/rss/dulich.rss",
            "http://www.24h.com.vn/upload/rss/suckhoedoisong.rss",
            "http://www.24h.com.vn/upload/rss/cuoi24h.rss"};

    // Dan tri
    public static final String[] DANTRI_CATEGORIES = {"Trang chủ", "Sức khỏe",
            "Xã hội", "Giải trí", "Giáo dục - Khuyến học", "Thể thao",
            "Thế Giới", "Kinh doanh", "Ô tô - Xe máy", "Sức mạnh số",
            "Tình yêu - Giới tính", "Chuyện lạ", "Việc làm", "Nhịp sống trẻ",
            "Tấm lòng nhân ái", "Pháp luật", "Bạn đọc", "Diễn đàn",
            "Tuyển sinh", "Blog", "Văn hóa", "Du học", "Du lịch", "Đời sống",
            "SEA Games 28"};
    public static final String[] DANTRI_LINKS = {
            "http://dantri.com.vn/trangchu.rss",
            "http://dantri.com.vn/suc-khoe.rss",
            "http://dantri.com.vn/xa-hoi.rss",
            "http://dantri.com.vn/giai-tri.rss",
            "http://dantri.com.vn/giao-duc-khuyen-hoc.rss",
            "http://dantri.com.vn/the-thao.rss",
            "http://dantri.com.vn/the-gioi.rss",
            "http://dantri.com.vn/kinh-doanh.rss",
            "http://dantri.com.vn/o-to-xe-may.rss",
            "http://dantri.com.vn/suc-manh-so.rss",
            "http://dantri.com.vn/tinh-yeu-gioi-tinh.rss",
            "http://dantri.com.vn/chuyen-la.rss",
            "http://dantri.com.vn/viec-lam.rss",
            "http://dantri.com.vn/nhip-song-tre.rss",
            "http://dantri.com.vn/tam-long-nhan-ai.rss",
            "http://dantri.com.vn/phap-luat.rss",
            "http://dantri.com.vn/ban-doc.rss",
            "http://dantri.com.vn/dien-dan.rss",
            "http://dantri.com.vn/tuyen-sinh.rss",
            "http://dantri.com.vn/blog.rss",
            "http://dantri.com.vn/van-hoa.rss",
            "http://dantri.com.vn/du-hoc.rss",
            "http://dantri.com.vn/du-lich.rss",
            "http://dantri.com.vn/doi-song.rss",
            "http://dantri.com.vn/sea-games-28.rss"};

    // ICT News
    public static final String[] ICTNEWS_CATEGORIES = {"Viễn thông",
            "Internet", "CNTT", "Nước mạnh CNTT", "Phần mềm", "Phần cứng",
            "VNPT - Cuộc sống đích thực", "Góc Viettel", "Bảo mật",
            "Thế giới số", "Máy ảnh số", "Di động", "Máy tính",
            "Hình ảnh âm thanh", "Phụ kiện", "Thủ thuật", "Game",
            "Công nghệ 360", "Báo cáo, thống kê, các con số"};
    public static final String[] ICTNEWS_LINKS = {
            "http://ictnews.vn/rss/vien-thong",
            "http://ictnews.vn/rss/internet", "http://ictnews.vn/rss/cntt",
            "http://ictnews.vn/rss/nuoc-manh-cntt",
            "http://ictnews.vn/rss/phan-mem",
            "http://ictnews.vn/rss/phan-cung",
            "http://ictnews.vn/rss/vien-thong/vnpt-cuoc-song-dich-thuc",
            "http://ictnews.vn/rss/vien-thong/goc-viettle",
            "http://ictnews.vn/rss/vien-thong/bao-mat",
            "http://ictnews.vn/rss/vien-thong/the-gioi-so",
            "http://ictnews.vn/rss/vien-thong/may-anh-so",
            "http://ictnews.vn/rss/vien-thong/di-dong",
            "http://ictnews.vn/rss/vien-thong/may-tinh",
            "http://ictnews.vn/rss/vien-thong/hinh-anh-am-thanh",
            "http://ictnews.vn/rss/vien-thong/phu-kien",
            "http://ictnews.vn/rss/vien-thong/thu-thuat",
            "http://ictnews.vn/rss/vien-thong/game",
            "http://ictnews.vn/rss/vien-thong/cong-nghe-360",
            "http://ictnews.vn/rss/khoi-nghiep/bao-cao-thong-ke-cac-con-so"};
    // All
    public static final String[][] CATEGORIES = {VNEXPRESS_CATEGORIES,
            NEWS24H_CATEGORIES, DANTRI_CATEGORIES, ICTNEWS_CATEGORIES};
    public static final String[][] LINKS = {VNEXPRESS_LINKS, NEWS24H_LINKS,
            DANTRI_LINKS, ICTNEWS_LINKS};

    public static final String PAPER = "paper";
    public static final String CATEGORY = "category";
    public static final String LINK = "link";

    public static HashMap<Integer, List<RssItem>> newsMap = new HashMap<Integer, List<RssItem>>();

}
