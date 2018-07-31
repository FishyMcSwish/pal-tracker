package io.pivotal.pal.tracker;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryTimeEntryRepository implements TimeEntryRepository{

    List<TimeEntry> entries = new ArrayList<>();

    public TimeEntry create(TimeEntry timeEntryToCreate) {

        long id = entries.size() + 1;
        timeEntryToCreate.setId(id);
        entries.add(timeEntryToCreate);
        return timeEntryToCreate;
    }

    public TimeEntry find(long id) {
        int index = (int) id -1;
        if(index < entries.size())
            return entries.get(index);
        else
            return null;
    }

    public List<TimeEntry> list() {
        return entries;
    }

    public TimeEntry update(long id, TimeEntry newTimeEntry) {
        TimeEntry foundTimeEntry = find(id);
        foundTimeEntry.setDate(newTimeEntry.getDate());
        foundTimeEntry.setHours(newTimeEntry.getHours());
        foundTimeEntry.setProjectId(newTimeEntry.getProjectId());
        foundTimeEntry.setUserId(newTimeEntry.getUserId());

        return foundTimeEntry;
    }

    public void delete(long id) {
        int index = (int)(id - 1);
        if(index < entries.size()) {
            entries.remove(index);
        }
    }
}
