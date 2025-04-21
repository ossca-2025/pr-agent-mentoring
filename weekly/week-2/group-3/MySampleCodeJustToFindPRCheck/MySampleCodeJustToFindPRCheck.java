import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

// 이거 나중에 PR 코드리뷰용으로 진행해보자 정보 수정해서 올리기기
public class MySampleCodeJustToFindPRCheck {
    public static void main(String[] args) {
        int batchSize = 5000;
        Connection conn = null;
        PreparedStatement pstmt = null;

        // -------------------------------- 수정할 부분 --------------------------------
        String url = "jdbc:trino://172.25.5.15:8088?user=hue&SSL=false";
        String getTableName = "wpdb.wordpress.wp_comments";
        String insertTableName = "hive.wpdb.wp_comments";
        String externalLocation = "hdfs://namenode:9000/wordpress/wp_comments";
        String createColumnsNames = "comment_ID, comment_post_ID, comment_author, comment_author_email, comment_author_url, comment_author_IP, comment_date, comment_date_gmt, comment_content, comment_karma, comment_approved, comment_agent, comment_type, comment_parent, user_id, comment_date_p";
        String columnsNames = "comment_ID, comment_post_ID, comment_author, comment_author_email, comment_author_url, comment_author_IP, comment_date, comment_date_gmt, comment_content, comment_karma, comment_approved, comment_agent, comment_type, comment_parent, user_id, comment_date";
        String selectColumnsNames = "comment_ID, comment_post_ID, comment_author, comment_author_email, comment_author_url, comment_author_IP, CAST(comment_date AS timestamp(3)), CAST(comment_date_gmt AS timestamp(3)), comment_content, comment_karma, comment_approved, comment_agent, comment_type, comment_parent, user_id, CAST(comment_date AS date) AS comment_date_p"; // comment_date_p
                                                                                                                                                                                                                                                                                                                                                                                  // 추가
        String partitionColumnName = "comment_date_p"; // comment_date로 파티셔닝
        // ------------------------------- 수정할 부분 끝 -------------------------------

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateValue = sdf.format(now);

        try {
            // JDBC 드라이버 로드 및 연결
            Class.forName("io.trino.jdbc.TrinoDriver");
            conn = DriverManager.getConnection(url);

            // SQL 문장 준비
            String createTableSQL = "CREATE TABLE IF NOT EXISTS " +
                    insertTableName +
                    " (" +
                    createColumnsNames +
                    ") WITH ( format = 'parquet', partitioned_by = ARRAY['" +
                    partitionColumnName +
                    "'], external_location = '" +
                    externalLocation +
                    "' ) AS SELECT " +
                    selectColumnsNames +
                    " FROM " +
                    getTableName +
                    " WITH NO DATA";
            System.out.println(createTableSQL);
            pstmt = conn.prepareStatement(createTableSQL);
            pstmt.executeUpdate();

            // SQL 문장 준비
            String sql = "INSERT INTO " +
                    insertTableName +
                    " SELECT " +
                    columnsNames +
                    " FROM " +
                    getTableName +
                    " src " +
                    " WHERE NOT EXISTS (" +
                    "    SELECT 1 FROM " +
                    insertTableName + " dst " +
                    "    WHERE src.comment_ID = dst.comment_ID" + // comment_ID 기준으로 중복 검사
                    ")" +
                    " AND src.comment_date >= CURRENT_TIMESTAMP - INTERVAL '5' MINUTE" + // comment_date가 5분 이내
                    " OFFSET ? LIMIT ?";
            pstmt = conn.prepareStatement(sql);

            int offset = 0;
            while (true) {
                pstmt.setInt(1, offset);
                pstmt.setInt(2, batchSize);

                int updatedRows = pstmt.executeUpdate();
                if (updatedRows == 0) {
                    break; // 더 이상 가져올 데이터가 없으면 반복 종료
                }

                offset += batchSize;
                System.out.println(offset + "건까지 처리 완료");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // 연결 닫기
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
