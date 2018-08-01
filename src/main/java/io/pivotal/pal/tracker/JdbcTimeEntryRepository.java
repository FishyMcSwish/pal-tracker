package io.pivotal.pal.tracker;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private final MysqlDataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(MysqlDataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO time_entries (project_id, user_id, date, hours) " +
                            "VALUES (?, ?, ?, ?)",
                    RETURN_GENERATED_KEYS
            );

            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());

            return statement;
        }, generatedKeyHolder);

        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long l) {
        try {
            Map<String, Object> foundEntry = jdbcTemplate.queryForMap("select * from time_entries where id = ?", l);
            return getTimeEntry(foundEntry);
        }
        catch (EmptyResultDataAccessException ex)
        {
            return null;
        }

    }

    private TimeEntry getTimeEntry(Map<String, Object> foundEntry) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setId((long) foundEntry.get("id"));
        timeEntry.setProjectId((long) foundEntry.get("project_id"));
        timeEntry.setUserId((long) foundEntry.get("user_id"));
        timeEntry.setHours((int) foundEntry.get("hours"));
        timeEntry.setDate(((Date) foundEntry.get("date")).toLocalDate());
        return timeEntry;
    }

    @Override
    public List<TimeEntry> list() {

        List<Map<String, Object>> listTimeEntries = jdbcTemplate.queryForList("select * from time_entries");
        List<TimeEntry> listEntries = new ArrayList<>();
        for (Map<String,Object> eachMap:listTimeEntries ) {
            TimeEntry eachTimeEntry = getTimeEntry(eachMap);
            listEntries.add(eachTimeEntry);
        }
        return listEntries;
    }

    @Override
    public TimeEntry update(long updateId, TimeEntry timeEntry) {

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE time_entries set project_id = ?, user_id = ?, date = ?, hours = ? " +
                            "where id = ?"
            );

            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());
            statement.setLong(5, updateId);
            return statement;
        });
        return find(updateId);
    }

    @Override
    public void delete(long deleteId) {
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE from time_entries where id = ?"
            );
            statement.setLong(1, deleteId);
            return statement;
        });
    }
}
