import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LessonDao {

    private final DataSource dataSource;

    public LessonDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Lesson insert(Lesson lesson) {
        Objects.requireNonNull(lesson);

        if (lesson.getId() != null) {
            throw new JdbcOperationException("ID must be provided during the insert operation!");
        }
        var sql = """
                INSERT INTO lesson (name, homework_id) VALUES (?, ?)
                """;
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, lesson.getName());
            ps.setString(2, lesson.getHomework());
            int rowsInserted = ps.executeUpdate();
            if (rowsInserted < 1) {
                throw new JdbcOperationException("No rows were inserted, input lesson %s");
            }
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id =  generatedKeys.getLong(1);
                lesson.setId(id);
            }
            return lesson;
        } catch (SQLException e) {
            throw new JdbcOperationException("Failed to insert a lesson", e);
        }
    }

    public boolean delete(final Long id) {
        Objects.requireNonNull(id);
        var sql = """
                DELETE FROM lesson WHERE id = ?
                """;
        try (Connection connection = this.dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            var idx = 1;
            ps.setLong(idx, id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted < 1) {
                throw new JdbcOperationException("No rows were deleted!");
            }
            return true;
        } catch (SQLException e) {
            throw new JdbcOperationException("Failed to delete a product by id", e);
        }
    }

    public List<Lesson> getAllLessons() {
        List<Lesson> lessons = new ArrayList<>();
        var sql = """
                SELECT id, name, updatedAt, homework_id FROM lesson
                """;

        try (Connection connection = this.dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Long lessonId = resultSet.getLong("id");
                String lessonName = resultSet.getString("name");
                Long homeworkId = resultSet.getLong("homework_id");

                Lesson lesson = new Lesson(lessonId, lessonName, homeworkId);
                lessons.add(lesson);
            }
        } catch (SQLException e) {
            throw new JdbcOperationException("Failed to receive all lessons", e);
        }
        return lessons;
    }

    public Lesson findLessonById(final Long id) {
        Objects.requireNonNull(id);
        var sql = """
                SELECT id, name, updatedAt, homework_id FROM lesson WHERE id = ?
                """;
        try (Connection connection = this.dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;
            ps.setLong(idx, id);
            ResultSet rs = ps.executeQuery();
            Lesson lesson = new Lesson();

            if (rs.next()) {
                lesson.setId(rs.getLong("id"));
                lesson.setName(rs.getString("name"));
                lesson.setHomework(rs.getString("homework_id"));
                return lesson;
            } else {
                throw new JdbcOperationException("Lesson with id = %d not found!".formatted(id));
            }
        } catch (SQLException e) {
            throw new JdbcOperationException("Failed to receive lesson by id", e);
        }
    }
}